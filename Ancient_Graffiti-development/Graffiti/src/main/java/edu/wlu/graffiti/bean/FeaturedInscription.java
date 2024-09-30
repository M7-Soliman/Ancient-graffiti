package edu.wlu.graffiti.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class FeaturedInscription implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String PATH_TO_EDR_PAGE = "http://www.edr-edr.it/edr_programmi/res_complex_comune.php?lang=en&id_nr=";	
	private String graffitiId;
	private String content;
	private String contentTranslation;
	private String cil;
	private String commentary;
	private Photo preferredImage;
	private Boolean in_database;
	
	public String getAgpId() {
		return "AGP-" + graffitiId;
	}
	
	public String getGraffitiId() {
		return this.graffitiId;
	}

	public void setGraffitiId(final String graffiti_id) {
		this.graffitiId = graffiti_id;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public String getContentWithLineBreaks() {
		if (this.content != null) {
			this.content = this.content.replace("\n", "<br/>");
			return this.content.replace("  ", "&nbsp;&nbsp;");
		} else {
			return "";
		}
	}
	
	public void setContent(final String content) {
		this.content = content;
	}
	
	public String getContentTranslation() {
		return contentTranslation;
	}
	
	public void setContentTranslation(String translation) {
		this.contentTranslation = translation;
	}
	
	public String getContentTranslationWithLineBreaks() {
		if (this.contentTranslation != null) {
			this.contentTranslation = this.contentTranslation.replace("\n", "<br/>");
			return this.contentTranslation.replace("  ", "&nbsp;&nbsp;");
		} else {
			return "";
		}
	}

	public String getCil() {
		return cil;
	}

	public void setCil(String cil) {
		this.cil = cil;
	}
	
	public Boolean getInDatabase() {
		return in_database;
	}
	
	public void setInDatabase(Boolean in){
		in_database = in;
	}

	public String getPagePath() {
		return PATH_TO_EDR_PAGE + graffitiId;
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
}
