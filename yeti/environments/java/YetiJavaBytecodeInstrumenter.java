package yeti.environments.java;

/**

YETI - York Extensible Testing Infrastructure

Copyright (c) 2009-2010, Manuel Oriol <manuel.oriol@gmail.com> - University of York
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
1. Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
3. All advertising materials mentioning features or use of this software
must display the following acknowledgement:
This product includes software developed by the University of York.
4. Neither the name of the University of York nor the
names of its contributors may be used to endorse or promote products
derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

**/ 

import java.io.IOException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Mnemonic;

/**
 * Class that allows to instument the bytecode to allow branch coverage.
 * Note that this can be used as a standalone instrumenter on a class file.
 * It uses the Javassist package.
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Jun 30, 2010
 *
 */
public class YetiJavaBytecodeInstrumenter {
	
	/**
	 * The number of branches in the class.
	 */
	public  int n = 0;

	/**
	 * A utility method that adds the branching call if there is none at the specified index already.
	 * 
	 * @param cc the class representation in Javassist.
	 * @param ci the iterator on the code.
	 * @param index the index at which the call should be added.
	 */
	public void insertBranchVisit(CtClass cc, CodeIterator ci, int index) {
		//System.out.println(index);
		// we first build the code to add
		try {
			if (ci.byteAt(index)<9) {
				if ((Mnemonic.OPCODE[ci.byteAt(index+1)].equals("invokestatic"))&&(cc.getClassFile().getConstPool().getMethodrefName(ci.u16bitAt(index+2)).equals("__yeti_branch_visit")))
					return;
			} else {
				if ((Mnemonic.OPCODE[ci.byteAt(index+2)].equals("invokestatic"))&&(cc.getClassFile().getConstPool().getMethodrefName(ci.u16bitAt(index+3)).equals("__yeti_branch_visit")))
					return;			
				
			}
		} catch (ArrayIndexOutOfBoundsException e1) {
			// nothing to do, we have just hit the end of the method and there is no banch recorded
		}
				
		Bytecode bc = new Bytecode(cc.getClassFile().getConstPool());
		bc.addIconst(n++);
		CtClass []args= { CtClass.intType };
		bc.addInvokestatic(cc, "__yeti_branch_visit", Descriptor.ofMethod(CtClass.voidType, args));
		// we finally insert the bytecode
		try {
			ci.insertAt(index, bc.get());
		} catch (BadBytecode e) {
			// should not happen
			e.printStackTrace();
		}
	}

