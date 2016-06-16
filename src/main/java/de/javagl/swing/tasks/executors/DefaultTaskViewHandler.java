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

import java.awt.Color;

/**
 * Default implementation of a {@link TaskViewHandler}
 */
class DefaultTaskViewHandler implements TaskViewHandler
{
    /**
     * Whether tasks that have been completed successfully should be removed
     * from the view
     */
    private final boolean removeSuccessfullyFinishedTasks;

    /**
     * Default constructor
     *  
     * @param removeSuccessfullyFinishedTasks Whether tasks that have been 
     * completed successfully should be removed from the view
     */
    DefaultTaskViewHandler(boolean removeSuccessfullyFinishedTasks)
    {
        this.removeSuccessfullyFinishedTasks = removeSuccessfullyFinishedTasks;
    }
    
    @Override
    public void scheduled(Object task, TaskView taskView)
    {
        taskView.setText("scheduled : "+stringFor(task));
        taskView.setForegroundColor(Color.DARK_GRAY);
    }

    @Override
    public void beforeExecute(Object task, TaskView taskView)
    {
        taskView.setText("processing: " + stringFor(task));
        taskView.setForegroundColor(new Color(255, 128, 0));
    }

    @Override
    public void afterExecute(
        Object task, Throwable throwable, TaskView taskView)
    {
        if (throwable == null)
        {
            taskView.setText("finished  : "+stringFor(task));
            taskView.setForegroundColor(new Color(0,128,0));
            if (removeSuccessfullyFinishedTasks)
            {
                taskView.remove();
            }
        }
        else
        {
            taskView.setText("failed    : " + stringFor(task) + 
                ", error: " + throwable.getMessage());
            taskView.setForegroundColor(Color.RED);
            taskView.setThrowable(throwable);
        }
    }

    @Override
    public void messageChanged(
        ProgressTask progressTask, TaskView taskView, String message)
    {
        taskView.setText("processing: " + stringFor(progressTask) + message);
    }

    @Override
    public void progressChanged(
        ProgressTask progressTask, TaskView taskView, double progress)
    {
        taskView.setProgress(progress);
    }
    
    /**
     * Create a string representation of the given task. If the task is
     * a {@link ProgressTask}, then its {@link ProgressTask#getDescription()
     * description} will be returned. Otherwise, a string representation of
     * the given object will be returned.
     * 
     * @param task The tasl
     * @return The string for the task
     */
    private String stringFor(Object task)
    {
        if (task instanceof ProgressTask)
        {
            ProgressTask progressTask = (ProgressTask)task;
            return progressTask.getDescription();
        }
        return String.valueOf(task);
    }
    
}