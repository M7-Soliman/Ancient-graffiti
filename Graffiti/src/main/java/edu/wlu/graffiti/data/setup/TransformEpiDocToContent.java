package edu.wlu.graffiti.data.setup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * Transforms nested xml tags / nodes into EDR style content
 * 
 * @author Trevor Stalnaker
 * 
 *
 */
public class TransformEpiDocToContent {

	private static Map<String, String> alphaNumeral = new HashMap<String, String>();

	static {
		createAlphaToNumeralMap();
	}

	// Refer to Epidoc Content Conversion Document for Translation Information
	public static String translateContent(Node node, EpiDocReader reader) {
		String translation = "";

		// Plain text nodes
		// Cleans excess white space, but leaves single spaces
		if (node.getNodeName().equals("#text")) {
			if (node.getTextContent().equals(" ")) {
				return " ";
			}
			return trimWhiteSpace(node.getTextContent());
		}

		String tag = ((Element) node).getTagName();

		// Deal with the case of an ab
		if (tag.equals("ab")) {
			List<Node> nodes = reader.getDirectChildren(node);
			for (Node child : nodes) {
				translation += translateContent(child, reader);
			}
			return translation;
		}

		// Handles Columns and alternative use of div in Smyrna EpiDocs
		if (tag.equals("div")) {
			if (reader.hasAttribute(node, "subtype")
					&& reader.getAttributeValueForNode(node, "subtype").equals("column")) {
				String level = convertFromAlphaToNumeral(reader.getAttributeValueForNode(node, "n"));
				translation += "\n&#60;:columna " + level + "&#62;\n"; // Add the first newline, because otherwise the
																		// code will exclude it
			}
			// if (reader.hasAttribute(node, "subtype") &&
			// reader.getAttributeValueForNode(node, "subtype").equals("section")) {
			// TO-DO (Necessary for Smyrna EpiDocs)
			// }
			List<Node> nodes = reader.getDirectChildren(node);
			for (Node child : nodes) {
				translation += translateContent(child, reader);
			}
			return translation;
		}

		// Newlines and line breaks
		if (tag.equals("lb")) {
			Node parent = node.getParentNode().getParentNode();
			// Special case for lines written vertically
			if (reader.getAttributeValueForNode(node, "style").equals("text-direction:vertical")) {
				return "&#60;:ad perpendiculum>\n";
			}
			String retString = "";
			if (!reader.getAttributeValueForNode(node, "n").equals("1")) {
				if (reader.getAttributeValueForNode(node, "break").equals("no")) {
					retString += "=";
				}
				retString += "\n";
			}
			// This adds newlines for div subsections, excluding the first one
			else if (reader.hasAttribute(parent, "n") && (!reader.getAttributeValueForNode(parent, "n").equals("1"))) {
				retString += "\n";

			}
			return retString;

		}

		// Spaces left blank
		if (tag.equals("space")) {
			// Space caused by a door
			if (reader.hasAttribute(node, "type") && reader.getAttributeValueForNode(node, "type").equals("door")) {
				return "&#60;:ianua&#62;";
			}
			// Spaces left intentionally blank
			return " &#60;:vacat&#62; ";
		}

		// Puts dots under characters that are marked as unclear
		// Dot character: '\u0323' Note: If these are printed in the console they may
		// appear one off, but they work on the site
		if (tag.equals("unclear")) {
			String tempstr = node.getTextContent();
			for (int i = 0; i < tempstr.length(); i++) {
				translation += (tempstr.charAt(i) + "\u0323");
			}
			return translation;
		}

		// Notation for nodes marked as supplied
		if (tag.equals("supplied")) {
			if (reader.getAttributeValueForNode(node, "reason").equals("lost")) {
				// Put [] around characters
				translation = "[";
				List<Node> recurseNodes = reader.getDirectChildren(node);
				for (Node recurseChild : recurseNodes) {
					translation += translateContent(recurseChild, reader);
				}
				if (reader.hasAttribute(node, "cert")) {
					if (reader.getAttributeValueForNode(node, "cert").equals("low")) {
						translation += "?]";
					}
				} else {
					translation += "]";
				}
				return translation;
			}
			if (reader.getAttributeValueForNode(node, "reason").equals("undefined")) {
				// Underline characters -- Macron Symbol: '\u0331' or combining low line \u0332
				String tempstr = node.getTextContent();
				for (int i = 0; i < tempstr.length(); i++) {
					translation += (tempstr.charAt(i) + "\u0332");
				}
				return translation;
			}
			if (reader.getAttributeValueForNode(node, "reason").equals("subaudible")) {
				return "&#60;:" + node.getTextContent() + "&#62;";
			}
			if (reader.getAttributeValueForNode(node, "reason").equals("omitted")) {
				return "&#60;" + node.getTextContent() + "&#62;";
			}
		}

		// Inserts gap notation for areas marked as a gap
		if (tag.equals("gap")) {
			if (reader.getAttributeValueForNode(node, "reason").equals("lost")) {
				// Put [---]
				if (reader.getAttributeValueForNode(node, "unit").equals("character")) {
					translation = "[";
					if (reader.hasAttribute(node, "quantity")) {
						if (reader.hasAttribute(node, "precision")) {
							translation += "+" + reader.getAttributeValueForNode(node, "quantity") + "?+";
						} else {
							// Dot character to replace '-' with '•' once the classicists are ready
							int quant = Integer.parseInt(reader.getAttributeValueForNode(node, "quantity"));
							for (int x = 0; x < quant; x++) {
								translation += "-";
							}
						}
					} else {
						translation += "---";
					}
					translation += "]";
					return translation;
				}
				if (reader.getAttributeValueForNode(node, "unit").equals("line")) {
					if (reader.hasAttribute(node, "extent")
							&& reader.getAttributeValueForNode(node, "extent").equals("unknown")) {
						return translation + "\n- - - - - -";
					} else {
						int ex = Integer.parseInt(reader.getAttributeValueForNode(node, "extent"));
						for (int x = 0; x < ex; x++) {
							translation += "[------]\n";
						}
						return translation;
					}

				}
			}
			if (reader.getAttributeValueForNode(node, "reason").equals("illegible")) {
				if (reader.hasAttribute(node, "extent")) {
					if (reader.getAttributeValueForNode(node, "extent").equals("unknown")) {
						return "&#60;:textus non legitur&#62;";
					}
				}
				if (reader.hasAttribute(node, "quantity")) {
					int quant = Integer.parseInt(reader.getAttributeValueForNode(node, "quantity"));
					for (int i = 0; i < quant; i++) {
						translation += "+";
					}
				}
				return translation;
			}
		}

		// Notations for nodes marked expan
		if (tag.equals("expan")) {
			Boolean symbol = false;
			List<Node> nodes = reader.getDirectChildren(node);
			for (Node child : nodes) {
				// The part of the abbreviation outside of parens (if not a symbol)
				if (child.getNodeName().equals("#text")) {
					translation += translateContent(child, reader);
				} else if (((Element) child).getTagName().equals("abbr")) {
					// Check if abbr tag has any children at all (there are a few cases where it
					// doesn't)
					if (child.hasChildNodes()) {
						// Check if the child element of the child node is text
						if (!reader.getDirectChildren(child).get(0).getNodeName().equals("#text")) {
							// Determine if child element should be represented as a symbol
							if (((Element) reader.getDirectChildren(child).get(0)).getTagName().equals("am")) {
								symbol = true;
							}
							// Recurse through any nested nodes within abbr tag
							else {
								List<Node> grandChildren = reader.getDirectChildren(child);
								for (Node grandChild : grandChildren) {
									translation += translateContent(grandChild, reader);
								}
							}
						}
						// Child is text and this is a basic abbreviation
						else {
							List<Node> recurseNodes = reader.getDirectChildren(child);
							for (Node recurseChild : recurseNodes) {
								translation += translateContent(recurseChild, reader);
							}
						}
					}
				}
				// Part of the abbreviation within parens (if not a symbol)
				else if (((Element) child).getTagName().equals("ex")) {
					// Return notation for symbols
					if (symbol) {
						return "((" + child.getTextContent() + "))";
					}
					// Construct Notation for abbreviations
					else {
						translation += "(";
						List<Node> recurseNodes = reader.getDirectChildren(child);
						for (Node recurseChild : recurseNodes) {
							translation += translateContent(recurseChild, reader);
						}
						// Check for uncertainty, represented with a '?'
						if (reader.getAttributeValueForNode(child, "cert").equals("low")) {
							// Add a question mark
							translation += "?";
						}
						translation += ")";
					}
				}
			}
			return translation;
		}

		// Notation for nodes marked del
		if (tag.equals("del")) {
			NodeList nodes = node.getChildNodes();
			translation = "〚";
			for (int i = 0; i < nodes.getLength(); i++) {

				// Handles the case where text is not embedded in a supplied tag
				// This check has to happen first to prevent an error
				if (nodes.item(i).getNodeName().equals("#text")) {
					translation += translateContent(nodes.item(i), reader);
				}

				// Handles the normal case where interior text is inside a supplied tag
				else if (((Element) nodes.item(i)).getTagName().equals("supplied")) {
					List<Node> recurseNodes = reader.getDirectChildren(nodes.item(i));
					for (Node recurseChild : recurseNodes) {
						translation += translateContent(recurseChild, reader);
					}
				}
				// Handles other tags that may appear within the del tag
				else {
					translation += translateContent(nodes.item(i), reader);
				}
			}
			translation += "〛";
			return translation;
		}

		// Notation for nodes marked as a figure
		if (tag.equals("figure")) {
			NodeList nodes = node.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				if (((Element) nodes.item(i)).getTagName().equals("figDesc")) {
					// Put text in ((:abc))
					translation += "((:";
					List<Node> recurseNodes = reader.getDirectChildren(nodes.item(i));
					for (Node recurseChild : recurseNodes) {
						translation += translateContent(recurseChild, reader);
					}
					translation += "))";
				}
			}
			return translation;
		}

