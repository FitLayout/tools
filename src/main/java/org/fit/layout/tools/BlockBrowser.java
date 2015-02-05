/**
 * 
 */
package org.fit.layout.tools;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Vector;

import org.fit.cssbox.layout.BrowserCanvas;
import org.fit.cssbox.layout.BrowserConfig;
import org.fit.layout.api.AreaTreeProvider;
import org.fit.layout.api.BoxTreeProvider;
import org.fit.layout.api.OutputDisplay;
import org.fit.layout.classify.FeatureVector;
import org.fit.layout.gui.AreaSelectionListener;
import org.fit.layout.gui.Browser;
import org.fit.layout.gui.BrowserPlugin;
import org.fit.layout.model.Area;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.Box;
import org.fit.layout.model.Page;
import org.fit.layout.model.Tag;
import org.fit.layout.process.ScriptableProcessor;

import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import java.awt.GridBagConstraints;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.GridLayout;

import javax.swing.JTree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.GridBagLayout;

import javax.swing.JList;
import javax.swing.JToggleButton;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.FlowLayout;

import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;

import java.awt.Insets;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JCheckBox;

/**
 * @author burgetr
 *
 */
public class BlockBrowser implements Browser
{
    public static BlockBrowser browser;

    public static final float TAG_PROBABILITY_THRESHOLD = 0.3f; 
    
    private BrowserConfig config;
    private GUIProcessor proc;
    private Page page;
    /*private BoxTree btree;
    private LogicalTree ltree;
    private FeatureAnalyzer features;
    private ArticleExtractor ex;
    private TreeTagger tagger;
    private SingleClassStyleAnalyzer ssa;
    private AreaTreeStyleAnalyzer msa;
    private ExtractedStyleAnalyzer esa;
    private TagPredictor tpred;
    private Vector<EvalData> evalData;*/
    private File saveDir = null;
    private URL currentUrl = null;
    private boolean dispFinished = false;
    private boolean areasync = true;
    private boolean logsync = true;
    private List<AreaSelectionListener> areaListeners;

    private JFrame mainWindow = null;  //  @jve:decl-index=0:visual-constraint="-239,28"
    private JPanel container = null;
    private JPanel mainPanel = null;
    private JPanel contentPanel = null;
    private JPanel structurePanel = null;
    private JPanel statusPanel = null;
    private JTextField statusText = null;
    private JLabel jLabel = null;
    private JButton okButton = null;
    private JTabbedPane sidebarPane = null;
    private JPanel boxTreePanel = null;
    private JScrollPane boxTreeScroll = null;
    private JTree boxTree = null;
    private JScrollPane contentScroll = null;
    private JPanel contentCanvas = null;
    private JSplitPane mainSplitter = null;
    private JToolBar showToolBar = null;
    private JButton redrawButton = null;
	private JPanel areaTreePanel = null;
	private JScrollPane areaTreeScroll = null;
	private JTree areaJTree = null;
	private JPanel sepListPanel = null;
	private JScrollPane sepScroll = null;
	private JList sepList = null;
    private JToggleButton lookupButton = null;
    private JToggleButton extractButton = null;
    private JToggleButton boxLookupButton = null;
    private JButton showSepButton = null;
    private JButton showBoxButton = null;
    private JButton showAreaButton = null;
    private JToolBar lookupToolBar = null;
    private JPanel toolPanel = null;
    private JToolBar fileToolBar = null;
    private JButton saveButton = null;
    private JButton gridButton = null;
    private JPanel jPanel = null;
    private JScrollPane logicalTreeScroll = null;
    private JTree logicalTree = null;
    private JButton refreshButton = null;
    private JSplitPane infoSplitter = null;
    private JPanel objectInfoPanel = null;
    private JScrollPane objectInfoScroll = null;
    private JTable infoTable = null;
    private JButton showArtAreaButton = null;
    private JButton showColumnsButton = null;
    private JScrollPane featureScroll = null;
    private JTable featureTable = null;
    private JButton saveLogicalButton = null;
    private JTextField markednessText;
    private JPanel pathsPanel;
    private JScrollPane pathListScroll;
    private JScrollPane extractionScroll;
    private JTable extractionTable;
    private JFrame treeCompWindow;
    private JToggleButton sepLookupButton;
    private JScrollPane probabilityScroll;
    private JTable probTable;
    private JButton saveRDFButton;
    private JTabbedPane toolTabs;
    private JPanel sourcesTab;
    private JLabel rendererLabel;
    private JComboBox<BoxTreeProvider> rendererCombo;
    private JPanel rendererChoicePanel;
    private JPanel rendererParamsPanel;
    private JPanel segmChoicePanel;
    private JPanel segmParamsPanel;
    private JLabel lblSegmentator;
    private JComboBox<AreaTreeProvider> segmentatorCombo;
    private JCheckBox segmAutorunCheckbox;
    private JButton segmRunButton;
    private JButton btnOperators;
    private JFrame operatorWindow;


    public BlockBrowser()
    {
        config = new BrowserConfig();
        areaListeners = new LinkedList<AreaSelectionListener>();
        saveDir = new File("/home/burgetr/local/rdf");
        proc = new GUIProcessor() {
            protected void treesCompleted()
            {
                refresh();
            }
        };
    }
    
    //===========================================================================
    
    public void setLocation(String url)
    {
        ((ParamsPanel) rendererParamsPanel).setParam("url", url);
        displayURL(url);
    }
    
    public String getLocation()
    {
        return currentUrl.toString();
    }

    public void setLoadImages(boolean b)
    {
        config.setLoadImages(b);
    }
    
    public boolean getLoadImages()
    {
        return config.getLoadImages();
    }
    
    /**
     * Refresh the trees.
     */
    public void refresh()
    {
        boxTree.setModel(new BoxTreeModel(proc.getPage().getRoot()));
        areaJTree.setModel(new AreaTreeModel(proc.getAreaTree().getRoot()));
        //logicalTree.setModel(new DefaultTreeModel(proc.getLogicalTree().getRoot()));
    }
    
    //=============================================================================================================
    
    @Override
    public void addToolBar(JToolBar toolbar)
    {
        toolPanel.add(toolbar);
        toolPanel.updateUI();
    }

    @Override
    public void addToolPanel(String title, JComponent component)
    {
        toolTabs.addTab(title, component);
    }

    @Override
    public void addStructurePanel(String title, JComponent component)
    {
        sidebarPane.addTab(title, component);
    }

    @Override
    public void addInfoPanel(JComponent component, double weighty)
    {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 0, 5, 0);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.weightx = 1.0;
        constraints.weighty = weighty;
        constraints.gridx = 0;
        
