/*
 * GraffitiController.java is the main backend controller of the Ancient Graffiti Project. It handles most of the
 * controls regarding the requests.
 */
package edu.wlu.graffiti.controller;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.elasticsearch.index.query.QueryBuilders.regexpQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RegexpQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wlu.graffiti.bean.FeaturedInscription;
import edu.wlu.graffiti.bean.IndexTerm;
import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.Insula;
import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.dao.DrawingTagsDao;
import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.GraffitiDao;
import edu.wlu.graffiti.dao.IndexTermDao;
import edu.wlu.graffiti.dao.InsulaDao;
import edu.wlu.graffiti.dao.PropertyTypesDao;
import edu.wlu.graffiti.data.setup.TransformEDRContentToEpiDoc;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;

@Controller
//@Api(tags = { "graffiti-controller" })
public class GraffitiController {

	@Resource
	private GraffitiDao graffitiDao;

	@Resource
	private DrawingTagsDao drawingTagsDao;

	@Resource
	private PropertyTypesDao propertyTypesDao;

	@Resource
	private FindspotDao findspotDao;

	@Resource
	private InsulaDao insulaDao;

	@Resource
	private IndexTermDao indexTermDao;

	@Resource
	private FindspotDao propertyDao;

	// Field names
	public static final String CONTENT_FIELD_NAME = "content";
	public static final String CIL_FIELD_NAME = "cil";
	public static final String CITY_FIELD_NAME = "city";
	public static final String INSULA_NAME_FIELD_NAME = "insula.insula_name";
	public static final String INSULA_ID_FIELD_NAME = "insula.insula_id";
	public static final String PROPERTY_ID_FIELD_NAME = "property.property_id";
	public static final String PROPERTY_TYPES_FIELD_NAME = "property.property_types";
	public static final String DRAWING_TAG_ID_FIELD_NAME = "drawing.drawing_tag_ids";
	public static final String WRITING_STYLE_IN_ENGLISH_FIELD_NAME = "writing_style_in_english";
	public static final String LANGUAGE_IN_ENGLISH_FIELD_NAME = "language_in_english";
	public static final String FACADE_FIELD_NAME = "on_facade";
	public static final String STREET_FIELD_NAME = "segment.street.street_id";
	public static final String SEGMENT_FIELD_NAME = "segment.segment_id";
	public static final String COLUMN_FIELD_NAME = "column.column_id";
	public static final String PHOTOS_FIELD_NAME = "has_photos";
	
	public static final String POETRY_FIELD_NAME = "is_poetic";


	public static final String MULTIPLE_FIELDS_NAME = "content content_translation summary city "
			+ "insula.insula_name property.property_name property.property_types"
			+ "cil description writing_style language graffiti_id bibliography"
			+ " drawing.description_in_english drawing.description_in_latin drawing.drawing_tags";

	// Search Descriptions
	public static final String CONTENT_KEYWORD_SEARCH_DESC = "Content Keyword";
	public static final String GLOBAL_KEYWORD_SEARCH_DESC = "Global Keyword";
	public static final String CIL_KEYWORD_SEARCH_DESC = "CIL Keyword";
	public static final String CITY_SEARCH_DESC = "Ancient Site";
	public static final String INSULA_SEARCH_DESC = "Insula";
	public static final String PROPERTY_SEARCH_DESC = "Property";
	public static final String PROPERTY_TYPE_SEARCH_DESC = "Property Type";
	public static final String DRAWING_CATEGORY_SEARCH_DESC = "Drawing Category";
	
	public static final String POETRY_SEARCH_DESC = "poetry";
	
	public static final String WRITING_STYLE_SEARCH_DESC = "Writing Style";
	public static final String LANGUAGE_SEARCH_DESC = "Language";
	public static final String FACADE_SEARCH_DESC = "Facade";
	public static final String STREET_SEARCH_DESC = "Street";
	public static final String SEGMENT_SEARCH_DESC = "Segment";
	public static final String COLUMN_SEARCH_DESC = "Column";
	public static final String PHOTOS_SEARCH_DESC = "Photos";

	public static final String WRITING_STYLE_GRAFFITI_INSCRIBED = "Graffito/incised";
	public static final String WRITING_STYLE_PARAM_NAME = "writing_style";

	public static final Map<String, Float> WEIGHTED_SEARCH_FIELDS = createWeightedSearchFieldsForGlobalSearch();

	/** default size in elasticsearch is 10 */
	private static final int NUM_RESULTS_TO_RETURN = 3300;
	private static final int DEFAULT_RESULTS_PER_PAGE = 200;
	private static final int MAXIMUM_NUM_PAGE_TABS = 7; // This number should be odd so that there is a midpoint
	

	@Value("${spring.elasticsearch.rest.index}")
	private String ES_INDEX_NAME;

	@Autowired
	private RestHighLevelClient client;

	private static String[] searchDescs = { CONTENT_KEYWORD_SEARCH_DESC, GLOBAL_KEYWORD_SEARCH_DESC,
			CIL_KEYWORD_SEARCH_DESC, CITY_SEARCH_DESC, INSULA_SEARCH_DESC, PROPERTY_SEARCH_DESC,
			PROPERTY_TYPE_SEARCH_DESC, DRAWING_CATEGORY_SEARCH_DESC, WRITING_STYLE_SEARCH_DESC, LANGUAGE_SEARCH_DESC,
			FACADE_SEARCH_DESC, STREET_SEARCH_DESC, SEGMENT_SEARCH_DESC, COLUMN_SEARCH_DESC, PHOTOS_SEARCH_DESC, POETRY_SEARCH_DESC};

