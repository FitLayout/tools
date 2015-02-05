/**
 * GUIProcessor.java
 *
 * Created on 5. 2. 2015, 10:59:17 by burgetr
 */
package org.fit.layout.tools;

import java.util.Vector;

import org.fit.layout.api.AreaTreeOperator;

/**
 * 
 * @author burgetr
 */
public class GUIProcessor extends BaseProcessor
{
    private Vector<AreaTreeOperator> selectedOperators;

    
    public GUIProcessor()
    {
        super();
        selectedOperators = new Vector<AreaTreeOperator>();
    }
    
    public Vector<AreaTreeOperator> getSelectedOperators()
    {
        return selectedOperators;
    }
}
