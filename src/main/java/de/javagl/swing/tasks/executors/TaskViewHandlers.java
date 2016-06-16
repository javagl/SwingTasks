/*
 * www.javagl.de - Swing Task Utilities
 *
 * Copyright (c) 2013-2016 Marco Hutter - http://www.javagl.de
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package de.javagl.swing.tasks.executors;

/**
 * Methods to create {@link TaskViewHandler} instances
 */
public class TaskViewHandlers
{
    /**
     * Creates a {@link TaskViewHandler} that shows basic information about
     * the execution status of tasks, with unspecified (but sensible) messages
     * and color coding.
     * 
     * @return The {@link TaskViewHandler}
     */
    public static TaskViewHandler createDefault()
    {
        return createDefault(false);
    }
    
    /**
     * Creates a {@link TaskViewHandler} that shows basic information about
     * the execution status of tasks, with unspecified (but sensible) messages
     * and color coding, which will remove finished tasks from the list
     * depending on the given flag.
     * 
     * @param removeSuccessfullyFinishedTasks Whether tasks that completed 
     * successfully should be removed from the list 
     * @return The {@link TaskViewHandler}
     */
    public static TaskViewHandler createDefault(
        boolean removeSuccessfullyFinishedTasks)
    {
        return new DefaultTaskViewHandler(removeSuccessfullyFinishedTasks);
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private TaskViewHandlers()
    {
        // Private constructor to prevent instantiation
    }
}
