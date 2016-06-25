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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ListCellRenderer;

/**
 * Implementation of a list cell renderer that shows basic information
 * about a {@link TaskView}, and a small progress bar indicating the
 * progress of the task.
 */
class ProgressBarTaskViewListCellRenderer implements ListCellRenderer<Object>
{
    /**
     * The renderer component
     */
    private final JPanel component;
    
    /**
     * The delegate that is responsible for the text
     */
    private final TaskViewListCellRenderer delegate;
    
    /**
     * The progress bar that shows the progress
     */
    private final JProgressBar progressBar;
    
    /**
     * Default constructor
     */
    ProgressBarTaskViewListCellRenderer()
    {
        component = new JPanel(new BorderLayout());
        delegate = new TaskViewListCellRenderer();
        component.add(delegate, BorderLayout.CENTER);

        progressBar = new JProgressBar(0, 100);
        int height = delegate.getPreferredSize().height;
        int width = 50;
        progressBar.setPreferredSize(new Dimension(width, height));
        component.add(progressBar, BorderLayout.EAST);
        component.setBackground(delegate.getBackground());
    }
    
    @Override
    public Component getListCellRendererComponent(JList<?> list,
        Object value, int index, boolean isSelected,
        boolean cellHasFocus)
    {
        delegate.getListCellRendererComponent(
            list, value, index, isSelected, cellHasFocus);
        if (value instanceof TaskView)
        {
            TaskView taskView = (TaskView)value;
            double progress = taskView.getProgress();
            int percent = (int)(progress * 100);
            progressBar.setStringPainted(progress >= 0);
            progressBar.setValue(Math.min(100, Math.max(0, percent)));
        }
        return component;
    }
}