/**
 * ScriptableProcessor.java
 *
 * Created on 14. 1. 2015, 14:52:04 by burgetr
 */
package org.fit.layout.process;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.fit.layout.api.AreaTreeOperator;
import org.fit.layout.api.AreaTreeProvider;
import org.fit.layout.api.BoxTreeProvider;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author burgetr
 */
public class ScriptableProcessor extends BaseProcessor
{
    private static Logger log = LoggerFactory.getLogger(ScriptableProcessor.class);

    private BufferedReader rin;
    private PrintWriter wout;
    private PrintWriter werr;
    private ScriptEngine engine;
    
    
    public ScriptableProcessor()
    {
        rin = new BufferedReader(new InputStreamReader(System.in));
        wout = new PrintWriter(System.out);
        werr = new PrintWriter(System.err);
    }

    //======================================================================================================
    // scripting interface
    
    public List<String> getOperatorIds()
    {
        return new ArrayList<String>(getOperators().keySet());
    }
    
    public List<String> getBoxProviderIds()
    {
        return new ArrayList<String>(getBoxProviders().keySet());
    }
    
    public List<String> getAreaProviderIds()
    {
        return new ArrayList<String>(getAreaProviders().keySet());
    }
    
    public Page renderPage(String providerName, Map<String, Object> params)
    {
        BoxTreeProvider provider = getBoxProviders().get(providerName);
        if (provider != null)
        {
            return renderPage(provider, params);
        }
        else
        {
            log.error("Unknown box tree provider: " + providerName);
            return null;
        }
    }
    
    public AreaTree initAreaTree(String providerName, Map<String, Object> params)
    {
        AreaTreeProvider provider = getAreaProviders().get(providerName);
        if (provider != null)
        {
            return initAreaTree(provider, params);
        }
        else
        {
            log.error("Unknown area tree provider: " + providerName);
            return null;
        }
    }
    
    public void apply(String operatorName, Map<String, Object> params)
    {
        /*System.out.println("Apply: " + operatorName + " : " + params);
        System.out.println(params.keySet());
        Object o1 = params.get("useConsistentStyle");
        Object o2 = params.get("maxLineEmSpace");*/

        AreaTreeOperator op = getOperators().get(operatorName);
        if (op != null)
        {
            apply(op, params);
        }
        else
            log.error("Unknown operator " + operatorName);
        
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
