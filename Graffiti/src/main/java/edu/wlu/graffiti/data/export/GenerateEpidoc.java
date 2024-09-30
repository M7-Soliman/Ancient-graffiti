/**
 * 
 */
package edu.wlu.graffiti.data.export;

import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import edu.wlu.graffiti.bean.Contribution;
import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.data.setup.TransformEDRContentToEpiDoc;

/**
 * This class serializes Inscription objects and returns a string in XML format
 * to represent the objects.
 * 
 * @author Hammad Ahmad
 * @author Trevor Stalnaker
 * 
 */
public class GenerateEpidoc {

	public static final String URI_BASE = "ancientgraffiti.org/Graffiti";

	private static final String SEGMENT_URI_BASE = URI_BASE + "/segments/";
	public static final String PROPERTIES_URI_BASE = URI_BASE + "/properties/";

	public GenerateEpidoc() {
	}

	/**
	 * Serializes an inscription to XML.
	 * 
	 * @param i The inscription
	 * @return the string representation in CML format
	 */
	public String serializeInscriptionToXML(Inscription i) {

		Element root = new Element("TEI");
		Document doc = new Document(root);

		Namespace ns = Namespace.getNamespace("ns", "http://www.tei-c.org/ns/1.0");
		root.setAttribute(new Attribute("lang", "en", Namespace.XML_NAMESPACE));
		root.setAttribute(new Attribute("base", "ex-epidoctemplate.xml", Namespace.XML_NAMESPACE));
		root.addNamespaceDeclaration(ns);

		generateTEIHeader(i, root);
		generateFacsimile(i, root);
		generateBody(i, root);

		XMLOutputter out = new XMLOutputter();
		out.setFormat(Format.getPrettyFormat());

		return out.outputString(doc);
	}

	/**
	 * Serializes a list of inscriptions to XML.
	 * 
	 * @param inscriptions The list of inscription
	 * @return the string representation in XML format
	 */
	public String serializeInscriptionsToXML(List<Inscription> inscriptions) {

		XMLOutputter out = new XMLOutputter();
		out.setFormat(Format.getPrettyFormat());

		Element root = new Element("Inscriptions");
		Document doc = new Document(root);

		for (Inscription i : inscriptions) {

			Element inscriptionRoot = new Element("TEI");

			Namespace ns = Namespace.getNamespace("ns", "http://www.tei-c.org/ns/1.0");
			inscriptionRoot.setAttribute(new Attribute("space", "preserve", Namespace.XML_NAMESPACE));
			inscriptionRoot.setAttribute(new Attribute("lang", "en", Namespace.XML_NAMESPACE));
			inscriptionRoot.setAttribute(new Attribute("base", "ex-epidoctemplate.xml", Namespace.XML_NAMESPACE));
			inscriptionRoot.addNamespaceDeclaration(ns);

			generateTEIHeader(i, inscriptionRoot);
			generateFacsimile(i, inscriptionRoot);
			generateBody(i, inscriptionRoot);

			root.addContent(inscriptionRoot);
		}

		return out.outputString(doc);

	}

