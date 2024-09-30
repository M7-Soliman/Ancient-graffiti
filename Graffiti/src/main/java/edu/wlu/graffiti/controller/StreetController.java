package edu.wlu.graffiti.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wlu.graffiti.bean.Segment;
import edu.wlu.graffiti.bean.Street;
import edu.wlu.graffiti.dao.FindspotDao;


/**
 * Controller that works with jsp files in the "street" folder to handle requests related 
 * to streets and their segments. Provides methods to handle requests for specific street 
 * pages, lists of streets, specific segment pages, and lists of segments.

 * 
 * @author Trevor Stalnaker
 * @author Ben Wechsler
 */
@Controller
public class StreetController {

	@Resource
	private FindspotDao findSpotDao;
	

	
	/**
     * Handles the get requests to retrieve information about a specific street in a given city.
     * 
     * @param city The name of the city
     * @param street The name of the street
     * @param request The HTTP request object that we use to set attributes
     * @return A string that gives the street information or an error page.
     */

	@RequestMapping(value = "/streets/{city}/{street}", method = RequestMethod.GET)
	public String StreetPage(@PathVariable String city, @PathVariable String street,
			HttpServletRequest request) {
		try {
			String street_str = street.replaceAll("__", ".").replaceAll("_", " ").replace("-", " / ");
			Street str = findSpotDao.getStreetByNameAndCity(street_str, city);
			List<Segment> segs = findSpotDao.getSegmentsByStreetName(street_str);
			request.setAttribute("str", str);
			request.setAttribute("sections", segs);
			//List<String> locationKeys = new ArrayList<>();
			//locationKeys.add(seg.getLocationKey());
			//request.setAttribute("findLocationKeys", locationKeys);
			return "street/streetInfo";
		} catch (Exception e) {
			String street_str = street.replaceAll("__", ".").replaceAll("_", " ").replace("-", " / ");
			System.out.println(e);
			request.setAttribute("message", "No street with address " + city + " " + street_str);
			return "property/error";
		}
	}
	

	
	/**
	 * Handles the get requests to retrieve a list of information about all the cities
	 * @param request The HTTP request object that we use to set attributes
	 * @return A string that gives a list of all streets
	 */
	@RequestMapping(value = "/streets", method = RequestMethod.GET)
	public String searchStreets(final HttpServletRequest request) {
		
		final List<Street> streets = findSpotDao.getStreets();

		request.setAttribute("streets", streets);
		
		return "street/streetList";
	}

	
	/**

	 * Retrieves information about a specific segment in a street of a city
	 * 
	 * @param city The name of the city
	 * @param street The name of the street
	 * @param section The name of the section
	 * @param request The HTTP Servlet request
	 * @return The name of the JSP file for displaying segment information or error if the segment is not found

	 */
	@RequestMapping(value = "/streets/{city}/{street}/{section}", method = RequestMethod.GET)
	public String SegmentPage(@PathVariable String city, @PathVariable String street, @PathVariable String section,
			HttpServletRequest request) {
		try {
			String str = street.replaceAll("__", ".").replaceAll("_", " ").replace("-", " / ");
			String s = section.replaceAll("_", ".");
			final Segment seg = findSpotDao.getSegmentByNameStreetAndCity(s, str, city);
			request.setAttribute("seg", seg);
			//List<String> locationKeys = new ArrayList<>();
			//locationKeys.add(seg.getLocationKey());
			//request.setAttribute("findLocationKeys", locationKeys);
			return "street/sectionInfo";
		} catch (Exception e) {
			String str = street.replaceAll("__", ".").replaceAll("_", " ").replace("-", " / ");
			String s = section.replaceAll("_", ".");
			request.setAttribute("message", "No segment with address " + city + " " + str + " " + s);
			return "property/error";
		}
	}

	/**
	 * Handles get request to get a list of all segment names
	 * @param request The HTTP request object that we use to set attributes
	 * @return A string that represents a list of all street segments

	 */
	@RequestMapping(value = "/streets/sections", method = RequestMethod.GET)
	public String searchSegments(final HttpServletRequest request) {
		// get all the property types, all the properties and their mappings to
		// property types

		final List<Segment> segments = findSpotDao.getSegmentsWithFacades();

		request.setAttribute("segs", segments);
		
		return "street/sectionList";
	}
	
}
