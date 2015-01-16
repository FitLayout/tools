/**
 * ScriptableProcessor.java
 *
 * Created on 14. 1. 2015, 14:52:04 by burgetr
 */
package org.fit.layout.process;

import java.awt.Dimension;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.fit.layout.api.AreaTreeOperator;
import org.fit.layout.api.AreaTreeProvider;
import org.fit.layout.cssbox.CSSBoxTreeBuilder;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.Page;
import org.xml.sax.SAXException;

/**
 * 
 * @author burgetr
 */
public class ScriptableProcessor
{
    private Map<String, AreaTreeProvider> providers;
    private Map<String, AreaTreeOperator> operators;

    private Page page;
    private AreaTree atree;
    
    
    public ScriptableProcessor()
    {
        findAreaTreeProviders();
        findAreaTreeOperators();
    }

    public Page renderPage(String urlstring, Dimension dim) throws MalformedURLException, IOException, SAXException
    {
        CSSBoxTreeBuilder build = new CSSBoxTreeBuilder(dim);
        build.parse(urlstring);
        page = build.getPage();
        return page;
    }

    //======================================================================================================
    // scripting initialization
    
    private void findAreaTreeProviders()
    {
        ServiceLoader<AreaTreeProvider> loader = ServiceLoader.load(AreaTreeProvider.class);
        Iterator<AreaTreeProvider> it = loader.iterator();
        providers = new HashMap<String, AreaTreeProvider>();
        while (it.hasNext())
        {
            AreaTreeProvider op = it.next();
            providers.put(op.getId(), op);
        }
    }
    
    private void findAreaTreeOperators()
    {
        ServiceLoader<AreaTreeOperator> loader = ServiceLoader.load(AreaTreeOperator.class);
        Iterator<AreaTreeOperator> it = loader.iterator();
        operators = new HashMap<String, AreaTreeOperator>();
        while (it.hasNext())
        {
            AreaTreeOperator op = it.next();
            operators.put(op.getId(), op);
        }
    }
    
    //======================================================================================================
    // scripting interface
    
    public List<String> getOperatorIds()
    {
        return new ArrayList<String>(operators.keySet());
    }
    
    public List<String> getProviderIds()
    {
        return new ArrayList<String>(providers.keySet());
    }
    
    public AreaTree initAreaTree(String providerName)
    {
        AreaTreeProvider provider = providers.get(providerName);
        if (provider != null)
        {
            atree = provider.createAreaTree(page);
            return atree;
        }
        else
            return null;
    }
    
    public void apply(String operatorName, Map<String, Object> params)
    {
        System.out.println("Apply: " + operatorName + " : " + params);
        System.out.println(params.keySet());
        Object o1 = params.get("useConsistentStyle");
        Object o2 = params.get("maxLineEmSpace");
        System.out.println(params.get("useConsistentStyle"));
        System.out.println(params.get("useConsistentStyle"));
    }
    
    //======================================================================================================
    // Block browser interface
    
    public Page getPage()
    {
        return page;
    }


    public AreaTree getAreaTree()
    {
        return atree;
    }

}
