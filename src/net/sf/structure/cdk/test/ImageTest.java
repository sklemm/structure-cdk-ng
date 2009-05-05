/**
 * 
 */
package net.sf.structure.cdk.test;

import junit.framework.TestCase;

import java.io.FileReader;

import net.sf.structure.cdk.util.ImageKit;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.Molecule;

/**
 * @author rich
 *
 */
public class ImageTest extends TestCase
{

  /**
   * 
   */
  public ImageTest()
  {
    super();
    // TODO Auto-generated constructor stub
  }
  
  public void testMakePNG() throws Exception
  {
    
  }
  
  public void testMakeSVG() throws Exception
  {
    
  }

  private void writePNG(String pathToMolfile, String pathToPNG) throws Exception
  {
    MDLReader mdlReader = new MDLReader(new FileReader(pathToMolfile));
    IMolecule mol = (IMolecule) mdlReader.read(new Molecule());

    ImageKit.writePNG(mol, 300, 300, pathToPNG);
  }
  
  private void writeSVG(String pathToMolfile, String pathToSVG) throws Exception
  {
    MDLReader mdlReader = new MDLReader(new FileReader(pathToMolfile));
    IMolecule mol = (IMolecule) mdlReader.read(new Molecule());
    
    ImageKit.writeSVG(mol, 300, 300, pathToSVG);
  }
}
