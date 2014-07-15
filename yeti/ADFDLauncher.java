package yeti;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import yeti.strategies.YetiADFDLongStrategy;
import yeti.strategies.YetiADFDPlusStrategy;
import yeti.strategies.YetiADFDStrategy;
import yeti.strategies.YetiADFDWideStrategy;

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
 * For correct working of ADFDPlus graphical interface, add the following to your classpath according to your directory structure.
 * 
 * export JAVA_HOME=$(/usr/libexec/java_home)
 * export CLASSPATH=$CLASSPATH:/Users/mian/daikonparent/daikon/daikon.jar
 * export CLASSPATH=$CLASSPATH:/Users/mian/git/yeti-test/lib/jfreechart-1.0.14.jar
 * export CLASSPATH=$CLASSPATH:/Users/mian/git/yeti-test/lib/jcommon-1.0.17.jar
 * export CLASSPATH=$CLASSPATH:/Users/mian/git/yeti-test/lib/jfreechart-1.0.14-experimental.jar
 * export DAIKONDIR=/Users/mian/daikonparent/daikon
 * source $DAIKONDIR/scripts/daikon.bashrc
 * export CLASSPATH=$CLASSPATH:/Users/mian/git/yeti-test/
 * export CLASSPATH=$CLASSPATH:/Users/mian/git/yeti-test/lib/javassist.jar
 * export CLASSPATH=$CLASSPATH:/Users/mian/javaTest
 * export CLASSPATH=$CLASSPATH:/Users/mian/randoopparent/randoop/bin
 * export CLASSPATH=$CLASSPATH:/Users/mian/randoopparent/randoop.jar
 * export CLASSPATH=$CLASSPATH:/Users/mian/git/yeti-test
 * export CLASSPATH=$CLASSPATH:/Users/mian/git/yeti-test/lib/javassist.jar
 * # Setting PATH for Python 3.4
 * # The orginal version is saved in .bash_profile.pysave
 * PATH="/Library/Frameworks/Python.framework/Versions/3.4/bin:${PATH}"
 * export PATH
 *
 */
public class ADFDLauncher extends JFrame{

	private static final boolean DEBUG = false;

	/**
	 * panel1 is the left panel in the GUI which contains all the controls including Labels, textfields, buttons etc.
	 */
	private JPanel panel1 = new JPanel();

	/**
	 * panel2 is the top panel in GUI the which contains the heading "ADFD+" and the Logo image.
	 */
	private JPanel panel2 = new JPanel();

	/**
	 * panel3 is the middle panel in the GUI which contains the graph for a SUT generated at the end of execution.
	 */
	static JPanel panel3 = new JPanel();

	/**
	 * panel4 works as a container to hold panel1, panel2, panel3 and panel5
	 */
	private JPanel panel4 = new JPanel(new BorderLayout());

	/**
	 * panel5 is the right panel in the GUI which contains the invariants for a SUT generated at the end of execution.
	 */
	private JTextArea panel5 = new JTextArea();

	/**
	 * scrolling is a scrollbar added to panel3 to scroll horizontally when multiple graphs are generated.
	 */
	private JScrollPane scrolling = new JScrollPane(panel3);

	/**
	 * scrolling1 is a scrollbar added to panel5 to scroll horizontally when multiple invariants are generated.
	 */
	private JScrollPane scrolling1 = new JScrollPane(panel5);

	/**
	 * generated_TextField is used to display the number of files generated by the testing process.
	 */
	JTextField 		generated_TextField;

	/**
	 * compile_TextField is used to display the number of files compiled by the testing process.
	 */
	JTextField		compile_TextField;

	/**
	 * executed_TextField is used to display the number of files executed by the testing process.
	 */
	JTextField 		execute_TextField;

	/**
	 * browse_TextField is used to display the file under test.
	 */
	JTextField browse_TextField;

	/**
	 * rangeValue_TextField is a text field which takes the value from the user. This is a radius value used as a reference to search around the found failure.
	 */
	JTextField		rangeValue_TextField;
	//	JTextField		maxValue_TextField;


	/**
	 * plot1_Button which is labeled as Draw Fault Domain starts the testing process.
	 */
	JButton 		plot1_Button;

	/**
	 * This is a Jlabel which is labeled as Executed files
	 */
	JLabel 			execute_Label;

	/**
	 * This is a Jlabel which is labeled as Compiled files
	 */
	JLabel 			compile_Label;

	/**
	 * This is a Jlabel which is labeled as Generated files
	 */
	JLabel 			generated_Label;

	/**
	 * time1_ComboBox displays the optional values from which user can select for the current test session. 
	 */
	JComboBox 		time1_ComboBox;

	/**
	 * time2_ComboBox displays the options of minutes or seconds from which user can select for the current test session. 
	 */
	JComboBox		time2_ComboBox;


