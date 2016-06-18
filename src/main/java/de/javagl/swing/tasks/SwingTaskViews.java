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
package de.javagl.swing.tasks;

import javax.swing.JComponent;

/**
 * Methods to create {@link SwingTaskView} instances.
 */
public class SwingTaskViews
{
    /**
     * Create a default {@link SwingTaskView} with the given configuration.
     * 
     * @param swingTaskViewConfig The {@link SwingTaskViewConfig}
     * @return The {@link SwingTaskView}
     */
    public static SwingTaskView create(
        SwingTaskViewConfig swingTaskViewConfig)
    {
        return create(swingTaskViewConfig, null, true);
    }

    /**
     * Create a default {@link SwingTaskView} with the given configuration
     * and accessory component.
     * 
     * @param swingTaskViewConfig The {@link SwingTaskViewConfig}
     * @param accessory An optional accessory component. If this is not
     * <code>null</code>, then it will be shown in the center of the
     * resulting {@link SwingTaskView}
     * @return The {@link SwingTaskView}
     */
    public static SwingTaskView create(
        SwingTaskViewConfig swingTaskViewConfig, JComponent accessory)
    {
        return create(swingTaskViewConfig, accessory, true);
    }
    
    /**
     * Create a default {@link SwingTaskView} with the given configuration
     * and accessory component.
     * 
     * @param swingTaskViewConfig The {@link SwingTaskViewConfig}
     * @param accessory An optional accessory component. If this is not
     * <code>null</code>, then it will be shown in the center of the
     * resulting {@link SwingTaskView}
     * @param autoCloseWhenTaskFinished Whether the view should be closed
     * when the task was finished.
     * @return The {@link SwingTaskView}
     */
    public static SwingTaskView create(
        SwingTaskViewConfig swingTaskViewConfig, 
        JComponent accessory, boolean autoCloseWhenTaskFinished)
    {
        return new DefaultSwingTaskView(
            swingTaskViewConfig, accessory, autoCloseWhenTaskFinished);
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private SwingTaskViews()
    {
        // Private constructor to prevent instantiation
    }
}
