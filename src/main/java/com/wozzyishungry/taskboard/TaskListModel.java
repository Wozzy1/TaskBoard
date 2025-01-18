package main.java.com.wozzyishungry.taskboard;

import java.util.ArrayList;

import javax.swing.AbstractListModel;

public class TaskListModel extends AbstractListModel {
    private final ArrayList<Task> tasks;

    public TaskListModel(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public int getSize() {
        return tasks.size();
    }

    @Override
    public Task getElementAt(int index) {
        return tasks.get(index);
    }
    
    // notifies listeners when a new task is added
    public void taskAdded(int index) {
        fireIntervalAdded(this, index, index);
    }

    // notifies listeners when a task is removed
    public void taskRemoved(int index) {
        fireIntervalRemoved(this, index, index);
    }

    // notifies listeners when a task is updated
    public void taskUpdated(int index) {
        fireContentsChanged(this, index, index);
    }
}
