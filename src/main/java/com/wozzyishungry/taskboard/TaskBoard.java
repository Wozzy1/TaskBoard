package main.java.com.wozzyishungry.taskboard;

import java.util.ArrayList;

public class TaskBoard {

    private ArrayList<Task> tasks;

    public TaskBoard() {
        tasks = new ArrayList<>();
    }

    public void run() {
        Task t = new Task();
        t.setDescription("Wash the dishes");
        t.setComplete(false);
        t.setCol(0);
        t.setRow(0);
        tasks.add(t);

        t = new Task("Eat dinner", true, 0, 1);
        tasks.add(t);

        for (Task task : tasks) {
            System.out.println(task);
        }
    }

}
