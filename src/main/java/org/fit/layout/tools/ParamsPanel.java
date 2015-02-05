package org.fit.layout.tools;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.fit.layout.api.ParametrizedOperation;


/**
 * A panel that lets the user to set the configurable parametres of a parametrized service.
 * 
 * @author burgetr
 */
public class ParamsPanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    
    private ParametrizedOperation op;
    private Vector<Component> before;
    private Vector<Component> after;
    private Map<String, Component> fields;

    public ParamsPanel()
    {
        super();
        op = null;
        before = new Vector<Component>();
        after = new Vector<Component>();
        fields = new HashMap<String, Component>();
        setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    /**
     * Assigns the parametrized operation and creates the input fields for all its parametres.
     * @param op The parametrized operation.
     */
    public void setOperation(ParametrizedOperation op)
    {
        this.op = op;
        clear();
        addFields();
        updateUI();
    }
    
    /**
     * Adds a component that will be added before the operation parametres. This should be used before
     * calling the {@code setOperation()} method.
     * @param component The component to be added.
     */
    public void addBefore(Component component)
    {
        before.add(component);
    }
    
    /**
     * Adds a component that will be added after the operation parametres. This should be used before
     * calling the {@code setOperation()} method.
     * @param component The component to be added.
     */
    public void addAfter(Component component)
    {
        after.add(component);
    }
    
    /**
     * Sets the value of the input field that corresponds to the given parameter.
     * @param name the parameter name
     * @param value the value to be set
     */
    public void setParam(String name, Object value)
    {
        Component comp = fields.get(name);
        if (comp != null)
        {
            if (comp instanceof JCheckBox)
            {
                if (value != null && value instanceof Boolean)
                    ((JCheckBox) comp).setSelected((Boolean) value);
            }
            else if (comp instanceof JSpinner)
            {
                if (value != null && (value instanceof Integer || value instanceof Float || value instanceof Double))
                    ((JSpinner) comp).setValue(value);
            }
            else if (comp instanceof JTextField)
            {
                if (value != null)
                    ((JTextField) comp).setText(value.toString());
            }
        }
    }
    
    /**
     * Obtains the current value of the input field that corresponds to the given parameter.
     * @param name the parameter name
     * @return the value of the parameter or {@code null} for unknown parameter
     */
    public Object getParam(String name)
    {
        Component comp = fields.get(name);
        if (comp != null)
        {
            if (comp instanceof JCheckBox)
                return ((JCheckBox) comp).isSelected();
            else if (comp instanceof JSpinner)
                return ((JSpinner) comp).getValue();
            else if (comp instanceof JTextField)
                return ((JTextField) comp).getText();
            else
                return null;
        }
        else
            return null;
    }
    
    /**
     * Obtains the current values of all the parametres.
     * @return a map from parameter name to the value
     */
    public Map<String, Object> getParams()
    {
        Map<String, Object> ret = new HashMap<String, Object>(fields.size());
        for (String param : fields.keySet())
        {
            ret.put(param, getParam(param));
        }
        return ret;
    }
    
    //======================================================================================
    
    /**
     * Removes all the input fields.
     */
    protected void clear()
    {
        removeAll();
    }
    
    /**
     * Adds all the configured input fields to the panel.
     */
    protected void addFields()
    {
        for (Component comp : before)
            add(comp);
        addParamFields();
        for (Component comp : after)
            add(comp);
    }

    /**
     * Adds the input fields that correspond to the operation parametres.
     */
    protected void addParamFields()
    {
        String[] params = op.getParamNames();
        ParametrizedOperation.ValueType[] types = op.getParamTypes();
        
        for (int i = 0; i < params.length; i++)
        {
            if (types[i] != ParametrizedOperation.ValueType.BOOLEAN)
            {
                JLabel lbl = new JLabel(params[i]);
                add(lbl);
            }
            
            String name = params[i];
            Object value = op.getParam(name); 
            Component comp = null;
            switch (types[i])
            {
                case BOOLEAN:
                    comp = new JCheckBox(name);
                    if (value != null && value instanceof Boolean)
                        ((JCheckBox) comp).setSelected((Boolean) value);
                    break;
                case FLOAT:
                    SpinnerNumberModel model = new SpinnerNumberModel(0.0, Double.MIN_VALUE, Double.MAX_VALUE, 0.1);
                    comp = new JSpinner(model);
                    if (value != null && (value instanceof Integer || value instanceof Float || value instanceof Double))
                        ((JSpinner) comp).setValue(value);
                    break;
                case INTEGER:
                    comp = new JSpinner();
                    if (value != null && value instanceof Integer)
                        ((JSpinner) comp).setValue(value);
                    break;
                case STRING:
                    comp = new JTextField(64);
                    if (value != null)
                        ((JTextField) comp).setText(value.toString());
                    break;
            }
            fields.put(name, comp);
            add(comp);
        }
    }
    
}
