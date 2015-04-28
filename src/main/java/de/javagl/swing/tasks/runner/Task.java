package de.javagl.swing.tasks.runner;

/**
 * Interface for a task that is executed by a {@link TaskRunner}
 */
public interface Task
{
    /**
     * Will be called before the task is run for the first time
     */
    void started();
    
    /**
     * Will be called repeatedly to actually run the task
     */
    void run();
    
    /**
     * Implementors should return here whether this task is done, and 
     * the {@link #run()} method should no longer be called
     * 
     * @return Whether this task is done
     */
    boolean isDone();
    
    /**
     * Will be called when the task is finished.
     * 
     * @param completed Whether the task completed normally (due to
     * the {@link #isDone()} method having returned <code>true</code>).
     * If the task runner is {@link TaskRunner#stop(boolean) stopped},
     * this value will be <code>false</code>.
     * @param t The throwable that caused the task to finish.
     * If the task completed normally or was stopped, then this
     * throwable will be <code>null</code>.
     */
    void finished(boolean completed, Throwable t);
}