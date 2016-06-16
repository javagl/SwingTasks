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

import javax.swing.ListCellRenderer;

/**
 * Methods to create list cell renderers that show a {@link TaskView} 
 */
public class TaskViewListCellRenderers
{
    /**
     * Creates a default list cell renderer that displays basic information
     * about a {@link TaskView}
     * 
     * @return The list cell renderer
     */
    public static ListCellRenderer<Object> createBasic()
    {
        return new TaskViewListCellRenderer();
    }

    /**
     * Creates a list cell renderer that displays basic information
     * about a {@link TaskView}, and shows a small progress bar
     * that indicates the progress of the task.
     * 
     * @return The list cell renderer
     */
    public static ListCellRenderer<Object> createWithProgressBar()
    {
        return new ProgressBarTaskViewListCellRenderer();
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private TaskViewListCellRenderers()
    {
        // Private constructor to prevent instantiation
    }
}
