package edu.wlu.graffiti.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wlu.graffiti.bean.DrawingTag;
import edu.wlu.graffiti.bean.IndexTerm;
import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.Theme;
import edu.wlu.graffiti.dao.DisplayTermsDao;
import edu.wlu.graffiti.dao.DrawingTagsDao;
import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.GraffitiDao;
import edu.wlu.graffiti.dao.IndexTermDao;
import edu.wlu.graffiti.dao.InsulaDao;
import edu.wlu.graffiti.dao.PropertyTypesDao;
import edu.wlu.graffiti.dao.ThemeDao;
import edu.wlu.graffiti.dao.ReportDao;

/*
 * Handles admin functionality
 */

@Controller
public class AdminController {

	// The @Resource injects an instance of the GraffitiDao at runtime. The
	// GraffitiDao instance is defined in graffiti-servlet.xml.
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
	private ThemeDao themeDao;

	@Resource
	private DisplayTermsDao displayTermsDao;

	@Resource
	private ReportDao reportDao;

	@Resource
	private IndexTermDao indexTermDao;

	// TODO Use resource by adding the notable authors as a Dao instance variable
//	List of given notable authors commonly associated with poetic graffiti
	private String[] notableAuthors = { "Ennius", "Lucretius", "Ovid", "Propertius", "Seneca", "Tibullus", "Vergil" };

	// TODO
	@RequestMapping(value = "/AdminFunctions", method = RequestMethod.GET)
	public String adminFunctions(final HttpServletRequest request) {
		return "admin/AdminFunctions";
	}

	@RequestMapping(value = "/admin/editGraffito", method = RequestMethod.GET)
	public String editGraffito(final HttpServletRequest request) {
		String id = request.getParameter("graffitiID");

		if (id == null || id.equals("")) {
			// request.setAttribute("msg", "Please enter a Graffiti ID");
			return "admin/editGraffito";
		}

		Inscription inscription = graffitiDao.getInscriptionByID(id);

		if (inscription == null) {
			request.setAttribute("msg", "Not a valid Graffiti ID");
			return "admin/editGraffito";
		}

		request.setAttribute("graffito", inscription);

		addDrawingTagsAndThemesToRequest(request, inscription);

		return "admin/updateGraffito";

	}

	@RequestMapping(value = "/admin/modifyIgnoreList", method = { RequestMethod.POST, RequestMethod.GET })
	public String modifyIgnoreList(final HttpServletRequest request) throws SQLException {
		// handles pulling the information from the database and sending it to the front
		// end
		// this.displayTermsDao.populateIndex();

		// handles pulling the information from the front end and sending the updates to
		// the database
		HttpSession session = request.getSession();
		String userRole = (String) session.getAttribute("role");
		if (userRole.equals("admin")) {

			if (request.getParameterValues("displayList") != null) {
				// System.out.println(request.getParameterValues("displayList").length);
				this.displayTermsDao.setIndexTerms(request.getParameterValues("displayList"));
			}

			if (request.getParameterValues("bufferList") != null) {
				this.displayTermsDao.setBufferTerms(request.getParameterValues("bufferList"));
			}

			if (request.getParameterValues("hiddenList") != null) {
				this.displayTermsDao.setIgnoreTerms(request.getParameterValues("hiddenList"));
			}

			List<String> displayTerms = this.displayTermsDao.getIndexTerms();
			List<String> bufferTerms = this.displayTermsDao.getBufferTerms();
			List<String> ignoreTerms = this.displayTermsDao.getIgnoreTerms();
			displayTerms.sort(null);
			bufferTerms.sort(null);
			ignoreTerms.sort(null);
			request.setAttribute("displayingTerms", displayTerms);
			request.setAttribute("bufferTerms", bufferTerms);
			request.setAttribute("ignoredTerms", ignoreTerms);

			return "admin/modifyIgnoreList";
		} else {
			request.setAttribute("msg", "Editors cannot modify ignore list");
			return "admin/admin_page";
		}
	}

