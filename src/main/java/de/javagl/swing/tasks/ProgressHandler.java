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
 * Interface for classes that can pass progress information from implementors
 * to a {@link SwingTask}.
 * <p> 
 * An instance of a <code>ProgressHandler</code> may be obtained
 * from a {@link SwingTask} by calling {@link SwingTask#getProgressHandler()}.
 * This <code>ProgressHandler</code> serves as a channel for informing
 * the {@link SwingTask} about progress changes. The {@link SwingTask}
 * will forward this information to its {@link ProgressListener}s on
 * the <i>Event Dispatch Thread</i>. 
 */
public interface ProgressHandler
{
    /**
     * Set the given progress message
     * 
     * @param message The progress message
     */
    void setMessage(String message);
    
    /**
     * Set the given progress. This should be a value between 0 and 1,
     * indicating the progress, or a value &lt;0 if the progress is
     * not known.
     * 
     * @param progress The progress
     */
    void setProgress(double progress);
}
