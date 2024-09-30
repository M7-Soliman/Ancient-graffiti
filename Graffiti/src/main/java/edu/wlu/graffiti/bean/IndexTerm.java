package edu.wlu.graffiti.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This class holds the getters and setters for the IndexTerm object. The
 * IndexTerm object consists for fours strings: graffiti_id, term, category and
 * entry. The term field holds each word, name or place that occurs in the
 * graffiti. A term has a separate record every time it is found in graffiti, so
 * if there are 7 separate graffiti with the word "servus", there will be 7
 * entries with "servus" in the term field. graffiti_id is the agp id associated
 * with the graffito in which the term exists. Most id's will occur more than
 * once because they contain more than one term. Category differentiates terms
 * that are vocabula, nomina, or loci. For uniformity, figural graffiti may also
 * want to be given their own category, since they exist in a different index
 * page than vocabula and right now they are only set aside from the vocabula
 * index with a js hack. Entry contains the full text of the graffito in which
 * the term occurs.
 * 
 * @author: Bancks Holmes
 * @author: Trevor Stalnaker
 */
public class IndexTerm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int termID;
	private String term;
	private String category;
	private String language;
	private Boolean display;
	private List<IndexEntry> entries;

	public IndexTerm() {
		super();
	}
	
	public void setTermID(int id) {
		this.termID = id;
	}
	
	@JsonIgnore
	public int getTermID() {
		return this.termID;
	}
	
	public void setTerm(String term) {
		this.term = term;
	}
	
	@JsonIgnore
	public String getTerm() {
		return this.term;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	@JsonIgnore
	public String getCategory() {
		return this.category;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	@JsonIgnore
	public String getLanguage() {
		return this.language;
	}
	
	public void setDisplay(boolean display) {
		this.display = display;
	}
	
	@JsonIgnore
	public boolean getDisplay() {
		return this.display;
	}
	
	public void setEntries(List<IndexEntry> entries) {
		this.entries = entries;
	}
	
	@JsonIgnore
	public List<IndexEntry> getEntries() {
		return this.entries;
	}
	
	public List<IndexEntry> getEntriesByLocation(List<String> locations){
		ArrayList<IndexEntry> filtered = new ArrayList<IndexEntry>();
		for (IndexEntry e : this.entries) {
			if (locations.contains(e.getCity())) {
				filtered.add(e);
			}
		}
		return filtered;
	}
}
