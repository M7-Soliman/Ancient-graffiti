/*
 */
package edu.wlu.graffiti.bean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Inscription.java class holds all the information about a graffito.
 * 
 * @author Sara Sprenkle
 * @author Cooper Baird
 * @author Hammad Ahmad
 * @author Trevor Stalnaker
 */

public class Inscription implements Comparable<Inscription> , Serializable {

	private static final long serialVersionUID = 1L;

	private String PATH_TO_EDR_PAGE = "http://www.edr-edr.it/edr_programmi/res_complex_comune.php?lang=en&id_nr=";

	private int id;
	private String ancientCity;
	private String findSpot;
	private int findSpotPropertyID;
	private String measurements;
	private String language;
	private String content;
	private String bibliography;
	private String graffitiId;
	private String writingStyle;
	private String apparatus;
	private String apparatusDisplay;
	private List<Photo> photos;
	private String sourceFindSpot;
	private String dateBeginning;
	private String dateEnd;
	private String dateExplanation;
	private String principleContributors;
	private String lastRevision;
	private String editor;

	/* Field that were previously located in the AGPInfo bean */
	private String commentary;
	private String contentTranslation;
	private String caption;
	private String writingStyleInEnglish;
	private String languageInEnglish;
	private String meter;
	
	private Property property;
	private String cil;
	private String langner;
	private String graffito_height;
	private String graffito_length;
	private String letter_height_min;
	private String letter_height_max;
	private String letter_with_flourishes_height_min;
	private String letter_with_flourishes_height_max;
	private String height_from_ground;
	private String individualLetterHeights;
	private String epidoc;
	private FiguralInfo figuralInfo;
	private PoeticInfo poeticInfo;
	private boolean hasFiguralComponent = false;
	private boolean isPoetic = false;

	private boolean isFeaturedTranslation = false;
	private boolean isFeaturedFigural = false;
	private boolean isThemed = false;
	private FeaturedGraffitiInfo fgInfo;
	private List<Theme> themes;
	private List<Contribution> contributions;
	private String contributors;
	private boolean updateOfCil;

	private String support_desc;
	private String layout_desc;
	private String handnote_desc;

	private Segment segment;
	private boolean onFacade;

	private String preciseLocation;
	private boolean onColumn;
	private Column column;

	public Inscription() {

	}

	@Override
	public String toString() {
		return "Inscription [\n ancientCity=" + ancientCity + ",\n apparatus=" + apparatus + ",\n apparatusDisplay="
				+ apparatusDisplay + ",\n bibliography=" + bibliography + ",\n caption=" + caption + ",\n cil=" + cil
				+ ",\n commentary=" + commentary + ",\n content=" + content + ",\n contentTranslation="
				+ contentTranslation + ",\n contributions=" + contributions + ",\n contributors=" + contributors
				+ ",\n dateBeginning=" + dateBeginning + ",\n dateEnd=" + dateEnd + ",\n dateExplanation="
				+ dateExplanation + ",\n editor=" + editor + ",\n epidoc=" + epidoc + ",\n fgInfo=" + fgInfo
				+ ",\n figuralInfo=" + figuralInfo + ",\n findSpot=" + findSpot + ",\n findSpotPropertyID="
				+ findSpotPropertyID + ",\n graffitiId=" + graffitiId + ",\n graffito_height=" + graffito_height
				+ ",\n graffito_length=" + graffito_length + ",\n handnote_desc=" + handnote_desc
				+ ",\n hasFiguralComponent=" + hasFiguralComponent + ",\n height_from_ground=" + height_from_ground
				+ ",\n id=" + id + ",\n individualLetterHeights=" + individualLetterHeights + ",\n isFeaturedFigural="
				+ isFeaturedFigural + ",\n isFeaturedTranslation=" + isFeaturedTranslation + ",\n isThemed=" + isThemed
				+ ",\n langner=" + langner + ",\n language=" + language + ",\n languageInEnglish=" + languageInEnglish
				+ ",\n lastRevision=" + lastRevision + ",\n layout_desc=" + layout_desc + ",\n letter_height_max="
				+ letter_height_max + ",\n letter_height_min=" + letter_height_min
				+ ",\n letter_with_flourishes_height_max=" + letter_with_flourishes_height_max
				+ ",\n letter_with_flourishes_height_min=" + letter_with_flourishes_height_min + ",\n measurements="
				+ measurements + ",\n onFacade=" + onFacade + ",\n PATH_TO_EDR_PAGE=" + PATH_TO_EDR_PAGE + ",\n photos="
				+ photos + ",\n principleContributors=" + principleContributors + ",\n property=" + property
				+ ",\n segment=" + segment + ",\n sourceFindSpot=" + sourceFindSpot + ",\n support_desc=" + support_desc
				+ ",\n themes=" + themes + ",\n updateOfCil=" + updateOfCil + ",\n writingStyle=" + writingStyle
				+ ",\n writingStyleInEnglish=" + writingStyleInEnglish + "\n]";
	}