		// Notation for nodes marked as graphical
		if (tag.equals("g")) {
			translation = "((:" + reader.getAttributeValueForNode(node, "type") + "))";
		}

		// Notation for nodes marked orig
		if (tag.equals("orig")) {
			// Capitalize all letters within this tag
			List<Node> recurseNodes = reader.getDirectChildren(node);
			for (Node recurseChild : recurseNodes) {
				translation += translateContent(recurseChild, reader);
			}
			return translation.toUpperCase();
		}

		// Notation for the choice tag
		if (tag.equals("choice")) {
			List<Node> nodes = reader.getDirectChildren(node);
			String end = "";
			for (Node child : nodes) {
				// Deal with random white space (occurs in some of the Smyrna EpiDocs)
				if (child.getNodeName().equals("#text")) {
					translation += translateContent(child, reader);
				} else if (((Element) child).getTagName().equals("reg")) {
					end += " (:";
					if (reader.hasAttribute(child, "cert")) {
						if (reader.getAttributeValueForNode(child, "cert").equals("low")) {
							end += child.getTextContent() + "?";
						}
					} else {
						List<Node> recurseNodes = reader.getDirectChildren(child);
						for (Node recurseChild : recurseNodes) {
							end += translateContent(recurseChild, reader);
						}
					}
					end += ")";

				} else if (((Element) child).getTagName().equals("orig")) {
					List<Node> recurseNodes = reader.getDirectChildren(child);
					for (Node recurseChild : recurseNodes) {
						translation += translateContent(recurseChild, reader);
					}
					translation += end;
				}
			}
			return translation;
		}

