package yeti;


import java.awt.BasicStroke;
import java.awt.BorderLayout;
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


public class ADFDWideGraphGeneratorFor2Arg {


	/**
	 * Construct a new frame
	 *
	 * @param title the frame title
	 */
	public ADFDWideGraphGeneratorFor2Arg(String title) {


		final XYDataset dataset = createDataset();

		final JFreeChart chart = createChart(dataset);


		ChartPanel chartPanel = new ChartPanel(chart, false);

		chartPanel.setPreferredSize(new Dimension(800, 600));
		ADFDLauncher.panel3.add(chartPanel);
	}


	private XYDataset createDataset() {


		final XYSeriesCollection dataset = new XYSeriesCollection();

		int failValuesX[] = GraphDataScannerForADFD.readFailDataFromFileX();
		int failValuesY[] = GraphDataScannerForADFD.readFailDataFromFileY();

		final XYSeries series1 = new XYSeries("Failing input");

		for (int i =0; i < failValuesX.length; i=i+4 ){
			series1.add((double)failValuesX[i],(double)failValuesX[i+1]);
			series1.add((double)failValuesX[i+2],(double)failValuesX[i+3]);
			series1.add((double)failValuesX[i+2],null);
			series1.add((double)failValuesX[i+3],null);	
			//				System.out.println("added fail: "+failValuesX[i]+"->"+failValuesX[i+1]);
		}

		for (int i =0; i < failValuesY.length; i=i+4 ){
			series1.add((double)failValuesY[i],(double)failValuesY[i+1]);
			series1.add((double)failValuesY[i+2],(double)failValuesY[i+3]);
			series1.add((double)failValuesY[i+2],null);
			series1.add((double)failValuesY[i+3],null);
			//				System.out.println("added fail: "+failValuesY[i]+"->"+failValuesY[i+1]);
		}

		final XYSeries series2 = new XYSeries("Passing input");

		int passValuesX[] = GraphDataScannerForADFD.readPassDataFromFileX();
		int passValuesY[] = GraphDataScannerForADFD.readPassDataFromFileY();

		for (int j =0; j < passValuesX.length; j=j+4){
			series2.add((double)passValuesX[j],(double)passValuesX[j+1]);
			series2.add((double)passValuesY[j],null); 
			series2.add((double)passValuesX[j+2],(double)passValuesX[j+3]);
			series2.add((double)passValuesX[j+2],null);
			series2.add((double)passValuesX[j+3],null);
			//				System.out.println("added pass: "+passValuesY[j]+"->"+passValuesY[j+1]);
		}

		for (int j =0; j < passValuesY.length; j=j+4){
			series2.add((double)passValuesY[j],(double)passValuesY[j+1]);
			series2.add((double)passValuesY[j+2],(double)passValuesY[j+3]);
			series2.add((double)passValuesY[j+2],null);
			series2.add((double)passValuesY[j+3],null);
			//				System.out.println("added pass: "+passValuesY[j]+"->"+passValuesY[j+1]);
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
		JFreeChart chart = ChartFactory.createXYLineChart(
				"Two Dimensional Program Graph", // chart title
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
		        XYItemLabelGenerator generator = new StandardXYItemLabelGenerator( "{1}, {2}", new DecimalFormat("0"),  new DecimalFormat("0") );
//		XYItemLabelGenerator generator = new StandardXYItemLabelGenerator( "{1}", new DecimalFormat("0"),  new DecimalFormat("0") );
		renderer.setBaseItemLabelGenerator(generator);
		renderer.setBaseItemLabelsVisible(true);

		return chart;
	}

}



