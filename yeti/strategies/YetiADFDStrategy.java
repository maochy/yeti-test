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

public class YetiADFDStrategy extends YetiRandomStrategy {

	public static String lowerLimit = "" + Integer.MIN_VALUE;
	public static String upperLimit = "" + Integer.MAX_VALUE;
	
	// public static double INTERESTING_VALUE_INJECTION_PROBABILITY = 0.50;
	long currentErrors = YetiLogProcessor.numberOfNewErrors;

	public YetiADFDStrategy(YetiTestManager ytm) {
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

	long oldFaults1 = 0;
	YetiCard[] oldyt = null;
	private YetiRoutine oldroutine;
	public static long uid = 0;
	String argumentTwo = "";
	public String args = "";
	

	public YetiCard[] getAllCards(YetiRoutine routine)
			throws ImpossibleToMakeConstructorException {

		long currentErrors = YetiLogProcessor.numberOfNewErrors;

		YetiLog.printDebugLog("nErrors " + currentErrors, this);

		if (currentErrors > oldFaults1) {

			YetiLog.printDebugLog("found bug in the strategy", this);
			oldFaults1 = currentErrors;

			for (int j1 = 0; j1 < oldyt.length; j1++) {
				YetiCard yc = oldyt[j1];

				if (yc.getType().getName().equals("int")) {


					String call = "";

					YetiJavaRoutine jroutine = (YetiJavaRoutine) oldroutine;
					if (jroutine instanceof YetiJavaConstructor) {
						YetiJavaConstructor c = (YetiJavaConstructor) jroutine;
						call = "new "
								+ c.getOriginatingModule().getModuleName()
								+ "(" + args + "i";
						for (int k = j1 + 1; k < oldyt.length; k++) {
							call = call + ","
									+ oldyt[k].getValue().toString();

						}
						call = call + ")";

					} else {


						if (jroutine instanceof YetiJavaMethod) {
							YetiJavaMethod m = (YetiJavaMethod) jroutine;
							if (m.isStatic) {
								call = ""
										+ m.getOriginatingModule()
										.getModuleName() + "."
										+ m.getMethod().getName() + "("

										+ args + "i";

								for (int k = j1 + 1; k < oldyt.length; k++) {

									call = call + "," + oldyt[k].getValue().toString();

								}
								call = call + ")";
							} else {
								call = "variable" + "."
										+ m.getMethod().getName() + "("
										+ args + "i";
								for (int k = j1 + 1; k < oldyt.length; k++) {

									call = call + "," + oldyt[k].getValue().toString();

								}
								call = call + ")";	
							}

						}
					}


					String programBegin = programBeginPart();
					String programMiddle = programMiddlePart();
					String programEnd = programEndPart();
					String programEnd2 = programEndPart2();

					String generatedProgram = programBegin + call + programMiddle  + programEnd  + "   " + call + ";" + programEnd2;

					generateProgram(generatedProgram);

					argumentTwo = ""+yc.getValue();
					args = args + yc.getValue() + ",";


				}

			}


		}

		oldyt = super.getAllCards(routine);
		oldroutine = routine;
		return oldyt;
	}

	//%%%%%%%%%%%%%%%%%%%%%%%%%%%   BEGIN PART %%%%%%%%%%%%%%%%%%%%//

	public String programBeginPart(){
		String temp = "import java.io.*;\n"
				+ "import java.util.*;\n\n" 
				+ "public class C"
				+  uid++
				+ " \n{\n" 
				+ " public static ArrayList<Integer> pass = new ArrayList<Integer>();\n"
				+ " public static ArrayList<Integer> fail = new ArrayList<Integer>();\n"
				+ " public static boolean startedByFailing = false;\n"
				+ " public static boolean isCurrentlyFailing = false;\n"

				//+ " public static int start = Integer.MIN_VALUE;\n"
				//+ " public static int stop = Integer.MAX_VALUE;\n\n"
				// reduce to 80 because the other one take a lot of time to process. In final version the value will be changed to max.
				+ " public static int start = " + lowerLimit + "\n;"
				+ " public static int stop = "+ upperLimit +";\n\n"

				+ " public static void main(String []argv){\n"

				+ "  checkStartAndStopValue(start);\n"

				+ "  for (int i=start+1;i<stop;i++){\n   try{\n";
		return temp;
	}

	//%%%%%%%%%%%%%%%%%%%%%%%%%   MIDDLE PART %%%%%%%%%%%%%%%%%%%%//


	public String programMiddlePart(){
		String temp1 = 
				";\n "
						+ "  if (isCurrentlyFailing) \n"
						+ "  {\n"
						+ "fail.add(i-1);\n";
		// this statement will add second argument to the arraylist to be used for x y graphs.
		// its sequence is x then 0 in case of empty and value of y if not empty
		if (argumentTwo.equals("")){
			temp1 = temp1 + "fail.add(0);\n"; 
		}
		else {
			temp1 = temp1 +"fail.add("+argumentTwo+");\n";
		}

		temp1	=	temp1 + "pass.add(i);\n";
		// this statement will add second argument to the arraylist to be used for x y graphs.
		// its sequence is x then 0 in case of empty and value of y if not empty
		if (argumentTwo.equals("")){
			temp1 = temp1 + "pass.add(0);\n";
		}
		else {
			temp1 = temp1 + "pass.add("+argumentTwo+");\n";
		}

		temp1	=	temp1 
				+ "  isCurrentlyFailing=false; \n"
				+ "  } \n } \n"
				+ "  catch(Throwable t) { \n"
				+ "  if (!isCurrentlyFailing) \n"
				+ "  {\n"
				+ "pass.add(i-1);\n";
		// this statement will add second argument to the arraylist to be used for x y graphs.
		// its sequence is x then 0 in case of empty and value of y if not empty
		if (argumentTwo.equals("")){
			temp1 = temp1 + "pass.add(0);\n";
		}
		else {
			temp1 = temp1 + "pass.add("+argumentTwo+");\n";
		}

		temp1	=	temp1 + "fail.add(i);\n";
		// this statement will add second argument to the arraylist to be used for x y graphs.
		// its sequence is x then 0 in case of empty and value of y if not empty
		if (argumentTwo.equals("")){
			temp1 = temp1 + "fail.add(0);\n";
		}
		else {
			temp1 = temp1 + "fail.add("+argumentTwo+");\n";
		}

		temp1	=	temp1 
				+ "isCurrentlyFailing = true;\n"
				+ " }  \n } \n } \n"
				+ " checkStartAndStopValue(stop); \n"
				+ "  printRangeFail(); \n"
				+ "  printRangePass();  \n"
				+ "  }\n";


		return temp1;
	}



	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%   END PART %%%%%%%%%%%%%%%%%%%%//

	public String programEndPart(){
		String temp3 = "\n public static void printRangeFail() { \n"
				+"   try { \n"
				//+"   FileWriter fw = new FileWriter(\"/Users/mian/inclaspath/Fail.txt\" , true); \n"
				+"   File fw = new File(\"Fail.txt\"); \n"
				+"   if (fw.exists() == false) { \n"
				+" 	 fw.createNewFile(); \n"	
				+"   }\n"
				+"	 PrintWriter pw = new PrintWriter(new FileWriter (fw, true));"
				+"   for (Integer i1 : fail) { \n"
				+"      pw.append(i1+\"\\n\"); \n"
				+"   } \n"
				+"   pw.close(); \n"
				+"   } \n"
				+"   catch(Exception e) { \n"
				+"   System.err.println(\" Error : e.getMessage() \"); \n"
				+"   } \n"
				+" } \n"
				//+"   FileWriter fw = new FileWriter(\"/Users/mian/inclaspath/Pass.txt\" , true); \n"
				+"  public static void printRangePass() { \n"
				+"   try { \n"
				+"   File fw1 = new File(\"Pass.txt\"); \n"
				+"   if (fw1.exists() == false) { \n"
				+" 	 fw1.createNewFile(); \n"	
				+"   }\n"
				+"	 PrintWriter pw1 = new PrintWriter(new FileWriter (fw1, true));"
				+"   for (Integer i2 : pass) { \n"
				+"      pw1.append(i2+\"\\n\");\n"
				+"   } \n"
				+"   pw1.close(); \n"
				+"   } \n"
				+"   catch(Exception e) { \n"
				+"   System.err.println(\" Error : e.getMessage() \"); \n"
				+"   } \n"
				+" } \n"
				+"   public static void checkStartAndStopValue(int i) { \n"
				+"   try { \n";
		return temp3;
	}



	public String programEndPart2(){
		String temp4 = "\n pass.add(i); \n";
		// this statement will add second argument to the arraylist to be used for x y graphs.
		// its sequence is x then 0 in case of empty and value of y if not empty
		if (argumentTwo.equals("")){
			temp4 = temp4 + "pass.add(0);\n";
		}
		else {
			temp4 = temp4 + " pass.add(" + argumentTwo + ");\n";
		}

		temp4	=	temp4
				+  "  } \n"
				+  "  catch (Throwable t) { \n"
				+  "  startedByFailing = true; \n"
				+  "  isCurrentlyFailing = true; \n"
				+  "  fail.add(i); \n ";
		// this statement will add second argument to the arraylist to be used for x y graphs.
		// its sequence is x then 0 in case of empty and value of y if not empty
		if (argumentTwo.equals("")){
			temp4 = temp4 + "fail.add(0);\n";
		}
		else {
			temp4 = temp4 + " fail.add(" + argumentTwo + ");\n";
		}

		temp4	=	temp4
				+ " } \n } \n}";

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
		return "ADFD strategy";
	}

}