/*
 * www.javagl.de - Swing Task Utilities
 *
 * Copyright (c) 2013-2015 Marco Hutter - http://www.javagl.de
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
package de.javagl.swing.tasks;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JFrame;

/**
 * Package-private utility methods related to {@link SwingTask}s and
 * {@link SwingTaskExecutor}s
 */
class SwingTaskUtils
{
    /**
     * Creates a handler for uncaught exceptions that shows an error dialog
     * 
     * @param parentComponent The parent component which determines the 
     * frame in which the dialog is displayed, or <code>null</code> to 
     * use a default frame
     * @return The handler
     */
    static UncaughtExceptionHandler createDialogUncaughtExceptionHandler(
        final Component parentComponent)
    {
        return createDialogUncaughtExceptionHandler(parentComponent, "Error");
    }
    
    /**
     * Creates a handler for uncaught exceptions that shows an error dialog
     * 
     * @param parentComponent The parent component which determines the 
     * frame in which the dialog is displayed, or <code>null</code> to 
     * use a default frame
     * @param dialogTitle The title of the dialog
     * @return The handler
     */
    static UncaughtExceptionHandler createDialogUncaughtExceptionHandler(
        final Component parentComponent, final String dialogTitle)
    {
        return new UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException(Thread t, Throwable e)
            {
                ThrowableDialog.show(parentComponent, dialogTitle, e, t);
            }
        };
    }
    
    
    /**
     * Utility method that finds the first focused frame. If no frame
     * is focused, then the first visible frame will be returned. 
     * If no frame is visible, then the first frame created by this
     * application will be returned. If no frame was created yet,
     * then <code>null</code> will be returned.
     *  
     * @return The potential parent window for a dialog
     */
    static Window findParentWindow()
    {
        Frame frames[] = JFrame.getFrames();
        if (frames.length == 0)
        {
            return null;
        }
        for (Frame f : frames)
        {
            if (f.isFocused())
            {
                return f;
            }
        }
        for (Frame f : frames)
        {
            if (f.isShowing())
            {
                return f;
            }
        }
        return frames[0];
    }
    

    /**
     * Private constructor to prevent instantiation
     */
    private SwingTaskUtils()
    {
        // Private constructor to prevent instantiation
    }
}