	private static String[] searchFields = { CONTENT_FIELD_NAME, MULTIPLE_FIELDS_NAME, CIL_FIELD_NAME, CITY_FIELD_NAME,
			INSULA_ID_FIELD_NAME, PROPERTY_ID_FIELD_NAME, PROPERTY_TYPES_FIELD_NAME, DRAWING_TAG_ID_FIELD_NAME,
			WRITING_STYLE_IN_ENGLISH_FIELD_NAME, LANGUAGE_IN_ENGLISH_FIELD_NAME, FACADE_FIELD_NAME, STREET_FIELD_NAME,
			SEGMENT_FIELD_NAME, COLUMN_FIELD_NAME, PHOTOS_FIELD_NAME, POETRY_FIELD_NAME};

	@RequestMapping(value = "/searchPompeii", method = RequestMethod.GET)
	public String searchMap(final HttpServletRequest request) {
		return "searchPompeii";
	}

	@RequestMapping(value = "/searchHerculaneum", method = RequestMethod.GET)
	public String searchHerc(final HttpServletRequest request) {
		return "searchHerculaneum";
	}

	public static String cleanParam(String param) {
		if (param == null) {
			param = "(%)";
		} else {
			param = param.replaceAll(" ", "\\|");
		}
		return param;
	}

	public List<IndexTerm> filterIndices(final HttpServletRequest request, String index) {
		List<IndexTerm> terms = null;
		String ordering = request.getParameter("sort_by");
		String lang = cleanParam(request.getParameter("lang"));
		String pos = cleanParam(request.getParameter("pos"));
		String name = cleanParam(request.getParameter("name"));
		String pers = cleanParam(request.getParameter("person"));
		String gen = cleanParam(request.getParameter("gender"));
		String city = cleanParam(request.getParameter("ancient_site"));
		if (index.equals("people")) {
			if (ordering == null || ordering.equals("alpha")) {
				terms = this.indexTermDao.getIndexTermsByLangNamePersonGender(index, lang, name, pers, gen, city);
			} else if (ordering.equals("appear")) {
				terms = this.indexTermDao.getIndexPeopleByOccurence(index, lang, name, pers, gen, city);
			} else {
				terms = this.indexTermDao.getIndexTermsByLangNamePersonGender(index, lang, name, pers, gen, city);
			}
		} else {
			if (ordering == null || ordering.equals("alpha")) {
				terms = this.indexTermDao.getIndexTermsByCategoryLanguageAndPOS(index, lang, pos, city);
			} else if (ordering.equals("appear")) {
				terms = this.indexTermDao.getIndexTermsByOccurrenceCategoryLanguageAndPOS(index, lang, pos, city);
			} else {
				terms = this.indexTermDao.getIndexTermsByCategoryLanguageAndPOS(index, lang, pos, city);
			}
		}
		return terms;
	}

	/*
	 * Calls functions to generate and format the data needed by the
	 * termIndexList.jsp
	 */
	@RequestMapping(value = "/indices/{index}", method = RequestMethod.GET)
	public String Indices(@PathVariable String index, final HttpServletRequest request) {
		final List<IndexTerm> indexTerms = filterIndices(request, index);
		request.setAttribute("terms", indexTerms);
		request.setAttribute("index", index);
		HttpSession s = request.getSession();
		s.setAttribute("returnFromTermsURL", ControllerUtils.getFullRequest(request));
		return "indices/indices";
	}

	/*
	 * Calls functions to generate the termPage for an index term from a link in the
	 * termIndex
	 */
	@RequestMapping(value = "/indices/{index}/term", method = RequestMethod.GET)
	public String termPage(@PathVariable String index, HttpServletRequest request) {
		int id = Integer.parseInt(request.getParameter("id"));
		List<IndexTerm> indexTerms = this.indexTermDao.getIndexTermsByID(id);
		request.setAttribute("term", indexTerms.get(0));
		request.setAttribute("index", index);
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		request.getSession().setAttribute("returnFromTerms", id);
		return "indices/term";
	}

	// Used for mapping the URI to show inscriptions. It has a similar structure
	// to the mapping done to /result below
	//@ApiOperation(value = "Shows the results page for a given city")
	@RequestMapping(value = "/region/{city}", method = RequestMethod.GET)
	public String cityPage(@PathVariable String city, HttpServletRequest request) {
		String searches = city;
		final List<Inscription> resultsList = this.graffitiDao.getInscriptionsByCity(searches);
		request.setAttribute("resultsLyst", resultsList);
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		return "displayData";
	}

	@RequestMapping(value = "/region/{city}/{insula}", method = RequestMethod.GET)
	public String insulaPage(@PathVariable String city, @PathVariable String insula, HttpServletRequest request,
			HttpServletResponse response) {
		// System.out.println("insulaPage: " + insula);
		int insula_id = getInsulaId(city, insula);
		final List<Inscription> inscriptions = this.graffitiDao.getInscriptionsByCityAndInsula(city, insula_id);
		request.setAttribute("resultsLyst", inscriptions);
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		return "displayData";
	}

