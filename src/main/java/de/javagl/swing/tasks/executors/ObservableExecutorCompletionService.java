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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;


/**
 * A {@link CompletionService} that may be wrapped around an 
 * {@link ObservableExecutorService}, and make sure that the tasks
 * that are actually submitted are {@link ObservableTask} instances.
 * 
 * @param <V> The type of the futures provided by this service
 */
public class ObservableExecutorCompletionService<V> 
    implements CompletionService<V> {

    /**
     * The {@link ObservableExecutorService} to which the tasks are submitted
     */
    private final ObservableExecutorService observableExecutorService;
    
    /**
     * The queue that will receive the completed results
     */
    private final BlockingQueue<Future<V>> completionQueue;

    /**
     * Extension of {@link ObservableTask} that puts the completed tasks
     * into the {@link #completionQueue}
     */
    private class QueueingObservableTask extends ObservableTask<Void> 
    {
        /**
         * The runnable future that this task was wrapped around
         */
        private final RunnableFuture<V> runnableFuture;

        /**
         * Creates a new instance that wraps the given task
         * 
         * @param runnableFuture The task
         */
        QueueingObservableTask(RunnableFuture<V> runnableFuture) 
        {
            super(runnableFuture, null);
            this.runnableFuture = runnableFuture;
        }
        
        @Override
        protected void done() 
        {
            super.done();
            completionQueue.add(runnableFuture); 
        }
    }

    /**
     * Creates a new instance that executes the tasks using the given 
     * {@link ObservableExecutorService}
     *
     * @param observableExecutorService The executor to use. May not be 
     * <code>null</code>.
     */
    public ObservableExecutorCompletionService(
        ObservableExecutorService observableExecutorService) 
    {
        this(observableExecutorService, new LinkedBlockingQueue<Future<V>>());
    }

    /**
     * Creates a new instance that executes the tasks using the given 
     * {@link ObservableExecutorService}, and puts the futures of the
     * completed tasks into the given (unbounded) queue
     *
     * @param observableExecutorService The executor to use. May not be 
     * <code>null</code>.
     * @param completionQueue The queue to receive the completed futures
     */
    public ObservableExecutorCompletionService(
        ObservableExecutorService observableExecutorService,
        BlockingQueue<Future<V>> completionQueue) 
    {
        Objects.requireNonNull(observableExecutorService, 
            "The observableExecutorService may not be null");
        Objects.requireNonNull(completionQueue, 
            "The completionQueue may not be null");
        this.observableExecutorService = observableExecutorService;
        this.completionQueue = completionQueue;
    }

    @Override
    public Future<V> submit(Callable<V> task)
    {
        Objects.requireNonNull(task, "The task may not be null");
        RunnableFuture<V> f = observableExecutorService.newTaskFor(task);
        observableExecutorService.execute(new QueueingObservableTask(f));
        return f;
    }

    @Override
    public Future<V> submit(Runnable task, V result)
    {
        Objects.requireNonNull(task, "The task may not be null");
        RunnableFuture<V> f =
            observableExecutorService.newTaskFor(task, result);
        observableExecutorService.execute(new QueueingObservableTask(f));
        return f;
    }

    @Override
    public Future<V> take() throws InterruptedException
    {
        return completionQueue.take();
    }

    @Override
    public Future<V> poll()
    {
        return completionQueue.poll();
    }

    @Override
    public Future<V> poll(long timeout, TimeUnit unit)
        throws InterruptedException
    {
        return completionQueue.poll(timeout, unit);
    }

}
