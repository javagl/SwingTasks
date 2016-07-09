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
package de.javagl.swing.tasks.runner;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A runner for a {@link Task}s. It executes the {@link Task#run() run method}
 * of a {@link Task} repeatedly, and allows starting, pausing and 
 * stopping the execution.
 */
public final class TaskRunner
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(TaskRunner.class.getName());
    
    /**
     * The log level for detail messages
     */
    private static final Level logLevel = Level.INFO;
    
    
    /**
     * Whether the task is currently running
     */
    private volatile boolean running = false;
    
    /**
     * The lock for the 'running' state
     */
    private final Lock runningLock = new ReentrantLock(true);
    
    /**
     * The condition that the task started running
     */
    private final Condition startedRunning = runningLock.newCondition();

    /**
     * The condition that the task finished running
     */
    private final Condition finishedRunning = runningLock.newCondition();

    

    /**
     * Whether the task is paused
     */
    private volatile boolean paused = false;

    /**
     * The lock for the 'paused' state
     */
    private final Lock pausedLock = new ReentrantLock(true);
    
    /**
     * The condition that the task was unpaused
     */
    private final Condition unpaused = pausedLock.newCondition();
    
    
    /**
     * Whether a single step should be one, and the task should then
     * be paused
     */
    private volatile boolean singleStep = false;
    
    /**
     * The lock for the 'singleStep' state
     */
    private final Lock singleStepLock = new ReentrantLock(true);
    
    
    /**
     * Whether the task thread should stop as soon as possible
     */
    private volatile boolean shouldStop = false;

    /**
     * The lock for the 'shouldStop' state
     */
    private final Lock shouldStopLock = new ReentrantLock(true);
    
    
    /**
     * The {@link Task} that is run
     */
    private final Task task;

    /**
     * The task thread
     */
    private Thread taskThread;
    
    /**
     * The list of {@link TaskRunnerListener}s
     */
    private final List<TaskRunnerListener> taskRunnerListeners;
    
    /**
     * Creates a new task runner that will run the given {@link Task}
     * 
     * @param task The {@link Task}
     */
    public TaskRunner(Task task)
    {
        this.task = task;
        this.taskRunnerListeners = 
            new CopyOnWriteArrayList<TaskRunnerListener>();
    }
    
    /**
     * Add the given {@link TaskRunnerListener} to be informed about the
     * state of this runner
     * 
     * @param taskRunnerListener The {@link TaskRunnerListener}
     */
    public void addTaskRunnerListener(TaskRunnerListener taskRunnerListener)
    {
        taskRunnerListeners.add(taskRunnerListener);
    }

    /**
     * Remove the given {@link TaskRunnerListener} 
     * 
     * @param taskRunnerListener The {@link TaskRunnerListener}
     */
    public void removeTaskRunnerListener(TaskRunnerListener taskRunnerListener)
    {
        taskRunnerListeners.remove(taskRunnerListener);
    }
    
    /**
     * Start the task. 
     */
    void start()
    {
        runningLock.lock();
        try
        {
            if (running)
            {
                logger.warning("Task is already running");
                return;
            }
        }
        finally
        {
            runningLock.unlock();
        }
        log("Starting task thread");
        taskThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                doRun();
            }
        }, "TaskRunnerThread");
        taskThread.start();
    }
    
    /**
     * Set the "running" state to <code>true</code>
     */
    void setRunning()
    {
        runningLock.lock();
        try
        {
            running = true;
            startedRunning.signalAll();
        }
        finally
        {
            runningLock.unlock();
        }
    }
    
    
    /**
     * Perform a single step, and then go into the "paused" state.
     * If the task is not started yet, it will be started here, and
     * the method will block until the task actually started running.
     */
    void singleStep()
    {
        log("Performing single step");
        singleStepLock.lock();
        try
        {
            singleStep = true;
        }
        finally
        {
            singleStepLock.unlock();
        }
        pausedLock.lock();
        try
        {
            if (paused)
            {
                setPaused(false);
            }
        }
        finally
        {
            pausedLock.unlock();
        }
        runningLock.lock();
        try
        {
            if (!running)
            {
                start();
                waitForStartedRunning();
            }
        }
        finally
        {
            runningLock.unlock();
        }
        
    }
    
    /**
     * While the state is not "running", wait for the "startedRunning" signal
     */
    private void waitForStartedRunning()
    {
        log("Waiting until task is started");
        runningLock.lock();
        try
        {
            while (!running)
            {
                startedRunning.await();
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        finally
        {
            runningLock.unlock();
        }
    }
    
    
    /**
     * Set whether the task should be paused
     * 
     * @param newPaused Whether the task should be paused
     */
    void setPaused(boolean newPaused)
    {
        pausedLock.lock();
        try
        {
            if (this.paused != newPaused)
            {
                log("Setting task to paused="+newPaused);
                this.paused = newPaused;
                if (!newPaused)
                {
                    unpaused.signalAll();
                }
                firePauseChanged(newPaused);
            }
        }
        finally
        {
            pausedLock.unlock();
        }
    }

    /**
     * Stop the task as soon as possible. This method will block
     * until the task has finished any steps that may currently 
     * be in progress. If the task is not running, the nothing
     * will be done.
     * 
     * @param mayInterrupt Whether the thread that is executing
     * the task may be interrupted
     */
    void stop(boolean mayInterrupt)
    {
        runningLock.lock();
        try
        {
            if (!running)
            {
                return;
            }
        }
        finally
        {
            runningLock.unlock();
        }
        
        log("Stopping task");
        
        setShouldStop(true);
        
        if (mayInterrupt)
        {
            log("Interrupting task thread");
            taskThread.interrupt();
        }
        waitForFinishedRunning();
        log("Stopping task DONE");
    }

    /**
     * While the state is "running", wait for the "finishedRunning" signal
     */
    private void waitForFinishedRunning()
    {
        log("Waiting until task is finished");
        runningLock.lock();
        try
        {
            while (running)
            {
                finishedRunning.await();
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        finally
        {
            runningLock.unlock();
        }
    }
    

    /**
     * Set the flag indicating that the task should stop
     * 
     * @param s The flag
     */
    private void setShouldStop(boolean s)
    {
        shouldStopLock.lock();
        try
        {
            shouldStop = s;
            if (shouldStop)
            {
                setPaused(false);
            }
        }
        finally 
        {
            shouldStopLock.unlock();
        }
    }


    
    /**
     * Wait while the runner is in the "paused" state
     */
    private void waitWhilePaused()
    {
        pausedLock.lock();
        try
        {
            while (paused)
            {
                try
                {
                    unpaused.await();
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
        finally 
        {
            pausedLock.unlock();
        }
    }
    
    /**
     * The actual method running in the task thread
     */
    private void doRun()
    {
        try
        {
            setShouldStop(false);
            setRunning();
            fireStarting();
            task.started();

            while (true)
            {
                waitWhilePaused();

                shouldStopLock.lock();
                try
                {
                    if (shouldStop)
                    {
                        task.finished(false, null);
                        return;
                    }
                }
                finally
                {
                    shouldStopLock.unlock();
                }

                try
                {
                    task.run();
                }
                catch (Throwable t)
                {
                    if (!(t instanceof InterruptedException))
                    {
                        logger.severe("Exception in task thread");
                        t.printStackTrace();
                    }
                    else
                    {
                        Thread.currentThread().interrupt();
                        log("Task Thread was interrupted");
                    }
                    task.finished(false, t);
                    return;
                }
                if (task.isDone())
                {
                    task.finished(true, null);
                    return;
                }
                
                singleStepLock.lock();
                try
                {
                    if (singleStep)
                    {
                        log("Single step was requested, pausing");
                        singleStep = false;
                        setPaused(true);
                    }
                }
                finally
                {
                    singleStepLock.unlock();
                }
                
            }
        }
        finally
        {
            runningLock.lock();
            try
            {
                running = false;
                finishedRunning.signalAll();
            }
            finally
            {
                runningLock.unlock();
            }
            fireFinished();
        }
    }

    /**
     * Notify all {@link TaskRunnerListener}s that the task is starting
     */
    private void fireStarting()
    {
        for (TaskRunnerListener taskRunnerListener : taskRunnerListeners)
        {
            taskRunnerListener.starting();
        }
    }

    /**
     * Notify all {@link TaskRunnerListener}s that the task was
     * paused or unpaused
     * 
     * @param paused The new state
     */
    private void firePauseChanged(boolean paused)
    {
        for (TaskRunnerListener taskRunnerListener : taskRunnerListeners)
        {
            taskRunnerListener.pauseChanged(paused);
        }
    }
    
    /**
     * Notify all {@link TaskRunnerListener}s that the task finished
     */
    private void fireFinished()
    {
        for (TaskRunnerListener taskRunnerListener : taskRunnerListeners)
        {
            taskRunnerListener.finished();
        }
    }


    /**
     * Print the given message with the current log level, possibly 
     * extended by information about the current thread
     * 
     * @param message The message
     */
    private static void log(String message)
    {
        boolean printThread = false;
        printThread = true;
        if (printThread)
        {
            message += " on "+Thread.currentThread();            
        }
        logger.log(logLevel, message);
    }

} 
