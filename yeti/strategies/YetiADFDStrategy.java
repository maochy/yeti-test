package yeti.strategies;

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
		JLabel txt = new JLabel("% DSS interesting values: YDS2040 ");
		p.add(txt);
		txt.setAlignmentX(0);

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
		p.add(useInterestingValuesSlider);

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
	String called = "";
	public String args = "";
	public int intIncrement = -50;


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
									call = call
											+ ","
											+ oldyt[k].getValue()
											.toString();
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

				+ " public static int start = -80;\n"
				+ " public static int stop = 80;\n\n"

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
						+ "  { \n"
						+ "  fail.add(i-1); \n"
						+ "  pass.add(i); \n"
						+ "  isCurrentlyFailing=false; \n"
						+ "  } \n } \n"
						+ "  catch(Throwable t) { \n"
						+ "  if (!isCurrentlyFailing) \n"
						+ "  { \n"
						+ "  pass.add(i-1); \n"
						+ "  fail.add(i); \n"
						+ "  isCurrentlyFailing = true; \n"
						+ " }  \n } \n } \n"
						+ " checkStartAndStopValue(stop); \n"
						+ "  printRangeFail(); \n"
						+ "  printRangePass();  \n"
						+ "  }\n";

		;
		return temp1;
	}



	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%   END PART %%%%%%%%%%%%%%%%%%%%//

	public String programEndPart(){
		String temp3 = "\n public static void printRangeFail() { \n"
				+"   try { \n"
				+"   PrintWriter pr = new PrintWriter(\"/Users/mian/inclaspath/Fail.txt\"); \n"
				+"   for (Integer i1 : fail) { \n"
				+"      pr.println(i1); \n"
				+"   } \n"
				+"   pr.close(); \n"
				+"   } \n"
				+"   catch(Exception e) { \n"
				+"   System.err.println(\" Error : e.getMessage() \"); \n"
				+"   } \n"
				+" } \n"
				+"  public static void printRangePass() { \n"
				+"   try { \n"
				+"   PrintWriter pr = new PrintWriter(\"/Users/mian/inclaspath/Pass.txt\"); \n"
				+"   for (Integer i2 : pass) { \n"
				+"      pr.println(i2); \n"
				+"   } \n"
				+"   pr.close(); \n"
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
		String temp4 = "\n pass.add(i); \n"
				+  "  } \n"
				+  "  catch (Throwable t) { \n"
				+  "  startedByFailing = true; \n"
				+  "  isCurrentlyFailing = true; \n"
				+  "  fail.add(i); \n } \n } \n}";
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
		return "Dirt Spot Sweeping Strategy Two";
	}

}