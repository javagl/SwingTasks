package de.javagl.swing.tasks.test;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import de.javagl.swing.tasks.SwingTask;
import de.javagl.swing.tasks.SwingTaskExecutors;

public class SwingTaskTestIssue1
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

        c.add(createTestButton());

        f.setSize(800, 400);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private static JButton createTestButton()
    {
        final JButton b = new JButton("Test");
        b.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {

                SwingTask<Integer, Integer> swingTask = createTest();
                SwingTaskExecutors
                    .create(swingTask)
                    .setTitle("Loading")
                    .setMillisToPopup(0)
                    .setCancelable(true)
                    .build()
                    .execute();
            }
        });
        return b;
    }

    private static SwingTask<Integer, Integer> createTest()
    {
        SwingTask<Integer, Integer> task = new SwingTask<Integer, Integer>()
        {
            @Override
            public Integer doInBackground()
            {
                int n = 0;
                try
                {
                    Thread.sleep(1);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                // System.out.println("Done, return "+n);
                return n;
            }

            @Override
            protected void process(List<Integer> chunks)
            {
                // System.out.println("SwingTask processing "+chunks);
            }

            @Override
            protected void done()
            {
                try
                {
                    Integer result = get();
                    System.out.println("SwingTask done, result " + result
                        + ", cancelled " + isCancelled());
                }
                catch (CancellationException e)
                {
                    System.err.println("SwingTask done, " + e);
                    // e.printStackTrace();
                }
                catch (InterruptedException e)
                {
                    System.err.println("SwingTask done, " + e);
                    // e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    System.err.println("SwingTask done, " + e);
                    // e.printStackTrace();
                }
            }

            @Override
            public String toString()
            {
                return "Test";
            }
        };
        return task;
    }

}
