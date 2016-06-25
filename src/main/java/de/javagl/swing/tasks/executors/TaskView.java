/*
 * www.javagl.de - Swing Task Utilities
 *
 * Copyright (c) 2013-2016 Marco Hutter - http://www.javagl.de
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
package de.javagl.swing.tasks.executors;

import java.awt.Color;

import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 * A class representing the view for a task that is executed 
 * in an {@link ObservableExecutorService}.<br>
 * <br>  
 * Instances of this class will be created by the
 * {@link ObservableExecutorPanel}, and passed to the 
 * {@link TaskViewHandler} that was assigned to the 
 * {@link ObservableExecutorPanel}, for further configuration.
 */
public final class TaskView
{
    /**
     * The text that should be displayed for the task
     */
    private String text;
    
    /**
     * The foreground (text) color of the task view
     */
    private Color foregroundColor;
    
    /**
     * The progress of the task, if available. A negative value indicates
     * that the progress is not known. 
     */
    private double progress = -1.0;
    
    /**
     * The throwable that may have been cause by the task
     */
    private Throwable throwable;
    
    /**
     * The list model that contains this task view
     */
    private final DefaultListModel<TaskView> listModel;
    
    /**
     * The list that displays this task view
     */
    private final JList<TaskView> list;
    
    /**
     * Default constructor
     * 
     * @param listModel The list model that contains this task view
     * @param list The list that displays this task view
     */
    TaskView(DefaultListModel<TaskView> listModel, JList<TaskView> list)
    {
        this.listModel = listModel;
        this.list = list;
    }
    
    /**
     * Set the text that should be displayed in this task view
     * 
     * @param text The text
     */
    public void setText(String text)
    {
        this.text = text;
        this.list.repaint();
    }
    
    /**
     * Returns the text that is displayed in this task view
     * 
     * @return The text
     */
    public String getText()
    {
        return text;
    }
    
    /**
     * Set the progress of the task computation. This should usually be
     * a value in [0,1], or a value &lt;0 to indicate that the progress
     * is not known.
     * 
     * @param progress The progress
     */
    public void setProgress(double progress)
    {
        this.progress = progress;
        this.list.repaint();
    }
    
    /**
     * Returns the progress of the task computation. See 
     * {@link #setProgress(double)}
     * 
     * @return The progress
     */
    public double getProgress()
    {
        return progress;
    }
    
    /**
     * Set the throwable that may have been caused by the task
     * 
     * @param throwable The throwable
     */
    public void setThrowable(Throwable throwable)
    {
        this.throwable = throwable;
    }
    
    /**
     * Returns the throwable that was caused by the task, or <code>null</code>
     * if no throwable is associated with the task
     * 
     * @return The throwable
     */
    public Throwable getThrowable()
    {
        return throwable;
    }

    /**
     * Set the foreground (text) color of this task view
     * 
     * @param color The color
     */
    public void setForegroundColor(Color color)
    {
        this.foregroundColor = color;
        this.list.repaint();
    }
    
    /**
     * Returns the foreground color of this task view, or <code>null</code>
     * if no foreground color has been set
     * 
     * @return The color
     */
    public Color getForegroundColor()
    {
        return foregroundColor;
    }
    
    /**
     * Cause this task view to be removed from the list that displayed it
     */
    public void remove()
    {
        listModel.removeElement(this);
    }
}