	// Update a graffito page - sharmas
	@RequestMapping(value = "/admin/updateGraffito", method = RequestMethod.GET)
	public String updateGraffito(final HttpServletRequest request) {

		String id = request.getParameter("graffitiID");
		if (id == null || id.equals("")) {
			request.setAttribute("msg", "Please enter a Graffiti ID");
			return "admin/editGraffito";
		}

		request.getSession().setAttribute("graffitiID", id);

		Inscription inscription = graffitiDao.getInscriptionByID(id);

		if (inscription == null) {
			request.setAttribute("msg", "Not a valid Graffiti ID");
			return "admin/editGraffito";
		}

		request.setAttribute("graffito", inscription);

		addDrawingTagsAndThemesToRequest(request, inscription);

		return "admin/updateGraffito";

	}

	// maps to inputData.jsp page which is used to input inscription to the
	// database using a csv file
	@RequestMapping(value = "/admin/inputData", method = RequestMethod.GET)
	public String inputData(final HttpServletRequest request) {
		return "admin/inputData";
	}

	@RequestMapping(value = "/admin/inputData", method = RequestMethod.POST)
	public String handleInputData(final HttpServletRequest request) {
		System.out.println("post input data");
		return "admin/inputDataComplete";
	}

	private void addDrawingTagsAndThemesToRequest(final HttpServletRequest request, Inscription inscription) {
		Set<DrawingTag> drawingTags = inscription.getFiguralInfo().getDrawingTags();
		List<Integer> drawingTagIds = new ArrayList<Integer>();

		for (DrawingTag i : drawingTags) {
			int dtId = i.getId();
			drawingTagIds.add(dtId);
		}

		List<Theme> themes = inscription.getThemes();
		List<Integer> themeIds = new ArrayList<Integer>();
		List<Integer> allThemeIds = themeDao.getAllThemeIds();

		for (Theme t : themes) {
			int tId = t.getId();
			themeIds.add(tId);
		}

		request.setAttribute("drawingTags", drawingTagsDao.getDrawingTags());
		request.setAttribute("drawingTagIds", drawingTagIds);
		request.setAttribute("themes", themeDao.getThemes());
		request.setAttribute("inscriptionThemeIds", themeIds);
		request.setAttribute("allThemeIds", allThemeIds);
	}

