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
public class LogGrapher1 {


	/**
	 * Construct a new frame
	 *
	 * @param title the frame title
	 */
	public LogGrapher1 (String title) {


		final XYDataset dataset = createDataset();
		
		final JFreeChart chart = createChart(dataset);
		
		//        final DefaultXYDataset dataset = new DefaultXYDataset();
		//        dataset.addSeries("Series0", createSeries(0));
		//        dataset.addSeries("Series1", createSeries(1));
		//        JFreeChart chart = createChart(dataset);
		ChartPanel chartPanel = new ChartPanel(chart, false);

		chartPanel.setPreferredSize(new Dimension(800, 600));
		YetiLauncher.panel3.add(chartPanel);
		//this.add(chartPanel, BorderLayout.CENTER);

		//        JPanel buttonPanel = new JPanel();
		//        JButton addButton = new JButton("Add Series");
		//        buttonPanel.add(addButton);
		//        addButton.addActionListener(new ActionListener() {
		//            public void actionPerformed(ActionEvent e) {
		//                int n = dataset.getSeriesCount();
		//                dataset.addSeries("Series" + n, createSeries(n));
		//            }
		//        });
		//        JButton remButton = new JButton("Remove Series");
		//        buttonPanel.add(remButton);
		//        remButton.addActionListener(new ActionListener() {
		//            public void actionPerformed(ActionEvent e) {
		//                int n = dataset.getSeriesCount() - 1;
		//                dataset.removeSeries("Series" + n);
		//            }
		//        });
		//        this.add(buttonPanel, BorderLayout.SOUTH);
	}


private XYDataset createDataset() {
		
		
		final XYSeriesCollection dataset = new XYSeriesCollection();
		
		int failValues[] = GraphDataScanner.readFailDataFromFile();

		final XYSeries series1 = new XYSeries("Failing input");

		for (int i =0; i < failValues.length; i=i+2 ){
			series1.add((double)failValues[i], 0);
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

	//    /**
	//     * Create a series
	//     *
	//     * @ return the series
	//     */
	//    private double[][] createSeries(int mean) {
	//        double[][] series = new double[2][MAX];
	//        for (int i = 0; i < MAX; i++) {
	//            series[0][i] = (double) i;
	//            //series[1][i] = mean + random.nextGaussian() / 2;
	//            
	//        }
	//        return series;
	//    }

	/**
	 * Create a chart.
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

		// XYItemLabelGenerator generator = new StandardXYItemLabelGenerator( StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT, format, format);
		// XYItemLabelGenerator generator = new StandardXYItemLabelGenerator( "{1}; {2}", new DecimalFormat("0.0"),  new DecimalFormat("0.0") );
		
		//XYItemLabelGenerator generator = new StandardXYItemLabelGenerator( "{1}, {2}", new DecimalFormat("0"),  new DecimalFormat("0") );
		// modified by mian to see only one labels
		
		XYItemLabelGenerator generator = new StandardXYItemLabelGenerator( "{1}", new DecimalFormat("0"),  new DecimalFormat("0") );
		
		renderer.setBaseItemLabelGenerator(generator);
		renderer.setBaseItemLabelsVisible(true);

		return chart;
	}

	/** Main method */
	//    public static void main(String[] args) {
	//        EventQueue.invokeLater(new Runnable() {
	//            public void run() {
	//                LogGraph2 demo = new LogGraph2("JFreeChartDemo");
	//                demo.pack();
	//                demo.setLocationRelativeTo(null);
	//                demo.setVisible(true);
	//            }
	//         });
	//    }
}



