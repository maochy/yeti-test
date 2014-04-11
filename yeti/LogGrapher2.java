package yeti;


/**
 * JFreeChartDemo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * More than 150 demo applications are included with the JFreeChart
 * Developer Guide. For more information, see:
 *
 * JFreeChart Developer's Guide
 */
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
import yeti.GraphDataScanner;

/**
 * @author John B. Matthews
 */
public class LogGrapher2 {


	public int loopHold;
	/**
	 * Construct a new frame
	 *
	 * @param title the frame title
	 */
	public LogGrapher2 (String title) {
		final XYDataset dataset = createDataset();
		final JFreeChart chart = createChart(dataset);



		ChartPanel chartPanel = new ChartPanel(chart, false);
	// I am trying to increase the size of the chart.
	//	chartPanel.setPreferredSize(new Dimension(800, 600));
		chartPanel.setPreferredSize(new Dimension(1200, 800));
		chartPanel.setVisible(true);
		ADFDPlus.panel3.add(chartPanel);
	}

	// This is the original one and the following same name method is the one i do mess.


	private XYDataset createDataset() {


		final XYSeriesCollection dataset = new XYSeriesCollection();

		int failValues[] = GraphDataScanner.readFailDataFromFile();
		int passValues[] = GraphDataScanner.readPassDataFromFile();	

		final XYSeries series1 = new XYSeries("Failing input");
		final XYSeries series2 = new XYSeries("Passing input");

		for (int j =0; j < passValues.length; j=j+2){

			series2.add((double)passValues[j], (double)passValues[j+1]);

		}

		loopHold = failValues.length;
		loopHold = loopHold - 1;

		for (int i =0; i < loopHold; i=i+2 ){
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

// Comment the immidiate first below line and uncomment the following second line if you need labelless chart and vice versa.		
		XYItemLabelGenerator generator = new StandardXYItemLabelGenerator( "({1}, {2})", new DecimalFormat("0"),  new DecimalFormat("0") );
//		XYItemLabelGenerator generator = new StandardXYItemLabelGenerator( "", new DecimalFormat("0"),  new DecimalFormat("0") );

		renderer.setBaseItemLabelGenerator(generator);
		renderer.setBaseItemLabelsVisible(true);

		return chart;
	}

}



