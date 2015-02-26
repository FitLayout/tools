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
import org.fit.layout.api.AreaTreeProvider;
import org.fit.layout.api.ServiceManager;
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
            execInternal("default_operators.js");
        } catch (ScriptException e) {
            log.error("Couldn't load config: " + e.getMessage());
        }
        configMode = false;
    }
    
    @Override
    public AreaTree segmentPage()
    {
        if (!getAreaProviders().isEmpty())
        {
            //just use the first available provider as the default
            AreaTreeProvider provider = getAreaProviders().values().iterator().next();
            log.warn("Using default area tree provider " + provider.getId());
            return segmentPage(provider, null);
        }
        else
            return null;
    }
    
    public AreaTree segmentPage(AreaTreeProvider provider, Map<String, Object> params)
    {
        setAreaTree(null);
        initAreaTree(provider, params);
        for (AreaTreeOperator op : selectedOperators)
        {
            apply(op, null); //no parametres--they should be already set from the GUI
        }
        treesCompleted();
        return getAreaTree();
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
                ServiceManager.setServiceParams(op, params);
                selectedOperators.add(op);
            }
        }
    }
    
    
    
}
