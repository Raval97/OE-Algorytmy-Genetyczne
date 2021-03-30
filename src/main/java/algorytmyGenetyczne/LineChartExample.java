package algorytmyGenetyczne;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class LineChartExample extends JFrame {

    public LineChartExample(String path, String title, String yTittle , XYDataset dataset  ) throws IOException {
        super(title);
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "Epoki",
                yTittle,
                dataset, PlotOrientation.VERTICAL, true, false, false
        );
        Rectangle rect = new Rectangle(1, 1);
        ChartPanel panel = new ChartPanel(chart);
        XYPlot plot1 = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot1.getRenderer();
        renderer.setBaseShapesVisible(true);
        renderer.setSeriesShape(1, rect);
        setContentPane(panel);

        ChartUtilities.saveChartAsPNG(new File(path+"/"+title+".png"), chart, 450, 400);

    }

}
