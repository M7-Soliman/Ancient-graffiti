/**
 * 
 */
package edu.wlu.graffiti.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Stores information about the graffiti that are "featured"
 * 
 * @author Sara Sprenkle
 * @author Trevor Stalnaker
 */
public class FeaturedGraffitiInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	private String commentary = "";
	private Photo preferredImage = null;

	/**
	 * @return the commentary
	 */
	@JsonIgnore
	public String getCommentary() {
		return commentary;
	}

	/**
	 * @param commentary the commentary to set
	 */
	public void setCommentary(String commentary) {
		this.commentary = commentary;
	}

	/**
	 * @return the preferredImage
	 */
	@JsonIgnore
	public Photo getPreferredImage() {
		return preferredImage;
	}

	/**
	 * @param preferredImage the preferredImage to set
	 */
	public void setPreferredImage(Photo preferredImage) {
		this.preferredImage = preferredImage;
	}
}
