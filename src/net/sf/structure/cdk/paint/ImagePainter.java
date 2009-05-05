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
 * Created on Dec 20, 2005
 */
package net.sf.structure.cdk.paint;

import java.awt.Color;
import java.awt.Image;

/**
 * A painter capable of creating output onto an <code>java.awt.Image</code> context.
 * 
 * @author Richard Apodaca
 * @see net.sf.structure.cdk.util.ImageKit
 */
public interface ImagePainter extends Painter
{
  /**
   * Paints a rendering onto <code>image</code>. The background color will be that specified
   * by <code>getBackgroundColor</code>.
   * 
   * @param image the Image that will be painted onto
   */
  public void paint(Image image);
  
  /**
   * Sets the background color of this <code>ImagePainter</code>.
   * 
   * @param color the new background color of this <code>ImagePainter</code>
   */
  public void setBackgroundColor(Color color);
  
  /**
   * Returns the background color being used by this <code>Painter</code>.
   * 
   * @return the background color being used by this <code>Painter</code>
   */
  public Color getBackgroundColor();
}
