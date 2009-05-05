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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Font;
import java.awt.font.GlyphVector;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ringsearch.SSSRFinder;

import net.sf.structure.cdk.util.GeometryKit;

/**
 * A default implementation of the <code>GraphicsPainter</code> interface.
 * @author Richard Apodaca
 */
public class DefaultGraphicsPainter implements GraphicsPainter
{
  private IAtomContainer structure;
  private ColorScheme colorScheme;
  private Hashtable atomToShape;
  private Hashtable atomPairToShape;
  private Rectangle2D perimeter;
  private double atomPairLength;
  private double atomHeight;
  private double lineSpacing;
  private double lineThickness;
  private boolean antialiasing;
  private IRingSet sssr;
  private boolean aromaticityDetected;
  
  /**
   * Default constructor.
   */
  public DefaultGraphicsPainter()
  {
    super();
    
    colorScheme = new DefaultColorScheme();
    structure = null;
    atomHeight = 0.50;
    lineSpacing = 0.20;
    lineThickness = 0.07;//0.1;
    antialiasing = true;
    atomToShape = new Hashtable();
    atomPairToShape = new Hashtable();  
    perimeter = null;
    atomPairLength = 0;
    sssr = null;
    aromaticityDetected = false;
  }

  /* (non-Javadoc)
   * @see net.sf.josef.paint.Painter#getAtomContainer()
   */
  public IAtomContainer getAtomContainer()
  {
    return structure;
  }

  /* (non-Javadoc)
   * @see net.sf.josef.paint.Painter#setAtomContainer(org.openscience.cdk.interfaces.IAtomContainer)
   */
  public void setAtomContainer(IAtomContainer ac)
  {
    this.structure = ac;
    this.sssr = null;
    this.aromaticityDetected = false;
    
    layout();
  }

  /* (non-Javadoc)
   * @see net.sf.structure.paint.GraphicsPainter#paint(java.awt.Graphics2D, java.awt.geom.Rectangle2D)
   */
  public void paint(Graphics2D g, Rectangle2D bounds)
  {
    if (structure == null)
    {
      return;
    }

    prepaint(g, bounds);
    paintAtomPairs(g);
    paintAtoms(g);
    postpaint(g, bounds);
  }

  /* (non-Javadoc)
   * @see net.sf.structure.paint.GraphicsPainter#importSettings(net.sf.structure.paint.GraphicsPainter.SettingsImporter)
   */
  public void importSettings(SettingsImporter importer)
  {
    this.antialiasing = importer.getAntialiasing();
    this.atomHeight = importer.getAtomLabelHeight();
    this.colorScheme = importer.getColorScheme();
    this.lineSpacing = importer.getLineSpacing();
    this.lineThickness = importer.getLineThickness();
    
    layout();
  }

  /* (non-Javadoc)
   * @see net.sf.structure.paint.GraphicsPainter#exportSettings(net.sf.structure.paint.GraphicsPainter.SettingsExporter)
   */
  public void exportSettings(SettingsExporter exporter)
  {
    exporter.setAntialiasing(antialiasing);
    exporter.setAtomLabelHeight(atomHeight);
    exporter.setColorScheme(colorScheme);
    exporter.setLineSpacing(lineSpacing);
    exporter.setLineThickness(lineThickness);
  }
  
  private double getX(IAtom atom)
  {
    return atom.getX2d();
  }
  
  private double getY(IAtom atom)
  {
    // match molfile y-coordinate with Graphics y-coordinate
    return -atom.getY2d();
  }
  
  private void layout()
  {
    atomToShape.clear();
    atomPairToShape.clear();
    this.atomPairLength = getAverageAtomDistance(structure);
    
    createAtomShapes();
    createAtomPairShapes();
    
    this.perimeter = createPerimeter();
  }
  
  private void createAtomShapes()
  {
    for (int i = 0; i < structure.getAtomCount(); i++)
    {
      IAtom atom = structure.getAtomAt(i);
      
      if (!"C".equals(atom.getSymbol()))
      {  
        atomToShape.put(atom, createShape(atom));
      }
    }
  }
  
  private Shape createShape(IAtom atom)
  {
    double radius = atomPairLength * atomHeight;
    double x = getX(atom) - 0.5 * radius;
    double y = getY(atom) - 0.5 * radius;
    Ellipse2D circle = new Ellipse2D.Double(x, y, radius, radius);
    
    return circle;      
  }
  
