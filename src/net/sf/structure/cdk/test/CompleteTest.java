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
 * Created on Dec 7, 2005
 */
package net.sf.structure.cdk.test;

import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * The main entry point into the unit testing framework.
 * 
 * @author Richard Apodaca
 */
public class CompleteTest
{
  /**
   * This class should not be instantiated.
   */
  private CompleteTest()
  {
    
  }

  /**
   * The main method.
   * 
   * @param args not used
   */
  public static void main(String[] args)
  {
    TestSuite suite = new TestSuite();
    
    suite.addTestSuite(ImageTest.class);
    
    //suite.addTestSuite(StructureBuilderTest.class);
    //suite.addTestSuite(FlexMol2DRoundTripTest.class);
    
    TestRunner.run(suite);
  }
}
