/*
 * www.javagl.de - Swing Task Utilities
 *
 * Copyright (c) 2013-2015 Marco Hutter - http://www.javagl.de
 */
package de.javagl.swing.tasks.samples;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import de.javagl.swing.tasks.SwingTask;
import de.javagl.swing.tasks.SwingTaskExecutors;

/**
 * An example showing some convenience functions for {@link SwingTask}s
 */
public class SwingTasks_06_Convenience
{
    /**
     * Entry point of this sample
     * 
     * @param args Not used
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                JFrame f = new JFrame("SwingTasks");
                
                JButton startButton = new JButton("Start");
                startButton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        startTask();
                    }
                });
                f.getContentPane().setLayout(new FlowLayout());
                f.getContentPane().add(startButton);
                
                f.setSize(300,150);
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            }
        });
    }
    
    /**
     * Start a sample {@link SwingTask}
     */
    private static void startTask()
    {
        // Create a SwingTaskExecutor that executes a SwingTask
        // that simply calls a (possibly long-running) method 
        // and passes the result to a consumer method.
        SwingTaskExecutors.create(
            SwingTasks_06_Convenience::someMethodComputingTheResult,
            SwingTasks_06_Convenience::someMethodReceivingTheResult).
            build().execute();
    }
    
    
    // An example method that may perform a long-running computation
    private static Integer someMethodComputingTheResult()
    {
        System.out.println("Computing the result");
        try
        {
            Thread.sleep(3000);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        System.out.println("Computing the result DONE");
        return 42;
    }
    
    // An example method that may receive the result of a computation
    private static void someMethodReceivingTheResult(Integer result)
    {
        System.out.println("Received the result "+result);
    }
    
}
