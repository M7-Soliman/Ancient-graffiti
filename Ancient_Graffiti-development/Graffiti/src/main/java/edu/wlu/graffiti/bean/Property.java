package edu.wlu.graffiti.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import edu.wlu.graffiti.data.setup.PompeiiInPicturesURLGenerator;

/**
 * Represents a property
 * 
 * @author Sara Sprenkle
 * @author Cooper Baird
 * @author Hammad Ahmad
 * @author Abby Nason
 * @author Trevor Stalnaker
 * 
 */
public class Property implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String property_number;
	private String englishPropertyName;
	private String italianPropertyName;
	private String agpPropertyName;
	private String additionalEntrances;
	private Insula insula;
	private List<PropertyType> propertyTypes;
	private List<PropertyLink> propertyLinks;
	private String pleiadesId = "";
	private String commentary = "";
	private String locationKey = "";
	private int numberOfGraffiti;
	private String uri;

	/**
	 * 
	 */
	public Property() {

		numberOfGraffiti = 0;
	}

	public Property(int id) {
		this();
		this.id = id;
		this.locationKey = "p" + id;
	}

	/**
	 * @param id
	 * @param property_number
	 * @param property_name
	 * @param insula
	 */
	public Property(int id, String property_number, String property_name, Insula insula) {
		this();
		this.id = id;
		this.property_number = property_number;
		this.englishPropertyName = property_name;
		this.insula = insula;
		this.propertyTypes = new ArrayList<PropertyType>();
		this.locationKey = "p" + id;
	}

	@Override
	public String toString() {
		return "Property [\n \tadditionalEntrances=" + additionalEntrances + ",\n \tagpPropertyName=" + agpPropertyName
				+ ",\n \tcommentary=" + commentary + ",\n \tenglishPropertyName=" + englishPropertyName + ",\n \tid="
				+ id + ",\n \tinsula=" + insula + ",\n \titalianPropertyName=" + italianPropertyName
				+ ",\n \tlocationKey=" + locationKey + ",\n \tnumberOfGraffiti=" + numberOfGraffiti
				+ ",\n \tpleiadesId=" + pleiadesId + ",\n \tproperty_number=" + property_number + ",\n \tpropertyLinks="
				+ propertyLinks + ",\n \tpropertyTypes=" + propertyTypes + ",\n \turi=" + uri + "\n\n]";
	}

	@JsonInclude(Include.NON_NULL)
	public List<PropertyType> getPropertyTypes() {
		return propertyTypes;
	}

	@JsonIgnore
	public List<PropertyLink> getPropertyLinks() {
		return propertyLinks;
	}

	@JsonIgnore
	public String getPropertyTypesAsString() {
		StringBuilder returnStr = new StringBuilder();
		if (this.propertyTypes.size() > 1) {
			// change the separator to a comma if there are multiple property types
			for (int i = 0; i < this.propertyTypes.size() - 1; i++) {
				PropertyType pt = this.propertyTypes.get(i);
				returnStr.append(pt.getName() + ", ");
			}
			returnStr.append(this.propertyTypes.get(this.propertyTypes.size() - 1).getName());
		} else if (this.propertyTypes.size() == 1) {
			returnStr.append(this.propertyTypes.get(0).getName());
		}
		return returnStr.toString();
	}

	public void setPropertyTypes(List<PropertyType> propertyTypes) {
		this.propertyTypes = propertyTypes;
	}

	public void setPropertyLinks(List<PropertyLink> propertyLinks) {
		this.propertyLinks = propertyLinks;
	}

	/**
	 * @return the uri
	 */
	@JsonInclude(Include.NON_NULL)
	public String getUri() {
		uri = this.getInsula().getCity().getName() + "/" + this.getInsula().getShortName() + "/"
				+ this.getPropertyNumber();
		return uri;
	}

	/**
	 * @param uri the URI to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * @return the id
	 */
	@JsonIgnore
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
		this.locationKey = "p" + id;
	}

	/**
	 * @return the property_number
	 */
	public String getPropertyNumber() {
		return property_number;
	}

	/**
	 * @return the additionalEntrances
	 */
	@JsonInclude(Include.NON_EMPTY)
	public String getAdditionalEntrances() {
		return additionalEntrances;
	}

	/**
	 * @param additionalEntrances
	 */
	public void setAdditionalEntrances(String additionalEntrances) {
		this.additionalEntrances = additionalEntrances;
	}

	/**
	 * @param property_number the property_number to set
	 */
	public void setPropertyNumber(String property_number) {
		this.property_number = property_number;
	}

	/**
	 * @return the agpPropertyName
	 */
	@JsonInclude(Include.NON_EMPTY)
	public String getPropertyName() {
		return agpPropertyName;
	}

	/**
	 * @param agpPropertyName the property_name to set
	 */
	public void setPropertyName(String agpPropertyName) {
		this.agpPropertyName = agpPropertyName;
	}

	/**
	 * @return the insula
	 */
	public Insula getInsula() {
		return insula;
	}

	/**
	 * @param insula the insula to set
	 */
	public void setInsula(Insula insula) {
		this.insula = insula;
	}

	/**
	 * @return the pleiadesId
	 */
	@JsonInclude(Include.NON_EMPTY)
	public String getPleiadesId() {
		return pleiadesId;
	}

	/**
	 * @param pleiadesId the pleiadesId to set
	 */
	public void setPleiadesId(String pleiadesId) {
		this.pleiadesId = pleiadesId;
	}

	/**
	 * @return the italianPropertyName
	 */
	@JsonInclude(Include.NON_EMPTY)
	public String getItalianPropertyName() {
		return italianPropertyName;
	}

	/**
	 * @param italianPropertyName the italianPropertyName to set
	 */
	public void setItalianPropertyName(String italianPropertyName) {
		this.italianPropertyName = italianPropertyName;
	}

	/**
	 * @return the commentary
	 */
	@JsonInclude(Include.NON_EMPTY)
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
	 * @return the locationKey
	 */
	@JsonIgnore
	public String getLocationKey() {
		return locationKey;
	}

	// TODO: THis maybe should be removed
	
	/**
	 * @return the data (region and insula num) to be used in generating URLs
	 */
	private String[] parseData() {

		String shortName = insula.getShortName();
		int periodIndex = shortName.indexOf('.');
		String numeral = shortName.substring(0, periodIndex).trim();
		String region = String.valueOf(PompeiiInPicturesURLGenerator.convertRomanNumberToNumber(numeral));
		String insulaNum = shortName.substring(periodIndex + 1);

		return new String[] { region, insulaNum };
	}


	/**
	 * @return the URL to P-LOD linked open data for a Pompeii property
	 */
	@JsonIgnore
	public String getPlodURL() {
		if (insula.getCity().getName().equals("Pompeii") && this.id != 0) {
			String data[] = parseData();
			String region = data[0];
			String insulaNum = data[1];

			return "http://digitalhumanities.umass.edu/p-lod/entities/r" + region + "-i" + insulaNum + "-p"
					+ property_number;
		}

		return "";
	}

	/**
	 * @param count the number of graffiti in the property to set
	 */
	public void setNumberOfGraffiti(int count) {
		numberOfGraffiti = count;
	}

	/**
	 * @return the number of graffiti in the property
	 */
	public int getNumberOfGraffiti() {
		return numberOfGraffiti;
	}

	/**
	 * @return the englishPropertyName
	 */
	@JsonInclude(Include.NON_EMPTY)
	public String getEnglishPropertyName() {
		return englishPropertyName;
	}

	/**
	 * @param englishPropertyName the englishPropertyName to set
	 */
	public void setEnglishPropertyName(String englishPropertyName) {
		this.englishPropertyName = englishPropertyName;
	}

}
