import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FileIterator implements Iterator<String> {
	private BufferedReader reader;
	private String line;
	
	public FileIterator(String filepath) {
		try {
			reader = new BufferedReader(new FileReader(filepath));
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException();
		} catch (NullPointerException e) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public boolean hasNext() {
		try {
			return reader.ready();
		} catch (IOException e) {
			try {
				reader.close();
			} catch (IOException e1) {
				System.err.println("could not close BufferedReader");
			}
			return false;
		}
	}

	@Override
	public String next() {
		if (this.hasNext() == false) {
			throw new NoSuchElementException();
		}
		
		try {
			line = reader.readLine();
		} catch (IOException e) {
			throw new NoSuchElementException();
		}

		return line;
	}
}
