package anl.verdi.plot.jfree;

/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * --------------------
 * XYBlockRenderer.java
 * --------------------
 * (C) Copyright 2006, 2007, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYBlockRenderer.java,v 1.1.2.2 2007/02/02 10:51:42 mungady Exp $
 *
 * Changes
 * -------
 * 05-Jul-2006 : Version 1 (DG);
 * 02-Feb-2007 : Added getPaintScale() method (DG);
 *
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleAnchor;

/**
 * A renderer that represents data from an {@link org.jfree.data.xy.XYZDataset} by drawing a
 * color block at each (x, y) point, where the color is a function of the
 * z-value from the dataset.
 *
 * @since 1.0.4
 */
public class XYBlockRenderer extends AbstractXYItemRenderer
				implements XYItemRenderer, Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -435886241891000915L;

	/**
	 * The block width (defaults to 1.0).
	 */
	private double blockWidth = 1.0;

	/**
	 * The block height (defaults to 1.0).
	 */
	private double blockHeight = 1.0;

	/**
	 * The anchor point used to align each block to its (x, y) location.  The
	 * default value is <code>RectangleAnchor.CENTER</code>.
	 */
	private RectangleAnchor blockAnchor = RectangleAnchor.CENTER;

	/**
	 * Temporary storage for the x-offset used to align the block anchor.
	 */
	private double xOffset;

	/**
	 * Temporary storage for the y-offset used to align the block anchor.
	 */
	private double yOffset;

	/**
	 * The paint scale.
	 */
	private PaintScale paintScale;
	private Stroke gridLineStroke = new BasicStroke(1f);//new BasicStroke(1, 0, 2, 0, new float[]{2f, 2f}, 0);
	private boolean gridLinesEnabled = false;
	private Paint gridLineColor = Color.BLACK;
	private BasicStroke basicStroke = new BasicStroke(1f);

	/**
	 * Creates a new <code>XYBlockRenderer</code> instance with default
	 * attributes.
	 */
	public XYBlockRenderer() {
		updateOffsets();
		this.paintScale = new LookupPaintScale();
	}

	/**
	 * Returns the block width, in data/axis units.
	 *
	 * @return The block width.
	 * @see #setBlockWidth(double)
	 */
	public double getBlockWidth() {
		return this.blockWidth;
	}

	/**
	 * Sets the width of the blocks used to represent each data item.
	 *
	 * @param width the new width, in data/axis units (must be > 0.0).
	 * @see #getBlockWidth()
	 */
	public void setBlockWidth(double width) {
		if (width <= 0.0) {
			throw new IllegalArgumentException(
							"The 'width' argument must be > 0.0");
		}
		this.blockWidth = width;
		updateOffsets();
		this.notifyListeners(new RendererChangeEvent(this));
	}

	/**
	 * Returns the block height, in data/axis units.
	 *
	 * @return The block height.
	 * @see #setBlockHeight(double)
	 */
	public double getBlockHeight() {
		return this.blockHeight;
	}

	/**
	 * Sets the height of the blocks used to represent each data item.
	 *
	 * @param height the new height, in data/axis units (must be > 0.0).
	 * @see #getBlockHeight()
	 */
	public void setBlockHeight(double height) {
		if (height <= 0.0) {
			throw new IllegalArgumentException(
							"The 'height' argument must be > 0.0");
		}
		this.blockHeight = height;
		updateOffsets();
		this.notifyListeners(new RendererChangeEvent(this));
	}

	/**
	 * Returns the anchor point used to align a block at its (x, y) location.
	 * The default values is {@link RectangleAnchor#CENTER}.
	 *
	 * @return The anchor point (never <code>null</code>).
	 * @see #setBlockAnchor(RectangleAnchor)
	 */
	public RectangleAnchor getBlockAnchor() {
		return this.blockAnchor;
	}

	/**
	 * Sets the anchor point used to align a block at its (x, y) location and
	 * sends a {@link RendererChangeEvent} to all registered listeners.
	 *
	 * @param anchor the anchor.
	 * @see #getBlockAnchor()
	 */
	public void setBlockAnchor(RectangleAnchor anchor) {
		if (anchor == null) {
			throw new IllegalArgumentException("Null 'anchor' argument.");
		}
		if (this.blockAnchor.equals(anchor)) {
			return;  // no change
		}
		this.blockAnchor = anchor;
		updateOffsets();
		notifyListeners(new RendererChangeEvent(this));
	}

	/**
	 * Returns the paint scale used by the renderer.
	 *
	 * @return The paint scale (never <code>null</code>).
	 * @see #setPaintScale(PaintScale)
	 * @since 1.0.4
	 */
	public PaintScale getPaintScale() {
		return this.paintScale;
	}

	/**
	 * Sets the paint scale used by the renderer.
	 *
	 * @param scale the scale (<code>null</code> not permitted).
	 * @see #getPaintScale()
	 * @since 1.0.4
	 */
	public void setPaintScale(PaintScale scale) {
		if (scale == null) {
			throw new IllegalArgumentException("Null 'scale' argument.");
		}
		this.paintScale = scale;
		notifyListeners(new RendererChangeEvent(this));
	}


	public double getXOffset() {
		return xOffset;
	}

	public double getYOffset() {
		return yOffset;
	}

	/**
	 * Updates the offsets to take into account the block width, height and
	 * anchor.
	 */
	private void updateOffsets() {
		if (this.blockAnchor.equals(RectangleAnchor.BOTTOM_LEFT)) {
			xOffset = 0.0;
			yOffset = 0.0;
		} else if (this.blockAnchor.equals(RectangleAnchor.BOTTOM)) {
			xOffset = -this.blockWidth / 2.0;
			yOffset = 0.0;
		} else if (this.blockAnchor.equals(RectangleAnchor.BOTTOM_RIGHT)) {
			xOffset = -this.blockWidth;
			yOffset = 0.0;
		} else if (this.blockAnchor.equals(RectangleAnchor.LEFT)) {
			xOffset = 0.0;
			yOffset = -this.blockHeight / 2.0;
		} else if (this.blockAnchor.equals(RectangleAnchor.CENTER)) {
			xOffset = -this.blockWidth / 2.0;
			yOffset = -this.blockHeight / 2.0;
		} else if (this.blockAnchor.equals(RectangleAnchor.RIGHT)) {
			xOffset = -this.blockWidth;
			yOffset = -this.blockHeight / 2.0;
		} else if (this.blockAnchor.equals(RectangleAnchor.TOP_LEFT)) {
			xOffset = 0.0;
			yOffset = -this.blockHeight;
		} else if (this.blockAnchor.equals(RectangleAnchor.TOP)) {
			xOffset = -this.blockWidth / 2.0;
			yOffset = -this.blockHeight;
		} else if (this.blockAnchor.equals(RectangleAnchor.TOP_RIGHT)) {
			xOffset = -this.blockWidth;
			yOffset = -this.blockHeight;
		}
	}

	/**
	 * Returns the lower and upper bounds (range) of the x-values in the
	 * specified dataset.
	 *
	 * @param dataset the dataset (<code>null</code> permitted).
	 * @return The range (<code>null</code> if the dataset is <code>null</code>
	 *         or empty).
	 */
	public Range findDomainBounds(XYDataset dataset) {
		if (dataset != null) {
			Range r = DatasetUtilities.findDomainBounds(dataset, false);
			return new Range(r.getLowerBound() + xOffset, r.getUpperBound()
							+ blockWidth + xOffset);
		} else {
			return null;
		}
	}

	/**
	 * Returns the range of values the renderer requires to display all the
	 * items from the specified dataset.
	 *
	 * @param dataset the dataset (<code>null</code> permitted).
	 * @return The range (<code>null</code> if the dataset is <code>null</code>
	 *         or empty).
	 */
	public Range findRangeBounds(XYDataset dataset) {
		if (dataset != null) {
			Range r = DatasetUtilities.findRangeBounds(dataset, false);
			return new Range(r.getLowerBound() + yOffset, r.getUpperBound()
							+ blockHeight + yOffset);
		} else {
			return null;
		}
	}

	/**
	 * Draws the block representing the specified item.
	 *
	 * @param g2             the graphics device.
	 * @param state          the state.
	 * @param dataArea       the data area.
	 * @param info           the plot rendering info.
	 * @param plot           the plot.
	 * @param domainAxis     the x-axis.
	 * @param rangeAxis      the y-axis.
	 * @param dataset        the dataset.
	 * @param series         the series index.
	 * @param item           the item index.
	 * @param crosshairState the crosshair state.
	 * @param pass           the pass index.
	 */
	public void drawItem(Graphics2D g2, XYItemRendererState state,
	                     Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
	                     ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
	                     int series, int item, CrosshairState crosshairState, int pass) {

		double x = dataset.getXValue(series, item);
		double y = dataset.getYValue(series, item);
		double z = 0.0;
		double max = paintScale.getUpperBound();
		double min = paintScale.getLowerBound();
		
		if (dataset instanceof XYZDataset)
			z = ((XYZDataset) dataset).getZValue(series, item);

		//NOTE: so to get the max/min color instead of unknown (Qun He, UNC, 03/19/2009)
		if (z > max) 
			z = max;
		
		if (z < min) 
			z = min;
		
		Color p = (Color)this.paintScale.getPaint(z);
		
		double xx0 = domainAxis.valueToJava2D(x + this.xOffset, dataArea,
						plot.getDomainAxisEdge());
		double yy0 = rangeAxis.valueToJava2D(y + this.yOffset, dataArea,
						plot.getRangeAxisEdge());
		double xx1 = domainAxis.valueToJava2D(x + this.blockWidth
						+ this.xOffset, dataArea, plot.getDomainAxisEdge());
		double yy1 = rangeAxis.valueToJava2D(y + this.blockHeight
						+ this.yOffset, dataArea, plot.getRangeAxisEdge());
		Rectangle2D block;
		PlotOrientation orientation = plot.getOrientation();
		if (orientation.equals(PlotOrientation.HORIZONTAL)) {
			block = new Rectangle2D.Double(Math.min(yy0, yy1),
							Math.min(xx0, xx1), Math.abs(yy1 - yy0),
							Math.abs(xx0 - xx1));
		} else {
			block = new Rectangle2D.Double(Math.min(xx0, xx1),
							Math.min(yy0, yy1), Math.abs(xx1 - xx0),
							Math.abs(yy1 - yy0));
		}
		g2.setColor(p);
		g2.fill(block);

		if (gridLinesEnabled) {
			boolean aaOn = false;
			if (g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING) == RenderingHints.VALUE_ANTIALIAS_ON) {
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				aaOn = true;
			}
			g2.setPaint(gridLineColor);
			g2.setStroke(gridLineStroke);
			g2.draw(block);
			if (aaOn) g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			g2.setStroke(basicStroke);
			g2.draw(block);
		}
	}

	public void setGridLinesEnabled(boolean enabled) {
		this.gridLinesEnabled = enabled;
	}

	public boolean isGridLinesEnabled() {
		return gridLinesEnabled;
	}

	public Stroke getGridLineStroke() {
		return gridLineStroke;
	}

	public void setGridLineStroke(Stroke gridLineStroke) {
		this.gridLineStroke = gridLineStroke;
	}

	public Paint getGridLineColor() {
		return gridLineColor;
	}

	public void setGridLineColor(Paint gridLineColor) {
		this.gridLineColor = gridLineColor;
	}

	/**
	 * Tests this <code>XYBlockRenderer</code> for equality with an arbitrary
	 * object.  This method returns <code>true</code> if and only if:
	 * <ul>
	 * <li><code>obj</code> is an instance of <code>XYBlockRenderer</code> (not
	 * <code>null</code>);</li>
	 * <li><code>obj</code> has the same field values as this
	 * <code>XYBlockRenderer</code>;</li>
	 * </ul>
	 *
	 * @param obj the object (<code>null</code> permitted).
	 * @return A boolean.
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof XYBlockRenderer)) {
			return false;
		}
		XYBlockRenderer that = (XYBlockRenderer) obj;
		if (this.blockHeight != that.blockHeight) {
			return false;
		}
		if (this.blockWidth != that.blockWidth) {
			return false;
		}
		if (!this.blockAnchor.equals(that.blockAnchor)) {
			return false;
		}
		if (!this.paintScale.equals(that.paintScale)) {
			return false;
		}
		return super.equals(obj);
	}

	/**
	 * Returns a clone of this renderer.
	 *
	 * @return A clone of this renderer.
	 * @throws CloneNotSupportedException if there is a problem creating the
	 *                                    clone.
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}

