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
import java.io.File;
import java.util.ArrayList;
import org.jfree.ui.RefineryUtilities;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import yeti.LogGraph2;


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



public class YetiLauncher extends JFrame{

	private JPanel panel1 = new JPanel();
	private JPanel panel2 = new JPanel();
	private JPanel panel3 = new JPanel();
	private JPanel panel4 = new JPanel(new BorderLayout());

	JTextField 		generated_TextField;
	JTextField		compile_TextField;
	JTextField 		execute_TextField;

	public static String testFilePathInitial = ".";

	String 		testFilePathFinal 	 = "-yetiPath=.";
	String 		fileName			 = "-testModules=yeti.test.YetiTest"; 
	String 		language 			 = "-java";
	String 		strategy 			 = "-ADFD";
	String 		gui 				 = "-gui";
	String 		logs 				 = "-nologs";
	String 		time 				 = "-time=5s";

	String[] languages = {"Java", ".Net", "JML", "Pharo" };
	String[] strategies = {"ADFD", "DSSR", "Random", "RandomPlus" };
	String[] time1 = {"5", "10", "15", "20", "30", "40", "50", "60", "70", "80", "90", "100" };
	String[] time2 = {"Seconds", "Minutes" };
	String[] command;
	String[] filesToCompileArray;

	ArrayList<String> 	list 			 = new ArrayList<String>();
	ArrayList<String> 	filesToCompile	 = new ArrayList<String>();

	Thread thread1 = new Thread(new Thread1());


	// Constructor of the class to create frame and draw components on the frame. //////

	public YetiLauncher(){

		this.setTitle("Yeti Launcher");
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = new Dimension();
		dim = tk.getScreenSize();
		int xPos = dim.width;
		int yPos = dim.height;
		this.setSize(xPos, yPos);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		drawAllComponents();
		panel4.setBackground(Color.LIGHT_GRAY);
		panel4.add(panel1, BorderLayout.NORTH);
		this.add(panel4, BorderLayout.WEST);
		this.add(panel2, BorderLayout.NORTH);
		this.add(panel3, BorderLayout.CENTER); 
		this.setVisible(true);
		//this.pack();


	}

	// Main method of the class.

	public static void main(String[] args){

		new YetiLauncher();






	}
	
	

	// Method draw all the components on the frame. 