	@JsonIgnore
	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getAgpId() {
		return "AGP-" + graffitiId;
	}

	@JsonIgnore
	public String getDateBeginning() {
		return this.dateBeginning;
	}

	public void setDateBeginning(final String date) {
		this.dateBeginning = date;
	}

	@JsonIgnore
	public String getDateEnd() {
		return this.dateEnd;
	}

	public void setDateEnd(final String date) {
		this.dateEnd = date;
	}

	@JsonIgnore
	public String getAncientCity() {
		return this.ancientCity;
	}

	public void setAncientCity(final String ancientCity) {
		this.ancientCity = ancientCity;
	}

	// Uses the split up fields from agpInscription.java to create the findspot
	@JsonIgnore
	public String getFindSpot() {
		findSpot = "";
		findSpot += property.getPropertyName() + " (" + property.getInsula().getFullName() + "."
				+ property.getPropertyNumber() + ")";
		return findSpot;
	}

	/**
	 * @return the findSpotPropertyID
	 */
	@JsonIgnore
	public int getFindSpotPropertyID() {
		return findSpotPropertyID;
	}

	/**
	 * @param findSpotPropertyID the findSpotPropertyID to set
	 */
	public void setFindSpotPropertyID(int findSpotPropertyID) {
		this.findSpotPropertyID = findSpotPropertyID;
	}

	/**
	 * @param findSpot the findSpot from the source (EDR or Smyrna XML)
	 */
	public void setSourceFindSpot(String findSpot) {
		this.sourceFindSpot = findSpot;
	}

	@JsonInclude(Include.NON_EMPTY)
	public String getSourceFindSpot() {
		return sourceFindSpot;
	}

	// Used in the GrafittiController.java in the method findLocationKeys to
	// send to map.jsp in order to highlight the map
	// highlighting properties --> Just need to use the property id?
	@JsonIgnore
	public int getSpotKey() {
		if (getFindSpot() != null) { // TODO do we need to check this? when will an inscription not have a findspot?
			return property.getId();
		}
		return -1;
	}

	// highlighting insula
	@JsonIgnore
	public int getGenSpotKey() {
		if (getSpotKey() != -1) { // TODO do we need to check this?
			return property.getInsula().getId();
		}
		return 0;
	}

	// highlighting streets
	@JsonIgnore
	public int getStreetSpotKey() {
		if (onFacade == true) {
			if (segment != null) { // handle if we don't have the segment yet
				return segment.getStreet().getId();
			}
		}
		return -1;
	}

	// highlighting segments
	@JsonIgnore
	public int getSegmentSpotKey() {
		if (onFacade == true) {
			if (segment != null) { // handle if we don't have the segment yet
				return segment.getId();
			}
		}
		return -1;
	}

	public void setFindSpot(final String findSpot) {
		this.findSpot = findSpot;
	}

	@JsonIgnore
	public String getMeasurements() {
		return this.measurements;
	}

