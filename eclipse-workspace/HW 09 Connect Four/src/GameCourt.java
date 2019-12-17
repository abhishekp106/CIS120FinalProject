/**
 * CIS 120 Game HW
 * (c) University of Pennsylvania
 * @version 2.1, Apr 2017
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.LinkedList;


import javax.swing.*;

/**
 * GameCourt
 * 
 * This class holds the primary game logic for how different objects interact with one another. Take
 * time to understand how the timer interacts with the different methods and how it repaints the GUI
 * on every tick().
 */
@SuppressWarnings("serial")
public class GameCourt extends JPanel {

    // the state of the game logic
    private Square square; // the Black Square, keyboard control
    private Circle snitch; // the Golden Snitch, bounces
    private Poison poison; // the Poison Mushroom, doesn't move
    
    // Create the 2D-array to store the game state
    // Invariants: 
    // if entry is...
    // 		0 -> No piece is placed here.
    // 		1 -> Player 1 (Red) has a piece here.
    // 		2 -> Player 1 (Red) has a piece here.
    // any piece in the board must be on the lowest possible height
    private int[][] board;

    public boolean playing = false; // whether the game is running 
    private JLabel status; // Current status text, i.e. "Running..."
    private JLabel wins;  // Displays number of wins for each player
    private boolean turn = true; // if turn is true, then it is P1's turn. Otherwise, P2's turn.
    private LinkedList<Move> moves = new LinkedList<Move>();
    private LinkedList<Move> saved_moves = new LinkedList<Move>();
    private int P1_wins = 0;
    private int P2_wins = 0;

    // Game constants
    public static final int COURT_WIDTH = 700;
    public static final int COURT_HEIGHT = 600;
    public static final int SQUARE_VELOCITY = 4;
    public static final int PIECE_SIZE = 100;
    public static final int CIRCLE_SIZE = 90;
    public static final int BOARD_COLS = 7;
    public static final int BOARD_ROWS = 6;
    public static final int NUMBER_OF_SPOTS = GameCourt.BOARD_ROWS * GameCourt.BOARD_COLS;

    // Update interval for timer, in milliseconds
    public static final int INTERVAL = 35;
    
    private JOptionPane display_winner;
    private Icon congrats_icon = new ImageIcon("files/nimbus.png");
    private Icon connect_with_jesus_icon = new ImageIcon("files/connect_with_jesus.png");
    
