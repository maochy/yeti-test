package yeti;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import org.jfree.chart.renderer.xy.XYItemRenderer;

import java.awt.EventQueue;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import yeti.GraphDataScannerForADFDPlus;

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
 * This class generate the graph in the case of two argument method failure which is displayed in panel3 of the ADFD+ GUI. 
 * 
 * @author Mian Asbat Ahmad (mian.ahmad@york.ac.uk)
 * @date   10 Apr 2014
 *
 */
public class ADFDAndADFDPlusGraphGenerator {

	/**
	 * The variable to stop the loop in the case of failing values.
	 */
	public int loopHold;
	/**
	 * The constructor specifying the chart area and other settings.
	 *
	 * @param title of the chart.
	 */
	public ADFDAndADFDPlusGraphGenerator (String title) {
		final XYDataset dataset = createDataset();
		final JFreeChart chart = createChart(dataset);
		ChartPanel chartPanel = new ChartPanel(chart, false);
		chartPanel.setPreferredSize(new Dimension(900, 700));
		chartPanel.setVisible(true);
		ADFDLauncher.panel3.add(chartPanel);
	}


	/**
	 * This method get the failing and passing values
	 * Convert them to double type and assign them to two series i.e series1 for failing and series2 for passing.
	 *  
	 * @return the dataset containing the graphs data.
	 */
	private XYDataset createDataset() {
		//int a = 1000;


		final XYSeriesCollection dataset = new XYSeriesCollection();
		//JOptionPane.showMessageDialog(null, a, "the file f", JOptionPane.CANCEL_OPTION);

		int failValues[] = GraphDataScannerForADFDPlus.readFailDataFromFile();
		int passValues[] = GraphDataScannerForADFDPlus.readPassDataFromFile();	

		final XYSeries series1 = new XYSeries("Failing input");
		final XYSeries series2 = new XYSeries("Passing input");

		for (int j =0; j < passValues.length; j=j+2){
			System.out.println("first add pass X " + passValues[j] + ", " + passValues[j+1]);
			series2.add((double)passValues[j], (double)passValues[j+1]);
		}

		loopHold = failValues.length;
		loopHold = loopHold - 1;

		for (int i =0; i < loopHold; i=i+2 ){
			System.out.println("first add fail X " + failValues[i] + ", " + failValues[i+1]);
			series1.add((double)failValues[i],(double)failValues[i+1]);
		}

		dataset.addSeries(series1);
		dataset.addSeries(series2);

		return dataset;

	}



	/**
	 * Create a chart.
	 *
	 * @param dataset the dataset
	 * @return the chart
	 */
	private JFreeChart createChart(XYDataset dataset) {

		// create the chart...
		JFreeChart chart = ChartFactory.createScatterPlot(
				"Pass and Fail Values for the SUT", // chart title
				"X", // domain axis label
				"Y", // range axis label
				dataset,  // initial series
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				true, // tooltips?
				false // URLs?
				);

		// set chart background
		chart.setBackgroundPaint(Color.white);

		// set a few custom plot features
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(new Color(0xffffe0));
		plot.setDomainGridlinesVisible(true);
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);

		// set the plot's axes to display integers
		TickUnitSource ticks = NumberAxis.createIntegerTickUnits();
		NumberAxis domain = (NumberAxis) plot.getDomainAxis();
		domain.setStandardTickUnits(ticks);
		NumberAxis range = (NumberAxis) plot.getRangeAxis();
		range.setStandardTickUnits(ticks);

		// render shapes and lines
		XYItemRenderer renderer = plot.getRenderer();
		plot.setRenderer(renderer);
		//		renderer.setBaseShapesVisible(true);
		//		renderer.setBaseShapesFilled(true);

		// This line is added by Mian to change the colour of series 3 (2 here) from green to red.
		renderer.setSeriesPaint(2, Color.red);


		// set the renderer's stroke
		Stroke stroke = new BasicStroke(
				3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
		renderer.setBaseOutlineStroke(stroke);

		// label the points
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(2);
		
		
//		
//		// Comment the immidiate first below line and uncomment the following second line if you need labelless chart and vice versa.	
//		if (ADFDLauncher.labeledChart == true){
//			JOptionPane.showMessageDialog(null, ADFDLauncher.labeledChart, "true = ADFDLauncher.labeledChart", JOptionPane.CANCEL_OPTION);
//			generator = new StandardXYItemLabelGenerator( "({1}, {2})", new DecimalFormat("0"),  new DecimalFormat("0") );
//		}
//		else{
//			JOptionPane.showMessageDialog(null, ADFDLauncher.labeledChart, "false = ADFDLauncher.labeledChart", JOptionPane.CANCEL_OPTION);
//			generator = new StandardXYItemLabelGenerator( "", new DecimalFormat("0"),  new DecimalFormat("0") );
//
//		}

//		// Comment the immidiate first below line and uncomment the following second line if you need labelless chart and vice versa.		
//		XYItemLabelGenerator generator = new StandardXYItemLabelGenerator( "({1}, {2})", new DecimalFormat("0"),  new DecimalFormat("0") );
		XYItemLabelGenerator generator = new StandardXYItemLabelGenerator( "", new DecimalFormat("0"),  new DecimalFormat("0") );
		renderer.setBaseItemLabelGenerator(generator);
		renderer.setBaseItemLabelsVisible(true);
		return chart;

	}

}