	@RequestMapping(value = "/region/{city}/{insula}/{property}", method = RequestMethod.GET)
	public String propertyPage(@PathVariable String city, @PathVariable String property, @PathVariable String insula,
			HttpServletRequest request) {
		// System.out.println("propertyPage: " + property);
		int insula_id = getInsulaId(city, insula);
		int property_id = getPropertyId(city, insula, property);
		final List<Inscription> inscriptions = this.graffitiDao.getInscriptionsByCityAndInsulaAndPropertyNumber(city,
				insula_id, property_id);
		request.setAttribute("resultsLyst", inscriptions);
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		return "displayData";
	}

	@RequestMapping(value = "/region/{city}/{insula}/{property}/{id}", method = RequestMethod.GET)
	public String dataPage(@PathVariable String city, @PathVariable String property, @PathVariable String insula,
			@PathVariable int id, HttpServletRequest request) {
		int insula_id = getInsulaId(city, insula);
		int property_id = getPropertyId(city, insula, property);
		final List<Inscription> allInscriptions = this.graffitiDao.getInscriptionsByCityAndInsulaAndPropertyNumber(city,
				insula_id, property_id);
		final List<Inscription> resultsList2 = new ArrayList<Inscription>();

		if (id < allInscriptions.size()) {
			Inscription whichInsc = allInscriptions.get(id);
			resultsList2.add(whichInsc);
		}
		request.setAttribute("resultsLyst", resultsList2);
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		return "displayData";
	}

	// helper method to get insula id for given insula name
	private int getInsulaId(String city, String insula) {
		final List<Insula> ins = this.insulaDao.getInsulaByCityAndInsula(city, insula.toUpperCase());
		if (ins != null && !ins.isEmpty()) {
			return ins.get(0).getId();
		}
		return -1;
	}

	// helper method to get property id for given property number
	private int getPropertyId(String city, String insula, String property_number) {
		final Property prop = this.findspotDao.getPropertyByCityAndInsulaAndProperty(city, insula.toUpperCase(),
				property_number);
		if (prop != null) {
			return prop.getId();
		}
		return -1;
	}

	// The default page is sent to index.jsp
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String indexPage(final HttpServletRequest request) {

		request.setAttribute("drawingCategories", drawingTagsDao.getDrawingTags());
		request.setAttribute("propertyTypes", findspotDao.getPropertyTypes());

		return "index";
	}
	
	@RequestMapping(value = "/lang", method = RequestMethod.GET) 
	public String languageSelect (final HttpServletRequest request){
		String language = request.getParameter("lang");
		HttpSession s = request.getSession();
		s.setAttribute("lang", language);
        return "redirect:/?lang=" + language;
	}

	// Maps to the details page once an individual result has been selected in
	// the results page
	//@ApiOperation(value = "Displays information pertaining to the selected inscription.")
	@RequestMapping(value = "/graffito/{agpId}", method = RequestMethod.GET)
	public String graffito(final HttpServletRequest request, @PathVariable String agpId) {
		String graffiti_id = agpId.replaceFirst("AGP-", "");
		final Inscription i = this.graffitiDao.getInscriptionByID(graffiti_id);
		if (i == null) {
			String error;
			if (this.graffitiDao.EDRIDExists(graffiti_id)) {
				error = "Note: Inscription with id AGP-" + graffiti_id + " is in process.";
			} else {
				error = "Note: Inscription with id AGP-" + graffiti_id + " may not exist.";
			}
			request.setAttribute("error", error);
			return search(request);
		} else {
			request.setAttribute("images", i.getImages());
			request.setAttribute("imagePages", i.getPages());
			request.setAttribute("findLocationKeys", findLocationKey(i));
			request.setAttribute("insulaLocationKeys", findInsulaLocation(i));
			request.setAttribute("inscription", i);
			request.setAttribute("notations", notationsInContent(i.getContent()));

			// Uncomment this and the matching code in sidebarSearchMenu if
			// you want the window to jump to the previous result on browsing
			request.getSession().setAttribute("returnFromDetails", graffiti_id);

			// Decides which jsp page to travel to when user clicks "More
			// Information" on Search page.

			return "details";
		}
	}

	@RequestMapping(value = "/graffito/featured/{agpId}", method = RequestMethod.GET)
	public String featuredGraffito(final HttpServletRequest request, @PathVariable String agpId) {
		String graffiti_id = agpId.replaceFirst("AGP-", "");
		final FeaturedInscription i = this.graffitiDao.getFeaturedInscriptionByID(graffiti_id);
		if (i == null) {
			request.setAttribute("error", "Inscription with id " + graffiti_id + " is in process.");
			return search(request);
		} else {
			request.setAttribute("inscription", i);
			request.setAttribute("notations", notationsInContent(i.getContent()));
			return "featured_details";
		}
	}

