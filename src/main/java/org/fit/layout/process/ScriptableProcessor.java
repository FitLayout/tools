/**
 * ScriptableProcessor.java
 *
 * Created on 14. 1. 2015, 14:52:04 by burgetr
 */
package org.fit.layout.process;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.fit.layout.api.AreaTreeOperator;
import org.fit.layout.api.AreaTreeProvider;
import org.fit.layout.api.BoxTreeProvider;
import org.fit.layout.cssbox.CSSBoxTreeBuilder;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * 
 * @author burgetr
 */
public class ScriptableProcessor
{
    private static Logger log = LoggerFactory.getLogger(ScriptableProcessor.class);

    private Map<String, BoxTreeProvider> boxProviders;
    private Map<String, AreaTreeProvider> areaProviders;
    private Map<String, AreaTreeOperator> operators;

    private BufferedReader rin;
    private PrintWriter wout;
    private PrintWriter werr;
    
    private Page page;
    private AreaTree atree;
    
    private ScriptEngine engine;
    
    
    public ScriptableProcessor()
    {
        rin = new BufferedReader(new InputStreamReader(System.in));
        wout = new PrintWriter(System.out);
        werr = new PrintWriter(System.err);
        findBoxTreeProviders();
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

    public Map<String, BoxTreeProvider> getBoxProviders()
    {
        return boxProviders;
    }

    public void setBoxProviders(Map<String, BoxTreeProvider> boxProviders)
    {
        this.boxProviders = boxProviders;
    }

    public Map<String, AreaTreeProvider> getAreaProviders()
    {
        return areaProviders;
    }

    //======================================================================================================
    
    protected void treesCompleted()
    {
        //this is called when the tree creation is finished
    }
    
    //======================================================================================================
    // scripting initialization
    
    private void findBoxTreeProviders()
    {
        ServiceLoader<BoxTreeProvider> loader = ServiceLoader.load(BoxTreeProvider.class);
        Iterator<BoxTreeProvider> it = loader.iterator();
        boxProviders = new HashMap<String, BoxTreeProvider>();
        while (it.hasNext())
        {
            BoxTreeProvider op = it.next();
            boxProviders.put(op.getId(), op);
        }
    }
    
    public void setAreaProviders(Map<String, AreaTreeProvider> areaProviders)
    {
        this.areaProviders = areaProviders;
    }

    public Map<String, AreaTreeOperator> getOperators()
    {
        return operators;
    }

    public void setOperators(Map<String, AreaTreeOperator> operators)
    {
        this.operators = operators;
    }

    private void findAreaTreeProviders()
    {
        ServiceLoader<AreaTreeProvider> loader = ServiceLoader.load(AreaTreeProvider.class);
        Iterator<AreaTreeProvider> it = loader.iterator();
        areaProviders = new HashMap<String, AreaTreeProvider>();
        while (it.hasNext())
        {
            AreaTreeProvider op = it.next();
            areaProviders.put(op.getId(), op);
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
    
    public List<String> getBoxProviderIds()
    {
        return new ArrayList<String>(boxProviders.keySet());
    }
    
    public List<String> getAreaProviderIds()
    {
        return new ArrayList<String>(areaProviders.keySet());
    }

    public Page renderPage(String providerName, Map<String, Object> params)
    {
        BoxTreeProvider provider = boxProviders.get(providerName);
        if (provider != null)
        {
            for (Map.Entry<String, Object> entry : params.entrySet())
            {
                provider.setParam(entry.getKey(), entry.getValue());
            }
            page = provider.getPage();
            return page;
        }
        else
        {
            log.error("Unknown box tree provider: " + providerName);
            return null;
        }
    }
    
    public AreaTree initAreaTree(String providerName)
    {
        AreaTreeProvider provider = areaProviders.get(providerName);
        if (provider != null)
        {
            atree = provider.createAreaTree(page);
            return atree;
        }
        else
        {
            log.error("Unknown area tree provider: " + providerName);
            return null;
        }
    }
    
    public void apply(String operatorName, Map<String, Object> params)
    {
        System.out.println("Apply: " + operatorName + " : " + params);
        System.out.println(params.keySet());
        Object o1 = params.get("useConsistentStyle");
        Object o2 = params.get("maxLineEmSpace");

        AreaTreeOperator op = operators.get(operatorName);
        if (op != null)
        {
            if (atree != null)
            {
                for (Map.Entry<String, Object> entry : params.entrySet())
                {
                    op.setParam(entry.getKey(), entry.getValue());
                }
                op.apply(atree);
            }
            else
                log.error("Couldn't apply " + operatorName + ": no area tree");
        }
        else
            log.error("Unknown operator " + operatorName);
        
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

    //======================================================================================================
    // Script invocation

    protected ScriptEngine getEngine()
    {
        if (engine == null)
        {
            ScriptEngineManager factory = new ScriptEngineManager();
            engine = factory.getEngineByName("JavaScript");
            engine.put("proc", this);
        }
        return engine;
    }
    
    public void setIO(Reader in, Writer out, Writer err)
    {
        rin = new BufferedReader(in);
        wout = new PrintWriter(out);
        werr = new PrintWriter(err);
        
        ScriptContext ctx = getEngine().getContext();
        ctx.setReader(rin);
        ctx.setWriter(wout);
        ctx.setErrorWriter(werr);
    }
    
    public void flushIO()
    {
        wout.flush();
        werr.flush();
    }
    
    public void put(String var, Object obj)
    {
        getEngine().put(var, obj);
    }
    
    public boolean execInternal(String scriptName) throws ScriptException
    {
        InputStream is = ClassLoader.getSystemResourceAsStream(scriptName);
        if (is != null)
        {
            getEngine().eval(new InputStreamReader(is));
            return true;
        }
        else
        {
            log.error("Couldn't access internal script " + scriptName);
            return false;
        }
    }

    public boolean execCommand(String command) throws ScriptException
    {
        getEngine().eval(command);
        return true;
    }
    
}