	// Update a graffito controller
	@RequestMapping(value = "/admin/updateGraffito", method = RequestMethod.POST)
	public String adminUpdateGraffito(final HttpServletRequest request) {
		// updating AGP Inscription Information
		String graffitiID = (String) request.getSession().getAttribute("graffitiID");

		// updating AGP Inscriptions
		String summary = request.getParameter("summary");
		String commentary = request.getParameter("commentary");
		String cil = request.getParameter("cil");
		String langner = request.getParameter("langner");
		String epidoc = request.getParameter("epidocContent");
		String floor_to_graffito_height = request.getParameter("floor_to_graffito_height");
		String content_translation = request.getParameter("content_translation");
		String graffito_height = request.getParameter("graffito_height");
		String graffito_length = request.getParameter("graffito_length");
		String letter_height_min = request.getParameter("letter_height_min");
		String letter_height_max = request.getParameter("letter_height_max");
		String charHeights = request.getParameter("character_heights");
		String figural = request.getParameter("figural");
		String ghFig = request.getParameter("gh_fig");
		String ghTrans = request.getParameter("gh_trans");
		String theme = request.getParameter("themed");

		boolean hasFiguralComponent = false;
		boolean isfeaturedHitFig = false;
		boolean isfeaturedHitTrans = false;
		boolean isThemed = false;

		if (figural != null) {
			hasFiguralComponent = true;
		}
		if (ghFig != null) {
			isfeaturedHitFig = true;
		}
		if (ghTrans != null) {
			isfeaturedHitTrans = true;
		}
		if (theme != null) {
			isThemed = true;
		}

		List<Object> agpOneDimArrList = new ArrayList<Object>();
		agpOneDimArrList.add(summary);
		agpOneDimArrList.add(content_translation);
		agpOneDimArrList.add(cil);
		agpOneDimArrList.add(langner);
		agpOneDimArrList.add(epidoc.replaceAll("\r|\n", ""));
		agpOneDimArrList.add(floor_to_graffito_height);
		agpOneDimArrList.add(graffito_height);
		agpOneDimArrList.add(graffito_length);
		agpOneDimArrList.add(letter_height_min);
		agpOneDimArrList.add(letter_height_max);
		agpOneDimArrList.add(charHeights);
		agpOneDimArrList.add(commentary);
		agpOneDimArrList.add(hasFiguralComponent);
		agpOneDimArrList.add(isfeaturedHitFig);
		agpOneDimArrList.add(isfeaturedHitTrans);
		agpOneDimArrList.add(isThemed);

		graffitiDao.updateAgpInscription(agpOneDimArrList, graffitiID);

		String inscription_id = graffitiID;
		String user_name = (String) request.getSession().getAttribute("name");
		String comment = request.getParameter("comment_on_update");
		comment = comment.replaceAll("'", "''");
		// Calculate the current date and format it
		Calendar cal = Calendar.getInstance();
		String month = ((Integer) (cal.get(Calendar.MONTH) + 1)).toString();
		if (month.length() == 1) {
			month = "0" + month;
		}
		String day = ((Integer) (cal.get(Calendar.DAY_OF_MONTH))).toString();
		if (day.length() == 1) {
			day = "0" + day;
		}
		String year = ((Integer) (cal.get(Calendar.YEAR))).toString().substring(2);
		String date = month + "/" + day + "/" + year;

		graffitiDao.insertContribution(inscription_id, user_name, comment, date);

		if (hasFiguralComponent) {
			String drawingDescriptionLatin = request.getParameter("drawing_description_latin");
			String drawingDescriptionEnglish = request.getParameter("drawing_description_english");

			graffitiDao.updateDrawingInfo(drawingDescriptionLatin, drawingDescriptionEnglish, graffitiID);

		}

		if (isfeaturedHitFig || isfeaturedHitTrans) {
			String ghCommentary = request.getParameter("gh_commentary");
			String ghPreferredImage = request.getParameter("gh_preferred_image");
			graffitiDao.updateFeaturedGraffitiInfo(graffitiID, ghCommentary, ghPreferredImage);
		}

		// updating drawing tags
		String[] drawingTags = request.getParameterValues("drawingCategory");
		graffitiDao.clearDrawingTags(graffitiID);

		if (drawingTags != null && hasFiguralComponent) {
			graffitiDao.insertDrawingTags(graffitiID, drawingTags);
		}

		// updating themes
		String[] themes = request.getParameterValues("themes");
		graffitiDao.clearThemes(graffitiID);

		if (themes != null && isThemed) {
			graffitiDao.insertThemes(graffitiID, themes);
		}

		request.setAttribute("msg", "The graffito has been successfully updated in the database");

		Inscription element = graffitiDao.getInscriptionByID(graffitiID);

		request.setAttribute("graffito", element);

		addDrawingTagsAndThemesToRequest(request, element);

		return "admin/updateGraffito";

	}

	@RequestMapping(value = "admin/updatePoetry", method = RequestMethod.GET)
	public String confirmPoetry(final HttpServletRequest request) {
		String id = request.getParameter("graffitiID");
		String author = "unknown";
		
		
		if (request.getParameter("authorType").equals("known")) {
			
			if (request.getParameter("authorDropdown").equals("other")) {
				author = request.getParameter("otherAuthor");
			}
			else {
				author = request.getParameter("authorDropdown");
			}		
	
		}

		reportDao.updatePoetry(id, author);
		List<List<String>> unconfirmedPoetry = reportDao.getUnconfirmedPoetry();
		request.setAttribute("notableAuthors", notableAuthors);
		request.setAttribute("unconfirmedPoetry", unconfirmedPoetry);
		return "admin/reports/reportUnconfirmedPoetry";

	}

	@RequestMapping(value = "/admin/reportMissing", method = RequestMethod.GET)
	public String reportMissing(final HttpServletRequest request) {
		List<List<String>> missingInfo = reportDao.getMissingInfo();
		request.setAttribute("missingInfo", missingInfo);
		return "admin/reports/reportMissing";
	}

	@RequestMapping(value = "/admin/reportMissingFiguralData", method = RequestMethod.GET)
	public String reportMissingFiguralData(final HttpServletRequest request) {
		List<List<String>> missingInfo = reportDao.getMissingFiguralInfo();
		request.setAttribute("missingInfo", missingInfo);
		return "admin/reports/reportMissingFiguralData";
	}

