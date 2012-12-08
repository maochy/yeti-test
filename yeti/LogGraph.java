package yeti;


import java.awt.Color;

import javax.swing.JOptionPane;

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
//import org.jfree.ui.RefineryUtilities;
//import org.jfree.ui.Spacer;
import org.jfree.ui.RefineryUtilities;
import yeti.GraphDataScanner;

/**
 * A simple demonstration application showing how to create a line chart using data from an
 * {@link XYDataset}.
 *
 */
public class LogGraph extends ApplicationFrame {

	/**
	 * Creates a new demo.
	 *
	 * @param title  the frame title.
	 */
	public LogGraph(final String title) {

		super(title);

		final XYDataset dataset = createDataset();
		final JFreeChart chart = createChart(dataset);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);

	}

	/**
	 * Creates a sample dataset.
	 * 
	 * @return a sample dataset.
	 */
	private XYDataset createDataset() {

		final XYSeries series1 = new XYSeries("Failing input");

		//        int [] exampledata = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		//        for (int i =0; i < exampledata.length; i++ )
		//        	series1.add((double)exampledata[i],0);
		JOptionPane.showMessageDialog(null, GraphDataScanner.graphDataIntFail[0]+"", "hello", JOptionPane.INFORMATION_MESSAGE);

		for (int i = 0; i < GraphDataScanner.graphDataIntFail.length; i++ )
		{
			JOptionPane.showMessageDialog(null, GraphDataScanner.graphDataIntFail[i]+"", "hello", JOptionPane.INFORMATION_MESSAGE);

			series1.add((double)GraphDataScanner.graphDataIntFail[i],0);
		}
			//        }
			//        series1.add(1.0, 1.0);
			//        series1.add(2.0, 4.0);
			//        series1.add(3.0, 3.0);
			//        series1.add(4.0, 5.0);
			//        series1.add(5.0, 5.0);
			//        series1.add(6.0, 7.0);
			//        series1.add(7.0, 7.0);
			//        series1.add(8.0, 8.0);

			final XYSeries series2 = new XYSeries("Passing input");

			for (int j =0; j < GraphDataScanner.graphDataIntPass.length; j++ )
				series2.add((double)GraphDataScanner.graphDataIntPass[j],0);

			//        series2.add(1.0, 5.0);
			//        series2.add(2.0, 7.0);
			//        series2.add(3.0, 6.0);
			//        series2.add(4.0, 8.0);
			//        series2.add(5.0, 4.0);
			//        series2.add(6.0, 4.0);
			//        series2.add(7.0, 2.0);
			//        series2.add(8.0, 1.0);

			//        final XYSeries series3 = new XYSeries("Third");
			//        series3.add(3.0, 4.0);
			//        series3.add(4.0, 3.0);
			//        series3.add(5.0, 2.0);
			//        series3.add(6.0, 3.0);
			//        series3.add(7.0, 6.0);
			//        series3.add(8.0, 3.0);
			//        series3.add(9.0, 4.0);
			//        series3.add(10.0, 3.0);

			final XYSeriesCollection dataset = new XYSeriesCollection();
			dataset.addSeries(series1);
			//       dataset.addSeries(series2);
			//        dataset.addSeries(series3);

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
			renderer.setSeriesShapesVisible(0, false);
			renderer.setSeriesShapesVisible(1, false);
			plot.setRenderer(renderer);

			// change the auto tick unit selection to integer units only...
			final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			// OPTIONAL CUSTOMISATION COMPLETED.

			return chart;

		}

		//    
		//    public static void main(String[] args){
		//    	 final LogGraph demo = new LogGraph("LineChartDemo6");
		//	     demo.pack();
		//	     RefineryUtilities.centerFrameOnScreen(demo);
		//	     demo.setVisible(true);
		//    }

	}