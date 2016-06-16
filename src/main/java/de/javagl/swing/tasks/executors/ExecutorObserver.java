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

/**
 * Interface for classes that may be informed about the execution status of 
 * tasks in an executor that was created with the {@link ObservableExecutors}
 * class.<br>
 * <br>
 * If this observer is attached to an {@link ObservableExecutorService},
 * then the <code>Runnable</code> instances that it receives should 
 * always be {@link ObservableTask} instances. The 
 * {@link ObservableExecutors#getInnerTask(Runnable, Class)} method may
 * be used to extract the task that originally has been submitted to
 * the executor service.
 * <br>
 * Implementing classes should make sure that none of the methods in this
 * interface ever throw an exception.
 */
public interface ExecutorObserver
{
    /**
     * Will be called when the given task was scheduled for execution in
     * the executor service.
     * 
     * @param r The task.
     */
    void scheduled(Runnable r);
    
    /**
     * Will be called before the given thread executes the given task.
     * 
     * @param t The thread
     * @param r The task
     */
    void beforeExecute(Thread t, Runnable r);
    
    /**
     * Will be called after the given task was executed
     * 
     * @param r The task
     * @param t A throwable that may have been caused
     */
    void afterExecute(Runnable r, Throwable t);
    
    /**
     * Will be called after all tasks have been finished. That is,
     * when {@link #afterExecute(Runnable, Throwable)} was called
     * and no new tasks have been {@link #scheduled(Runnable)} in
     * the meantime.
     */
    void tasksFinished();
}


