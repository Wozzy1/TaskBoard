package main.java.com.wozzyishungry.taskboard;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

// import javax.swing.JButton;
// import javax.swing.JFrame;
// import javax.swing.JLabel;
// import javax.swing.JTextField;
// import javax.swing.SwingConstants;

import javax.swing.*;
import java.awt.*;

public class TaskBoard {

    private ArrayList<Task> tasks;
    private Task[][] board;
    private int boardLength;

    private ArrayList<TaskBoardAction> stack;
    private int sp;

    private class TaskBoardAction {
        private Task task;
        private JButton cell;
        private boolean prevState;

        public TaskBoardAction(Task task,JButton cell, boolean prevState) {
            this.task = task;
            this.cell = cell;
            this.prevState = prevState;
        }

        public Task getTask() {
            return task;
        }
        
        public void setTask(Task t) {
            this.task = t;
        }

        public JButton getCell() {
            return cell;
        }

        public void setCell(JButton cell) {
            this.cell = cell;
        }

        public boolean getPrevState() {
            return prevState;
        }

        public void setPrevState(boolean prevState) {
            this.prevState = prevState;
        }
    }

    private static final Dimension SMALL_DIMENSION = new Dimension(400, 300);
    private static final Dimension MEDIUM_DIMENSION = new Dimension(800, 600);
    private static final Dimension LARGE_DIMENSION = new Dimension(1000, 800);

    public TaskBoard() {
        tasks = new ArrayList<>();
        stack = new ArrayList<>();
        sp = -1;
    }

    public void run() {

        JFrame frame = new JFrame("Enter Items Frame");
        frame.setSize(SMALL_DIMENSION);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // highest level of layout that gets added to frame and displayed
        CardLayout layout = new CardLayout();
        JPanel cardPanel = new JPanel(layout);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Enter new task:");
        JTextField textField = new JTextField(15);
        JButton button = new JButton("Add");
        button.addActionListener(event -> {
            String input = textField.getText();
            System.out.println(input);

            tasks.add(new Task(input, false));
            textField.setText(""); 
        });
        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(event -> {
            arrayToBoard();
            printTasks();
            printBoard();
            cardPanel.add(createTaskBoardPanel(frame), "TaskBoardPanel");
            frame.setTitle("View Board");

            if (tasks.size() + 1 <= 9) {
                frame.setSize(SMALL_DIMENSION);
            } else if (tasks.size() + 1 <= 25) {
                frame.setSize(MEDIUM_DIMENSION);
            } else {
                frame.setSize(LARGE_DIMENSION);
            }
            layout.show(cardPanel, "TaskBoardPanel");
        });

        // Add to frame
        inputPanel.add(label);
        inputPanel.add(textField);
        inputPanel.add(button);
        inputPanel.add(nextButton);

        cardPanel.add(inputPanel, "InputPanel");
        
        frame.add(cardPanel);
        
        // Display the frame
        frame.setVisible(true);

        Task t = new Task("Wash the dishes", false);
        tasks.add(t);
        t = new Task("Eat three meals a day", true);
        tasks.add(t);
        t = new Task("Sleep a full eight hours", true);
        tasks.add(t);
        t = new Task("Run a marathon", true);
        tasks.add(t);
        t = new Task("Cook pasta with ground beef", true);
        tasks.add(t);
        t = new Task("Swim four laps in the pool", true);
        tasks.add(t);
        t = new Task("Read webtoons before bed", true);
        tasks.add(t);
        // arrayToBoard();

        // board[1][1] = new Task("free", true, 1, 1);
        // printBoard();
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

    private JPanel createTaskBoardPanel(JFrame frame) {
        // Main Panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); // Spacing between components

        // Center: 3x3 Board
        JPanel boardPanel = new JPanel(new GridLayout(boardLength, boardLength, 5, 5)); // Spacing between cells
        System.out.println("BoardLength: " + boardLength);
        for (int i = 0; i < boardLength; i++) {
            for (int j = 0; j < boardLength; j++) {
                if (board[i][j] != null) {
                    Task currTask = board[i][j];
                    JButton cell = new JButton(currTask.getDescription());

                    if (currTask.isComplete()) {
                        cell.setBackground(new Color(152, 251,152));
                    } else {
                        cell.setBackground(new Color(240,128,128));
                    }

                    cell.addActionListener(click -> {
                        if (currTask.getDescription().equals("Free")) {
                            JOptionPane.showMessageDialog(frame, "Bro this spot is free");
                            return;
                        }

                        // make the cell complete 
                        currTask.setComplete(!currTask.isComplete());
                        if (currTask.isComplete()) {
                            cell.setBackground(new Color(152, 251,152));
                        } else {
                            cell.setBackground(new Color(240,128,128));
                        }
                        // record the action on the stack
                        TaskBoardAction action = new TaskBoardAction(currTask, cell, !currTask.isComplete());
                        // forks the history if a new action is taken
                        if (sp+1 < stack.size()) {
                            stack.subList(sp+1, stack.size()).clear();
                        }
                        sp++;
                        stack.add(action);
                        System.out.println("Added: " + action.getTask().getRow() + ", " + action.getTask().getCol() + " " + action.getPrevState());

                    });
                    cell.setToolTipText("<html>" + currTask.getDescription() + "<br>" + (currTask.isComplete() ? "\u2713 completed" : "\u2716 not completed") + "<html>");
                    boardPanel.add(cell);
                } else {
                    JButton cell = new JButton("NULL");
                    boardPanel.add(cell);
                }
            }
        }
        // for (int i = 0; i < boardLength * boardLength; i++) {
        //     JButton cell = new JButton("temp"); // Example text
        //     boardPanel.add(cell);
        // }

        // Right: Buttons Panel
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        String[] buttonLabels = {"Save Board", "Open Board", "New Board", "Randomize board"};
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setAlignmentX(Component.CENTER_ALIGNMENT); // Center-align the buttons
            rightPanel.add(button);
            rightPanel.add(Box.createVerticalStrut(10)); // Add space between buttons
        }

        // Bottom: Undo and Redo Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(click -> {
            if (sp == -1) {
                return;
            }
            
            TaskBoardAction a = stack.get(sp);
            System.out.println("Undid: " + a.getTask().getRow() + ", " + a.getTask().getCol() + " " + a.getPrevState());
            boolean prev = a.getPrevState();
            a.getTask().setComplete(prev);
            if (a.getTask().isComplete()) {
                a.getCell().setBackground(new Color(152, 251,152));
            } else {
                a.getCell().setBackground(new Color(240,128,128));
            }
            sp--;
            
            for (TaskBoardAction ac : stack) {
                System.out.println(ac.getTask().getRow() + ", " + ac.getTask().getCol() + " " + ac.getPrevState());
            }
        });
        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(click -> {
            for (TaskBoardAction ac : stack) {
                System.out.println(ac.getTask().getRow() + ", " + ac.getTask().getCol() + " " + ac.getPrevState());
            }
            System.out.println("--> " + sp);
        });
        bottomPanel.add(undoButton);
        bottomPanel.add(redoButton);

        // Add panels to the main panel
        mainPanel.add(boardPanel, BorderLayout.CENTER); // Board in the center
        mainPanel.add(rightPanel, BorderLayout.EAST); // Buttons on the right
        mainPanel.add(bottomPanel, BorderLayout.SOUTH); // Undo/Redo at the bottom

        return mainPanel;
    }
}