    public GameCourt(JLabel status, JLabel wins) {
    	
    	if (status == null || wins == null) {
    		throw new IllegalArgumentException();
    	}
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        

        // The timer is an object which triggers an action periodically with the given INTERVAL. We
        // register an ActionListener with this timer, whose actionPerformed() method is called each
        // time the timer triggers. We define a helper method called tick() that actually does
        // everything that should be done in a single timestep.
        Timer timer = new Timer(INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick();
            }
        });
        timer.start(); // MAKE SURE TO START THE TIMER!

        // Enable keyboard focus on the court area.
        // When this component has the keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        // This key listener allows the square to move as long as an arrow key is pressed, by
        // changing the square's velocity accordingly. (The tick method below actually moves the
        // square.)
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    square.setVx(-SQUARE_VELOCITY);
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    square.setVx(SQUARE_VELOCITY);
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    square.setVy(SQUARE_VELOCITY);
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    square.setVy(-SQUARE_VELOCITY);
                }
            }

            public void keyReleased(KeyEvent e) {
                square.setVx(0);
                square.setVy(0);
            }
        });
        
        addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (playing == false) {
					return;
				}
				Point p = e.getPoint();
				int col = (int) p.getX() / PIECE_SIZE;
				int row = (int) p.getY() / PIECE_SIZE;
				
				if (col >= BOARD_COLS || row >= BOARD_ROWS) {
					status.setText("You clicked outside the board ._.");
					return;
				}
					
				addPieceToColumn(col);
				checkIfWinner(true);
				
			}	
        });

        this.status = status;
        this.wins = wins;
        
        board = new int[BOARD_ROWS][BOARD_COLS];
    }
    
    private int countEmptyPieces() {
    	int counter = 0;
    	for (int row = 0; row < GameCourt.BOARD_ROWS; row++ ) {
    		for (int col = 0; col < GameCourt.BOARD_COLS; col++) {
    			if (board[row][col] == 0) {
    				counter++;
    			}
    		}
    	}
    	return counter;
    		
    }
    
    public void setWins() {
    	wins.setText("P1 wins: " + P1_wins + ", P2 wins: " + P2_wins);
    }
    
    public boolean addPieceToColumn(int col) {
    	if (col < 0 || col > BOARD_COLS) {
    		throw new IllegalArgumentException();
    	}
    	
    	for (int j = BOARD_ROWS-1; j >= 0; j--) {
    		
    		if (board[j][col] == 0) {
    			if (turn) {
    				status.setText("Player 2's turn!");
    				board[j][col] = 1;
    				moves.add(new Move(j,col,turn));
    			} else {
    				status.setText("Player 1's turn!");
    				board[j][col] = 2;
    				moves.add(new Move(j,col,turn));
    			}
    			turn = !turn;
    			 
    			return true;
    		}
    	}
    	status.setText("This column is full ._.");
    	return false;
    }
    

    /**
     * (Re-)set the game to its initial state.
     */
    public void reset() {
        square = new Square(COURT_WIDTH, COURT_HEIGHT, Color.BLACK);
        poison = new Poison(COURT_WIDTH, COURT_HEIGHT);
        snitch = new Circle(COURT_WIDTH, COURT_HEIGHT, Color.YELLOW);
        
        board = new int[BOARD_ROWS][BOARD_COLS];
        moves = new LinkedList<Move>();

        playing = true;
        status.setText("New Game Started!");
        turn = true;

        // Make sure that this component has the keyboard focus
        requestFocusInWindow();
    }
    
    // Undo a move
    protected void undo() {
    	if (moves.isEmpty()) 
    	{
    		status.setText("No more moves to undo!");
    		return;
    	}
    	Move last = moves.getLast();
    	board[last.getRow()][last.getCol()] = 0;
    	turn = !turn;
    	setStatus();
    	moves.removeLast();
    }
    
    private void setStatus() {
    	if (turn) {
    		status.setText("Player 1's turn!");
    	} else {
    		status.setText("Player 2's turn!");
    	}
    }
    
    public boolean checkIfWinner(boolean display_pop_up) {
    	// check for horizontal 
    	
    	if (countEmptyPieces() == 0) {
			status.setText("It's a tie! Click reset to start a new game!");
			playing = false;
		}
    	
    	for (int row = 0; row <= BOARD_ROWS-4; row++) {
    		for (int col = 0; col < BOARD_COLS; col++) {
    			if (board[row][col] != 0 
    					&& board[row][col] == board[row+1][col] 
    					&& board[row+1][col] == board[row+2][col] 
    					&& board[row+2][col] == board[row+3][col]) {
    				repaint();
    				if (display_pop_up) {
    					setWinner(board[row][col]);
    				}
    				updateWins(board[row][col]);
    				return true;
    			}
    		}
    	}
    	
    	//check for vertical
    	for (int row = 0; row < BOARD_ROWS; row++) {
    		for (int col = 0; col <= BOARD_COLS-4; col++) {
    			if (board[row][col] != 0 
    					&& board[row][col] == board[row][col+1] 
    					&& board[row][col+1] == board[row][col+2] 
    					&& board[row][col+2] == board[row][col+3]) {
    				repaint();
    				if (display_pop_up) {
    					setWinner(board[row][col]);
    				}
    				updateWins(board[row][col]);
    				return true;
    			}
    		}
    	}
    	
    	//check for ascending diagonals
    	for (int row = 3; row < BOARD_ROWS; row++) {
    		for (int col = 0; col <= BOARD_COLS-4; col++) {
    			if (board[row][col] != 0
    					&& board[row][col] == board[row-1][col+1]
    					&& board[row-1][col+1] == board[row-2][col+2]
    					&& board[row-2][col+2] == board[row-3][col+3]) {
    				repaint();
    				if (display_pop_up) {
    					setWinner(board[row][col]);
    				}
    				updateWins(board[row][col]);
    				return true;
    			}	
    		}
    	}
    	
    	//check for descending diagonals
    	for (int row = 0; row <= BOARD_ROWS-4; row++) {
    		for (int col = 0; col <= BOARD_COLS-4; col++) {
    			if (board[row][col] != 0
    					&& board[row][col] == board[row+1][col+1]
    					&& board[row+1][col+1] == board[row+2][col+2]
    					&& board[row+2][col+2] == board[row+3][col+3]) {
    				repaint();
    				if (display_pop_up) {
    					setWinner(board[row][col]);
    				}
    				updateWins(board[row][col]);
    				return true;
    			}
    		}
    	}
    	return false;	
    }
    
    private void updateWins(int player) {
    	if (player == 1) {
    		P1_wins++;
    	} else if (player == 2) {
    		P2_wins++;
    	}
    	repaint();
    }
  
    private void setWinner(int player) {
    	status.setText("Player " + player + " wins!");
    	display_winner = new JOptionPane("Player " + player + "wins!");
    	JOptionPane.showMessageDialog(display_winner, "Player " + player + " wins! Click reset to "
    			+ "start a new game.", "Congratulations!", JOptionPane.DEFAULT_OPTION, 
    			congrats_icon);
    	playing = false;
    }
    
    protected void displayInstructions() {
    	JOptionPane instructions = new JOptionPane();
    	JOptionPane.showMessageDialog(instructions, "Click any column, and the piece will "
    			+ "automatically drop to the lowest spot! "
    			+ "\nThe turns will automatically alternate."
    			+ "\nClick reset to start a new game."
    			+ "\nClick undo to undo a move."
    			+ "\nUse the Save Game and Load Game buttons to save and load a game!"
    			+ "\n\nAnd of course, get four in a row to win the game!", 
    			"How to Play!", 
    			JOptionPane.DEFAULT_OPTION, connect_with_jesus_icon);
	}
    
    public void saveGame(String filepath_board, String filepath_moves) {
    	File file_board = Paths.get(filepath_board).toFile();
    	File file_moves = Paths.get(filepath_moves).toFile();
		BufferedWriter br_board = null;
		BufferedWriter br_moves = null;
		try {
			br_board = new BufferedWriter(new FileWriter(filepath_board));
			br_moves = new BufferedWriter(new FileWriter(filepath_moves));
		} catch (IOException e) {
			System.err.println("File not found");
			return;
		}
		
		try {
			for (int row = 0; row < BOARD_ROWS; row++) {
				for (int col = 0; col < BOARD_COLS; col++) {
					br_board.write(board[row][col] + " ");
				}
				br_board.newLine();
			}
			for (Move m : moves) {
				br_moves.write(m.getRow() + " " + m.getCol() + " " + m.getPlayer());
				if (m != moves.getLast()) {
					br_moves.newLine();
				}
			}
			status.setText("Game Saved!");
		} catch (IOException e) {
			System.err.println("Error writing tweet to file " + filepath_board);
			
			try {
				br_board.close();
				br_moves.close();
			} catch (IOException e1) {
				System.err.println("Could not close file");
				return;
			}
			return;
		}
		
		try {
			br_board.close();
			br_moves.close();
		} catch (IOException e1) {
			System.err.println("Could not close file");
			return;
		}
    }
    
    public void loadGame(String filepath_board, String filepath_moves) {
    	FileIterator it_board = new FileIterator(filepath_board);
    	LinkedList<String> rows = new LinkedList<String>();
    	while (it_board.hasNext()) {
    		rows.add(it_board.next());
    	}
    	int[][] new_board = new int[BOARD_ROWS][BOARD_COLS];
    	int counter = 0;
    	for (String s : rows) {
    		String[] row_strings = s.split(" ");
    		int[] row = new int[row_strings.length];
    		for (int i = 0; i < row_strings.length; i++) {
    			row[i] = Integer.parseInt(row_strings[i]);
    		}
    		new_board[counter] = row;
    		counter++;
    	}
    	
    	FileIterator it_moves = new FileIterator(filepath_moves);
    	LinkedList<String> move_temp = new LinkedList<String>();
    	while (it_moves.hasNext()) {
    		move_temp.add(it_moves.next());
    	}
    	
    	LinkedList<Move> new_moves = new LinkedList<Move>();
    	for (String m : move_temp) {
    		String[] temp = m.split(" ");
    		new_moves.add(new Move(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]),
    				Boolean.parseBoolean(temp[2])));
    	}
    	
    	board = new_board;
    	turn = determineTurn(board);
    	moves = new_moves;
    	status.setText("Game Loaded!");
    	repaint();
    }

    private boolean determineTurn(int[][] board1) {
		int number_of_pieces = 0;
    	for (int row = 0; row < BOARD_ROWS; row++) {
			for (int col = 0 ; col < BOARD_COLS; col++) {
				if (board1[row][col] != 0) {
					number_of_pieces++;
				}
			}
		}
    	
    	if (number_of_pieces % 2 == 1) {
    		return false;
    	} else {
    		return true;
    	}
    	
	}

	/**
     * This method is called every time the timer defined in the constructor triggers.
     */
    void tick() {
        if (playing) {
            // advance the square and snitch in their current direction.
            square.move();
            snitch.move();

            // make the snitch bounce off walls...
            snitch.bounce(snitch.hitWall());
            // ...and the mushroom
            snitch.bounce(snitch.hitObj(poison));

            // check for the game end conditions
            if (square.intersects(poison)) {
                playing = false;
                status.setText("You lose!");
            } else if (square.intersects(snitch)) {
                playing = false;
                status.setText("You win!");
            }
            
            // update the display
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        square.draw(g);
        poison.draw(g);
        snitch.draw(g);
        
        for (int row = 0; row < BOARD_ROWS; row++) {
        	for (int col = 0; col < BOARD_COLS; col++) {
        		g.setColor(Color.BLUE);
        		g.fillRect(col*PIECE_SIZE, row*PIECE_SIZE, PIECE_SIZE, PIECE_SIZE);
        		g.setColor(Color.BLACK);
        		g.drawRect(col*PIECE_SIZE, row*PIECE_SIZE, PIECE_SIZE, PIECE_SIZE);
        		
        		
        		if (board[row][col] == 0) {
        			g.setColor(Color.WHITE);
        		} else if (board[row][col] == 1) {
        			g.setColor(Color.RED);
        		} else {
        			g.setColor(Color.YELLOW);
        		}
        		
        		int shiftUpperLeftForCircle = (PIECE_SIZE - CIRCLE_SIZE) / 2;
        		g.fillOval((col*PIECE_SIZE) + shiftUpperLeftForCircle, 
        				(row*PIECE_SIZE) + shiftUpperLeftForCircle, CIRCLE_SIZE, CIRCLE_SIZE);
        	}
        }
        setWins();
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(COURT_WIDTH, COURT_HEIGHT);
    }
    
    // Testing methods below (RIP encapsulation...)
    
    public int getBoardPiece(int row, int col) {
    	return board[row][col];
    }
    
    public LinkedList<Move> getMoves() {
    	LinkedList<Move> new_moves = new LinkedList<Move>();
    	for (Move m : moves) {
    		new_moves.add(m);
    	}
    	return new_moves;
    }
    
    public int[][] getBoard() {
    	return board.clone();
    }

    public void nextTurn() {
    	turn = !turn;
    }
    
    public String getStatus() {
    	return status.getText();
    }
    
    public int getP1Wins() {
    	return P1_wins;
    }

    public int getP2Wins() {
    	return P2_wins;
    }
	
}