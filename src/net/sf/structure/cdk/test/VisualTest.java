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
package net.sf.structure.cdk.test;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.event.EventListenerList;
import javax.swing.JFileChooser;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.io.MDLReader;

import net.sf.structure.cdk.paint.ColorScheme;
import net.sf.structure.cdk.paint.DefaultImagePainter;
import net.sf.structure.cdk.paint.ImagePainter;
import net.sf.structure.cdk.paint.Painter;
import net.sf.structure.cdk.paint.SwingPainter;
import net.sf.structure.cdk.util.ImageKit;

/**
 * A visual test of layout and rendering capabilities.
 * 
 * @author Richard Apodaca
 */
public class VisualTest
{
  private ImagePainter imagePainter;
  private TestFrame frame;
  private StructureDiagramGenerator sdg;
  //private StructureBuilder builder;
  private String dir;

  /**
   * Do not construct directly. Use the <code>main</code> method instead.
   */
  private VisualTest()
  {
    imagePainter = new DefaultImagePainter();
    frame = new TestFrame();
    sdg = new StructureDiagramGenerator();
    dir = System.getProperty("user.dir");
  }

  private void run()
  {
    imagePainter.setBackgroundColor(Color.WHITE);
    frame.setSize(400, 400);
    frame.show();
  }

  /**
   * Main method.
   * 
   * @param args not used
   */
  public static void main(String[] args)
  {
    VisualTest test = new VisualTest();

    test.run();
  }

  private class TestFrame extends JFrame
  {
    private static final long serialVersionUID = 1;

    private SwingPainter painter;

    private TestFrame()
    {
      super();

      painter = null;

      initGlobal();
      initMenu();
      initPainter();
    }

    private void initGlobal()
    {
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setTitle("Structure Visual Testing Framework");
      getContentPane().setBackground(Color.WHITE);
    }

    private void initMenu()
    {
      JMenuBar menuBar = new JMenuBar();
      JMenu file = new JMenu("File");
      JMenu edit = new JMenu("Edit");
      JMenu structure = new JMenu("Structure");
      JMenu createImage = new JMenu("Image");

      file.add(new FileOpenAction());
      file.add(new FileSaveAsSVGAction());
      file.add(new FileExitAction());
      edit.add(new EditPreferencesAction());
      
      addTestStructures(structure);
      
      createImage.add(new ImageAction("png", 400, 400));
      createImage.add(new ImageAction("jpg", 400, 400));

      menuBar.add(file);
      menuBar.add(edit);
      menuBar.add(structure);
      menuBar.add(createImage);

      setJMenuBar(menuBar);
    }

    private void addTestStructures(JMenu menu)
    {
      Method[] methods = MoleculeFactory.class.getDeclaredMethods();

      for (int i = 0; i < methods.length; i++)
      {
        if (methods[i].getName().startsWith("make") && !methods[i].getName().equals("makeAlkane"))
        {
          ViewStructureAction action = new ViewStructureAction(methods[i]);

          menu.add(action);
        }
      }
    }

    private void initPainter()
    {
      this.painter = new SwingPainter();

      getContentPane().add("Center", painter);
    }

    private void setStructure(IMolecule structure)
    {
      painter.setAtomContainer(structure);
    }
    
    private IAtomContainer getStructure()
    {
      return painter.getAtomContainer();
    }
  }
  
  private class FileOpenAction extends AbstractAction
  {
    private static final long serialVersionUID = 1;
    
    private FileOpenAction()
    {
      super("Open...");
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
      JFileChooser chooser = new JFileChooser();
      
      chooser.setCurrentDirectory(new File(dir));
      
      int returnVal = chooser.showOpenDialog(VisualTest.this.frame);
      
      if (returnVal == JFileChooser.APPROVE_OPTION)
      {
        try
        {
          FileReader reader = new FileReader(chooser.getSelectedFile());
          MDLReader mdlReader = new MDLReader(reader);
          IMolecule structure = (IMolecule) mdlReader.read(new org.openscience.cdk.Molecule());
          frame.setStructure(structure);
          imagePainter.setAtomContainer(structure);
        }
        
        catch (Exception err)
        {
          err.printStackTrace();
        }
      }
      
      dir = chooser.getCurrentDirectory().getAbsolutePath();
    }
  }
  
  private class FileSaveAsSVGAction extends AbstractAction
  {
    private FileSaveAsSVGAction()
    {
      super("Save As SVG...");
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
      if (frame.getStructure() == null)
      {
        return;
      }
      
      JFileChooser chooser = new JFileChooser();
      
      chooser.setCurrentDirectory(new File(dir));
      
      int returnVal = chooser.showSaveDialog(VisualTest.this.frame);
      
      if (returnVal == JFileChooser.APPROVE_OPTION)
      {
        try
        {
          ImageKit.writeSVG(frame.getStructure(), frame.getWidth(), frame.getHeight(), chooser.getSelectedFile().getAbsolutePath());
        }
        
        catch (Exception err)
        {
          err.printStackTrace();
        }
      }
    }
  }

