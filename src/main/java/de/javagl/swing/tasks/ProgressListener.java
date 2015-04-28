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
 * Interface for classes that can receive progress information. 
 * A <code>ProgressListener</code> may be attached to a 
 * {@link SwingTask} by calling 
 * {@link SwingTask#addProgressListener(ProgressListener)},
 * and then will receive progress information from the
 * {@link SwingTask} on the <i>Event Dispatch Thread</i>.
 */
public interface ProgressListener
{
    /**
     * Will be called when the progress message changed
     * 
     * @param message The new progress message
     */
    void messageChanged(String message);
    
    /**
     * Will be called when the progress changed. The given progress
     * will usually be a value between 0 and 1, indicating the 
     * progress, or a value &lt;0 if the progress is unknown. 
     * 
     * @param progress The progress.
     */
    void progressChanged(double progress);
}