		// Notation for nodes marked app
		if (tag.equals("app")) {
			List<Node> nodes = reader.getDirectChildren(node);
			for (Node child : nodes) {
				// Deal with random white space (occurs in some of the Smyrna EpiDocs)
				if (child.getNodeName().equals("#text")) {
					translation += translateContent(child, reader);
				} else if (((Element) child).getTagName().equals("lem")) {
					// translation += child.getTextContent();
					translation += translateContent(child, reader);
				} else if (((Element) child).getTagName().equals("rdg")) {
					// translation += " (or " + child.getTextContent() + ")";
					translation += " (or " + translateContent(child, reader) + ")";
				}
			}
			return translation;
		}

		// Handles tags that are intermediary
		if (tag.equals("lem") || tag.equals("rdg")) {
			List<Node> nodes = reader.getDirectChildren(node);
			for (Node child : nodes) {
				translation += translateContent(child, reader);
			}
			return translation;
		}

		// Notation for nodes marked hi
		String tempstr = node.getTextContent();
		if (tag.equals("hi")) {
			// Add a carot to characters in this tag
			if (reader.getAttributeValueForNode(node, "rend").equals("ligature")) {
				for (int i = 0; i < tempstr.length(); i++) {
					translation += tempstr.charAt(i);
					if (i < tempstr.length() - 1) {
						translation += "\u0302";
					}
				}
			}
			// Add superscript markup if the character is a superscript
			if (reader.getAttributeValueForNode(node, "rend").equals("superscript")) {
				translation = "<sup>" + tempstr + "</sup>";
			}
			return translation;
		}

