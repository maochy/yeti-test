package yeti;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.util.*;
import javax.swing.JPanel;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.ShapeUtilities;

public class ScatterGraph extends ApplicationFrame {

    public ScatterGraph(String s) {
        super(s);
        JPanel jpanel = createDemoPanel();
        jpanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(jpanel);
    }

    public static JPanel createDemoPanel() {

        JFreeChart jfreechart = ChartFactory.createScatterPlot("Scatter Plot Demo",
            "X", "Y", samplexydataset2(), PlotOrientation.VERTICAL, true, true, false);
        Shape cross = ShapeUtilities.createDiagonalCross(3, 1);

        XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
        XYItemRenderer renderer = xyPlot.getRenderer();
        renderer.setBaseShape(cross);
        renderer.setBasePaint(Color.red);
        //changing the Renderer to XYDotRenderer
        //xyPlot.setRenderer(new XYDotRenderer());
        XYDotRenderer xydotrenderer = new XYDotRenderer();
        xyPlot.setRenderer(xydotrenderer);
        xydotrenderer.setSeriesShape(0, cross);

        xyPlot.setDomainCrosshairVisible(true);
        xyPlot.setRangeCrosshairVisible(true);

        return new ChartPanel(jfreechart);
    }

    private static XYDataset samplexydataset2() {
        int cols = 20;
        int rows = 20;
        double[][] values = new double[cols][rows];

        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
//        XYSeries series1 = new XYSeries("Random");
//        XYSeries series2 = new XYSeries("Random1");
        
        
	int failValues[] = GraphDataScanner.readFailDataFromFile();
	int passValues[] = GraphDataScanner.readPassDataFromFile();	
	
	final XYSeries series1 = new XYSeries("Failing input");
	final XYSeries series2 = new XYSeries("Passing input");

	for (int j =0; j < passValues.length; j=j+2){
		
		series2.add((double)passValues[j], (double)passValues[j+1]);
		
	}


	for (int i =0; i < failValues.length; i=i+2 ){
		series1.add((double)failValues[i],(double)failValues[i+1]);
	
	}

        
        
//        Random rand = new Random();
//        for (int i = 0; i < values.length; i++) {
//            for (int j = 0; j < values[i].length; j++) {
//                double x = Math.round(rand.nextDouble() * 500);
//                double y = Math.round(rand.nextDouble() * 500);
//
//                series.add(x, y);
//            }
//        }
//        
//        Random rand1 = new Random();
//        for (int i = 0; i < values.length; i++) {
//            for (int j = 0; j < values[i].length; j++) {
//                double x = Math.round(rand.nextDouble() * 500);
//                double y = Math.round(rand.nextDouble() * 500);
//
//                series1.add(x, y);
//            }
//        }
        
        
        xySeriesCollection.addSeries(series1);
        xySeriesCollection.addSeries(series2);
        return xySeriesCollection;
    }

//    public static void main(String args[]) {
//        ScatterGraph scatterplotdemo4 = new ScatterGraph("Scatter Plot Demo 4");
//        scatterplotdemo4.pack();
//        RefineryUtilities.centerFrameOnScreen(scatterplotdemo4);
//        scatterplotdemo4.setVisible(true);
//    }
}

