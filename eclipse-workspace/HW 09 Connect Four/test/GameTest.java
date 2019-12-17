import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.swing.*;

import org.junit.Test;

/** 
 *  You can use this file (and others) to test your
 *  implementation.
 */

public class GameTest {
	
	public static final int NUMBER_OF_SPOTS = GameCourt.BOARD_ROWS * GameCourt.BOARD_COLS;

    @Test (expected = IllegalArgumentException.class)
    public void testMoveOutOfBounds() {
    	Move m = new Move(-1,2,true);
    	Move m1 = new Move(1,-2,true);
    	Move m2 = new Move(GameCourt.BOARD_ROWS,0,true);
    	Move m3 = new Move(0,GameCourt.BOARD_COLS,true);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testStatusIsNull() {
    	GameCourt game = new GameCourt(null, new JLabel());
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testWinsIsNull() {
    	GameCourt game1 = new GameCourt(new JLabel(), null);
    }
    
    @Test 
    public void testAddPieceToColumn() {
    	// TODO: test for out of bounds inputs, should set status to you clicked outside the board!
    	// make getStatusTextMethod
    	GameCourt game = new GameCourt(new JLabel(), new JLabel());
    	game.reset();
    	game.addPieceToColumn(0);
    	assertEquals(game.getBoardPiece(GameCourt.BOARD_ROWS-1, 0),1);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testAddPieceToColumnOutOfBounds() {
    	GameCourt game = new GameCourt(new JLabel(), new JLabel());
    	game.reset();
    	game.addPieceToColumn(-1);
    	game.addPieceToColumn(7);
    }
    
    @Test
    public void testVerticalCheckIfWinner() {
    	GameCourt game = new GameCourt(new JLabel(), new JLabel());
    	game.reset();
    	for (int i = 0; i < 4; i++) {
    		game.addPieceToColumn(0);
    		game.nextTurn(); // skip P2's turn
    	}
    	
    	assertTrue(game.checkIfWinner(false));
    }
    
    @Test
    public void testHorizontalCheckIfWinner() {
    	GameCourt game = new GameCourt(new JLabel(), new JLabel());
    	game.reset();
    	for (int i = 0; i < 4; i++) {
    		game.addPieceToColumn(i);
    		game.nextTurn(); // skip P2's turn
    	}
    	//printBoard(game);
    	assertTrue(game.checkIfWinner(false));
    }
    
    @Test
    public void testAscendingDiagonalCheckIfWinner() {
    	GameCourt game = new GameCourt(new JLabel(), new JLabel());
    	game.reset();
    	for (int i = 0; i < 4; i++) {
    		for (int j = 0; j <= i; j++) {
    			game.addPieceToColumn(i);
        		game.nextTurn(); // skip P2's turn
    		}
    	}
    	//printBoard(game);
    	assertTrue(game.checkIfWinner(false));
    }
    
    @Test
    public void testDescendingDiagonalCheckIfWinner() {
    	GameCourt game = new GameCourt(new JLabel(), new JLabel());
    	game.reset();
    	for (int i = 0; i < 4; i++) {
    		for (int j = i; j < 4; j++) {
    			game.addPieceToColumn(i);
        		game.nextTurn(); // skip P2's turn
    		}
    	}
    	//printBoard(game);
    	assertTrue(game.checkIfWinner(false));
    }
    
    @Test
    public void testTie() {
    	GameCourt game = new GameCourt(new JLabel(), new JLabel());
    	game.reset();
    	for (int i = 0; i < 7; i++) {
    		for (int j = 0; j < 6; j++) {
    			game.addPieceToColumn(i);
        		game.nextTurn(); // skip P2's turn
    		}
    	}
    	game.checkIfWinner(false);
    	
    	assertEquals("It's a tie! Click reset to start a new game!", game.getStatus());
    }
    
    @Test
    public void addPieceToColumnWhenColumnIsFull() {
    	GameCourt game = new GameCourt(new JLabel(), new JLabel());
    	game.reset();
    	// fill board one more than height
    	for (int i = 0; i <= GameCourt.BOARD_ROWS; i++) {
    		game.addPieceToColumn(0);
    	}
    	assertEquals("This column is full ._.",game.getStatus());
    	int empty_pieces = countEmptyPieces(game);
    	int player_pieces = NUMBER_OF_SPOTS - empty_pieces;
    	// board should not have any extra pieces
    	assertTrue(player_pieces == GameCourt.BOARD_ROWS);
    }
    
    @Test
    public void testReset() {
    	GameCourt game = new GameCourt(new JLabel(), new JLabel());
    	game.reset();
    	for (int i = 0; i <= GameCourt.BOARD_ROWS; i++) {
    		game.addPieceToColumn(0);
    	}
    	game.reset();
    	assertTrue(countEmptyPieces(game) == NUMBER_OF_SPOTS);
    }
    
    @Test
    public void testUndo() {
    	GameCourt game = new GameCourt(new JLabel(), new JLabel());
    	game.reset();
    	game.addPieceToColumn(0);
    	game.addPieceToColumn(0);
    	game.undo();
    	assertTrue(countEmptyPieces(game) == NUMBER_OF_SPOTS-1);
    }
    
    @Test 
    public void testUndoWhenNoMovesHaveBeenMade() {
    	GameCourt game = new GameCourt(new JLabel(), new JLabel());
    	game.reset();
    	game.undo();
    	assertTrue(countEmptyPieces(game) == NUMBER_OF_SPOTS);
    }
    
    @Test 
    public void testUndoTooManyTimes() {
    	GameCourt game = new GameCourt(new JLabel(), new JLabel());
    	game.reset();
    	game.addPieceToColumn(0);
    	game.addPieceToColumn(0);
    	game.undo();
    	game.undo();
    	game.undo();
    	assertTrue(countEmptyPieces(game) == NUMBER_OF_SPOTS);
    	assertEquals("No more moves to undo!",game.getStatus());
    }
    
    //test FileIterator class...
    
    @Test (expected = IllegalArgumentException.class)
    public void testFileIteratorInvalidInputs() {
    	FileIterator it = new FileIterator(null);
    	FileIterator it1 = new FileIterator("files/yikes.txt");
    }
    
    @Test
    public void testFileIteratorOnReadMe() {
    	FileIterator it = new FileIterator("README.txt");
    	while (it.hasNext()) {
    		assertTrue(it.next() != null);
    	}
    }
    
    @Test 
    public void testHasNextandNext() {
    	FileIterator it = new FileIterator("README.txt");
    	assertTrue(it.hasNext());
    	assertTrue(it.next() != null);
    	assertTrue(it.hasNext());
    }
    
    @Test (expected = NoSuchElementException.class)
	public void testFileIteratorNoSuchElementException() {
		FileIterator it = new FileIterator("README.txt");
		while (it.hasNext()) {
			it.next();
		}
		it.next();
	}
    
    @Test 
	public void testEmptyFile() {
		FileIterator li = new FileIterator("empty.txt");
		assertFalse(li.hasNext());
	}
    
    @Test
    public void testFileSaveAndFileLoad() {
    	GameCourt court = new GameCourt(new JLabel(), new JLabel());
    	court.addPieceToColumn(0);
    	court.addPieceToColumn(0);
    	String filepath_board = "test_save_board.txt";
    	String filepath_moves = "test_save_moves.txt";
    	court.saveGame(filepath_board,filepath_moves);
    	int[][] old_board = court.getBoard();
    	LinkedList<Move> old_moves = court.getMoves();
    
    	court.reset();
    	court.loadGame(filepath_board,filepath_moves);
    	int[][] new_board = court.getBoard();
    	LinkedList<Move> new_moves = court.getMoves();
    	for (int row = 0; row < GameCourt.BOARD_ROWS; row++) {
    		for (int col = 0; col < GameCourt.BOARD_COLS; col++) {
    			assertTrue(old_board[row][col] == new_board[row][col]);
    		}
    	}
    	
    	Iterator<Move> it_new = new_moves.iterator();
    	Iterator<Move> it_old = old_moves.iterator();
    	
    	while (it_old.hasNext()) {
    		Move new_move = it_new.next();
    		Move old_move = it_old.next();
    		
    		assertTrue(new_move.getRow() == old_move.getRow());
    		assertTrue(new_move.getCol() == old_move.getCol());
    		assertTrue(new_move.getPlayer() == old_move.getPlayer());
    	}
    	
    }
    
    @Test
    public void testWins() {
    	GameCourt court = new GameCourt(new JLabel(), new JLabel());
    	assertEquals(0, court.getP1Wins());
    	for (int i = 0; i < 4; i++) {
    		court.addPieceToColumn(0);
    		court.nextTurn();
    	}
    	
    	assertTrue(court.checkIfWinner(false));
    	assertEquals(1, court.getP1Wins());
    	// resetting the game should not change the number of wins!
    	court.reset();
    	assertEquals(1, court.getP1Wins());
    }
    
    // Helper methods below.
    
    private int countEmptyPieces(GameCourt game) {
    	int counter = 0;
    	for (int row = 0; row < GameCourt.BOARD_ROWS; row++ ) {
    		for (int col = 0; col < GameCourt.BOARD_COLS; col++) {
    			if (game.getBoardPiece(row, col) == 0) {
    				counter++;
    			}
    		}
    	}
    	return counter;
    		
    }
    
    private void printBoard(GameCourt game) {
    	for (int row = 0; row < GameCourt.BOARD_ROWS; row++ ) {
    		for (int col = 0; col < GameCourt.BOARD_COLS; col++) {
    			System.out.print(game.getBoardPiece(row, col) + " ");
    		}
    		System.out.println();
    	}
    }

}
