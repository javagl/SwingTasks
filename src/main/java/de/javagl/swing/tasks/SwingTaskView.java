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
 * Interface for the communication with a view for a {@link SwingTask}.
 * This view may, for example, be a dialog. Such a view is maintained
 * by a {@link SwingTaskExecutor}. During the execution of the 
 * {@link SwingTask}, the {@link SwingTaskExecutor} will show, update 
 * and hide this view according to its settings. 
 */
public interface SwingTaskView
{
    /**
     * Will be called when the task was started and the decision to show
     * the task view was made.
     */
    void show();
    
    /**
     * Set the current progress of the {@link SwingTask}. This will
     * be a value between 0.0 and 1.0 (inclusive), or a value &lt;0.0
     * when the progress is not known.
     * 
     * @param progress The progress of the task
     */
    void setProgress(double progress);
    
    /**
     * Set the message that should be displayed in this view
     * 
     * @param message The message
     */
    void setMessage(String message);
    
    /**
     * Will be called when the task finished. This will usually cause the
     * view to be made invisible and disposed.
     * 
     * @param t An exception that may have been caused by the task. If the
     * task completed normally, this will be <code>null</code>. 
     */
    void taskFinished(Throwable t);
}