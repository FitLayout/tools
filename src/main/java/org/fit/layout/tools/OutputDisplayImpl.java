/**
 * OutputDisplayImpl.java
 *
 * Created on 31. 10. 2014, 13:47:46 by burgetr
 */
package org.fit.layout.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.Set;

import org.fit.layout.api.OutputDisplay;
import org.fit.layout.model.Area;
import org.fit.layout.model.Box;
import org.fit.layout.model.Rectangular;
import org.fit.layout.model.Tag;

/**
 * An output display implementation that shows the areas on a Graphics2D device.
 * 
 * @author burgetr
 */
public class OutputDisplayImpl implements OutputDisplay
{
    private Graphics2D g;
    private Color boxLogicalColor = Color.RED;
    private Color boxContentColor = Color.GREEN;
    private Color areaBoundsColor = Color.MAGENTA;
    
    public OutputDisplayImpl(Graphics2D g)
    {
        this.g = g;
    }

    public Graphics2D getGraphics()
    {
        return g;
    }

    //=================================================================================

    @Override
    public void drawExtent(Box box)
    {
        //draw the visual content box
        g.setColor(boxLogicalColor);
        Rectangular r = box.getBounds();
        g.drawRect(r.getX1(), r.getY1(), r.getWidth() - 1, r.getHeight() - 1);
        
        //draw the visual content box
        g.setColor(boxContentColor);
        r = box.getVisualBounds();
        g.drawRect(r.getX1(), r.getY1(), r.getWidth() - 1, r.getHeight() - 1);
    }
    
    @Override
    public void drawExtent(Area area)
    {
        Rectangular bounds = area.getBounds();
        Color c = g.getColor();
        g.setColor(areaBoundsColor);
        g.drawRect(bounds.getX1(), bounds.getY1(), bounds.getWidth() - 1, bounds.getHeight() - 1);
        g.setColor(c);
    }

    @Override
    public void colorizeByTags(Area area, Set<Tag> s)
    {
        if (!s.isEmpty())
        {
            Rectangular bounds = area.getBounds();
            Color c = g.getColor();
            float step = (float) bounds.getHeight() / s.size();
            float y = bounds.getY1();
            for (Iterator<Tag> it = s.iterator(); it.hasNext();)
            {
                Tag tag = it.next();
                g.setColor(stringColor(tag.getValue()));
                g.fillRect(bounds.getX1(), (int) y, bounds.getWidth(), (int) (step+0.5));
                y += step;
            }
            g.setColor(c);
        }
    }

    @Override
    public void colorizeByClass(Area area, String cname)
    {
        if (cname != null && !cname.equals("") && !cname.equals("none"))
        {
            Rectangular bounds = area.getBounds();
            Color c = g.getColor();
            float step = (float) bounds.getHeight();
            float y = bounds.getY1();
            g.setColor(stringColor(cname));
            g.fillRect(bounds.getX1(), (int) y, bounds.getWidth(), (int) (step+0.5));
            g.setColor(c);
        }
    }

    @Override
    public void drawLayout(Area area)
    {
        //TODO the area should provide an implementation of this (it depends on how the areas are internally organized)
    }

    protected Color stringColor(String cname)                                 
    {                                                                            
            if (cname == null || cname.equals(""))       
                    return Color.WHITE;                                                 
                                                                                 
            String s = new String(cname);                                        
            while (s.length() < 6) s = s + s;                                    
            int r = (int) s.charAt(0) *  (int) s.charAt(1);                      
            int g = (int) s.charAt(2) *  (int) s.charAt(3);                      
            int b = (int) s.charAt(4) *  (int) s.charAt(5);                      
            Color ret = new Color(100 + (r % 150), 100 + (g % 150), 100 + (b % 150), 128);              
            //System.out.println(cname + " => " + ret.toString());               
            return ret;                                                          
    } 
    
    
}