	/**
	 * Generate the TEI header section of the XML.
	 * 
	 * @param i
	 * @param root
	 */
	private void generateTEIHeader(Inscription i, Element root) {
		Element teiHeader = new Element("teiHeader");
		Element fileDesc = new Element("fileDesc");
		Element titleStmt = new Element("titleStmt");

		Element title = new Element("title").setText(i.getAgpId()); // change this if needed
		titleStmt.addContent(title);

		Element editor = new Element("editor").setText(i.getEditor());
		titleStmt.addContent(editor);

		// Generate Responsibility Statement for Original XML from Smyrna
		if (i.getAncientCity().equals("Smyrna")) {
			Element smyRespStmt = new Element("respStmt");
			Element smyResp = new Element("resp").setText("Encoded in XML from original publication");
			Element smyName1 = new Element("name").setText("Tom Elliott");
			Element smyName2 = new Element("name").setText("David M. Ratzan");
			Element smyName3 = new Element("name").setText("Patrick J. Burns");
			Element smyName4 = new Element("name").setText("Georgios Tsolakis");
			smyRespStmt.addContent(smyResp);
			smyRespStmt.addContent(smyName1);
			smyRespStmt.addContent(smyName2);
			smyRespStmt.addContent(smyName3);
			smyRespStmt.addContent(smyName4);
			titleStmt.addContent(smyRespStmt);
		}

		// Generate Responsibility Statement for Automation of Epidoc Generation
		Element agpEpidocRespStmt = new Element("respStmt");
		Element agpEpidocResp = new Element("resp")
				.setText("Created scripts automatically generating XML according to EpiDoc Guidelines");
		agpEpidocRespStmt.addContent(agpEpidocResp);
		String[] agpEpidocNames = { "Hammad Ahmad", "Mackenzie Brooks", "Kyle Helms", "Sara Sprenkle",
				"Trevor Stalnaker" };
		for (String name : agpEpidocNames) {
			agpEpidocRespStmt.addContent(new Element("name").setText(name));
		}
		titleStmt.addContent(agpEpidocRespStmt);

		// Generate Responsibility Statement for Field Work
		if (i.getContributors() != null && !i.getContributors().equals("")) {
			String[] contributorNames = i.getContributors().split(", ");
			Element fieldWorkRespStmt = new Element("respStmt");
			Element fieldWorkResp = new Element("resp").setText("Field Work");
			fieldWorkRespStmt.addContent(fieldWorkResp);
			for (String name : contributorNames) {
				fieldWorkRespStmt.addContent(new Element("name").setText(name));
			}
			titleStmt.addContent(fieldWorkRespStmt);
		}

		// Generate Responsibility Statement for Principle Contributors
		if (i.getPrincipleContributors() != null && !i.getPrincipleContributors().equals("")) {
			String[] principleContributorNames = i.getPrincipleContributors().split(", ");
			Element princonRespStmt = new Element("respStmt");
			Element princonResp = new Element("resp").setText("Principle Contributor");
			princonRespStmt.addContent(princonResp);
			for (String name : principleContributorNames) {
				princonRespStmt.addContent(new Element("name").setText(name));
			}
			princonRespStmt.addContent(new Element("date").setText(i.getLastRevision()));
			titleStmt.addContent(princonRespStmt);
		}

		// Generate Responsibility Statement for editors of a graffito
		List<Contribution> contributions = i.getContributions();
		if (contributions != null && contributions.size() != 0) {
			for (Contribution contribution : contributions) {
				Element conEpidocRespStmt = new Element("respStmt");
				Element conEpidocResp = new Element("resp").setText(contribution.getComment());
				conEpidocRespStmt.addContent(conEpidocResp);
				conEpidocRespStmt.addContent(new Element("name").setText(contribution.getUserName()));
				conEpidocRespStmt.addContent(new Element("Date").setText(contribution.getDate()));
				titleStmt.addContent(conEpidocRespStmt);
			}
		}

		fileDesc.addContent(titleStmt);

		Element publicationStmt = new Element("publicationStmt");

		Element publisher = new Element("publisher").setText("Ancient Graffiti Project");
		Element idno = new Element("idno").setAttribute("ref", "URI");
		idno.setText("http://ancientgraffiti.org/Graffiti/graffito/AGP-" + i.getGraffitiId());

		Integer year = Calendar.getInstance().get(Calendar.YEAR);
		Element date = new Element("date");
		date.setText(year.toString());

		Element availability = new Element("availability");
		Element pAvailability = new Element("p").setText("This work is licensed under a Creative Commons "
				+ "Attribution-NonCommercial-ShareAlike 4.0 International License.");
		availability.addContent(pAvailability);

		publicationStmt.addContent(publisher);
		publicationStmt.addContent(idno);
		publicationStmt.addContent(date);
		publicationStmt.addContent(availability);
		fileDesc.addContent(publicationStmt);

		Element sourceDesc = new Element("sourceDesc");

		if (i.getAncientCity().equals("Smyrna")) {
			Element smyBibl = new Element("bibl");
			String[] smyAuthors = { "R.S. Bagnall", "R. Casagrande-Kim", "A. Ersoy", "C. Tanriver", "B. Yolaçan" };
			for (String author : smyAuthors) {
				smyBibl.addContent(new Element("name").setText(author));
			}
			Element smyTitle = new Element("title").setAttribute("level", "m")
					.setText("Graffiti from the Basilica" + " in the Agora of Smyrna");
			Element smyPublishers = new Element("publisher").setText("First published by the Institute for "
					+ "the Study of the Ancient World and the New York University Press, 2016.");
			Element smyPubPlace = new Element("pubPlace").setText("New York");
			Element smyDate = new Element("date").setText("2016");
			smyBibl.addContent(smyTitle);
			smyBibl.addContent(smyPublishers);
			smyBibl.addContent(smyPubPlace);
			smyBibl.addContent(smyDate);
			sourceDesc.addContent(smyBibl);
		}

		Element msDesc = new Element("msDesc");

		Element msIdentifier = new Element("msIdentifier");
		Element repository = new Element("repository").setText("EDR");
		Element idno_msIdentifier = new Element("idno").setText(i.getGraffitiId());

		msIdentifier.addContent(repository);
		msIdentifier.addContent(idno_msIdentifier);
		msDesc.addContent(msIdentifier);

		Element msIdentifier2 = new Element("msIdentifier");
		Element repository2 = new Element("repository").setText("AGP");
		Element idno_msIdentifier2 = new Element("idno").setText("AGP-" + i.getGraffitiId());

		msIdentifier2.addContent(repository2);
		msIdentifier2.addContent(idno_msIdentifier2);
		msDesc.addContent(msIdentifier2);

		Element physDesc = new Element("physDesc");
		Element objectDesc = new Element("objectDesc");
		Element supportDesc = new Element("supportDesc");
		Element support = new Element("support");
		if (i.getSupportDesc() != null && i.getSupportDesc().equals("")) {
			support.setText(i.getSupportDesc());
		}
		supportDesc.addContent(support);
		objectDesc.addContent(supportDesc);

		Element layoutDesc = new Element("layoutDesc");
		Element layout = new Element("layout");

		if (i.getLayoutDesc() != null && !i.getLayoutDesc().equals("")) {
			if (i.getGraffitoHeight() != null && i.getGraffitoLength() != null && !i.getGraffitoHeight().equals("")
					&& !i.getGraffitoLength().equals("")) {
				String[] layout_desc = i.getLayoutDesc().split(i.getGraffitoHeight() + " " + i.getGraffitoLength());
				layout.addContent(layout_desc[0]);
				Element dimensions = new Element("dimensions");
				Element height = new Element("height");
				Element width = new Element("width");
				height.setAttribute("unit", "centimeter");
				width.setAttribute("unit", "centimeter");
				height.setText(i.getGraffitoHeight());
				width.setText(i.getGraffitoLength());
				dimensions.addContent(height);
				dimensions.addContent(width);
				layout.addContent(dimensions);
				if (layout_desc.length > 1) {
					layout.addContent(layout_desc[1]);
				}
			}
		} else {
			Element rs = new Element("rs");
			rs.setAttribute("type", "execution");
			rs.setText(i.getWritingStyleInEnglish());
			layout.addContent(rs);
			if (Pattern.compile("\\&\\#60\\;\\:ianua\\&\\#62\\;")
					.matcher(TransformEDRContentToEpiDoc.normalize(i.getContent())).find()) {
				layout.addContent(" inscribed on either side of a doorway");
			}

			if (i.getHeightFromGround() != null && !i.getHeightFromGround().equals("")
					&& !i.getHeightFromGround().equals("null")) {
				String heightFromGround = i.getHeightFromGround();
				String[] heightValues = heightFromGround.split("-");
				if (heightValues.length == 2) {
					Element dim = new Element("dim");
					dim.setAttribute("type", "fromGround");
					dim.setAttribute("unit", "centimeter");
					dim.setAttribute("min", heightValues[0]);
					dim.setAttribute("max", heightValues[1]);
					dim.setText(heightFromGround);
					layout.addContent(dim);
				}
			}

			if (i.getGraffitoHeight() != null && i.getGraffitoLength() != null) {
				Element dimensions = new Element("dimensions");
				Element height = new Element("height");
				Element width = new Element("width");
				height.setAttribute("unit", "centimeter");
				width.setAttribute("unit", "centimeter");
				height.setText(i.getGraffitoHeight());
				width.setText(i.getGraffitoLength());
				dimensions.addContent(height);
				dimensions.addContent(width);
				layout.addContent(dimensions);
			}
		}

		layoutDesc.addContent(layout);
		objectDesc.addContent(layoutDesc);
		physDesc.addContent(objectDesc);

		Element handDesc = new Element("handDesc");

		if (i.getMinLetterHeight() != null && i.getMaxLetterHeight() != null && !i.getMinLetterHeight().equals("")
				&& !i.getMaxLetterHeight().equals("") && !i.getMinLetterHeight().equals("null")
				&& !i.getMaxLetterHeight().equals("null")) {

			Element handNote1 = new Element("handNote");

			if (i.getHandnoteDesc() != null && !i.getHandnoteDesc().equals("")) {
				String[] handNote = i.getHandnoteDesc()
						.split(i.getMinLetterHeight() + "[ ]?[,-][ ]?" + i.getMaxLetterHeight());
				handNote1.addContent(handNote[0]);
				if (handNote.length > 1) {
					Element height_handNote1 = new Element("height");
					height_handNote1.setAttribute("min", i.getMinLetterHeight());
					height_handNote1.setAttribute("max", i.getMaxLetterHeight());
					height_handNote1.setAttribute("scope", "letter");
					height_handNote1.setText(i.getMinLetterHeight() + "-" + i.getMaxLetterHeight());
					handNote1.addContent(height_handNote1);
					handNote1.addContent(handNote[1]);
				}
			} else {
				handNote1.setText("Letter heights: ");
				Element height_handNote1 = new Element("height");
				height_handNote1.setAttribute("min", i.getMinLetterHeight());
				height_handNote1.setAttribute("max", i.getMaxLetterHeight());
				height_handNote1.setAttribute("scope", "letter");
				height_handNote1.setText(i.getMinLetterHeight() + "-" + i.getMaxLetterHeight());
				handNote1.addContent(height_handNote1);
			}

			handDesc.addContent(handNote1);
		}

		// This may need to be touched up once we get actual data
		if (i.getIndividualLetterHeights() != null && !i.getIndividualLetterHeights().equals("")
				&& !i.getIndividualLetterHeights().equals("null")) {
			String letterHeights = i.getIndividualLetterHeights();
			String[] individualHeightValues = letterHeights.split("-c. |-c.| -c. | - c. | -c."); // split along any of
																									// these variations
			if (individualHeightValues.length == 2) {
				Element handNote2 = new Element("handNote").setText("[Specific letter] height: ");
				Element height_handNote2 = new Element("height");
				height_handNote2.setAttribute("min", individualHeightValues[0]);
				height_handNote2.setAttribute("max", individualHeightValues[1]);
				height_handNote2.setAttribute("scope", "individualLetter");
				height_handNote2.setText(letterHeights);
				handNote2.addContent(height_handNote2);
				handDesc.addContent(handNote2);
			}

		}

		// This will need to be worked on again after we get data and know what it looks
		// like
		if (i.getMinLetterWithFlourishesHeight() != null && i.getMaxLetterWithFlourishesHeight() != null
				&& !i.getMinLetterWithFlourishesHeight().equals("") && !i.getMaxLetterWithFlourishesHeight().equals("")
				&& !i.getMinLetterWithFlourishesHeight().equals("null")
				&& !i.getMaxLetterWithFlourishesHeight().equals("null")) {
			Element handNote3 = new Element("handNote").setText("Flourish height: ");
			Element height_handNote3 = new Element("height");
			height_handNote3.setAttribute("min", i.getMinLetterWithFlourishesHeight());
			height_handNote3.setAttribute("max", i.getMaxLetterWithFlourishesHeight());
			height_handNote3.setAttribute("scope", "flourishLetter");
			height_handNote3.setText(Double.toString(Double.valueOf(i.getMinLetterWithFlourishesHeight())
					- Double.valueOf(i.getMaxLetterWithFlourishesHeight())));
			handNote3.addContent(height_handNote3);
			handDesc.addContent(handNote3);
		}

		physDesc.addContent(handDesc);

		if (i.hasFiguralComponent()) {
			Element decoDesc = new Element("decoDesc");
			Element decoNote1 = new Element("decoNote").setText(i.getFiguralInfo().getDescriptionInLatin());
			Element decoNote2 = new Element("decoNote").setText(i.getFiguralInfo().getDescriptionInEnglish());
			;
			decoNote1.setAttribute("lang", "la", Namespace.XML_NAMESPACE);
			decoNote2.setAttribute("lang", "en", Namespace.XML_NAMESPACE);
			decoDesc.addContent(decoNote1);
			decoDesc.addContent(decoNote2);
			physDesc.addContent(decoDesc);
		}

		msDesc.addContent(physDesc);

		Element history = new Element("history");
		Element origin = new Element("origin");
		Element origPlace = new Element("origPlace");
		Element placeName = new Element("placeName").setText(i.getAncientCity());
		if (!i.getAncientCity().equals("Smyrna")) {
			placeName.setAttribute("ref",
					"https://pleiades.stoa.org/places/" + i.getProperty().getInsula().getCity().getPleiadesId());
		} else {
			placeName.setAttribute("ref", "https://pleiades.stoa.org/places/550771");
		}

		origPlace.addContent(placeName);
		// "ancientgraffiti.org/Graffiti/property/" +
		// i.getProperty().getInsula().getCity().getName() +
		// "/" + i.getProperty().getInsula().getShortName() + "/" +
		// i.getProperty().getPropertyNumber()
		Element origDate = new Element("origDate");
		String dateBeginning, dateEnd;
		String notBefore = "";
		String notAfter = "";
		dateBeginning = i.getDateBeginning();
		dateEnd = i.getDateEnd();
		if (dateBeginning == null || dateEnd == null) {
			origDate.setText("unknown");
		} else {
			boolean inBC = false;

			// map the Italion explanations for date to English translations
			Map<String, String> translations = new HashMap<String, String>();
			translations.put("archaeologia", "archaeological context");
			translations.put("formulae", "terminology");
			translations.put("historia, antiquitates", "historical context");
			translations.put("lingua", "terminology");
			translations.put("nomina", "onomastics");
			translations.put("palaeographia", "palaeography");
			translations.put("prosopographia", "prosopography");

			StringBuilder dateExplanation = new StringBuilder();

			if (i.getDateExplanation() != null) {
				String[] dateExplanations = i.getDateExplanation().split("\\;\\s*");
				for (int index = 0; index < dateExplanations.length - 1; index++) {
					String str = dateExplanations[index];
					if (translations.get(str) != null) {
						dateExplanation.append(translations.get(str) + " and ");
					}
				}
				if (translations.get(dateExplanations[dateExplanations.length - 1]) != null) {
					dateExplanation.append(translations.get(dateExplanations[dateExplanations.length - 1]));
				}
			}

			// -40, for example, means 40 B.C.
			if (dateBeginning.contains("-") && dateEnd.contains("-")) {
				// remove the negative signs
				dateBeginning = dateBeginning.replace("-", "");
				dateEnd = dateEnd.replace("-", "");
				inBC = true;
			}

			if (dateBeginning != null) {
				if (dateBeginning.length() == 1) {
					notBefore = "000" + dateBeginning;
				} else if (dateBeginning.length() == 2) {
					notBefore = "00" + dateBeginning;
				} else {
					notBefore = dateBeginning;
				}
				if (dateEnd.length() == 1) {
					notAfter = "000" + dateEnd;
				} else if (dateEnd.length() == 2) {
					notAfter = "00" + dateEnd;
				} else {
					notAfter = dateEnd;
				}
			}

			origDate.setAttribute("notBefore-custom", notBefore).setAttribute("notAfter-custom", notAfter);
			if (!dateExplanation.toString().equals("")) {
				origDate.setAttribute("evidence", dateExplanation.toString().replaceAll(" ", "_"));
			}
			origDate.setAttribute("datingMethod", "#julian");
			if (inBC) {
				origDate.setText(dateBeginning + "-" + dateEnd + " B.C.");
			} else {
				origDate.setText(dateBeginning + "-" + dateEnd + " C.E.");
			}

		}
		origin.addContent(origPlace);
		origin.addContent(origDate);
		history.addContent(origin);

		Element provenance1 = new Element("provenance");
		provenance1.setAttribute("type", "found");
		Element provName = new Element("placeName");
		String provenanceName = "";
		if (i.getOnFacade()) {
			if (i.getSegment() != null) {
				provName.setAttribute("ref", SEGMENT_URI_BASE + i.getSegment().getUri());
				provenanceName = i.getSegment().getStreet().getCity().getName() + ", "
						+ i.getSegment().getStreet().getStreetName() + "(" + i.getSegment().getSegmentName() + ")";
			}
		} else {
			provName.setAttribute("ref", PROPERTIES_URI_BASE + i.getProperty().getInsula().getCity().getName() + "/"
					+ i.getProperty().getInsula().getShortName() + "/" + i.getProperty().getPropertyNumber());
			if (!i.getAncientCity().equals("Smyrna")) {
				provenanceName = i.getAncientCity() + ", " + i.getProperty().getInsula().getFullName() + "."
						+ i.getProperty().getPropertyNumber();
				if (!i.getProperty().getPropertyName().equals("") && i.getProperty().getPropertyName() != null) {
					provenanceName += ", " + i.getProperty().getPropertyName();
				}
			} else {
				provenanceName = "Basilica of the Agora of Smyrna, Izmir, Turkey";
			}
		}
		provName.setText(provenanceName);
		provenance1.addContent(provName);
		if (!i.getAncientCity().equals("Smyrna")) {
			provenance1.addContent(i.getSourceFindSpot());
		}
		history.addContent(provenance1);

//		Element provenance2 = new Element("provenance");
//		if(i.getAncientCity().equals("Pompeii")) {
//			provenance2.setText("Pompei");
//		} else if(i.getAncientCity().equals("Herculaneum")) {
//			provenance2.setText("Ercolano");
//		} else if(i.getAncientCity().equals("Smyrna")) {
//			provenance2.setText("Izmir, Turkey");
//		}

//		provenance2.setAttribute("type", "observed");
//		history.addContent(provenance2);
		msDesc.addContent(history);

		sourceDesc.addContent(msDesc);
		fileDesc.addContent(sourceDesc);
		teiHeader.addContent(fileDesc);

		// Add Calendar Information
		Element profileDesc = new Element("profileDesc");
		Element calendarDesc = new Element("calendarDesc");
		Element calendar = new Element("calendar");
		calendar.setAttribute("id", "julian", Namespace.XML_NAMESPACE);
		Element p_cal = new Element("p").setText("Julian calendar");
		calendar.addContent(p_cal);
		calendarDesc.addContent(calendar);
		profileDesc.addContent(calendarDesc);
		teiHeader.addContent(profileDesc);

		root.addContent(teiHeader);
	}

