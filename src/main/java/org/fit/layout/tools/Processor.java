/*
 * Processor.java
 *
 * Created on 15. 12. 2013, 11:26:38 by burgetr
 */

package org.fit.layout.tools;

import java.awt.Dimension;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.fit.layout.classify.TreeTagger;
import org.fit.layout.cssbox.CSSBoxTreeBuilder;
import org.fit.layout.model.Page;
import org.fit.segm.grouping.AreaTree;
import org.fit.segm.grouping.op.AreaTreeOperator;
import org.fit.segm.grouping.op.FindLineOperator;
import org.fit.segm.grouping.op.HomogeneousLeafOperator;
import org.xml.sax.SAXException;


/**
 * Implementation of the basic processes.
 * @author burgetr
 */
public class Processor
{
    private Page page;
    private AreaTree atree;
    private TreeTagger tagger;


    public Processor()
    {
        
    }
    
    
    public Page renderPage(String urlstring, Dimension dim) throws MalformedURLException, IOException, SAXException
    {
        CSSBoxTreeBuilder build = new CSSBoxTreeBuilder(dim);
        build.parse(urlstring);
        page = build.getPage();
        return page;
    }

    
    /**
     * Segments a rendered page and creates the box tree, area tree and logical tree.
     * @param viewport the viewport of the rendered page
     */
    public void segmentPage(Page page)
    {
        //area tree
        atree = new AreaTree(page);
        atree.findBasicAreas();
        
        //apply the area tree operations
        Vector<AreaTreeOperator> operations = new Vector<AreaTreeOperator>();
        operations.add(new FindLineOperator(false, 1.5f));
        operations.add(new HomogeneousLeafOperator());
        ////operations.add(new FindColumnsOperator());
        //operations.add(new SuperAreaOperator(1)); //TODO misto pass limit by se hodilo nejake omezeni granularity na zaklade vlastnosti oblasti
        ////operations.add(new CollapseAreasOperator());
        //operations.add(new ReorderOperator());
        
        System.out.println("OPERATORS");
        for (AreaTreeOperator op : operations)
        {
            System.out.println(op.toString());
            op.apply(atree);
        }
        System.out.println("DONE");
        
        treesCompleted();
    }
    
    //====================================================================================
    
    protected void treesCompleted()
    {
        //this is called when the tree creation is finished
    }

    //====================================================================================
    


    public Page getPage()
    {
        return page;
    }


    public AreaTree getAreaTree()
    {
        return atree;
    }


}
