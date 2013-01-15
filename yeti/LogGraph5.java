package yeti;


import java.awt.Color;
import java.awt.Dimension;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a line chart using data from an
 * {@link XYDataset}.
 *
 */
public class LogGraph5 extends ApplicationFrame {

	/**
	 * Creates a new demo.
	 *
	 * @param title  the frame title.
	 */
	public LogGraph5(final String title) {

		super(title);

		final XYDataset dataset = createDataset();
		final JFreeChart chart = createChart(dataset);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(800, 600));
		YetiLauncher.panel3.add(chartPanel);

	}

	/**
	 * Creates a sample dataset.
	 * 
	 * @return a sample dataset.
	 */
	private XYDataset createDataset() {




		int failValues[] = GraphDataScanner.readFailDataFromFile();
		
		final XYSeriesCollection dataset = new XYSeriesCollection();

		final XYSeries series1 = new XYSeries("Failing input");

		for (int i =0; i < failValues.length; i=i+2 ){
			series1.add((double)failValues[i],0);
			series1.add((double)failValues[i+1],0);
			dataset.addSeries(series1);
			System.out.println("added fail: "+failValues[i]+"->"+failValues[i+1]);
		}

		final XYSeries series2 = new XYSeries("Passing input");
		
		int passValues[] = GraphDataScanner.readPassDataFromFile();
		
		for (int j =0; j < passValues.length; j=j+2){
			series2.add((double)passValues[j],0);
			series2.add((double)passValues[j+1],0);
			dataset.addSeries(series2);
			System.out.println("added pass: "+passValues[j]+"->"+passValues[j+1]);
		}


		dataset.addSeries(series1);
		dataset.addSeries(series2);


		return dataset;

	}

	/**
	 * Creates a chart.
	 * 
	 * @param dataset  the data for the chart.
	 * 
	 * @return a chart.
	 */
	private JFreeChart createChart(final XYDataset dataset) {

		// create the chart...
		final JFreeChart chart = ChartFactory.createXYLineChart(
				"Line Chart Demo 6",      // chart title
				"X",                      // x axis label
				"Y",                      // y axis label
				dataset,                  // data
				PlotOrientation.VERTICAL,
				true,                     // include legend
				true,                     // tooltips
				false                     // urls
				);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		chart.setBackgroundPaint(Color.white);

		//        final StandardLegend legend = (StandardLegend) chart.getLegend();
		//      legend.setDisplaySeriesShapes(true);

		// get a reference to the plot for further customisation...
		final XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.lightGray);
		//    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(0, false);
		renderer.setSeriesShapesVisible(1, false);
		plot.setRenderer(renderer);

		// change the auto tick unit selection to integer units only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		// OPTIONAL CUSTOMISATION COMPLETED.

		return chart;

	}

}