	/**
	 * Generate the facsimilie section of the XML.
	 * 
	 * @param i
	 * @param root
	 */
	private void generateFacsimile(Inscription i, Element root) {
		List<String> images = i.getImages(); // get all image URLs
		Element facsimilie = new Element("facsimilie");
		for (String url : images) {
			Element graphic = new Element("graphic").setAttribute("url", url);
			facsimilie.addContent(graphic);
		}
		root.addContent(facsimilie);
	}

	/**
	 * Generate the body section of the XML.
	 * 
	 * @param i
	 * @param root
	 */
	private void generateBody(Inscription i, Element root) {
		Element text = new Element("text");
		Element body = new Element("body");
		Element div1 = new Element("div");
		div1.setAttribute("type", "edition");
		div1.setAttribute("space", "preserve", Namespace.XML_NAMESPACE);
		String lang = i.getLanguageInEnglish();
		String langTag = "";
		if (lang != null && !lang.equals("")) {
			String[] languages = lang.split("/");
			for (int k = 0; k < languages.length; k++) {
				if (languages[k].equals("Latin")) {
					langTag += "la";
				}
				if (languages[k].equals("Greek")) {
					langTag += "grk";
				}
				if (k < languages.length - 1) {
					langTag += " ";
				}
			}
			// This should be the language the inscription is written in
			div1.setAttribute("lang", langTag, Namespace.XML_NAMESPACE);
		}

		// Use SAXBuilder + StringReader to turn the string content into XML elements
		SAXBuilder contentBuilder = new SAXBuilder();
		String epidoc = null;
		try {
			// Use to hotwire code and generate Epidocs on the fly
			// System.out.println(TransformEDRContentToEpiDoc.transformContentToEpiDoc(i.getContent()));
			// Document tempDoc = contentBuilder.build(new StringReader("<ab>" +
			// TransformEDRContentToEpiDoc.transformContentToEpiDoc(i.getContent())
			// +"</ab>"));
			epidoc = i.getEpidoc();
			Document tempDoc = contentBuilder.build(new StringReader("<ab>" + epidoc + "</ab>"));
			Element temp = tempDoc.detachRootElement();
			div1.addContent(temp);
		} catch (JDOMException e) {
			System.err.println("Error epidocifying: " + i.getAgpId());
			System.err.println(epidoc);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		body.addContent(div1);

		Element div2 = new Element("div").setAttribute("type", "apparatus");
		Element p_div2 = new Element("p").setText(i.getApparatus());
		div2.addContent(p_div2);
		body.addContent(div2);

		Element div3 = new Element("div").setAttribute("type", "translation");
		Element p_div3 = new Element("p").setText(i.getContentTranslation());
		div3.addContent(p_div3);
		body.addContent(div3);

		Element div4 = new Element("div").setAttribute("type", "commentary");
		Element p_div4 = new Element("p").setText(i.getCommentary());
		div4.addContent(p_div4);
		body.addContent(div4);

		Element div5 = new Element("div").setAttribute("type", "bibliography");
		Element p_div5 = new Element("p").setText(i.getBibliography());
		div5.addContent(p_div5);
		body.addContent(div5);

		Element div6 = new Element("div").setAttribute("type", "summary");
		Element p_div6 = new Element("p").setText(i.getCaption());
		div6.addContent(p_div6);
		body.addContent(div6);

		text.addContent(body);
		root.addContent(text);
	}

}