//
//
///**
// * JFreeChartDemo
// *
// * This program is free software; you can redistribute it and/or
// * modify it under the terms of the GNU Lesser General Public
// * License as published by the Free Software Foundation; either
// * version 2.1 of the License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// * Lesser General Public License for more details.
// *
// * More than 150 demo applications are included with the JFreeChart
// * Developer Guide. For more information, see:
// *
// * JFreeChart Developer's Guide
// */
//import java.awt.BasicStroke;
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.EventQueue;
//import java.awt.Stroke;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.text.DecimalFormat;
//import java.text.NumberFormat;
//import java.util.Random;
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.axis.NumberAxis;
//import org.jfree.chart.axis.TickUnitSource;
//import org.jfree.chart.labels.StandardXYItemLabelGenerator;
//import org.jfree.chart.labels.XYItemLabelGenerator;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.chart.plot.XYPlot;
//import org.jfree.chart.renderer.xy.DeviationRenderer;
//import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
//import org.jfree.data.xy.DefaultXYDataset;
//import org.jfree.data.xy.XYDataset;
//import org.jfree.data.xy.XYSeries;
//import org.jfree.data.xy.XYSeriesCollection;
//import org.jfree.ui.RefineryUtilities;
//import yeti.GraphDataScanner;
//
///**
// * @author John B. Matthews
// */
//public class ScatterGraph {
//
//	
//	public int loopHold;
//	/**
//	 * Construct a new frame
//	 *
//	 * @param title the frame title
//	 */
//	public ScatterGraph (String title) {
//
//
//		final XYDataset dataset = createDataset();
//		
//		final JFreeChart chart = createChart(dataset);
//		
//
//		
//		ChartPanel chartPanel = new ChartPanel(chart, false);
//
//		chartPanel.setPreferredSize(new Dimension(800, 600));
//	//ADFDLauncher.panel3.add(chartPanel);
//		ADFDPlus.panel3.add(chartPanel);
//	}
//
//	// This is the original one and the following same name method is the one i do mess.
////
////private XYDataset createDataset() {
////		
////		
////		final XYSeriesCollection dataset = new XYSeriesCollection();
////		
////		int failValues[] = GraphDataScanner.readFailDataFromFile();
////		int passValues[] = GraphDataScanner.readPassDataFromFile();	
////		
////		final XYSeries series1 = new XYSeries("Failing input");
////		final XYSeries series2 = new XYSeries("Passing input");
////
////		for (int j =0; j < passValues.length; j=j+4){
////			
////			series2.add((double)passValues[j+1], (double)passValues[j]);
////			series2.add((double)passValues[j+3], (double)passValues[j+2]);
////			series2.add((double)passValues[j+2],null);
////			series2.add((double)passValues[j+3],null);
////			
////			System.out.println("added pass: "+passValues[j]+"->"+passValues[j+1]);
////		}
////
////
////		for (int i =0; i < failValues.length; i=i+8 ){
////			series1.add((double)failValues[i],(double)failValues[i+1]);
////			series1.add((double)failValues[i+2],(double)failValues[i+3]);
////			series1.add((double)failValues[i+2],null);
////			//series1.add((double)failValues[i+3],null);
////			series1.add((double)failValues[i+5], (double)failValues[i+4]);
////			series1.add((double)failValues[i+7], (double)failValues[i+6]);
////			
////			System.out.println("added fail: "+failValues[i]+"->"+failValues[i+1]);
////		}
////
////		dataset.addSeries(series1);
////		dataset.addSeries(series2);
////
////		return dataset;
////
////	}
//
//
//private XYDataset createDataset() {
//		
//		
//		final XYSeriesCollection dataset = new XYSeriesCollection();
//		
//		int failValues[] = GraphDataScanner.readFailDataFromFile();
//		int passValues[] = GraphDataScanner.readPassDataFromFile();	
//		
//		final XYSeries series1 = new XYSeries("Failing input");
//		final XYSeries series2 = new XYSeries("Passing input");
//
//		for (int j =0; j < passValues.length; j=j+2){
//			
//			series2.add((double)passValues[j], (double)passValues[j+1]);
//		}
//
//		
//		for (int i =0; i < failValues.length; i=i+2 ){
//			series1.add((double)failValues[i],(double)failValues[i+1]);
//
//		}
//		
//		dataset.addSeries(series1);
//		dataset.addSeries(series2);
//		
//		return dataset;
//
//	}
//	
//
//	/**
//	 * Create a chart.
//	 *
//	 * @param dataset the dataset
//	 * @return the chart
//	 */
//	private JFreeChart createChart(XYDataset dataset) {
//
//		// create the chart...
//		JFreeChart chart = ChartFactory.createScatterPlot(
//				"Serial Data", // chart title
//				"Domain", // domain axis label
//				"Range", // range axis label
//				dataset,  // initial series
//				PlotOrientation.VERTICAL, // orientation
//				true, // include legend
//				true, // tooltips?
//				false // URLs?
//				);
//
//		// set chart background
//		chart.setBackgroundPaint(Color.white);
//
//		// set a few custom plot features
//		XYPlot plot = (XYPlot) chart.getPlot();
//		plot.setBackgroundPaint(new Color(0xffffe0));
//		plot.setDomainGridlinesVisible(true);
//		plot.setDomainGridlinePaint(Color.lightGray);
//		plot.setRangeGridlinePaint(Color.lightGray);
//
//		// set the plot's axes to display integers
//		TickUnitSource ticks = NumberAxis.createIntegerTickUnits();
//		NumberAxis domain = (NumberAxis) plot.getDomainAxis();
//		domain.setStandardTickUnits(ticks);
//		NumberAxis range = (NumberAxis) plot.getRangeAxis();
//		range.setStandardTickUnits(ticks);
//
//		// render shapes and lines
//		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
//		plot.setRenderer(renderer);
//		renderer.setBaseShapesVisible(true);
//		renderer.setBaseShapesFilled(true);
//		
//		// This line is added by Mian to change the colour of series 3 (2 here) from green to red.
//		renderer.setSeriesPaint(2, Color.red);
//		
//
//		// set the renderer's stroke
//		Stroke stroke = new BasicStroke(
//				3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
//		renderer.setBaseOutlineStroke(stroke);
//
//		// label the points
//		NumberFormat format = NumberFormat.getNumberInstance();
//		format.setMaximumFractionDigits(2);
//
//		//        XYItemLabelGenerator generator = new StandardXYItemLabelGenerator( StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT, format, format);
//		//        XYItemLabelGenerator generator = new StandardXYItemLabelGenerator( "{1}; {2}", new DecimalFormat("0.0"),  new DecimalFormat("0.0") );
//		XYItemLabelGenerator generator = new StandardXYItemLabelGenerator( "{1}, {2}", new DecimalFormat("0"),  new DecimalFormat("0") );
//		renderer.setBaseItemLabelGenerator(generator);
//		renderer.setBaseItemLabelsVisible(true);
//
//		return chart;
//	}
//
//	/** Main method */
//	//    public static void main(String[] args) {
//	//        EventQueue.invokeLater(new Runnable() {
//	//            public void run() {
//	//                LogGraph2 demo = new LogGraph2("JFreeChartDemo");
//	//                demo.pack();
//	//                demo.setLocationRelativeTo(null);
//	//                demo.setVisible(true);
//	//            }
//	//         });
//	//    }
//}
//
//
//
