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

import java.util.Objects;
import java.util.concurrent.Callable;

import de.javagl.swing.tasks.ProgressListener;

/**
 * Implementation of a {@link ProgressTask} that also implements the
 * <code>Callable</code> interface, and delegates its work to another
 * <code>Callable</code>.
 *
 * @param <T> The return type
 */
public final class GenericProgressTask<T> implements Callable<T>, ProgressTask
{
    /**
     * The description of this task, to be shown in the UI
     */
    private final String description;
    
    /**
     * The {@link DispatchingProgressListener} that will dispatch changes
     * to the other registered {@link ProgressListener}s
     */
    private final DispatchingProgressListener dispatchingProgressListener;
    
    /**
     * The callable that does the actual work
     */
    private Callable<T> callable;
    
    /**
     * Creates a new task with the given description. The description should
     * be a short string that is suitable for using it as a label for this
     * task, in a UI. The callable that should be executed by this task can be 
     * set with {@link #setCallable(Callable)}. If no callable is set, then
     * this task simply returns <code>null</code>.
     * 
     * @param description The description
     */
    public GenericProgressTask(String description)
    {
        this.description = description; 
        this.dispatchingProgressListener = new DispatchingProgressListener();
    }
    
    /**
     * Set the callable that should be executed by this task
     * 
     * @param callable The callable
     */
    public void setCallable(Callable<T> callable)
    {
        this.callable = Objects.requireNonNull(
            callable, "The callable may not be null");
        this.callable = callable;
    }
    
    /**
     * Returns a {@link ProgressListener} that may be informed about 
     * changes in the progress of the callable that was given to this
     * task. It will forward all progress changes to the 
     * {@link ProgressListener}s that have been registered for
     * this task.
     * 
     * @return The dispatching {@link ProgressListener}
     */
    public ProgressListener getDispatchingProgressListener()
    {
        return dispatchingProgressListener;
    }
    
    @Override
    public T call() throws Exception
    {
        if (callable == null)
        {
            return null;
        }
        return callable.call();
    }
    
    @Override
    public String getDescription()
    {
        return description;
    }
    
    @Override
    public void addProgressListener(ProgressListener progressListener)
    {
        this.dispatchingProgressListener.addProgressListener(progressListener);
    }
    
    @Override
    public void removeProgressListener(ProgressListener progressListener)
    {
        this.dispatchingProgressListener.removeProgressListener(progressListener);
    }
    
    @Override
    public String toString()
    {
        return "GenericProgressTask[description="+description+"]";
    }
}