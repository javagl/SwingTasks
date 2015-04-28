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
import java.util.function.Supplier;

/**
 * Methods to create {@link SwingTaskExecutor} instances
 */
public class SwingTaskExecutors
{
    /**
     * Start creating a {@link SwingTaskExecutor} using a 
     * {@link SwingTaskExecutorBuilder}. 
     * <p>
     * By default, the builder will be configured as follows:
     * <ul>
     *   <li>
     *     The {@link SwingTaskExecutorBuilder#setMillisToDecideToPopup(int)
     *     decision delay} will be 300ms. After this time, it will decide 
     *     whether a dialog should be shown or not.
     *   </li>
     *   <li>
     *     The {@link SwingTaskExecutorBuilder#setMillisToPopup(int) duration
     *     to pop up} will be 1000ms. After the decision delay has passed, 
     *     the completion time of the task will be predicated based on 
     *     the {@link ProgressHandler#setProgress(double) progress} that
     *     was reported by the task. If the predicted completion time is
     *     more than this time, the dialog will be shown. If the task 
     *     actually takes more than this time, a dialog will be shown in
     *     any case
     *   </li>
     *   <li>
     *     The dialog will be 
     *     {@link SwingTaskExecutorBuilder#setModal(boolean) modal}. 
     *   </li>
     *   <li>
     *     The dialog will not be 
     *     {@link SwingTaskExecutorBuilder#setCancelable(boolean) cancelable}. 
     *   </li>
     *   <li>
     *     The dialog will have a default  
     *     {@link SwingTaskExecutorBuilder#setTitle(String) title} saying
     *     <code>"Working"</code>
     *   </li>
     *   <li>
     *     The dialog will have no 
     *     {@link SwingTaskExecutorBuilder#setParentComponent(Component) 
     *     parent component}
     *   </li>
     *   <li>
     *     The dialog will have no 
     *     {@link SwingTaskExecutorBuilder#setParentWindow(Window) 
     *     parent window}
     *   </li>
     *   <li>
     *     The builder will have no 
     *     {@link SwingTaskExecutorBuilder#setUncaughtExceptionHandler(
     *     UncaughtExceptionHandler) uncaught exception handler}
     *   </li>
     * </ul>
     * 
     * 
     * @param <T> The result type of the {@link SwingTask}
     * 
     * @param swingTask The {@link SwingTask} to execute
     * @return The {@link SwingTaskExecutorBuilder}
     */
    public static <T> SwingTaskExecutorBuilder<T> create(
        SwingTask<T, ?> swingTask)
    {
        return new SwingTaskExecutorBuilder<T>(swingTask);
    }
    
    /**
     * Start creating a {@link SwingTaskExecutor} using a 
     * {@link SwingTaskExecutorBuilder}. 
     * <p>
     * This will just call {@link #create(SwingTask)} using a {@link SwingTask}
     * that was created from the given <code>Runnable</code>.
     * <p>
     * NOTE: Any exceptions caused by the runnable (including exceptions
     * that are caused by interrupting or canceling the {@link SwingTask})
     * will be ignored.
     * 
     * @param runnable The runnable
     * @return The {@link SwingTaskExecutorBuilder}
     */
    public static SwingTaskExecutorBuilder<Void> createSimple(Runnable runnable)
    {
        return create(SwingTasks.createSimple(runnable));
    }
    
    /**
     * Start creating a {@link SwingTaskExecutor} using a 
     * {@link SwingTaskExecutorBuilder}. 
     * <p>
     * This will just call {@link #create(SwingTask)} using a {@link SwingTask}
     * that was created from the given <code>Supplier</code>.
     * <p>
     * NOTE: Any exceptions caused by the supplier (including exceptions
     * that are caused by interrupting or canceling the {@link SwingTask})
     * will be ignored.
     * 
     * @param <T> The type of the supplied objects
     * 
     * @param supplier The supplier
     * @return The {@link SwingTaskExecutorBuilder}
     */
    public static <T> SwingTaskExecutorBuilder<T> create(Supplier<T> supplier)
    {
        return create(SwingTasks.createSimple(supplier));
    }

    /**
     * Start creating a {@link SwingTaskExecutor} using a 
     * {@link SwingTaskExecutorBuilder}.
     * <p>
     * This will just call {@link #create(SwingTask)} using a {@link SwingTask}
     * that was created from the given <code>Supplier</code> and 
     * <code>Consumer</code> as follows:
     * <p>
     * The {@link SwingTask} calls the given supplier. The result that is 
     * provided by the supplier will be obtained from the {@link SwingTask}, 
     * and passed to the given consumer (if the consumer is not 
     * <code>null</code>) 
     * <p>
     * NOTE: Any exceptions caused by the supplier (including exceptions
     * that are caused by interrupting or canceling the {@link SwingTask})
     * will be ignored. If an exception occurs, then the consumer will
     * not be called.
     * 
     * @param <T> The type of the result
     * @param supplier The supplier
     * @param consumer The consumer
     * @return The {@link SwingTaskExecutorBuilder}
     */
    public static <T> SwingTaskExecutorBuilder<T> create(
        Supplier<T> supplier, Consumer<? super T> consumer)
    {
        return create(SwingTasks.create(supplier, consumer));
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private SwingTaskExecutors()
    {
        // Private constructor to prevent instantiation
    }

}
