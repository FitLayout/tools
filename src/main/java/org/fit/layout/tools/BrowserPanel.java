/**
 * BrowserPanel.java
 *
 * Created on 4.9.2007, 13:57:43 by burgetr
 */
package org.fit.layout.tools;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import org.fit.layout.model.Page;


/**
 * @author burgetr
 *
 */
public class BrowserPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    protected Page page;
    
    public BrowserPanel(Page page)
    {
        this.page = page;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        OutputDisplayImpl disp = new OutputDisplayImpl((Graphics2D) g);
        disp.drawPage(page);
    }

}
