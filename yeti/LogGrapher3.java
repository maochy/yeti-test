package yeti;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import yeti.GraphDataScanner;

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
public class LogGrapher3 {


	/**
	 * The constructor specifying the chart area and other settings.
	 *
	 * @param title of the chart.
	 */
	public LogGrapher3 (String title) {
		final XYDataset dataset = createDataset();
		final JFreeChart chart = createChart(dataset);
		ChartPanel chartPanel = new ChartPanel(chart, false);
		chartPanel.setPreferredSize(new Dimension(800, 600));
		//ADFDLauncher.panel3.add(chartPanel);
		ADFDLauncher.panel3.add(chartPanel);
		chartPanel.setVisible(true);
	}

	/**
	 * This method get the failing and passing values
	 * Convert them to double type and assign them to two series i.e series1 for failing and series2 for passing.
	 *  
	 * @return the dataset containing the graphs data.
	 */
	private XYDataset createDataset() {


		final XYSeriesCollection dataset = new XYSeriesCollection();
		int failValues[] = GraphDataScanner.readFailDataFromFile();
		final XYSeries series1 = new XYSeries("Failing input");
		for (int i =0; i < failValues.length; i=i+2 ){
			series1.add((double)failValues[i],0);
			series1.add((double)failValues[i+1],0);
			series1.add((double)failValues[i+1],null);
			System.out.println("added fail: "+failValues[i]+"->"+failValues[i+1]);
		}

		final XYSeries series2 = new XYSeries("Passing input");
		int passValues[] = GraphDataScanner.readPassDataFromFile();
		for (int j =0; j < passValues.length; j=j+2){
			series2.add((double)passValues[j],0);
			series2.add((double)passValues[j+1],0);
			series2.add((double)passValues[j+1],null);
			System.out.println("added pass: "+passValues[j]+"->"+passValues[j+1]);
		}

		dataset.addSeries(series1);
		dataset.addSeries(series2);

		return dataset;

	}


	/**
	 * This method set various properties of the chart that is to be displayed in the GUI.
	 *
	 * @param dataset the dataset
	 * @return the chart
	 */
	private JFreeChart createChart(XYDataset dataset) {

		// create the chart...
		JFreeChart chart = ChartFactory.createXYLineChart(
				"Serial Data", // chart title
				"Domain", // domain axis label
				"Range", // range axis label
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
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);

		// set the plot's axes to display integers
		TickUnitSource ticks = NumberAxis.createIntegerTickUnits();
		NumberAxis domain = (NumberAxis) plot.getDomainAxis();
		domain.setStandardTickUnits(ticks);
		NumberAxis range = (NumberAxis) plot.getRangeAxis();
		range.setStandardTickUnits(ticks);

		// render shapes and lines
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
		plot.setRenderer(renderer);
		renderer.setBaseShapesVisible(true);
		renderer.setBaseShapesFilled(true);

		// set the renderer's stroke
		Stroke stroke = new BasicStroke(
				3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
		renderer.setBaseOutlineStroke(stroke);

		// label the points
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(2);

		//        XYItemLabelGenerator generator = new StandardXYItemLabelGenerator( StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT, format, format);
		//        XYItemLabelGenerator generator = new StandardXYItemLabelGenerator( "{1}; {2}", new DecimalFormat("0.0"),  new DecimalFormat("0.0") );
		XYItemLabelGenerator generator = new StandardXYItemLabelGenerator( "{1}, {2}", new DecimalFormat("0"),  new DecimalFormat("0") );
		renderer.setBaseItemLabelGenerator(generator);
		renderer.setBaseItemLabelsVisible(true);

		return chart;
	}

}