package main.java.com.wozzyishungry.taskboard;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TaskBoard {

    private ArrayList<Task> tasks;
    private Task[][] board;
    private int boardLength;
    private File workingFile;

    private CardLayout layout;
    private JPanel cardPanel;
    private JFrame frame;

    /** 
     * ArrayList based "stack" for storing the history
     */
    private ArrayList<TaskBoardAction> stack;
    /**
     * Stack pointer variable for keeping track of where the user is in the history stack
     */
    private int sp;

    private static final Dimension SMALL_DIMENSION = new Dimension(400, 300);
    private static final Dimension MEDIUM_DIMENSION = new Dimension(800, 600);
    private static final Dimension LARGE_DIMENSION = new Dimension(1000, 800);
    private static final String DELIMITER = " * ";

    public TaskBoard() {
        tasks = new ArrayList<>();
        stack = new ArrayList<>();
        workingFile = null;
        sp = -1;
    }

    public void run() {
        frame = new JFrame("Enter Items Frame");
        frame.setSize(SMALL_DIMENSION);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // highest level of layout that gets added to frame and displayed
        layout = new CardLayout();
        cardPanel = new JPanel(layout);

        JPanel inputPanel = createInputComponents();

        cardPanel.add(inputPanel, "InputPanel");
        
        frame.add(cardPanel);
        
        // Display the frame
        frame.setVisible(true);
    }

    private JPanel createInputComponents() {
        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Enter new task:");

        TaskListModel listModel = new TaskListModel(tasks);
        JList<Task> jList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(jList);
        scrollPane.setSize(new Dimension(150, 200));
        jList.setCellRenderer(new ListCellRenderer<Task>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Task> list, Task task, int index, boolean isSelected, boolean cellHasFocus) {
                JPanel panel = new JPanel(new BorderLayout(5, 5));
                panel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                
                // label for task description
                JLabel label = new JLabel("\u2022 " +task.getDescription());
                panel.add(label, BorderLayout.CENTER);
        
                // highlight the panel if selected
                if (isSelected) {
                    panel.setBackground(list.getSelectionBackground());
                    label.setForeground(list.getSelectionForeground());
                } else {
                    panel.setBackground(list.getBackground());
                    label.setForeground(list.getForeground());
                }
        
                return panel;
            }
        });
        JTextField textField = new JTextField(15);
        JButton button = new JButton("Add");
        ActionListener actionListener = event -> {
            String input = textField.getText();

            if (input.length() == 0) {
                JOptionPane.showMessageDialog(frame, "Your task description can't be empty, sorry!");
                return;
            }
            if (input.contains("*")) {
                JOptionPane.showMessageDialog(frame, "Your task description can't contain an asterisk, sorry!");
                return;
            }
            
            tasks.add(new Task(input, false));
            listModel.taskAdded(tasks.size() - 1);
            textField.setText("");
            textField.requestFocus(); 
        };
        button.addActionListener(actionListener);
        textField.addActionListener(actionListener);

        JButton removeButton = new JButton("Remove Selected");
        removeButton.addActionListener(click -> {
            int selectedIndex = jList.getSelectedIndex();
            
            // ensures an item is selected
            if (selectedIndex != -1) { 
                tasks.remove(selectedIndex);
                listModel.taskRemoved(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(frame, "No task selected to remove!");
            }
        });
        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(event -> {
            arrayToBoard(); // for logging

            // handles logic of shuffling board and displays it
            createAndDisplayBoardPanel(frame, layout, cardPanel);
        });

        // Add to frame
        inputPanel.add(label);
        inputPanel.add(textField);
        inputPanel.add(button);
        inputPanel.add(nextButton);
        inputPanel.add(scrollPane);
        inputPanel.add(removeButton);
        return inputPanel;
    }

    private void createAndDisplayBoardPanel(JFrame frame, CardLayout layout, JPanel cardPanel) {
        cardPanel.add(createTaskBoardPanel(frame), "TaskBoardPanel");
        frame.setTitle("View Board");

        if (boardLength <= 3) {
            frame.setSize(SMALL_DIMENSION);
        } else if (boardLength <= 5) {
            frame.setSize(MEDIUM_DIMENSION);
        } else {
            frame.setSize(LARGE_DIMENSION);
        }
        layout.show(cardPanel, "TaskBoardPanel");
    }

    /**
     * Print all tasks in {@link #tasks} for debugging.
     */
    private void printTasks() {
        for (Task task : tasks) {
            // System.out.println("Task \'" + task.getDescription() + 
            // "\' located at row " + task.getRow() + 
            // ", col " + task.getCol() + 
            // (task.isComplete() ? " is complete" : " is not complete"));
            System.out.println(task);
        }
    }

    /**
     * Takes {@link #tasks} and updates the {@link #board} with the shuffled list
     */
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

    /**
     * Prints the {@link #board} using the task's description (for the time being) for use debugging.
     */
    private void printBoard() {
        for (int i = 0; i < boardLength; i++) {
            for (int j = 0; j < boardLength; j++) {
                if (board[i][j] == null) {
                    System.out.print("null ");
                } else {
                    // TODO update this to print something shorted and related to the task
                    // System.out.print("Test ");
                    System.out.print(board[i][j].getDescription() + " ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Creates the "View Board" Card that holds the NxN task board,
     * buttons to save or randomize the board, 
     * and buttons to create or open a different board.
     * @param frame
     *          the main frame
     * @return the JPanel with all the components attached to it
     */
    private JPanel createTaskBoardPanel(JFrame frame) {
        // Main Panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); // Spacing between components

        // Center: NxN board
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
                        // System.out.println("Added: " + action.getTask().getRow() + ", " + action.getTask().getCol() + " " + action.getStoredState());

                    });
                    cell.setToolTipText("<html>" + currTask.getDescription() + "<br>" + (currTask.isComplete() ? "\u2713 completed" : "\u2716 not completed") + "<html>");
                    boardPanel.add(cell);
                } else {
                    JButton cell = new JButton("NULL");
                    boardPanel.add(cell);
                }
            }
        }

        // Right: Buttons Panel
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Save Board Button
        JButton saveButton = new JButton("Save Board");
        saveButton.addActionListener(click -> {
            saveBoardToFile();
        });
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(saveButton);
        rightPanel.add(Box.createVerticalStrut(10));

        // Open Board Button
        JButton openButton = new JButton("Open Board");
        openButton.addActionListener(click -> {
            JFileChooser fileChooser = new JFileChooser(new File("."));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileNameExtensionFilter("TaskBoard Text Files", "txt"));
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                // Get the selected file
                File selectedFile = fileChooser.getSelectedFile();
    
                // Do something with the file
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                ArrayList<Task> newTasks = parseFile(selectedFile.getAbsolutePath());

                this.tasks = newTasks;
                printTasks();
                updateBoardFromList();
                createAndDisplayBoardPanel(frame, layout, cardPanel);
                System.out.println(tasks.size() + ", " + boardLength);
                printBoard();
            }
        });
        openButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(openButton);
        rightPanel.add(Box.createVerticalStrut(10));

        // New Board Button
        JButton newButton = new JButton("New Board");
        newButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(newButton);
        rightPanel.add(Box.createVerticalStrut(10));

        // Randomize Board Button
        JButton randomizeButton = new JButton("Randomize board");
        randomizeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(randomizeButton);
        rightPanel.add(Box.createVerticalStrut(10));


        // Bottom: Undo and Redo Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(click -> {
            if (sp == -1) {
                return;
            }

            TaskBoardAction a = stack.get(sp);
            // System.out.println("Undid: " + a.getTask().getRow() + ", " + a.getTask().getCol() + " " + a.getStoredState());
            boolean prev = a.getStoredState();
            a.getTask().setComplete(prev);
            if (a.getTask().isComplete()) {
                a.getCell().setBackground(new Color(152, 251,152));
            } else {
                a.getCell().setBackground(new Color(240,128,128));
            }
            sp--;
            
        });

        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(click -> {
            if (sp + 1 >= stack.size()) {
                return;
            }
            sp++;
            System.out.println("--> " + sp);
            TaskBoardAction a = stack.get(sp);
            boolean next = a.getStoredState();
            // System.out.println("Redid: " + a.getTask().getRow() + ", " + a.getTask().getCol() + " " + !a.getStoredState());
            a.getTask().setComplete(!next);
            if (a.getTask().isComplete()) {
                a.getCell().setBackground(new Color(152, 251,152));
            } else {
                a.getCell().setBackground(new Color(240,128,128));
            }
        });
        bottomPanel.add(undoButton);
        bottomPanel.add(redoButton);

        // Add panels to the main panel
        mainPanel.add(boardPanel, BorderLayout.CENTER); // Board in the center
        mainPanel.add(rightPanel, BorderLayout.EAST); // Buttons on the right
        mainPanel.add(bottomPanel, BorderLayout.SOUTH); // Undo/Redo at the bottom

        return mainPanel;
    }

    /**
     * Writes the current board to a file with the format: <br>
     * description * isComplete * row * col
     * @return boolean of succesful save or not
     */
    private boolean saveBoardToFile() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < boardLength; i++) {
            for (int j = 0; j < boardLength; j++) {
                Task t = board[i][j];
                if (t != null) {
                    sb.append(
                        t.getDescription())
                        .append(DELIMITER)
                        .append(t.isComplete())
                        .append(DELIMITER)
                        .append(t.getRow())
                        .append(DELIMITER)
                        .append(t.getCol())
                        .append('\n');
                } else {
                    sb.append(
                        "None")
                        .append(DELIMITER)
                        .append(false)
                        .append(DELIMITER)
                        .append(-1)
                        .append(DELIMITER)
                        .append(-1)
                        .append('\n');
                }
            }
        }

        if (workingFile == null) {
            String newFilename = (String)JOptionPane.showInputDialog(null, "Name of file to save:");
            if (newFilename != null && newFilename.length() > 0) {
                try {
                    File file = new File("./" + newFilename);
                    FileWriter fw = new FileWriter(file);
                    fw.write(sb.toString());
                    fw.close();
                    JOptionPane.showMessageDialog(null, "Succesfully saved to disk!");
                    System.out.println("Succesfully saved to " + file.getCanonicalPath());
                    workingFile = file;
                    return true;
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    return false;
                }
            }
        } else {
            // if there is a working file already do something different
            // maybe create different buttons like save vs save as 
        }
        
        return false;
    }

    /**
     * Reads the file and recreates the task board from what was previously saved.
     * @param filepath
     *          absolute file path to the saved task board
     * @return
     *          the tasks to overwite {@link #tasks} and populate the task board
     */
    private ArrayList<Task> parseFile(String filepath) {
        ArrayList<Task> newTasks = new ArrayList<>();
    
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" \\* ");
                if (parts[0].equalsIgnoreCase("None") || parts[0].equalsIgnoreCase("NULL")) {
                    newTasks.add(null);
                    continue;
                }
                Task t = new Task(parts[0].strip(), Boolean.valueOf(parts[1].strip()), Integer.valueOf(parts[2].strip()), Integer.valueOf(parts[3].strip()));
                newTasks.add(t);
            }
            // System.out.println(newTasks);
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in the file: " + e.getMessage());
        }
    
        return newTasks;
    }

    /**
     * Updates {@link #board} and {@link #boardLength} with new tasks returned from {@link #parseFile(String)}
     */
    private void updateBoardFromList() {
        boardLength = (int)Math.sqrt(tasks.size());
        Task[][] newBoard = new Task[boardLength][boardLength];
        for (int i = 0; i < tasks.size(); i++) {
            newBoard[i / boardLength][i % boardLength] = tasks.get(i);
        }
        board = newBoard;
    }
}
