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

import java.awt.Component;
import java.awt.Window;

/**
 * Default implementation of a {@link SwingTaskViewConfig}
 */
class DefaultSwingTaskViewConfig implements SwingTaskViewConfig
{
    /**
     * The {@link SwingTask}
     */
    private final SwingTask<?,?> swingTask;
    
    /**
     * The title
     */
    private final String title;
    
    /**
     * The optional parent window
     */
    private final Window parentWindow;
    
    /**
     * The optional parent component
     */
    private final Component parentComponent;
    
    /**
     * Whether the task is modal
     */
    private final boolean modal;
    
    /**
     * Whether the task is cancelable
     */
    private final boolean cancelable;    

    /**
     * Creates a new instance
     * 
     * @param swingTask The {@link SwingTask}
     * @param title The title
     * @param parentWindow The optional parent window
     * @param parentComponent The parent component
     * @param modal Whether the task is modal
     * @param cancelable Whether the task is cancelable
     */
    DefaultSwingTaskViewConfig(
        SwingTask<?, ?> swingTask, String title,
        Window parentWindow, Component parentComponent, boolean modal,
        boolean cancelable)
    {
        super();
        this.swingTask = swingTask;
        this.title = title;
        this.parentWindow = parentWindow;
        this.parentComponent = parentComponent;
        this.modal = modal;
        this.cancelable = cancelable;
    }
    
    @Override
    public SwingTask<?, ?> getSwingTask()
    {
        return swingTask;
    }

    @Override
    public String getTitle()
    {
        return title;
    }

    @Override
    public Window getParentWindow()
    {
        return parentWindow;
    }

    @Override
    public Component getParentComponent()
    {
        return parentComponent;
    }

    @Override
    public boolean isModal()
    {
        return modal;
    }

    @Override
    public boolean isCancelable()
    {
        return cancelable;
    }
}