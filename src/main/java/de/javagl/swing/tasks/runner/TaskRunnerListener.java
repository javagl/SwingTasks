package de.javagl.swing.tasks.runner;

/**
 * Interface for classes that want to be informed about the state
 * of a {@link TaskRunner}
 */
public interface TaskRunnerListener
{
    /**
     * Will be called immediately before the {@link TaskRunner} starts
     * the {@link Task}
     */
    void starting();
    
    /**
     * Will be called when the paused-state of the {@link TaskRunner} changed
     * 
     * @param paused The new paused state
     */
    void pauseChanged(boolean paused);
    
    /**
     * Will be called after the {@link TaskRunner} has finished executing
     * the {@link Task}
     */
    void finished();
}