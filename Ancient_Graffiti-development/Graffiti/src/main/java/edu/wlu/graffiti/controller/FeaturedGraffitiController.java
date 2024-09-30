package edu.wlu.graffiti.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wlu.graffiti.bean.FeaturedInscription;
import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.Theme;
import edu.wlu.graffiti.dao.GraffitiDao;
import edu.wlu.graffiti.dao.ThemeDao;

/**
 * 
 * Handles Featured graffiti functionality
 * 
=======

/**
 * Controller for handling featured graffiti and different themes
 */
@Controller
public class FeaturedGraffitiController {
	
	@Resource
	private GraffitiDao graffitiDao;
	
	@Resource
	private ThemeDao themeDao;

	private final ArrayList<String> themesWithWarmUps = new ArrayList<String>(Arrays.asList("Food", "Gladiators",
			"Love","Occupations","Poetry","Fungraffiti"));
	
	
	/**
	 * retrieves the translation practice page
	 * @param request The HTTP request object that we use to set attributes
	 * @return the name of the translation practice page
	 */
	@RequestMapping(value = "/TranslationPractice", method = RequestMethod.GET)
	public String translationQuiz(final HttpServletRequest request) {
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		final List<Inscription> featuredTranslationGraffiti = this.graffitiDao.getFeaturedTranslationGraffiti();
		request.setAttribute("translationHits", featuredTranslationGraffiti);

		return "translationPractice";
	}
	
	/**
	 * gets a table of all themes for the featured graffiti page
	 * @param request The HTTP request object that we use to set attributes
	 * @return the name of the featured graffiti page
	 */
	@RequestMapping(value = "/featured-graffiti", method = RequestMethod.GET)
	public String featuredHits(final HttpServletRequest request) {
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		final List<Theme> themes = themeDao.getThemes();
		request.setAttribute("themes", themes);

		return "featuredGraffiti";
	}
	
	
	/**
	 * displays all figural graffiti
	 * @param request The HTTP request object that we use to set attributes
	 * @return gives a list of all figural graffiti
	 */
	@RequestMapping(value = "/themes/Figural")
	public String featuredFiguralGraffiti(final HttpServletRequest request) {
		Theme theme = themeDao.getThemeByName("Figural");
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		List<FeaturedInscription> featuredFiguralGraffiti = graffitiDao.getFeaturedInscriptionByTheme(theme.getId());
		request.setAttribute("figuralHits", featuredFiguralGraffiti);
		request.setAttribute("theme", theme);
		return "figuralGraffiti";
	}
	
	/**
	 * retrieves all graffiti with a specified theme on the featured graffiti page
	 * @param themeName the name of the theme you wish to look up
	 * @param request The HTTP request object that we use to set attributes
	 * @return gives a list of all graffiti under the specified theme
	 */
	@RequestMapping(value = "/themes/{themeName}", method = RequestMethod.GET)
	public String searchThemedGraffiti(@PathVariable String themeName, final HttpServletRequest request) {
		Theme theme = themeDao.getThemeByName(themeName);
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		List<FeaturedInscription> inscriptions = graffitiDao.getFeaturedInscriptionByTheme(theme.getId());
		request.setAttribute("inscriptions", inscriptions);
		request.setAttribute("theme", theme);
		
		// Check if there is a pre-made warmup activity for the theme
		Boolean hasWarmUp = false;
		if (themesWithWarmUps.contains(theme.getName())) {
			hasWarmUp = true;
		}
		request.setAttribute("hasWarmUp", hasWarmUp);

		return "themedGraffitiResults";
	}
	
}
