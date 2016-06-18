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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.javagl.swing.tasks.SwingTask.SwingTaskListener;


/**
 * An executor for a {@link SwingTask}. Instances of this class may be 
 * created with the factory methods in the {@link SwingTaskExecutors} class.
 * <p> 
 * It will take care of maintaining a {@link SwingTaskView} during the 
 * execution of the {@link SwingTask}. Such a view may, for example, be
 * a dialog that is shown, updated and hidden according to the state
 * of the {@link SwingTask} and the timing configuration of this
 * executor.
 * 
 * @param <T> The result type of the {@link SwingTask}
 */
public final class SwingTaskExecutor<T>
{
    /**
     * The {@link SwingTask} that is executed
     */
    private final SwingTask<T, ?> swingTask;
    
    /**
     * The {@link SwingTaskView} that handles the GUI components
     */
    private final SwingTaskView swingTaskView;
    
    /**
     * A synchronization aid. When the {@link #execute()} method is called,
     * the call will block the Event Dispatch Thread, waiting for this 
     * latch to count to zero. It will count to zero either when the 
     * {@link SwingTask} completes, or when the {@link SwingTaskView} 
     * is actually shown. In this case, control will be returned to the 
     * calling thread, and depending on the {@link #haveToShowView} flag,
     * a {@link SwingTaskView} may be shown.
     */
    private final CountDownLatch decisionWaiter = new CountDownLatch(1);
    
    /**
     * Whether showing the {@link SwingTaskView} was triggered
     */
    private boolean haveToShowView = false;
    
    /**
     * The lock protecting the {@link #wasFinished} state
     */
    private final Lock wasFinishedLock = new ReentrantLock(true);
    
    /**
     * Whether the task was finished
     */
    private volatile boolean wasFinished = false;
    
    /**
     * The time, in milliseconds, when the execution of the 
     * {@link SwingTask} started
     */
    private long startTimeMillis;
    
    /**
     * The time, in milliseconds after starting the {@link SwingTask}, when 
     * the decision should be made about whether to show a 
     * {@link SwingTaskView} or not.
     */
    private final int millisToDecideToPopup;
    
    /**
     * The time, in milliseconds, that a {@link SwingTask} must take in 
     * order to show a {@link SwingTaskView}
     */
    private final int millisToPopup;
    
    /**
     * A listener that will be attached to the internal SwingWorker of
     * the {@link SwingTask}, in order to hide the {@link SwingTaskView}
     * when the state of the SwingWorker changes to <code>DONE</code>.
     */
    private final PropertyChangeListener stateListener = 
        new PropertyChangeListener()
    {
        @Override
        public void propertyChange(PropertyChangeEvent event)
        {
            boolean stateChanged = "state".equals(event.getPropertyName());
            if (stateChanged)
            {
                if (event.getNewValue() == SwingWorker.StateValue.DONE)
                {
                    finish(null);
                }
            }
        }
    };

