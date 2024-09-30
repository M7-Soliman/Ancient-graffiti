package edu.wlu.graffiti.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import edu.wlu.graffiti.bean.Theme;
import edu.wlu.graffiti.data.rowmapper.ThemeRowMapper;

/**
 * Class to extract themes from the DB
 * 
 * @author Hammad Ahmad
 * 
 */

@Repository
public class ThemeDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final String SELECT_STATEMENT = "SELECT * " + "FROM themes ORDER BY name";
	
	private static final String SELECT_BY_ID = "SELECT * " + "FROM themes WHERE theme_id = ?";
	
	private static final String SELECT_BY_NAME = "SELECT * " + "FROM themes WHERE name = ?";

	public static final String SELECT_BY_graffiti_id = "SELECT themes.theme_id, themes.name, themes.description "
			+ "FROM graffititothemes, themes WHERE graffito_id = ? "
			+ "AND themes.theme_id = graffititothemes.theme_id ORDER BY name;";

	private List<Theme> themes = null;
	
	/**
	 * 
	 * @return a list of all of the themes in the database ordered by name.
	 */
	//@Cacheable("themes")
	public List<Theme> getThemes() {
		themes = jdbcTemplate.query(SELECT_STATEMENT, new ThemeRowMapper());
		return themes;
	}
	/**
	 * 
	 * @param theme_id an integer representing the id of a theme.
	 * @return an object of Theme type representing the requested theme.
	 */
	//@Cacheable("themes")
	public Theme getThemeById(int theme_id) {
		Theme theme = jdbcTemplate.queryForObject(SELECT_BY_ID, new ThemeRowMapper(), theme_id);
		return theme;
	}
	
	/**
	 * 
	 * @param theme_name, a string representing the theme name
	 * @return an object of Theme type with the requested name.
	 */
	public Theme getThemeByName(String theme_name) {
		Theme theme = jdbcTemplate.queryForObject(SELECT_BY_NAME, new ThemeRowMapper(), theme_name);
		return theme;
	}
	/**
	 * 
	 * @param id, a string representing the theme id of several themes.
	 * @return a list of themes that have the corresponding id.
	 */
	//@Cacheable("themes")
	public List<Theme> getThemesByID(String id) {
		themes = jdbcTemplate.query(SELECT_BY_graffiti_id, new ThemeRowMapper(), id);
		return themes;
	}
	
	/**
	 * 
	 * @return a list of integers containing all theme Ids.
	 */
	public List<Integer> getAllThemeIds() {
		List<Integer> themeIds = new ArrayList<Integer>();
		for(Theme t : this.getThemes()) {
			themeIds.add(t.getId());
		}
		return themeIds;
	}

}
