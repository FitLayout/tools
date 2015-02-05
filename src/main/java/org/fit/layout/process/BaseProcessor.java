/**
 * BaseProcessor.java
 *
 * Created on 5. 2. 2015, 9:34:28 by burgetr
 */
package org.fit.layout.process;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import org.fit.layout.api.AreaTreeOperator;
import org.fit.layout.api.AreaTreeProvider;
import org.fit.layout.api.BoxTreeProvider;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base of a processor. It takes care about the existing providers and operators and their invocation.
 * 
 * @author burgetr
 */
public class BaseProcessor
{
    private static Logger log = LoggerFactory.getLogger(BaseProcessor.class);
    
    private Map<String, BoxTreeProvider> boxProviders;
    private Map<String, AreaTreeProvider> areaProviders;
    private Map<String, AreaTreeOperator> operators;

    private Page page;
    private AreaTree atree;
    

    public BaseProcessor()
    {
        findBoxTreeProviders();
        findAreaTreeProviders();
        findAreaTreeOperators();
    }
    
    public Map<String, BoxTreeProvider> getBoxProviders()
    {
        return boxProviders;
    }

    public Map<String, AreaTreeProvider> getAreaProviders()
    {
        return areaProviders;
    }
    
    public Map<String, AreaTreeOperator> getOperators()
    {
        return operators;
    }

    public Page getPage()
    {
        return page;
    }

    public AreaTree getAreaTree()
    {
        return atree;
    }

    //======================================================================================================
    
    public Page renderPage(BoxTreeProvider provider, Map<String, Object> params)
    {
        for (Map.Entry<String, Object> entry : params.entrySet())
        {
            provider.setParam(entry.getKey(), entry.getValue());
        }
        page = provider.getPage();
        return page;
    }
    
    public Page renderPage(String providerName, Map<String, Object> params)
    {
        BoxTreeProvider provider = boxProviders.get(providerName);
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
        AreaTreeProvider provider = areaProviders.get(providerName);
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
    
    public AreaTree initAreaTree(AreaTreeProvider provider, Map<String, Object> params)
    {
        for (Map.Entry<String, Object> entry : params.entrySet())
        {
            provider.setParam(entry.getKey(), entry.getValue());
        }
        atree = provider.createAreaTree(page);
        return atree;
    }
    
    public void apply(String operatorName, Map<String, Object> params)
    {
        /*System.out.println("Apply: " + operatorName + " : " + params);
        System.out.println(params.keySet());
        Object o1 = params.get("useConsistentStyle");
        Object o2 = params.get("maxLineEmSpace");*/

        AreaTreeOperator op = operators.get(operatorName);
        if (op != null)
        {
            apply(op, params);
        }
        else
            log.error("Unknown operator " + operatorName);
        
    }
    
    public void apply(AreaTreeOperator op, Map<String, Object> params)
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
            log.error("Couldn't apply " + op.getId() + ": no area tree");
    }
    
    //======================================================================================================
    
    protected void treesCompleted()
    {
        //this is called when the tree creation is finished
    }
    
    //======================================================================================================
    
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


}