		// Notation for nodes marked surplus
		if (tag.equals("surplus")) {
			return "{" + node.getTextContent() + "}";
		}

		// Notation for nodes marked as abbreviations (the expansion of which is
		// unknown)
		if (tag.equals("abbr")) {
			return node.getTextContent() + "(---)";
		}

		// Handles the persName tag
		if (tag.equals("persName")) {
			Node child = reader.getDirectChildren(node).get(0);
			List<Node> nodes = reader.getDirectChildren(child);
			for (Node grandchild : nodes) {
				translation += translateContent(grandchild, reader);
			}
			return translation;
		}

		// Handles the placeName tag
		if (tag.equals("placeName")) {
			List<Node> nodes = reader.getDirectChildren(node);
			for (Node child : nodes) {
				translation += translateContent(child, reader);
			}
			return translation;
		}

		// Handles the geogName tag
		if (tag.equals("geogName")) {
			List<Node> nodes = reader.getDirectChildren(node);
			for (Node child : nodes) {
				translation += translateContent(child, reader);
			}
			return translation;
		}

		// Handles the w tag
		if (tag.equals("w")) {
			List<Node> nodes = reader.getDirectChildren(node);
			translation += " ";
			for (Node child : nodes) {
				translation += translateContent(child, reader);
			}
			return translation;
		}

		// Handles the num tag
		if (tag.equals("num")) {
			List<Node> nodes = reader.getDirectChildren(node);
			for (Node child : nodes) {
				translation += translateContent(child, reader);
			}
			return translation;
		}

		// return an empty string if node matches none of the above
		return translation;
	}

	/**
	 * Builds a map used for the conversion between alphabet characters and numerals
	 */
	private static void createAlphaToNumeralMap() {
		String[] alphabet = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r",
				"s", "t", "u", "v", "w", "x", "y", "z" };
		String[] numerals = { "I", "II", "III", "VI", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII", "XIV",
				"XV", "XVI", "XVII", "XVIII", "XIX", "XX", "XXI", "XXII", "XXIII", "XXIV", "XXV", "XXVI" };
		for (int i = 0; i < alphabet.length; i++) {
			alphaNumeral.put(alphabet[i], numerals[i]);
		}
	}

	/**
	 * Converts alphabetic characters in column information to roman numerals
	 * 
	 * @param str
	 * @return
	 */
	private static String convertFromAlphaToNumeral(String str) {
		// createAlphaToNumeralMap();
		return alphaNumeral.get(str);
	}

	/**
	 * Trim white space, but preserve space on sides of end words
	 * 
	 * @param str
	 * @return
	 */
	private static String trimWhiteSpace(String str) {
		String retString = "";
		if (!str.trim().equals("")) {
			if (str.charAt(0) == ' ') {
				retString += " ";
			}
			retString += str.trim();
			if (str.charAt(str.length() - 1) == ' ') {
				retString += " ";
			}
		}
		return retString;
	}

	/**
	 * Preserve the parts of the epidoc that we don't save in our database, or would
	 * otherwise be lost
	 * 
	 * @param content
	 * @return
	 */
