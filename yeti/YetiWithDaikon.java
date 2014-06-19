package yeti;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import yeti.strategies.YetiADFDPlusStrategy;

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
must display the following acknowledgment:
This product includes software developed by the University of York.
4. Neither the name of the University of York nor the
names of its contributors may be used to endorse or promote products
derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 **/ 

/**
 * Class that represents the GUI of ADFD+. It takes input from the user, pass the input to YETI, collect the results from YETI
 * Execute Daikon on the results, and then compile the results to generate invariants and draw the failure domain.
 * 
 * @author Mian Asbat Ahmad (mian.ahmad@york.ac.uk)
 * @date 14 Apr 2014
 *
 */
public class YetiWithDaikon{

	private static final boolean DEBUG = false;

	/**
	 * testFilePathInitial contains the path to the directory from which the .jar is executed. 
	 */
	public static String testFilePathInitial = ".";

	/**
	 * testFilePathFinal contains the -yetiPath=. followed by the path to the directory from which the .jar is executed. It is passed to yeti as argument to set the path. 
	 */
	String 		testFilePathFinal 	 = "-yetiPath=.";

	/**
	 *  totalFiles counts the number of files generated during the test session. 
	 */
	static	int totalFiles 			 = 0;

	/**
	 *  countCompileFiles counts the number of files compiled after generation during the test session. 
	 */
	int 		countCompileFiles;

	/**
	 *   waitForYetiToFinish gets the value of the total time for which yeti will execute. It keep the other threads on hold and allow them to execute only after the execution of YETI is finished.
	 */
	int 		waitForYetiToFinish;


	/**
	 *   filesToCompileArray hold the number and name of generated files during execution.
	 */
	String[] filesToCompileArray;

	/**
	 *   list contains all the options/arguments set by the user and pass it to the String object/variable "command" which is later passed to Yeti.
	 */
	ArrayList<String> 	list 			 = new ArrayList<String>();

	/**
	 *   filesToCompile list contains the list of files that are compiled during execution.
	 */
	ArrayList<String> 	filesToCompile	 = new ArrayList<String>();

	/**
	 * Constructor of the class to create frame and draw components on the frame. 
	 */
	public YetiWithDaikon(){
		countFiles();
		compileFiles();
		executeFiles();
//		deleteOldTestFiles();
	}


	/**
	 * This method delete the test files of the last executed experiment/Test which includes pass.txt, fail.txt, Cx.class and Cx.java.
	 * It is added to simplify the process. If the files from the last test remain in the folder
	 * then the results of new tests are misleading.
	 */
	public static void deleteOldTestFiles(){

		File directory = new File(".");
		File[] files = directory.listFiles();
		for (File f : files)
		{
			if ((f.getName().startsWith("C") || f.getName().startsWith("Pass") || f.getName().startsWith("Fail") ))
			{
				f.delete();
			}

		}
	}










	/**
	 * This method count the number of C*.java files created dynamically by ADFD+.
	 *  
	 */
	public void countFiles() {
		try{

			String files;
			File folder = new File(testFilePathInitial);
			File[] listOfFiles = folder.listFiles();

			for (int i=0; i < listOfFiles.length; i++)
			{
				if(listOfFiles[i].isFile()){
					files = listOfFiles[i].getName();
					if((files.startsWith("C"))&&(files.endsWith(".java"))){
//						System.out.println(files + "is added");
						filesToCompile.add(files);
//						Thread.sleep(200);

					}

				}
			}


			filesToCompileArray = filesToCompile.toArray(new String[filesToCompile.size()]);

			//			generated_TextField.setText(filesToCompileArray.length + " files") ;

		}
		catch(Exception e1){
			e1.printStackTrace();
		}
	}

	/**
	 * This method compiles files generated by the ADFD+ strategy
	 */
	public void compileFiles() {
		try{
			int count = 0;
			for (int i = 0; i < filesToCompileArray.length; i++){
				Process pro1;
//				Thread.sleep(200);
				pro1 = Runtime.getRuntime().exec("javac -g " + filesToCompileArray[i]);
				pro1.waitFor();
				count = count + 1;
			}
			countCompileFiles = count;
			//			compile_TextField.setText(count + " files");
		}

		catch(Exception e1){
			e1.printStackTrace();
		}

	}


