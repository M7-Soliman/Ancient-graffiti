package edu.wlu.graffiti.data.main;

/**
 * Base class to set up and run database interactions.
 * Child classes must implement the run method.
 * Child classes should call runDBInteractions.
 * 
 * @author Sara Sprenkle
 *
 */
public abstract class DBInteraction extends DBConnection {

	/**
	 * Automatically calls run
	 */
	public DBInteraction() {
		init();
	}

	/**
	 * Runs the database interactions
	 */
	public abstract void run();

	/**
	 * Runs the database interactions for the child class and then closes the
	 * connection
	 */
	public void runDBInteractions() {
		run();
		close();
	}

}
