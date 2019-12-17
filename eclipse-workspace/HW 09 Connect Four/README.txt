=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 120 Game Project README
PennKey: abpandya
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. Modeling State Using 2D Arrays
  
  	a) I use a 2D array of ints to model the Connect Four board (0 if space is empty,
  	   1 if P1 (red) has a piece there, and 2 if P2 (yellow) has a piece there).
  	   
  	b) This is an appropriate use of the concept because the game regularly parses
  	   through the array to check if there is a winner, and to place pieces. When a 
  	   piece is added, the board changes its appropriate entry.

  2. Modeling State Using Collections
  
  	a) I implement a LinkedList<Moves> to keep track of the order of the moves. It is
  	   simple to "undo" a move when one can simply pop off the last entry in the
  	   linked list, remove the appropriate entry from the board, and finally switch
  	   the player's turn.
  	   
  	b) This is an appropriate use because a LinkedList is designed to make adding
  	   elements easy, and popping off the last element easy as well. Undo-ing moves
  	   lends itself naturally to a queue, and the LinkedList is a doubly-ended queue!

  3. JUnit Testing
  	
  	a) JUnit Testing was appropriate, because there were various methods that can be
  	   tested without using the GUI. For example, checking if there is a winner and 
  	   checking if a piece was added to the appropriate spot on the board works well
  	   JUnit Testing because one simply has to verify that the board has 1 more piece,
  	   and assert that it is in the right spot.

  4. File IO
  
  	a) I use File IO to save and load the game state. 
  	
  	b) This is appropriate because the board can easily be read into a text file, and
  	   read back to load the saved state.

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.
  
  I have the provided Game and GameCourt classes, which provide the bulk of the logic.
  I created a Move class to easily store moves in a LinkedList<Move> collection. This
  makes it easy to undo moves. Finally, I have a FileIterator class that implements
  the Iterator<String> interface, and lets me read text files easily.


- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?
  
  I had messed up the implementation of the board. I did not realize I had swapped
  the rows and the columns. I initially used x and y whenever I called accessed
  an entry by writing board[x][y], but I had mixed up the constants for the 
  number of rows (BOARD_ROWS) and the number of columns (BOARD_COLS). I had to fix
  all of my code halfway through the project to fix the error. 


- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?
  
  There is a good separation of functionality in my code. Private state is not
  encapsulated very well, because I had to make many additional public methods in
  the GameCourt class to test it appropriately. For example, the nextTurn method
  lets me skip a player's turn (which made it easy to test), but breaks the 
  invariants of the game. In production, these methods could be removed, and 
  private state would remain encapsulated.



========================
=: External Resources :=
========================

- Cite any external resources (libraries, images, tutorials, etc.) that you may
  have used while implementing your game.
  
  https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html
  https://docs.oracle.com/javase/tutorial/uiswing/components/icon.html
  https://me.me/i/with-connect-jesus-the-classic-relationship-building-with-the-king
  	-c0f32785a9d543bf8dc7352070548b5c
  https://dragonball.fandom.com/wiki/Flying_Nimbus  
  
  
