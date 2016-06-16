/*
 * www.javagl.de - Swing Task Utilities
 *
 * Copyright (c) 2013-2016 Marco Hutter - http://www.javagl.de
 */
package de.javagl.swing.tasks.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.javagl.swing.tasks.ProgressListener;
import de.javagl.swing.tasks.executors.ExecutorObserver;
import de.javagl.swing.tasks.executors.GenericProgressTask;
import de.javagl.swing.tasks.executors.ObservableExecutorPanel;
import de.javagl.swing.tasks.executors.ObservableExecutorService;
import de.javagl.swing.tasks.executors.ObservableExecutors;
import de.javagl.swing.tasks.executors.ProgressTask;
import de.javagl.swing.tasks.executors.TaskView;

/**
 * Basic integration test for the swing task executors package
 */
public class ObservableExecutorsTest
{
    /**
     * A counter for the IDs of the dummy tasks
     */
    private static int taskIdCounter = 0;
    
    /**
     * Random number generator for the dummy task durations
     */
    private static final Random random = new Random(0);
    
    /**
     * The entry point of this test
     * 
     * @param args Not used
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    /**
     * Create and show the GUI, to be called on the Event Dispatch Thread
     */
    private static void createAndShowGUI()
    {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setLayout(new BorderLayout());

        ObservableExecutorService observableExecutorService = 
            ObservableExecutors.newFixedThreadPool(4);
        
        observableExecutorService.addExecutorObserver(
            createLoggingExecutorObserver());
        
        ObservableExecutorPanel observableExecutorPanel = 
            new ObservableExecutorPanel();
        observableExecutorPanel.setObservableExecutorService(
            observableExecutorService);
        
        
        JTextArea statusTextArea = new JTextArea();
        statusTextArea.setTabSize(2);
        statusTextArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        observableExecutorPanel.addListSelectionListener(new ListSelectionListener()
        {
            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                JList<?> list = (JList<?>)e.getSource();
                Object value = list.getSelectedValue();
                TaskView taskView = (TaskView)value;
                Throwable throwable = taskView.getThrowable();
                if (throwable != null)
                {
                    StringWriter stringWriter = new StringWriter();
                    throwable.printStackTrace(new PrintWriter(stringWriter));
                    statusTextArea.setText(stringWriter.toString());
                }
                else
                {
                    statusTextArea.setText("");
                }
            }
        });
        
        //observableExecutorPanel.setTaskViewHandler(
        //    TaskViewHandlers.createDefault(true));
        //observableExecutorPanel.setCellRenderer(
        //    TaskViewListCellRenderers.createBasic());
        
        f.getContentPane().add(observableExecutorPanel, BorderLayout.CENTER);
        
        JPanel controlPanel = createControlPanel(
            observableExecutorService, statusTextArea);
        f.getContentPane().add(controlPanel, BorderLayout.EAST);
        
        f.setSize(1000,600);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
    
    /**
     * Create an {@link ExecutorObserver} that prints some logging information
     * 
     * @return The {@link ExecutorObserver}
     */
    private static ExecutorObserver createLoggingExecutorObserver()
    {
        ExecutorObserver executorObserver = new ExecutorObserver()
        {
            @Override
            public void scheduled(Runnable command)
            {
                System.out.println("scheduled     "+command);
            }
            
            @Override
            public void beforeExecute(Thread t, Runnable r)
            {
                System.out.println("beforeExecute "+r+" "+t);
            }
            
            @Override
            public void afterExecute(Runnable r, Throwable t)
            {
                System.out.println("afterExecute  "+r+" "+t);
            }

            @Override
            public void tasksFinished()
            {
                System.out.println("tasksFinished");
            }
        };
        return executorObserver;
    }

    /**
     * Create the control panel for this test
     * 
     * @param observableExecutorService The {@link ObservableExecutorService}
     * @param statusTextArea A text area for status messages
     * 
     * @return The control panel
     */
    private static JPanel createControlPanel(
        ObservableExecutorService observableExecutorService,
        JTextArea statusTextArea)
    {
        JPanel controlPanel = new JPanel(new BorderLayout());
        JPanel p = new JPanel(new GridLayout(0,1));
        controlPanel.add(p, BorderLayout.NORTH);
        
        JButton addRandomTasksButton = 
            new JButton("Add random tasks");
        p.add(addRandomTasksButton);
        addRandomTasksButton.addActionListener(
            e -> addRandomTasks(observableExecutorService));

        JButton addRandomTasksWithErrorButton = 
            new JButton("Add random tasks with error");
        p.add(addRandomTasksWithErrorButton);
        addRandomTasksWithErrorButton.addActionListener(
            e -> addRandomTasksWithError(observableExecutorService));
        
        JButton addRandomProgressTasksButton = 
            new JButton("Add random progress tasks");
        p.add(addRandomProgressTasksButton);
        addRandomProgressTasksButton.addActionListener(
            e -> addRandomProgressTasks(observableExecutorService));
        
        JButton addRandomProgressTasksWithErrorButton = 
            new JButton("Add random progress tasks with error");
        p.add(addRandomProgressTasksWithErrorButton);
        addRandomProgressTasksWithErrorButton.addActionListener(
            e -> addRandomProgressTasksWithError(observableExecutorService));
        
        JScrollPane scrollPane = new JScrollPane(statusTextArea);
        scrollPane.setPreferredSize(new Dimension(400, 1000));
        controlPanel.add(scrollPane, BorderLayout.CENTER);
        
        return controlPanel;
    }

