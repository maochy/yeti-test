package yeti.strategies;

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

import java.awt.Component;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import yeti.ImpossibleToMakeConstructorException;
import yeti.Yeti;
import yeti.YetiCard;
import yeti.YetiIdentifier;
import yeti.YetiLog;
import yeti.YetiLogProcessor;
import yeti.YetiRoutine;
import yeti.YetiType;
import yeti.YetiVariable;
import yeti.environments.YetiTestManager;
import yeti.environments.java.YetiJavaConstructor;
import yeti.environments.java.YetiJavaMethod;
import yeti.environments.java.YetiJavaRoutine;
import yeti.monitoring.YetiGUI;
import yeti.monitoring.YetiUpdatableSlider;

public class YetiADFDPlusStrategy extends YetiRandomStrategy {

	public static int  rangeToPlot = 5;
	public static String argumentFirst = "" + 0;
	public static String argumentSecond = "" + 0;
	// ADFDPlus class drawGraph() will decide on the value of this variable to plot one dimensional chart or two dimensional chart.
	public static int plotOneDimOrTwoDim = 1;

	// public static double INTERESTING_VALUE_INJECTION_PROBABILITY = 0.50;
	long currentErrors = YetiLogProcessor.numberOfNewErrors;

	public YetiADFDPlusStrategy(YetiTestManager ytm) {
		super(ytm);


	}

	@Override
	public YetiCard getNextCard(YetiRoutine routine, int argumentNumber,
			int recursiveRank) throws ImpossibleToMakeConstructorException {
		YetiType cardType = routine.getOpenSlots()[argumentNumber];
		if (cardType.hasInterestingValues())
			// if (Math.random() < INTERESTING_VALUE_INJECTION_PROBABILITY) {

			if (currentErrors == 0) {
				return cardType.getRandomInterestingVariable();
			} else {

				Object value = cardType.getDSSRInterestingValue();
				YetiLog.printDebugLog("Interesting value: " + value, this);
				YetiIdentifier id = YetiIdentifier.getFreshIdentifier();
				return new YetiVariable(id, cardType, value);

			}

		return super.getNextCard(routine, argumentNumber, recursiveRank);
	}

	/**
	 * This overrides the strategy and chooses to pick from a set of interesting
	 * values if available.
	 * 
	 * @see yeti.strategies.YetiRandomStrategy#getNextCard(yeti.YetiRoutine,
	 *      int, int)
	 */
	@Override
	public YetiCard getNextCard(YetiRoutine routine, int argumentNumber)
			throws ImpossibleToMakeConstructorException {
		YetiType cardType = routine.getOpenSlots()[argumentNumber];
		if (cardType.hasInterestingValues())
			// if (Math.random() < INTERESTING_VALUE_INJECTION_PROBABILITY) {
			if (currentErrors == 0) {
				Object value = cardType.getRandomInterestingValue();
				YetiLog.printDebugLog("Interesting value: " + value, this);
				YetiIdentifier id = YetiIdentifier.getFreshIdentifier();
				return new YetiVariable(id, cardType, value);

			}

		return super.getNextCard(routine, argumentNumber);
	}

