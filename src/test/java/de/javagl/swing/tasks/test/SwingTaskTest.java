package de.javagl.swing.tasks.test;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import de.javagl.swing.tasks.ProgressHandler;
import de.javagl.swing.tasks.SwingTask;
import de.javagl.swing.tasks.SwingTaskExecutor;
import de.javagl.swing.tasks.SwingTaskExecutorBuilder;
import de.javagl.swing.tasks.SwingTaskExecutors;

/**
 * A test for various functionalities of the {@link SwingTask}s
 */
public class SwingTaskTest
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
        
        c.add(createSimpleButton(false, false));
        c.add(createSimpleButton(true, false));
        c.add(createSimpleButton(false, true));
        c.add(createSimpleButton(true, true));
        c.add(Box.createHorizontalStrut(1000));
        
        c.add(createWithErrorButton(false, true, false, false));
        c.add(createWithErrorButton(false, true, true, false));
        c.add(createWithErrorButton(false, true, true, true));
        c.add(Box.createHorizontalStrut(1000));

        c.add(createShortButton(false, true, 0, 1500, 1500));
        c.add(createShortButton(false, true, 1, 1, 1));
        c.add(Box.createHorizontalStrut(1000));
        
        c.add(createCreatedOnOtherThreadButton(true, true));
        c.add(createCancelledByOtherThreadButton(true, true));
        c.add(Box.createHorizontalStrut(1000));

        c.add(createComplexTestButton(false, false));
        c.add(Box.createHorizontalStrut(1000));

        c.add(createNestedButton(true, true, false));
        c.add(createNestedButton(true, true, true));
        c.add(Box.createHorizontalStrut(1000));
        
        c.add(createSupplierConsumerButton(false));
        c.add(createSupplierConsumerButton(true));
        c.add(Box.createHorizontalStrut(1000));
        
        f.setSize(800,400);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
    
    

    private static JButton createSimpleButton(
        final boolean modal, final boolean cancelable)
    {
        final JButton b = new JButton("Simple" + 
            (modal ? " modal" : "") + 
            (cancelable ? " cancelable" : ""));
        b.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                SwingTask<Integer,Integer> swingTask = 
                    createSimple(50, false);
                SwingTaskExecutor<Integer> h = 
                    SwingTaskExecutors.create(swingTask).
                        setCancelable(cancelable).
                        setModal(modal).
                        setParentComponent(b).
                        build();
                h.execute();
            }
        });
        return b;
    }
    
    private static JButton createComplexTestButton(
        final boolean modal, final boolean cancelable)
    {
        final JButton b = new JButton("ComplexTest" + 
            (modal ? " modal" : "") + 
            (cancelable ? " cancelable" : ""));
        b.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                SwingTask<Integer,Integer> swingTask = createComplexTest();
                SwingTaskExecutor<Integer> h = 
                    SwingTaskExecutors.create(swingTask).
                        setCancelable(cancelable).
                        setModal(modal).
                        setParentComponent(b).
                        build();
                h.execute();
            }
        });
        return b;
    }
    
    private static JButton createWithErrorButton(
        final boolean modal, final boolean cancelable, 
        final boolean handle, final boolean handleWithDialog)
    {
        final JButton b = new JButton("WithError" + 
            (modal ? " modal" : "") + 
            (cancelable ? " cancelable" : "") + 
            (handle ? " handled" : "") + 
            (handleWithDialog ? " dialog" : ""));
        b.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                SwingTask<Integer, Integer> swingTask = createSimple(50, true);
                UncaughtExceptionHandler uncaughtExceptionHandler = null;
                if (handle && !handleWithDialog)
                {
                    uncaughtExceptionHandler = new UncaughtExceptionHandler()
                    {
                        @Override
                        public void uncaughtException(Thread t, Throwable e)
                        {
                            System.out.println(
                                "UncaughtExceptionHandler handling "+e);
                        }
                    };
                }
                
                SwingTaskExecutorBuilder<Integer> builder =
                    SwingTaskExecutors.create(swingTask).
                    setCancelable(cancelable).
                    setModal(modal).
                    setParentComponent(b);
                if (handle)
                {
                    if (handleWithDialog)
                    {
                        builder.setDialogUncaughtExceptionHandler();
                    }
                    else
                    {
                        builder.setUncaughtExceptionHandler(
                            uncaughtExceptionHandler);
                    }
                }
                SwingTaskExecutor<Integer> h =  builder.build();
                h.execute();
            }
        });
        return b;
    }
    

    private static JButton createShortButton(
        final boolean modal, final boolean cancelable,
        final int msToTake, final int msToDecide, final int msToPopup)
    {
        final JButton b = new JButton("Short" + 
            (modal ? " modal" : "") + 
            (cancelable ? " cancelable" : "") + ", " + 
            "take: " + msToTake + ", " + 
            "decide: " + msToDecide + ", " +
            "popup: " + msToPopup);
        b.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                SwingTask<Integer,Integer> swingTask = 
                    createSimple(msToTake, false);
                SwingTaskExecutor<Integer> h = 
                    SwingTaskExecutors.create(swingTask).
                        setCancelable(cancelable).
                        setModal(modal).
                        setParentComponent(b).
                        setMillisToDecideToPopup(msToDecide).
                        setMillisToPopup(msToPopup).
                        build();
                h.execute();
            }
        });
        return b;
    }
    
    private static JButton createCreatedOnOtherThreadButton(
        final boolean modal, final boolean cancelable)
    {
        final JButton b = new JButton("CreatedOnOtherThread" + 
            (modal ? " modal" : "") + 
            (cancelable ? " cancelable" : ""));
        b.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                SwingTask<Integer,Integer> swingTask = createSimple(50, false);
                final SwingTaskExecutor<Integer> h = 
                    SwingTaskExecutors.create(swingTask).
                        setCancelable(cancelable).
                        setModal(modal).
                        setParentComponent(b).
                        build();
                Thread t = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        h.execute();
                    }
                });
                t.start();
            }
        });
        return b;
    }


    private static JButton createCancelledByOtherThreadButton(
        final boolean modal, final boolean cancelable)
    {
        final JButton b = new JButton("CancelledByOtherThread" + 
            (modal ? " modal" : "") + 
            (cancelable ? " cancelable" : ""));
        b.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                final SwingTask<Integer,Integer> swingTask = 
                    createSimple(50, false);
                final SwingTaskExecutor<Integer> h = 
                    SwingTaskExecutors.create(swingTask).
                        setCancelable(cancelable).
                        setModal(modal).
                        setParentComponent(b).
                        build();
                Thread t = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        swingTask.cancel(true);
                    }
                });
                t.start();
                h.execute();
            }
        });
        return b;
    }
    
    private static SwingTask<Integer,Integer> createSimple(
        final int stepMS, final boolean causeError)
    {
        SwingTask<Integer,Integer> task = new SwingTask<Integer, Integer>()
        {
            @Override
            public Integer doInBackground()
            {
                int n = 100;
                for (int i=0; i<=n; i++)
                {
                    publish(i);
                    if (causeError)
                    {
                        if (i >= 42)
                        {
                            throw new IndexOutOfBoundsException("42");
                        }
                    }
                    setProgress(i/100.0);
                    if (stepMS > 0)
                    {                    
                        try
                        {
                            Thread.sleep(stepMS);
                        }
                        catch (InterruptedException e)
                        {
                            System.out.println("SwingTask was interrupted");
                            return null;
                        }
                    }
                }
                System.out.println("Done, return "+n);
                return n;
            }
            
            @Override
            protected void process(List<Integer> chunks)
            {
                System.out.println("SwingTask processing "+chunks);
            }
            
            @Override
            protected void done()
            {
                try
                {
                    Integer result = get();
                    System.out.println("SwingTask done, result "+result+
                        ", cancelled "+isCancelled());
                }
                catch (CancellationException e)
                {
                    System.err.println("SwingTask done, "+e);
                    //e.printStackTrace();
                }
                catch (InterruptedException e)
                {
                    System.err.println("SwingTask done, "+e);
                    //e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    System.err.println("SwingTask done, "+e);
                    //e.printStackTrace();
                }
            }
            
            @Override
            public String toString()
            {
                return "Simple";
            }
        };
        return task;
    }
    
    

    private static SwingTask<Integer,Integer> createComplexTest()
    {
        SwingTask<Integer,Integer> task = new SwingTask<Integer, Integer>()
        {
            @Override
            public Integer doInBackground()
            {
                setMessage("Ping pong");
                setProgress(-1);
                for (int i=0; i<=100; i++)
                {
                    if (i > 50)
                    {
                        setProgress(i/100.0);
                    }
                    if (i == 50)
                    {
                        someOtherMethod(getProgressHandler());
                        setMessage("Back to normal");
                    }
                    try
                    {
                        Thread.sleep(150);
                    }
                    catch (InterruptedException e)
                    {
                        System.out.println("Task was interrupted");
                        return null;
                    }
                }
                return 100;
            }
            
            @Override
            public String toString()
            {
                return "ComplexTest";
            }
            
        };
        return task;
    }
    
    private static void someOtherMethod(ProgressHandler progressHandler)
    {
        progressHandler.setMessage("Some other method");
        for (int i=0; i<=100; i++)
        {
            progressHandler.setProgress(i/100.0);
            try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e)
            {
                System.out.println("Task was interrupted");
                return;
            }
        }
    }


    
    private static JButton createNestedButton(
        final boolean modal, final boolean cancelable, boolean causeError)
    {
        final JButton b = new JButton("Nested" + 
            (modal ? " modal" : "") + 
            (cancelable ? " cancelable" : "") + 
            (causeError ? " causeError" : ""));
        b.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                SwingTask<Void,?> swingTask = createNested(2, causeError);
                SwingTaskExecutor<Void> h = 
                    SwingTaskExecutors.create(swingTask).
                        setCancelable(cancelable).
                        setModal(modal).
                        build();
                h.execute();
            }
        });
        return b;
    }
    
    private static SwingTask<Void,?> createNested(
        final int level, final boolean causeError)
    {
        SwingTask<Void,?> task = new SwingTask<Void, Void>()
        {
            @Override
            public Void doInBackground()
            {
                for (int i=0; i<=10; i++)
                {
                    setProgress(i/10.0);
                    
                    if (i == 5)
                    {
                        if (level == 0 && causeError)
                        {
                            SwingTask<Integer, Integer> swingTask = 
                                createSimple(10, causeError);
                            SwingTaskExecutor<Integer> h = 
                                SwingTaskExecutors.create(swingTask).
                                    setCancelable(true).
                                    //setModal(false).
                                    setTitle("Child of level "+level).
                                    build();
                            setMessage("Executing "+swingTask);
                            System.out.println("Executing "+swingTask);
                            h.execute();
                        }
                        if (level > 0)
                        {
                            SwingTask<Void,?> swingTask = 
                                createNested(level-1, causeError);
                            SwingTaskExecutor<Void> h = 
                                SwingTaskExecutors.create(swingTask).
                                    setCancelable(true).
                                    //setModal(false).
                                    setTitle("Child of level "+level).
                                    build();
                            setMessage("Executing "+swingTask);
                            System.out.println("Executing "+swingTask);
                            h.execute();
                            System.out.println("Executing "+swingTask+" DONE");
                            setMessage("Executing "+swingTask+" DONE");
                        }
                    }
                    
                    try
                    {
                        Thread.sleep(250);
                    }
                    catch (InterruptedException e)
                    {
                        System.out.println("Task was interrupted");
                        return null;
                    }
                }
                return null;
            }
            
            @Override
            public String toString()
            {
                return "Nested level "+level;
            }
        };
        return task;
    }
    

    private static JButton createSupplierConsumerButton(
        final boolean causeError)
    {
        final JButton b = new JButton("Supplier/Consumer"+
            (causeError ? " causeError" : ""));
        b.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Supplier<Integer> supplier = new Supplier<Integer>()
                {
                    @Override
                    public Integer get()
                    {
                        System.out.println("Supplier working");
                        try
                        {
                            Thread.sleep(5000);
                        }
                        catch (InterruptedException e)
                        {
                            System.out.println("Supplier: "+e);
                            //e.printStackTrace();
                        }
                        if (causeError) 
                        {
                            throw new ArrayIndexOutOfBoundsException(42);
                        }
                        System.out.println("Supplier working DONE");
                        return 42;
                    }
                };
                Consumer<Integer> consumer = new Consumer<Integer>()
                {
                    @Override
                    public void accept(Integer t)
                    {
                        System.out.println("Consumer accepting " + t + 
                            " on " + Thread.currentThread());
                    }
                };
                SwingTaskExecutor<Integer> h = 
                    SwingTaskExecutors.create(supplier, consumer).
                    setParentComponent(b).
                    setModal(false).
                    setCancelable(true).
                    build();
                h.execute();
            }
        });
        return b;
    }
    
}
