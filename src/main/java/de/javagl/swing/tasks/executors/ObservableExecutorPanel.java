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
import java.awt.Font;
import java.awt.GridLayout;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;

import de.javagl.swing.tasks.ProgressListener;

/**
 * A panel that displays a list of tasks that are currently executed in
 * an {@link ObservableExecutorService}.<br>
 * <br>
 * The display and appearance of the list may be modified by assigning a 
 * {@link TaskViewHandler} using {@link #setTaskViewHandler(TaskViewHandler)},
 * which will configure the {@link TaskView} for each task, and by setting
 * a cell renderer that is responsible for displaying the {@link TaskView}s
 * in the list, using {@link #setCellRenderer(ListCellRenderer)}.
 */
public class ObservableExecutorPanel extends JPanel
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = -6643789350339749108L;

    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(ObservableExecutorPanel.class.getName());
    
    /**
     * The {@link ObservableExecutorService} that is currently displayed 
     * in this panel
     */
    private ObservableExecutorService observableExecutorService;
    
    /**
     * The {@link ExecutorObserver} that will be attached to the 
     * {@link ObservableExecutorService}, and forward all status
     * updates, on the Event Dispatch Thread, to the handling 
     * methods in this class.
     */
    private final ExecutorObserver executorObserver;

    /**
     * The list model that contains a {@link TaskView} for each task
     * that was submitted to the {@link ObservableExecutorService}
     */
    private DefaultListModel<TaskView> listModel;
    
    /**
     * The list that contains the list model
     */
    private JList<TaskView> list;
    
    /**
     * A map from the tasks that have been submitted to the 
     * {@link ObservableExecutorService} to the corresponding
     * {@link TaskView}s that are shown in the list
     */
    private final Map<Object, TaskView> taskViews;
    
    /**
     * A map from the tasks that have been submitted to the 
     * {@link ObservableExecutorService} and implement the 
     * {@link ProgressTask} interface to the corresponding
     * {@link ProgressListener}s that will receive the progress
     * information and forward it to the {@link TaskViewHandler}
     * on the Event Dispatch Thread
     */
    private final Map<ProgressTask, ProgressListener> progressListeners;
    
    /**
     * The {@link TaskViewHandler} that will update the {@link TaskView}
     * instances based on the execution status of the tasks
     */
    private TaskViewHandler taskViewHandler;
    
    /**
     * Creates a new, empty observable executor panel
     */
    public ObservableExecutorPanel()
    {
        super(new GridLayout(1,1));
        
        taskViews = new IdentityHashMap<Object, TaskView>();
        progressListeners = 
            new IdentityHashMap<ProgressTask, ProgressListener>();
        
        taskViewHandler = TaskViewHandlers.createDefault();

        listModel = new DefaultListModel<TaskView>();
        list = new JList<TaskView>(listModel);
        list.setFont(new Font("Monospaced", Font.BOLD, 12));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectionBackground(new Color(216,216,216));
        ListCellRenderer<Object> listCellRenderer = 
            TaskViewListCellRenderers.createWithProgressBar();
        list.setCellRenderer(listCellRenderer);
        add(new JScrollPane(list));

        executorObserver = new ExecutorObserver()
        {
            /**
             * If the given object is an ObservableTask, then this method
             * will return the runnable or callable that it was created 
             * from. Otherwise, the given runnable itself is returned.
             * (Note: If tasks are properly submitted to the 
             * ObservableExecutorService, then these runnables will always
             * be ObservableTasks. But to avoid errors and missing tasks,
             * this is checked here)
             * 
             * @param runnable The runnable
             * @return The task for the given runnable
             */
            private Object getTask(Runnable runnable)
            {
                Object innerTask = 
                    ObservableExecutors.getInnerTask(runnable, Object.class);
                if (innerTask != null)
                {
                    return innerTask;
                }
                return runnable;
            }
            
            @Override
            public void scheduled(Runnable r)
            {
                Object task = getTask(r);
                SwingUtilities.invokeLater(
                    () -> handleScheduled(task));
            }

            @Override
            public void beforeExecute(Thread t, Runnable r)
            {
                Object task = getTask(r);
                SwingUtilities.invokeLater(
                    () -> handleBeforeExecute(task));
            }
            
            @Override
            public void afterExecute(Runnable r, Throwable t)
            {
                Object task = getTask(r);
                SwingUtilities.invokeLater(
                    () -> handleAfterExecute(task, t));
            }
            
            @Override
            public void tasksFinished()
            {
                // Nothing to do here
            }
        };
    }
    
    /**
     * Set the cell renderer for the list that displays the {@link TaskView}
     * instances for the tasks that have been submitted to the 
     * {@link ObservableExecutorService} that is currently displayed in
     * this panel
     * 
     * @param cellRenderer The cell renderer
     */
    public final void setCellRenderer(
        ListCellRenderer<? super TaskView> cellRenderer)
    {
        list.setCellRenderer(cellRenderer);
    }
    
    /**
     * Add the given listener to be informed about selections in the
     * list that displays the {@link TaskView}s
     * 
     * @param listener The listener
     */
    public void addListSelectionListener(ListSelectionListener listener)
    {
        list.addListSelectionListener(listener);
    }
    
    /**
     * Remove the given listener to from the list that displays the 
     * {@link TaskView}s
     * 
     * @param listener The listener
     */
    public void removeListSelectionListener(ListSelectionListener listener)
    {
        list.removeListSelectionListener(listener);
    }
    
    /**
     * Set the {@link TaskViewHandler} that may be used to configure the
     * appearance of the {@link TaskView} instances that are displayed
     * in the list for each task that has been submitted to the 
     * {@link ObservableExecutorService}
     * 
     * @param taskViewHandler The {@link TaskViewHandler}. May not be 
     * <code>null</code>.
     */
    public final void setTaskViewHandler(TaskViewHandler taskViewHandler)
    {
        Objects.requireNonNull(taskViewHandler, 
            "The taskViewHandler may not be null");
        this.taskViewHandler = taskViewHandler;
    }
    
    /**
     * Set the {@link ObservableExecutorService} that should be displayed
     * in this panel
     * 
     * @param newObservableExecutorService The {@link ObservableExecutorService}
     */
    public final void setObservableExecutorService(
        ObservableExecutorService newObservableExecutorService)
    {
        if (observableExecutorService != null &&
            observableExecutorService != newObservableExecutorService)
        {
            observableExecutorService.removeExecutorObserver(executorObserver);
            listModel.removeAllElements();
            taskViews.clear();
            progressListeners.entrySet().stream().forEach(e -> 
            {
                e.getKey().removeProgressListener(e.getValue());
            });
            progressListeners.clear();
        }
        observableExecutorService = newObservableExecutorService;
        if (observableExecutorService != null)
        {
            observableExecutorService.addExecutorObserver(executorObserver);
        }
    }
    
    
    /**
     * Will be called on the Event Dispatch Thread when the given task was
     * scheduled for execution in the {@link ObservableExecutorService}
     * 
     * @param task The task
     */
    private void handleScheduled(Object task)
    {
        TaskView taskView = new TaskView(listModel, list);
        taskViews.put(task, taskView);
        listModel.addElement(taskView);
        
        taskViewHandler.scheduled(task, taskView);
        
        if (task instanceof ProgressTask)
        {
            ProgressTask progressTask = (ProgressTask)task;
            ProgressListener progressListener = new ProgressListener()
            {
                @Override
                public void progressChanged(double progress)
                {
                    SwingUtilities.invokeLater(() ->
                    {
                        taskViewHandler.progressChanged(
                            progressTask, taskView, progress);
                    });
                }
                
                @Override
                public void messageChanged(String message)
                {
                    SwingUtilities.invokeLater(() ->
                    {
                        taskViewHandler.messageChanged(
                            progressTask, taskView, message);
                    });
                }
            };
            progressTask.addProgressListener(progressListener);
            progressListeners.put(progressTask, progressListener);
        }
    }
    
    /**
     * Will be called on the Event Dispatch Thread when the given task 
     * is about to be executed in the {@link ObservableExecutorService}
     * 
     * @param task The task
     */
    private void handleBeforeExecute(Object task)
    {
        TaskView taskView = taskViews.get(task);
        if (taskView == null)
        {
            logger.warning("No taskView found for task " + task);
            return;
        }
        taskViewHandler.beforeExecute(task, taskView);
    }

    /**
     * Will be called on the Event Dispatch Thread when the given task 
     * finished execution in the {@link ObservableExecutorService}
     * 
     * @param task The task
     * @param t The throwable that was caused by the task, or <code>null</code>
     * if the task completed successfully
     */
    private void handleAfterExecute(Object task, Throwable t)
    {
        TaskView taskView = taskViews.get(task);
        if (taskView == null)
        {
            logger.warning("No taskView found for task " + task);
            return;
        }
        taskViewHandler.afterExecute(task, t, taskView);
        if (task instanceof ProgressTask)
        {
            ProgressTask progressTask = (ProgressTask)task;
            ProgressListener progressListener = 
                progressListeners.remove(progressTask);
            progressTask.removeProgressListener(progressListener);
        }
    }
    
}