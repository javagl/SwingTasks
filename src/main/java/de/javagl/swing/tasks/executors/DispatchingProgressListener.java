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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.javagl.swing.tasks.ProgressListener;

/**
 * Implementation of a {@link ProgressListener} that dispatches all
 * changes to other {@link ProgressListener}s
 */
class DispatchingProgressListener implements ProgressListener
{
    /**
     * The list of {@link ProgressListener}s to dispatch to
     */
    private final List<ProgressListener> progressListeners;
    
    /**
     * Default constructor
     */
    DispatchingProgressListener()
    {
        this.progressListeners = new CopyOnWriteArrayList<ProgressListener>();
    }
    
    /**
     * Add the given {@link ProgressListener} to be informed about all 
     * changes that this instance received.
     * 
     * @param progressListener The {@link ProgressListener} to add
     */
    void addProgressListener(ProgressListener progressListener)
    {
        this.progressListeners.add(progressListener);
    }
    
    /**
     * Remove the given {@link ProgressListener} to no longer be informed
     * about the changes that this instance received.
     * 
     * @param progressListener The {@link ProgressListener} to remove
     */
    void removeProgressListener(ProgressListener progressListener)
    {
        this.progressListeners.remove(progressListener);
    }
    

    @Override
    public void messageChanged(String message)
    {
        for (ProgressListener progressListener : progressListeners)
        {
            progressListener.messageChanged(message);
        }
    }

    @Override
    public void progressChanged(double progress)
    {
        for (ProgressListener progressListener : progressListeners)
        {
            progressListener.progressChanged(progress);
        }
    }
    
}