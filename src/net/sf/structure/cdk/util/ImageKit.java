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
 * Created on Dec 19, 2005
 */
package net.sf.structure.cdk.util;

import java.awt.image.RenderedImage;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.ImageIO;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;

import org.openscience.cdk.interfaces.IAtomContainer;

import net.sf.structure.cdk.paint.DefaultImagePainter;
import net.sf.structure.cdk.paint.ImagePainter;
import net.sf.structure.cdk.paint.GraphicsPainter;
import net.sf.structure.cdk.paint.DefaultGraphicsPainter;

/**
 * A set of utility methods for rendering molecular images in a variety of formats.
 * 
 * @author Richard Apodaca
 */
public class ImageKit
{
  private static ImagePainter painter = new DefaultImagePainter();
  private static GraphicsPainter graphicsPainter = new DefaultGraphicsPainter();
  
  static
  {
    painter.setBackgroundColor(Color.WHITE);
  }
  
  /**
   * This class should not be instantiated. Use the static methods instead.
   */
  private ImageKit()
  {
    
  }
  
  public static void writeSVG(IAtomContainer structure, int width, int height, String filePath) throws IOException
  {
    // Get a DOMImplementation
    DOMImplementation domImpl =
        GenericDOMImplementation.getDOMImplementation();

    // Create an instance of org.w3c.dom.Document
    Document document = domImpl.createDocument(null, "svg", null);
    
    // Create an instance of the SVG Generator
    SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
    
    graphicsPainter.setAtomContainer(structure);
    graphicsPainter.paint(svgGenerator, new Rectangle(width, height));
    
    Writer out = new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8");
    svgGenerator.stream(out, false);
  }
  
  /**
   * Writes a graphical representation of <code>structure</code> to a PNG image file
   * of dimensions <code>width</code> and <code>height</code>, using the specified
   * <code>String</code> as the path name. A white background, and default rendering
   * settings are used.
   * 
   * @param structure the Structure to be rendered
   * @param width the width, in pixels, of the image
   * @param height the height, in pixels, of the image
   * @param filePath the file pathname
   */
  public static void writePNG(IAtomContainer structure, int width, int height, String filePath) throws IOException
  {
    ImageIO.write(createRenderedImage(structure, width, height), "png", new File(filePath));
  }
  
  /**
   * Writes a graphical representation of <code>structure</code> to a JPG image file
   * of dimensions <code>width</code> and <code>height</code>, using the specified
   * <code>String</code> as the path name. A white background, and default rendering
   * settings are used.
   * 
   * @param structure the Structure to be rendered
   * @param width the width, in pixels, of the image
   * @param height the height, in pixels, of the image
   * @param filePath the file pathname
   */
  public static void writeJPG(IAtomContainer structure, int width, int height, String filePath) throws IOException
  {
    ImageIO.write(createRenderedImage(structure, width, height), "png", new File(filePath));
  }
  
  /**
   * Creates a <code>RenderedImage</code> of the specified dimensions from the specified
   * <code>Structure</code>.
   * 
   * @param structure the Structure to create an image from
   * @param width the width, in pixels, of the image
   * @param height the height, in pixels, of the image
   * @return a RenderedImage containing the specified <code>Structure</code>
   */
  public static RenderedImage createRenderedImage(IAtomContainer structure, int width, int height)
  {
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    
    painter.setAtomContainer(structure);
    painter.paint(image);
    
    return image;
  }
}
