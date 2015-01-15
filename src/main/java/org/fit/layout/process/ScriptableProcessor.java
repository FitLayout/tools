/**
 * ScriptableProcessor.java
 *
 * Created on 14. 1. 2015, 14:52:04 by burgetr
 */
package org.fit.layout.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.fit.layout.api.AreaTreeOperator;
import org.fit.layout.api.AreaTreeProvider;

/**
 * 
 * @author burgetr
 */
public class ScriptableProcessor
{
    private Map<String, AreaTreeProvider> providers;
    private Map<String, AreaTreeOperator> operators;
    
    public ScriptableProcessor()
    {
        findAreaTreeProviders();
        findAreaTreeOperators();
    }

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
    
    public List<String> getOperatorIds()
    {
        return new ArrayList<String>(operators.keySet());
    }
    
    public List<String> getProviderIds()
    {
        return new ArrayList<String>(providers.keySet());
    }
    
}
