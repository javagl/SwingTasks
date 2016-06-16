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
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

/**
 * Utility methods related to observable executors
 */
public class ObservableExecutors
{
    /**
     * Create a new {@link ObservableExecutorService} with the given 
     * fixed pool size. See {@link Executors#newFixedThreadPool(int)}
     * for details. 
     *  
     * @param poolSize The pool size
     * @return The {@link ObservableExecutorService}
     */
    public static ObservableExecutorService newFixedThreadPool(int poolSize)
    {
        return new ObservableExecutorService(
            poolSize, poolSize, 
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(),
            Executors.defaultThreadFactory(), new AbortPolicy());
    }
    
    /**
     * Create a new {@link ObservableExecutorService} with a cached thread
     * pool. See {@link Executors#newCachedThreadPool()}.
     * 
     * @return The {@link ObservableExecutorService}
     */
    public static ObservableExecutorService newCachedThreadPool()
    {
        return new ObservableExecutorService(
            0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS, 
            new SynchronousQueue<Runnable>(),
            Executors.defaultThreadFactory(), new AbortPolicy());
    }
    
    /**
     * Utility method to obtain the task that may be wrapped in the 
     * given task. If the given task is an {@link ObservableTask},
     * then its <code>Runnable</code> or <code>Callable</code> will
     * be extracted, and returned if it is assignable to the given 
     * type. Otherwise, <code>null</code> is returned.
     * 
     * @param <T> The type of the inner task
     * 
     * @param task The task
     * @param type The expected type of the inner task
     * @return The inner task, or <code>null</code>
     */
    public static <T> T getInnerTask(Runnable task, Class<T> type)
    {
        if (task instanceof ObservableTask<?>)
        {
            ObservableTask<?> observableTask = (ObservableTask<?>)task;
            Runnable innerRunnable = observableTask.getRunnable();
            if (innerRunnable != null)
            {
                if (type.isAssignableFrom(innerRunnable.getClass()))
                {
                    return type.cast(innerRunnable);
                }
            }
            Callable<?> innerCallable = observableTask.getCallable();
            if (innerCallable != null)
            {
                if (type.isAssignableFrom(innerCallable.getClass()))
                {
                    return type.cast(innerCallable);
                }
            }
        }
        return null;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private ObservableExecutors()
    {
        // Private constructor to prevent instantiation
    }
    
}