	public void setMeasurements(final String measurements) {
		this.measurements = measurements;
	}

	@JsonIgnore
	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(final String language) {
		this.language = language;
	}

	@JsonInclude(Include.NON_EMPTY)
	public String getContent() {
		return this.content;
	}

	// replaces the line breaks in the database with <br> tag to create line
	// breaks for the html page
	@JsonIgnore
	public String getContentWithLineBreaks() {
		if (this.content != null) {
			return this.content.replace("\n", "<br/>");
		} else {
			return "";
		}
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public String getBibliography() {
		return this.bibliography;
	}

	@JsonIgnore
	public String getBibliographyTrunc() {
		if (this.bibliography != null && !this.bibliography.isEmpty()) {
			return this.bibliography.substring(0, this.bibliography.indexOf('('));
		}
		return null;
	}

	public void setBibliography(final String bibliography) {
		this.bibliography = bibliography;
	}

	@JsonIgnore
	public String getGraffitiId() {
		return this.graffitiId;
	}


	public void setGraffitiId(final String eagleId) {
		this.graffitiId = eagleId;
	}

	@JsonIgnore
	public String getWritingStyle() {
		return this.writingStyle;
	}

	public void setWritingStyle(final String writingStyle) {
		this.writingStyle = writingStyle;
	}

	@JsonInclude(Include.NON_NULL)
	public String getApparatus() {
		return this.apparatus;
	}

	public void setApparatus(final String apparatus) {
		this.apparatus = apparatus;
	}

	@JsonIgnore
	public String getApparatusDisplay() {
		return this.apparatusDisplay;
	}

	public void setApparatusDisplay(final String apparatusDisplay) {
		this.apparatusDisplay = apparatusDisplay;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.graffitiId == null) ? 0 : this.graffitiId.hashCode());
		result = prime * result + this.id;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Inscription other = (Inscription) obj;
		if (this.graffitiId == null) {
			if (other.graffitiId != null)
				return false;
		} else if (!this.graffitiId.equals(other.graffitiId))
			return false;
		if (this.id != other.id)
			return false;
		return true;
	}

	public int compareTo(final Inscription inscription) {
		return this.getGraffitiId().compareTo(inscription.getGraffitiId());
	}

	/**
	 * 
	 * @return the list of image URLs
	 */
	@JsonIgnore
	public List<String> getImages() {
		List<String> imageList = new ArrayList<String>();
		for (Photo p : this.photos) {
			imageList.add(p.getImagePath());
		}
		return imageList;
	}

	/**
	 * 
	 * @return the list of the pages associated with the images
	 */
	@JsonIgnore
	public List<String> getPages() {
		List<String> pageList = new ArrayList<String>();
		for (Photo p : photos) {
			pageList.add(p.getPagePath());
		}
		return pageList;
	}

	/**
	 * 
	 * @return the list of image thumbnail urls
	 * 
	 */
	@JsonIgnore
	public List<String> getThumbnails() {
		List<String> imageList = new ArrayList<String>();
		for (Photo p : photos) {
			imageList.add(p.getThumbPath());
		}
		return imageList;
	}

	/**
	 * @return the citation for the graffito page in AGP
	 */
	public String getCitation() {
		String title = caption;
		if (title == null)
			title = "Graffito";

		DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
		Date date = new Date();
		String dateString = dateFormat.format(date);

		return "AGP-" + graffitiId
				+ ", <i>The Ancient Graffiti Project</i>, &lt;http://ancientgraffiti.org/Graffiti/graffito/AGP-"
				+ graffitiId + "&gt; [accessed: " + dateString + "]";
	}

	/**
	 * @return the photos
	 */
	public List<Photo> getPhotos() {
		return photos;
	}

	/**
	 * @param photos the photos to set
	 */
	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}

	/**
	 * @param myContent the content to pre-process
	 * @return myContent html characters converted to unicode
	 */
	@JsonIgnore
	public String getPreprocessedContent(String myContent) {
		if (myContent != null)
			return StringEscapeUtils.unescapeHtml4(myContent);

		return null;
	}

	/**
	 * @return the dataExplanation
	 */
	@JsonIgnore
	public String getDateExplanation() {
		return dateExplanation;
	}

	/**
	 * @param dataExplanation the dataExplanation to set
	 */
	public void setDateExplanation(String dateExplanation) {
		this.dateExplanation = dateExplanation;
	}

	@JsonInclude(Include.NON_NULL)
	public String getLastRevision() {
		return lastRevision;
	}

	public void setLastRevision(String lastRevision) {
		this.lastRevision = lastRevision;
	}

	@JsonInclude(Include.NON_NULL)
	public String getEditor() {
		return editor;
	}
	@JsonInclude(Include.NON_NULL)
	public String getMeter() {
		return meter;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	@JsonInclude(Include.NON_NULL)
	public String getPrincipleContributors() {
		return principleContributors;
	}

	public void setPrincipleContributors(String principleContributors) {
		this.principleContributors = principleContributors;
	}

	/* Methods that were previously in the AGPInfo Bean */

	/**
	 * @return the isThemed
	 */
	@JsonIgnore
	public boolean isThemed() {
		return isThemed;
	}

	/**
	 * @param isThemed the isThemed to set
	 */
	public void setThemed(boolean isThemed) {
		this.isThemed = isThemed;
	}

	@JsonIgnore
	public String getPagePath() {
		return PATH_TO_EDR_PAGE + graffitiId;
	}

	@JsonInclude(Include.NON_EMPTY)
	public String getCaption() {
		return caption;
	}

	@JsonInclude(Include.NON_EMPTY)
	public String getCommentary() {
		return commentary;
	}

	@JsonInclude(Include.NON_EMPTY)
	public String getContentTranslation() {
		return contentTranslation;
	}

	@JsonInclude(Include.NON_EMPTY)
	public String getWritingStyleInEnglish() {
		return writingStyleInEnglish;
	}

	public void setWritingStyleInEnglish(String writingStyleInEnglish) {
		this.writingStyleInEnglish = writingStyleInEnglish;
	}

	@JsonInclude(Include.NON_EMPTY)
	public String getLanguageInEnglish() {
		return languageInEnglish;
	}

	public void setLanguageInEnglish(String languageInEnglish) {
		this.languageInEnglish = languageInEnglish;
	}

	// measurement related getter methods
	@JsonInclude(Include.NON_EMPTY)
	public String getGraffitoHeight() {
		return graffito_height;
	}

	@JsonInclude(Include.NON_EMPTY)
	public String getGraffitoLength() {
		return graffito_length;
	}

	@JsonInclude(Include.NON_EMPTY)
	public String getMinLetterHeight() {
		return letter_height_min;
	}

	@JsonInclude(Include.NON_EMPTY)
	public String getMaxLetterHeight() {
		return letter_height_max;
	}

	/**
	 * @return the letter_with_flourishes_height_min
	 */
	@JsonInclude(Include.NON_EMPTY)
	public String getMinLetterWithFlourishesHeight() {
		return letter_with_flourishes_height_min;
	}

	/**
	 * @param letter_with_flourishes_height_min the
	 *                                          letter_with_flourishes_height_min to
	 *                                          set
	 */
	public void setMinLetterWithFlourishesHeight(String letter_with_flourishes_height_min) {
		this.letter_with_flourishes_height_min = letter_with_flourishes_height_min;
	}

	/**
	 * @return the letter_with_flourishes_height_max
	 */
	@JsonInclude(Include.NON_EMPTY)
	public String getMaxLetterWithFlourishesHeight() {
		return letter_with_flourishes_height_max;
	}

	/**
	 * @param letter_with_flourishes_height_max the
	 *                                          letter_with_flourishes_height_max to
	 *                                          set
	 */
	public void setMaxLetterWithFlourishesHeight(String letter_with_flourishes_height_max) {
		this.letter_with_flourishes_height_max = letter_with_flourishes_height_max;
	}

	// measurement related setter methods
	public void setGraffitoHeight(String graffito_height) {
		this.graffito_height = graffito_height;
	}

	public void setGraffitoLength(String graffito_length) {
		this.graffito_length = graffito_length;
	}

	public void setMinLetterHeight(String letter_height_min) {
		this.letter_height_min = letter_height_min;
	}

	public void setMaxLetterHeight(String letter_height_max) {
		this.letter_height_max = letter_height_max;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public void setCommentary(String commentary) {
		this.commentary = commentary;
	}

	public void setContentTranslation(String translation) {
		this.contentTranslation = translation;
	}

	@JsonInclude(Include.NON_EMPTY)
	public String getCil() {
		return cil;
	}

	public void setCil(String cil) {
		this.cil = cil;
	}

	/**
	 * @return the langner
	 */
	@JsonInclude(Include.NON_EMPTY)
	public String getLangner() {
		return langner;
	}

	/**
	 * @param langner the langner to set
	 */
	public void setLangner(String langner) {
		this.langner = langner;
	}

	/**
	 * @return the individualLetterHeights
	 */
	@JsonInclude(Include.NON_EMPTY)
	public String getIndividualLetterHeights() {
		return individualLetterHeights;
	}

	/**
	 * @param individualLetterHeights the individualLetterHeights to set
	 */
	public void setIndividualLetterHeights(String individualLetterHeights) {
		this.individualLetterHeights = individualLetterHeights;
	}

	/**
	 * @return the isFeaturedTranslation
	 */
	@JsonIgnore
	public boolean isFeaturedTranslation() {
		return isFeaturedTranslation;
	}

	/**
	 * @param isFeaturedTranslation the isFeaturedTranslation to set
	 */
	public void setFeaturedTranslation(boolean isFeaturedTranslation) {
		this.isFeaturedTranslation = isFeaturedTranslation;
	}

	/**
	 * @return the isFeaturedFigural
	 */
	@JsonIgnore
	public boolean isFeaturedFigural() {
		return isFeaturedFigural;
	}

	/**
	 * @param isFeaturedFigural the isFeaturedFigural to set
	 */
	public void setFeaturedFigural(boolean isFeaturedFigural) {
		this.isFeaturedFigural = isFeaturedFigural;
	}

	/**
	 * @return the figuralInfo
	 */
	@JsonInclude(Include.NON_EMPTY)
	public FiguralInfo getFiguralInfo() {
		return figuralInfo;
	}
	@JsonInclude(Include.NON_EMPTY)
	public PoeticInfo getPoeticInfo() {
		return poeticInfo;
	}

	/**
	 * @param figuralInfo the figuralInfo to set
	 */
	public void setFiguralInfo(FiguralInfo figuralInfo) {
		this.figuralInfo = figuralInfo;
	}

	/**
	 * @return the hasFiguralComponent
	 */
	@JsonIgnore
	public boolean hasFiguralComponent() {
		return hasFiguralComponent;
	}

	/**
	 * @param hasFiguralComponent the hasFiguralComponent to set
	 */
	public void setHasFiguralComponent(boolean hasFiguralComponent) {
		this.hasFiguralComponent = hasFiguralComponent;
	}

	@JsonIgnore
	public boolean getHasFiguralComponent() {
		return hasFiguralComponent;
	}

	
	@JsonIgnore
	public boolean isPoetic() {
		return isPoetic;
	}

	/**
	 * @param isPoetic the isPoetic to set
	 */
	public void setIsPoetic(boolean isPoetic) {
		this.isPoetic = isPoetic;
	}

	@JsonIgnore
	public boolean getIsPoetic() {
		return isPoetic;
	}


	/**
	 * @return the epidoc
	 */
	@JsonIgnore
	public String getEpidoc() {
		return epidoc;
	}

	/**
	 * @param epidoc the epidoc to set
	 */
	public void setEpidoc(String epidoc) {
		this.epidoc = epidoc;
	}

	@JsonIgnore
	public String getEpidocWithLineBreaks() {
		// remove all \r chars and add \n chars at every tag beginning
		return epidoc.trim().replaceAll("\r", "").replaceAll("<", "\n<");
	}

	/**
	 * @return the height_from_ground
	 */
	@JsonInclude(Include.NON_EMPTY)
	public String getHeightFromGround() {
		return height_from_ground;
	}

	/**
	 * @param height_from_ground the height_from_ground to set
	 */
	public void setHeightFromGround(String height_from_ground) {
		this.height_from_ground = height_from_ground;
	}

	/**
	 * @return the Featured Graffiti info
	 */
	@JsonIgnore
	public FeaturedGraffitiInfo getFeaturedGraffitiInfo() {
		return fgInfo;
	}

	/**
	 * @param fgInfo the featured graffiti info
	 */
	public void setFeaturedGraffitInfo(FeaturedGraffitiInfo fgInfo) {
		this.fgInfo = fgInfo;
	}

	/**
	 * @return the property
	 */
	@JsonInclude(Include.NON_EMPTY)
	public Property getProperty() {
		return property;
	}

	/**
	 * @param property the property to set
	 */
	public void setProperty(Property property) {
		this.property = property;
	}

	/**
	 * 
	 */
	public void setThemes(List<Theme> themes) {
		this.themes = themes;
	}

	/**
	 * 
	 * @return
	 */
	@JsonIgnore
	public List<Theme> getThemes() {
		return themes;
	}

	@JsonInclude(Include.NON_NULL)
	public String getContributors() {
		return contributors;
	}

	public void setContributors(String contributors) {
		this.contributors = contributors;
	}

	public void setContributions(List<Contribution> contributions) {
		this.contributions = contributions;
	}

	/**
	 * 
	 * @return
	 */
	@JsonIgnore
	public List<Contribution> getContributions() {
		return contributions;
	}

	@JsonIgnore
	public boolean isUpdateOfCil() {
		return updateOfCil;
	}

	public void setUpdateOfCil(boolean updateOfCil) {
		this.updateOfCil = updateOfCil;
	}

	@JsonIgnore
	public boolean getUpdateOfCil() {
		return updateOfCil;
	}

	public void setLayoutDesc(String layout_desc) {
		this.layout_desc = layout_desc;
	}

	@JsonIgnore
	public String getLayoutDesc() {
		return layout_desc;
	}

	public void setSupportDesc(String support_desc) {
		this.support_desc = support_desc;
	}

	@JsonIgnore
	public String getSupportDesc() {
		return support_desc;
	}

	public void setHandnoteDesc(String handnote_desc) {
		this.handnote_desc = handnote_desc;
	}

	@JsonIgnore
	public String getHandnoteDesc() {
		return handnote_desc;
	}

	/**
	 * @return the segment
	 */
	@JsonInclude(Include.NON_NULL)
	public Segment getSegment() {
		return segment;
	}

	/**
	 * @param segment the segment to set
	 */
	public void setSegment(Segment segment) {
		this.segment = segment;
	}

	@JsonIgnore
	public boolean getOnFacade() {
		return onFacade;
	}

	public void setOnFacade(boolean onFacade) {
		this.onFacade = onFacade;
	}

	public void setPreciseLocation(String location) {
		this.preciseLocation = location;
	}

	@JsonInclude(Include.NON_EMPTY)
	public String getPreciseLocation() {
		return this.preciseLocation;
	}

	@JsonIgnore
	public boolean getOnColumn() {
		return onColumn;
	}

	public void setOnColumn(boolean onColumn) {
		this.onColumn = onColumn;
	}

	@JsonInclude(Include.NON_NULL)
	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}
	
	/**
	 * 
	 * @return the Pleiades id of this location
	 */
	public String getPleiadesId() {
		if (onFacade) {
			// it's on a facade
			return segment.getStreet().getCity().getPleiadesId();
		} else {
			return property.getInsula().getCity().getPleiadesId();
		}
	}

}
