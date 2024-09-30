/**
 * 
 */
package edu.wlu.graffiti.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Information about the figural component
 * 
 * @author Sara Sprenkle
 * @author Trevor Stalnaker
 *
 */
public class FiguralInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String description_in_latin = "";
	private String description_in_english = "";
	private Set<DrawingTag> drawingTags;
	
	public FiguralInfo() {
		drawingTags = new HashSet<DrawingTag>();
	}
	
	

	@Override
	public String toString() {
		return "FiguralInfo [\n \tdescription_in_english=" + description_in_english + ",\n\n \tdescription_in_latin="
				+ description_in_latin + ",\n\n \tdrawingTags=" + drawingTags + "\n\n]";
	}



	/**
	 * @return the figural component's description in Latin
	 */
	@JsonInclude(Include.NON_EMPTY)
	public String getDescriptionInLatin() {
		return description_in_latin;
	}


	/**
	 * @param description_in_latin the figural component's description in Latin
	 */
	public void setDescriptionInLatin(String description_in_latin) {
		this.description_in_latin = description_in_latin;
	}


	/**
	 * @return the figural component's description in English
	 */
	@JsonInclude(Include.NON_EMPTY)
	public String getDescriptionInEnglish() {
		return description_in_english;
	}


	/**
	 * @param description_in_english the figural component's description in English
	 */
	public void setDescriptionInEnglish(String description_in_english) {
		this.description_in_english = description_in_english;
	}
	@JsonInclude(Include.NON_EMPTY)
	public Set<DrawingTag> getDrawingTags() {
		return drawingTags;
	}

	public void addDrawingTag(DrawingTag drawingtag) {
		this.drawingTags.add(drawingtag);
	}

	public void addDrawingTags(List<DrawingTag> drawingTags) {
		for (DrawingTag dt : drawingTags) {
			addDrawingTag(dt);
		}
	}

}