    /**
     * Add some random tasks to the given {@link ObservableExecutorService}
     * 
     * @param observableExecutorService The {@link ObservableExecutorService}
     */
    private static void addRandomTasks(
        ObservableExecutorService observableExecutorService)
    {
        for (int i=0; i<10; i++)
        {
            observableExecutorService.submit(
                createDummyTask(false));
        }
    }
    
    /**
     * Add some random tasks to the given {@link ObservableExecutorService},
     * of which some will cause an error
     * 
     * @param observableExecutorService The {@link ObservableExecutorService}
     */
    private static void addRandomTasksWithError(
        ObservableExecutorService observableExecutorService)
    {
        for (int i=0; i<10; i++)
        {
            boolean causeError = (i % 3 == 1);
            observableExecutorService.submit(
                createDummyTask(causeError));
        }
    }
    
    /**
     * Create a dummy task
     * 
     * @param causeError Whether the task should cause an error
     * @return The task
     */
    private static Runnable createDummyTask(boolean causeError)
    {
        int id = taskIdCounter++;
        int delayMs = 1000 + random.nextInt(2000);
        Runnable task = new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println(this + " running...");
                try
                {
                    Thread.sleep(delayMs);
                } 
                catch (InterruptedException e)
                {
                    // Ignored here
                }
                if (causeError)
                {
                    throw new RuntimeException(
                        "Exception, intentionally thrown");
                }
                System.out.println(this + " running... DONE");
            }
            
            @Override
            public String toString()
            {
                return "Task " + id + ", " + delayMs + "ms";
            }
        };
        return task;
    }
    
    /**
     * Add some random {@link ProgressTask} instances to the given 
     * {@link ObservableExecutorService}
     * 
     * @param observableExecutorService The {@link ObservableExecutorService}
     */
    private static void addRandomProgressTasks(
        ObservableExecutorService observableExecutorService)
    {
        for (int i=0; i<10; i++)
        {
            observableExecutorService.submit(
                createDummyProgressTask(false));
        }
    }
    
    /**
     * Add some random {@link ProgressTask} instances to the given 
     * {@link ObservableExecutorService}, of which some will cause an error
     * 
     * @param observableExecutorService The {@link ObservableExecutorService}
     */
    private static void addRandomProgressTasksWithError(
        ObservableExecutorService observableExecutorService)
    {
        for (int i=0; i<10; i++)
        {
            boolean causeError = (i % 3 == 1);
            observableExecutorService.submit(
                createDummyProgressTask(causeError));
        }
    }
    
    /**
     * Create a dummy {@link ProgressTask}
     * 
     * @param causeError Whether the task should cause an error
     * @return The task
     */
    private static Callable<Object> createDummyProgressTask(boolean causeError)
    {
        int id = taskIdCounter++;
        int delayMs = 2000 + random.nextInt(4000);
        
        
        GenericProgressTask<Object> progressTask = 
            new GenericProgressTask<Object>(
                "Task " + id + ", " + delayMs + "ms");
        ProgressListener progressListener = 
            progressTask.getDispatchingProgressListener();
        
        Runnable task = new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println(this + " running...");
                for (int i=0; i<10; i++)
                {
                    try
                    {
                        Thread.sleep(delayMs / 10);
                    } 
                    catch (InterruptedException e)
                    {
                        // Ignored here
                    }
                    progressListener.progressChanged(i / 9.0);
                    progressListener.messageChanged(", step "+i);
                    if (causeError)
                    {
                        throw new RuntimeException(
                            "Exception, thrown for testing");
                    }
                }
                System.out.println(this + " running... DONE");
            }
            
            @Override
            public String toString()
            {
                return "Task " + id + ", " + delayMs + "ms";
            }
        };
        progressTask.setCallable(Executors.callable(task));
        return progressTask;
    }
    

    
    
}
