/* =====================================================
 * StructureCDK : A 2D Molecular Visualization Framework
 * =====================================================
 *
 * Project Info:  http://structure.sourceforge.net
 *
 * Copyright (C) 2004-2006 Richard L. Apodaca
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * Created Mar 21, 2004
 */
 
package net.sf.structure.cdk.paint;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * <p>
 * A <code>GraphicsPainter</code> is responsible for providing a graphical representation
 * of a <code>Structure</code> on a <code>Graphics2D</code> context. This representation may be used
 * within a particular user interface framework such as AWT or Swing. Alternatively,
 * <code>GraphicsPainter</code> may be used in other contexts, such as those involving the
 * direct generation of graphics files.
 * </p>
 * 
 * @author Richard Apodaca
 * @see net.sf.structure.cdk.util.ImageKit
 */
public interface GraphicsPainter extends Painter
{
  /**
   * Paints this <code>GraphicsPainter</code> onto the specified <code>java.awt.Graphics
   * </code> context.
   * 
   * @param g the <code>Graphics2D</code> context
   * @param bounds the bounding rectangle into which context should be painted
   */
  public void paint(Graphics2D g, Rectangle2D bounds);
}
