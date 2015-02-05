/**
 * GUIProcessor.java
 *
 * Created on 5. 2. 2015, 10:59:17 by burgetr
 */
package org.fit.layout.process;

import java.util.Map;
import java.util.Vector;

import javax.script.ScriptException;

import org.fit.layout.api.AreaTreeOperator;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author burgetr
 */
public class GUIProcessor extends ScriptableProcessor
{
    private static Logger log = LoggerFactory.getLogger(GUIProcessor.class);
    
    private Vector<AreaTreeOperator> selectedOperators;
    private boolean configMode = false;
    
    public GUIProcessor()
    {
        super();
        selectedOperators = new Vector<AreaTreeOperator>();
        loadConfig();
    }
    
    public Vector<AreaTreeOperator> getSelectedOperators()
    {
        return selectedOperators;
    }

    public void loadConfig()
    {
        configMode = true;
        try
        {
            execInternal("op_defaults.js");
        } catch (ScriptException e) {
            log.error("Couldn't load config: " + e.getMessage());
        }
        configMode = false;
    }
    
    //========================================================================================
    
    @Override
    public Page renderPage(String providerName, Map<String, Object> params)
    {
        if (!configMode)
            return super.renderPage(providerName, params);
        else
            return getPage();
    }

    @Override
    public AreaTree initAreaTree(String providerName, Map<String, Object> params)
    {
        if (!configMode)
            return super.initAreaTree(providerName, params);
        else
            return getAreaTree();
    }

    @Override
    public void apply(String operatorName, Map<String, Object> params)
    {
        if (!configMode)
            super.apply(operatorName, params);
        else
        {
            AreaTreeOperator op = getOperators().get(operatorName);
            if (op != null)
            {
                setOperatorParams(op, params);
                selectedOperators.add(op);
            }
        }
    }
    
    
    
}
