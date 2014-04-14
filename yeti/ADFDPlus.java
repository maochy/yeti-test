package yeti;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import yeti.LogGrapher3;
import yeti.strategies.YetiADFDPlusStrategy;

import javax.swing.*;


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
public class ADFDPlus extends JFrame{

	private static final boolean DEBUG = false;
	private JPanel panel1 = new JPanel();
	private JPanel panel2 = new JPanel();
	static JPanel panel3 = new JPanel();
	private JPanel panel4 = new JPanel(new BorderLayout());
	private JTextArea panel5 = new JTextArea();
	private JScrollPane scrolling = new JScrollPane(panel3);
	private JScrollPane scrolling1 = new JScrollPane(panel5);

	JTextField 		generated_TextField;
	JTextField		compile_TextField;
	JTextField 		execute_TextField;
	JTextField		rangeValue_TextField;
	//	JTextField		maxValue_TextField;
	JButton 		plot1_Button;
	JLabel 			execute_Label;
	JLabel 			compile_Label;
	JLabel 			generated_Label;
	JComboBox 		time1_ComboBox;
	JComboBox		time2_ComboBox;


	public static String testFilePathInitial = ".";

	String 		testFilePathFinal 	 = "-yetiPath=.";
	String 		fileName			 = "-testModules=yeti.test.YetiTest"; 
	String 		language 			 = "-java";
	String 		strategy 			 = "-ADFDPlus";
	String 		gui 				 = "";
	String 		logs 				 = "-nologs";
	String 		time 				 = "-time=5s";
	static	int totalFiles 			 = 0;
	int 		countCompileFiles;
	int 		waitForYetiToFinish;



	String[] languages = {"Java", ".Net", "JML", "Pharo", "CoFoJa", "Kermeta" };
	String[] strategies = {"ADFDPlus", "ADFD", "DSSR", "Random", "Chromosome", "Evolutionary", "Random Plus", "Random Plus Periodic", "Random Plus Decreasing" };
	String[] time1 = {"2", "5", "10", "15", "20", "30", "40", "50", "60", "70", "80", "90", "100" };
	String[] time2 = {"Seconds", "Minutes" };
	String[] command;
	String[] filesToCompileArray;

	ArrayList<String> 	list 			 = new ArrayList<String>();
	ArrayList<String> 	filesToCompile	 = new ArrayList<String>();

	Thread thread1 = new Thread(new Thread1());


	GridBagConstraints 	gbc;
	JProgressBar 		execute_ProgressBar;
	ImageIcon 			duckImage;
	JLabel				duckImageLabel;


	/**
	 * Constructor of the class to create frame and draw components on the frame. 
	 */
	public ADFDPlus(){
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
		//this.add(panel3, BorderLayout.CENTER);
		this.add(scrolling, BorderLayout.CENTER);
		this.setVisible(true);
		//this.setAlwaysOnTop(true);
		//this.pack();



	}



