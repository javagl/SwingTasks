package de.javagl.swing.tasks.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.javagl.swing.tasks.ProgressListener;
import de.javagl.swing.tasks.SwingTask;
import de.javagl.swing.tasks.SwingTaskExecutor;
import de.javagl.swing.tasks.SwingTaskExecutors;
import de.javagl.swing.tasks.SwingTaskView;
import de.javagl.swing.tasks.SwingTaskViews;
import de.javagl.swing.tasks.executors.GenericProgressTask;
import de.javagl.swing.tasks.executors.ObservableExecutorPanel;
import de.javagl.swing.tasks.executors.ObservableExecutorService;
import de.javagl.swing.tasks.executors.ObservableExecutors;
import de.javagl.swing.tasks.executors.ProgressTask;
import de.javagl.swing.tasks.executors.TaskView;

/**
 * A test / example combining the {@link SwingTask}s and the 
 * {@link ObservableExecutors}: A {@link SwingTask} schedules multiple 
 * {@link ProgressTask}s to an {@link ObservableExecutorService}, and
 * an {@link ObservableExecutorPanel} of the active tasks is shown 
 * as the accessory of a {@link SwingTaskView}. 
 */
@SuppressWarnings("javadoc")
public class SwingExecutorsTaskTest
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
        // Create the accessory component to be shown in the dialog
        JComponent accessory = new JPanel(new BorderLayout());
        accessory.setBorder(
            BorderFactory.createTitledBorder("Active tasks:"));
        
        // Create the executor service that will execute the tasks,
        // and an ObservableExectutorPanel that will be added to
        // the accessory
        ObservableExecutorService observableExecutorService = 
            ObservableExecutors.newCachedThreadPool();
        ObservableExecutorPanel observableExecutorPanel = 
            new ObservableExecutorPanel();
        observableExecutorPanel.setObservableExecutorService(
            observableExecutorService);
        observableExecutorPanel.setPreferredSize(new Dimension(600, 300));
        accessory.add(observableExecutorPanel, BorderLayout.CENTER);
        
        // Create a text area that will show possible exception stack
        // traces of selected tasks
        JTextArea statusTextArea = new JTextArea();
        statusTextArea.setTabSize(2);
        statusTextArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        observableExecutorPanel.addListSelectionListener(
            new ListSelectionListener()
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
        statusTextArea.setPreferredSize(new Dimension(600, 200));
        accessory.add(statusTextArea, BorderLayout.SOUTH);
        
        // Finally, assemble and execute the swing task executor
        SwingTask<Void,Void> swingTask = 
            createTestTask(observableExecutorService);
        SwingTaskExecutor<Void> h = 
            SwingTaskExecutors.create(swingTask)
                .setMillisToPopup(0)
                .setSwingTaskViewFactory(c -> 
                    SwingTaskViews.create(c, accessory, false))
                .setCancelable(true)
                .build();
        h.execute();
    }
    
    private static SwingTask<Void,Void> createTestTask(
        ExecutorService executorService)
    {
        SwingTask<Void,Void> task = new SwingTask<Void, Void>()
        {
            @Override
            public Void doInBackground()
            {
                // Schedule the dummy tasks by submitting them to the executor
                
                int n = 20;
                List<Future<Object>> futures = 
                    new ArrayList<Future<Object>>();
                for (int i=0; i<n; i++)
                {
                    Callable<Object> callable = 
                        createDummyProgressTask(((i+1) % 5 == 0));
                    Future<Object> future = 
                        executorService.submit(callable);
                    futures.add(future);
                }
                
                // Wait until all tasks are finished
                for (int i=0; i<futures.size(); i++)
                {
                    Future<Object> future = futures.get(i);
                    try
                    {
                        future.get();
                    }
                    catch (InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                        System.out.println("Task was interrupted");
                        shutdownNowAndWait(executorService);
                        return null;
                    }
                    catch (ExecutionException e)
                    {
                        // Ignored
                        System.out.println("Task caused an exception: " + 
                            e.getMessage());
                    }
                    setProgress((double)i / (futures.size() - 1));
                }
                shutdownNowAndWait(executorService);
                return null;
            }
            
            private void shutdownNowAndWait(ExecutorService executorService)
            {
                executorService.shutdownNow();
                try
                {
                    executorService.awaitTermination(5, TimeUnit.SECONDS);
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    System.out.println("Waiting was interrupted");
                }
            }
        };
        return task;
    }
    
    /**
     * A counter for the IDs of the dummy tasks
     */
    private static int taskIdCounter = 0;
    
    /**
     * Random number generator for the dummy task durations
     */
    private static final Random random = new Random(0);
    
    /**
     * Create a dummy {@link ProgressTask}
     * 
     * @param causeError Whether the task should cause an error
     * @return The task
     */
    private static Callable<Object> createDummyProgressTask(boolean causeError)
    {
        int id = taskIdCounter++;
        int delayMs = 2000 + random.nextInt(2000);
        
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
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(
                            "Interrupted " + Thread.currentThread());
                    }
                    progressListener.progressChanged(i / 9.0);
                    progressListener.messageChanged(", step "+i);
                    if (causeError && i > 5)
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
