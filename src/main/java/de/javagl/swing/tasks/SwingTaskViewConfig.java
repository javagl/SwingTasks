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
 * Interface describing the configuration of a {@link SwingTaskView}.
 * An instance of such a class may be passed to a {@link SwingTaskViewFactory}
 * to create a new {@link SwingTaskView}.
 */
public interface SwingTaskViewConfig
{
    /**
     * Returns the {@link SwingTask} that is shown in the {@link SwingTaskView}
     * 
     * @return The {@link SwingTask}
     */
    SwingTask<?,?> getSwingTask();
    
    /**
     * Returns the title of the {@link SwingTaskView}
     * 
     * @return The title of the {@link SwingTaskView}
     */
    String getTitle();
    
    /**
     * Returns the <i>optional</i> parent window that should be used
     * for the {@link SwingTaskView}
     * 
     * @return The parent window, or <code>null</code>
     */
    Window getParentWindow();

    /**
     * Returns the <i>optional</i> parent component that should be used
     * for the {@link SwingTaskView}
     * 
     * @return The parent component, or <code>null</code>
     */
    Component getParentComponent();
    
    /**
     * Returns whether the task is modal
     * 
     * @return Whether the task is modal
     */
    boolean isModal();
    
    /**
     * Returns whether the task is cancelable
     * 
     * @return Whether the task is cancelable
     */
    boolean isCancelable();
}