	public void drawAllComponents(){

		GridBagConstraints gbc = new GridBagConstraints();
		panel1.setBorder(new TitledBorder("Test Settings"));
		panel1.setLayout(new GridBagLayout());
		panel1.setBackground(Color.LIGHT_GRAY);
		gbc.insets = new Insets(2, 2, 8, 2);
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.BOTH;


		/////////// Language Label, ComboBox and ActionListener //////////

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
				}
				else
					language = "-pharo";

			}
		});
		//////////////////////////////////////////////////////////////////


		/////////// Strategy Label, ComboBox and ActionListener //////////

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
				}else if (strategy_ComboBox.getSelectedItem().equals("Random Plus")){
					strategy = "-randomPlus";
				} else if (strategy_ComboBox.getSelectedItem().equals("DSSR")){
					strategy = "-DSSR";
				}else
					strategy = "-ADFD";

			}
		});

		//////////////////////////////////////////////////////////////////

		///////// Time label, Time1 Combo Box and Time2 Combo Box ////////


		JLabel duration_Label = new JLabel("Duration:");
		gbc.gridx = 0;
		gbc.gridy = 2;
		panel1.add(duration_Label, gbc);

		final JComboBox time1_ComboBox = new JComboBox(time1);
		gbc.gridx = 1;
		gbc.gridy = 2;
		panel1.add(time1_ComboBox, gbc);

		final JComboBox time2_ComboBox = new JComboBox(time2);
		gbc.gridx = 2;
		gbc.gridy = 2;
		panel1.add(time2_ComboBox, gbc);

		///////////////////////////////////////////////////////////////


		/////////// GUI Label, check box and ActionListener //////////

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

		///////////////////////////////////////////////////////////////
		/////////// Logs Label, check box and ActionListener //////////

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
				JFileChooser chooser = new JFileChooser();
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
		/////////// Run Label, Button and ActionListener /////////////////

		JButton run_Button = new JButton("Run Test");
		gbc.gridx = 1;
		gbc.gridy = 6;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(run_Button, gbc);

		run_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				time = "-time=" + time1_ComboBox.getSelectedItem().toString();
				if(time2_ComboBox.getSelectedItem().equals("Minutes")){
					time = time + "mn";
				} else {
					time = time + "s";
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
				list.add(gui);
				list.add(logs);
				list.add(fileName);
				list.add(testFilePathFinal);




				command = list.toArray(new String[list.size()]);



				try{
					thread1.start();
				}
				catch(Exception e1){
					e1.printStackTrace();
				}
			}
		});



		//////////////////////////////////////////////////////////////////////////////
		////// Button, TextField and action listener for the number of generated Files //////

		JButton generated_Button = new JButton("Count Files:");
		gbc.gridx = 1;
		gbc.gridy = 7;
		gbc.gridwidth = 1;
		panel1.add(generated_Button, gbc);

		generated_TextField = new JTextField("");
		gbc.gridx = 2;
		gbc.gridy = 7;
		gbc.gridwidth = 1;
		panel1.add(generated_TextField, gbc);

		generated_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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

							}

						}
					}


					filesToCompileArray = filesToCompile.toArray(new String[filesToCompile.size()]);

					generated_TextField.setText(filesToCompileArray.length + " files generated") ;

				}
				catch(Exception e1){
					e1.printStackTrace();
				}
			}
		});


		///////////////////////////////////////////////////////////////////////////
		////// Button, TextField and action listener for the number of compiled Files //////


		JButton compile_Button = new JButton("Compile Files:");
		gbc.gridx = 1;
		gbc.gridy = 8;
		panel1.add(compile_Button, gbc);

		compile_TextField = new JTextField("");
		gbc.gridx = 2;
		gbc.gridy = 8;
		gbc.gridwidth = 1;
		panel1.add(compile_TextField, gbc);

		compile_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					int count = 0;
					for (int i = 0; i < filesToCompileArray.length; i++){
						Process pro1;
						pro1 = Runtime.getRuntime().exec("javac " + testFilePathInitial + filesToCompileArray[i]);
						count = count + 1;
					}
					compile_TextField.setText(count + " files compiled");
				}

				catch(Exception e1){
					e1.printStackTrace();
				}

			}
		});



		/////////////////////////////////////////////////////////////////////////////////
		////// Button, TextField and actionlistener for the number of executed Files /////


		JButton execute_Button = new JButton("Execute Files:");
		gbc.gridx = 1;
		gbc.gridy = 9;
		panel1.add(execute_Button, gbc);

		execute_TextField = new JTextField("");
		gbc.gridx = 2;
		gbc.gridy = 9;
		gbc.gridwidth = 1;
		panel1.add(execute_TextField, gbc);

		execute_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					int count = 0;
					for (int i = 0; i < filesToCompileArray.length; i++){
						Runtime.getRuntime().exec("java C"+ i);
						count++;
					}

					execute_TextField.setText(count + " files executed");
				}


				catch(Exception e1){
					e1.printStackTrace();
				}

			}

		});



		///////////////////////////////////////////////////////////////////////////
		////// Button and actionlistener for plotting graph /////

		JButton plot_Button = new JButton("Draw Fault Domain");
		gbc.gridx = 1;
		gbc.gridy = 10;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(plot_Button, gbc);

		plot_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{

					//				GraphingData gd = new GraphingData();
					//				gd.draw();

					//				GraphDataScanner Gdr = new GraphDataScanner();
					//				Gdr.readFailDataFromFile();
					//				Gdr.readPassDataFromFile();
					//				 final LogGraph demo = new LogGraph("Failing and Passing values");
					//				 demo.pack();
					//				 RefineryUtilities.centerFrameOnScreen(demo);
					//				 demo.setVisible(true);
					//%%%%%%%%%  code for graph2 test %%%%%%%%%%%%%


					final LogGraph2 demo = new LogGraph2("Failure Domains");
					demo.pack();
					RefineryUtilities.centerFrameOnScreen(demo);
					demo.setVisible(true);


//					LogGraph2 demo = new LogGraph2("FailureDomains");
//					demo.pack();
//					demo.setLocationRelativeTo(null);
//					demo.setVisible(true);
//					JOptionPane.showMessageDialog(null, "working", " Mian", JOptionPane.PLAIN_MESSAGE);
				}
				catch(Exception e1){
					e1.printStackTrace();
				}

			}

		});


		JLabel heading_Label = new JLabel("Automated Discovery of Failure Domain, based on YETI");
		heading_Label.setFont(heading_Label.getFont().deriveFont(32.0f ));
		panel2.add(heading_Label);
		panel2.setBackground(Color.LIGHT_GRAY);

		panel3.setBorder(new TitledBorder("Plot Fault Domain "));
		panel3.setBackground(Color.LIGHT_GRAY);


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
}