	/**
	 * chart_label_CheckBox displays the options labeled chart or without labeled. 
	 */
	JCheckBox 		chart_label_CheckBox;

	/**
	 * testFilePathInitial contains the path to the directory from which the .jar is executed. 
	 */
	public static String testFilePathInitial = ".";

	/**
	 * testFilePathFinal contains the -yetiPath=. followed by the path to the directory from which the .jar is executed. It is passed to yeti as argument to set the path. 
	 */
	String 		testFilePathFinal 	 = "-yetiPath=.";

	/**
	 * fileName contains the module or file name which is to be tested in the current session. Its default value is set to the sample file for testing included with yeti package. 
	 */	
	String 		fileName			 =   "-testModules=OneDimensionalPointFailureDomain1"; //"-testModules=yeti.test.YetiTest"; 

	/**
	 * fileUnderTest contains the name of the file under test which is used in the screen capture name. 
	 */	
	String 		fileUnderTest			 = ""; 

	/**
	 * language contains the language of the program which is to be tested in the current session. Its default value is set to Java language. 
	 */
	String 		language 			 = "-java";

	/**
	 * strategy contains the strategy which is to be used in the current session. Its default value is set to the ADFD+ strategy. 
	 */
	String 		strategy 			 = "-ADFDLong";

	/**
	 * gui contains the option to enable or disable the GUI of the yeti test session. To enable it, change its value to -gui 
	 */
	String 		gui 				 = "";

	/**
	 * logs contains the option to enable or disable the logs of the yeti test session. To disable it, change its value to blank i.e. logs = "";
	 */
	String 		logs 				 = "-nologs";

	/**
	 * time contains the option to run the yeti for that amount of time. The default value is 5 seconds. 
	 */
	String 		time 				 = "-time=5s";

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
	 *  languages contains all the languages which are supported by YETI. One of them can be selected from languages combo box within the GUI. 
	 */
	String[] languages = {"Java", ".Net", "JML", "Pharo", "CoFoJa", "Kermeta" };

	/**
	 *  strategies contains all the strategies which are supported by YETI. One of them can be selected from strategies combo box within the GUI. 
	 */
	String[] strategies = {"ADFDLong", "ADFDWide", "ADFDPlus", "ADFD", "DSSR", "Random", "Chromosome", "Evolutionary", "Random Plus", "Random Plus Periodic", "Random Plus Decreasing" };

	/**
	 *  time1 contains some specific values which can be selected from time combo box with in the GUI. 
	 */
	String[] time1 = {"5", "2", "10", "15", "20", "30", "40", "50", "60", "70", "80", "90", "100" };

	/**
	 *  time2 contains the options of seconds or minutes which can be selected from time combo box with in the GUI. 
	 */
	String[] time2 = {"Seconds", "Minutes" };

	/**
	 *  command contains all the choices selected by the user and pass it to Yeti as arguments. 
	 */
	String[] command;

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
	 *   thread1 is the first thread which is responsible to run the yeti on specified file and specified values.
	 */
	Thread thread1 = new Thread(new Thread1());

	/**
	 * gbc is used to specify the layout of compenents in panel 1.  
	 */
	GridBagConstraints 	gbc;

	/**
	 * execute_ProgressBar shows the total progress of the test session that includes yeti exection, generation, compilation and execution of files, graph gneration and invariants genration.  
	 */
	JProgressBar 		execute_ProgressBar;

	/**
	 * duckImage is used to hold the logo of ADFD+ in the panel2 in GUI.  
	 */
	ImageIcon 			duckImage;

	/**
	 * duckImageLabel is used to hold the logo in panel1 in GUI.  
	 */
	JLabel				duckImageLabel;

	/**
	 * Variable to avoid overwriting of screen capture.  
	 */
	private int i = 0;


	/**
	 * Constructor of the class to create frame and draw components on the frame. 
	 */
	public ADFDLauncher(){
		// A method is added to automatically delete the unwanted files before the next run.
		deleteOldTestFiles();
		this.setTitle("Yeti Launcher");
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = new Dimension();
		dim = tk.getScreenSize();
		int xPos = dim.width;
		int yPos = dim.height;
		this.setSize(xPos, yPos);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		drawAllComponents();
		panel4.setBackground(Color.WHITE);
		panel4.add(panel1, BorderLayout.NORTH);
		this.add(panel4, BorderLayout.WEST);
		this.add(panel2, BorderLayout.NORTH);
		scrolling.setBorder(new EmptyBorder(0, 0, 0, 0));
		this.add(scrolling, BorderLayout.CENTER);
		//this.getRootPane().setDefaultButton(plot1_Button);
		this.setVisible(true);
		//this.setAlwaysOnTop(true);
		//this.pack();



	}


