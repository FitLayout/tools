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

/**
 * 
 * @author burgetr
 */
public class ScriptableProcessor
{
    private Map<String, AreaTreeOperator> operators;
    
    public ScriptableProcessor()
    {
        findAreaTreeOperators();
    }

    protected void findAreaTreeOperators()
    {
        ServiceLoader<AreaTreeOperator> loader = ServiceLoader.load(AreaTreeOperator.class);
        Iterator<AreaTreeOperator> it = loader.iterator();
        operators = new HashMap<String, AreaTreeOperator>();
        while (it.hasNext())
        {
            //System.out.println(it.next().getName());
            AreaTreeOperator op = it.next();
            operators.put(op.getId(), op);
        }
    }
    
    public List<String> getOperatorIds()
    {
        return new ArrayList<String>(operators.keySet());
    }
    
}
