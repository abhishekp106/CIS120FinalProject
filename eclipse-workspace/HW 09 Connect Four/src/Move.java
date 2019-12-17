
public class Move 
{
	private final int row;
	private final int col;
	private final boolean player; // true means P1, false means P2  
								  // just like turn in GameCourt class!
	
	//invariants:
	//	x and y must be positive, and < GameCourt.BOARD_ROWS, GameCourt.BOARD_COLS respectively
	
	public Move(int row, int col, boolean player) {
		if (row < 0 || col < 0 || row >= GameCourt.BOARD_ROWS || col >= GameCourt.BOARD_COLS) {
			throw new IllegalArgumentException();
		}
		this.row = row;
		this.col = col;
		this.player = player;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	public boolean getPlayer() {
		return player;
	}
	
}
