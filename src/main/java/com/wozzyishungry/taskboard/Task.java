package main.java.com.wozzyishungry.taskboard;

public class Task {
    private String description;
    private boolean isComplete;
    private int row;
    private int col;

    // Default constructor
    public Task() {
    }

    // All-arguments constructor
    public Task(String description, boolean isComplete, int row, int col) {
        this.description = description;
        this.isComplete = isComplete;
        this.row = row;
        this.col = col;
    }

    public Task(String description, boolean isComplete) {
        this.description = description;
        this.isComplete = isComplete;
        this.row = -1;
        this.col = -1;
    }

    // Getters and Setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    @Override
    public String toString() {
        return "Task \'" + description + "\' located at row " + row + ", col " + col + (isComplete ? " is complete" : " is not complete");
    }
}