	@RequestMapping(value = "/admin/reportUnidentifiedFigural", method = RequestMethod.GET)
	public String reportMissingFig(final HttpServletRequest request) {
		List<List<String>> missingFig = reportDao.getUnrecognizedFig();
		request.setAttribute("missingFig", missingFig);
		return "admin/reports/reportUnidentifiedFigural";
	}

	@RequestMapping(value = "/admin/reportConfirmedPoetry", method = RequestMethod.GET)
	public String reportConfirmedPoetry(final HttpServletRequest request) {
		List<List<String>> confirmedPoetry = reportDao.getConfirmedPoetry();
		request.setAttribute("confirmedPoetry", confirmedPoetry);
		return "admin/reports/reportConfirmedPoetry";
	}

	@RequestMapping(value = "/admin/reportUnconfirmedPoetry", method = RequestMethod.GET)
	public String reportUnconfirmedPoetry(final HttpServletRequest request) {
		List<List<String>> unconfirmedPoetry = reportDao.getUnconfirmedPoetry();
		request.setAttribute("notableAuthors", notableAuthors);
		request.setAttribute("unconfirmedPoetry", unconfirmedPoetry);
		return "admin/reports/reportUnconfirmedPoetry";
	}

	@RequestMapping(value = "/admin/reportAllFigural", method = RequestMethod.GET)
	public String reportAllFig(final HttpServletRequest request) {
		List<List<String>> allFig = reportDao.getAllFig();
		request.setAttribute("allFig", allFig);
		return "admin/reports/reportAllFigural";
	}

	@RequestMapping(value = "/admin/reportAllTxt", method = RequestMethod.GET)
	public String reportAllTxt(final HttpServletRequest request) {
		List<List<String>> allTxt = reportDao.getAllTxt();
		request.setAttribute("allTxt", allTxt);
		return "admin/reports/reportAllTxt";
	}

	@RequestMapping(value = "/admin/reportMissingLangner", method = RequestMethod.GET)
	public String reportMissingLangner(final HttpServletRequest request) {
		List<List<String>> missingLangner = reportDao.getMissingLangner();
		request.setAttribute("missingLangner", missingLangner);
		return "admin/reports/reportMissingLangner";
	}

	@RequestMapping(value = "/admin/reportMissingFindspot", method = RequestMethod.GET)
	public String reportMissingFindspot(final HttpServletRequest request) {
		List<List<String>> missingFindspot = reportDao.getMissingFindspot();
		request.setAttribute("missingFindspot", missingFindspot);
		return "admin/reports/reportMissingFindspot";
	}

	@RequestMapping(value = "/admin/reportFiguralCaptionOccurrences", method = RequestMethod.GET)
	public String reportFiguralCaptionOccurrences(final HttpServletRequest request) {
		String city = request.getParameter("city");
		if (city == null) {
			city = "All";
		}
		List<List<String>> figuralCaptionOccurrences = reportDao.getFiguralCaptionOccurrences(city);
		request.setAttribute("figuralCaptionOccurrences", figuralCaptionOccurrences);
		List<String> cities = findspotDao.getCityNames();
		request.setAttribute("cities", cities);
		return "admin/reports/reportFiguralCaptionOccurrences";
	}

	public static String cleanParam(String param) {
		if (param == null) {
			param = "(%)";
		} else {
			param = param.replaceAll(" ", "\\|");
		}
		return param;
	}

	// repeated code
	public List<IndexTerm> filterIndices(final HttpServletRequest request, String index) {
		List<IndexTerm> terms = null;
		String lang = cleanParam(request.getParameter("lang"));
		String pos = cleanParam(request.getParameter("pos"));
		String city = cleanParam(request.getParameter("city"));
		terms = this.indexTermDao.getIndexTermsByOccurrenceCategoryLanguageAndPOS(index, lang, pos, city);
		return terms;
	}

	@RequestMapping(value = "/admin/reportFiguralTerms", method = RequestMethod.GET)
	public String reportFiguralTerms(final HttpServletRequest request) {
		String index = "figural-terms";
		final List<IndexTerm> figuralTerms = filterIndices(request, index);
		request.setAttribute("figuralTerms", figuralTerms);
		request.setAttribute("index", index);
		HttpSession s = request.getSession();
		s.setAttribute("returnFromTermsURL", ControllerUtils.getFullRequest(request));
		return "admin/reports/reportFiguralTerms";
	}

}