  private void createAtomPairShapes()
  {
    for (int i = 0; i < structure.getBondCount(); i++)
    {
      IBond pair = structure.getBondAt(i);
      
      double order = pair.getOrder();
      
      if (order == 1.0)
      {
        atomPairToShape.put(pair, createSingleBondShape(pair));
      }
      
      else if (order == 2.0)
      {
        atomPairToShape.put(pair, createDoubleBondShape(pair));
      }
      
      else if (order == 3.0)
      {
        atomPairToShape.put(pair, createTripleBondShape(pair));
      }
      
      else
      {
        atomPairToShape.put(pair, createNonintegerBondShape(pair, order));
      }
    }
  }
  
  private Shape createSingleBondShape(IBond pair)
  {
    return createLine(pair);
  }
  
  private Shape createDoubleBondShape(IBond pair)
  {
    Shape shape = null;
    
    int source = structure.getBondCount(pair.getAtomAt(0)) - 1;
    int target = structure.getBondCount(pair.getAtomAt(1)) - 1;
    
    if ((source == 1 && target == 0) || (source == 0 && target == 1))
    {
      shape = createTerminalDoubleBondShape(pair);
    }
    
    else if (source == 0 ||  target == 0)
    {
      shape = create11DisubstitutedDoubleBondShape(pair);
    }
    
    else if (source == 1 && target == 1)
    {
      shape = create12DisubstitutedDoubleBondShape(pair);
    }
    
    else if ((source == 2 && target == 1) || (source == 1 && target == 2))
    {
      shape = createTriSubstitutedDoubleBondShape(pair);
    }
    
    else if (source == 2 && target == 2)
    {
      shape = createTetrasubstitutedBondShape(pair);
    }
    
    else
    {
      shape = createTerminalDoubleBondShape(pair);
    }
    
    return shape;
  }
  
  private Shape createTerminalDoubleBondShape(IBond pair)
  {
    Line2D line1 = createLine(pair);
    Line2D line2 = createLine(pair);
    double translation = 0.5 * atomPairLength * lineSpacing;

    GeometryKit.translate(line2, -2 * translation);
    GeometryKit.shortenToCenter(line2, 0.25);

    GeneralPath path = new GeneralPath();

    path.append(line1, false);
    path.append(line2, false);

    return path;
  }
  
  private IAtom getSourceSubstituent(IBond pair)
  {
    IAtom source = pair.getAtomAt(0);
    
    IAtom[] neighbors = structure.getConnectedAtoms(source);
    
    for (int i = 0; i < neighbors.length; i++)
    {
      if (neighbors[i] != pair.getAtomAt(1))
      {
        return neighbors[i];
      }
    }
    
    return null;
  }
  
  private IAtom getTargetSubstituent(IBond pair)
  {
    IAtom target = pair.getAtomAt(1);
    
    IAtom[] neighbors = structure.getConnectedAtoms(target);
    
    for (int i = 0; i < neighbors.length; i++)
    {
      if (neighbors[i] != pair.getAtomAt(0))
      {
        return neighbors[i];
      }
    }
    
    return null;
  }

  
  private Shape create12DisubstitutedDoubleBondShape(IBond pair)
  {
    Line2D line1 = createLine(pair);
    Line2D line2 = createLine(pair);
    double translation = 0.5 * atomPairLength * lineSpacing;
    double sourceX = getX(getSourceSubstituent(pair));
    double sourceY = getY(getSourceSubstituent(pair));
    double targetX = getX(getTargetSubstituent(pair));
    double targetY = getY(getTargetSubstituent(pair));
    
    boolean sourceAbove = GeometryKit.pointAbove(line1, sourceX, sourceY);
    boolean targetAbove = GeometryKit.pointAbove(line1, targetX, targetY);
    
    if (sourceAbove && targetAbove)
    {
      translation = -translation;
    }
    
    GeometryKit.translate(line2, -2 * translation);
    GeometryKit.shortenToCenter(line2, 0.25);

    GeneralPath path = new GeneralPath();

    path.append(line1, false);
    path.append(line2, false);

    return path;
  }
  
  private Shape create11DisubstitutedDoubleBondShape(IBond pair)
  {
    Line2D line1 = createLine(pair);
    Line2D line2 = createLine(pair);
    double translation = 0.5 * atomPairLength * lineSpacing;

    GeometryKit.translate(line1, translation);
    GeometryKit.translate(line2, -translation);

    GeneralPath path = new GeneralPath();

    path.append(line1, false);
    path.append(line2, false);

    return path;
  }
  
