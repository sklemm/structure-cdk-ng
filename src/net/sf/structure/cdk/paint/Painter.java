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

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * <p>
 * A painter of 2-D molecular representations. <code>Painter</code> is a general-
 * purpose interface to basic painting capabilities. Implementations will
 * provide context-specific rendering capabilities.
 * </p>
 * 
 * <p>
 * Note that a <code>Painter</code> may or may not render the entire area
 * provided to it, depending on implementation. In many cases, <code>Painter
 * </code> will not directly render the color, image, or special
 * effect to be used on the background.
 * </p>
 * 
 * @author Richard Apodaca
 */
public interface Painter
{
  /**
   * Sets the <code>Structure</code> that will be rendered.
   * 
   * @param ac the <code>IAtomContainer</code> that will be rendered
   */
  public void setAtomContainer(IAtomContainer ac);
  
  /**
   * Returns the <code>IAtomContainer</code> that will be rendered.
   * 
   * @return the <code>IAtomContainer</code> that will be rendered
   */
  public IAtomContainer getAtomContainer();
  
  /**
   * Imports paint settings using <code>importer</code>.
   * 
   * @param importer the importer containing the settings to use
   */
  public void importSettings(SettingsImporter importer);
  
  /**
   * Exports paint settings to <code>exporter</code>.
   * 
   * @param exporter to exporter that will be written
   */
  public void exportSettings(SettingsExporter exporter);
  
  /**
   * An exporter of paint settings.
   * 
   * @author Richard Apodaca
   */
  public interface SettingsExporter
  {
    /**
     * Sets the atom label height, in terms of average interatomic distance.
     * 
     * @param atomLabelHeight the atom label height, in terms of average interatomic distance
     */
    public void setAtomLabelHeight(double atomLabelHeight);
    
    /**
     * Sets the line thickness, in terms of average interatomic distance.
     * 
     * @param lineThickness the line thickness, in terms of average interatomic distance
     */
    public void setLineThickness(double lineThickness);
    
    /**
     * Sets the line spacing, in terms of average interatomic distance.
     * 
     * @param lineSpacing the line spacing, in terms of average interatomic distance
     */
    public void setLineSpacing(double lineSpacing);
    
    /**
     * Activates or deactivates antialiasing.
     * 
     * @param antialiasing use <code>true</code> to activate antialiasing, or
     * <code>false</code> to deactivate it
     */
    public void setAntialiasing(boolean antialiasing);   
    
    /**
     * Sets the <code>ColorScheme</code>.
     * 
     * @param scheme the <code>ColorScheme</code>
     */
    public void setColorScheme(ColorScheme scheme);
  }
  
  /**
   * An importer of paint settings.
   * 
   * @author Richard Apodaca
   */
  public interface SettingsImporter
  {
    /**
     * Returns the atom label height relative to the average interatomic distance.
     *
     * @return the atom height relative to the average interatomic distance
     */
    public double getAtomLabelHeight();
    
    /**
     * Returns the line thickness relative to the average interatomic distance.
     * 
     * @return the line thickness relative to the average interatomic distance
     */
    public double getLineThickness();
    
    /**
     * Returns the parallel line spacing relative to the average atomic
     * distance.
     * 
     * @return the parallel line spacing relative to the average atomic
     * distance
     */
    public double getLineSpacing();
    
    /**
     * Returns the <code>ColorScheme</code>.
     * 
     * @return the <code>ColorScheme</code>.
     */
    public ColorScheme getColorScheme();
    
    /**
     * Returns <code>true</code> if antialiasing is activated, or </code>false</code>
     * otherwise.
     * 
     * @return <code>true</code> if antialiasing is activated, or </code>false</code>
     * otherwise
     */
    public boolean getAntialiasing();   
  }
}
