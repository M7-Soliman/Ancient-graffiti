package edu.wlu.graffiti.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * 
 * @author Trevor Stalnaker
 *
 */
public class IndexEntry implements Serializable {
	
	private static final long serialVersionUID = 1L;
	int term_id;
	String graffiti_id;
	String hit;
	String content;
	String city;
	
	public IndexEntry() {
		super();
	}
	
	public void setTermID(int term_id) {
		this.term_id = term_id;
	}
	
	@JsonIgnore
	public int getTermID() {
		return term_id;
	}
	
	public void setGraffitiID(String graffiti_id) {
		this.graffiti_id = graffiti_id;
	}
	
	@JsonIgnore
	public String getGraffitiID() {
		return graffiti_id;
	}
	
	public void setHit(String hit) {
		this.hit = hit;
	}
	
	@JsonIgnore
	public String getHit() {
		return hit;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	@JsonIgnore
	public String getContent() {
		return content;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	@JsonIgnore
	public String getCity() {
		return city;
	}
}
