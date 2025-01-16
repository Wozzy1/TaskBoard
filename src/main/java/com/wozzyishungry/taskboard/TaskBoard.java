package main.java.com.wozzyishungry.taskboard;

import java.util.ArrayList;
import java.util.Random;

public class TaskBoard {

    private ArrayList<Task> tasks;
    private Task[][] board;
    private int boardLength;

    public TaskBoard() {
        tasks = new ArrayList<>();
    }

    public void run() {
        Task t = new Task();
        t.setDescription("Wash");
        t.setComplete(false);
        t.setCol(0);
        t.setRow(0);
        tasks.add(t);

        t = new Task("Eat", true, 0, 0);
        tasks.add(t);
        t = new Task("Sleep", true, 0, 0);
        tasks.add(t);
        arrayToBoard();

        // board[1][1] = new Task("free", true, 1, 1);
        printBoard();
    }

    private void printTasks() {
        for (Task task : tasks) {
            System.out.println(task);
        }
    }

    private void arrayToBoard() {
        // finds the next perfect square greater than tasks.size() + 1 
        // +1 to account for free middle space
        boardLength = (int)Math.floor(Math.sqrt(tasks.size() + 1)) + 1;
        
        // forces board to be odd by odd
        if (boardLength % 2 == 0) {
            boardLength++;
        }
        board = new Task[boardLength][boardLength];

        Random rand = new Random(1);
        int maxValue = boardLength * boardLength;
        boolean[] usedPositions = new boolean[maxValue];

        board[boardLength/2][boardLength/2] = new Task("Free", true, boardLength/2, boardLength/2);
        usedPositions[boardLength/2 * boardLength + boardLength/2] = true;
        
        for (int i = 0; i < tasks.size(); i++) {
            int r, c;

            // Find an unused random position
            do {
                int nextValue = rand.nextInt(maxValue);
                r = nextValue / boardLength;
                c = nextValue % boardLength;
            } while (usedPositions[r * boardLength + c]);

            // Mark position as used
            usedPositions[r * boardLength + c] = true;
            Task t = tasks.get(i);
            t.setRow(r);
            t.setCol(c);
            board[r][c] = t;
        }
    }

    private void printBoard() {
        for (int i = 0; i < boardLength; i++) {
            for (int j = 0; j < boardLength; j++) {
                if (board[i][j] == null) {
                    System.out.print("null ");
                } else {
                    // TODO update this to print something related to the task
                    // System.out.print("Test ");
                    System.out.print(board[i][j].getDescription() + " ");
                }
            }
            System.out.println();
        }
    }

}
