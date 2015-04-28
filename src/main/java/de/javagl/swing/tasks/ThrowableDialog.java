/*
 * www.javagl.de - Swing Task Utilities
 *
 * Copyright (c) 2013-2015 Marco Hutter - http://www.javagl.de
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package de.javagl.swing.tasks;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Utility class to show a dialog containing the stack trace of a throwable
 */
class ThrowableDialog
{
    /**
     * Shows a dialog containing information about the given throwable
     * 
     * @param parentComponent The parent component for the dialog
     * @param title The title of the dialog
     * @param throwable The throwable to show
     * @param thread The thread in which the error occurred
     */
    public static void show(
        Component parentComponent, String title, 
        Throwable throwable, Thread thread)
    {
        JOptionPane.showMessageDialog(parentComponent, 
            createThrowableComponent(throwable, thread), 
            title, JOptionPane.ERROR_MESSAGE);        
    }
    
    /**
     * Creates the component with the contents for the dialog, namely
     * a label and a text area containing the stack trace
     * 
     * @param throwable The throwable
     * @param thread The thread in which the error occurred
     * @return The component
     */
    private static JComponent createThrowableComponent(
        Throwable throwable, Thread thread)
    {
        JComponent c = new JPanel(new BorderLayout());
        
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("An exception occurred:<br />");
        sb.append("<br />");
        if (thread != null)
        {
            sb.append("Thread: "+thread+"<br />");
            sb.append("<br />");
        }
        sb.append("Stack trace:");
        sb.append("</html>");
        JLabel label = new JLabel(sb.toString());
        label.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        c.add(label, BorderLayout.NORTH);
        
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        String s = sw.getBuffer().toString();
        textArea.setText(s);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        Dimension d = scrollPane.getPreferredSize();
        scrollPane.setPreferredSize(
            new Dimension(d.width+40, Math.min(600, d.height)));
        c.add(scrollPane, BorderLayout.CENTER);
        
        return c;
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private ThrowableDialog()
    {
        // Private constructor to prevent instantiation
    }
}
