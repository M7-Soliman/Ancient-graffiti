package edu.wlu.graffiti.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents a street, which is made up of multiple segments.
 * 
 * @author Trevor Stalnaker
 */
public class Street implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String street_name;
	private City city;
	private List<Segment> segments;

	public Street() {
		super();
	}

	@Override
	public String toString() {
		return "Street [id=" + id + ", street_name=" + street_name + "]";
	}

	/**
	 * Constructs a street with the given id and name
	 * 
	 * @param id
	 * @param street_name
	 */
	public Street(int id, String street_name) {
		super();
		this.id = id;
		this.street_name = street_name;
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

	/**
	 * @return the street_name
	 */
	public String getStreetName() {
		return street_name;
	}

	/**
	 * @param street_name
	 */
	public void setStreetName(String street_name) {
		this.street_name = street_name;
	}

	/**
	 * A method that escapes apostrophes in street names, used in the sidebar search
	 * menu to prevent the apostrophes from breaking the statements
	 */
	@JsonIgnore
	public String getEscapedStreetName() {
		return street_name.replace("'", "_");
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	@JsonIgnore
	public List<Segment> getSegments() {
		return segments;
	}

	public void setSegments(List<Segment> segments) {
		this.segments = segments;
	}

	@JsonIgnore
	public String getUri() {
		String city = this.getCity().getName().toLowerCase();
		if( this.getStreetName() == null ) {
			System.out.println("Problem with street in " + city);
			System.out.println(id);
			System.out.println(street_name);
		}
		String street = this.getStreetName().replaceAll(" / ", "-").replaceAll(" ", "_").replaceAll("\\.", "__");
		return city + "/" + street;
	}

	@JsonInclude(Include.NON_EMPTY)
	public String getPompeiiinPicturesURL() {
		if (this.getCity().getName().equals("Pompeii")) {

			// Get the street name
			String streetName = this.getStreetName();

			// Return the empty string on special cases
			ArrayList<String> nolink = new ArrayList<String>(
					Arrays.asList("Vicolo del Foro", "Vicolo del Granaio", "Vicolo btw I.17 and I.16",
							"Vicolo btw III.4 and III.5", "Vicolo btw III.5 and III.6", "Vicolo btw III.6 and III.7"));
			if (nolink.contains(streetName)) {
				return "";
			}

			if (streetName != null) {

				// Make replacements for special cases
				if (streetName.equals("Via di Porta Nocera")) {
					streetName = streetName.replace("Porta ", "");
				} else if (streetName.equals("Strada Stabiana / Via del Vesuvio")) {
					streetName = streetName.replace("Strada Stabiana / ", "");
				} else if (streetName.equals("Via del Tempio di Iside")) {
					streetName = "Via del Tempio d'Iside";
				} else if (streetName.equals("Via dell'Abbondanza")) {
					streetName += " west p1";
				}
				// Handle the unnamed streets
				else if (streetName.equals("Vicolo btw I.1 and I.5")) {
					streetName = "UV between I 5 and I 1";
				} else if (streetName.equals("Vicolo btw I.8 and I.9")) {
					streetName = "UV between I 8 and I 9";
				} else if (streetName.equals("Vicolo btw I.9 and I.11")) {
					streetName = "UV between I 11 and I 9";
				} else if (streetName.equals("Vicolo btw III.10 and III.11")
						|| streetName.equals("Vicolo btw IV.4 and IV.5")) {
					streetName = "UV_between_IV_4_and_IV_5_and_III_11_and_III_10";
				} else if (streetName.equals("Vicolo btw III.11 and III.12")) {
					streetName = "UV_between_IV_5_and_Nola_Gate_and_III_11_and_III_12";
				} else if (streetName.equals("Vicolo btw III.8 and III.9")
						|| streetName.equals("Vicolo btw IV.2 and IV.3")) {
					streetName = "UV_between_IV_2_and_IV_3_and_III_9_and_III_8";
				} else if (streetName.equals("Vicolo btw III.9 and III.10")
						|| streetName.equals("Vicolo btw IV.3 and IV.4")) {
					streetName = "UV_between_IV_3_and_IV_4_and_III_10_and_III_9";
				} else if (streetName.equals("Vicolo btw III.9 and III.10")) {
					streetName = "UV_between_IV_2_and_IV_3_and_III_9_and_III_8";
				} else if (streetName.equals("Vicolo btw IV.1 and IV.2")
						|| streetName.contentEquals("Vicolo btw IX.14 and III.8")) {
					streetName = "UV_between_IV_1_and_IV_2_and_III_8_and_IX_14";
				} else if (streetName.equals("Vicolo della Basilica")) {
					streetName = "UV_between_VIII_1_3_and_VIII_1_1";
				} else if (streetName.equals("Vicolo di Octavius Quartio / Vicolo di Loreio Tiburtino")) {
					streetName = "Vicolo di Octavius Quartio";
				} else if (streetName.equals("Vicolo di Paquio Proculo")) {
					streetName = "Vicolo di Paquius Proculus";
				} else if (streetName.equals("Vicolo btw I.1 and Fortification Wall and extension")) {
					streetName = "UV_south_of_I_1_and_I_5";
				} else if (streetName.equals("Vicolo btw VIII.5.11 and VIII.5.19")) {
					streetName = "UV_between_VIII_5_19_and_VIII_5_11";
				} else if (streetName.equals("Vicolo btw 1.3 and 1.2 and extension")) {
					streetName = "UV_between_I_3_and_I_2";
				} else if (streetName.equals("Vicolo btw IX.3 and IX.4 and extension")) {
					streetName = "UV_between_IX_3_and_IX_4";
				} else if (streetName.equals("Vicolo dei 12 Dei")) {
					streetName = "Vicolo dei Dodici Dei";
				}
			}

			else {
				streetName = "";
			}

			// Final adjustments
			streetName = streetName.replace(" ", "_").replace("'", "_").replace("Strada", "Via");

			return "http://pompeiiinpictures.com/pompeiiinpictures/Streets/" + streetName + ".htm";
		}

		return "";
	}
}
