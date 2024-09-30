/**
 * 
 */
package edu.wlu.graffiti.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Information about the poetic component
 * 
 * @author Jack Sorenson
 *
 */
public class PoeticInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String meter = "";
	private String author = "";
	private boolean confirmed = false;
	
	
	public PoeticInfo() {
	}
	
	

	@Override
	public String toString() {
		String ifNoMeter = "";
		String ifNoAuthor = "";
		String isConfirmed = " and it is not currently confirmed.";
		
		if (meter.equals("")) {
			ifNoMeter = "unknown ";
		}
		if (author.equals("") ) {
			ifNoAuthor = "unknown";
		}
		if (confirmed) {
			isConfirmed = " and it is currently confirmed.";
		}
		
		
		return "PoeticInfo: The author of the poem is  " + ifNoAuthor + "the meter is " + ifNoMeter + isConfirmed;
	}



	/**
	 * @return the poem's author
	 */
	@JsonInclude(Include.NON_EMPTY)
	public String getAuthor() {
		return author;
	}


	/**
	 * @param set the poem's author
	 */
	public void setAuthor(String authorName) {
		this.author = authorName;
	}


	/**
	 * @return the poems meter
	 */
	@JsonInclude(Include.NON_EMPTY)
	public String getMeter() {
		return meter;
	}


	/**
	 * @param set the poem's meter
	 */
	public void setMeter(String newMeter) {
		this.meter = newMeter;
	}

}