        objectInfoPanel.add(component, constraints);
    }

    @Override
    public OutputDisplay getOutputDisplay()
    {
        return ((BrowserPanel) contentCanvas).getOutputDisplay();
    }

    @Override
    public void updateDisplay()
    {
        contentCanvas.repaint();
    }

    @Override
    public Area getSelectedArea()
    {
        if (areaJTree == null)
            return null;
        else                   
            return (Area) areaJTree.getLastSelectedPathComponent();
    }

    @Override
    public void addAreaSelectionListener(AreaSelectionListener listener)
    {
        areaListeners.add(listener);
    }
    
    @Override
	public void setPage(Page page) 
    {
    	
    	this.page = page;
    	contentCanvas = createContentCanvas();
        
        contentCanvas.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e)
            {
                System.out.println("Click: " + e.getX() + ":" + e.getY());
                canvasClick(e.getX(), e.getY());
            }
            public void mousePressed(MouseEvent e) { }
            public void mouseReleased(MouseEvent e) { }
            public void mouseEntered(MouseEvent e) { }
            public void mouseExited(MouseEvent e) 
            {
                statusText.setText("");
            }
        });
        contentCanvas.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) { }
            public void mouseMoved(MouseEvent e) 
            { 
                String s = "Absolute: " + e.getX() + ":" + e.getY();
                Area node = (Area) areaJTree.getLastSelectedPathComponent();
                if (node != null)
                {
                    Area area = (Area) node;
                    int rx = e.getX() - area.getX1();
                    int ry = e.getY() - area.getY1();
                    s += "  Relative: " + rx + ":" + ry;
                    /*if (area.getBounds().contains(e.getX(), e.getY()))
                    {
                        AreaGrid grid = area.getGrid();
                        if (grid != null)
                        {
                            int gx = grid.findCellX(e.getX());
                            int gy = grid.findCellY(e.getY());
                            s += "  Grid: " + gx + ":" + gy;
                        }
                    }*/
                }
                statusText.setText(s);
            }
        });
        contentScroll.setViewportView(contentCanvas);
        
        boxTree.setModel(new BoxTreeModel(proc.getPage().getRoot()));
        //proc.segmentPage(page); //TODO jinak

        dispFinished = true;
        saveButton.setEnabled(true);
        saveLogicalButton.setEnabled(true);
        saveRDFButton.setEnabled(true);
	}

	@Override
	public Page getPage() 
	{
		return page;
	}
    
    @Override
    public AreaTree getAreaTree()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAreaTree(AreaTree areaTree)
    {
        // TODO Auto-generated method stub
        
    }

    //=============================================================================================================
    
    public void displayURL(String urlstring)
    {
        dispFinished = false;
        saveButton.setEnabled(false);
        saveLogicalButton.setEnabled(false);
        saveRDFButton.setEnabled(false);
        if (treeCompWindow != null)
        {
            treeCompWindow.setVisible(false);
            treeCompWindow.dispose();
            treeCompWindow = null;
        }
        
        try {
            if (!urlstring.startsWith("http:") &&
                !urlstring.startsWith("ftp:") &&
                !urlstring.startsWith("file:"))
                    urlstring = "http://" + urlstring;

            //page = proc.renderPage(urlstring, contentScroll.getSize());
            
            int i = rendererCombo.getSelectedIndex();
            if (i != -1)
            {
                BoxTreeProvider btp = rendererCombo.getItemAt(i);
                page = proc.renderPage(btp, ((ParamsPanel) rendererParamsPanel).getParams());
                setPage(page);
                
                if (segmAutorunCheckbox.isSelected())
                {
                    //TODO segmentation here
                }
            }
            
            
        } catch (Exception e) {
            System.err.println("*** Error: "+e.getMessage());
            e.printStackTrace();
        }
    }
    
    /** Creates the appropriate canvas based on the file type */
    private JPanel createContentCanvas()
    {
        if (contentCanvas != null)
            contentCanvas = new BrowserPanel(page);
        return contentCanvas;
    }
    
    /** This is called when the browser canvas is clicked */
    private void canvasClick(int x, int y)
    {
        if (lookupButton.isSelected())
        {
            Area node = proc.getAreaTree().getAreaAt(x, y);
            if (node != null)
            {
                showAreaInTree(node);
                showAreaInLogicalTree(node);
            }
            //lookupButton.setSelected(false);
        }
        if (boxLookupButton.isSelected())
        {
            Box node = proc.getPage().getBoxAt(x, y);
            if (node != null)
                showBoxInTree(node);
            //boxLookupButton.setSelected(false);
        }
        /*if (sepLookupButton.isSelected())
        {
            showSeparatorAt(x, y);
        }
        if (extractButton.isSelected())
        {
            AreaNode node = proc.getAreaTree().getAreaAt(x, y);
            if (node != null)
            {
                proc.getExtractor().findArticleBounds(node);
                try {
                    PrintStream exs = new PrintStream(new FileOutputStream("test/extract.html"));
                    proc.getExtractor().dumpTo(exs);
                    exs.close();
                } catch (java.io.IOException e) {
                    System.err.println("Output failed: " + e.getMessage());
                }
                
                //String s = ex.getDescriptionX(node, 2);
                //System.out.println("Extracted: " + s);
            }
            extractButton.setSelected(false);
        }*/
    }
    
    private void showBoxInTree(Box node)
    {
        //find the path to root
        int len = 0;
        for (Box b = node; b != null; b = b.getParentBox())
            len++;
        Box[] path = new Box[len];
        for (Box b = node; b != null; b = b.getParentBox())
            path[--len] = b;
        
        TreePath select = new TreePath(path);
        boxTree.setSelectionPath(select);
        //boxTree.expandPath(select);
        boxTree.scrollPathToVisible(new TreePath(path));
    }
    
    private void showAreaInTree(Area node)
    {
        //find the path to root
        int len = 0;
        for (Area a = node; a != null; a = a.getParentArea())
            len++;
        Area[] path = new Area[len];
        for (Area a = node; a != null; a = a.getParentArea())
            path[--len] = a;
        
        TreePath select = new TreePath(path);
        areaJTree.setSelectionPath(select);
        //areaTree.expandPath(select);
        areaJTree.scrollPathToVisible(new TreePath(path));
    }
    
    private void showAreaInLogicalTree(Area node)
    {
        /*LogicalNode lnode = proc.getLogicalTree().findArea(node);
        if (lnode != null)
        {
            TreePath select = new TreePath(lnode.getPath());
            logicalTree.setSelectionPath(select);
            //logicalTree.expandPath(select);
            logicalTree.scrollPathToVisible(new TreePath(lnode.getPath()));
        }*/
    }
    
    private void displayAreaInfo(Area area)
    {
        Vector<String> cols = infoTableData("Property", "Value");
        
        Vector<Vector <String>> vals = new Vector<Vector <String>>();
        //vals.add(infoTableData("Layout", area.getLayoutType().toString()));
        vals.add(infoTableData("GP", area.getTopology().getPosition().toString()));
        vals.add(infoTableData("Tags", tagProbabilityString(area.getTags())));
        //if (proc.getVisualClassifier() != null)
        //    vals.add(infoTableData("V. class", proc.getVisualClassifier().classifyArea(area)));
        //vals.add(infoTableData("Style probs", tagProbabilityString(proc.getMsa() != null ? proc.getMsa().classifyNode(area) : null)));
        //vals.add(infoTableData("Total probs", tagProbabilityString(proc.getTagPredictor() != null ? proc.getTagPredictor().getTagProbabilities(area) : null)));
        //vals.add(infoTableData("Importance", String.valueOf(area.getImportance())));
        //vals.add(infoTableData("Separated", (area.isSeparated()) ? "true" : "false"));
        //vals.add(infoTableData("Atomic", (area.isAtomic()) ? "true" : "false"));
        vals.add(infoTableData("Indent scale", area.getTopology().getMinIndent() + " - " + area.getTopology().getMaxIndent()));
        //vals.add(infoTableData("Indent value", String.valueOf(proc.getFeatures().getIndentation(area))));
        //vals.add(infoTableData("Centered", (area.isCentered()) ? "true" : "false"));
        //vals.add(infoTableData("Coherent", (area.isCoherent()) ? "true" : "false"));
        //vals.add(infoTableData("Parent perc.", String.valueOf(area.getParentPercentage())));
        
        //vals.add(infoTableData("Name", area.getName()));
        vals.add(infoTableData("Bounds", area.getBounds().toString()));
        //vals.add(infoTableData("Content", (a.getContentBounds() == null) ? "" : a.getContentBounds().toString()));
        //vals.add(infoTableData("Level", String.valueOf(a.getLevel())));
        vals.add(infoTableData("Borders", borderString(area)));
        vals.add(infoTableData("Bg separated", (area.isBackgroundSeparated()) ? "true" : "false"));
        //vals.add(infoTableData("Is hor. sep.", (area.isHorizontalSeparator()) ? "true" : "false"));
        //vals.add(infoTableData("Is vert. sep.", (area.isVerticalSeparator()) ? "true" : "false"));
        vals.add(infoTableData("Avg. fsize", String.valueOf(area.getFontSize())));
        vals.add(infoTableData("Avg. fweight", String.valueOf(area.getFontWeight())));
        vals.add(infoTableData("Avg. fstyle", String.valueOf(area.getFontStyle())));
        //vals.add(infoTableData("Decl. fsize", String.valueOf(area.getDeclaredFontSize())));
        //vals.add(infoTableData("Luminosity", String.valueOf(area.getColorLuminosity())));
        //vals.add(infoTableData("Start color", colorString(a.getBoxes().firstElement().getStartColor())));
        vals.add(infoTableData("Bg color", colorString(area.getBackgroundColor())));
        
        //vals.add(infoTableData("Fg color", colorString(area.getBoxes().firstElement().getColor())));
        
        //markednessText.setText(String.format("%.2f", proc.getFeatures().getMarkedness(area)));

        //classification result
        displayProbabilityTable(area);
        
        /*Vector<Vector <String>> fvals = new Vector<Vector <String>>();
        FeatureVector f = proc.getFeatures().getFeatureVector(area);
        if (f != null)
        {
            Method[] methods = f.getClass().getMethods();
            for (Method m : methods)
            {
                try
                {
                    if (m.getName().startsWith("get") && !m.equals("getClass"))
                    {
                        Object ret = m.invoke(f, (Object []) null);
                        if (ret != null)
                            fvals.add(infoTableData(m.getName().substring(3), ret.toString()));
                    }
                    if (m.getName().startsWith("is"))
                    {
                        Object ret = m.invoke(f, (Object []) null);
                        if (ret != null)
                            fvals.add(infoTableData(m.getName().substring(2), ret.toString()));
                    }
                } catch (Exception e) {}
            }
        }*/
        
        DefaultTableModel tab = new DefaultTableModel(vals, cols);
        infoTable.setModel(tab);
        //DefaultTableModel ftab = new DefaultTableModel(fvals, cols);
        //featureTable.setModel(ftab);
    }
    
    private String borderString(Area a)
    {
        String bs = "";
        if (a.hasTopBorder()) bs += "^";
        if (a.hasLeftBorder()) bs += "<";
        if (a.hasRightBorder()) bs += ">";
        if (a.hasBottomBorder()) bs += "_";
        return bs;
    }
    
    private String colorString(java.awt.Color color)
    {
        if (color == null)
            return "- transparent -";
        else
            return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
    
    private String tagString(Map<Tag, Float> tags)
    {
        String ret = "";
        for (Map.Entry<Tag, Float> entry : tags.entrySet())
            ret += entry.getKey() + " ";
        return ret;
    }
    
    private String tagProbabilityString(Map<Tag, Float> map)
    {
        String ret = "";
        if (map != null)
        {
            for (Map.Entry<Tag, Float> entry : map.entrySet())
            {
                if (entry.getValue() > TAG_PROBABILITY_THRESHOLD)
                    ret += entry.getKey() + " (" + String.format("%1.2f", entry.getValue()) + ") "; 
            }
        }
        return ret;
    }
    
    private Vector<String> infoTableData(String prop, String value)
    {
        Vector<String> cols = new Vector<String>(2);
        cols.add(prop);
        cols.add(value);
        return cols;
    }
    
    private void displayProbabilityTable(Area area)
    {
        /*List<Tag> tags = proc.getTagger().getAllTags();
        Vector<String> cols = new Vector<String>();
        cols.add("Source");
        for (Tag tag : tags)
            cols.add(tag.toString());
        
        Vector<Vector <String>> vals = new Vector<Vector <String>>();
        //tags
        Vector<String> tprob = new Vector<String>();
        tprob.add("Tag");
        for (Tag tag : tags)
        {
            double p = 0.0;
            if (area.hasTag(tag))
                p = tag.getSource().getRelevance();
            tprob.add(String.format("%1.2f", p));
        }
        vals.add(tprob);
        //classifiers
        if (proc.getMsa() != null)
            vals.add(getProbTableLine("m.class", tags, proc.getMsa().classifyNode(area)));
        if (proc.getEsa() != null)
            vals.add(getProbTableLine("extr", tags, proc.getEsa().classifyNode(area)));
        if (proc.getTagPredictor() != null)
            vals.add(getProbTableLine("total", tags, proc.getTagPredictor().getTagProbabilities(area)));
                
        probTable.setModel(new DefaultTableModel(vals, cols));*/
    }
    
    private Vector<String> getProbTableLine(String title, List<Tag> tags, Map<Tag, Double> data)
    {
        Vector<String> ret = new Vector<String>();
        ret.add(title);
        for (Tag tag : tags)
        {
            if (data.containsKey(tag))
                ret.add(String.format("%1.2f", data.get(tag)));
            else
                ret.add("");
        }
        return ret;
    }
    
    private void showArea(Area area)
    {
        ((BrowserPanel) contentCanvas).getOutputDisplay().drawExtent(area);
        contentCanvas.repaint();
        
        //show the info table
        displayAreaInfo(area);

        //notify area listeners
        for (AreaSelectionListener listener : areaListeners)
            listener.areaSelected(area);
        
        //show the separator list
        /*SeparatorSet sset = Config.createSeparators(anode);
        DefaultListModel ml = new DefaultListModel();
        for (Separator sep : sset.getHorizontal())
            ml.addElement(sep);
        for (Separator sep : sset.getVertical())
            ml.addElement(sep);
        for (Separator sep : sset.getBoxsep())
            ml.addElement(sep);
        sepList.setModel(ml);
        
        //debug joining
        AreaNode p = anode.getParentArea();
        if (p != null)
            p.debugAreas(anode);*/
    }

    @SuppressWarnings("unchecked")
    public void showAreas(Area node, String name)
    {
        /*Enumeration<AreaNode> en = node.postorderEnumeration();
        while (en.hasMoreElements())
        {
            AreaNode child = en.nextElement();
            if (child.getArea() != null && (name == null || child.getArea().toString().contains(name)))
                child.getArea().drawExtent((BrowserCanvas) contentCanvas);
        }
        contentCanvas.repaint();*/
    }
    
    public BrowserCanvas getBrowserCanvas()
    {
        return (BrowserCanvas) contentCanvas;
    }
    
    private void initPlugins()
    {
        ServiceLoader<BrowserPlugin> loader = ServiceLoader.load(BrowserPlugin.class);
        Iterator<BrowserPlugin> it = loader.iterator();
        while (it.hasNext())
        {
            BrowserPlugin plugin = it.next();
            System.out.println("Init plugin: " + plugin.getClass().getName());
            plugin.init(this);
        }
    }
    
    //===========================================================================
    
    /**
     * This method initializes jFrame	
     * 	
     * @return javax.swing.JFrame	
     */
    private JFrame getMainWindow()
    {
        if (mainWindow == null)
        {
            mainWindow = new JFrame();
            mainWindow.setTitle("Visual Block Browser");
            mainWindow.setVisible(true);
            mainWindow.setBounds(new Rectangle(0, 0, 1489, 256));
            mainWindow.setMinimumSize(new Dimension(1200, 256));
            mainWindow.setContentPane(getContainer());
            mainWindow.addWindowListener(new java.awt.event.WindowAdapter()
            {
                public void windowClosing(java.awt.event.WindowEvent e)
                {
                    mainWindow.setVisible(false);
                    System.exit(0);
                }
            });
        }
        return mainWindow;
    }

    private JPanel getContainer()
    {
        if (container == null)
        {
            container = new JPanel();
            container.setLayout(new BorderLayout());
            container.add(getToolPanel(), BorderLayout.NORTH);
            container.add(getMainPanel(), BorderLayout.CENTER);
        }
        return container;
    }
    
    private JPanel getMainPanel()
    {
        if (mainPanel == null)
        {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridy = -1;
            gridBagConstraints2.anchor = GridBagConstraints.WEST;
            gridBagConstraints2.gridx = -1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.weighty = 1.0;
            gridBagConstraints11.insets = new Insets(0, 0, 5, 0);
            gridBagConstraints11.gridy = 1;
            gridBagConstraints11.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.weightx = 1.0;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridwidth = 1;
            gridBagConstraints3.gridy = 2;
            mainPanel = new JPanel();
            GridBagLayout gbl_mainPanel = new GridBagLayout();
            mainPanel.setLayout(gbl_mainPanel);
            GridBagConstraints gbc_toolTabs = new GridBagConstraints();
            gbc_toolTabs.fill = GridBagConstraints.HORIZONTAL;
            gbc_toolTabs.weightx = 1.0;
            gbc_toolTabs.insets = new Insets(0, 0, 5, 0);
            gbc_toolTabs.gridx = 0;
            gbc_toolTabs.gridy = 0;
            mainPanel.add(getToolTabs(), gbc_toolTabs);
            mainPanel.add(getMainSplitter(), gridBagConstraints11);
            mainPanel.add(getStatusPanel(), gridBagConstraints3);
        }
        return mainPanel;
    }

    /**
     * This method initializes jPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getContentPanel()
    {
        if (contentPanel == null)
        {
            GridLayout gridLayout1 = new GridLayout();
            gridLayout1.setRows(1);
            contentPanel = new JPanel();
            contentPanel.setLayout(gridLayout1);
            contentPanel.add(getContentScroll(), null);
        }
        return contentPanel;
    }

    /**
     * This method initializes jPanel1	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getStructurePanel()
    {
        if (structurePanel == null)
        {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            structurePanel = new JPanel();
            structurePanel.setPreferredSize(new Dimension(200, 408));
            structurePanel.setLayout(gridLayout);
            structurePanel.add(getSidebarPane(), null);
        }
        return structurePanel;
    }

    /**
     * This method initializes jPanel2	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getStatusPanel()
    {
        if (statusPanel == null)
        {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.insets = new java.awt.Insets(0,7,0,0);
            gridBagConstraints4.gridy = 2;
            statusPanel = new JPanel();
            statusPanel.setLayout(new GridBagLayout());
            statusPanel.add(getStatusText(), gridBagConstraints4);
        }
        return statusPanel;
    }

    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getStatusText()
    {
        if (statusText == null)
        {
            statusText = new JTextField();
            statusText.setEditable(false);
            statusText.setText("Browser ready.");
        }
        return statusText;
    }

    /**
     * This method initializes jButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getOkButton()
    {
        if (okButton == null)
        {
            okButton = new JButton();
            okButton.setText("Go!");
            okButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    Object urlval = ((ParamsPanel) rendererParamsPanel).getParam("url");
                    if (urlval != null)
                        displayURL(urlval.toString());
                }
            });
        }
        return okButton;
    }

    /**
     * This method initializes jTabbedPane	
     * 	
     * @return javax.swing.JTabbedPane	
     */
    private JTabbedPane getSidebarPane()
    {
        if (sidebarPane == null)
        {
            sidebarPane = new JTabbedPane();
            sidebarPane.addTab("Area tree", null, getJPanel(), null);
            sidebarPane.addTab("Logical tree", null, getJPanel4(), null);
            sidebarPane.addTab("Box tree", null, getBoxTreePanel(), null);
            sidebarPane.addTab("Separators", null, getJPanel2(), null);
            sidebarPane.addTab("Paths", null, getPathsPanel(), null);
        }
        return sidebarPane;
    }

    /**
     * This method initializes jPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getBoxTreePanel()
    {
        if (boxTreePanel == null)
        {
            GridLayout gridLayout2 = new GridLayout();
            gridLayout2.setRows(1);
            boxTreePanel = new JPanel();
            boxTreePanel.setLayout(gridLayout2);
            boxTreePanel.add(getBoxTreeScroll(), null);
        }
        return boxTreePanel;
    }

    /**
     * This method initializes jScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getBoxTreeScroll()
    {
        if (boxTreeScroll == null)
        {
            boxTreeScroll = new JScrollPane();
            boxTreeScroll.setViewportView(getBoxTree());
        }
        return boxTreeScroll;
    }

    /**
     * This method initializes jTree	
     * 	
     * @return javax.swing.JTree	
     */
    private JTree getBoxTree()
    {
        if (boxTree == null)
        {
            boxTree = new JTree();
            boxTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener()
            {
                public void valueChanged(javax.swing.event.TreeSelectionEvent e)
                {
                    Box node = (Box) boxTree.getLastSelectedPathComponent();
                    if (node != null)
                    {
	                    //node.drawExtent((BrowserCanvas) contentCanvas);
                        System.out.println("Node:" + node);
                        ((BrowserPanel) contentCanvas).getOutputDisplay().drawExtent(node);
                        contentCanvas.repaint();
                        //boxTree.scrollPathToVisible(new TreePath(node.getPath()));
                    }
                }
            });
        }
        return boxTree;
    }

    /**
     * This method initializes jScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getContentScroll()
    {
        if (contentScroll == null)
        {
            contentScroll = new JScrollPane();
            contentScroll.setViewportView(getContentCanvas());
            contentScroll.getVerticalScrollBar().setUnitIncrement(10);
            contentScroll.addComponentListener(new java.awt.event.ComponentAdapter()
            {
                /*public void componentResized(java.awt.event.ComponentEvent e)
                {
                    if (contentCanvas != null && contentCanvas instanceof BrowserCanvas)
                    {
                        ((BrowserCanvas) contentCanvas).createLayout(contentScroll.getSize());
                        contentScroll.repaint();
                        BoxTree btree = new BoxTree(((BrowserCanvas) contentCanvas).getViewport());
                        boxTree.setModel(new DefaultTreeModel(btree.getRoot()));
                    }
                }*/
            });
        }
        return contentScroll;
    }

    /**
     * This method initializes jPanel   
     *  
     * @return javax.swing.JPanel   
     */
    private JPanel getContentCanvas()
    {
        if (contentCanvas == null)
        {
            contentCanvas = new JPanel();
            //contentCanvas.add(getInfoSplitter(), null);
        }
        return contentCanvas;
    }
    
    /**
     * This method initializes jSplitPane	
     * 	
     * @return javax.swing.JSplitPane	
     */
    private JSplitPane getMainSplitter()
    {
        if (mainSplitter == null)
        {
            mainSplitter = new JSplitPane();
            mainSplitter.setDividerLocation(250);
            mainSplitter.setLeftComponent(getStructurePanel());
            mainSplitter.setRightComponent(getInfoSplitter());
        }
        return mainSplitter;
    }

    /**
     * This method initializes jToolBar 
     *  
     * @return javax.swing.JToolBar 
     */
    private JToolBar getShowToolBar()
    {
        if (showToolBar == null)
        {
            showToolBar = new JToolBar();
            showToolBar.add(getRedrawButton());
            showToolBar.add(getShowBoxButton());
            showToolBar.add(getShowAreaButton());
            showToolBar.add(getShowArtAreaButton());
            showToolBar.add(getShowColumnsButton());
            showToolBar.add(getShowSepButton());
            showToolBar.add(getGridButton());
        }
        return showToolBar;
    }
    
    
    /**
     * This method initializes jButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getRedrawButton()
    {
        if (redrawButton == null)
        {
            redrawButton = new JButton();
            redrawButton.setText("Clear");
            redrawButton.setMnemonic(KeyEvent.VK_UNDEFINED);
            redrawButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    ((BrowserPanel) contentCanvas).redrawPage();
                    contentCanvas.repaint();
                }
            });
        }
        return redrawButton;
    }

    /**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel()
	{
		if (areaTreePanel == null)
		{
			GridLayout gridLayout4 = new GridLayout();
			gridLayout4.setRows(1);
			gridLayout4.setColumns(1);
			areaTreePanel = new JPanel();
			areaTreePanel.setLayout(gridLayout4);
			areaTreePanel.add(getJScrollPane(), null);
		}
		return areaTreePanel;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane()
	{
		if (areaTreeScroll == null)
		{
			areaTreeScroll = new JScrollPane();
			areaTreeScroll.setViewportView(getAreaJTree());
		}
		return areaTreeScroll;
	}

	/**
	 * This method initializes jTree	
	 * 	
	 * @return javax.swing.JTree	
	 */
	private JTree getAreaJTree()
	{
		if (areaJTree == null)
		{
			areaJTree = new JTree();
			areaJTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener()
			{
				public void valueChanged(javax.swing.event.TreeSelectionEvent e)
				{
                    if (logsync)
                    {
	                    Area node = (Area) areaJTree.getLastSelectedPathComponent();
	                    if (node != null)
	                    {
	                        showArea(node);
                        	areasync = false;
                        	showAreaInLogicalTree(node);
                        	areasync = true;
	                    }
                    }
				}
			});
		}
		return areaJTree;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel2()
	{
		if (sepListPanel == null)
		{
			GridLayout gridLayout3 = new GridLayout();
			gridLayout3.setRows(1);
			gridLayout3.setColumns(1);
			sepListPanel = new JPanel();
			sepListPanel.setLayout(gridLayout3);
			sepListPanel.add(getJScrollPane2(), null);
		}
		return sepListPanel;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane2()
	{
		if (sepScroll == null)
		{
			sepScroll = new JScrollPane();
			sepScroll.setViewportView(getSepList());
		}
		return sepScroll;
	}

	/**
	 * This method initializes sepList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getSepList()
	{
		/*if (sepList == null)
		{
			sepList = new JList();
			sepList.addListSelectionListener(new javax.swing.event.ListSelectionListener()
			{
				public void valueChanged(javax.swing.event.ListSelectionEvent e)
				{
                    Separator sep = (Separator) sepList.getSelectedValue();
                    if (sep != null)
                    {
	                    sep.drawExtent((BrowserCanvas) contentCanvas);
	                    contentCanvas.repaint();
                    }
				}
			});
		}
		return sepList;*/
	    return new JList();
	}

	/**
     * This method initializes lookupButton	
     * 	
     * @return javax.swing.JToggleButton	
     */
    private JToggleButton getLookupButton()
    {
        if (lookupButton == null)
        {
            lookupButton = new JToggleButton();
            lookupButton.setSelected(true);
            lookupButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    if (lookupButton.isSelected())
                    {
                        boxLookupButton.setSelected(false);
                        sepLookupButton.setSelected(false);
                        extractButton.setSelected(false);
                    }
                }
            });
            lookupButton.setText("Area");
            lookupButton.setToolTipText("Find areas");
        }
        return lookupButton;
    }

    private JToggleButton getSepLookupButton() {
        if (sepLookupButton == null) {
            sepLookupButton = new JToggleButton("Sep");
            sepLookupButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    lookupButton.setSelected(false);
                    boxLookupButton.setSelected(false);
                    extractButton.setSelected(false);
                }
            });
        }
        return sepLookupButton;
    }
    
    /**
     * This method initializes extractButton	
     * 	
     * @return javax.swing.JToggleButton	
     */
    private JToggleButton getExtractButton()
    {
        if (extractButton == null)
        {
            extractButton = new JToggleButton();
            extractButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (extractButton.isSelected())
                    {
                        boxLookupButton.setSelected(false);
                        sepLookupButton.setSelected(false);
                        lookupButton.setSelected(false);
                    }
                }
            });
            extractButton.setText("Extract");
        }
        return extractButton;
    }

    /**
     * This method initializes boxLookupButton	
     * 	
     * @return javax.swing.JToggleButton	
     */
    private JToggleButton getBoxLookupButton()
    {
        if (boxLookupButton == null)
        {
            boxLookupButton = new JToggleButton();
            boxLookupButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (boxLookupButton.isSelected())
                    {
                        lookupButton.setSelected(false);
                        sepLookupButton.setSelected(false);
                        extractButton.setSelected(false);
                    }
                }
            });
            boxLookupButton.setText("Box");
            boxLookupButton.setToolTipText("Find boxes");
        }
        return boxLookupButton;
    }

    /**
     * This method initializes showSepButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getShowSepButton()
    {
        if (showSepButton == null)
        {
            showSepButton = new JButton();
            showSepButton.setText("Separators");
            /*showSepButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    showSeparators();
                }
            });*/
        }
        return showSepButton;
    }

    /**
     * This method initializes showBoxButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getShowBoxButton()
    {
        if (showBoxButton == null)
        {
            showBoxButton = new JButton();
            showBoxButton.setText("Show boxes");
            showBoxButton.setToolTipText("Show all boxes in the selected tree");
            showBoxButton.addActionListener(new java.awt.event.ActionListener()
            {
				public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    Box node = (Box) boxTree.getLastSelectedPathComponent();
                    if (node != null /*&& node.getBox() != null*/)
                    {
                        /*Enumeration<?> en = node.postorderEnumeration();
                        while (en.hasMoreElements())
                        {
                            BoxNode child = (BoxNode) en.nextElement();
                            if (child.getBox() != null)
                                child.drawExtent((BrowserCanvas) contentCanvas);
                        }*/
                        contentCanvas.repaint();
                    }
                }
            });
        }
        return showBoxButton;
    }

    /**
     * This method initializes showAreaButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getShowAreaButton()
    {
        if (showAreaButton == null)
        {
            showAreaButton = new JButton();
            showAreaButton.setText("Show areas");
            showAreaButton.setToolTipText("Show all the areas in the selected area");
            showAreaButton.addActionListener(new java.awt.event.ActionListener()
            {
				public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    Area node = (Area) areaJTree.getLastSelectedPathComponent();
                    if (node != null)
                    {
                        showAreas(node, null);
                    }
                }
            });
        }
        return showAreaButton;
    }

    /**
     * This method initializes lookupToolBar	
     * 	
     * @return javax.swing.JToolBar	
     */
    private JToolBar getLookupToolBar()
    {
        if (lookupToolBar == null)
        {
            lookupToolBar = new JToolBar();
            lookupToolBar.add(getBoxLookupButton());
            lookupToolBar.add(getLookupButton());
            lookupToolBar.add(getSepLookupButton());
            lookupToolBar.add(getExtractButton());
        }
        return lookupToolBar;
    }

    /**
     * This method initializes jPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getToolPanel()
    {
        if (toolPanel == null)
        {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(java.awt.FlowLayout.LEFT);
            toolPanel = new JPanel();
            //toolPanel.setLayout(new WrappingLayout(WrappingLayout.LEFT, 1, 1));
            toolPanel.setLayout(new ToolbarLayout());
            toolPanel.add(getShowToolBar());
            toolPanel.add(getLookupToolBar());
            toolPanel.add(getFileToolBar());
        }
        return toolPanel;
    }

    /**
	 * This method initializes fileToolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getFileToolBar() {
		if (fileToolBar == null) {
			fileToolBar = new JToolBar();
			fileToolBar.add(getRefreshButton());
			fileToolBar.add(getSaveButton());
			fileToolBar.add(getSaveLogicalButton());
			fileToolBar.add(getSaveRDFButton());
		}
		return fileToolBar;
	}

	/**
	 * This method initializes saveButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSaveButton() {
		if (saveButton == null) {
			saveButton = new JButton();
			saveButton.setText("Save Visual");
			saveButton.setEnabled(false);
			/*saveButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if (currentUrl != null && dispFinished)
					{
	            		JFileChooser chooser = new JFileChooser();
	            	    FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files", "xml");
	            	    chooser.setFileFilter(filter);
	            	    chooser.setMultiSelectionEnabled(false);
	            	    chooser.setDialogTitle("Save Area Tree");
	            	    if (saveDir != null)
	            	    	chooser.setCurrentDirectory(saveDir);
	            	    int returnVal = chooser.showSaveDialog(mainWindow);
	            	    if(returnVal == JFileChooser.APPROVE_OPTION)
	            	    {
	            	    	saveDir = chooser.getCurrentDirectory();
	            	    	try {
		            	    	File file = chooser.getSelectedFile();
                                PrintWriter xs = new PrintWriter(file, "utf-8");
		                        XMLOutput xo = new XMLOutput(proc.getAreaTree(), currentUrl);
		                        xo.dumpTo(xs);
		                        xs.close();
	            	    	} catch (FileNotFoundException ex) {
	            	    		System.err.println("Error: " + ex.getMessage());
	            	    	} catch (UnsupportedEncodingException ex) {
                                System.err.println("Error: " + ex.getMessage());
                            }
	            	    }
					}
				}
			});*/
		}
		return saveButton;
	}

	/**
     * This method initializes gridButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getGridButton()
    {
        if (gridButton == null)
        {
            gridButton = new JButton();
            gridButton.setText("Show grid");
            gridButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    Area node = (Area) areaJTree.getLastSelectedPathComponent();
                    if (node != null && contentCanvas instanceof BrowserPanel)
                    {
                        node.getTopology().drawLayout(((BrowserPanel) contentCanvas).getOutputDisplay());
                        contentCanvas.repaint();
                    }
                }
            });
        }
        return gridButton;
    }

    /**
     * This method initializes jPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJPanel4()
    {
        if (jPanel == null)
        {
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = GridBagConstraints.BOTH;
            gridBagConstraints8.gridy = 0;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.weighty = 1.0;
            gridBagConstraints8.gridx = 0;
            jPanel = new JPanel();
            jPanel.setLayout(new GridBagLayout());
            jPanel.add(getJScrollPane3(), gridBagConstraints8);
        }
        return jPanel;
    }

    /**
     * This method initializes jScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJScrollPane3()
    {
        if (logicalTreeScroll == null)
        {
            logicalTreeScroll = new JScrollPane();
            logicalTreeScroll.setViewportView(getLogicalTree());
        }
        return logicalTreeScroll;
    }

    /**
     * This method initializes logicalTree	
     * 	
     * @return javax.swing.JTree	
     */
    private JTree getLogicalTree()
    {
        if (logicalTree == null)
        {
            logicalTree = new JTree();
            /*logicalTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener()
                    {
                        public void valueChanged(javax.swing.event.TreeSelectionEvent e)
                        {
                        	if (areasync)
                        	{
	                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) logicalTree.getLastSelectedPathComponent();
	                            if (node != null && node instanceof LogicalNode)
	                            {
	                            	showNode((LogicalNode) node);
                            		logsync = false;
                            		showAreaInTree(((LogicalNode) node).getFirstAreaNode());
                            		logsync = true;
	                            }
                        	}
                        }
                    });*/
        }
        return logicalTree;
    }

    /**
     * This method initializes refreshButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getRefreshButton()
    {
        if (refreshButton == null)
        {
            refreshButton = new JButton();
            refreshButton.setText("Refresh");
            refreshButton.setToolTipText("Refresh the tree views");
            refreshButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    refresh();
                }
            });
        }
        return refreshButton;
    }

    /**
     * This method initializes infoSplitter	
     * 	
     * @return javax.swing.JSplitPane	
     */
    private JSplitPane getInfoSplitter()
    {
        if (infoSplitter == null)
        {
            infoSplitter = new JSplitPane();
            infoSplitter.setDividerLocation(1050);
            infoSplitter.setLeftComponent(getContentPanel());
            infoSplitter.setRightComponent(getObjectInfoPanel());
        }
        return infoSplitter;
    }

    /**
     * This method initializes objectInfoPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getObjectInfoPanel()
    {
        if (objectInfoPanel == null)
        {
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.insets = new Insets(0, 0, 5, 0);
            gridBagConstraints9.fill = GridBagConstraints.BOTH;
            gridBagConstraints9.weighty = 1.0;
            gridBagConstraints9.weightx = 1.0;
            gridBagConstraints9.gridy = 3;
            gridBagConstraints9.gridx = 0;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.insets = new Insets(0, 0, 5, 0);
            gridBagConstraints10.fill = GridBagConstraints.BOTH;
            gridBagConstraints10.gridy = 0;
            gridBagConstraints10.weightx = 1.0;
            gridBagConstraints10.weighty = 1.0;
            gridBagConstraints10.gridx = 0;
            objectInfoPanel = new JPanel();
            GridBagLayout gbl_objectInfoPanel = new GridBagLayout();
            gbl_objectInfoPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
            gbl_objectInfoPanel.columnWeights = new double[]{1.0};
            objectInfoPanel.setLayout(gbl_objectInfoPanel);
            objectInfoPanel.add(getJScrollPane4(), gridBagConstraints10);
            GridBagConstraints gbc_probabilityScroll = new GridBagConstraints();
            gbc_probabilityScroll.weighty = 0.25;
            gbc_probabilityScroll.weightx = 1.0;
            gbc_probabilityScroll.insets = new Insets(0, 0, 5, 0);
            gbc_probabilityScroll.fill = GridBagConstraints.BOTH;
            gbc_probabilityScroll.gridx = 0;
            gbc_probabilityScroll.gridy = 1;
            objectInfoPanel.add(getProbabilityScroll(), gbc_probabilityScroll);
            GridBagConstraints gbc_markednessText = new GridBagConstraints();
            gbc_markednessText.insets = new Insets(0, 0, 5, 0);
            gbc_markednessText.fill = GridBagConstraints.HORIZONTAL;
            gbc_markednessText.gridx = 0;
            gbc_markednessText.gridy = 2;
            objectInfoPanel.add(getMarkednessText(), gbc_markednessText);
            objectInfoPanel.add(getJScrollPane5(), gridBagConstraints9);
        }
        return objectInfoPanel;
    }

    /**
     * This method initializes jScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJScrollPane4()
    {
        if (objectInfoScroll == null)
        {
            objectInfoScroll = new JScrollPane();
            objectInfoScroll.setViewportView(getInfoTable());
        }
        return objectInfoScroll;
    }

    /**
     * This method initializes infoTable	
     * 	
     * @return javax.swing.JTable	
     */
    private JTable getInfoTable()
    {
        if (infoTable == null)
        {
            infoTable = new JTable();
        }
        return infoTable;
    }
    
    /**
     * This method initializes showArtAreaButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getShowArtAreaButton()
    {
        if (showArtAreaButton == null)
        {
            showArtAreaButton = new JButton();
            showArtAreaButton.setText("Art. areas");
            showArtAreaButton.setToolTipText("Show artificial areas marked with <area>");       
            /*showArtAreaButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) areaTree.getLastSelectedPathComponent();
                    if (node != null && node instanceof AreaNode)
                    {
                        showAreas((AreaNode) node, "<area");
                    }
                }
            });*/
        }
        return showArtAreaButton;
    }

    /**
     * This method initializes showColumnsButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getShowColumnsButton()
    {
        if (showColumnsButton == null)
        {
          showColumnsButton = new JButton();
          showColumnsButton.setText("Columns");
          showColumnsButton.setToolTipText("Show columns marked with <column>");
          /*showColumnsButton.addActionListener(new java.awt.event.ActionListener()
          {
              public void actionPerformed(java.awt.event.ActionEvent e)
              {
                  DefaultMutableTreeNode node = (DefaultMutableTreeNode) areaTree.getLastSelectedPathComponent();
                  if (node != null && node instanceof AreaNode)
                  {
                      showAreas((AreaNode) node, "<column>");
                  }
              }
          });*/
        }
        return showColumnsButton;
    }

    /**
     * This method initializes jScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJScrollPane5()
    {
        if (featureScroll == null)
        {
            featureScroll = new JScrollPane();
            featureScroll.setViewportView(getFeatureTable());
        }
        return featureScroll;
    }

    /**
     * This method initializes featureTable	
     * 	
     * @return javax.swing.JTable	
     */
    private JTable getFeatureTable()
    {
        if (featureTable == null)
        {
            featureTable = new JTable();
        }
        return featureTable;
    }

    /**
     * This method initializes saveLogicalButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getSaveLogicalButton()
    {
        if (saveLogicalButton == null)
        {
            saveLogicalButton = new JButton();
            saveLogicalButton.setText("Save Logical");
            saveLogicalButton.setEnabled(false);
            saveLogicalButton.setToolTipText("Save logical tree to a file");
            /*saveLogicalButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    if (currentUrl != null && dispFinished)
                    {
                        JFileChooser chooser = new JFileChooser();
                        FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files", "xml");
                        chooser.setFileFilter(filter);
                        chooser.setMultiSelectionEnabled(false);
                        chooser.setDialogTitle("Save Logical Tree");
                        if (saveDir != null)
                            chooser.setCurrentDirectory(saveDir);
                        int returnVal = chooser.showSaveDialog(mainWindow);
                        if(returnVal == JFileChooser.APPROVE_OPTION)
                        {
                            saveDir = chooser.getCurrentDirectory();
                            try {
                                File file = chooser.getSelectedFile();
                                PrintWriter xs = new PrintWriter(file, "utf-8");
                                XMLLogicalOutput xo = new XMLLogicalOutput(proc.getLogicalTree(), currentUrl);
                                xo.dumpTo(xs);
                                xs.close();
                            } catch (FileNotFoundException ex) {
                                System.err.println("Error: " + ex.getMessage());
                            } catch (UnsupportedEncodingException ex) {
                                System.err.println("Error: " + ex.getMessage());
                            }
                        }
                    }
                }
            });*/
        }
        return saveLogicalButton;
    }

    private JButton getSaveRDFButton()
    {
        if (saveRDFButton == null)
        {
            saveRDFButton = new JButton("Save RDF");
            saveRDFButton.setEnabled(false);
            /*saveRDFButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent arg0)
                {
                    if (currentUrl != null && dispFinished)
                    {
                        JFileChooser chooser = new JFileChooser();
                        FileNameExtensionFilter filter = new FileNameExtensionFilter("RDF files", "rdf");
                        chooser.setFileFilter(filter);
                        chooser.setMultiSelectionEnabled(false);
                        chooser.setDialogTitle("Save RDF");
                        if (saveDir != null)
                            chooser.setCurrentDirectory(saveDir);
                        int returnVal = chooser.showSaveDialog(mainWindow);
                        if(returnVal == JFileChooser.APPROVE_OPTION)
                        {
                            saveDir = chooser.getCurrentDirectory();
                            try {
                                File file = chooser.getSelectedFile();
                                PrintWriter xs = new PrintWriter(file, "utf-8");
                                RDFOutput xo = new RDFOutput(proc.getBoxTree(), proc.getAreaTree(), currentUrl);
                                xo.dumpTo(xs);
                                xs.close();
                            } catch (FileNotFoundException ex) {
                                System.err.println("Error: " + ex.getMessage());
                            } catch (UnsupportedEncodingException ex) {
                                System.err.println("Error: " + ex.getMessage());
                            }
                        }
                    }
                }
            });*/
        }
        return saveRDFButton;
    }
    
    private JTextField getMarkednessText() 
    {
        if (markednessText == null) 
        {
            markednessText = new JTextField();
            markednessText.setHorizontalAlignment(SwingConstants.CENTER);
            markednessText.setText("---");
            markednessText.setFont(new Font("Dialog", Font.PLAIN, 18));
            markednessText.setEditable(false);
            markednessText.setColumns(10);
        }
        return markednessText;
    }

    private JPanel getPathsPanel()
    {
        if (pathsPanel == null)
        {
            pathsPanel = new JPanel();
            GridBagLayout gbl_pathsPanel = new GridBagLayout();
            gbl_pathsPanel.columnWidths = new int[] { 0, 0 };
            gbl_pathsPanel.rowHeights = new int[] { 0, 0, 0 };
            gbl_pathsPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
            gbl_pathsPanel.rowWeights = new double[] { 1.0, 1.0,
                    Double.MIN_VALUE };
            pathsPanel.setLayout(gbl_pathsPanel);
            GridBagConstraints gbc_pathListScroll = new GridBagConstraints();
            gbc_pathListScroll.insets = new Insets(0, 0, 5, 0);
            gbc_pathListScroll.fill = GridBagConstraints.BOTH;
            gbc_pathListScroll.gridx = 0;
            gbc_pathListScroll.gridy = 0;
            pathsPanel.add(getPathListScroll(), gbc_pathListScroll);
            GridBagConstraints gbc_extractionScroll = new GridBagConstraints();
            gbc_extractionScroll.fill = GridBagConstraints.BOTH;
            gbc_extractionScroll.gridx = 0;
            gbc_extractionScroll.gridy = 1;
            pathsPanel.add(getExtractionScroll(), gbc_extractionScroll);
        }
        return pathsPanel;
    }

    private JScrollPane getPathListScroll()
    {
        if (pathListScroll == null)
        {
            pathListScroll = new JScrollPane();
        }
        return pathListScroll;
    }

    private JScrollPane getExtractionScroll()
    {
        if (extractionScroll == null)
        {
            extractionScroll = new JScrollPane();
            extractionScroll.setViewportView(getExtractionTable());
        }
        return extractionScroll;
    }

    private JScrollPane getProbabilityScroll()
    {
        if (probabilityScroll == null)
        {
            probabilityScroll = new JScrollPane();
            probabilityScroll.setViewportView(getProbTable());
        }
        return probabilityScroll;
    }

    private JTable getProbTable()
    {
        if (probTable == null)
        {
            probTable = new JTable();
            probTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer()
            {
                private static final long serialVersionUID = 1L;
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
                {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    try {
                        Double d = Double.parseDouble(value.toString().replace(',', '.'));
                        if (d <= TAG_PROBABILITY_THRESHOLD)
                            c.setForeground(new java.awt.Color(180, 180, 180));
                        else
                            c.setForeground(Color.BLACK);
                    } catch (NumberFormatException e) {
                        c.setForeground(Color.BLACK);
                    }
                    return c;
                }
            });
        }
        return probTable;
    }

    private JTable getExtractionTable()
    {
        if (extractionTable == null)
        {
            extractionTable = new JTable();
        }
        return extractionTable;
    }
    

    private JTabbedPane getToolTabs()
    {
        if (toolTabs == null)
        {
            toolTabs = new JTabbedPane(JTabbedPane.TOP);
            toolTabs.addTab("Sources", null, getSourcesTab(), null);
        }
        return toolTabs;
    }

    private JPanel getSourcesTab()
    {
        if (sourcesTab == null)
        {
            sourcesTab = new JPanel();
            GridBagLayout gbl_sourcesTab = new GridBagLayout();
            gbl_sourcesTab.columnWeights = new double[] { 0.0 };
            gbl_sourcesTab.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
            sourcesTab.setLayout(gbl_sourcesTab);
            GridBagConstraints gbc_rendererChoicePanel = new GridBagConstraints();
            gbc_rendererChoicePanel.weightx = 1.0;
            gbc_rendererChoicePanel.anchor = GridBagConstraints.EAST;
            gbc_rendererChoicePanel.fill = GridBagConstraints.BOTH;
            gbc_rendererChoicePanel.insets = new Insets(0, 0, 5, 0);
            gbc_rendererChoicePanel.gridx = 0;
            gbc_rendererChoicePanel.gridy = 0;
            sourcesTab.add(getRendererChoicePanel(), gbc_rendererChoicePanel);
            GridBagConstraints gbc_rendererParamsPanel = new GridBagConstraints();
            gbc_rendererParamsPanel.weightx = 1.0;
            gbc_rendererParamsPanel.fill = GridBagConstraints.BOTH;
            gbc_rendererParamsPanel.insets = new Insets(0, 0, 5, 0);
            gbc_rendererParamsPanel.gridx = 0;
            gbc_rendererParamsPanel.gridy = 1;
            sourcesTab.add(getRendererParamsPanel(), gbc_rendererParamsPanel);
            GridBagConstraints gbc_segmChoicePanel = new GridBagConstraints();
            gbc_segmChoicePanel.weightx = 1.0;
            gbc_segmChoicePanel.anchor = GridBagConstraints.EAST;
            gbc_segmChoicePanel.fill = GridBagConstraints.BOTH;
            gbc_segmChoicePanel.insets = new Insets(0, 0, 5, 0);
            gbc_segmChoicePanel.gridx = 0;
            gbc_segmChoicePanel.gridy = 2;
            sourcesTab.add(getSegmChoicePanel(), gbc_segmChoicePanel);
            GridBagConstraints gbc_segmParamsPanel = new GridBagConstraints();
            gbc_segmParamsPanel.weightx = 1.0;
            gbc_segmParamsPanel.fill = GridBagConstraints.BOTH;
            gbc_segmParamsPanel.gridx = 0;
            gbc_segmParamsPanel.gridy = 3;
            sourcesTab.add(getSegmParamsPanel(), gbc_segmParamsPanel);

            BoxTreeProvider p = (BoxTreeProvider) rendererCombo
                    .getSelectedItem();
            if (p != null) ((ParamsPanel) rendererParamsPanel).setOperation(p);
            AreaTreeProvider ap = (AreaTreeProvider) segmentatorCombo
                    .getSelectedItem();
            if (ap != null) ((ParamsPanel) segmParamsPanel).setOperation(ap);

        }
        return sourcesTab;
    }

    private JLabel getRendererLabel()
    {
        if (rendererLabel == null)
        {
            rendererLabel = new JLabel("Renderer");
        }
        return rendererLabel;
    }

    private JComboBox<BoxTreeProvider> getRendererCombo()
    {
        if (rendererCombo == null)
        {
            rendererCombo = new JComboBox<BoxTreeProvider>();
            rendererCombo.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    BoxTreeProvider p = (BoxTreeProvider) rendererCombo.getSelectedItem();
                    if (p != null)
                        ((ParamsPanel) rendererParamsPanel).setOperation(p);
                }
            });
            Vector<BoxTreeProvider> providers = new Vector<BoxTreeProvider>(proc.getBoxProviders().values());
            DefaultComboBoxModel<BoxTreeProvider> model = new DefaultComboBoxModel<BoxTreeProvider>(providers);
            rendererCombo.setModel(model);
        }
        return rendererCombo;
    }

    private JPanel getRendererChoicePanel()
    {
        if (rendererChoicePanel == null)
        {
            rendererChoicePanel = new JPanel();
            FlowLayout flowLayout = (FlowLayout) rendererChoicePanel.getLayout();
            flowLayout.setAlignment(FlowLayout.LEFT);
            rendererChoicePanel.add(getRendererLabel());
            rendererChoicePanel.add(getRendererCombo());
            rendererChoicePanel.add(getOkButton());
        }
        return rendererChoicePanel;
    }

    private JPanel getRendererParamsPanel()
    {
        if (rendererParamsPanel == null)
        {
            rendererParamsPanel = new ParamsPanel();
        }
        return rendererParamsPanel;
    }

    private JPanel getSegmChoicePanel()
    {
        if (segmChoicePanel == null)
        {
            segmChoicePanel = new JPanel();
            FlowLayout flowLayout = (FlowLayout) segmChoicePanel.getLayout();
            flowLayout.setAlignment(FlowLayout.LEFT);
            segmChoicePanel.add(getLblSegmentator());
            segmChoicePanel.add(getSegmentatorCombo());
            segmChoicePanel.add(getSegmRunButton());
            segmChoicePanel.add(getSegmAutorunCheckbox());
            segmChoicePanel.add(getBtnOperators());
        }
        return segmChoicePanel;
    }

    private JPanel getSegmParamsPanel()
    {
        if (segmParamsPanel == null)
        {
            segmParamsPanel = new ParamsPanel();
        }
        return segmParamsPanel;
    }

    private JLabel getLblSegmentator()
    {
        if (lblSegmentator == null)
        {
            lblSegmentator = new JLabel("Segmentator");
        }
        return lblSegmentator;
    }

    private JComboBox<AreaTreeProvider> getSegmentatorCombo()
    {
        if (segmentatorCombo == null)
        {
            segmentatorCombo = new JComboBox<AreaTreeProvider>();
            segmentatorCombo.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    AreaTreeProvider ap = (AreaTreeProvider) segmentatorCombo.getSelectedItem();
                    if (ap != null)
                        ((ParamsPanel) segmParamsPanel).setOperation(ap);
                }
            });
            Vector<AreaTreeProvider> providers = new Vector<AreaTreeProvider>(
                    proc.getAreaProviders().values());
            DefaultComboBoxModel<AreaTreeProvider> model = new DefaultComboBoxModel<AreaTreeProvider>(providers);
            segmentatorCombo.setModel(model);
        }
        return segmentatorCombo;
    }

    private JButton getSegmRunButton()
    {
        if (segmRunButton == null)
        {
            segmRunButton = new JButton("Run");
            segmRunButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    //TODO run segmentation
                }
            });
        }
        return segmRunButton;
    }
    
    private JCheckBox getSegmAutorunCheckbox()
    {
        if (segmAutorunCheckbox == null)
        {
            segmAutorunCheckbox = new JCheckBox("Run automatically");
        }
        return segmAutorunCheckbox;
    }
    
    private JButton getBtnOperators()
    {
        if (btnOperators == null)
        {
            btnOperators = new JButton("Operators...");
            btnOperators.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    if (operatorWindow == null)
                        operatorWindow = new OperatorConfigWindow(proc);
                    operatorWindow.setVisible(true);
                }
            });
        }
        return btnOperators;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    browser = new BlockBrowser();
                    browser.setLoadImages(false);
                    JFrame main = browser.getMainWindow();
                    //main.setSize(1000,600);
                    //main.setMinimumSize(new Dimension(1200, 600));
                    //main.setSize(1500,600);
                    main.setSize(1600,1000);
                    browser.initPlugins();
                    main.setVisible(true);
                    
                    //String localpath = "file:///home/radek/myprog/workspace/Layout";
                    //String localpath = "file:///home/burgetr/workspace/Layout";
                    //String localpath = "file:/C:\\Documents and Settings\\burgetr\\workspace\\Layout";
                    String localpath = "file://" + System.getProperty("user.home");
                    localpath += "/git/Layout";
            
                    //URL url = new URL("http://www.idnes.cz/");
                    //URL url = new URL("http://olomouc.idnes.cz/rad-nemeckych-rytiru-pozadal-v-restitucich-i-o-hrady-bouzov-a-sovinec-12b-/olomouc-zpravy.aspx?c=A131113_115042_olomouc-zpravy_mip");
                    //URL url = new URL("http://www.aktualne.cz/");
                    
                    /* PROGRAMMES */
                    
                    //URL url = new URL("http://faculty.neu.edu.cn/yangxc/DQIS2011/workshop.html");
                    //URL url = new URL("http://dali2011.dia.uniroma3.it/program.html");
                    //URL url = new URL("http://www.searchingspeech.org/SSCS2010/SSCS2010.html");
                    //URL url = new URL("http://sspnet.eu/2010/04/sspw/");
                    //URL url = new URL("http://www.icudl2010.org/icudl_program.htm"); //problemy - prazdne vyrazne oblasti?
                    //URL url = new URL("http://iwssps2010.cs.arizona.edu/program.html");
                    //URL url = new URL("http://www.cssim.org/sites/cssim.org/files/cssim-timetable-full.html");
                    //URL url = new URL("http://liber2009.biu-toulouse.fr/images/stories/documents/conference_programme_toulouse_EN.pdf");
                    //URL url = new URL("http://www.ehealthconference.info/ConferenceProgramme/index.php");
                    //URL url = new URL("http://www.dexa.org/previous/dexa2011/programme703b.html?cid=189");
                    //URL url = new URL("http://www.icdar2011.org/EN/column/column32.shtml");
                    //URL url = new URL("http://aktualne.centrum.cz/ekonomika/business-ve-svete/clanek.phtml?id=749291");
                    //URL url = new URL("http://clair.si.umich.edu/clair/sigmod-pods06/SIGMOD-program.htm");
                    //URL url = new URL("http://edbticdt2011.it.uu.se/workshops_program.html");
                    //URL url = new URL("http://www.znalosti.eu/program-konference");
                    //URL url = new URL(localpath + "/test/simple.html");
                    //URL url = new URL(localpath + "/test/markedness.html");
                    //URL url = new URL(localpath + "/test/programmes/dqis2011.html");
                    //URL url = new URL(localpath + "/test/programmes/SSCS2010.html");
                    //URL url = new URL(localpath + "/test/programmes/icudl2010.html");
                    //URL url = new URL(localpath + "/test/programmes/iwssps2010.html");
                    //URL url = new URL(localpath + "/test/programmes/dali2011.html");
                    //URL url = new URL(localpath + "/test/programmes/RuleML-2010-Programme.pdf");
                    //URL url = new URL(localpath + "/test/programmes/cade23-schedule.pdf");
                    //URL url = new URL(localpath + "/test/programmes/ehealth07.html");
                    //URL url = new URL(localpath + "/test/programmes/znalosti2013.html");
                    //URL url = new URL(localpath + "/test/programmes2/1/aaa-idea.org/program.shtml");            
                    //URL url = new URL(localpath + "/test/programmes2/2/aciids2010.hueuni.edu.vn/index.html");
                    //URL url = new URL(localpath + "/test/programmes3/x37/index.html");

                    
                    /* MENUS */

                    //URL url = new URL("http://menu.olomouc.cz/index.php?act=rmenu&rid=32");
                    //URL url = new URL("http://www.obedvat.cz/cz/obedove-menu/619-pivni-bar-atrium.html");
                    
                    /* NEWS */
                    //URL url = new URL("http://edition.cnn.com/2014/02/24/world/europe/ukraine-protests-up-to-speed/index.html?hpt=hp_t1");
                    URL url = new URL("http://www.reuters.com/article/2014/03/28/us-trading-momentum-analysis-idUSBREA2R09M20140328");
                    
                    browser.setLocation(url.toString());
                        
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
    }

}
