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
 * Created on Dec 11, 2005
 */
package net.sf.structure.cdk.paint;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import org.openscience.cdk.interfaces.IAtomContainer;


/**
 * A <code>JComponent</code> capable of rendering 2-D molecular representations.
 * 
 * @author Richard Apodaca
 */
public class SwingPainter extends JComponent implements Painter
{
  private static final long serialVersionUID = 1;
  private GraphicsPainter painter = null;
  
  public SwingPainter()
  {
    super();
    
    this.painter = new DefaultGraphicsPainter();
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
    
    repaint();
  }

  /**
   * Constructs a <code>SwingPainter</code> using <code>painter</code>.
   * 
   * @param painter the GraphicsPainter to be used
   */
  public SwingPainter(GraphicsPainter painter)
  {
    super();
    
    this.painter = painter;
  }

  /* (non-Javadoc)
   * @see javax.swing.JComponent#paint(java.awt.Graphics)
   */
  public void paint(Graphics g)
  {
    super.paint(g);

    painter.paint((Graphics2D) g, getBounds());
  }

  /* (non-Javadoc)
   * @see net.sf.structure.paint.GraphicsPainter#exportSettings(net.sf.structure.paint.GraphicsPainter.SettingsExporter)
   */
  public void exportSettings(SettingsExporter exporter)
  {
    painter.exportSettings(exporter);
  }

  /* (non-Javadoc)
   * @see net.sf.structure.paint.GraphicsPainter#importSettings(net.sf.structure.paint.GraphicsPainter.SettingsImporter)
   */
  public void importSettings(SettingsImporter importer)
  {
    painter.importSettings(importer);
    
    repaint();
  }
}
