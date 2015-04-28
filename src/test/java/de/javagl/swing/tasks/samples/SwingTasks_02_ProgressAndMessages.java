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
 * An example showing how to report progress and messages from a 
 * {@link SwingTask}
 */
public class SwingTasks_02_ProgressAndMessages
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
        // Create an instance of the SwingTask class, implementing
        // the "doInBackground" method to perform some computation.
        // In this method, the "setProgress" and "setMessage" methods
        // of the SwingTask are used to inform the dialog about the
        // progress of the computation.
        SwingTask<Integer, ?> swingTask = new SwingTask<Integer, Void>()
        {
            @Override
            protected Integer doInBackground() throws Exception
            {
                System.out.println("Running on background thread");
                for (int i=0; i<=10; i++)
                {
                    setProgress(i / 10.0);
                    setMessage("Step "+i);
                    Thread.sleep(300);
                }
                System.out.println("Running on background thread DONE");
                return 42;
            }
        };
        
        // Create a SwingTaskExecutor with a default configuration,
        // and execute the SwingTask
        SwingTaskExecutors.create(swingTask).build().execute();
    }
}
