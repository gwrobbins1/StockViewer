/**
 * 
 */
package main;

import gui.App;

/**
 * @author george
 *
 */
public class Main {
	public Main() {}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			App.main(args);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
