/**
 * BaseProcessor.java
 *
 * Created on 5. 2. 2015, 9:34:28 by burgetr
 */
package org.fit.layout.process;

import java.util.Map;

import org.fit.layout.api.AreaTreeOperator;
import org.fit.layout.api.AreaTreeProvider;
import org.fit.layout.api.BoxTreeProvider;
import org.fit.layout.api.ServiceManager;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base of a processor. It takes care about the existing providers and operators and their invocation.
 * 
 * @author burgetr
 */
public abstract class BaseProcessor
{
    private static Logger log = LoggerFactory.getLogger(BaseProcessor.class);
    
    private Map<String, BoxTreeProvider> boxProviders;
    private Map<String, AreaTreeProvider> areaProviders;
    private Map<String, AreaTreeOperator> operators;

    private Page page;
    private AreaTree atree;
    

    public BaseProcessor()
    {
        boxProviders = ServiceManager.findBoxTreeProviders();
        areaProviders = ServiceManager.findAreaTreeProviders();
        operators = ServiceManager.findAreaTreeOperators();
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

    public void setPage(Page page)
    {
        this.page = page;
    }

    public AreaTree getAreaTree()
    {
        return atree;
    }

    public void setAreaTree(AreaTree atree)
    {
        this.atree = atree;
    }

    /**
     * Runs the default segmentation process with the default parameter values.
     * @return The resulting area tree or {@code null} for an unsuccessfull segmentation
     */
    public abstract AreaTree segmentPage();
    
    //======================================================================================================
    
    public Page renderPage(BoxTreeProvider provider, Map<String, Object> params)
    {
        ServiceManager.setServiceParams(provider, params);
        page = provider.getPage();
        atree = null; //destroy the old area tree if any
        return page;
    }
    
    public AreaTree initAreaTree(AreaTreeProvider provider, Map<String, Object> params)
    {
        ServiceManager.setServiceParams(provider, params);
        atree = provider.createAreaTree(page);
        return atree;
    }
    
    public void apply(AreaTreeOperator op, Map<String, Object> params)
    {
        if (atree != null)
        {
            ServiceManager.setServiceParams(op, params);
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
    

}
