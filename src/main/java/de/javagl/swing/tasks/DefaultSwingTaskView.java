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

import javax.swing.JComponent;

/**
 * Default implementation of a {@link SwingTaskView} that is backed
 * by a {@link SwingTaskDialog}
 */
class DefaultSwingTaskView implements SwingTaskView
{
    /**
     * The underlying {@link SwingTaskDialog}
     */
    private final SwingTaskDialog swingTaskDialog;
    
    /**
     * Creates a new view based on the given {@link SwingTaskViewConfig}
     * 
     * @param swingTaskViewConfig The {@link SwingTaskViewConfig}
     * @param accessory An optional accessory component. If this is not
     * <code>null</code>, then it will be shown in the center of the
     * resulting {@link SwingTaskDialog}.
     * @param autoCloseWhenTaskFinished Whether the dialog should be closed
     * when the task was finished.
     */
    DefaultSwingTaskView(
        SwingTaskViewConfig swingTaskViewConfig, 
        JComponent accessory,
        boolean autoCloseWhenTaskFinished)
    {
        swingTaskDialog = new SwingTaskDialog(
            swingTaskViewConfig.getParentWindow(),
            swingTaskViewConfig.getParentComponent(),
            swingTaskViewConfig.getTitle(),
            swingTaskViewConfig.getSwingTask(),
            swingTaskViewConfig.isModal(), 
            swingTaskViewConfig.isCancelable(),
            accessory, autoCloseWhenTaskFinished);
    }

    @Override
    public void show()
    {
        swingTaskDialog.setVisible(true);
    }
    
    @Override
    public void setProgress(double progress)
    {
        swingTaskDialog.setProgress(progress);
    }

    @Override
    public void setMessage(String message)
    {
        swingTaskDialog.setMessage(message);
    }

    @Override
    public void taskFinished(Throwable t)
    {
        swingTaskDialog.taskFinished(t);
    }

}