//		private static String preserveEpidoc(String content) {
//			String epidoc = TransformEDRContentToEpiDoc.transformContentToEpiDoc(content);
//			// Restore Name Tags to Content
//			Node contentNode = getNodeByTagAndAttribute("div", "type", "edition");
//			ArrayList<Node> names = getNodesByTagAndAncestor("persName", contentNode);
//			String nymRef = "", persName_type = "", name_type = "", personName = "";
//			if (names != null && names.size() != 0) {
//				for (Node name : names) {
//					ArrayList<Node> children = getDirectChildrenExcludingText(name);
//					persName_type = getAttributeValueForNode(name, "type");
//					for (Node child : children) {
//						String tag = ((Element) child).getTagName();
//						if (tag.equals("name")) {
//							nymRef = getAttributeValueForNode(child, "nymRef");
//							name_type = getAttributeValueForNode(child, "type");
//							personName = TransformEDRContentToEpiDoc.transformContentToEpiDoc(translateContent(name))
//									.replaceAll("<lb n='1'/>", "");
//							// personName = child.getTextContent(); //Text located within tags
//						}
//					}
//					// Convert the contents of the node to text, then to epidoc, remove new line,
//					// and finally use to replace
//					epidoc = epidoc.replace(personName, "<persName type='" + persName_type + "'><name nymRef='" + nymRef
//							+ "' type='" + name_type + "'>" + personName + "</name></persName>");
//				}
//			}
//			// Restore Place Tags to Content
//			ArrayList<Node> places = getNodesByTagAndParent("placeName", "ab");
//			String lemma = "", idno = "", idno_type = "", placeName = "";
//			if (places != null && places.size() != 0) {
//				for (Node place : places) {
//					ArrayList<Node> children = getDirectChildrenExcludingText(place);
//					for (Node child : children) {
//						String tag = ((Element) child).getTagName();
//						if (tag.equals("w")) {
//							lemma = getAttributeValueForNode(child, "lemma");
//							placeName = TransformEDRContentToEpiDoc.transformContentToEpiDoc(translateContent(place))
//									.replaceAll("<lb n='1'/>", "");
//							// placeName = child.getTextContent(); //Text located within tags
//						}
//						if (tag.equals("idno")) {
//							idno = child.getTextContent();
//							idno_type = getAttributeValueForNode(child, "type");
//						}
//					}
//					// Convert the contents of the node to text, then to epidoc, remove new line,
//					// and finally use to replace
//
//					epidoc = epidoc.replaceAll(placeName, "<placeName><w lemma='" + lemma + "'>" + placeName
//							+ "</w><idno type='" + idno_type + "'>" + idno + "</idno></placeName>");
//				}
//			}
//			// Restore GeoName Tags to Content
//			ArrayList<Node> geoNames = getNodesByTagAndParent("geogName", "ab");
//			String type = "", geogName = "";
//			if (geoNames != null && geoNames.size() != 0) {
//				for (Node name : geoNames) {
//					type = getAttributeValueForNode(name, "type");
//					// Convert the contents of the node to text, then to epidoc, remove new line,
//					// and finally use to replace
//					geogName = TransformEDRContentToEpiDoc.transformContentToEpiDoc(translateContent(name))
//							.replaceAll("<lb n='1'/>", "");
//					epidoc = epidoc.replace(geogName, "<geogName type='" + type + "'>" + geogName + "</geogName>");
//				}
//			}
//			// Restore plain W Tags to Content
//			ArrayList<Node> wTags = getNodesByTagAndParent("w", "ab");
//			String wlemma = "", contents = "";
//			if (wTags != null && wTags.size() != 0) {
//				for (Node name : wTags) {
//					wlemma = getAttributeValueForNode(name, "lemma");
//					// Convert the contents of the node to text, then to epidoc, remove new line,
//					// and finally use to replace
//					contents = TransformEDRContentToEpiDoc.transformContentToEpiDoc(translateContent(name))
//							.replaceAll("<lb n='1'/>", "");
//					epidoc = epidoc.replace(contents, "<w lemma='" + wlemma + "'>" + contents + "</w>");
//				}
//			}
//			return epidoc;
//		}
}