	/**
	 * This method delete the test files of the last executed experiment/Test which includes pass.txt, fail.txt, Cx.class and Cx.java.
	 * It is added to simplify the process. If the files from the last test remain in the folder
	 * then the results of new tests are misleading.
	 */
	public void deleteOldTestFiles(){

		File directory = new File(".");
		File[] files = directory.listFiles();
		for (File f : files)
		{
			if ((f.getName().startsWith("C") || f.getName().startsWith("PassX") || f.getName().startsWith("FailX") || f.getName().startsWith("PassY") || f.getName().startsWith("FailY")))
			{
				f.delete();
			}

		}
	}



	/**
	 *  Method to reset the static variables.
	 *  
	 */
	public void resetStaticVariables(){

	}



	/**
	 * @param args
	 * // Main method of the class. It calls the constructor of the class to draw the ADFD+ GUI.
	 */
	public static void main(String[] args){

		new ADFDLauncher();

	}


	/**
	 *  Method to draw all the components on the frame.
	 *  It adds and arranges all the labels, textboxes and buttons etc to the panel and
	 *  arrange the panels in a proper way.
	 */
	public void drawAllComponents(){

		gbc = new GridBagConstraints();
		panel1.setBorder(new TitledBorder("Test Settings"));
		panel1.setLayout(new GridBagLayout());
		panel1.setBackground(Color.WHITE);
		gbc.insets = new Insets(2, 2, 8, 2);
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.BOTH;
		////////// Language Label, ComboBox and ActionListener //////////

		JLabel language_Label = new JLabel("Language:");
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel1.add(language_Label, gbc);

		final JComboBox language_ComboBox = new JComboBox(languages);
		gbc.gridx = 1;
		gbc.gridy = 0;
		panel1.add(language_ComboBox, gbc);

		language_ComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(language_ComboBox.getSelectedItem().equals("Java")){
					language = "-java";
				} else if(language_ComboBox.getSelectedItem().equals(".Net")){
					language = "-dotnet";
				}else if(language_ComboBox.getSelectedItem().equals("JML")){
					language = "-jml";
				}else if(language_ComboBox.getSelectedItem().equals("CoFoJa")){
					language = "-cofoja";
				}else if(language_ComboBox.getSelectedItem().equals("Kermeta")){
					language = "-kermeta";
				}
				else
					language = "-pharo";

			}
		});


		//////////////////////////////////////////////////////////////////
		/////////// Strategy Label, ComboBox and ActionListener //////////
		//////////////////////////////////////////////////////////////////

		JLabel strategy_Label = new JLabel("Strategy:");
		gbc.gridx = 0;
		gbc.gridy = 1;
		panel1.add(strategy_Label, gbc);

		final JComboBox strategy_ComboBox = new JComboBox(strategies);
		gbc.gridx = 1;
		gbc.gridy = 1;
		panel1.add(strategy_ComboBox, gbc);

		strategy_ComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(strategy_ComboBox.getSelectedItem().equals("Random")){
					strategy = "-random";
					hideItems();
				}else if (strategy_ComboBox.getSelectedItem().equals("Random Plus")){
					strategy = "-randomPlus";
					hideItems();
				} else if (strategy_ComboBox.getSelectedItem().equals("DSSR")){
					strategy = "-DSSR";
					hideItems();
				} else if (strategy_ComboBox.getSelectedItem().equals("Random Plus Periodic")){
					strategy = "-randomPlusPeriodic";
					hideItems();
				} else if (strategy_ComboBox.getSelectedItem().equals("Random Plus Decreasing")){
					strategy = "-randomPlusDecreasing";
					hideItems();
				} else if (strategy_ComboBox.getSelectedItem().equals("Chromosome")){
					strategy = "-chromosome";
					hideItems();
				} else if (strategy_ComboBox.getSelectedItem().equals("Evolutionary")){
					strategy = "-evolutionary";
					hideItems();
				} else if (strategy_ComboBox.getSelectedItem().equals("ADFD")){
					strategy = "-ADFD";
					unhideItems();
				} else if (strategy_ComboBox.getSelectedItem().equals("ADFDLong")){
					strategy = "-ADFDLong";
					unhideItems();
				} else if (strategy_ComboBox.getSelectedItem().equals("ADFDWide")){
					strategy = "-ADFDWide";
					unhideItems();
				}
				else
				{
					strategy = "-ADFDPlus";
					unhideItems();
				}

			}
		});



		//////////////////////////////////////////////////////////////////
		///////// Time label, Time1 Combo Box and Time2 Combo Box ////////
		//////////////////////////////////////////////////////////////////


		JLabel duration_Label = new JLabel("Duration:");
		gbc.gridx = 0;
		gbc.gridy = 2;
		panel1.add(duration_Label, gbc);

		time1_ComboBox = new JComboBox(time1);
		gbc.gridx = 1;
		gbc.gridy = 2;
		panel1.add(time1_ComboBox, gbc);

		time2_ComboBox = new JComboBox(time2);
		gbc.gridx = 1;
		gbc.gridy = 3;
		panel1.add(time2_ComboBox, gbc);

		//////////////////////////////////////////////////////////////////
		/////////// GUI Label, check box and ActionListener //////////////
		//////////////////////////////////////////////////////////////////

		JLabel gui_Label = new JLabel("Test GUI:");
		gbc.gridx = 0;
		gbc.gridy = 4;
		panel1.add(gui_Label, gbc);

		final JCheckBox gui_CheckBox = new JCheckBox();
		gui_CheckBox.setSelected(true);
		gbc.gridx = 1;
		gbc.gridy = 4;
		panel1.add(gui_CheckBox, gbc);

		gui_CheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (gui_CheckBox.isSelected())
					gui = "-gui";
			}
		});

		//////////////////////////////////////////////////////////////////
		/////////// Logs Label, check box and ActionListener /////////////
		//////////////////////////////////////////////////////////////////

		JLabel logs_Label = new JLabel("Test logs:");
		gbc.gridx = 0;
		gbc.gridy = 5;
		panel1.add(logs_Label, gbc);

		final JCheckBox logs_CheckBox = new JCheckBox();
		gbc.gridx = 1;
		gbc.gridy = 5;
		panel1.add(logs_CheckBox, gbc);

		logs_CheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (logs_CheckBox.isSelected()){
					logs = "-rawlogs";
				}
				else {
					logs = "-nologs";
				}
			} 

		});


		//////////////////////////////////////////////////////////////////
		/////////// Browse Label, Button, TextField and ActionListener ///
		//////////////////////////////////////////////////////////////////

		JLabel browse_Label = new JLabel("Test File:");
		gbc.gridx = 0;
		gbc.gridy = 6;
		panel1.add(browse_Label, gbc);

		JButton browse_Button = new JButton("Browse");
		gbc.gridx = 1;
		gbc.gridy = 6;
		panel1.add(browse_Button, gbc);

		browse_TextField = new JTextField("yeti.test.YetiTest");
		gbc.gridx = 1;
		gbc.gridy = 7;
		panel1.add(browse_TextField, gbc);

		browse_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(".");
				chooser.showOpenDialog(null);
				File file = chooser.getSelectedFile();
				String fullPath = file.getAbsolutePath();
				char extentionSeperator = '.';
				char pathSeperator = '/';


				int sep = fullPath.lastIndexOf(pathSeperator); 

				testFilePathInitial = fullPath.substring(0, sep + 1);
				testFilePathFinal = "-yetiPath=" + fullPath.substring(0 , sep + 1);

				int dot = fullPath.lastIndexOf(extentionSeperator);
				int sept = fullPath.lastIndexOf(pathSeperator);
				fileName = fullPath.substring(sept+1,dot);

				browse_TextField.setText(fileName);

				// The following statement do not work if there are periods in the path.
				// This problem is solved by adding a line of command in run method.
				fileName = "-testModules="+fileName;



			}
		});

		//////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////

		JLabel range = new JLabel("Domain Range:");
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.gridwidth = 1;
		panel1.add(range, gbc);

		// changed to 100 for test purpose.
		//		minValue_TextField = new JTextField("" + Integer.MIN_VALUE);
		rangeValue_TextField = new JTextField("" + 5);
		gbc.gridx = 1;
		gbc.gridy = 8;
		gbc.gridwidth = 1;
		panel1.add(rangeValue_TextField, gbc);	



		//////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////



		/////////////////////////////////////////////////////////////////////////////////////
		////// Button, TextField and action listener for the number of generated Files //////
		/////////////////////////////////////////////////////////////////////////////////////

		generated_Label = new JLabel("Count Files:");
		gbc.gridx = 0;
		gbc.gridy = 10;
		gbc.gridwidth = 1;
		panel1.add(generated_Label, gbc);

		generated_TextField = new JTextField("");
		gbc.gridx = 1;
		gbc.gridy = 10;
		gbc.gridwidth = 1;
		panel1.add(generated_TextField, gbc);



		////////////////////////////////////////////////////////////////////////////////////
		////// Button, TextField and action listener for the number of compiled Files //////
		////////////////////////////////////////////////////////////////////////////////////


		compile_Label = new JLabel("Compile Files:");
		gbc.gridx = 0;
		gbc.gridy = 11;
		panel1.add(compile_Label, gbc);

		compile_TextField = new JTextField("");
		gbc.gridx = 1;
		gbc.gridy = 11;
		gbc.gridwidth = 1;
		panel1.add(compile_TextField, gbc);







		//////////////////////////////////////////////////////////////////////////////////
		////// Button, TextField and actionlistener for the number of executed Files /////
		//////////////////////////////////////////////////////////////////////////////////


		execute_Label = new JLabel("Execute Files:");
		gbc.gridx = 0;
		gbc.gridy = 12;
		panel1.add(execute_Label, gbc);

		execute_TextField = new JTextField("");
		gbc.gridx = 1;
		gbc.gridy = 12;
		gbc.gridwidth = 1;
		panel1.add(execute_TextField, gbc);

		execute_ProgressBar = new JProgressBar(0,100);
		gbc.gridx = 1;
		gbc.gridy = 13;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(execute_ProgressBar, gbc);



		///////////////////////////////////////////////////////////////////////////
		////// Button and action listener for plotting graph //////////////////////
		///////////////////////////////////////////////////////////////////////////


		plot1_Button = new JButton("Draw Fault Domain");
		gbc.gridx = 1;
		gbc.gridy = 14;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(plot1_Button, gbc);

		plot1_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try{

					// The runTest method executes YETi 
					progressStart();
					runTest();

					// This time is used so that count etc dont execute untill yeti is finished 
					// So once yeti is finished the count will execute to count the generated files
					// then compile to compile it and execute to execute it.
					// Each one also has some delay inside their for loop because
					// creating text files and compiling etc takes some time.

					new java.util.Timer().schedule( 
							new java.util.TimerTask() {
								@Override
								public void run() {
									countFiles();
									compileFiles();
									executeFiles();
									drawGraph();
									panel3.revalidate();

								}
							}, 
							waitForYetiToFinish
							);


				}
				catch(Exception e1){
					e1.printStackTrace();
				}


			}


		});

		roseImage();

		// this label is added to create a gap between exit and help from plot button.

		JLabel emptyLabel1 = new JLabel("");
		gbc.gridx = 0;
		gbc.gridy = 15;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(emptyLabel1, gbc);


		// Adding a button to capture a screen of the window to store the results

		JButton screen_capture_Button = new JButton("Screen Capture at any time");
		gbc.gridx = 1;
		gbc.gridy = 16;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(screen_capture_Button, gbc);

		screen_capture_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				try {

					// To get the name of the file under test and use it in screen capture name.
					fileUnderTest = browse_TextField.getText();

					Robot robot = new Robot();

					BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
					ImageIO.write(screenShot, "JPG", new File("" + fileUnderTest + "_screenShot_" + i++ +".jpg"));


					// The following code will inform the user about the name and capture of the screen shot.
					JOptionPane.showMessageDialog(null, "Screen capture " + "" + fileUnderTest + "_screenShot_" + i +".jpg" + " taken and stored in the current directory", "Screen Capture", JOptionPane.INFORMATION_MESSAGE);

				} catch (Exception e1){

					e1.printStackTrace();
				}
			}

		});



		JButton help_Button = new JButton("Help");
		gbc.gridx = 0;
		gbc.gridy = 17;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(help_Button, gbc);

		help_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JOptionPane.showMessageDialog(null, "1.    In language combo box select the language of the program under test. \n" +
						"2.    In strategy combo box select the strategy for the current test session.\n"+ 
						"3.    For duration select time for the current test session either in minutes or seconds.\n"+ 
						"4.    Select GUI checkbox if you want to see real time YETI GUI during test execution.\n"+
						"5.    Select Log checkbox if you want to dump logs of the test session.\n"+
						"6.    Click browse button to select the file you want to test. \n"+
						"7.    Name of the file selected will appear in the text field without extension.\n"+
						"8.    Run Button will run YETI on selected file.\n"+
						"9.    ADFD specific, count button count the number of generated files.\n" +
						"10.  ADFD speific, Compile button compiles generated files.\n"+
						"11.  ADFD speific, Execute button executes the compiled files.\n"+
						"12.  ADFD speific, Progress bar shows the progess of executing process.\n"+
						"13.  ADFD speific, Plot button plots the fault domain on the graph.\n" +
						"14.  For more detail write an email to manuel.oriol@ch.abb.com \n", "YETI GUI Help", JOptionPane.INFORMATION_MESSAGE);



			}
		});

		JButton exit_Button = new JButton("Exit");
		gbc.gridx = 1;
		gbc.gridy = 17;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(exit_Button, gbc);

		exit_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				System.exit(0);
			}
		});

		JLabel heading_Label = new JLabel("  Automated Discovery of Failure Domain Plus");
		heading_Label.setFont(heading_Label.getFont().deriveFont(32.0f ));
		panel2.add(heading_Label);
		panel2.setBackground(Color.WHITE);

		panel3.setBorder(new TitledBorder("Plot Fault Domain "));
		panel3.setBackground(Color.WHITE);


	}


	/**
	 * Method to parse the arguments of YETI and passed it to the YETI.
	 * It takes the user supplied arguments from ADFD+ GUI and execute YETI to test it.
	 */

	public void runTest() {


		fileName = "-testModules=" + browse_TextField.getText();
		time = "-time=" + time1_ComboBox.getSelectedItem().toString();
		waitForYetiToFinish = Integer.parseInt(time1_ComboBox.getSelectedItem().toString());
		if(time2_ComboBox.getSelectedItem().equals("Minutes")){
			waitForYetiToFinish = waitForYetiToFinish * 60 * 1000;
			time = time + "mn";
		} else {
			time = time + "s";
			waitForYetiToFinish = waitForYetiToFinish * 1000;
		}

		// @@@@@@@ Output the values to see correct or not for troubleshooting @@@@@@ //

		//	JOptionPane.showMessageDialog(null, language, " language is", JOptionPane.PLAIN_MESSAGE);
		//	JOptionPane.showMessageDialog(null, time, " time is ", JOptionPane.PLAIN_MESSAGE);
		//	JOptionPane.showMessageDialog(null, logs, " logs is ", JOptionPane.PLAIN_MESSAGE);
		//		JOptionPane.showMessageDialog(null, strategy, " strategy is ",JOptionPane.PLAIN_MESSAGE);
		//	JOptionPane.showMessageDialog(null, fileName, "fileName is", JOptionPane.PLAIN_MESSAGE);
		//	JOptionPane.showMessageDialog(null, testFilePathFinal, "testFilePath is", JOptionPane.PLAIN_MESSAGE);
		//	JOptionPane.showMessageDialog(null, testFilePathInitial, "testFilePathInitial value is", JOptionPane.PLAIN_MESSAGE);			

		list.add(language);
		list.add(strategy);
		list.add(time);
		if (!gui.isEmpty())
			list.add(gui);
		list.add(logs);
		list.add(fileName);
		list.add(testFilePathFinal);
		//		YetiADFDPlusStrategy.lowerLimit = rangeValue_TextField.getText();
		//		YetiADFDPlusStrategy.upperLimit = maxValue_TextField.getText();
		int temp = Integer.parseInt(rangeValue_TextField.getText());
		YetiADFDStrategy.rangeToPlot +=  temp;
		YetiADFDPlusStrategy.rangeToPlot +=  temp;
		//		YetiADFDLongStrategy.rangeToPlot +=  temp;
		//			JOptionPane.showMessageDialog(null, YetiADFDPlusStrategy.rangeToPlot, " The value for range to plot is ", JOptionPane.PLAIN_MESSAGE);
		command = list.toArray(new String[list.size()]);



		try{
			thread1.start();
		}
		catch(Exception e1){
			e1.printStackTrace();
		}
	}


	/**
	 * This method draw the graph of failure domain and pass domain. It create an object of the class LogGrapher on 
	 * the basis of the number of C* files created by YETI ADFD+ strategy. At the moment it can evaluate a method of 
	 * up to 3 arguments where C0, C1 and C2 files will be created.
	 *  
	 */

	public void drawGraph(){

		//		JOptionPane.showMessageDialog(null, language, " drawGraph method is executed.", JOptionPane.PLAIN_MESSAGE);



		//JOptionPane.showMessageDialog(null,  " Graph is");

		/* I added this if statement to control the graphs
		 * so if we get one fault then one C file will be generated and
		 * thus one argument graph will be create 
		 * For simplicity we are doing it as a separate class
		 * Later we will try to do it using one class with different methods or 
		 * method overloading kind of.
		 */

		//		if (countCompileFiles == 1){
		//			LogGrapher2 demo = new LogGrapher2("Failure Domains");
		//
		//	}

		//				if ((countCompileFiles == 1)||(countCompileFiles == 2)){
		if((strategy.equalsIgnoreCase("-ADFD"))||(strategy.equalsIgnoreCase("-ADFDPlus"))){
			//				JOptionPane.showMessageDialog(null, language, " Graph is 1 and 2 dim ADFD/ADFD+", JOptionPane.PLAIN_MESSAGE);
			ADFDAndADFDPlusGraphGenerator demo = new ADFDAndADFDPlusGraphGenerator("Failure Domains");


		}

		if((strategy.equalsIgnoreCase("-ADFDLong"))&&(YetiADFDLongStrategy.programDim == 1)){
			JOptionPane.showMessageDialog(null, language, " Graph is 1 dim ADFDLong", JOptionPane.PLAIN_MESSAGE);
			ADFDLongGraphGeneratorFor1Arg demo = new ADFDLongGraphGeneratorFor1Arg("Failure Domains");

		}
		
		if((strategy.equalsIgnoreCase("-ADFDLong"))&&(YetiADFDLongStrategy.programDim == 2)){
			JOptionPane.showMessageDialog(null, language, " Graph is 2 dim ADFDLong", JOptionPane.PLAIN_MESSAGE);
			ADFDLongGraphGeneratorFor2Arg demo = new ADFDLongGraphGeneratorFor2Arg("Failure Domains");

		}

		if((strategy.equalsIgnoreCase("-ADFDWide"))&&(YetiADFDWideStrategy.programDim == 1)){
//			JOptionPane.showMessageDialog(null, language, " Graph is 1 dim ADFDWide", JOptionPane.PLAIN_MESSAGE);
			ADFDWideGraphGeneratorFor1Arg demo = new ADFDWideGraphGeneratorFor1Arg("Failure Domains");

		}
		
		if((strategy.equalsIgnoreCase("-ADFDWide"))&&(YetiADFDWideStrategy.programDim == 2)){
//			JOptionPane.showMessageDialog(null, language, " Graph is 2 dim ADFDWide", JOptionPane.PLAIN_MESSAGE);
			ADFDWideGraphGeneratorFor2Arg demo = new ADFDWideGraphGeneratorFor2Arg("Failure Domains");

		}


		//		if (YetiADFDPlusStrategy.plotOneDimOrTwoDim == 1){
		//			LogGrapher1 demo = new LogGrapher1("Failure Domains");
		//		}
		//		else 
		//		{
		//			LogGrapher2 demo = new LogGrapher2("Failure Domains");
		//		}
		//





		ADFDLauncher.panel3.validate();
		panel3.add(scrolling1,BorderLayout.SOUTH);

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
						filesToCompile.add(files);
						Thread.sleep(200);

					}

				}
			}


			filesToCompileArray = filesToCompile.toArray(new String[filesToCompile.size()]);

			generated_TextField.setText(filesToCompileArray.length + " files") ;


		}
		catch(Exception e1){
			e1.printStackTrace();
		}

		// The following if statement is added to quit the program in the case when no error is found and 
		// no files are generated. 

		// It is not working properly so they are commented out.

		//		if (filesToCompileArray.length == 0){


		//JOptionPane pane = new JOptionPane();
		//JDialog dialog = pane.createDialog("Hi there!");
		//dialog.setAlwaysOnTop(true);
		//dialog.show();

		//			int result = JOptionPane.showConfirmDialog(null,
		//			        "No failure found in the SUT and no files are generated to process \nPress the button to exit testing",
		//			        "Confirm Quit", JOptionPane.DEFAULT_OPTION);
		//			if (result == 0) System.exit(0);

		//		}
	}

	/**
	 * This method compiles files generated by the ADFD+ strategy
	 */
	public void compileFiles() {
		try{
			int count = 0;
			for (int i = 0; i < filesToCompileArray.length; i++){
				Process pro1;
				Thread.sleep(200);
				// Trying to compile file, the original is not working from GUI so commented out.
				String compileLocal = "javac -g " + filesToCompileArray[i];
				pro1 = Runtime.getRuntime().exec(compileLocal);
				//		pro1 = Runtime.getRuntime().exec("javac -g " + testFilePathInitial + filesToCompileArray[i]);
				pro1.waitFor();
				count = count + 1;
			}
			countCompileFiles = count;
			compile_TextField.setText(count + " files");
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
			//JOptionPane.showMessageDialog(null, testFilePathInitial, "Thread 2 Starting", JOptionPane.CANCEL_OPTION);
			//		progressStart();
			//JOptionPane.showMessageDialog(null, testFilePathInitial, "Thread 2 Joining", JOptionPane.CANCEL_OPTION);
			//thread2.join();
			//JOptionPane.showMessageDialog(null, testFilePathInitial, "Thread 3 Starting", JOptionPane.CANCEL_OPTION);
			executeJavaFiles();
			//JOptionPane.showMessageDialog(null, testFilePathInitial, "Thread 3 joining", JOptionPane.CANCEL_OPTION);
			//thread3.join();

			//temporary disabling daikon.
			String result = executeDaikon();

			//To display the generated invariants in GUI, panel 5


			//temporary disabling the following two lines.
			panel5.setText("=============Test LOGS=============\n\n Candidate invariant:"+result 
					+ "\n\n=============Test Case=============\n\n" + YetiLog.proc.processLogs());



			//JOptionPane.showMessageDialog(null, testFilePathInitial, "Thread 4 Starting", JOptionPane.CANCEL_OPTION);
			progressStop();
			//JOptionPane.showMessageDialog(null, testFilePathInitial, "Thread 4 joining", JOptionPane.CANCEL_OPTION);
			//thread4.join();



		}


		catch(Exception e1){
			e1.printStackTrace();
		}

	}


	/**
	 * This method shows and control the progress bar. 
	 * Thread 2 to initialize the progress bar to indeterminate state.
	 */
	public void progressStart(){

		try {
			execute_ProgressBar.setIndeterminate(true);
			execute_ProgressBar.setStringPainted(true); 

		}

		catch (Exception e1){
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
		String daikonOptions = "--config_option daikon.inv.unary.scalar.LowerBound.minimal_interesting=-10000 "+// TODO Adapt to boundaries set up for the failure
				//		String daikonOptions = "--config_option daikon.inv.unary.scalar.LowerBound.minimal_interesting=Integer.MIN_VALUE "+// TODO Adapt to boundaries set up for the failure
				"--config_option daikon.inv.unary.scalar.LowerBound.maximal_interesting=20000 "+ // TODO
				"--config_option daikon.inv.unary.scalar.UpperBound.maximal_interesting=20000 "+// TODO
				"--config_option daikon.inv.unary.scalar.UpperBound.minimal_interesting=-10000 "+// TODO
				//				"--config_option daikon.inv.unary.scalar.LowerBound.maximal_interesting=Integer.MAX_VALUE "+ // TODO
				//				"--config_option daikon.inv.unary.scalar.UpperBound.maximal_interesting=Integer.MAX_VALUE "+// TODO
				//				"--config_option daikon.inv.unary.scalar.UpperBound.minimal_interesting=Integer.MIN_VALUE "+// TODO
				"--config_option daikon.PptRelation.enable_object_user=true "+
				"--config_option daikon.PptSliceEquality.set_per_var=true "+
				"--conf_limit 0 --var-select-pattern=^i$";// TODO Make more generic

		// I am disabling if statement so that invariants are generated for two arguments even if one argument program is under test.
		if(YetiADFDPlusStrategy.twoDimProgram == 2 || YetiADFDStrategy.twoDimProgram == 2 || YetiADFDLongStrategy.programDim == 2){
			daikonOptions = daikonOptions + "|^j$";
		}

		daikonOptions = daikonOptions + " C";



		try {
			int count = 0;
			// for each file we will execute a session with Daikon

			// Mian is modifying it so that Daikon is executed only once. 
			// Because we get the same invariants for each file and 
			// It works fine when one file is generated, compiled and executed. 
			// If successful I will leave like it this.s

			//			for (int i = 0; i < filesToCompileArray.length; i++){

			if (filesToCompileArray.length > 0){
				int i = 0;
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
	 * 	Thread 4 to update the status of progress bar when pass and fail files are generated 
	 */
	public void progressStop(){


		//		File file = new File(testFilePathInitial + "Pass.txt");
		File file = new File("PassX.txt");
		boolean exists = false;

		while (!exists){

			exists = file.exists();


		}
		try {
			execute_ProgressBar.setValue(execute_ProgressBar.getMaximum());
			execute_ProgressBar.setIndeterminate(false);
			execute_TextField.setText(totalFiles + " files");


		}
		catch(Exception e1){
			e1.printStackTrace();
		}

	}


	/**
	 * 	Thread 1 to execute YETI for finding faults and generating CX.java files, where X is int variable 
	 */
	@SuppressWarnings("unused")
	private class Thread1 implements Runnable{
		public void run(){
			try{
				Yeti.YetiRun(command);

			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}


	/**
	 * Method hides item in case strategy other than ADFD is selected for the current test session.
	 */
	public void hideItems(){
		execute_TextField.setVisible(false);
		execute_Label.setVisible(false);
		execute_ProgressBar.setVisible(false);
		plot1_Button.setVisible(false);
		compile_Label.setVisible(false);
		compile_TextField.setVisible(false);
		generated_Label.setVisible(false);
		generated_TextField.setVisible(false);


	}


	/**
	 * This method unhide components if ADFD strategy is selected.
	 */
	public void unhideItems(){
		execute_TextField.setVisible(true);
		execute_Label.setVisible(true);
		execute_ProgressBar.setVisible(true);
		plot1_Button.setVisible(true);
		compile_Label.setVisible(true);
		compile_TextField.setVisible(true);
		generated_Label.setVisible(true);
		generated_TextField.setVisible(true);


	}


	/**
	 * This method adds ADFD+ logo at the right of the title in GUI.
	 */
	public void roseImage(){
		try{
			duckImage = new ImageIcon(getClass().getResource("Rose.jpg"));
			duckImageLabel = new JLabel(duckImage);
			panel2.add(duckImageLabel);
		}
		catch (Exception e1){
			e1.printStackTrace();

		}

	}

}





