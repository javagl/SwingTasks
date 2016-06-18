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

import java.awt.Component;
import java.awt.Window;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

/**
 * A builder for {@link SwingTaskExecutor} instances, returned by
 * {@link SwingTaskExecutors#create(SwingTask)}. It allows setting
 * all parameters for the execution of a {@link SwingTask}, in 
 * a fluent style, and finally obtaining the {@link SwingTaskExecutor}
 * instance by calling {@link #build()}.
 * 
 * @param <T> The result type of the {@link SwingTask}
 */
public final class SwingTaskExecutorBuilder<T>
{
    /**
     * The {@link SwingTask} that will be executed
     */
    private final SwingTask<T,?> swingTask;
    
    /**
     * The title for the {@link SwingTaskView} that may be shown
     */
    private String title;
    
    /**
     * The parent component for the {@link SwingTaskView} that may be shown.
     */
    private Component parentComponent;
    
    /**
     * The parent window for the {@link SwingTaskView} that may be shown
     */
    private Window parentWindow;
    
    /**
     * Whether the parent window was set explicitly
     */
    private boolean parentWindowWasSet;
    
    /**
     * Whether the {@link SwingTaskView} should be modal
     */
    private boolean modal;
    
    /**
     * Whether the task should be cancelable (and the {@link SwingTaskView}
     * should contain a button for canceling the task)
     */
    private boolean cancelable;
    
    /**
     * The milliseconds to block until the decision will
     * be made whether the {@link SwingTaskView} should be shown or not
     */
    private int millisToDecideToPopup;
    
    /**
     * The milliseconds that the task has to take until
     * a {@link SwingTaskView} will be shown
     */
    private int millisToPopup;

    /**
     * The optional handler for uncaught exceptions that
     * may happen in the {@link SwingTask}
     */
    private UncaughtExceptionHandler uncaughtExceptionHandler;
    
    /**
     * Whether the dialog uncaught exception handler was set
     */
    private boolean dialogUncaughtExceptionHandlerWasSet;
    
    /**
     * The {@link SwingTaskViewFactory} that will create
     * the {@link SwingTaskView}
     */
    private SwingTaskViewFactory swingTaskViewFactory; 
    
    /**
     * Starts the creation of a new {@link SwingTaskExecutor}
     * 
     * @param swingTask The {@link SwingTask} that will be executed
     */
    SwingTaskExecutorBuilder(SwingTask<T, ?> swingTask)
    {
        this.swingTask = swingTask;
        
        // Initialize the default settings
        this.title = "Working";
        this.parentComponent = null;
        this.parentWindow = null;
        this.parentWindowWasSet = false;
        this.modal = true;
        this.cancelable = false;
        this.millisToDecideToPopup = 300;
        this.millisToPopup = 1000;
        this.uncaughtExceptionHandler = null;
        this.dialogUncaughtExceptionHandlerWasSet = false;

        this.swingTaskViewFactory = swingTaskViewConfig ->
            SwingTaskViews.create(swingTaskViewConfig);
    }
    
    /**
     * Set the parent window for the {@link SwingTaskView} that may be shown.
     * If this is not explicitly set, then the window ancestor
     * of the {@link #setParentComponent(Component) parent component} 
     * will be used. If no parent component was set, then the 
     * currently focused <code>java.awt.Frame</code> (or the first 
     * one that is visible) will be used. If the parent is explicitly set to 
     * <code>null</code>, then the {@link SwingTaskView} will have no parent.
     * 
     * @param parentWindow The parent window.
     * @return This builder
     */
    public SwingTaskExecutorBuilder<T> setParentWindow(Window parentWindow)
    {
        this.parentWindow = parentWindow;
        this.parentWindowWasSet = true;
        return this;
    }
    
    /**
     * Set the parent component for the {@link SwingTaskView}. If no 
     * {@link #setParentWindow(Window) parent window} was explicitly
     * set, then the window ancestor of the parent component will
     * be used as the parent window of the {@link SwingTaskView}
     * 
     * @param parentComponent The parent component for the {@link SwingTaskView}
     * @return This builder 
     */
    public SwingTaskExecutorBuilder<T> setParentComponent(
        Component parentComponent)
    {
        this.parentComponent = parentComponent;
        return this;
    }

    /**
     * Set the title of the {@link SwingTaskView} that may be shown
     * 
     * @param title The title of the {@link SwingTaskView}
     * @return This builder 
     */
    public SwingTaskExecutorBuilder<T> setTitle(String title)
    {
        this.title = title;
        return this;
    }

    /**
     * Set whether the {@link SwingTaskView} that may be shown should be modal
     * 
     * @param modal Whether the {@link SwingTaskView} should be modal
     * @return This builder 
     */
    public SwingTaskExecutorBuilder<T> setModal(boolean modal)
    {
        this.modal = modal;
        return this;
    }

    /**
     * Set whether the {@link SwingTask} should be cancelable, and the 
     * {@link SwingTaskView} should contain a button for canceling the task
     * 
     * @param cancelable Whether the {@link SwingTask} should be cancelable
     * @return This builder 
     */
    public SwingTaskExecutorBuilder<T> setCancelable(boolean cancelable)
    {
        this.cancelable = cancelable;
        return this;
    }