  private Shape createTriSubstitutedDoubleBondShape(IBond pair)
  {
    Line2D line1 = createLine(pair);
    Line2D line2 = createLine(pair);
    double translation = 0.5 * atomPairLength * lineSpacing;
    
    double x = 0;
    double y = 0;
    
    if (structure.getBondCount(pair.getAtomAt(0)) == 2)
    {
      x = getX(getSourceSubstituent(pair));
      y = getY(getSourceSubstituent(pair));
    }
    
    else
    {
      x = getX(getTargetSubstituent(pair));
      y = getY(getTargetSubstituent(pair));
    }
    
    boolean above = GeometryKit.pointAbove(line1, x, y);
    
    if (above)
    {
      translation = -translation;
    }

    GeometryKit.translate(line2, -2 * translation);
    GeometryKit.shortenToCenter(line2, 0.25);

    GeneralPath path = new GeneralPath();

    path.append(line1, false);
    path.append(line2, false);

    return path;
  }
  
  private Shape createTetrasubstitutedBondShape(IBond pair)
  {
    findSSSR();
    
    if (sssr.getAtomContainerCount() == 0 || !isBondInCycle(pair))
    {
      return create11DisubstitutedDoubleBondShape(pair);
    }

    return createRingTetrasubstitutedBondShape(pair);
  }
  
  private Shape createRingTetrasubstitutedBondShape(IBond pair)
  {
    IAtom anchor = getTetrasubRingBondAnchor(pair);
    
    if (anchor == null)
    {
      return this.create11DisubstitutedDoubleBondShape(pair);
    }
    
    Line2D line1 = createLine(pair);
    Line2D line2 = createLine(pair);
    double translation = 0.5 * atomPairLength * lineSpacing;
    
    if (GeometryKit.pointAbove(line1, getX(anchor), getY(anchor)))
    {
      translation = -translation;
    }
    
    GeometryKit.translate(line2, -2 * translation);
    GeometryKit.shortenToCenter(line2, 0.25);

    GeneralPath path = new GeneralPath();

    path.append(line1, false);
    path.append(line2, false);
    
    return path;
  }
  
  private IAtom getTetrasubRingBondAnchor(IBond bond)
  {   
    Set ringBondSubstituents = createRingSubstituentSet(bond);
    
    if (ringBondSubstituents.size() == 2)
    {
      return (IAtom) ringBondSubstituents.toArray()[0];
    }
    
    if (ringBondSubstituents.size() == 3)
    {
      List connected = structure.getConnectedAtomsVector(bond.getAtomAt(0));
      
      connected.addAll(structure.getConnectedAtomsVector(bond.getAtomAt(1)));
      connected.remove(bond.getAtomAt(1));
      connected.remove(bond.getAtomAt(0));
      connected.removeAll(ringBondSubstituents);
      
      if (connected.size() != 1)
      {
        System.out.println("*** paint error ***");
      }
      
      IAtom sourceTarget = null;
      
      if (connected.get(0) == bond.getAtomAt(0))
      {
        sourceTarget = bond.getAtomAt(0);
      }
      
      else
      {
        sourceTarget = bond.getAtomAt(1);
      }
      
      Iterator it = ringBondSubstituents.iterator();
      
      while (it.hasNext())
      {
        IAtom atom = (IAtom) it.next();
        
        if (structure.getBond(atom, sourceTarget) == null)
        {
          return atom;
        }
      }
    }
    
    if (ringBondSubstituents.size() == 4)
    {
      detectAromaticity(sssr);
      
      Iterator it = ringBondSubstituents.iterator();
      
      while (it.hasNext())
      {
        IAtom atom = (IAtom) it.next();
        
        for (int i = 0; i < sssr.getAtomContainerCount(); i++)
        {
          IAtomContainer ring = sssr.getAtomContainer(i);
          
          if (ring.contains(atom) && ring.getAtomCount() == 6 && atom.getFlag(CDKConstants.ISAROMATIC))
          {
            return atom;
          }
        }
      }
    }

    return null;
  }
  
  private boolean isBondInCycle(IBond bond)
  {
    for (int i = 0; i < sssr.getAtomContainerCount(); i++)
    {
      IAtomContainer ring = sssr.getAtomContainer(i);
      
      for (int j = 0; j < ring.getBondCount(); j++)
      {
        if (bond.equals(ring.getBondAt(j)))
        {
          return true;
        }
      }
    }
    
    return false;
  }
  
