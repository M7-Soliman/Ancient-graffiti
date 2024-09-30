/*
 * Drawing tag class holds the photos information for Inscription.java class it receives its information
 * from photos table in the database. 
 */
package edu.wlu.graffiti.bean;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a Photo for a graffito.
 * 
 * @author Hammad Ahmad
 * @author Trevor Stalnaker
 *
 */
public class Photo implements Comparable<Photo> , Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final String BASE_EDR_THUMBNAIL_PHOTO_URL = "http://www.edr-edr.it/foto_epigrafi/thumbnails/";
	private static final String BASE_EDR_IMAGE_PAGE_URL = "http://www.edr-edr.it/edr_programmi/view_img.php?lang=en&id_nr=";
	public static final String BASE_EDR_PHOTO_URL = "http://www.edr-edr.it/foto_epigrafi/immagini_uso/";
	
	public static final String BASE_SMYRNA_URL = "http://images.isaw.nyu.edu/collections/smyrna_basilica_graffiti/";

	private int id;
	private String graffitiId;
	private String photoId;
	private String imgPath;
	private String thumbPath;
	private String pagePath;

	public Photo() {
		super();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(graffitiId);
		builder.append(" ");
		builder.append(photoId);
		return builder.toString();
	}

	public void setId(int id) {
		this.id = id;
	}

	@JsonIgnore
	public String getGraffitiId() {
		return this.graffitiId;
	}

	public void setGraffitiId(final String id) {
		this.graffitiId = id;
	}

	public String getPhotoId() {
		return photoId;
	}

	public void setPhotoId(String name) {
		if (name != null && !name.equals("") && graffitiId.startsWith("EDR")) {
			while (name.length() < 6) {
				name = "0" + name;
			}
		}
		this.photoId = name;
	}
	
	public void setPaths() {
		this.imgPath = getImagePath(graffitiId, photoId);
		this.thumbPath = getThumbPath(graffitiId, photoId);
		this.pagePath = getPagePath(graffitiId, photoId);
	}

	public String getImagePath() {
		return imgPath;
	}

	public String getThumbPath() {
		return thumbPath;
	}

	public String getPagePath() {
		return pagePath;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.photoId == null) ? 0 : this.photoId.hashCode());
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
		final Photo other = (Photo) obj;
		if (this.photoId == null) {
			if (other.photoId != null)
				return false;
		} else if (!this.photoId.equals(other.photoId))
			return false;
		if (this.graffitiId != other.graffitiId)
			return false;
		return true;
	}

	/**
	 * compares by name, alphabetically
	 */
	public int compareTo(final Photo photo) {
		return this.getPhotoId().compareTo(photo.getPhotoId());
	}
	
	// Methods that dynamically create the various image paths
	
	@JsonIgnore
	private String getImagePath(String id, String photoId) {		
		String source = id.substring(0,3);	
		if(source.equals("EDR")) {
			return BASE_EDR_PHOTO_URL + getEdrDirectory(id) + "/" + photoId + ".jpg";
		}		
		if(source.equals("SMY")) {
			return BASE_SMYRNA_URL + getSmyrnaDirectory(id, photoId) + "/w360.jpg";
		}
		return "";
	}
	
	@JsonIgnore
	private String getThumbPath(String id, String photoId) {		
		String source = id.substring(0,3);	
		if(source.equals("EDR")) {
			return BASE_EDR_THUMBNAIL_PHOTO_URL + getEdrDirectory(id) + "/th_" + photoId + ".jpg";
		}		
		if(source.equals("SMY")) {
			return BASE_SMYRNA_URL + getSmyrnaDirectory(id, photoId) + "/w120.jpg";
		}
		return "";
	}
	
	@JsonIgnore
	private String getPagePath(String id, String photoId) {		
		String source = id.substring(0,3);	
		if(source.equals("EDR")) {
			return BASE_EDR_IMAGE_PAGE_URL + photoId;
		}		
		if(source.equals("SMY")) {
			return BASE_SMYRNA_URL + getSmyrnaDirectory(id, photoId);
		}
		return "";
	}
	
	@JsonIgnore
	private String getEdrDirectory(String id) {
		String dir = id.substring(3,6);
		int i = 0;
		while (dir.charAt(i) == '0') {
			i++;
		}
		return dir.substring(i);
	}
		
	@JsonIgnore
	private String getSmyrnaDirectory(String id, String photoId) {
		String baysORpiers = "bays/";
		String location = "";
		String landing = "";
		Matcher matcher = Pattern.compile("smy([td])([a-z]?)[0]*([0-9]+)([0-9])").matcher(id.toLowerCase());
		if (matcher.find()) {
			location = matcher.group(3);
			landing = matcher.group(1) + matcher.group(2) + matcher.group(3) + "_" + matcher.group(4);
			if (location.length() == 1) {
				location = "0" + location;
			}
			// Handles a very rare case where there are two numbers following the decimal point of the ID
			if (matcher.group(2).equals("")) {
				if (matcher.group(3).length()>2) {
					String temp = matcher.group(3);
					String beforePoint = temp.substring(0, temp.length()-1);
					String afterPoint = temp.charAt(temp.length()-1) + matcher.group(4);
					landing = matcher.group(1) + matcher.group(2) + beforePoint + "_" + afterPoint;
					location = location.substring(0, location.length()-1);
				}
			}
			if (matcher.group(2).equals("p")) {
				baysORpiers = "piers/";
				while (location.length() < 3) {
					location = "0" + location;
				}
				location = "p" + location;
			}
			if (matcher.group(2).equals("g")) {
				location = "g" + location;
			}
			if (matcher.group(2).equals("x")) {
				location = "";
				landing = matcher.group(1) + matcher.group(2) + "_" + matcher.group(4);
				baysORpiers = "misc";
			}
		}
		return baysORpiers + location + "/" + landing + "/" + photoId;
	}

}
