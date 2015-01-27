/**
 * Console.java
 *
 * Created on 27. 1. 2015, 13:40:51 by burgetr
 */
package org.fit.layout.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;

import javax.script.ScriptException;

import org.fit.layout.process.ScriptableProcessor;

/**
 * 
 * @author burgetr
 */
public class Console
{
    private ScriptableProcessor proc;
    
    public Console()
    {
        proc = new ScriptableProcessor();
        proc.put("system", this);
    }
    
    protected void interactiveSession(InputStream in, PrintStream out, PrintStream err) throws IOException
    {
        BufferedReader rin = new BufferedReader(new InputStreamReader(in));
        Writer wout = new OutputStreamWriter(out);
        Writer werr = new OutputStreamWriter(err);
        proc.setIO(rin, wout, werr);
        while (true)
        {
            out.println();
            out.print(prompt());
            String cmd = rin.readLine();
            if (cmd == null)
                break;
            try
            {
                proc.execCommand(cmd);
            } catch (ScriptException e) {
                out.println("Error: " + e.getMessage());
            }
        }
        out.println();
    }
    
    protected String prompt()
    {
        return "FitLayout> ";
    }
    
    public void exit()
    {
        System.exit(0);
    }
    
    //=============================================================================================
    
    public static void main(String[] args)
    {
        System.out.println("FitLayout interactive console");
        Console con = new Console();
        try
        {
            con.interactiveSession(System.in, System.out, System.err);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
