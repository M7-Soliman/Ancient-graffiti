package edu.wlu.graffiti.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A Segment represents a part of a Street
 * 
 * 
 * @author Trevor Stalnaker
 *
 */
public class Segment implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String segment_name;
	private Street street;
	private String display_name;
	private boolean hidden;

	public Segment() {
		super();
	}

	public Segment(int id) {
		super();
		this.id = id;
	}

	/**
	 * @param id
	 * @param street_name
	 */
	public Segment(int id, String segment_name, Street street) {
		super();
		this.id = id;
		this.segment_name = segment_name;
		this.street = street;
	}

	/**
	 * @return the id
	 */
	@JsonIgnore
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "Segment [id=" + id + ", segment_name=" + segment_name + ", street=" + street + "]";
	}

	/**
	 * @return the segment_name
	 */
	public String getSegmentName() {
		return segment_name;
	}

	/**
	 * @param segment_name
	 */
	public void setSegmentName(String segment_name) {
		this.segment_name = segment_name;
	}

	/**
	 * @return the display_name
	 */
	@JsonIgnore
	public String getDisplayName() {
		return display_name;
	}

	/**
	 * @param display_name
	 */
	public void setDisplayName(String display_name) {
		this.display_name = display_name;
	}

	@JsonIgnore
	public boolean getHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	/**
	 * @return the street_id
	 */
	public Street getStreet() {
		return street;
	}

	/**
	 * @param segment_name
	 */
	public void setStreet(Street street) {
		this.street = street;
	}

	/**
	 * @return the street_id
	 */
	public String getUri() {
		if( this.getStreet().getStreetName() == null ) {
			System.out.println("Problem with street name for segment ");
			System.out.println(this.getSegmentName());
			System.out.println(this.getId());
			System.out.println(this.getDisplayName());
			return "";
		}
		String street = this.getStreet().getUri();
		String seg = this.getSegmentName().replaceAll("\\.", "_");
		return street + "/" + seg;
	}
}
