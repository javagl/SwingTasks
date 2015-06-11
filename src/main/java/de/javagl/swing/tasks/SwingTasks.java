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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Methods to create {@link SwingTask} instances
 */
class SwingTasks
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(SwingTasks.class.getName());
    
    /**
     * Whether exceptions should be logged (although they are ignored
     * otherwise)
     */
    private static final boolean LOG_EXCEPTIONS = true;
    
    /**
     * Create a simple {@link SwingTask} that only calls the given 
     * runnable and returns <code>null</code>.<br> 
     * <br>
     * NOTE: Any exceptions caused by the runnable (including exceptions
     * that are caused by interrupting or canceling the {@link SwingTask})
     * will be ignored.
     * 
     * @param runnable The runnable
     * @return The {@link SwingTask}
     */
    public static SwingTask<Void, Void> createSimple(Runnable runnable)
    {
        return create(asSupplier(runnable), null);
    }
    
    /**
     * Create a simple {@link SwingTask} that only calls the given 
     * supplier and returns its result.<br> 
     * <br>
     * NOTE: Any exceptions caused by the supplier (including exceptions
     * that are caused by interrupting or canceling the {@link SwingTask})
     * will be ignored.
     * 
     * @param <T> The result type
     * @param supplier The supplier
     * @return The {@link SwingTask}
     */
    public static <T> SwingTask<T, Void> createSimple(Supplier<T> supplier)
    {
        return create(supplier, null);
    }
    
    /**
     * Creates a simple {@link SwingTask} that calls the given supplier.
     * The result that is provided by the supplier will be obtained from
     * the {@link SwingTask}, and passed to the given consumer (if
     * the consumer is not <code>null</code>)<br> 
     * <br>
     * NOTE: Any exceptions caused by the supplier (including exceptions
     * that are caused by interrupting or canceling the {@link SwingTask})
     * will be ignored. If an exception occurs, then the consumer will
     * not be called.
     * 
     * @param <T> The type of the result
     * @param supplier The supplier
     * @param consumer The consumer
     * @return The {@link SwingTask}
     */
    public static <T> SwingTask<T, Void> create(
        Supplier<T> supplier, Consumer<? super T> consumer)
    {
        Function<ProgressHandler, T> function = asFunction(supplier); 
        return create(function, consumer, null);
    }
    
    
    /**
     * Creates a simple {@link SwingTask} that calls the given supplier.
     * The result that is provided by the supplier will be obtained from
     * the {@link SwingTask}, and passed to the given consumer (if
     * the consumer is not <code>null</code>)<br> 
     * <br>
     * NOTE: Any exceptions caused by the supplier (including exceptions
     * that are caused by interrupting or canceling the {@link SwingTask})
     * will be ignored and only be passed to the given 
     * <code>UncaughtExceptionHandler</code>. If an exception occurs, then 
     * the consumer will not be called.
     * 
     * @param <T> The type of the result
     * @param supplier The supplier
     * @param consumer The consumer
     * @param uncaughtExceptionHandler The handler for uncaught exceptions
     * @return The {@link SwingTask}
     */
    public static <T> SwingTask<T, Void> create(
        final Supplier<T> supplier, 
        final Consumer<? super T> consumer,
        UncaughtExceptionHandler uncaughtExceptionHandler)
    {
        Function<ProgressHandler, T> function = asFunction(supplier); 
        return create(function, consumer, uncaughtExceptionHandler);
    }
    

    /**
     * Creates a simple {@link SwingTask} that calls the given function.
     * The function will receive the {@link SwingTask#getProgressHandler()
     * progress handler} of the task, which may be used to inform the
     * task about the computation progress.
     * The result that is provided by the function will be obtained from
     * the {@link SwingTask}, and passed to the given consumer (if
     * the consumer is not <code>null</code>)<br> 
     * <br>
     * NOTE: Any exceptions caused by the function (including exceptions
     * that are caused by interrupting or canceling the {@link SwingTask})
     * will be ignored and only be passed to the given 
     * <code>UncaughtExceptionHandler</code>. If an exception occurs,
     * then the consumer will not be called.
     * 
     * @param <T> The type of the result
     * @param function The supplier
     * @param consumer The consumer
     * @param uncaughtExceptionHandler The handler for uncaught exceptions
     * @return The {@link SwingTask}
     */
    public static <T> SwingTask<T, Void> create(
        final Function<ProgressHandler, T> function, 
        final Consumer<? super T> consumer, 
        UncaughtExceptionHandler uncaughtExceptionHandler)
    {
        SwingTask<T, Void> swingTask = new SwingTask<T, Void>()
        {
            @Override
            protected T doInBackground() throws Exception
            {
                return function.apply(getProgressHandler());
            }
        };
        if (consumer != null)
        {
            swingTask.addDoneCallback(new Consumer<SwingTask<T,Void>>()
            {
                @Override
                public void accept(SwingTask<T, Void> task)
                {
                    try
                    {
                        consumer.accept(task.get());
                    }
                    catch (Exception e)
                    {
                        // Ignored
                        if (LOG_EXCEPTIONS)
                        {
                            logger.fine(e.toString());
                        }
                    }
                }
            });
        }
        swingTask.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        return swingTask;
    }

    /**
     * Returns the given supplier as a function
     * 
     * @param <S> The argument type
     * @param <T> The result type
     * @param supplier The supplier
     * @return The function
     */
    private static <S, T> Function<S, T> asFunction(final Supplier<T> supplier)
    {
        return new Function<S, T>()
        {
            @Override
            public T apply(S s)
            {
                return supplier.get();
            }
        };
    }
    
    /**
     * Returns the given runnable as a supplier (which supplies a 
     * <code>Void</code> object, namely <code>null</code>)
     * 
     * @param runnable The runnable
     * @return The supplier
     */
    private static Supplier<Void> asSupplier(final Runnable runnable)
    {
        return new Supplier<Void>()
        {
            @Override
            public Void get()
            {
                runnable.run();
                return null;
            }
        };
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private SwingTasks()
    {
        // Private constructor to prevent instantiation
    }
}
