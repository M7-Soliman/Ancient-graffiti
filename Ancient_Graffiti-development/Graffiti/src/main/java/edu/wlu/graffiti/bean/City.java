package edu.wlu.graffiti.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents a city in AGP
 * @author Sara Sprenkle
 * @author Trevor Stalnaker
 */
public class City implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private String pleiadesID;
	
	
	
	
	@Override
	public String toString() {
		return "City [\n \tdescription=" + description + ",\n\n \tname=" + name + ",\n\n \tpleiadesID=" + pleiadesID
				+ "\n\n]";
	}
	/**
	 * @return the city's name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name name of the city
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the city's description
	 */
	@JsonInclude(Include.NON_NULL)
	public String getDescription() {
		return description;
	}
	/**
	 * @param description description of the city
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the pleiadesID
	 */
	@JsonInclude(Include.NON_NULL)
	public String getPleiadesId() {
		return pleiadesID;
	}
	/**
	 * @param pleiadesID the city's pleiadesID
	 */
	public void setPleiadesId(String pleiadesID) {
		this.pleiadesID = pleiadesID;
	}
}
