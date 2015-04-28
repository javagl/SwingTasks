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

import java.beans.PropertyChangeListener;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * A background task for Swing. A <code>SwingTask</code> is very
 * similar to a <code>SwingWorker</code>: It offers a method 
 * {@link #doInBackground()} that may be overridden and will be 
 * executed in a background thread, and implements the 
 * <code>RunnableFuture</code> interface.
 * <p>
 * Additionally, it maintains a {@link ProgressHandler} that may be obtained 
 * by calling the {@link #getProgressHandler()} method, and may be used 
 * by implementors to inform observers of this task about the progress of 
 * the computation. These observers are {@link ProgressListener}s that 
 * have been {@link #addProgressListener(ProgressListener) added} to this
 * task, and they will be informed about the progress on the 
 * <i>Event Dispatch Thread</i>.
 * <p>
 * It also maintains lists of callbacks that may be informed on the
 * <i>Event Dispatch Thread</i>. These callbacks serve as a 
 * runtime-alternative for overriding the {@link #process(List)} 
 * and the {@link #done()} method respectively: The 
 * {@link #addProcessCallback(Consumer) process callbacks} are informed 
 * about intermediate results that are produced by this task and published 
 * via the {@link #publish(Object...)} method. 
 * The {@link #addDoneCallback(Consumer) done callbacks} are 
 * informed when this task is {@link #done()}. 
 * When any of the callbacks throws an exception, the subsequent
 * behavior of this task is undefined.
 *
 * @param <T> The result type
 * @param <V> The intermediate results type
 */
public abstract class SwingTask<T, V> implements RunnableFuture<T>  
{
    /**
     * The SwingWorker that is doing the actual work 
     */
    private class SwingTaskWorker extends SwingWorker<T, V>
    {
        @Override
        protected T doInBackground() throws Exception
        {
            return doTaskInBackground();
        }

        /**
         * Forwards the given chunks to the {@link #publish(Object...)}
         * method of this SwingWorker
         * 
         * @param chunks The chunks
         */
        @SafeVarargs
        final void doPublish(V ... chunks)
        {
            SwingTaskWorker.this.publish(chunks);
        }
        
        @Override
        protected void process(List<V> chunks)
        {
            callProcess(chunks);
        }
        
        @Override
        protected void done()
        {
            callDone();
        }
    }
    
    /**
     * The underlying swing worker
     */
    private final SwingTaskWorker swingTaskWorker;
    
    /**
     * The {@link ProgressListener}s that want to be informed about
     * the progress
     */
    private final List<ProgressListener> progressListeners;
    
    /**
     * The current progress message
     */
    private volatile String message;
    
    /**
     * The current progress value
     */
    private volatile double progress;

    /**
     * The {@link ProgressHandler} that serves as a channel to
     * collect progress information and update this task
     * and inform the attached {@link ProgressListener}s
     */
    private final ProgressHandler progressHandler = new ProgressHandler()
    {
        @Override
        public void setProgress(double progress)
        {
            SwingTask.this.setProgress(progress);
        }
        
        @Override
        public void setMessage(String message)
        {
            SwingTask.this.setMessage(message);
        }
    };
    
    /**
     * Internal interface to inform a {@link SwingTaskExecutor} about
     * the execution status
     */
    interface SwingTaskListener 
    {
        /**
         * Will be called by the background thread when the
         * execution started
         */
        void started();
        
        /**
         * Will be called by the background thread when the
         * execution finished
         * 
         * @param t An <code>ExecutionException</code> containing the
         * exception that was thrown, or <code>null</code> if
         * no exception was thrown 
         */
        void finished(Throwable t);
        
        /**
         * Will be called by any thread when the message or
         * progress was updated
         */
        void updated();
    }
    
    /**
     * The {@link SwingTaskListener} that will be informed about
     * the execution status
     */
    private SwingTaskListener swingTaskListener;
    
    /**
     * A handler for uncaught exceptions that happen in the
     * swing worker
     */
    private UncaughtExceptionHandler uncaughtExceptionHandler;

    /**
     * The stack trace of where the task was scheduled
     */
    private StackTraceElement schedulingStackTrace[];
    
    /**
     * The list of callbacks that will be called on the Event Dispatch Thread,
     * with the intermediate results that are passed to the 
     * {@link #publish(Object...)} method
     */
    private final List<Consumer<List<V>>> processCallbacks;
    
    /**
     * The list of callbacks that will be called on the Event Dispatch Thread,
     * when this task is {@link #done()}.
     */
    private final List<Consumer<SwingTask<T, V>>> doneCallbacks;
    
    
    /**
     * Creates a new swing task with a default status message
     */
    protected SwingTask()
    {
        this("Please wait...");
    }
    
    /**
     * Creates a new swing task with the given status message
     * 
     * @param message The status message
     */
    protected SwingTask(String message)
    {
        this.message = message;
        this.progress = 0;
        
        this.swingTaskWorker = new SwingTaskWorker();
        this.progressListeners = new CopyOnWriteArrayList<ProgressListener>();
        this.processCallbacks = new CopyOnWriteArrayList<Consumer<List<V>>>();
        this.doneCallbacks = 
            new CopyOnWriteArrayList<Consumer<SwingTask<T,V>>>();

    }
    
    /**
     * Add the given callback to be called on the Event Dispatch Thread, 
     * receiving a list of intermediate results that have been passed
     * to the {@link #publish(Object...)} method.
     * 
     * @param processCallback The callback to add
     */
    public final void addProcessCallback(Consumer<List<V>> processCallback)
    {
        processCallbacks.add(processCallback);
    }

    /**
     * Remove the given callback
     * 
     * @see #addProcessCallback(Consumer)
     * @param processCallback The callback to remove
     */
    public final void removeProcessCallback(Consumer<List<V>> processCallback)
    {
        processCallbacks.remove(processCallback);
    }

    /**
     * Add the given callback to be called on the Event Dispatch Thread,
     * when this task is {@link #done()}
     * 
     * @param doneCallback The callback to add
     */
    public final void addDoneCallback(Consumer<SwingTask<T, V>> doneCallback)
    {
        doneCallbacks.add(doneCallback);
    }

    /**
     * Remove the given callback
     * 
     * @see #addDoneCallback(Consumer)
     * @param doneCallback The callback to remove
     */
    public final void removeDoneCallback(Consumer<SwingTask<T, V>> doneCallback)
    {
        doneCallbacks.add(doneCallback);
    }    
    
    /**
     * Set the handler for uncaught exceptions that may occur in the
     * underlying swing worker. If this is <code>null</code>, then
     * uncaught exceptions will be ignored (and only be passed to
     * the {@link SwingTaskListener#finished(Throwable)} method)
     * 
     * @param uncaughtExceptionHandler The handler
     */
    final void setUncaughtExceptionHandler(
        UncaughtExceptionHandler uncaughtExceptionHandler)
    {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }
    
    /**
     * Set the {@link SwingTaskListener}. Only to be called
     * by the {@link SwingTaskExecutor}
     * 
     * @param swingTaskListener The {@link SwingTaskListener}
     */
    final void setSwingTaskListener(SwingTaskListener swingTaskListener)
    {
        this.swingTaskListener = swingTaskListener;
    }
    
    /**
     * Set the stack trace with information about where this task was
     * scheduled
     *  
     * @param schedulingStackTrace The scheduling stack trace
     */
    final void setSchedulingStackTrace(StackTraceElement schedulingStackTrace[])
    {
        this.schedulingStackTrace = schedulingStackTrace;
    }

    /**
     * The method that will be executed by the underlying SwingWorker
     * 
     * @return The result of the computation
     * @throws Exception If the underlying SwingWorker throws an 
     * exception 
     */
    private T doTaskInBackground() throws Exception
    {
        if (swingTaskListener != null)
        {
            swingTaskListener.started();
        }
        try
        {
            T result = doInBackground();
            if (swingTaskListener != null)
            {
                swingTaskListener.finished(null);
            }
            return result;
        }
        catch (Exception e)
        {
            Exception extended = new ExecutionException(e);
            extended.setStackTrace(schedulingStackTrace);
            if (swingTaskListener != null)
            {
                swingTaskListener.finished(extended);
            }
            if (uncaughtExceptionHandler != null)
            {
                uncaughtExceptionHandler.uncaughtException(
                    Thread.currentThread(), extended);
            }
            throw e;
        }
    }

    /**
     * The method that may be overridden by implementors in order
     * to perform the work in the background thread
     * 
     * @return The result of the computation
     * @throws Exception If an exception happens during the computation
     */
    protected abstract T doInBackground() throws Exception;

    
    /**
     * Publishes the given data chunks. This method may be called
     * in the {@link #doInBackground()} method to publish 
     * intermediate results. These will be accumulated and 
     * may be processed in the {@link #process(List)} method,
     * which is called on the <i>Event Dispatch Thread</i> 
     * 
     * @param chunks The chunks to publish
     */
    @SafeVarargs
    protected final void publish(V... chunks) 
    {
        swingTaskWorker.doPublish(chunks);
    }
    
    /**
     * Inform all {@link #addProcessCallback(Consumer) process callbacks}
     * about the given intermediate results, and finally call 
     * {@link #process(List)}
     * 
     * @param chunks The intermediate results
     */
    private void callProcess(List<V> chunks)
    {
        for (Consumer<List<V>> processCallback : processCallbacks)
        {
            processCallback.accept(new ArrayList<V>(chunks));
        }
        process(chunks);
    }
    
    /**
     * Receives data chunks from the {@code publish} method on the
     * Event Dispatch Thread. The default implementation is empty
     * and may be overridden. 
     * <p>
     * Note that instead of overriding this method, it is also possible to 
     * register a {@link #addProcessCallback(Consumer) process callback}
     * to be informed about intermediate results.
     * 
     * @param chunks The intermediate results
     */
    protected void process(List<V> chunks) 
    {
        // Empty default implementation
    }
    
    
    /**
     * Executes this task. Similarly to a SwingWorker, this method
     * may only be called once.
     */
    public final void execute() 
    {
        swingTaskWorker.execute();
    }
    
    @Override
    public final void run()
    {
        swingTaskWorker.run();
    }
    
    @Override
    public final T get() throws InterruptedException, ExecutionException
    {
        return swingTaskWorker.get();
    }
    
    @Override
    public final T get(long timeout, TimeUnit unit) 
        throws InterruptedException, ExecutionException, TimeoutException
    {
        return swingTaskWorker.get(timeout, unit);
    }
    
    @Override
    public final boolean cancel(boolean mayInterruptIfRunning)
    {
        return swingTaskWorker.cancel(mayInterruptIfRunning);
    }
    
    @Override
    public final boolean isDone()
    {
        return swingTaskWorker.isDone();
    }
    
    @Override
    public final boolean isCancelled()
    {
        return swingTaskWorker.isCancelled();
    }
    
    /**
     * Inform all {@link #addDoneCallback(Consumer) done callbacks} that
     * this task is done, and finally call {@link #done()}
     */
    private void callDone()
    {
        for (Consumer<SwingTask<T, V>> doneCallback : doneCallbacks)
        {
            doneCallback.accept(this); // and get over it
        }
        done();
    }
    
    /**
     * This method is executed on the <i>Event Dispatch Thread</i> after 
     * the {@link #doInBackground()} method is finished. The default
     * implementation is empty and may be overridden. 
     * <p>
     * Note that instead of overriding this method, it is also possible to 
     * register a {@link #addDoneCallback(Consumer) done callback}
     * to be informed when the work is done. 
     */
    protected void done() 
    {
        // Empty default implementation
    }
    
    /**
     * Returns the {@link ProgressHandler} that may be used to inform
     * this task about the current progress during the execution of
     * the {@link #doInBackground()} method. 
     * 
     * @return The {@link ProgressHandler}
     */
    protected final ProgressHandler getProgressHandler()
    {
        return progressHandler;
    }

    /**
     * Set the current status message. If the message changed, all
     * {@link ProgressListener}s will be notified on the Event
     * Dispatch Thread
     * 
     * @param message The current status message
     */
    protected final void setMessage(final String message)
    {
        if (Objects.equals(this.message, message))
        {
            return;
        }
        this.message = message;
        if (swingTaskListener != null)
        {
            swingTaskListener.updated();
        }
        if (SwingUtilities.isEventDispatchThread())
        {
            for (ProgressListener p : progressListeners)
            {
                p.messageChanged(message);
            }
        }
        else
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    for (ProgressListener p : progressListeners)
                    {
                        p.messageChanged(message);
                    }
                }
            });
        }
    }

    
    /**
     * Set the current progress. This usually is a value between 0 and 1,
     * or a value &lt;0 if the progress is not known.
     * If the progress changed, all {@link ProgressListener}s will be 
     * notified on the Event Dispatch Thread
     * 
     * @param progress The current progress
     */
    protected final void setProgress(final double progress)
    {
        if (this.progress == progress)
        {
            return;
        }
        this.progress = progress;
        if (swingTaskListener != null)
        {
            swingTaskListener.updated();
        }
        if (SwingUtilities.isEventDispatchThread())
        {
            for (ProgressListener p : progressListeners)
            {
                p.progressChanged(progress);
            }
        }
        else
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    for (ProgressListener p : progressListeners)
                    {
                        p.progressChanged(progress);
                    }
                }
            });
        }
    }
    
    /**
     * Returns the current progress message
     * 
     * @return The current progress message
     */
    final String getMessage()
    {
        return message;
    }

    /**
     * Return the current progress. This usually is a value between 0 and 1,
     * or a value &lt;0 if the progress is not known.
     * 
     * @return The current progress.
     */
    final double getProgress()
    {
        return progress;
    }
    
    /**
     * Add the given {@link ProgressListener} to be informed about 
     * changes in the progress. The notifications will be performed
     * on the Event Dispatch Thread.
     * 
     * @param progressListener The {@link ProgressListener} to add
     */
    final void addProgressListener(ProgressListener progressListener)
    {
        progressListeners.add(progressListener);
    }
    
    /**
     * Remove the given {@link ProgressListener}
     * 
     * @param progressListener The {@link ProgressListener} to remove
     */
    final void removeProgressListener(ProgressListener progressListener)
    {
        progressListeners.remove(progressListener);
    }
    

    /**
     * Add the given property change listener to the internal SwingWorker
     * 
     * @param listener The listener to add
     */
    final void addSwingWorkerPropertyChangeListener(
        PropertyChangeListener listener)
    {
        swingTaskWorker.addPropertyChangeListener(listener);
    }

    
    
}