    /**
     * The {@link SwingTaskListener} that will be assigned to the 
     * {@link SwingTask} and manage the decision about whether
     * a {@link SwingTaskView} should be shown or not.
     */
    private final SwingTaskListener swingTaskListener = new SwingTaskListener()
    {
        @Override
        public void started()
        {
            startTimeMillis = System.currentTimeMillis();
            Thread popupThread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Thread.sleep(millisToPopup);
                    }
                    catch (InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                    }
                    triggerShowView();
                }
            });
            popupThread.setDaemon(true);
            popupThread.start();
            checkForShowView();
        }

        @Override
        public void updated()
        {
            checkForShowView();
        }

        @Override
        public void finished(Throwable t)
        {
            finish(t);
        }
    };
    
    /**
     * Creates a new {@link SwingTaskExecutor}
     * 
     * @param swingTask The {@link SwingTask}
     * @param swingTaskView The {@link SwingTaskView}
     * @param millisToDecideToPopup The milliseconds until the decision about
     * whether to show a {@link SwingTaskView} is made
     * @param millisToPopup The milliseconds that are required for a task
     * in order to show a {@link SwingTaskView}
     */
    SwingTaskExecutor(
        SwingTask<T, ?> swingTask, 
        SwingTaskView swingTaskView,
        int millisToDecideToPopup,
        int millisToPopup)
    {
        this.swingTask = swingTask;
        this.swingTaskView = swingTaskView;
        this.millisToDecideToPopup = millisToDecideToPopup;
        this.millisToPopup = millisToPopup;
        
        swingTask.addSwingWorkerPropertyChangeListener(stateListener);
        swingTask.setSwingTaskListener(swingTaskListener);
        
    }
    
    /**
     * Check whether a {@link SwingTaskView} has to be shown. If the time that 
     * has passed since the {@link #startTimeMillis} is greater than the 
     * {@link #millisToDecideToPopup}, then the completion time of the
     * task will be estimated based on the passed time and the current
     * progress. If this time exceeds the {@link #millisToPopup}, then
     * showing a {@link SwingTaskView} will be triggered
     */
    private void checkForShowView()
    {
        double progress = swingTask.getProgress();
        long currentTimeMillis = System.currentTimeMillis();
        long passedMillis = (int)(currentTimeMillis - startTimeMillis);
        if (passedMillis >= millisToDecideToPopup) 
        {
            int predictedCompletionTime = millisToPopup;
            if (progress > 0)
            {
                predictedCompletionTime = (int)(passedMillis / progress);
            }
            if (predictedCompletionTime >= millisToPopup) 
            {
                triggerShowView();
            }
        }
    }
    
    /**
     * Set the flag that indicates that the {@link SwingTaskView} should be 
     * shown, and count down the {@link #decisionWaiter} to cause the
     * calling thread in {@link #doExecute()} to continue its work
     */
    private void triggerShowView()
    {
        wasFinishedLock.lock();
        try
        {
            if (wasFinished)
            {
                return;
            }
            haveToShowView = true;
            decisionWaiter.countDown();
        }
        finally
        {
            wasFinishedLock.unlock();
        }
    }

    /**
     * Notify the {@link #decisionWaiter} to indicate that the task
     * was finished, and hide and dispose the {@link SwingTaskView} for the 
     * case that it was shown.
     * 
     * @param t An exception that was thrown by the {@link SwingTask},
     * or <code>null</code>
     */
    private void finish(Throwable t)
    {
        wasFinishedLock.lock();
        try
        {
            if (wasFinished)
            {
                return;
            }
            wasFinished = true;
            haveToShowView = false;
        }
        finally
        {
            wasFinishedLock.unlock();
        }
        decisionWaiter.countDown();
        swingTaskView.taskFinished(t);
        
    }
    
    /**
     * Executes the {@link SwingTask}. 
     * <p>
     * The calling thread will be blocked for a
     * {@link SwingTaskExecutorBuilder#setMillisToDecideToPopup(int)
     * specified decision delay}.
     * <p>
     * Depending on the progress that is made in this time, a
     * {@link SwingTaskView} may be shown for the task. If this method was 
     * called from the Event Dispatch Thread, this view may be modal and 
     * thus allow a controlled blocking of the Event Dispatch Thread.
     * <p>
     * If a {@link SwingTaskExecutorBuilder#setMillisToPopup(int)
     * specified duration} passes without any progress, 
     * then the {@link SwingTaskView} will be shown in any case.
     * <p>
     * 
     * @throws IllegalStateException If the {@link SwingTask} is 
     * already {@link SwingTask#isDone() done}.
     */
    public void execute()
    {
        if (swingTask.isDone())
        {
            throw new IllegalStateException("SwingTask is already done");
        }
        
        StackTraceElement[] stackTrace = 
            Thread.currentThread().getStackTrace();
        StackTraceElement schedulingStackTrace[] = 
            Arrays.copyOfRange(stackTrace, 1, stackTrace.length);
        swingTask.setSchedulingStackTrace(schedulingStackTrace);
        if (SwingUtilities.isEventDispatchThread())
        {
            doExecute();
        }
        else
        {
            try
            {
                SwingUtilities.invokeAndWait(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        doExecute();
                    }
                });
            }
            catch (InvocationTargetException e)
            {
                throw new RuntimeException(e);
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    
    
    /**
     * The method that starts the {@link SwingTask#execute() SwingTask
     * execution}. This method is called on the Event Dispatch Thread,
     * and will block until either the task is finished, or it receives
     * a notification to continue via the {@link #decisionWaiter}.
     */
    private void doExecute()
    {
        swingTask.execute();
        try
        {
            decisionWaiter.await();
            if (haveToShowView)
            {
                swingTaskView.show();
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
}