    /**
     * Set the number of milliseconds that the Event Dispatch Thread 
     * should be blocked when calling {@link SwingTaskExecutor#execute()}
     * before the decision is made about whether to show a 
     * {@link SwingTaskView} or not
     *  
     * @param millisToDecideToPopup The number of milliseconds
     * @return This builder  
     */
    public SwingTaskExecutorBuilder<T> setMillisToDecideToPopup(
        int millisToDecideToPopup)
    {
        this.millisToDecideToPopup = millisToDecideToPopup;
        return this;
    }

    /**
     * Set the number of milliseconds that the task has to take so 
     * that a {@link SwingTaskView} will be shown. After the 
     * {@link #setMillisToDecideToPopup(int) decision delay} has
     * passed, the completion time of the task will be predicted,
     * based on the {@link ProgressHandler#setProgress(double) progress}
     * that has been reported by the task so far. If the completion
     * time is greater than the time that is passed to this method,
     * then a {@link SwingTaskView} will be shown. Also, when the task 
     * actually takes longer than the time that is passed to this method,
     * a {@link SwingTaskView} will be shown. 
     * 
     * @param millisToPopup The number of milliseconds
     * @return This builder  
     */
    public SwingTaskExecutorBuilder<T> setMillisToPopup(int millisToPopup)
    {
        this.millisToPopup = millisToPopup;
        return this;
    }
    
    /**
     * Set the handler that should be assigned to the {@link SwingTask}
     * in order to handle potential uncaught exceptions that happen 
     * during the execution
     * 
     * @param uncaughtExceptionHandler The exception handler
     * @return This builder
     * 
     * @see #setDialogUncaughtExceptionHandler()
     */
    public SwingTaskExecutorBuilder<T> setUncaughtExceptionHandler(
        UncaughtExceptionHandler uncaughtExceptionHandler)
    {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        this.dialogUncaughtExceptionHandlerWasSet = false;
        return this;
    }
    
    /**
     * Set a handler for potential uncaught exceptions that happen during 
     * the execution of the {@link SwingTask}. This handler will
     * show a dialog containing the exception stack trace.
     * 
     * @return This builder
     * 
     * @see #setUncaughtExceptionHandler(UncaughtExceptionHandler)
     */
    public SwingTaskExecutorBuilder<T> setDialogUncaughtExceptionHandler()
    {
        this.uncaughtExceptionHandler = null;
        this.dialogUncaughtExceptionHandlerWasSet = true;
        return this;
    }
    
    /**
     * Set the factory that will be called to create the {@link SwingTaskView}
     * that will be displayed while the task is running.
     * 
     * @param swingTaskViewFactory The {@link SwingTaskViewFactory}
     * @return This builder
     */
    public SwingTaskExecutorBuilder<T> setSwingTaskViewFactory(
        SwingTaskViewFactory swingTaskViewFactory)
    {
        this.swingTaskViewFactory = swingTaskViewFactory;
        return this;
    }
    
    
    /**
     * Build the {@link SwingTaskExecutor} based on the current
     * configuration of this builder
     * 
     * @return The {@link SwingTaskExecutor}
     */
    public SwingTaskExecutor<T> build()
    {
        Window parentWindowToUse = determineParentWindowToUse();
        if (dialogUncaughtExceptionHandlerWasSet)
        {
            uncaughtExceptionHandler =
                SwingTaskUtils.createDialogUncaughtExceptionHandler(
                    parentComponent);
        }
        if (uncaughtExceptionHandler != null)
        {
            swingTask.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        }
        
        SwingTaskViewConfig swingTaskViewConfig = 
            new DefaultSwingTaskViewConfig(
                swingTask, title, parentWindowToUse, 
                parentComponent, modal, cancelable);
        final SwingTaskView swingTaskView =
            swingTaskViewFactory.create(swingTaskViewConfig);

        ProgressListener progressListener = new ProgressListener()
        {
            @Override
            public void progressChanged(double progress)
            {
                swingTaskView.setProgress(progress);
            }
            
            @Override
            public void messageChanged(String message)
            {
                swingTaskView.setMessage(message);
            }
        };
        swingTask.addProgressListener(progressListener);
        attachRemover(swingTask, progressListener);
        
        SwingTaskExecutor<T> swingTaskExecutor = 
            new SwingTaskExecutor<T>(swingTask, swingTaskView, 
                millisToDecideToPopup, millisToPopup);
        return swingTaskExecutor;
    }
    
    
    /**
     * Return the parent window for the {@link SwingTaskView}, based 
     * on the settings in this builder.
     * 
     * @return The parent window for the {@link SwingTaskView}
     */
    private Window determineParentWindowToUse()
    {
        if (parentWindowWasSet)
        {
            return parentWindow;
        }
        if (parentComponent != null)
        {
             return SwingUtilities.getWindowAncestor(parentComponent);
        }
        return SwingTaskUtils.findParentWindow();
    }

    /**
     * Attach a callback to the given {@link SwingTask} which removes the 
     * given {@link ProgressListener} when the task is done. 
     * 
     * @param swingTask The {@link SwingTask}
     * @param progressListener The {@link ProgressListener}
     */
    private static <T, V> void attachRemover(
        SwingTask<T, V> swingTask, ProgressListener progressListener)
    {
        swingTask.addDoneCallback(new Consumer<SwingTask<T, V>>()
        {
            @Override
            public void accept(SwingTask<T, V> t)
            {
                swingTask.removeProgressListener(progressListener);
            }
        });
    }
}