	// TODO: add annotation produces = "text/html" for the api docs?
	/*
	@ApiOperation(value = "Searches for inscriptions and returns the results. The base URI lists "
			+ "all inscriptions by default. Various parameters can be added to the URI to filter "
			+ "results as the user wishes.", notes = "A detailed overview of possible parameters is as follows: <br/> "
					+ "city={cityName}, where the cities are as follows: [Pompeii, Herculaneum]. <br/>"
					+ "insula={insulaID} <br/>" + "property={propertyID} <br/>" + "property_type={propertyType}<br/>"
					+ "drawing_category={dcID}, where the dcIDs are as follows: [All=0, Boats=1, Geometric designs=2, Animals=3, Erotic Images=4, Other=5, Human figures=6, Gladiators=7, Plants=8]. <br/>"
					+ "writing_style={writingStyle}, where the writing styles are as follows: [Graffito/incised, charcoal, other].<br/>"
					+ "language={language}, where the languages are as follows: [Latin, Greek, Latin/Greek, other].<br/>"
					+ "global={searchString}, where the search string can be any text to search globally for. <br/>"
					+ "content={searchString}, where the search string can be any text to search the content for. <br/>"
					+ "cil={searchString}, where the search string can be any text to search the cil for. <br/>"
					+ "sort_by={sortParameter}, where the sort parameters are as follows: [summary, cil, property.property_id]. <br/>"
					+ "Mutiple parameters passed in the URI can be separated using an ampersand symbol, '&'.")
	*/
	@RequestMapping(value = "/results", method = RequestMethod.GET)
	public String search(final HttpServletRequest request) {

		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		List<Inscription> inscriptions = searchResults(request);
		List<String> cities = findCities(inscriptions);

		request.setAttribute("searchQueryDesc", "filtering");

		// Process all search results
		populateRequestForSidebarSearch(request);
		updateRequestAttributes(request, inscriptions, cities);

		// Apply Pagination
		applyPagination(request, inscriptions);

		return "results";
	}
	
	@RequestMapping(value = "/mobile", method = RequestMethod.GET)
	public String mobile(final HttpServletRequest request) {

		return "mobile";
	}

	/**
	 * Set the request parameters needed for proper displaying of web page
	 * 
	 * @param request
	 * @param inscriptions
	 * @param cities
	 */
	private void updateRequestAttributes(final HttpServletRequest request, List<Inscription> inscriptions,
			List<String> cities) {
		request.setAttribute("allResultsLyst", inscriptions);
		request.setAttribute("hasHerc", hasCity("Herculaneum", cities));
		request.setAttribute("hasPomp", hasCity("Pompeii", cities));
		request.setAttribute("hasStab", hasCity("Stabiae", cities));
		request.setAttribute("findLocationKeys", findLocationKeys(inscriptions));
		request.setAttribute("insulaLocationKeys", findInsulaLocations(inscriptions));
		request.setAttribute("totalResults", inscriptions.size());
	}
/*
	@ApiOperation(value = "Filters the inscriptions and returns the results without any styling. The base URI lists "
			+ "all inscriptions by default. Various parameters can be added to the URI to filter "
			+ "results as the user wishes.", notes = "A detailed overview of possible parameters is as follows: <br/> "
					+ "city={cityName}, where the cities are as follows: [Pompeii, Herculaneum]. <br/>"
					+ "insula={insulaID} <br/>" + "property={propertyID} <br/>" + "property_type={propertyType}<br/>"
					+ "drawing_category={dcID}, where the dcIDs are as follows: [All=0, Boats=1, Geometric designs=2, Animals=3, Erotic Images=4, Other=5, Human figures=6, Gladiators=7, Plants=8]. <br/>"
					+ "writing_style={writingStyle}, where the writing styles are as follows: [Graffito/incised, charcoal, other].<br/>"
					+ "language={language}, where the languages are as follows: [Latin, Greek, Latin/Greek, other].<br/>"
					+ "global={searchString}, where the search string can be any text to search globally for. <br/>"
					+ "content={searchString}, where the search string can be any text to search the content for. <br/>"
					+ "cil={searchString}, where the search string can be any text to search the cil for. <br/>"
					+ "sort_by={sortParameter}, where the sort parameters are as follows: [summary, cil, property.property_id]. <br/>"
					+ "Mutiple parameters passed in the URI can be separated using an ampersand symbol, '&'.")
	*/
	@RequestMapping(value = "/filter", method = RequestMethod.GET)
	public String filterResults(final HttpServletRequest request) {
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		List<Inscription> inscriptions = searchResults(request);
		List<String> cities = findCities(inscriptions);

		updateRequestAttributes(request, inscriptions, cities);
		populateRequestForSidebarSearch(request);

		// Apply Pagination
		applyPagination(request, inscriptions);
		return "filter";
	}

	public static void applyPagination(HttpServletRequest request, List<Inscription> inscriptions) {
		// Return only the results to be displayed on current page
		String[] viewCount = request.getParameterValues("view_count");
		String[] page = request.getParameterValues("page");

		int currentPage = 1;
		if (page != null)
			currentPage = Integer.parseInt(page[0]);

		int resultsPerPage = DEFAULT_RESULTS_PER_PAGE;
		if (viewCount != null)
			resultsPerPage = Integer.parseInt(viewCount[0]);

		List<Inscription> displayedResults = null;

		int totalPagesRequired = (int) Math.ceil((double) inscriptions.size() / resultsPerPage);
		request.setAttribute("pageCount", totalPagesRequired);

		int startTab, endTab;

		if (totalPagesRequired < MAXIMUM_NUM_PAGE_TABS) {
			startTab = 1;
			endTab = totalPagesRequired;
		} else {
			if (currentPage <= MAXIMUM_NUM_PAGE_TABS/2 + 1) {
				startTab = 1;
				endTab = MAXIMUM_NUM_PAGE_TABS;
			} else if (currentPage >= (totalPagesRequired - MAXIMUM_NUM_PAGE_TABS/2)) {
				startTab = (totalPagesRequired - MAXIMUM_NUM_PAGE_TABS) + 1;
				endTab = totalPagesRequired;
			} else {
				startTab = currentPage - MAXIMUM_NUM_PAGE_TABS / 2;
				endTab = currentPage + MAXIMUM_NUM_PAGE_TABS / 2;
			}
		}

		request.setAttribute("firstTab", startTab);
		request.setAttribute("endTab", endTab);
		request.setAttribute("currentPage", currentPage);

		// Get the current page partition
		int startIndex = (currentPage - 1) * resultsPerPage;
		int endIndex = Math.min((currentPage * resultsPerPage), inscriptions.size());
		displayedResults = inscriptions.subList(startIndex, endIndex);

		request.setAttribute("resultsLyst", displayedResults);
	}

