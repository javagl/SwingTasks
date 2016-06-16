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

import de.javagl.swing.tasks.ProgressListener;

/**
 * Interface for classes that may update a {@link TaskView} based on the
 * state of the execution of a task. Instances of classes implementing 
 * this interface may be assigned to an {@link ObservableExecutorPanel},
 * using {@link ObservableExecutorPanel#setTaskViewHandler(TaskViewHandler)},
 * to customize the display of the tasks. In this case, all methods in
 * this interface will be called on the Event Dispatch Thread.
 */
interface TaskViewHandler
{
    /**
     * Will be called when the given task was scheduled and the given 
     * {@link TaskView} was created. 
     * 
     * @param task The task
     * @param taskView The {@link TaskView}
     */
    void scheduled(Object task, TaskView taskView);

    /**
     * Will be called before the execution of the given task starts
     * 
     * @param task The task
     * @param taskView The {@link TaskView}
     */
    void beforeExecute(Object task, TaskView taskView);

    /**
     * Will be called after the execution of the given task finished. 
     * 
     * @param task The task
     * @param throwable The throwable that may have been caused by the
     * task execution, or <code>null</code> if the task completed 
     * successfully
     * @param taskView The {@link TaskView}
     */
    void afterExecute(Object task, Throwable throwable, TaskView taskView);
    
    /**
     * If the tasks that have been submitted to the 
     * {@link ObservableExecutorService} that is displayed in the
     * {@link ObservableExecutorPanel} implement the {@link ProgressTask}
     * interface, then these methods will be called whenever the 
     * {@link ProgressTask} publishes progress information to its 
     * {@link ProgressListener}s. 
     * 
     * @param progressTask The {@link ProgressTask}
     * @param taskView The {@link TaskView}
     * @param message The message
     */
    void messageChanged(
        ProgressTask progressTask, TaskView taskView, String message);

    /**
     * If the tasks that have been submitted to the 
     * {@link ObservableExecutorService} that is displayed in the
     * {@link ObservableExecutorPanel} implement the {@link ProgressTask}
     * interface, then these methods will be called whenever the 
     * {@link ProgressTask} publishes progress information to its 
     * {@link ProgressListener}s. 
     * 
     * @param progressTask The {@link ProgressTask}
     * @param taskView The {@link TaskView}
     * @param progress The progress
     */
    void progressChanged(
        ProgressTask progressTask, TaskView taskView, double progress);
}