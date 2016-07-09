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

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Implementation of a future task that is used in an 
 * {@link ObservableExecutorService} to keep track of the runnable or
 * callable that it was created from.
 *
 * @param <V> The return type of this future
 */
class ObservableTask<V> extends FutureTask<V>
{
    /**
     * The optional callable that may have been given in the constructor
     */
    private final Callable<V> callable;
    
    /**
     * The optional runnable that may have been given in the constructor
     */
    private final Runnable runnable;
    
    /**
     * Creates a new observable task for the given callable
     * 
     * @param callable The callable
     */
    ObservableTask(Callable<V> callable)
    {
        super(callable);
        this.callable = callable;
        this.runnable = null;
    }
    
    /**
     * Create a new observable task for the given runnable
     * 
     * @param runnable The runnable
     * @param result The result
     */
    ObservableTask(Runnable runnable, V result)
    {
        super(runnable, result);
        this.runnable = runnable;
        this.callable = null;
    }
    
    /**
     * Returns the callable that was given in the constructor, or 
     * <code>null</code> if only a runnable was given
     * 
     * @return The callable
     */
    Callable<V> getCallable()
    {
        return callable;
    }
    
    /**
     * Returns the runnable that was given in the constructor, or 
     * <code>null</code> if only a callable was given
     * 
     * @return The callable
     */
    Runnable getRunnable()
    {
        return runnable;
    }
    
    @Override
    public String toString()
    {
        if (runnable != null)
        {
            return "ObservableTask[runnable=" + runnable + "]";
        }
        return "ObservableTask[callable=" + callable + "]";
    }
    
}