	@RequestMapping(value = "/print", method = RequestMethod.GET)
	public String printResults(final HttpServletRequest request) {
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		List<Inscription> inscriptions = searchResults(request);

		List<String> cities = findCities(inscriptions);
		request.setAttribute("hasHerc", hasCity("Herculaneum", cities));
		request.setAttribute("hasPomp", hasCity("Pompeii", cities));
		request.setAttribute("hasStab", hasCity("Stabiae", cities));
		request.setAttribute("resultsLyst", inscriptions);
		request.setAttribute("searchQueryDesc", "filtering");
		populateRequestForSidebarSearch(request);

		return "print";
	}

	/**
	 * Gather the search results and update the request object with attributes
	 * related to the search.
	 * 
	 * @param request
	 * @return
	 */
	@Cacheable
	private List<Inscription> searchResults(final HttpServletRequest request) {
		
		String facade_property_type_id = Integer.toString(this.propertyTypesDao.getFacadesID());

		SearchResponse response;
		String searchedProperties = "";
		String searchedDrawings = "";

		List<Inscription> inscriptions = new ArrayList<Inscription>();

		// List of parameter strings for each given search term
		List<String> parameters = new ArrayList<String>();
		// List of search terms
		List<String> searchTerms = new ArrayList<String>();
		// List of field names to search for each different search term
		List<String> fieldNames = new ArrayList<String>();

		// Gather all of the request parameters and make an array of those
		// arrays to loop through and check if null
		String[] content = request.getParameterValues("content");
		String[] global = request.getParameterValues("global");
		String[] cil = request.getParameterValues("cil");
		String[] city = request.getParameterValues("city");
		String[] insula = request.getParameterValues("insula");
		String[] property = request.getParameterValues("property");
		String[] propertyType = request.getParameterValues("property_type");
		String[] drawingCategory = request.getParameterValues("drawing_category");
		
		String[] poetry = request.getParameterValues("poetry");

		String[] writingStyle = request.getParameterValues("writing_style");
		String[] language = request.getParameterValues("language");
		String[] facade = request.getParameterValues("on_facade");
		String[] street = request.getParameterValues("street");
		String[] segment = request.getParameterValues("segment");
		String[] column = request.getParameterValues("column");
		String[] photos = request.getParameterValues("photos");

		String[] sortOrder = request.getParameterValues("sort_by");

		String[][] searches = { content, global, cil, city, insula, property, propertyType, drawingCategory, 
				writingStyle, language, facade, street, segment, column, photos, poetry};

		// Determine which parameters have been given; populate the
		// parameters, searchTerms, and fieldNames lists accordingly
		for (int i = 0; i < searches.length; i++) {
			if (searches[i] != null) {
				parameters.add(arrayToString(searches[i]));
				searchTerms.add(searchDescs[i]);
				fieldNames.add(searchFields[i]);
			}
		}

		try {
			// This is the main query; does an AND of all sub-queries
			BoolQueryBuilder query = boolQuery();

			// A Sub Query used to do an OR across some queries
			BoolQueryBuilder locationQuery = boolQuery();

			// For each given search term, we build a sub-query
			// Special cases are Global Keyword, Content Keyword, CIL Keyword, Property,
			// and writing style searches; all others are simple match queries
			for (int i = 0; i < searchTerms.size(); i++) {

				// Searches has_figural_component if user selected "All" drawings
				if (searchTerms.get(i).equals(DRAWING_CATEGORY_SEARCH_DESC) && parameters.get(i).contains("All")) {
					BoolQueryBuilder allDrawingsQuery = boolQuery();
					allDrawingsQuery.should(matchQuery("has_figural_component", true));					
					
					query.must(allDrawingsQuery);

					// Checks content, city, insula name, property name, property types,
					// drawing description, drawing tags, writing style, language, graffiti id,
					// and bibliography for a keyword match
				} else if (searchTerms.get(i).equals(POETRY_SEARCH_DESC) && parameters.get(i).contains("All")) {
					BoolQueryBuilder allPoetryQuery = boolQuery();
					allPoetryQuery.should(matchQuery("is_poetic", true));					
					
					query.must(allPoetryQuery);

				} else if (searchTerms.get(i).equals(GLOBAL_KEYWORD_SEARCH_DESC)) {
					BoolQueryBuilder globalQuery = createGlobalSearchQuery(parameters, i);
					query.must(globalQuery);

					// Checks content for keywords
				} else if (searchTerms.get(i).equals(CONTENT_KEYWORD_SEARCH_DESC)) {
					BoolQueryBuilder contentQuery = boolQuery();
					createContentKeywordSearchQuery(parameters, fieldNames, i, contentQuery);
					query.must(contentQuery);

					// Checks the CIL field for an exact match
				} else if (searchTerms.get(i).equals(CIL_KEYWORD_SEARCH_DESC)) {
					BoolQueryBuilder cilQuery = boolQuery();
					MatchQueryBuilder mq_exact = matchQuery(fieldNames.get(i), parameters.get(i).toUpperCase());
					cilQuery.should(mq_exact); // exact match
					query.must(cilQuery);

				} else if (searchTerms.get(i).equals(PROPERTY_SEARCH_DESC)
						|| searchTerms.get(i).equals(CITY_SEARCH_DESC) || searchTerms.get(i).equals(INSULA_SEARCH_DESC)
						|| searchTerms.get(i).equals(STREET_SEARCH_DESC)
						|| searchTerms.get(i).equals(SEGMENT_SEARCH_DESC)
						|| searchTerms.get(i).equals(COLUMN_SEARCH_DESC)) {

					// Checks for property matches
					if (searchTerms.get(i).equals(PROPERTY_SEARCH_DESC)) {
						String[] properties = parameters.get(i).split(" ");
						for (int j = 0; j < properties.length; j++) {
							String propertyID = properties[j];
							QueryBuilder propertyIdQuery = termQuery(fieldNames.get(i), propertyID);
							BoolQueryBuilder propertyQuery = boolQuery().should(propertyIdQuery);
							locationQuery.should(propertyQuery);
						}
					} else {
						String[] params = parameters.get(i).split(" ");
						for (String param : params) {
							locationQuery.should(termQuery(fieldNames.get(i), param));
						}
					}

					// Checks for writing style matches
				} else if (searchTerms.get(i).equals(WRITING_STYLE_SEARCH_DESC)
						&& parameters.get(i).equalsIgnoreCase("other")) {
					// special handling of the writing style being "other"
					query.mustNot(termQuery(fieldNames.get(i), "charcoal"));
					query.mustNot(termQuery(fieldNames.get(i), WRITING_STYLE_GRAFFITI_INSCRIBED));
				} else if (searchTerms.get(i).equals(PROPERTY_TYPE_SEARCH_DESC)) {
					BoolQueryBuilder pTypesQuery = boolQuery();
					String[] params = parameters.get(i).split(" ");
					for (String param : params) {
						if (param.equals(facade_property_type_id)) {
							pTypesQuery.should(termQuery("on_facade", "true"));
							System.out.println("FACADES");
						} else {
							pTypesQuery.should(termQuery(fieldNames.get(i), param));
						}
					}
					query.must(pTypesQuery);
				}
				// Query other fields (ie non-special cases)
				else {
					BoolQueryBuilder otherQuery = boolQuery();
					String[] params = parameters.get(i).split(" ");
					for (String param : params) {
						otherQuery.should(termQuery(fieldNames.get(i), param));
					}
					query.must(otherQuery);
				}
			}
			query.must(locationQuery);

			// Give a higher priority to inscriptions from Herculaneum and Pompeii
			BoolQueryBuilder priorityQuery = createPriorityQuery();
			query.must(priorityQuery);

			SearchRequest searchRequest = new SearchRequest(ES_INDEX_NAME);
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(query);
			
			searchSourceBuilder.storedField("graffiti_id");
			
			
			searchSourceBuilder.size(NUM_RESULTS_TO_RETURN);
			searchRequest.source(searchSourceBuilder);

			if (sortOrder != null && !sortOrder[0].equals("relevance")) {
				searchSourceBuilder.sort(sortOrder[0], SortOrder.ASC);
			}

			response = client.search(searchRequest, RequestOptions.DEFAULT);

			for (SearchHit hit : response.getHits()) {
				inscriptions.add(hitToInscription(hit));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Inscription>();
		}

		HttpSession session = request.getSession();

		request.setAttribute("searchedProperties", searchedProperties);
		request.setAttribute("searchedDrawings", searchedDrawings);
		session.setAttribute("returnURL", ControllerUtils.getFullRequest(request));

		return inscriptions;
	}

	/**
	 * Give a higher priority to inscriptions from Herculaneum and Pompeii in the
	 * search results
	 * 
	 * @return the query that represents Herculaneum and Pompeii have higher
	 *         priority.
	 */
	private BoolQueryBuilder createPriorityQuery() {
		BoolQueryBuilder priorityQuery = boolQuery();
		for (String c : findspotDao.getCityNames()) {
			if (c.equals("Herculaneum") || c.equals("Pompeii")) {
				priorityQuery.should(termQuery(CITY_FIELD_NAME, c).boost(2));
			} else if (c.equals("Stabiae")) {
				priorityQuery.should(termQuery(CITY_FIELD_NAME, c).boost(1));
			} else if (c.equals("Smyrna")) {
				priorityQuery.should(termQuery(CITY_FIELD_NAME, c).boost(0));
			} else {
				priorityQuery.should(termQuery(CITY_FIELD_NAME, c));
			}
		}
		return priorityQuery;
	}

	public static Map<String, Float> createWeightedSearchFieldsForGlobalSearch() {

		float highest_priority_boost = 7;
		float top_tier_boost = 6;
		float mid_tier_boost = 4;
		float bottom_tier_boost = 3;

		String[] top_tier_fields = { "content", "content_translation", "drawing.description_in_english", "caption",
				"drawing.drawing_tags" };

		String[] mid_tier_fields = { "city", "insula.insula_name", "property.property_name", "cil", "description",
				"writing_style", "language", "segment.street.street_name", "segment.segment_name",
				"writing_style_in_english" };

		String[] bottom_tier_fields = { "bibliography", "summary" };

		Map<String, Float> fields = new HashMap<String, Float>();
		fields.put("graffiti_id", highest_priority_boost);
		for (String field : top_tier_fields) {
			fields.put(field, top_tier_boost);
		}
		for (String field : mid_tier_fields) {
			fields.put(field, mid_tier_boost);
		}
		for (String field : bottom_tier_fields) {
			fields.put(field, bottom_tier_boost);
		}

		return fields;

	}

	/**
	 * @param parameters
	 * @param i
	 * @return
	 */
	public static BoolQueryBuilder createGlobalSearchQuery(List<String> parameters, int i) {

		System.out.println("Global Search: ");
		for (String param : parameters) {
			System.out.println(param);
		}

		String param = parameters.get(i);
		Pattern pattern = Pattern.compile("([A-Za-z]{3})(\\d{6})");
		Matcher matcher = pattern.matcher(param);
		if (matcher.find()) {
			param = matcher.group(1).toUpperCase() + matcher.group(2);
		}

		BoolQueryBuilder globalQuery;
		globalQuery = boolQuery();
		QueryStringQueryBuilder qsq1 = queryStringQuery(param);
		qsq1.fields(WEIGHTED_SEARCH_FIELDS);
		globalQuery.should(qsq1); // exact match
		QueryStringQueryBuilder qsq2 = queryStringQuery(param + "*");
		qsq2.fields(WEIGHTED_SEARCH_FIELDS);
		globalQuery.should(qsq2); // partial match
		return globalQuery;
	}

	/**
	 * @param parameters
	 * @param fieldNames
	 * @param i
	 * @param contentQuery
	 */
	private void createContentKeywordSearchQuery(List<String> parameters, List<String> fieldNames, int i,
			BoolQueryBuilder contentQuery) {
		String[] params = parameters.get(i).split(" ");
		for (String param : params) {
			BoolQueryBuilder orQuery = boolQuery();
			MatchQueryBuilder mq_exact = matchQuery(fieldNames.get(i), param.toLowerCase());
			orQuery.should(mq_exact); // exact match
			RegexpQueryBuilder rq_partial = regexpQuery(fieldNames.get(i), ".*" + param.toLowerCase() + ".*");
			orQuery.should(rq_partial); // partial match
			contentQuery.must(orQuery);
		}
	}

	/**
	 * populate the request object with attributes for the sidebar search menu.
	 * 
	 * @param request
	 */
	private void populateRequestForSidebarSearch(final HttpServletRequest request) {
		// Used in sidebarSearchMenu.jsp
		request.setAttribute("cities", findspotDao.getCityNames());
		request.setAttribute("citiesWithStreets", findspotDao.getCitiesWithStreets());
		request.setAttribute("drawingCategories", drawingTagsDao.getDrawingTags());
		request.setAttribute("propertyTypes", findspotDao.getPropertyTypes());
		request.setAttribute("streets", findspotDao.getStreetsWithFacades());
		request.setAttribute("segments", findspotDao.getSegmentsWithFacades());
		request.setAttribute("insulaList", insulaDao.getInsulae());
		// only get the properties that have graffiti in them.
		request.setAttribute("propertiesList", findspotDao.getPropertiesWithGraffiti());
	}

	/**
	 * Turns an array like ["Pompeii", "Herculaneum"] into a string like "Pompeii
	 * Herculaneum" for Elasticsearch match query
	 * 
	 * @param parameters
	 * @return
	 */
	private static String arrayToString(String[] parameters) {
		StringBuilder sb = new StringBuilder();
		sb.append(parameters[0].replace("_", " "));
		for (int i = 1; i < parameters.length; i++) {
			sb.append(" ").append(parameters[i].replace("_", " "));
		}
		return sb.toString();
	}

	private Inscription hitToInscription(SearchHit hit) {
		String graffitiID = hit.field("graffiti_id").getValue();
				
		Inscription inscription = graffitiDao.getInscriptionByID(graffitiID);

		if(inscription == null) {
			System.err.println("**************************");
			System.err.println("UH OH!! Error retrieving " + graffitiID);
			System.err.println("Skipping it...");
		}
		return inscription;
	}

	private static List<Integer> findLocationKeys(final List<Inscription> inscriptions) {
		final List<Integer> locationKeys = new ArrayList<Integer>();
		if (inscriptions != null) {
			final Set<Integer> locationKeysSet = new TreeSet<Integer>();
			for (final Inscription inscription : inscriptions) {
				locationKeysSet.add(inscription.getSpotKey());
				locationKeysSet.add(inscription.getStreetSpotKey());
				locationKeysSet.add(inscription.getSegmentSpotKey());
			}
			locationKeys.addAll(locationKeysSet);
		}
		return locationKeys;
	}

	private static List<String> findCities(final List<Inscription> inscriptions) {
		final List<String> cities = new ArrayList<String>();
		if (inscriptions != null) {
			for (final Inscription inscription : inscriptions) {
				String city = inscription.getAncientCity();
				if (!cities.contains(city)) {
					cities.add(city);
				}
			}
		}
		return cities;
	}

	private static boolean hasCity(String city, List<String> cities) {
		return cities.contains(city);
	}

	private static List<Integer> findLocationKey(final Inscription inscription) {
		final List<Integer> locationKeys = new ArrayList<Integer>();
		final Set<Integer> locationKeysSet = new TreeSet<Integer>();

		locationKeysSet.add(inscription.getSpotKey());
		locationKeys.addAll(locationKeysSet);
		return locationKeys;
	}

	private static List<Integer> findInsulaLocation(final Inscription inscription) {
		final List<Integer> locationKeys = new ArrayList<Integer>();
		locationKeys.add(inscription.getProperty().getInsula().getId());
		return locationKeys;
	}

	private static List<Integer> findInsulaLocations(final List<Inscription> inscriptions) {
		final List<Integer> locationKeys = new ArrayList<Integer>();
		if (inscriptions != null) {
			final Set<Integer> locationKeysSet = new TreeSet<Integer>();
			for (final Inscription inscription : inscriptions) {
				locationKeysSet.add(inscription.getProperty().getInsula().getId());
			}
			locationKeys.addAll(locationKeysSet);
		}
		return locationKeys;
	}

	public GraffitiDao getGraffitiDao() {
		return graffitiDao;
	}

	public void setGraffitiDao(final GraffitiDao graffitiDao) {
		this.graffitiDao = graffitiDao;
	}

	private static ArrayList<String> notationsInContent(String i) {
		ArrayList<String> notations = new ArrayList<String>();
		String content = TransformEDRContentToEpiDoc.normalize(i);
		Pattern pattern;
		Matcher matcher;

		// Contains UpperCase Characters
		pattern = Pattern.compile("([A-Z][\u0332,\u0323,\u0302]?){2,}");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			String temp = matcher.group(0);
			if (!temp.matches("[C,D,I,L,M,V,X]+"))
				notations.add("upper");
		}
		// Symbols
		pattern = Pattern.compile("\\(\\([^\\(\\)\\:]+\\)\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("sym");
			content = content.replaceAll("\\(\\([^\\(\\)\\:]+\\)\\)", "");
		}
		// Abbreviations
		pattern = Pattern.compile("[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]+\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("abbr");
		}
		// Uncertain Abbreviations
		pattern = Pattern.compile("[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]+\\?\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("uncert");
			notations.add("abbr");
		}
		// Lost Content -- dashes
		pattern = Pattern.compile("\\[(\\-[ ]?)+\\]");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("lostContent");
			content = content.replaceAll("\\[(\\-[ ]?)+\\]", "");
		}
		// Lost Content -- dots
		pattern = Pattern.compile("\\[(•[ ]?)+\\]");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("lostContent");
			content = content.replaceAll("\\[(•[ ]?)+\\]", "");
		}
		// Figural
		pattern = Pattern.compile("\\(\\(\\:[^\\(\\[\\)\\]\\:\\<\\>\\?]*\\)\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("fig");
		}
		// IntentionallyErased
		pattern = Pattern.compile("(\\[\\[|〚)([^\\s\\(\\[\\)\\]\\:\\<\\>\\,]|\\[?\\]?)*(\\]\\]|〛)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("intErased");
		}
		// Once Present
		pattern = Pattern.compile("\\[([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]||[ ]?)*\\]");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("oncePres");
		}
		// Uncertain Once Present
		pattern = Pattern.compile("\\[([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]||[ ]?)*\\?\\]");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("oncePres");
			notations.add("uncert");
		}
		// Non-standard Spellings
		pattern = Pattern.compile("[^\\s\\>\\(]+[ ]?\\(\\:[^\\s]*\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("nonStandSpell");
		}
		// Uncertain Non-standard Spellings
		pattern = Pattern.compile("[^\\s^>]*[ ]?\\(\\:[^\\s\\?]*\\?\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("nonStandSpell");
			notations.add("uncert");
		}
		// Illegible Characters
		pattern = Pattern.compile("\\++");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("illegChar");
		}
		// Letters Joined in Ligature
		pattern = Pattern.compile("(([^\\s]\u0302)+)([^\\s](\u0323|\u0332)?)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("lig");
		}
		// Lost line
		pattern = Pattern.compile("- - - - - -");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("lostLines");
		}
		// Once visible now missing
		pattern = Pattern.compile("([^\\s]\u0332)+");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("underline");
		}
		// Damaged Characters
		pattern = Pattern.compile("([^\\s]\u0323)+");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("dots");
		}
		// Uncertain Lost Characters
		pattern = Pattern.compile("\\[\\+([0-9]+)\\?\\+\\]");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("uncert");
			notations.add("illegChar");
			notations.add("oncePres");
		}
		
		pattern = Pattern.compile(".*&#60;:.*&#62;.*");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			notations.add("markup");
		}
		
		return notations;
	}

	@RequestMapping(value = "/admin/report", method = RequestMethod.GET)
	public String reportResults(final HttpServletRequest request) {
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		List<Inscription> inscriptions = searchResults(request);
		request.setAttribute("resultsLyst", inscriptions);
		request.setAttribute("searchQueryDesc", "filtering");
		populateRequestForSidebarSearch(request);
		return "admin/reports/report";
	}

}
