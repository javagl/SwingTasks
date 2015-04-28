package de.javagl.swing.tasks.test;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.javagl.swing.tasks.runner.Task;
import de.javagl.swing.tasks.runner.TaskRunner;
import de.javagl.swing.tasks.runner.TaskRunnerControlPanel;

/**
 * Test for the {@link TaskRunner} classes
 */
public class TaskRunnerTest
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(TaskRunnerTest.class.getName());

    /**
     * The entry point of this test
     * 
     * @param args Not used
     */
    public static void main(String[] args)
    {
        Logger logger = null;
        
        logger = Logger.getLogger("de.javagl");
        LoggerUtil.configureDefault(logger);
        logger.setLevel(Level.CONFIG);
        
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                createAndShowGUI();
            }
        });
    }
    
    /**
     * Create the GUI. To be called on the EDT
     */
    private static void createAndShowGUI()
    {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setLayout(new FlowLayout());
        
        final TaskRunnerControlPanel controlPanel = 
            new TaskRunnerControlPanel();
        
        JPanel buttonPanel = new JPanel(new GridLayout(1,0));
        
        JButton createSimpleTaskButton = new JButton("createSimpleTask");
        createSimpleTaskButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Task task = createSimpleTask();
                TaskRunner taskRunner = new TaskRunner(task);
                controlPanel.setTaskRunner(taskRunner);
            }
        });
        buttonPanel.add(createSimpleTaskButton);

        JButton createInterruptibleTaskButton =
            new JButton("createInterruptibleTask");
        createInterruptibleTaskButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Task task = createInterruptibleTask();
                TaskRunner taskRunner = new TaskRunner(task);
                controlPanel.setTaskRunner(taskRunner);
            }
        });
        buttonPanel.add(createInterruptibleTaskButton);

        JButton createErrorTaskButton = new JButton("createErrorTask");
        createErrorTaskButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Task task = createErrorTask();
                TaskRunner taskRunner = new TaskRunner(task);
                controlPanel.setTaskRunner(taskRunner);
            }
        });
        buttonPanel.add(createErrorTaskButton);

        
        
        f.getContentPane().add(buttonPanel);
        f.getContentPane().add(controlPanel);
        
        f.setSize(1200, 200);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
    
    
    /**
     * Create a simple {@link Task} to be run by a {@link TaskRunner}.
     * (This task will do "busy waiting", so it can't be interrupted)
     * 
     * @return The task
     */
    private static Task createSimpleTask()
    {
        return new Task()
        {
            int counter = 0;
            
            @Override
            public void started()
            {
                counter = 0;
                logger.info("SimpleTask started");
            }
            
            @Override
            public void run()
            {
                logger.info("SimpleTask running, step "+counter);
                
                long before = System.currentTimeMillis();
                while (System.currentTimeMillis() < before+3000) 
                {
                    //System.out.println("Waiting");
                }
                
                logger.info("SimpleTask running, step "+counter+" DONE");
                counter++;
            }
            
            @Override
            public boolean isDone()
            {
                return counter >= 4;
            }
            
            @Override
            public void finished(boolean completed, Throwable t)
            {
                logger.info("SimpleTask finished, " + 
                    "completed "+completed+", throwable "+t);
            }
        };
    }

    /**
     * Create an interruptible {@link Task} to be run by a {@link TaskRunner}
     * 
     * @return The task
     */
    private static Task createInterruptibleTask()
    {
        return new Task()
        {
            int counter = 0;
            
            @Override
            public void started()
            {
                counter = 0;
                logger.info("InterruptibleTask started");
            }
            
            @Override
            public void run()
            {
                logger.info("InterruptibleTask running, step "+counter);
                
                try
                {
                    Thread.sleep(3000);
                }
                catch (InterruptedException e)
                {
                    logger.info("InterruptibleTask was interrupted");
                }
                
                logger.info("InterruptibleTask running, step "+counter+" DONE");
                counter++;
            }
            
            @Override
            public boolean isDone()
            {
                return counter >= 4;
            }
            
            @Override
            public void finished(boolean completed, Throwable t)
            {
                logger.info("InterruptibleTask finished, "+
                    "completed "+completed+", throwable "+t);
            }
        };
    }
    
    
    /**
     * Create a simple {@link Task} to be run by a {@link TaskRunner},
     * that causes an error after a few steps
     * 
     * @return The task
     */
    private static Task createErrorTask()
    {
        return new Task()
        {
            int counter = 0;
            
            @Override
            public void started()
            {
                counter = 0;
                logger.info("ErrorTask started");
            }
            
            @Override
            public void run()
            {
                logger.info("ErrorTask running, step "+counter);
                
                long before = System.currentTimeMillis();
                while (System.currentTimeMillis() < before+3000) 
                {
                    //System.out.println("Waiting");
                }
                if (counter == 2)
                {
                    throw new IndexOutOfBoundsException("42");
                }

                logger.info("ErrorTask running, step "+counter+" DONE");
                counter++;
                
            }
            
            @Override
            public boolean isDone()
            {
                return counter >= 4;
            }
            
            @Override
            public void finished(boolean completed, Throwable t)
            {
                logger.info("ErrorTask finished, "+
                    "completed "+completed+", throwable "+t);
            }
        };
    }
    
    
}
