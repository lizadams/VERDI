package anl.verdi.plot.types;

import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;		// 2014
import org.apache.logging.log4j.Logger;			// 2014 replacing System.out.println with logger messages
import org.jfree.chart.JFreeChart;

import anl.verdi.plot.config.PlotConfiguration;
import anl.verdi.plot.gui.ConfigDialog;
import anl.verdi.plot.gui.Plot;
import anl.verdi.plot.gui.ThemeDialog;
import anl.verdi.plot.jfree.ChartPanel;
import anl.verdi.plot.util.PlotExporter;
import net.sf.epsgraphics.ColorMode;
import net.sf.epsgraphics.Drawable;
import net.sf.epsgraphics.EpsTools;

/**
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public abstract class AbstractPlot implements Plot, EPSExporter {
	static final Logger Logger = LogManager.getLogger(AbstractPlot.class.getName());

	protected enum ControlAction {
		ZOOM, PROBE
	}

	protected VerdiChartPanel panel;

	/**
	 * Exports an image of this Plot to the specified file in the
	 * specified format.
	 *
	 * @param format the image format. One of PlotExporter.JPG, PlotExporter.TIF,
	 *               PlotExporter.PNG, or PlotExporter.BMP
	 * @param width width of image in pixels
	 * @param height height of image in pixels
	 * @param file   the file to save the image to.
	 */
	public void exportImage(String format, File file, int width, int height) throws IOException {
		Logger.debug("in AbstractPlot.exportImage");
		PlotExporter exporter = new PlotExporter(this);
		exporter.save(format, file, width, height);
	}
	
	/**
	 * Gets a BufferedImage of the plot.
	 *
	 * @return a BufferedImage of the plot.
	 */
	public BufferedImage getBufferedImage() {
		Logger.debug("in AbstractPlot.getBufferedImage()");
		return getBufferedImage(panel.getWidth(), panel.getHeight());
	}

	/**
	 * Gets a BufferedImage of the plot.
	 *
	 * @param width  the width of the image in pixels
	 * @param height the height of the image in pixels
	 * @return a BufferedImage of the plot.
	 */
	public BufferedImage getBufferedImage(int width, int height) {
		Logger.debug("in AbstractPlot.getBufferedImage(width, height)");
		return panel.getChart().createBufferedImage(width, height);
	}



	/**
	 * Configure this Plot according to the specified PlotConfiguration.
	 *
	 * @param config the new plot configuration
	 */
	public void configure(PlotConfiguration config) {
		Logger.debug("in AbstractPlot.configure #1, not yet written");
		//todo implement method
	}
	
	public void configure(PlotConfiguration config, Plot.ConfigSource source) {
		Logger.debug("in AbstractPlot.configure#2, not yet written");
		configure(config);
	}
	
	public void setViewId(String id) {}

	/**
	 * Gets this Plot's configuration data.
	 *
	 * @return this Plot's configuration data.
	 */
	public PlotConfiguration getPlotConfiguration() {
		Logger.debug("in AbstractPlot.getPlotConfiguration(), returning null");
		return null;  //todo implement method
	}

	// customized chart panel for VerdiPlots
	protected class VerdiChartPanel extends ChartPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4069425929226151472L;
		private boolean zoom = false;

		public VerdiChartPanel(JFreeChart chart) {
			super(chart);
			Logger.debug("in AbstractPlot, constructor of private class VerdiChartPanel #1");
		}

		public VerdiChartPanel(JFreeChart chart, boolean properties, boolean save, boolean print, boolean zoom, boolean tooltips) {
			super(chart, properties, save, print, zoom, tooltips);
			Logger.debug("in AbstractPlot, constructor of private class VerdiChartPanel #2");
		}

		public VerdiChartPanel(JFreeChart chart, boolean useBuffer) {
			super(chart, useBuffer);
			Logger.debug("in AbstractPlot, constructor of private class VerdiChartPanel #3");
		}

		public VerdiChartPanel(JFreeChart chart, int width, int height, int minimumDrawWidth, int minimumDrawHeight, int maximumDrawWidth, int maximumDrawHeight, boolean useBuffer, boolean properties, boolean save, boolean print, boolean zoom, boolean tooltips) {
			super(chart, width, height, minimumDrawWidth, minimumDrawHeight, maximumDrawWidth, maximumDrawHeight, useBuffer, properties, save, print, zoom, tooltips);
			Logger.debug("in AbstractPlot, constructor of private class VerdiChartPanel #4");
		}

		public void doZoom(Rectangle2D selection) {
			Logger.debug("in AbstractPlot, private class VerdiChartPanel.doZoom");
			zoom = true;
			zoom(selection);
			zoom = false;
		}

		public void zoom(Rectangle2D selection) {
			if (zoom) 
				super.zoom(selection);
			Logger.debug("in AbstractPlot, private class VerdiChartPanel.zoom");
			repaint();
		}


		public boolean isZoom() {
			Logger.debug("in  AbstractPlot, private class VerdiChartPanel.isZoom");
			return zoom;
		}

		public void setZoom(boolean zoom) {
			Logger.debug("in AbstractPlot, private class VerdiChartPanel.setZoom");
			this.zoom = zoom;
		}

		/**
		 * Displays a dialog that allows the user to edit the properties for the
		 * current chart.
		 *
		 * @since 1.0.3
		 */
		public void doEditChartProperties() {
			Logger.debug("in AbstractPlot, private class VerdiChartPanel.doEditChartProperties");
			Window window = SwingUtilities.getWindowAncestor(panel);
			ConfigDialog dialog = null;
			if (window instanceof JFrame)
			{
				dialog = new ConfigDialog((JFrame) window);
				Logger.debug("just initialized dialog to new ConfigDialog((JFrame) window");
			}
			else
			{
				dialog = new ConfigDialog((JDialog)window);
				Logger.debug("just initialized dialog to new ConfigDialog((JDialog) window");
			}
			dialog.init(AbstractPlot.this, null);
			dialog.setVisible(true);
		}
		
		/**
		 * Displays a dialog that allows the user to edit the theme for the
		 * current chart.
		 *
		 * @since 1.6
		 */
		public void doEditChartTheme() {
			Window window = SwingUtilities.getWindowAncestor(panel);
			ThemeDialog dialog = null;
			if (window instanceof JFrame) dialog = new ThemeDialog((JFrame) window, panel.getChart());
			else dialog = new ThemeDialog((JDialog)window, panel.getChart());
			dialog.setSize(500, 506);
			dialog.setVisible(true);
		}
	}
	
	public void exportEPSImage(String filename) {
		int width = panel.getWidth();
		int height = panel.getHeight();
		exportEPSImage(filename, width, height);
	}
	
	public void exportEPSImage(String filename, int width, int height) {
		ChartEpsRenderer renderer = new ChartEpsRenderer(width, height);
		EpsTools.createFromDrawable(renderer, filename, width, height, ColorMode.COLOR_RGB);
	}
	
	class ChartEpsRenderer implements Drawable {
		final int canvasWidth, canvasHeight;
		
		public ChartEpsRenderer(int width, int height) {
			canvasWidth = width;
			canvasHeight = height;
			Logger.debug("within subclass EpsRenderer, setting canvasWidth = " + width + "; canvasHeight = " + canvasHeight);
		}
		
		@Override
		public void draw(Graphics2D g, Rectangle2D rect) {
			panel.directPaintImage(g);
		}
	}
}
