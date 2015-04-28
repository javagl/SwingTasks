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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;

import de.javagl.swing.tasks.SwingTask;
import de.javagl.swing.tasks.SwingTaskExecutors;

/**
 * A controller for a {@link TaskRunner}, offering actions to start and
 * stop the {@link TaskRunner}, and to pause it or perform single steps.
 */
public final class TaskRunnerController
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(TaskRunnerController.class.getName());

    /**
     * The log level for detail messages
     */
    private static final Level logLevel = Level.INFO;
    

    /**
     * The {@link TaskRunner} which is controlled 
     */
    private TaskRunner taskRunner;

    /**
     * The Action for starting the task
     * 
     * @see #startTask()
     */
    private final Action startAction = new AbstractAction()
    {
        /**
         * Serial UID
         */
        private static final long serialVersionUID = -5125243029591873126L;

        // Initialization
        {
            putValue(NAME, "Start");
            putValue(SHORT_DESCRIPTION, "Start");
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_S));
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            startTask();
        }
    };

    /**
     * The Action for pausing the task
     * 
     * @see #pauseTask()
     */
    private final Action pauseAction = new AbstractAction()
    {
        /**
         * Serial UID
         */
        private static final long serialVersionUID = -5125243029591873126L;

        // Initialization
        {
            putValue(NAME, "Pause");
            putValue(SHORT_DESCRIPTION, "Pause");
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_P));
            putValue(SELECTED_KEY, false);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            pauseTask();
        }
    };

    /**
     * The Action for doing a single step
     * 
     * @see #singleStep()
     */
    private final Action singleStepAction = new AbstractAction()
    {
        /**
         * Serial UID
         */
        private static final long serialVersionUID = -512524302959126L;

        // Initialization
        {
            putValue(NAME, "Single step");
            putValue(SHORT_DESCRIPTION, "Single step");
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_I));
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            singleStep();
        }
    };
    
    /**
     * The Action for stopping the task
     * 
     * @see #stopTask()
     */
    private final Action stopAction = new AbstractAction()
    {
        /**
         * Serial UID
         */
        private static final long serialVersionUID = -5125243029591873126L;

        // Initialization
        {
            putValue(NAME, "Stop");
            putValue(SHORT_DESCRIPTION, "Stop");
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_T));
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            stopTask();
        }
    };
    
    /**
     * The {@link TaskRunnerListener} that will update the button states
     */
    private final TaskRunnerListener taskRunnerListener = 
        new TaskRunnerAdapter()
    {
        @Override
        public void pauseChanged(final boolean paused) 
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    pauseAction.putValue(AbstractAction.SELECTED_KEY, paused);
                }
            });
        }
        
        @Override
        public void finished()
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    startAction.setEnabled(true);
                    pauseAction.putValue(AbstractAction.SELECTED_KEY, false);
                    pauseAction.setEnabled(false);
                    singleStepAction.setEnabled(true);
                    stopAction.setEnabled(false);
                }
            });
        }
    };
    
    
    /**
     * Default constructor
     */
    public TaskRunnerController()
    {
        startAction.setEnabled(false);
        pauseAction.setEnabled(false);
        singleStepAction.setEnabled(false);
        stopAction.setEnabled(false);
    }
    
    /**
     * Returns the action to start the {@link TaskRunner}
     * 
     * @return The action
     */
    public Action getStartAction()
    {
        return startAction;
    }
    
    /**
     * Returns the action to stop the {@link TaskRunner}
     * 
     * @return The action
     */
    public Action getStopAction()
    {
        return stopAction;
    }
    
    /**
     * Returns the action to pause the {@link TaskRunner}
     * 
     * @return The action
     */
    public Action getPauseAction()
    {
        return pauseAction;
    }
    
    /**
     * Returns the action to perform a single the step in the {@link TaskRunner}
     * 
     * @return The action
     */
    public Action getSingleStepAction()
    {
        return singleStepAction;
    }
    
    /**
     * Set the {@link TaskRunner} that is controlled with this panel
     * 
     * @param newTaskRunner The {@link TaskRunner}
     */
    public void setTaskRunner(TaskRunner newTaskRunner)
    {
        if (taskRunner != null)
        {
            taskRunner.removeTaskRunnerListener(taskRunnerListener);
        }
        taskRunner = newTaskRunner;
        if (taskRunner != null)
        {
            taskRunner.addTaskRunnerListener(taskRunnerListener);
            startAction.setEnabled(true);
            pauseAction.putValue(AbstractAction.SELECTED_KEY, false);
            pauseAction.setEnabled(false);
            singleStepAction.setEnabled(true);
            stopAction.setEnabled(false);
        }
        else
        {
            startAction.setEnabled(false);
            pauseAction.putValue(AbstractAction.SELECTED_KEY, false);
            pauseAction.setEnabled(false);
            singleStepAction.setEnabled(false);
            stopAction.setEnabled(false);
        }
    }
    
    /**
     * Returns the current {@link TaskRunner}. May be <code>null</code>.
     * 
     * @return The current {@link TaskRunner}
     */
    public TaskRunner getTaskRunner()
    {
        return taskRunner;
    }

    /**
     * Start the task by calling {@link TaskRunner#start()}.
     */
    private void startTask()
    {
        startAction.setEnabled(false);
        pauseAction.setEnabled(true);
        stopAction.setEnabled(true);
        
        log("Calling taskRunner.start()");
        taskRunner.start();
        log("Calling taskRunner.start() DONE");
    }
    
    /**
     * Calls {@link TaskRunner#setPaused(boolean)} with the value of 
     * the SELECTED_KEY of the pause action
     */
    private void pauseTask()
    {
        Boolean paused = 
            (Boolean)pauseAction.getValue(AbstractAction.SELECTED_KEY);

        log("Calling taskRunner.setPaused("+paused+")");
        taskRunner.setPaused(paused);
        log("Calling taskRunner.setPaused("+paused+") DONE");
    }

    /**
     * Calls {@link TaskRunner#singleStep()}
     */
    private void singleStep()
    {
        startAction.setEnabled(false);
        pauseAction.setEnabled(true);
        stopAction.setEnabled(true);
        
        log("Calling taskRunner.singleStep()");
        taskRunner.singleStep();
        log("Calling taskRunner.singleStep() DONE");
    }
    
    /**
     * Stops the task by calling {@link TaskRunner#stop(boolean)},
     * showing a modal dialog until the method returns.
     */
    private void stopTask()
    {
        class Task extends SwingTask<Void, Void>
        {
            @Override
            public Void doInBackground()
            {
                log("Calling taskRunner.stop()");
                taskRunner.stop(true);
                log("Calling taskRunner.stop() DONE");
                return null;
            }
        }
        Task task = new Task();
        SwingTaskExecutors.create(task).setTitle("Waiting").build().execute();
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
