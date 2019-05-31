# Eight Puzzle

Solves an 8 puzzle using the a star algorithm.
Input a start state and end state in the form: 1 2 3 4 5 6 7 8 0
This would correspond with this puzzle:
1 2 3
4 5 6
7 8 0

I am going to expand the program to be able to solve a 15 puzzle.
Some aspects are hardcoded to the 8 puzzle.

The runtime is very slow if the puzzle is not solvable as 20 or 30 objects are made in every iteration of the loop and the open list gets very large.
My project group agreed to use a heavily object oriented solution.
Because of this I'm not sure the 15 puzzle will even be possible using this solution.