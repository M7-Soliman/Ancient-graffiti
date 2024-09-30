/*
 * PropertyController -- handles serving property information
 */
package edu.wlu.graffiti.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.dao.InsulaDao;
import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.PropertyTypesDao;

/**
 * Provides methods to retrieve and handle information about properties
 */

@Controller
public class PropertyController {

	/**
	 * Data Access Object class that interacts with the database
	 */
	@Resource
	private PropertyTypesDao propertyTypesDao;

	/**
	 * Data Access Object class that interacts with the database
	 */
	@Resource
	private FindspotDao propertyDao;

	/**
	 * Data Access Object class that interacts with the database
	 */
	@Resource
	private InsulaDao insulaDao;
	
	/**
     * Retrieves and displays property information based on city, insula, and property identifiers.
     * 
     * @param city	   The name of the city
     * @param property The name of the property
     * @param insula   The name of the insula
     * @param request  The HTTP Servlet request 
     * @return         The name of the JSP file for displaying property information or error if property is without address
     */
	

	/**
	 * Method that handles GET request to extract details about a property 
	 * and uses city, insula, and property as parameters.
	 * It sets the property as a request attribute. 
	 * It creates an array list of location keys and sets it as an attribute
	 */
	@RequestMapping(value = "/properties/{city}/{insula}/{property}", method = RequestMethod.GET)
	public String propertyPage(@PathVariable String city, @PathVariable String property, @PathVariable String insula,
			HttpServletRequest request) {
		try {
			final Property prop = this.propertyDao.getPropertyByCityAndInsulaAndProperty(city, insula, property);
			request.setAttribute("prop", prop);
			List<String> locationKeys = new ArrayList<>();
			locationKeys.add(prop.getLocationKey());
			request.setAttribute("findLocationKeys", locationKeys);
			return "property/propertyInfo";
		} catch (Exception e) {
			request.setAttribute("message", "No property with address " + city + " " + insula + " " + property);
			return "property/error";
		}
	}
	

	/** 
	 * The method handles a GET request to search for properties. 
	 * It creates two Property Lists that contains all the properties in Pompeii and all the 
	 * properties in Herculaneum. The method sets these lists as request attributes
	 * @param request The HTTP Servlet request
	 * @return The name of the JSP file for displaying the list of available properties for Pompeii and Herculaneum
	 * */
	@RequestMapping(value = "/properties", method = RequestMethod.GET)
	public String searchProperties(final HttpServletRequest request) {
		// get all the property types, all the properties and their mappings to
		// property types
		
		//Stabiae properties
		final List<Property> stabiaeProperties = propertyDao.getPropertiesByCity("Stabiae");
		
		request.setAttribute("stabiaeProperties", stabiaeProperties);

		
		final List<Property> pompeiiProperties = propertyDao.getPropertiesByCity("Pompeii");

		/*for (Property p : pompeiiProperties) {
			p.setPropertyTypes(propertyDao.getPropertyTypeForProperty(p.getId()));
		}*/
		
		request.setAttribute("pompeiiProperties", pompeiiProperties);
		
		final List<Property> herculaneumProperties = propertyDao.getPropertiesByCity("Herculaneum");

		/*for (Property p : herculaneumProperties) {
			p.setPropertyTypes(propertyDao.getPropertyTypeForProperty(p.getId()));
		}*/
		
		request.setAttribute("herculaneumProperties", herculaneumProperties);

		return "property/propertyList";
	}

	
	
	/** 
	 * The method handles a GET request to search for properties by using city as a parameter. 
	 * The method creates a Propery List of properties in the city that was given as an argument 
	 * and sets it as a request attribute
   * @param city	  The name of the city
   * @param request The HTTP Servlet request
   * @return        The name of the JSP file for displaying the list of properties
	 * 
	 * */
	@RequestMapping(value = "/properties/{city}", method = RequestMethod.GET)
	public String searchByCityProperties(@PathVariable String city, final HttpServletRequest request) {
		// get all the property types, all the properties and their mappings to
		// property types\
		
		if(!propertyDao.getCityNamesUpperCase().contains(city.toUpperCase())) {
			request.setAttribute("message", city + " is not a valid city.");
			return "property/error";
		}
		
		final List<Property> properties = propertyDao.getPropertiesByCity(city);
		request.setAttribute(city.toLowerCase() + "Properties", properties);

		return "property/propertyList";
	}
	
	/** 
	 * Handles GET request to search for properties by using city and insula as parameters. 
	 * The method creates a Property List of properties specified by the city and insula that was given as an argument. 
	 * It sets the Property List and the insula as request attributes. 
   * @param city    The name of the city
	 * @param insula  The name of the insula
	 * @param request The HTTP Servlet request
	 * @return
	 * */
	@RequestMapping(value = "/properties/{city}/{insula:.+}", method = RequestMethod.GET)
	public String searchPropertiesByCityInsula(@PathVariable String city, @PathVariable String insula, final HttpServletRequest request) {
		// get all the property types, all the properties and their mappings to
		// property types
		
		if(!propertyDao.getCityNamesUpperCase().contains(city.toUpperCase())) {
			request.setAttribute("message", city + " is not a valid city.");
			return "property/error";
		}
		
		// TODO: add checks for invalid insula ID.
		
		final List<Property> properties = propertyDao.getPropertiesByCityAndInsula(city, insula);
		request.setAttribute(city.toLowerCase() + "Properties", properties);
		request.setAttribute("filterByInsula", insula);

		return "property/propertyList";
	}

}
