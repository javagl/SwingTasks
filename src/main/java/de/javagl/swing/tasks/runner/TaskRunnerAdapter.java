package de.javagl.swing.tasks.runner;

/**
 * Abstract base implementation of a {@link TaskRunnerListener}. 
 * All methods are empty and may be overridden.
 */
public abstract class TaskRunnerAdapter implements TaskRunnerListener
{

    @Override
    public void starting()
    {
        // Empty default implementation
    }

    @Override
    public void pauseChanged(boolean paused)
    {
        // Empty default implementation
    }

    @Override
    public void finished()
    {
        // Empty default implementation
    }
    
}