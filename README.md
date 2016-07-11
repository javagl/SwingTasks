# SwingTasks

Utility classes for task execution in Swing

Maven coordinates:
```
<dependency>
  <groupId>de.javagl</groupId>
  <artifactId>swing-tasks</artifactId>
  <version>0.0.3</version>
</dependency>
```

The SwingTasks package contains utility classes for different kinds of 
programming tasks related to threads and Swing:

0. Executing a long-running computation on a background thread, while showing a dialog
0. Executing a task repeatedly, and start/pause/stop the repeated execution
0. Executing multiple tasks in parallel, using an executor service, and showing 
   the progress of the task execution in the GUI


Long-running tasks
------------------

The [javax.swing.SwingWorker](https://docs.oracle.com/javase/8/docs/api/javax/swing/SwingWorker.html) 
class offers an infrastructure for the execution of long-running tasks on a background thread in 
Swing. The [javax.swing.ProgressMonitor](https://docs.oracle.com/javase/8/docs/api/javax/swing/ProgressMonitor.html)
class offers methods for monitoring the progress of such a long-running task, and show a dialog
during the execution. The tutorial about 
[How to Use Progress Bars](https://docs.oracle.com/javase/tutorial/uiswing/components/progress.html)
explains the interplay of both classes.

However, these classes can be a bit inconvenient to use, because they either lack configurability,
or don't offer sensible default settings for frequent application patterns.

This is where the SwingTasks package may help. A `SwingTask` is like a `SwingWorker` on steroids,
offering additional functionality for tracking and reporting the progress of a background
computation. The `SwingTaskExecutor` class can be used to execute a `SwingTask`, and will take
care of the handling of dialogs and progress bars.

Creating and executing a `SwingTask` is simple: The `SwingTask` offers an abstract `doInBackground` 
method, just like a `SwingWorker`, that can be implemented to perform the long-running computation.
The `SwingTaskExecutor` can receive the `SwingTask` and execute it:

```java
SwingTask<Integer, ?> swingTask = new SwingTask<Integer, Void>()
{
    @Override
    protected Integer doInBackground() throws Exception
    {
        Thread.sleep(3000);
        return 42;
    }
};
SwingTaskExecutors.create(swingTask).build().execute();
```  

By default, the the `SwingTaskExecutor` will behave as follows:

* It will block the calling thread for 300 milliseconds
* It will predict the completion time of the task
* After 1 second, it will show a (modal) dialog that shows an indeterminate progress bar
* When the task is finished, it will hide the dialog.

Note one detail of this behavior: **It will block the calling thread** for 300 ms. 

Yes, this also applies to the *Event Dispatch Thread*, and this is intentional: When
a button is clicked to start a potentially long-running computation, and the computation 
should be "blocking" (meaning that a modal dialog should be shown), then the user should 
not have the possibility to click any other button until it is clear whether the 
computation will indeed take long or not, and whether the dialog will be shown or not.

Of course, as soon as the dialog is shown, the *Event Dispatch Thread* will no longer
be blocked directly, but only via the modal dialog.

By default, it will not be possible to *cancel* the given task. But this is one of
the many configuration possibilities that the `SwingTaskExecutor` has in contrast
to the `ProgressMonitor`: In order to show, for example, a non-modal dialog after
500 milliseconds, with a button to cancel the task, the `SwingTaskExecutor` may
be configured fluently:

```java
SwingTaskExecutor<Integer> h = 
    SwingTaskExecutors.create(swingTask).
        setModal(false).
        setCancelable(true).
        setMillisToPopup(500).
        build().execute();
```

Additionally, the `SwingTaskExecutors` class offers utility methods that allow wrapping
arbitrary `Supplier` instances into `SwingTask` instances. So the easiest way to 
execute a long-running method in a background thread in Swing and showing a modal
dialog in the meantime is using a method reference:

```java
SwingTaskExecutors.create(SomeClass::someMethod).build().execute()
```

The 
[Samples](https://github.com/javagl/SwingTasks/tree/master/src/test/java/de/javagl/swing/tasks/samples)
contain more examples of how to use the SwingTasks, how to report progress, and how to handle
errors that occur in the background tasks.  

Repeated task execution
-----------------------

A common programming task is that a certain operation should be executed repeatedly,
in an own thread, controlled via buttons to start, pause or stop the repeated execution.

The SwingTasks package contains a `TaskRunner` that offers exactly this functionality:
It may receive an implementation of a `Task`, and manages the lifecycle of the 
execution of this task:

* It may start the task
* It may repeatedly execute the `run()` method of the task
* It may pause the task
* It may stop the task

The `TaskRunnerController` class offers actions for these functionalities. These actions
may be attached to arbitrary Swing components, or integrated into an existing Swing GUI
using the `TaskRunnerControlPanel`, which contains buttons that execute these actions.


Multiple tasks in an executor service
------------------

The [javax.util.concurrent.ExecutorService](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html) class offers an infrastructure for simple, parallel execution of multiple task. The tasks may be 
submitted to the executor service as `Runnable` or `Callable<?>` instances. The executor
service will return a `Future<?>` that describes the result of the asynchronous computation.

Beyond that, an executor service is mainly a "black box". It is not easily possible to
observe the execution of the tasks, or to obtain progress information about them (e.g.
to see how many tasks are currently running, or caused an exception). 

The classes in the `de.javagl.swing.tasks.executors` package aim at alleviating 
this problem. The `ObservableExecutors` class allows creating instances of 
an `ObservableExecutorService`. This executor service allows registering an
`ExecutorObserver` that is informed about the tasks that are scheduled and executed.

The `ObservableExecutorPanel` is a panel that can show an `ObservableExecutorService`,
and displays information about the running tasks, including their status and - if the tasks 
implement the `ProgressTask` interface - progress information:

![SwingTasksExecutors01.png](/screenshots/SwingTasksExecutors01.png)   




Change log:
------------------

20??-??-?? : Version 0.0.4-SNAPSHOT

2016-07-11 : Version 0.0.3
* Cleaner handling of thread interrupted status
* Avoid repeated wrapping of ObservableTask

2016-06-27 : Version 0.0.2
* Make `SwingTaskView` configurable
* Added executors package 

2015-06-11 : Version 0.0.1
* Initial release