  private void detectAromaticity(IRingSet ringSet)
  {
    if (aromaticityDetected)
    {
      return;
    }
    
    try
    {
      HueckelAromaticityDetector.detectAromaticity(structure, ringSet);
    }
    
    catch (CDKException e)
    {
      e.printStackTrace();
    }
    
    aromaticityDetected = false;
  }
  
  private void findSSSR()
  {
    if (sssr == null)
    {
      SSSRFinder finder = new SSSRFinder(structure);
      
      sssr = finder.findSSSR();
    }
  }
  
  private Set createRingSubstituentSet(IBond bond)
  {
    Set ringSubs = new HashSet();
    
    for (int i = 0; i < sssr.getAtomContainerCount(); i++)
    {
      IAtomContainer ring = sssr.getAtomContainer(i);
      
      ringSubs.addAll(ring.getConnectedAtomsVector(bond.getAtomAt(0)));
      ringSubs.addAll(ring.getConnectedAtomsVector(bond.getAtomAt(1)));
      
      ringSubs.remove(bond.getAtomAt(1));
      ringSubs.remove(bond.getAtomAt(0));
    }
    
    return ringSubs;
  }
  
  private Shape createTripleBondShape(IBond pair)
  {
    Line2D line1 = createLine(pair);
    Line2D line2 = createLine(pair);
    Line2D line3 = createLine(pair);

    double translation = atomPairLength * lineSpacing;

    GeometryKit.translate(line1, translation);
    GeometryKit.translate(line3, -translation);

    GeneralPath path = new GeneralPath();

    path.append(line1, false);
    path.append(line2, false);
    path.append(line3, false);

    return path;
  }
  
  private Shape createNonintegerBondShape(IBond pair, double order)
  {
    return createTripleBondShape(pair);
  }
  
  private Line2D createLine(IBond pair)
  {
    double sourceX = getX(pair.getAtomAt(0));
    double sourceY = getY(pair.getAtomAt(0));
    double targetX = getX(pair.getAtomAt(1));
    double targetY = getY(pair.getAtomAt(1));
    Line2D line =
      new Line2D.Double(sourceX, sourceY, targetX, targetY);
      
    Shape sourceShape = getShape(pair.getAtomAt(0));
    Shape targetShape = getShape(pair.getAtomAt(1));
    
    if (sourceShape != null)
    {
      GeometryKit.trimLine(line, sourceShape.getBounds2D());
    }
    
    if (targetShape != null)
    {
      GeometryKit.trimLine(line, targetShape.getBounds2D());
    }
    
    return line;     
  }
  
  private Rectangle2D createPerimeter()
  {
    Iterator it = atomPairToShape.values().iterator();
    Rectangle2D result = ((Shape) it.next()).getBounds2D();
    
    while(it.hasNext())
    { 
      Rectangle2D.union(result, ((Shape) it.next()).getBounds2D(), result);
    }
    
    it = atomToShape.values().iterator();
    
    while (it.hasNext())
    {
      Shape shape = (Shape) it.next(); 
      
      Rectangle2D.union(result, shape.getBounds2D(), result);
    }
    
    double correction = 0.5 * lineThickness * atomPairLength;
    //TODO only apply correction on boundary not intersecting atom label
    
    result.setRect(result.getX() - correction, result.getY() - correction, result.getWidth() + 2 * correction, result.getHeight() + 2 * correction);

    return result;      
  }
  
  private void prepaint(Graphics2D g, Rectangle2D bounds)
  {
    prepareGraphics(g, perimeter.getBounds2D(), bounds);
  }
  
  private void paintAtomPairs(Graphics2D g)
  {
    Color color = g.getColor();
    Stroke gStroke = g.getStroke();
    
    setAtomPairStroke(g, atomPairLength);
    
    g.setColor(Color.BLACK);
    
    for (int i = 0; i < structure.getBondCount(); i++)
    {
      g.draw(getShape(structure.getBondAt(i)));
    }
    
    g.setStroke(gStroke);
    g.setColor(color);
  }
  
