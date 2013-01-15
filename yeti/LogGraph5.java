package yeti;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
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
import yeti.GraphDataScanner;


public class LogGraph5 {

	/**
	 * Creates a new demo.
	 *
	 * @param title  the frame title.
	 */
	
	
	public LogGraph5(String title) {

		final XYDataset dataset = createDataset();
		final JFreeChart chart = createChart(dataset);
		
		ChartPanel chartPanel = new ChartPanel(chart, false);
		chartPanel.setPreferredSize(new Dimension(800, 600));
		YetiLauncher.panel3.add(chartPanel);
		//It is for testing purpose only.
		//JOptionPane.showInputDialog(null, "Enter your full name: ");
	}

	/**
	 * Creates a sample dataset.
	 * 
	 * @return a sample dataset.
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