	@SuppressWarnings("serial")
	@Override
	public JPanel getPreferencePane() {
		// we generate a panel to contain both the label and the slider
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

		//because we dont need interesting values here.
		//JLabel txt = new JLabel("% interesting values: ");
		//p.add(txt);
		//txt.setAlignmentX(0);

		// we create the slider, this slider is updated both ways
		YetiUpdatableSlider useInterestingValuesSlider = new YetiUpdatableSlider(
				JSlider.HORIZONTAL,
				0,
				100,
				(int) YetiDSSRStrategy.INTERESTING_VALUE_INJECTION_PROBABILITY * 100) {

			/*
			 * (non-Javadoc) Updates the value by taking its value from the
			 * variable
			 * 
			 * @see yeti.monitoring.YetiUpdatableSlider#updateValues()
			 */
			public void updateValues() {
				super.updateValues();
				this.setValue((int) (YetiDSSRStrategy.INTERESTING_VALUE_INJECTION_PROBABILITY * 100));

			}
		};

		// we set up the listener that updates the value
		useInterestingValuesSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int nullValuesP = (int) source.getValue();
					YetiDSSRStrategy.INTERESTING_VALUE_INJECTION_PROBABILITY = ((double) nullValuesP) / 100;
				}
			}
		});

		// Turn on labels at major tick marks.
		useInterestingValuesSlider.setMajorTickSpacing(25);
		useInterestingValuesSlider.setMinorTickSpacing(5);
		useInterestingValuesSlider.setPaintTicks(true);
		useInterestingValuesSlider.setPaintLabels(true);

		YetiGUI.allComponents.add(useInterestingValuesSlider);
		useInterestingValuesSlider.setMaximumSize(new Dimension(130, 50));
		useInterestingValuesSlider.setAlignmentX(0);

		// Not required in ADFD i think.
		//p.add(useInterestingValuesSlider);

		TitledBorder title = BorderFactory.createTitledBorder(Yeti.strategy
				.getName() + " Panel");
		p.setBorder(title);
		p.setMinimumSize(new Dimension(300, 250));
		p.setMaximumSize(new Dimension(300, 250));
		p.setAlignmentX(Component.CENTER_ALIGNMENT);

		return p;

	}





	//long oldFaults1 = 0;



	//It is used to get the values of the arguments.
	YetiCard[] oldyt = null;

	//It is used to get the name of the routine generating a failure.
	private YetiRoutine oldroutine;

	// It is used to make the names of generated files unique.
	public static long uid = 0;

	//It is used to hold the arguments.
	public String args = "";




	/*
	 * (non-Javadoc)
	 * @see yeti.strategies.YetiRandomStrategy#getAllCards(yeti.YetiRoutine)
	 */
	public YetiCard[] getAllCards(YetiRoutine routine) throws ImpossibleToMakeConstructorException {



		// It assigns the number of unique errors value to current errors. 
		long currentErrors = YetiLogProcessor.numberOfNewErrors;
		YetiLog.printDebugLog("nErrors " + currentErrors, this);



		// The second condition in the if statement is added to restrict the program to only 1 and 2 arguments programs.
		if (currentErrors == 1 && oldyt.length <= 2) {
			YetiLog.printDebugLog("found bug in the strategy", this);
			int j1 = 0;
			YetiCard yc = oldyt[j1];
			String call = "";


			// If statement to check the type of the first argument of method failing the test.
			if (yc.getType().getName().equals("int")||yc.getType().getName().equals("byte")||yc.getType().getName().equals("long")||yc.getType().getName().equals("short")) {


				YetiJavaRoutine jroutine = (YetiJavaRoutine) oldroutine;


				// I am trying the simplified version here.
				// So it will treat one dimensional program as two dimensional by making its second argument 0 in the case of one dimensional program.
				// Following is the case when constructor is faulty and may contain one or two arguments.

				if (jroutine instanceof YetiJavaConstructor) {

					YetiJavaConstructor c = (YetiJavaConstructor) jroutine;

					// Call is used in when generating the C0 program.
					call = "new "   + c.getOriginatingModule().getModuleName() + "( i ";

					// ArgumentFirst is the first argument used in C0 program.
					argumentFirst = oldyt[j1].getValue().toString();

					// If condition is used in the case is faulty constructor is one argument. So the second argument is made 0.
					if (oldyt.length == 1 ){
						//	argumentSecond	= "" + 0;
						call = call + ")";
					}	

					if (oldyt.length == 2){
						argumentSecond	= oldyt[++j1].getValue().toString();
						call = call + ", j )";
						plotOneDimOrTwoDim += 1;
					}

				} 

				else {


					if (jroutine instanceof YetiJavaMethod) {
						YetiJavaMethod m = (YetiJavaMethod) jroutine;
						if (m.isStatic) {

							call = "" + m.getOriginatingModule().getModuleName() + "." + m.getMethod().getName() + "( i "; 

							// ArgumentFirst is the first argument used in C0 program.
							argumentFirst = oldyt[j1].getValue().toString();


							// If condition is used in the case is faulty constructor is one argument. So the second argument is made 0.
							if (oldyt.length == 1 ){
								//		argumentSecond	= "" + 0;
								call = call + ")";

							}

							if (oldyt.length == 2){
								//JOptionPane.showMessageDialog(null, YetiADFDPlusStrategy.rangeToPlot, " The value for range to plot is ", JOptionPane.PLAIN_MESSAGE);

								argumentSecond	= oldyt[++j1].getValue().toString();
								call = call + ", j )";
								plotOneDimOrTwoDim += 1;
							}
						}


						else {
							call = "variable" + "." + m.getMethod().getName() + "( i ";

							// ArgumentFirst is the first argument used in C0 program.
							argumentFirst = oldyt[j1].getValue().toString();

							// If condition is used in the case is faulty constructor is one argument. So the second argument is made 0.
							if (oldyt.length == 1 ){
								//	argumentSecond	= "" + 0;
								call = call + ")";

							}	

							if (oldyt.length == 2){
								//JOptionPane.showMessageDialog(null, YetiADFDPlusStrategy.rangeToPlot, " The value for range to plot is ", JOptionPane.PLAIN_MESSAGE);

								argumentSecond	= oldyt[++j1].getValue().toString();
								call = call + ", j )";
								plotOneDimOrTwoDim += 1;
							}
						}
					}

					String programBegin = programBeginPart();
					String programEnd = programEndPart();
					String programEnd2 = programEndPart2();
					String completeProgram = programBegin + programEnd  + "   " + call  + programEnd2;
					generateProgram(completeProgram);
				}
			}
		}


		oldyt = super.getAllCards(routine);
		oldroutine = routine;
		return oldyt;
	}

	//%%%%%%%%%%%%%%%%%%%%%%%%%%%   BEGIN PART %%%%%%%%%%%%%%%%%%%%//

	public String programBeginPart(){
		String temp = "/** YETI - York Extensible Testing Infrastructure \n"  
				+ "Copyright (c) 2009-2010, Manuel Oriol <manuel.oriol@gmail.com> - University of York \n"
				+ "All rights reserved.\n"
				+ "Redistribution and use in source and binary forms, with or without\n"
				+ "modification, are permitted provided that the following conditions are met:\n"
				+ "1. Redistributions of source code must retain the above copyright\n"
				+ "notice, this list of conditions and the following disclaimer.\n"
				+ "2. Redistributions in binary form must reproduce the above copyright\n"
				+ "notice, this list of conditions and the following disclaimer in the\n"
				+ "documentation and/or other materials provided with the distribution.\n"
				+ "3. All advertising materials mentioning features or use of this software\n"
				+ "must display the following acknowledgement:\n"
				+ "This product includes software developed by the University of York.\n"
				+ "4. Neither the name of the University of York nor the\n"
				+ "names of its contributors may be used to endorse or promote products\n"
				+ "derived from this software without specific prior written permission.\n"

		    	+ "THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER ''AS IS'' AND ANY\n"
		    	+ "EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED\n"
		    	+ "WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE\n"
		    	+ "DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER BE LIABLE FOR ANY\n"
		    	+ "DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES\n"
		    	+ "(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;\n"
		    	+ "LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND\n"
		    	+ "ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\n"
		    	+ "(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS\n"
		    	+ "SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n"
		    	+ "**/\n"
		    	+ " // The value of currentErrors is " + currentErrors++ + "\n"
		    	+ "import java.io.*;\n"
		    	+ "import java.util.*;\n\n" 
		    	+ "public class C"
		    	+  uid++
		    	+ " \n{\n"
		    	+ " public static ArrayList<Integer> pass = new ArrayList<Integer>();\n"
		    	+ " public static ArrayList<Integer> fail = new ArrayList<Integer>();\n\n"

				+ " public static int range = "+ rangeToPlot + ";\n\n"

				+ " public static int xValue = " + argumentFirst  +";\n" ;

		if (oldyt.length == 1 ){

			temp = temp	+ " public static int starterX = xValue - range;\n" 
					+ " public static int stopperX = xValue + range;\n"

				+ " public static void main(String []argv){\n"
				+ "   checkFirstAndLastValue(starterX);\n"
				+ "   for (int i=starterX + 1; i < stopperX; i++) {\n"
				+ "       checkFirstAndLastValue(i);\n"
				+ "     }\n"
				+ "   checkFirstAndLastValue(stopperX);\n" 
				+ "   printRangeFail();\n" 
				+ "   printRangePass();\n" 
				+ " }\n";
		}

		if (oldyt.length == 2 ){

			temp = temp	+ " public static int yValue = " + argumentSecond +";\n\n"

				+ " public static int starterX = xValue - range;\n" 
				+ " public static int stopperX = xValue + range;\n"
				+ " public static int starterY = yValue - range;\n"
				+ " public static int stopperY = yValue + range;\n\n"

				+ " public static void main(String []argv){\n"
				+ "   checkFirstAndLastValue(starterX, starterY);\n"
				+ "   for (int i=starterX + 1; i < stopperX; i++) {\n"
				+ "     for (int j = starterY + 1; j < stopperY; j++) {\n"
				+ "       checkFirstAndLastValue(i,j);\n"
				+ "   }\n"
				+ "   checkFirstAndLastValue(stopperX, stopperY);\n" 
				+ "   printRangeFail();\n" 
				+ "   printRangePass();\n"
				+ "   }\n"
				+ " }\n";
		}

		return temp;
	}

	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%   END PART %%%%%%%%%%%%%%%%%%%%//

	public String programEndPart(){
		String temp3 = "\n // It prints the range of fail values \n"
				+ "public static void printRangeFail() { \n"
				+"   try { \n"
				//+"     FileWriter fw = new FileWriter(\"/Users/mian/inclaspath/Fail.txt\" , true); \n"
				+"     File fw = new File(\"Fail.txt\"); \n"
				+"     if (fw.exists() == false) { \n"
				+" 	     fw.createNewFile(); \n"	
				+"     }\n"
				+"	   PrintWriter pw = new PrintWriter(new FileWriter (fw, true));\n"
				//+"     int count = 1;\n"
				+"     for (Integer i1 : fail) { \n"
				+"        pw.append(i1+\"\\n\"); \n"
				//+"        if (count%2 == 0)\n"
				//+"        	pw1.append(\"\\n\");\n"
				//+"        count++; \n"
				+"     } \n"
				+"     pw.close(); \n"
				+"   } catch(Exception e) { \n"
				+"     System.err.println(\" Error : e.getMessage() \"); \n"
				+"   } \n"
				+" } \n"
				//+"     FileWriter fw = new FileWriter(\"/Users/mian/inclaspath/Pass.txt\" , true); \n"
				+ "// It prints the range of pass values \n"
				+" public static void printRangePass() { \n"
				+"   try { \n"
				+"     File fw1 = new File(\"Pass.txt\"); \n"
				+"     if (fw1.exists() == false) { \n"
				+" 	     fw1.createNewFile(); \n"	
				+"     }\n"
				+"	   PrintWriter pw1 = new PrintWriter(new FileWriter (fw1, true));\n"
				//+"     int count = 1;\n"
				+"     for (Integer i2 : pass) { \n"
				+"        pw1.append(i2+\"\\n\");\n"
				//+"        if (count%2 == 0)\n"
				//+"        	pw1.append(\" \");\n"
				//+"        count++; \n"
				+"     } \n"
				+"     pw1.close(); \n"
				+"   } \n"
				+"   catch(Exception e) { \n"
				+"   System.err.println(\" Error : e.getMessage() \"); \n"
				+"   } \n"
				+" } \n";

		if (oldyt.length == 1 ){
			temp3 = temp3	+" public static void checkFirstAndLastValue(int i) { \n";
		}
		if (oldyt.length == 2 ){
			temp3 = temp3 +" public static void checkFirstAndLastValue(int i, int j) { \n";
		}
		temp3 = temp3 +"   try { \n";
		return temp3;
	}

	public String programEndPart2(){
		String temp4 = "";
		if (oldyt.length == 1 ){
		temp4 = temp4 + "; \n"
					+ "     pass.add(i); \n"
					+ "   } catch (Throwable t) { \n"
					+ "    fail.add(i); \n "
					+ "    failureDomain(i);\n"
					+ "   } \n } "
					+ " public static void failureDomain(int i){}\n"
					+ "\n}"
					;
		}
		
		if (oldyt.length == 2 ){
		temp4 = temp4 + "; \n"
				+ "     pass.add(i); \n"
				+ "     pass.add(j); \n"
				+ "   } catch (Throwable t) { \n"
				+ "    fail.add(i); \n "
				+ "    fail.add(j); \n"
				+ "    failureDomain(i,j);\n"
				+ "   } \n } "
				+ " public static void failureDomain(int i, int j){}\n"
				+ "\n}"
				;
		}
		
		return temp4;
	}

	public void generateProgram(String program){
		try {
			PrintStream fos = new PrintStream("C" + (uid - 1)
					+ ".java");
			fos.println(program);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getName() {
		return "ADFD++ strategy";
	}

}