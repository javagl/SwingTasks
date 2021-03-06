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

/**
 * Interface for classes that can create a {@link SwingTaskView}
 */
public interface SwingTaskViewFactory
{
    /**
     * Creates a new {@link SwingTaskView} based on the given 
     * {@link SwingTaskViewConfig}. This method will be called by a 
     * {@link SwingTaskExecutorBuilder} when the {@link SwingTaskExecutor} is 
     * created for a particular {@link SwingTask}. The 
     * {@link SwingTaskExecutor} will then call the methods of the 
     * {@link SwingTaskView} to update the view depending on the execution
     * status of the task.<br>
     * <br>
     * This method may not return <code>null</code>.  
     * 
     * @param swingTaskViewConfig The {@link SwingTaskViewConfig}
     * @return The {@link SwingTaskView}
     */
    SwingTaskView create(SwingTaskViewConfig swingTaskViewConfig);
}