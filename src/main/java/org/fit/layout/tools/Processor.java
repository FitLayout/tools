/*
 * Processor.java
 *
 * Created on 15. 12. 2013, 11:26:38 by burgetr
 */

package org.fit.layout.tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Vector;

import org.fit.cssbox.layout.Viewport;
import org.fit.layout.impl.AreaTree;
import org.fit.layout.impl.BoxTree;

/**
 * Implementation of the basic processes.
 * @author burgetr
 */
public class Processor
{
    private BoxTree btree;
    private AreaTree atree;
    private LogicalTree ltree;
    private FeatureAnalyzer features;
    private ArticleExtractor ex;
    private TreeTagger tagger;
    private SingleClassStyleAnalyzer ssa;
    private AreaTreeStyleAnalyzer msa;
    private ExtractedStyleAnalyzer esa;
    private TagPredictor tpred;
    private VisualClassifier vcls;
    private Vector<EvalData> evalData;
    //weight that roughly correspond to those used in Annotator
    private double[] weights = null;//{100.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.5, 0.5};


    public Processor()
    {
        
    }
    

    /**
     * Segments a rendered page and creates the box tree, area tree and logical tree.
     * @param viewport the viewport of the rendered page
     */
    public void segmentPage(Viewport viewport)
    {
        //box tree
        btree = new BoxTree(viewport);
        
        //area tree
        atree = new AreaTree(btree);
        atree.findBasicAreas();
        
        //apply the area tree operations
        Vector<AreaTreeOperator> operations = new Vector<AreaTreeOperator>();
        operations.add(new FindLineOperator(Config.CONSISTENT_LINE_STYLE, Config.MAX_LINE_EM_SPACE));
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
        
        ////atree.findColumns();
        //atree = new VipsAreaTree(btree);
        
        features = new FeatureAnalyzer(atree);
        if (weights != null)
            features.setWeights(weights);
        LayoutAnalyzer layout = new LayoutAnalyzer(atree);
        
        //tagging
        Tagger tTime = new TimeTagger();
        Tagger tDate = new DateTagger();
        Tagger tPersons = new PersonsTagger(1);
        Tagger tTitle = new TitleTagger();
        
        tagger = new TreeTagger(atree);
        tagger.addTagger(tTime);
        tagger.addTagger(tDate);
        tagger.addTagger(tPersons);
        tagger.addTagger(tTitle);
        tagger.tagTree();
        
        TagJoinOperator tjo = new TagJoinOperator();
        //tjo.apply(atree);
        
        //style-based classification
        vcls = new VisualClassifier("test/train_mix.arff", 1);
        vcls.classifyTree(atree.getRoot(), features);
        
        //style statistical analysis
        /*ssa = new SingleClassStyleAnalyzer(atree, tagger);
        msa = new AreaTreeStyleAnalyzer(tagger, atree);
        msa.createTrainingData();
        tpred = new TagPredictor(msa);
        System.out.println("Styles for DATE: " + msa.getSortedStylesForTag(tDate.getTag()));
        System.out.println("Styles for TIME: " + msa.getSortedStylesForTag(tTime.getTag()));
        System.out.println("Styles for TITLE: " + msa.getSortedStylesForTag(tTitle.getTag()));
        System.out.println("Styles for PERSONS: " + msa.getSortedStylesForTag(tPersons.getTag()));
        System.out.println("All styles: " + msa.getAllStylesSorted());*/
        
        //IndentationAreaOperator iaop = new IndentationAreaOperator(atree);
        //iaop.apply(atree);
        
        ltree = new LogicalTreeIndentation(atree, features, layout);
        //ltree.joinTaggedNodes(tpred);
        
        //refresh the tree display
        treesCompleted();
        
        //XML output
        /*PrintWriter xs = new PrintWriter(new FileOutputStream("test/tree.xml"));
        XMLOutput xo = new XMLOutput(atree, url);
        xo.dumpTo(xs);
        xs.close();*/

        //HTML output
        /*PrintWriter hts = new PrintWriter(new FileOutputStream("test/tree.html"));
        HTMLOutput hto = new HTMLOutput(atree, url);
        hto.dumpTo(hts);
        hts.close();*/
        
        //extract
        System.out.println("EXTRACTION");
        ex = new ArticleExtractor(atree);
        //ex.extractDescriptions();
        /*try {
            PrintStream exs = new PrintStream(new FileOutputStream("test/extract.html"));
            ex.dumpTo(exs);
            exs.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        
        /*Vector<AreaNode> hdrs = ex.findHeadings();
        System.out.println("HEADINGS:");
        for (AreaNode hdr : hdrs)
        {
            System.out.println("  " + hdr.getArea().getBoxText());
        }*/
        
    }
    
    public void evaluate(URL url)
    {
        //load the evaluation results
        String localpath = "file://" + System.getProperty("user.home") + "/workspace/Layout/test/programmes3";
        CSVLoader eval = new CSVLoader("test/programmes3/eval.csv", localpath);
        evalData = eval.getData(url);
        if (evalData == null)
            System.out.println("We have no evaluation data for " + url);
        else
        {
            System.out.println("Evaluation data found: " + evalData.size() + " entries");
            DataMatcher matcher = new DataMatcher(ltree.getRoot());
            matcher.matchAll(evalData);
        }
            
        /*JsonLoader evalJson = new JsonLoader("test/eval/eval.json");
        evalData = evalJson.getData(url);
        if (evalData == null)
            System.out.println("We have no evaluation data for " + url);
        else
            System.out.println("Evaluation data found: " + evalData.size() + " entries");
        PrintStream eds = new PrintStream(new FileOutputStream("test/eval/eval.csv"));
        evalJson.dumpCSV(eds);
        eds.close();*/
    }
    
    //====================================================================================
    
    protected void treesCompleted()
    {
        //this is called when the tree creation is finished
    }

    //====================================================================================
    
    public BoxTree getBoxTree()
    {
        return btree;
    }


    public AreaTree getAreaTree()
    {
        return atree;
    }


    public LogicalTree getLogicalTree()
    {
        return ltree;
    }


    public FeatureAnalyzer getFeatures()
    {
        return features;
    }


    public ArticleExtractor getExtractor()
    {
        return ex;
    }


    public TreeTagger getTagger()
    {
        return tagger;
    }


    public SingleClassStyleAnalyzer getSsa()
    {
        return ssa;
    }


    public AreaTreeStyleAnalyzer getMsa()
    {
        return msa;
    }


    public ExtractedStyleAnalyzer getEsa()
    {
        return esa;
    }


    public TagPredictor getTagPredictor()
    {
        return tpred;
    }


    public Vector<EvalData> getEvalData()
    {
        return evalData;
    }


    public VisualClassifier getVisualClassifier()
    {
        return vcls;
    }


    public void setVisualClassifier(VisualClassifier vcls)
    {
        this.vcls = vcls;
    }


    public double[] getWeights()
    {
        return weights;
    }


    public void setWeights(double[] weights)
    {
        this.weights = weights;
    }
    
}
