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

import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * The panel containing the GUI components for controlling a 
 * {@link TaskRunner}. It is the view component for a 
 * {@link TaskRunnerController}.
 */
public class TaskRunnerControlPanel extends JPanel
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = -6228325008911002334L;

    /**
     * The {@link TaskRunnerController} controlled with this panel
     */
    private final TaskRunnerController taskRunnerController;

    /**
     * Default constructor
     */
    public TaskRunnerControlPanel()
    {
        this.taskRunnerController = new TaskRunnerController();
        
        setLayout(new GridLayout(1, 0));

        Action startAction = taskRunnerController.getStartAction();
        JButton startButton = new JButton(startAction);
        startButton.setMargin(new Insets(0,0,0,0));
        startAction.setEnabled(false);
        add(startButton);
        
        Action pauseAction = taskRunnerController.getPauseAction();
        JToggleButton pauseButton = new JToggleButton(pauseAction);
        pauseButton.setMargin(new Insets(0,0,0,0));
        pauseButton.setEnabled(false);
        add(pauseButton);

        Action singleStepAction = taskRunnerController.getSingleStepAction();
        JButton singleStepButton = new JButton(singleStepAction);
        singleStepButton.setMargin(new Insets(0,0,0,0));
        singleStepAction.setEnabled(false);
        add(singleStepButton);
        
        Action stopAction = taskRunnerController.getStopAction();
        JButton stopButton = new JButton(stopAction);
        stopButton.setMargin(new Insets(0,0,0,0));
        stopAction.setEnabled(false);
        add(stopButton);
    }
    
    /**
     * Set the {@link TaskRunner} that is controlled with this panel
     * 
     * @param newTaskRunner The {@link TaskRunner}
     */
    public void setTaskRunner(TaskRunner newTaskRunner)
    {
        taskRunnerController.setTaskRunner(newTaskRunner);
    }
    
    /**
     * Returns the current {@link TaskRunner}. May be <code>null</code>.
     * 
     * @return The current {@link TaskRunner}
     */
    public TaskRunner getTaskRunner()
    {
        return taskRunnerController.getTaskRunner();
    }
}