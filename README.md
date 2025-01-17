# TaskBoard
I need a way to stay organized with myself

Objective:
Develop a simple application for someone to brain dump a list of tasks they need to accomplish. The tasks will then be shuffled into a bingo board style of N x N size where N is the ceiling of # of items then squared.

What is needed: -> refer to excalidraw notes
1. Take string inputs from user
2. Append items to a list
3. Repeat until generate board is entered
4. Compute ceiling(numItems + 1) and make a 2D array of items
5. Randomly place numItems into the 2D array, filling empty spots with a FREE (or maybe a self care item from a predetermined list?)
6. Display, save (as a local file)


What does the tech stack require?
UI component -> could use:
* react
* android
* java swing (feels most promising)
* python tkinter

notes:
dont forget about the center square <br>
even N x N boards wont have a center space

Need to work on bingo finding algorithm
Wanna create the dynamic list view to see all the tasks at once before creating the board, also display # of tasks