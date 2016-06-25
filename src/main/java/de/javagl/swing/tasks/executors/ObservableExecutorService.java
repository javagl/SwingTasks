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
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * An executor service that may notify {@link ExecutorObserver} instances
 * about the progress of processing the submitted tasks.<br>
 * <br>
 * Note that the task observation will <b>only</b> be possible for tasks
 * that are actually submitted using {@link #submit(Runnable)},
 * {@link #submit(Callable)} or {@link #submit(Runnable, Object)}. It
 * will <b>not</b> be possible for tasks that are submitted with 
 * {@link #execute(Runnable)}.<br>
 * <br>
 * Instances of this class may be passed to an {@link ObservableExecutorPanel},
 * which will track the task execution and display information about the
 * active tasks and in the UI. If the tasks that are submitted to this 
 * executor service implement the {@link ProgressTask} interface (for 
 * example, instances of the {@link GenericProgressTask} class), 
 * then the UI may show additional information about the progress
 * of each individual task.
 */
public class ObservableExecutorService extends ThreadPoolExecutor
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(ObservableExecutorService.class.getName());
    
    /**
     * The counter for the active tasks
     */
    private final AtomicInteger activeTaskCounter = new AtomicInteger();
    
    /**
     * The {@link ExecutorObserver}s that will be informed about the
     * progress of the task execution
     */
    private final List<ExecutorObserver> executorObservers;

    /**
     * Default constructor. See <code>ThreadPoolExecutor</code> for details.
     * 
     * @param corePoolSize The core pool size
     * @param maximumPoolSize The maximum pool size
     * @param keepAliveTime The keep-alive time
     * @param unit The time unit
     * @param workQueue The work queue
     * @param threadFactory The thread factory
     * @param handler The rejected execution handler
     */
    ObservableExecutorService(
        int corePoolSize, int maximumPoolSize,
        long keepAliveTime, TimeUnit unit, 
        BlockingQueue<Runnable> workQueue, 
        ThreadFactory threadFactory, 
        RejectedExecutionHandler handler)
    {
        super(corePoolSize, maximumPoolSize,
            keepAliveTime, unit, workQueue, threadFactory, handler);
        
        this.executorObservers = new CopyOnWriteArrayList<ExecutorObserver>();
    }
    
    /**
     * Add the given {@link ExecutorObserver} to be informed about the
     * progress of the task execution.<br>
     * <br>
     * The given observer may not be <code>null</code>. If the observer
     * throws an exception in one of its methods, then an error message
     * will be printed. Beyond that, the exceptions will be ignored. 
     * 
     * @param executorObserver The {@link ExecutorObserver}
     */
    public final void addExecutorObserver(ExecutorObserver executorObserver)
    {
        Objects.requireNonNull(executorObserver, 
            "The executorObserver may not be null");
        executorObservers.add(executorObserver);
    }
    
    /**
     * Remove the given {@link ExecutorObserver}
     * 
     * @param executorObserver The {@link ExecutorObserver}
     */
    public final void removeExecutorObserver(ExecutorObserver executorObserver)
    {
        executorObservers.remove(executorObserver);
    }
    
    @Override
    protected final <V> RunnableFuture<V> newTaskFor(Callable<V> c)
    {
        return new ObservableTask<V>(c);
    }

    @Override
    protected final <V> RunnableFuture<V> newTaskFor(Runnable r, V v)
    {
        return new ObservableTask<V>(r, v);
    }    
    
    @Override
    public void execute(Runnable command)
    {
        activeTaskCounter.incrementAndGet();
        for (ExecutorObserver executorObserver : executorObservers)
        {
            try
            {
                executorObserver.scheduled(command);
            }
            catch (Exception e)
            {
                logger.severe(
                    "Error when notifying observer: " + e.getMessage());
                e.printStackTrace();
            }
        }
        super.execute(command);       
    }
    
    @Override
    protected void beforeExecute(Thread t, Runnable r)
    {
        for (ExecutorObserver executorObserver : executorObservers)
        {
            try
            {
                executorObserver.beforeExecute(t, r);
            }
            catch (Exception e)
            {
                logger.severe(
                    "Error when notifying observer: " + e.getMessage());
                e.printStackTrace();
            }
        }
        super.beforeExecute(t, r);
    }
    
    @Override
    protected void afterExecute(Runnable r, Throwable t)
    {
        super.afterExecute(r, t);
        Throwable throwable = t;
        
        // Unwrap possible exceptions if the runnable is actually a Future
        if (t == null && r instanceof Future<?>)
        {
            try
            {
                Future<?> future = (Future<?>)r;
                future.get();
            } 
            catch (CancellationException e)
            {
                throwable = e;
            } 
            catch (ExecutionException e)
            {
                throwable = e.getCause();
            } 
            catch (InterruptedException e)
            {
                throwable = e;
            }
        }
        
        for (ExecutorObserver executorObserver : executorObservers)
        {
            try
            {
                executorObserver.afterExecute(r, throwable);
            }
            catch (Exception e)
            {
                logger.severe(
                    "Error when notifying observer: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        if (activeTaskCounter.decrementAndGet() == 0)
        {
            for (ExecutorObserver executorObserver : executorObservers)
            {
                try
                {
                    executorObserver.tasksFinished();
                }
                catch (Exception e)
                {
                    logger.severe(
                        "Error when notifying observer: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
    }
}