  private void setAtomPairStroke(Graphics2D g, double atomPairLength)
  {
    g.setStroke(new BasicStroke((float) (lineThickness * atomPairLength),
    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
  }
  
  private Shape getShape(IBond pair)
  {
    return (Shape) atomPairToShape.get(pair);
  }
  
  private void paintAtoms(Graphics2D g)
  {
    setAtomStroke(g, atomPairLength);
    
    Color gColor = g.getColor();
    
    for (int i = 0; i < structure.getAtomCount(); i++)
    {
      paintAtom(structure.getAtomAt(i), g);
    }
    
    g.setColor(gColor);
  }
  
  private void paintAtom(IAtom atom, Graphics2D g)
  {
    Shape circle = getShape(atom);
    
    if (circle != null)
    {
      Rectangle2D constraint = circle.getBounds2D();
      AffineTransform at = g.getTransform();
      
      setColor(g, atom);
      
      Font font = new Font("SansSerif", Font.BOLD, 6); // a basic font
      GlyphVector gv =
        font.createGlyphVector(g.getFontRenderContext(), atom.getSymbol().toString());
      Shape glyph1 = gv.getGlyphOutline(0);
      GeneralPath glyph = new GeneralPath();
      
      glyph.append(glyph1, false);
      
      if (atom.getSymbol().toString().length() == 2)
      {
        Shape glyph2 = gv.getGlyphOutline(1);
        
        glyph.append(glyph2, false);
      }
      
      Rectangle2D glyphBounds = glyph.getBounds2D();

      double coordX = getX(atom);
      double coordY = getY(atom);
      
      double scaleY = 0.7 * (constraint.getHeight() / glyphBounds.getHeight());
      double x = coordX - 0.57 * scaleY * (glyphBounds.getWidth()); // 0.57 needed to center!
      double y = coordY + 0.5 * scaleY * glyphBounds.getHeight();
      
      g.translate(x, y);
      g.scale(scaleY, scaleY);

      g.fill(glyph);

      g.setTransform(at);
    }
  }
  
  private void setAtomStroke(Graphics2D g, double atomPairLength)
  {
    g.setStroke(new BasicStroke((float) (lineThickness * atomPairLength),
    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
  }
  
  private Shape getShape(IAtom atom)
  {
    return (Shape) atomToShape.get(atom);
  }
  
  private void setColor(Graphics2D g, IAtom atom)
  {
    Color color = colorScheme.getColor(atom);
    
    g.setColor(color);
  }
  
  private void postpaint(Graphics2D g, Rectangle2D bounds)
  {
    
  }
  
  private void prepareGraphics(Graphics2D g, Rectangle2D renderBounds, Rectangle2D contextBounds)
  {
    prepareCoordinateSystem(g, contextBounds);
    scaleGraphics(g, renderBounds, contextBounds);
    translateGraphics(g, renderBounds, contextBounds);
    setRenderingHints(g);
  }
  
  private void prepareCoordinateSystem(Graphics2D g, Rectangle2D contextBounds)
  {
    
  }
  
  private void scaleGraphics(Graphics2D g, Rectangle2D renderBounds, Rectangle2D contextBounds)
  {
    Rectangle2D mBounds = renderBounds;
    Rectangle2D rBounds = contextBounds;

    double scaleX = rBounds.getWidth() / mBounds.getWidth();
    double scaleY = rBounds.getHeight() / mBounds.getHeight();

    if (scaleX > scaleY)
    {
      g.scale(scaleY, scaleY);
    }

    else
    {
      g.scale(scaleX, scaleX);
    }
  }
  
  /**
   * Sets the rendering hints for the specified <code>Graphics2D</code> context.
   * 
   * @param g the <code>Graphics2D</code> context
   */
  private void setRenderingHints(Graphics2D g)
  {
    if (antialiasing)
    {  
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
  }
  
  private void translateGraphics(Graphics2D g, Rectangle2D renderBounds, Rectangle2D contextBounds)
  {
    Rectangle2D pBounds = renderBounds;
    Rectangle2D cBounds = contextBounds;
    double scale = g.getTransform().getScaleX();
    double dx = -pBounds.getX() * scale + 0.5 * (cBounds.getWidth() - pBounds.getWidth() * scale);
    double dy = -pBounds.getY() * scale + 0.5 * (cBounds.getHeight() - pBounds.getHeight() * scale);

    g.translate(dx / scale, dy / scale);
  }
  
  private double getAverageAtomDistance(IAtomContainer structure)
  {
    double average = 0;

    if (structure != null)
    {
      double sum = 0;
      
      for (int i = 0; i < structure.getBondCount(); i++)
      {
        IBond bond = structure.getBondAt(i);
        
        double x1 = getX(bond.getAtomAt(0));
        double y1 = getY(bond.getAtomAt(0));
        double x2 = getX(bond.getAtomAt(1));
        double y2 = getY(bond.getAtomAt(1));
        
        sum += GeometryKit.getDistance(x1, y1, x2, y2);
      }

      average = sum / structure.getBondCount();
    }

    return average;
  }
}
