/**
 * Classes related to the asynchronous execution of long-running 
 * tasks in Swing. 
 * <p>
 * The core class of this package is the {@link de.javagl.swing.tasks.SwingTask}
 * class. It offers the same possibilities as a {@link javax.swing.SwingWorker},
 * but additionally allows progress reporting and registering callbacks for
 * event notifications.
 * <p>
 * The execution of <code>SwingTask</code>s is managed with  
 * {@link de.javagl.swing.tasks.SwingTaskExecutor}s. 
 */
package de.javagl.swing.tasks;

