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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

/**
 * A dialog that may be shown to indicate the progress of a {@link SwingTask}.
 */
class SwingTaskDialog extends JDialog
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = -4247620980959161270L;

    /**
     * The text area showing the current {@link SwingTask#getMessage() message}
     * of the {@link SwingTask}
     */
    private final JTextArea messageArea;
    
    /**
     * The progress bar showing the current {@link SwingTask#getProgress()
     * progress} of the {@link SwingTask}
     */
    private final JProgressBar progressBar;
    
    /**
     * The panel containing the buttons (e.g. the button for cancelling)
     */
    private final JPanel buttonsPanel;
    
    /**
     * Whether this dialog should be closed when the task was finished
     */
    private final boolean autoCloseWhenTaskFinished;
    
    /**
     * The button for cancelling the task, or <code>null</code> if the
     * task was not cancellable
     */
    private final JButton cancelButton;

    /**
     * The button for closing the dialog, or <code>null</code> when the
     * dialog closes automatically
     */
    private final JButton closeButton;
    
    /**
     * Creates a new task dialog
     * 
     * @param parentWindow The parent window
     * @param parentComponent The parent component. The dialog will be placed
     * relative to this component. If it is <code>null</code>, then the 
     * dialog will be placed in the center of the screen
     * @param title The title of the dialog
     * @param swingTask The {@link SwingTask} that is executed
     * @param modal Whether the dialog should be modal
     * @param cancelable Whether a button for canceling the {@link SwingTask}
     * should be available
     * @param accessory The optional accessory component. If this is not 
     * <code>null</code>, then this component will be shown in the center 
     * of the dialog. 
     * @param autoCloseWhenTaskFinished Whether this dialog should be closed
     * when the task was finished.
     */
    SwingTaskDialog(Window parentWindow, Component parentComponent, 
        String title, final SwingTask<?,?> swingTask, 
        boolean modal, boolean cancelable, 
        JComponent accessory, boolean autoCloseWhenTaskFinished)
    {
        super(parentWindow, title);
        
        this.autoCloseWhenTaskFinished = autoCloseWhenTaskFinished;
        
        if (modal)
        {
            setModalityType(ModalityType.APPLICATION_MODAL);
        }
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        
        getContentPane().setLayout(new GridLayout(1,1));
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        messageArea = new JTextArea(1, 30);
        messageArea.setFont(new Font("Dialog", Font.BOLD, 12));
        messageArea.setEditable(false);
        messageArea.setOpaque(false);
        messageArea.setWrapStyleWord(true);
        messageArea.setLineWrap(true);
        String message = swingTask.getMessage();
        if (message == null || message.trim().length() == 0)
        {
            messageArea.setText(" ");
        }
        else
        {
            messageArea.setText(message);
        }
        centerPanel.add(messageArea, BorderLayout.CENTER);
        if (accessory != null)
        {
            centerPanel.add(accessory, BorderLayout.SOUTH);
        }
        
        JPanel southPanel = new JPanel(new BorderLayout());
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setIndeterminate(true);
        southPanel.add(progressBar, BorderLayout.CENTER);
        
        buttonsPanel = new JPanel(new FlowLayout());
        southPanel.add(buttonsPanel, BorderLayout.SOUTH);
        if (cancelable)
        {
            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    swingTask.cancel(true);
                }
            });
            buttonsPanel.add(cancelButton);
        }
        else
        {
            cancelButton = null;
        }
        
        if (!autoCloseWhenTaskFinished)
        {
            closeButton = new JButton("Close");
            closeButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    setVisible(false);
                    dispose();
                }
            });
            closeButton.setEnabled(false);
            buttonsPanel.add(closeButton);
        }
        else
        {
            closeButton = null;
        }
        
        getContentPane().add(mainPanel);
        pack();
        setLocationRelativeTo(parentComponent);
    }
    
    /**
     * Set the message that should be displayed in the message area.
     * If the message does not fit, then this dialog will be resized
     * accordingly.
     * 
     * @param message The message
     */
    void setMessage(String message)
    {
        messageArea.setText(message);
        Dimension p = getPreferredSize();
        Dimension s = getSize();
        if (p.height > s.height)
        {
            pack();
        }
    }
    
    /**
     * Set the progress that should be displayed in the progress bar.
     * The progress is a value between 0.0 and 1.0 (inclusive). 
     * If the value is negative, then the progress bar will be
     * indeterminate.
     * 
     * @param progress The progress.
     */
    void setProgress(double progress)
    {
        if (progress < 0)
        {
            progressBar.setIndeterminate(true);
            progressBar.setStringPainted(false);
        }
        else
        {
            int percent = (int)(progress * 100);
            percent = Math.max(0, Math.min(100, percent));
            progressBar.setIndeterminate(false);
            progressBar.setValue(percent);
            progressBar.setStringPainted(true);
            progressBar.setString(percent+"%");
        }
    }

    /**
     * Will be called when the task was finished.
     * 
     * @param t The throwable that may have been caused by the task,
     * or <code>null</code>
     */
    void taskFinished(Throwable t)
    {
        if (autoCloseWhenTaskFinished)
        {
            setVisible(false);
            dispose();
        }
        else
        {
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            closeButton.setEnabled(true);
            if (cancelButton != null)
            {
                cancelButton.setEnabled(false);
            }
        }
            
    }
    
    
}