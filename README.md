# TaskBoard
I need a way to stay organized with myself

Objective:
Develop a simple application for someone to brain dump a list of tasks they need to accomplish. The tasks will then be shuffled into a bingo board style of N x N size where N is the ceiling of # of items then squared.

Process:
1. Take string inputs from user
2. Append items to a list
3. Repeat until generate board is entered
4. Compute ceiling(numItems + 1) and make a 2D array of items
5. Randomly place numItems into the 2D array, filling empty spots with a FREE (or maybe a self care item from a predetermined list?)
6. Display, save (as a local file), use


Tech stack used: <br>
Java Swing for GUI

notes:
dont forget about the center square <br>
even N x N boards wont have a center space

## Things to implement:
* Need to work on bingo finding algorithm
* Wanna create the dynamic list view to see all the tasks at once before creating the board, also display # of tasks
* Back button from board to creating board. Should save the current list items so you can modify the list before saving it as final.
* New Board Button. Takes you back to the creating board view. 
* Randomize Board -> Shuffle board button.
* A mechanism to check if you have saved or not before leaving the page.
* Make the naming process more UX friendly by having board name on the UI instead of in File System. Take the user inputted board name and populate the FS popup with it (if possible).
* Have a live count showing how many items are in the list when creating the list.
* Allow modification to a list from a saved board. Ex: open this week's list; should be able to modify list items and save again.