	/**
	 * The method that makes the actual instrumentation using the Javassist package.
	 * 
	 * @param cc The class to instrument.
	 * @return the instrumented class.
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 * @throws BadBytecode
	 * @throws IOException
	 */
	public CtClass instrument(CtClass cc) throws NotFoundException, CannotCompileException, BadBytecode, IOException {
		CtMethod []allMeth = cc.getDeclaredMethods();
		// we attach the visiting static method
		CtMethod m = CtNewMethod.make(
				"public static void __yeti_branch_visit(int i) { " +		           
				"System.out.println(\"visited branch: \"+$1); " +
				"}",
				cc);
		cc.addMethod(m);

		// we add the first branch of the methods
		
		for (CtMethod cm: allMeth) {
			//		CtMethod cm = cc.getDeclaredMethod("test");
			if (cm.isEmpty()) continue;
			cm.insertBefore("{__yeti_branch_visit("+ n++ +");}");
			cm.setModifiers(Modifier.setPublic(cm.getModifiers()));

			// we will add all branches in the bytecode
			MethodInfo mi = cm.getMethodInfo();
			CodeAttribute ca = mi.getCodeAttribute();

			// we iterate through the code of the method
			CodeIterator ci = ca.iterator();

			while (ci.hasNext()) {
				int index = ci.next();
				int op = ci.byteAt(index);
				String mo = Mnemonic.OPCODE[op];
				// if the opcode is an if or a goto
				// we add the call to __yeti_branch_visit just after that one
				//System.out.println(cm.getName());

				if (mo.startsWith("if")||mo.startsWith("goto")) {
					int i = index;
					if (mo.startsWith("goto_w"))
						i += ci.s32bitAt(index+1);											
					else
						i += ci.s16bitAt(index+1);	
					insertBranchVisit(cc,ci,i);
					if (mo.startsWith("if")) {
						insertBranchVisit(cc,ci,ci.next());						
					}
					
				}
				// we recompute the max stack (needed)
				ca.computeMaxStack();
			}
		}

		CtConstructor []allcons = cc.getDeclaredConstructors();

		// we add the first branch of the methods
		for (CtConstructor cm: allcons) {
			//		CtMethod cm = cc.getDeclaredMethod("test");
			if (cm.isEmpty()) continue;
			cm.insertBeforeBody("{__yeti_branch_visit("+ n++ +");}");
			cm.setModifiers(Modifier.setPublic(cm.getModifiers()));

			// we will add all branches in the bytecode
			MethodInfo mi = cm.getMethodInfo();
			CodeAttribute ca = mi.getCodeAttribute();

			// we iterate through the code of the method
			CodeIterator ci = ca.iterator();

			while (ci.hasNext()) {
				int index = ci.next();
				int op = ci.byteAt(index);
				String mo = Mnemonic.OPCODE[op];
				// if the opcode is an if or a goto
				// we add the call to __yeti_branch_visit just after that one
				//System.out.println(cm.getName());

				if (mo.startsWith("if")||mo.startsWith("goto")) {
					int i = index;
					if (mo.startsWith("goto_w"))
						i += ci.s32bitAt(index+1);											
					else
						i += ci.s16bitAt(index+1);	
					insertBranchVisit(cc,ci,i);
					if (mo.startsWith("if")) {
						insertBranchVisit(cc,ci,ci.next());						
					}
					
				}
				// we recompute the max stack (needed)
				ca.computeMaxStack();
			}
		}


		// we add all the fields necessary for the branch coverage
		// an array of booleans for the branches
		CtField cf = CtField.make("static boolean []__yeti_branches;",cc);
		cc.addField(cf, "new boolean["+n+"];");
		// the total number of branches
		CtField cf1 = CtField.make("static int __yeti_n_branches;",cc);
		cc.addField(cf1, n+";");

		// the number of covered branches
		CtField cf2 = CtField.make("static int __yeti_covered_branches;",cc);
		cc.addField(cf2, "0;");

		// the coverage itself
		CtField cf3 = CtField.make("static double __yeti_coverage;",cc);
		cc.addField(cf3, "0.0;");

		// we rewrite the body of __yeti_branch_visit to add the updating instructions 
		m.setBody("{if (!__yeti_branches[$1]){" +
				"__yeti_branches[$1]=true;" +
				"__yeti_covered_branches++;" +
				"__yeti_coverage = 100*((double)__yeti_covered_branches)/((double)__yeti_n_branches);" +
				"} " +
				//"System.out.println($class.getName()+\"visited branch: \"+$1+\" coverage: \"+100*__yeti_coverage+\"%\");" +
				"}" +
		"");
		
		CtMethod m1 = CtNewMethod.make(
				"public static double __yeti_get_coverage() { " +		           
				"return __yeti_coverage;"+
				"}",
				cc);
		cc.addMethod(m1);
		
		CtMethod m2 = CtNewMethod.make(
				"public static int __yeti_get_covered_branches() { " +		           
				"return __yeti_covered_branches;"+
				"}",
				cc);
		cc.addMethod(m2);
		
		CtMethod m3 = CtNewMethod.make(
				"public static int __yeti_get_n_branches() { " +		           
				"return __yeti_n_branches;"+
				"}",
				cc);
		cc.addMethod(m3);
	
		
		// we print the number of branches we found
		System.out.println(cc.getName()+", number of branches: "+n);		
		return cc;
		
	}
	
	/**
	 * Loads the class using the default loading mechanisms.
	 * 
	 * @param className the name of the class.
	 * @return the byte array containing the instrumented code.
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 * @throws BadBytecode
	 * @throws IOException
	 */
	public byte[] loadAndInstrument(String className) throws NotFoundException, CannotCompileException, BadBytecode, IOException {
		// we retrieve the class file into a Javassist utility class
		ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.get(className);

		cc = instrument(cc);
		
		return cc.toBytecode();
		
	}
	
	/**
	 * The main. Simply pass the name of the class to instrument as an argument.
	 * The program is not resilient at all. Only use twice on the program otherwise there will be exceptions...
	 * 
	 * @param args
	 * @throws NotFoundException 
	 * @throws CannotCompileException 
	 * @throws IOException 
	 * @throws BadBytecode 
	 */
	public static void main(String[] args) throws NotFoundException, IOException, CannotCompileException, BadBytecode {
		String className = args[0];
		// we retrieve the class file into a Javassist utility class
		ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.get(className);

		cc = new YetiJavaBytecodeInstrumenter().instrument(cc);
		
		cc.writeFile();
	}
}
