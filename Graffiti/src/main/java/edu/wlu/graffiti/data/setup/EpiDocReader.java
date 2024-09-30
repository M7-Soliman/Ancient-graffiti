package edu.wlu.graffiti.data.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author Trevor Stalnaker
 * 
 *         A helper class that parses through EpiDoc content
 *
 */
public class EpiDocReader {

	private List<Node> allRootsChildren;

	public EpiDocReader(Document dom) throws ParserConfigurationException, SAXException {
		// Get the root node of the XML file (a <TEI> tag)
		Node root = dom.getElementsByTagName("TEI").item(0); // This needs to be revisited

		// Get an arraylist of all of the root's children
		allRootsChildren = returnAllChildNodesExcludingText(root);
	}

	public EpiDocReader(Document dom, String tag) throws ParserConfigurationException, SAXException {
		// Get the root node of the XML file (a <TEI> tag)
		Node root = dom.getElementsByTagName(tag).item(0); // This needs to be revisited

		// Get an arraylist of all of the root's children
		allRootsChildren = returnAllChildNodesExcludingText(root);
	}

	public void setRoot(Document dom, String tag) {
		Node root = dom.getElementsByTagName(tag).item(0);
		allRootsChildren = returnAllChildNodesExcludingText(root);
	}

	/**
	 * Return the node with the given tag
	 * 
	 * @param tag
	 * @return
	 */
	public Node getNodeByTag(String tag) {
		for (Node node : allRootsChildren) {
			if (((Element) node).getTagName().equals(tag)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * Return the node with the given tag and attribute/value pair
	 * 
	 * @param tag
	 * @param attr
	 * @param val
	 * @return
	 */
	public Node getNodeByTagAndAttribute(String tag, String attr, String val) {
		for (Node node : allRootsChildren) {
			if (((Element) node).getTagName().equals(tag)) {
				if (hasAttribute(node, attr) && getAttributeValueForNode(node, attr).equals(val)) {
					return node;
				}
			}
		}
		return null;
	}

	/**
	 * Return the node with the given tag and parent tag
	 * 
	 * @param tag
	 * @param parent
	 * @return
	 */
	public Node getNodeByTagAndParent(String tag, String parent) {
		for (Node node : allRootsChildren) {
			if (((Element) node).getTagName().equals(parent)) {
				NodeList nodes = node.getChildNodes();
				for (int i = 0; i < nodes.getLength(); i++) {
					if (!nodes.item(i).getNodeName().equals("#text")
							&& ((Element) nodes.item(i)).getTagName().equals(tag)) {
						return nodes.item(i);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Return the node with the given tag and parent tag with the provided
	 * attribute/value pair
	 * 
	 * @param tag
	 * @param parent
	 * @param attr
	 * @param val
	 * @return
	 */
	public Node getNodeByTagAndParentAttribute(String tag, String parent, String attr, String val) {
		for (Node node : allRootsChildren) {
			if (((Element) node).getTagName().equals(parent)) {
				NodeList nodes = node.getChildNodes();
				for (int i = 0; i < nodes.getLength(); i++) {
					if (!nodes.item(i).getNodeName().equals("#text")
							&& ((Element) nodes.item(i)).getTagName().equals(tag) && hasAttribute(node, attr)
							&& getAttributeValueForNode(node, attr).equals(val)) {
						return nodes.item(i);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Return all nodes with a given tag
	 * 
	 * @param tag
	 * @return
	 */
	public ArrayList<Node> getNodesByTag(String tag) {
		ArrayList<Node> returnLyst = new ArrayList<Node>();
		for (Node node : allRootsChildren) {
			if (((Element) node).getTagName().equals(tag)) {
				returnLyst.add(node);
			}
		}
		return returnLyst;
	}

	/**
	 * Return all nodes with a given tag and attribute
	 * 
	 * @param tag
	 * @return
	 */
	public ArrayList<Node> getNodesByTagAndAttribute(String tag, String attr, String val) {
		ArrayList<Node> returnLyst = new ArrayList<Node>();
		for (Node node : allRootsChildren) {
			if (((Element) node).getTagName().equals(tag)) {
				if (hasAttribute(node, attr) && getAttributeValueForNode(node, attr).equals(val)) {
					returnLyst.add(node);
				}
			}
		}
		return returnLyst;
	}

	/**
	 * Return all nodes with a given tag and ancestor (not the same as direct
	 * parent)
	 * 
	 * @param tag
	 * @param ancestor
	 * @return
	 */
	public List<Node> getNodesByTagAndAncestor(String tag, Node ancestor) {
		List<Node> children = returnAllChildNodesExcludingText(ancestor);
		List<Node> returnLyst = new ArrayList<Node>();
		for (Node node : children) {
			if (((Element) node).getTagName().equals(tag)) {
				returnLyst.add(node);
			}
		}
		return returnLyst;
	}

	/**
	 * Return all nodes with a given tag and ancestor (not the same as direct
	 * parent)
	 * 
	 * @param tag
	 * @param ancestor
	 * @return
	 */
	public List<Node> getNodesByTagAtrributeAndAncestor(String tag, String attr, String val, Node ancestor) {
		List<Node> children = returnAllChildNodesExcludingText(ancestor);
		List<Node> returnLyst = new ArrayList<Node>();
		for (Node node : children) {
			if (((Element) node).getTagName().equals(tag)) {
				if (hasAttribute(node, attr) && getAttributeValueForNode(node, attr).equals(val)) {
					returnLyst.add(node);
				}
			}
		}
		return returnLyst;
	}

	// Return all nodes with the given tag and parent tag
	public ArrayList<Node> getNodesByTagAndParent(String tag, String parent) {
		ArrayList<Node> returnLyst = new ArrayList<Node>();
		for (Node node : allRootsChildren) {
			if (((Element) node).getTagName().equals(parent)) {
				NodeList nodes = node.getChildNodes();
				for (int i = 0; i < nodes.getLength(); i++) {
					if (!nodes.item(i).getNodeName().equals("#text")
							&& ((Element) nodes.item(i)).getTagName().equals(tag)) {
						returnLyst.add(nodes.item(i));
					}
				}
			}
		}
		return returnLyst;
	}

	/** Methods for returning text from nodes */

	// Returns the text from a node or the empty string
	public String getTextFromNode(Node node) {
		if (node != null) {
			return node.getTextContent();
		}
		return "";
	}

	// Returns the text from the node with the given tag
	public String getTextFromTag(String tag) {
		return getTextFromNode(getNodeByTag(tag));
	}

	// Returns the text from the node with the given tag and parent tag
	public String getTextFromTagAndParent(String tag, String parent) {
		return getTextFromNode(getNodeByTagAndParent(tag, parent));
	}

	/**
	 * Returns the text from the node with the given tag and parent tag with the
	 * provided attribute/value pair
	 * 
	 * @param tag
	 * @param parent
	 * @param attr
	 * @param val
	 * @return
	 */
	public String getTextFromTagAndParentAttribute(String tag, String parent, String attr, String val) {
		return getTextFromNode(getNodeByTagAndParentAttribute(tag, parent, attr, val));
	}

	/**
	 * 
	 * @param tag
	 * @param attr
	 * @param value
	 * @return
	 */
	public String getTextFromTagWithAttribute(String tag, String attr, String value) {
		return getTextFromNode(getNodeByTagAndAttribute(tag, attr, value));
	}

	/**
	 * Checks if a node has the given attribute (Doesn't check the value of that
	 * attribute)
	 * 
	 * @param node
	 * @param attr
	 * @return
	 */
	public boolean hasAttribute(Node node, String attr) {
		NamedNodeMap attributes = node.getAttributes();
		if (attributes.getLength() == 0) {
			return false;
		}
		for (int i = 0; i < attributes.getLength(); i++) {
			String attrStr = attributes.item(i).toString().substring(0, attributes.item(i).toString().indexOf("="));
			if (attr.equals(attrStr)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the value of an attribute of a single tag element
	 * 
	 * @param node
	 * @param atr
	 * @return
	 */
	public String getAttributeValueForNode(Node node, String atr) {
		// Create a list of nodes of specified tag type
		String value = null;
		// Iterate through the list if it is not empty
		value = ((Element) node).getAttribute(atr);
		return value;
	}

	/** Methods that return groups of nodes */

	/**
	 * Returns an List of all of a given node's children and grandchildren
	 * 
	 * @param root
	 * @param stack
	 * @return
	 */
	public List<Node> returnAllChildNodes(Node root, List<Node> stack) {
		stack.add(root);
		if (root.hasChildNodes()) {
			NodeList templist = root.getChildNodes();
			for (int i = 0; i < templist.getLength(); i++) {
				returnAllChildNodes(templist.item(i), stack);
			}
		}
		return stack;
	}

	/**
	 * Returns a List of all of a given node's children and grandchildren, excluding
	 * text
	 * 
	 * @param root
	 * @return
	 */
	public List<Node> returnAllChildNodesExcludingText(Node root) {
		List<Node> children = returnAllChildNodes(root, new ArrayList<Node>());
		List<Node> returnLyst = new ArrayList<Node>();
		for (Node node : children) {
			if (!node.getNodeName().equals("#text")) {
				returnLyst.add(node);
			}
		}
		return returnLyst;
	}

	/**
	 * Returns an ArrayList of the immediate children of a given node
	 * 
	 * @param localRoot
	 * @return
	 */
	public List<Node> getDirectChildren(Node localRoot) {
		List<Node> nodes = new ArrayList<Node>();
		if (localRoot != null && localRoot.hasChildNodes()) {
			NodeList templist = localRoot.getChildNodes();
			for (int i = 0; i < templist.getLength(); i++) {
				nodes.add(templist.item(i));
			}
		}
		return nodes;
	}

	/**
	 * Returns a List of the immediate children of a given node excluding text
	 * 
	 * @param node
	 * @return
	 */
	public List<Node> getDirectChildrenExcludingText(Node node) {
		List<Node> nodes = new ArrayList<Node>();
		if (node.hasChildNodes()) {
			NodeList templist = node.getChildNodes();
			for (int i = 0; i < templist.getLength(); i++) {
				if (!(templist.item(i).getNodeName().equals("#text"))) {
					nodes.add(templist.item(i));
				}
			}
		}
		return nodes;
	}

	/**
	 * Provided with the root node, returns the translated content
	 * 
	 * @param localRoot
	 * @return
	 */
	public String getContent(Node localRoot) {
		String content = "";
		if (localRoot != null) {
			List<Node> nodes = getDirectChildren(localRoot);
			for (Node node : nodes) {
				content += TransformEpiDocToContent.translateContent(node, this);
			}
			Matcher matcher = Pattern.compile("(\\[[^\\[\\]]+\\])+").matcher(content);
			while (matcher.find()) {
				String temp = matcher.group(0);
				content = content.replace(temp, "[" + temp.replaceAll("\\[|\\]", "") + "]");
			}
		}
		return content;
	}

}