	/**
	 * This method delete the test files of the last executed experiment/Test.
	 * It is added to simplify the process. If the files from the last test remain in the folder
	 * then the results of new tests are misleading.
	 */
	public void deleteOldTestFiles(){

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
	 * @param args
	 * // Main method of the class. It calls the constructor of the class to draw the ADFD+ GUI.
	 */
	public static void main(String[] args){

		new ADFDPlus();

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
					hideItems();
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
		gbc.gridx = 2;
		gbc.gridy = 2;
		panel1.add(time2_ComboBox, gbc);

		//////////////////////////////////////////////////////////////////
		/////////// GUI Label, check box and ActionListener //////////////
		//////////////////////////////////////////////////////////////////

		JLabel gui_Label = new JLabel("GUI:");
		gbc.gridx = 0;
		gbc.gridy = 3;
		panel1.add(gui_Label, gbc);

		final JCheckBox gui_CheckBox = new JCheckBox();
		gui_CheckBox.setSelected(true);
		gbc.gridx = 1;
		gbc.gridy = 3;
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

		JLabel logs_Label = new JLabel("Logs:");
		gbc.gridx = 0;
		gbc.gridy = 4;
		panel1.add(logs_Label, gbc);

		final JCheckBox logs_CheckBox = new JCheckBox();
		gbc.gridx = 1;
		gbc.gridy = 4;
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
		gbc.gridy = 5;
		panel1.add(browse_Label, gbc);

		JButton browse_Button = new JButton("Browse");
		gbc.gridx = 1;
		gbc.gridy = 5;
		panel1.add(browse_Button, gbc);

		final JTextField browse_TextField = new JTextField("yeti.test.YetiTest");
		gbc.gridx = 2;
		gbc.gridy = 5;
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

				fileName = "-testModules="+fileName;

			}
		});

		//////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////

		JLabel range = new JLabel("Domain Range:");
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 1;
		panel1.add(range, gbc);

		// changed to 100 for test purpose.
		//		minValue_TextField = new JTextField("" + Integer.MIN_VALUE);
		rangeValue_TextField = new JTextField("" + 5);
		gbc.gridx = 1;
		gbc.gridy = 6;
		gbc.gridwidth = 1;
		panel1.add(rangeValue_TextField, gbc);	



		/////////////////////////////////////////////////////////////////////////////////////
		////// Button, TextField and action listener for the number of generated Files //////
		/////////////////////////////////////////////////////////////////////////////////////

		generated_Label = new JLabel("Count Files:");
		gbc.gridx = 0;
		gbc.gridy = 9;
		gbc.gridwidth = 1;
		panel1.add(generated_Label, gbc);

		generated_TextField = new JTextField("");
		gbc.gridx = 1;
		gbc.gridy = 9;
		gbc.gridwidth = 1;
		panel1.add(generated_TextField, gbc);



		////////////////////////////////////////////////////////////////////////////////////
		////// Button, TextField and action listener for the number of compiled Files //////
		////////////////////////////////////////////////////////////////////////////////////


		compile_Label = new JLabel("Compile Files:");
		gbc.gridx = 0;
		gbc.gridy = 10;
		panel1.add(compile_Label, gbc);

		compile_TextField = new JTextField("");
		gbc.gridx = 1;
		gbc.gridy = 10;
		gbc.gridwidth = 1;
		panel1.add(compile_TextField, gbc);







		//////////////////////////////////////////////////////////////////////////////////
		////// Button, TextField and actionlistener for the number of executed Files /////
		//////////////////////////////////////////////////////////////////////////////////


		execute_Label = new JLabel("Execute Files:");
		gbc.gridx = 0;
		gbc.gridy = 11;
		panel1.add(execute_Label, gbc);

		execute_TextField = new JTextField("");
		gbc.gridx = 1;
		gbc.gridy = 11;
		gbc.gridwidth = 1;
		panel1.add(execute_TextField, gbc);

		execute_ProgressBar = new JProgressBar(0,100);
		gbc.gridx = 1;
		gbc.gridy = 12;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(execute_ProgressBar, gbc);



		///////////////////////////////////////////////////////////////////////////
		////// Button and action listener for plotting graph //////////////////////
		///////////////////////////////////////////////////////////////////////////


		plot1_Button = new JButton("Draw Fault Domain");
		gbc.gridx = 1;
		gbc.gridy = 13;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(plot1_Button, gbc);

		plot1_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try{


					// The runTest method executes YETi 

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
		gbc.gridy = 14;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(emptyLabel1, gbc);

		JButton help_Button = new JButton("Help");
		gbc.gridx = 0;
		gbc.gridy = 16;
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
		gbc.gridy = 16;
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
		//	JOptionPane.showMessageDialog(null, strategy, " strategy is ",JOptionPane.PLAIN_MESSAGE);
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
		YetiADFDPlusStrategy.rangeToPlot = rangeValue_TextField.getText();
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


		//JOptionPane.showMessageDialog(null,  " Graph is");

		/* I added this if statement to control the graphs
		 * so if we get one fault then one C file will be generated and
		 * thus one argument graph will be create 
		 * For simplicity we are doing it as a separate class
		 * Later we will try to do it using one class with different methods or 
		 * method overloading kind of.
		 */

		if (countCompileFiles == 1){
			LogGrapher2 demo = new LogGrapher2("Failure Domains");

		}
		else if (countCompileFiles == 2){
			LogGrapher2 demo = new LogGrapher2("Failure Domains");

		}
		else {
			LogGrapher3 demo = new LogGrapher3("Failure Domains");

		}
		ADFDPlus.panel3.validate();
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
				pro1 = Runtime.getRuntime().exec("javac -g " + testFilePathInitial + filesToCompileArray[i]);
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
			progressStart();
			//JOptionPane.showMessageDialog(null, testFilePathInitial, "Thread 2 Joining", JOptionPane.CANCEL_OPTION);
			//thread2.join();
			//JOptionPane.showMessageDialog(null, testFilePathInitial, "Thread 3 Starting", JOptionPane.CANCEL_OPTION);
			executeJavaFiles();
			//JOptionPane.showMessageDialog(null, testFilePathInitial, "Thread 3 joining", JOptionPane.CANCEL_OPTION);
			//thread3.join();
			String result = executeDaikon();

			panel5.setText("Candidate invariant:"+result);
			//JOptionPane.showMessageDialog(null, testFilePathInitial, "Thread 4 Starting", JOptionPane.CANCEL_OPTION);
			progressStop();
			//JOptionPane.showMessageDialog(null, testFilePathInitial, "Thread 4 joining", JOptionPane.CANCEL_OPTION);
			//thread4.join();



		}


		catch(Exception e1){
			e1.printStackTrace();
		}

	}
	//////////////////////////////////////////



	

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
	 * 	Thread 4 to update the status of progress bar when pass and fail files are generated 
	 */
	public void progressStop(){


		File file = new File(testFilePathInitial + "Pass.txt");
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

	//////Thread 1 to execute YETI for finding faults and generating CX.java files, where X is int variable ///////////////
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