	/**
	 * This method calls the progress bar method, executejavafiles method and executeDaikon method.
	 */
	public void executeFiles(){
		try{
			executeJavaFiles();
			//JOptionPane.showMessageDialog(null, testFilePathInitial, "Thread 3 joining", JOptionPane.CANCEL_OPTION);
			String result = executeDaikon();



		}


		catch(Exception e1){
			e1.printStackTrace();
		}

	}



	/**
	 * This method execute the dynamically generated and compiled C*.class files.
	 */
	public void executeJavaFiles(){
		try {
			int count = 0;
			for (int i = 0; i < filesToCompileArray.length; i++){
				Process p = Runtime.getRuntime().exec("java C"+ i);
				p.waitFor();
				count++;
			}

			totalFiles = count;

		}

		catch(Exception e1){
			e1.printStackTrace();
		}
	}


	/**
	 * Executes Daikon on each failure file.
	 */
	public String executeDaikon(){
		String result = ""; 
		String daikonOptions = "--config_option daikon.inv.unary.scalar.LowerBound.minimal_interesting=-1000 "+// TODO Adapt to boundaries set up for the failure
				"--config_option daikon.inv.unary.scalar.LowerBound.maximal_interesting=2000 "+ // TODO
				"--config_option daikon.inv.unary.scalar.UpperBound.maximal_interesting=2000 "+// TODO
				"--config_option daikon.inv.unary.scalar.UpperBound.minimal_interesting=-1000 "+// TODO
				"--config_option daikon.PptRelation.enable_object_user=true "+
				"--config_option daikon.PptSliceEquality.set_per_var=true "+
				"--conf_limit 0 --var-select-pattern=^i$|^j$ C";// TODO Make more generic

		try {
			int count = 0;
			// for each file we will execute a session with Daikon
			for (int i = 0; i < filesToCompileArray.length; i++){
				Thread.sleep(2000);
				Process p0 = Runtime.getRuntime().exec("java daikon.Chicory --ppt-select-pattern=failureDomain C"+ i);
				p0.waitFor();
				File trace = new File("C"+i+".dtrace.gz");
				while (!trace.exists()){
					Thread.sleep(100);
				}


				if (DEBUG) System.err.println("file C"+i+".dtrace.gz exists and has length "+trace.length());
				Process p = Runtime.getRuntime().exec("java daikon.Daikon "+daikonOptions+ i+".dtrace.gz");
				if (DEBUG) System.err.println("java daikon.Daikon "+daikonOptions+ i+".dtrace.gz");
				p.waitFor();  // wait for process to finish then continue.
				BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String execResult = "";
				String tmp;
				boolean traceOn = false;
				while ((tmp = bri.readLine())!=null){
					System.err.println(tmp);
					if (tmp.contains("failureDomain")&&tmp.contains("ENTER")){
						traceOn=true;
						continue;
					}
					if (traceOn){
						if (tmp.contains("===========================================================================")){
							traceOn=false;
							continue;
						}
						execResult=execResult+"\n"+tmp;
					}
				}
				result= execResult;
				count++;
			}

			totalFiles = count;

		}

		catch(Exception e1){
			e1.printStackTrace();
		}
		return result;
	}
	
	
	
	/**
	 * @param args
	 * // Main method of the class. It calls the constructor of the class to draw the ADFD+ GUI.
	 */
	public static void main(String[] args){
		deleteOldTestFiles();
		//JOptionPane.showMessageDialog(null, YetiADFDPlusStrategy.plotOneDimOrTwoDim, "Value of plotOneDimOrTwoDim", JOptionPane.CANCEL_OPTION);

		try{
			Yeti.YetiRun(args);

		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		YetiWithDaikon yd = new YetiWithDaikon();
		System.exit(0);
	}

}