  private class FileExitAction extends AbstractAction
  {
    private static final long serialVersionUID = 1;

    private FileExitAction()
    {
      super("Exit");
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
      frame.dispose();
    }
  }

  private class EditPreferencesAction extends AbstractAction
  {
    private static final long serialVersionUID = 1;

    private EditPreferencesAction()
    {
      super("Preferences");
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
      final JDialog dialog = new JDialog(frame, "Preferences", true);
      StylePanel stylePanel = new StylePanel();

      dialog.getContentPane().add(stylePanel);
      stylePanel.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          dialog.dispose();
        }
      });

      dialog.pack();
      dialog.setResizable(false);
      dialog.setLocationRelativeTo(frame);
      dialog.show();
    }
  }

  private class ViewStructureAction extends AbstractAction
  {
    private static final long serialVersionUID = 1;
    private Method method;

    private ViewStructureAction(Method method)
    {
      super(method.getName().substring(4));

      this.method = method;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
      IMolecule structure = null;
      
      try
      {
        structure = (IMolecule) method.invoke(null, new Object[] { });
        
        sdg.setMolecule(structure);
        sdg.generateCoordinates();
      }

      catch (Exception ex)
      {
        throw new RuntimeException(ex);
      }
      
      structure = sdg.getMolecule();
      
      frame.setStructure(structure);
      imagePainter.setAtomContainer(structure);
    }
  }
  
  private class ImageAction extends AbstractAction
  {
    private String imageType;
    private int width;
    private int height;
    
    private ImageAction(String imageType, int width, int height)
    {
      super("Create " + imageType + " image (" + String.valueOf(width) + "x" + String.valueOf(height));
      
      this.imageType = imageType;
      this.width = width;
      this.height = height;
    }
    
    public void actionPerformed(ActionEvent e)
    {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      
      imagePainter.paint(image);
      
      try
      {
        ImageIO.write(image, imageType, output);
      }
      
      catch (IOException err)
      {
        throw new RuntimeException(err);
      }
      
      ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
      
      try
      {
        image = ImageIO.read(input);
      }
      
      catch (IOException err)
      {
        throw new RuntimeException(err);
      }
      
      ImageViewer viewer = new ImageViewer("View" + imageType + " Image", image);
      
      viewer.show();
    }
  }
  
  private class ImageViewer extends JFrame
  {
    private ImageViewer(String title, BufferedImage image)
    {
      super(title);
      
      ImageComponent painter = new ImageComponent(image);
      
      this.getContentPane().add("Center", painter);
      
      setBounds(30, 30, 400, 400);
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
  }
  
  private class ImageComponent extends JComponent
  {
    private BufferedImage image;
    
    private ImageComponent(BufferedImage image)
    {
      this.image = image;
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     */
    public void paint(Graphics g)
    {
      super.paint(g);
      
      g.drawImage(image, 0, 0, Color.WHITE, null);
    }
  }

  public class StylePanel extends JPanel implements Painter.SettingsExporter,
      Painter.SettingsImporter
  {
    private static final long serialVersionUID = 1;

    private EventListenerList listenerList;
    private JTextField lineThickness;
    private JTextField lineSpacing;
    private JTextField atomHeight;
    private JCheckBox antialiasing;
    private ActionEvent styleAcceptedEvent;
    private ActionEvent styleCanceledEvent;
    private ColorScheme colorScheme;

    /**
     * Constructs a fully functional <code>StylePanel</code> from the
     * specified <code>Style</code>.
     * 
     * @param style
     *          the <code>Style</code> to be used
     */
    private StylePanel()
    {
      super();

      listenerList = new EventListenerList();

      setLayout(new GridBagLayout());

      GridBagConstraints constraints = new GridBagConstraints();

      constraints.fill = GridBagConstraints.HORIZONTAL;

      JButton acceptButton = new JButton(new AcceptAction());
      JButton cancelButton = new JButton(new CancelAction());

      lineThickness = new JTextField();
      atomHeight = new JTextField();
      lineSpacing = new JTextField();
      antialiasing = new JCheckBox();

      lineThickness.setColumns(4);
      atomHeight.setColumns(4);
      lineSpacing.setColumns(4);

      constraints.weightx = 0.5;

      constraints.gridx = 0;
      constraints.gridy = 0;
      add(new JLabel("Line Thickness"), constraints);

      constraints.gridx = 1;
      constraints.gridy = 0;
      add(lineThickness, constraints);

      constraints.gridx = 0;
      constraints.gridy = 1;
      add(new JLabel("Line Spacing"), constraints);

      constraints.gridx = 1;
      constraints.gridy = 1;
      add(lineSpacing, constraints);

      constraints.gridx = 0;
      constraints.gridy = 2;
      add(new JLabel("Atom Height"), constraints);

      constraints.gridx = 1;
      constraints.gridy = 2;
      add(atomHeight, constraints);

      constraints.gridx = 0;
      constraints.gridy = 3;
      add(new JLabel("Antialiasing"), constraints);

      constraints.gridx = 1;
      constraints.gridy = 3;
      add(antialiasing, constraints);

      constraints.insets = new Insets(10, 0, 0, 0);

      constraints.gridx = 0;
      constraints.gridy = 4;
      add(acceptButton, constraints);

      constraints.gridx = 1;
      constraints.gridy = 4;
      add(cancelButton, constraints);
      
      frame.painter.exportSettings(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.structure.presentation.Style.Importer#getAntialiasing()
     */
    public boolean getAntialiasing()
    {
      return antialiasing.isSelected();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.structure.presentation.Style.Importer#getAtomLabelHeight()
     */
    public double getAtomLabelHeight()
    {
      return Double.parseDouble(atomHeight.getText());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.structure.presentation.Style.Importer#getLineSpacing()
     */
    public double getLineSpacing()
    {
      return Double.parseDouble(lineSpacing.getText());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.structure.presentation.Style.Importer#getLineThickness()
     */
    public double getLineThickness()
    {
      return Double.parseDouble(lineThickness.getText());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.structure.presentation.Style.Exporter#setAntialiasing(boolean)
     */
    public void setAntialiasing(boolean antialiasing)
    {
      this.antialiasing.setSelected(antialiasing);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.structure.presentation.Style.Exporter#setAtomLabelHeight(double)
     */
    public void setAtomLabelHeight(double atomHeight)
    {
      this.atomHeight.setText(String.valueOf(atomHeight));
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.structure.presentation.Style.Exporter#setLineSpacing(double)
     */
    public void setLineSpacing(double lineSpacing)
    {
      this.lineSpacing.setText(String.valueOf(lineSpacing));
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.structure.presentation.Style.Exporter#setLineThickness(double)
     */
    public void setLineThickness(double lineThickness)
    {
      this.lineThickness.setText(String.valueOf(lineThickness));
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.structure.presentation.Style.Importer#getColorScheme()
     */
    public ColorScheme getColorScheme()
    {
      return colorScheme;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.structure.presentation.Style.Exporter#setColorScheme(net.sf.structure.presentation.ColorScheme)
     */
    public void setColorScheme(ColorScheme scheme)
    {
      this.colorScheme = scheme;
    }

    /**
     * Adds an <code>ActionListener</code> to the style panel.
     * 
     * @param listener
     *          the listener to be added
     */
    public void addActionListener(ActionListener listener)
    {
      listenerList.add(ActionListener.class, listener);
    }

    /**
     * Removes an <code>ActionListener</code> from the style panel.
     * 
     * @param listener
     *          the listener to be removed
     */
    public void removeActionListener(ActionListener listener)
    {
      listenerList.remove(ActionListener.class, listener);
    }

    /**
     * Called in response to the user clicking on the "OK" button.
     */
    protected void fireStyleAccepted()
    {
      Object[] listeners = listenerList.getListenerList();

      // Process the listeners last to first, notifying
      // those that are interested in this event
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
        if (listeners[i] == ActionListener.class)
        {
          // Lazily create the event:
          if (styleAcceptedEvent == null)
          {
            styleAcceptedEvent = new ActionEvent(this,
                ActionEvent.ACTION_PERFORMED, "approve");
          }

          ((ActionListener) listeners[i + 1])
              .actionPerformed(styleCanceledEvent);
        }
      }
    }

    /**
     * Called in response to the user clicking on the "Cancel" button.
     */
    protected void fireStyleCanceled()
    {
      Object[] listeners = listenerList.getListenerList();

      // Process the listeners last to first, notifying
      // those that are interested in this event
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
        if (listeners[i] == ActionListener.class)
        {
          // Lazily create the event:
          if (styleCanceledEvent == null)
          {
            styleCanceledEvent = new ActionEvent(this,
                ActionEvent.ACTION_PERFORMED, "cancel");
          }

          ((ActionListener) listeners[i + 1])
              .actionPerformed(styleCanceledEvent);
        }
      }
    }

    private class AcceptAction extends AbstractAction
    {
      private AcceptAction()
      {
        super("OK");
      }

      public void actionPerformed(ActionEvent e)
      {
        frame.painter.importSettings(StylePanel.this);
        imagePainter.importSettings(StylePanel.this);
        imagePainter.setBackgroundColor(VisualTest.this.frame.getBackground());

        fireStyleAccepted();
      }
    }

    private class CancelAction extends AbstractAction
    {
      private CancelAction()
      {
        super("Cancel");
      }

      public void actionPerformed(ActionEvent e)
      {
        fireStyleCanceled();
      }
    }
  }
}
