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
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.openscience.cdk.interfaces.IAtomContainer;


/**
 * @author Richard Apodaca
 */
public class DefaultImagePainter implements ImagePainter
{
  private GraphicsPainter painter;
  private Color backgroundColor;
  
  /**
   * 
   */
  public DefaultImagePainter()
  {
    this(new DefaultGraphicsPainter());
  }
  
  /* (non-Javadoc)
   * @see net.sf.josef.paint.Painter#getAtomContainer()
   */
  public IAtomContainer getAtomContainer()
  {
    return painter.getAtomContainer();
  }

  /* (non-Javadoc)
   * @see net.sf.josef.paint.Painter#setAtomContainer(org.openscience.cdk.interfaces.IAtomContainer)
   */
  public void setAtomContainer(IAtomContainer ac)
  {
    painter.setAtomContainer(ac);
  }

  public DefaultImagePainter(GraphicsPainter painter)
  {
    this.painter = painter;
    this.backgroundColor = null;
  }

  /* (non-Javadoc)
   * @see net.sf.structure.paint.ImagePainter#paint(java.awt.Image)
   */
  public void paint(Image image)
  {
    Graphics2D g = (Graphics2D) image.getGraphics();
    Color color = g.getColor();
    Rectangle2D bounds =
      new Rectangle2D.Double(0, 0, image.getWidth(null), image.getHeight(null));
    
    if (backgroundColor != null)
    {
      paintBackground(g, bounds);
    }
    
    painter.paint(g, bounds);
    g.setColor(color);
  }

  /* (non-Javadoc)
   * @see net.sf.structure.paint.ImagePainter#setBackgroundColor(java.awt.Color)
   */
  public void setBackgroundColor(Color color)
  {
    this.backgroundColor = color;
  }

  /* (non-Javadoc)
   * @see net.sf.structure.paint.ImagePainter#getBackgroundColor()
   */
  public Color getBackgroundColor()
  {
    return backgroundColor;
  }

  /* (non-Javadoc)
   * @see net.sf.structure.paint.Painter#importSettings(net.sf.structure.paint.Painter.SettingsImporter)
   */
  public void importSettings(SettingsImporter importer)
  {
    painter.importSettings(importer);
  }

  /* (non-Javadoc)
   * @see net.sf.structure.paint.Painter#exportSettings(net.sf.structure.paint.Painter.SettingsExporter)
   */
  public void exportSettings(SettingsExporter exporter)
  {
    painter.exportSettings(exporter);
  }
  
  private void paintBackground(Graphics2D g, Rectangle2D bounds)
  {
    g.setColor(backgroundColor);
    g.fill(bounds);
  }
}
