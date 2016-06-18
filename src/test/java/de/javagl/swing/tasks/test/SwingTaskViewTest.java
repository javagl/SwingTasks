package de.javagl.swing.tasks.test;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

import de.javagl.swing.tasks.SwingTask;
import de.javagl.swing.tasks.SwingTaskExecutor;
import de.javagl.swing.tasks.SwingTaskExecutors;
import de.javagl.swing.tasks.SwingTaskView;
import de.javagl.swing.tasks.SwingTaskViewFactory;
import de.javagl.swing.tasks.SwingTaskViews;

/**
 * A test for custom {@link SwingTaskView} implementations
 */
@SuppressWarnings("javadoc")
public class SwingTaskViewTest
{
    /**
     * Entry point of this test
     * 
     * @param args Not used
     */
    public static void main(String[] args)
    {
        Logger logger = null;
        
        logger = Logger.getLogger("de.javagl");
        LoggerUtil.configureDefault(logger);
        logger.setLevel(Level.FINE);
        
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
     * Create the GUI, to be called on the EDT
     */
    private static void createAndShowGUI()
    {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Container c = f.getContentPane();
        c.setLayout(new FlowLayout());
        
        JButton testButton = new JButton("Test");
        c.add(testButton);
        testButton.addActionListener(e -> runTest());
        
        f.setSize(800,400);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
    
    
    private static void runTest()
    {
        JComponent accessory = new JPanel(new GridLayout(1,1));
        accessory.setBorder(
            BorderFactory.createTitledBorder("Test accessory component:"));
        accessory.add(new JScrollPane(new JTree()));
        
        SwingTaskViewFactory swingTaskViewFactory = 
            c -> SwingTaskViews.create(c, accessory, false);
        
        SwingTask<Integer,Integer> swingTask = createTestTask();
        SwingTaskExecutor<Integer> h = 
            SwingTaskExecutors.create(swingTask)
                .setSwingTaskViewFactory(swingTaskViewFactory)
                .setCancelable(true)
                .setModal(true)
                .build();
        h.execute();
    }
    
    private static SwingTask<Integer,Integer> createTestTask()
    {
        SwingTask<Integer,Integer> task = new SwingTask<Integer, Integer>()
        {
            @Override
            public Integer doInBackground()
            {
                int n = 100;
                for (int i=0; i<=n; i++)
                {
                    setProgress(i/100.0);
                    try
                    {
                        Thread.sleep(30);
                    }
                    catch (InterruptedException e)
                    {
                        System.out.println("SwingTask was interrupted");
                        return null;
                    }
                }
                System.out.println("Done, return "+n);
                return n;
            }
        };
        return task;
    }
    
    
}
