package edu.wlu.graffiti.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import edu.wlu.graffiti.bean.City;
import edu.wlu.graffiti.bean.Contribution;
import edu.wlu.graffiti.bean.DrawingTag;
import edu.wlu.graffiti.bean.FeaturedGraffitiInfo;
import edu.wlu.graffiti.bean.FeaturedInscription;
import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.Insula;
import edu.wlu.graffiti.bean.Photo;
import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.bean.Theme;
import edu.wlu.graffiti.data.rowmapper.ContributionRowMapper;
import edu.wlu.graffiti.data.rowmapper.DrawingTagRowMapper;
import edu.wlu.graffiti.data.rowmapper.FeaturedGraffitiInfoRowMapper;
import edu.wlu.graffiti.data.rowmapper.FeaturedInscriptionRowMapper;
import edu.wlu.graffiti.data.rowmapper.InscriptionRowMapper;
import edu.wlu.graffiti.data.setup.InsertFeaturedGraffiti;

/**
 * DAO for accessing information about graffiti/inscriptions
 * 
 * @editor Trevor Stalnaker
 * @editor John Schleider
 */

@Repository
public class GraffitiDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final String DURING_TEST_LIMIT = ""; // " LIMIT 50";

	private static final String ALL_DRAWINGS = "0";
	
	private static final String ORDER_BY_GRAFFITI_ID_ASC = " ORDER BY inscriptions.graffiti_id ASC;";
	
	// The following strings are prepared SQL statements that will query the database with given information

	public static final String SELECT_STATEMENT = "SELECT *, " + "inscriptions.id as local_id, "
			+ "featured_graffiti_info.commentary as gh_commentary, properties.id AS property_id, "
			+ "insula.short_name AS insula_name, cities.name AS city_name, "
			+ "cities.pleiades_id as city_pleiades_id FROM inscriptions "
			+ "LEFT JOIN figural_graffiti_info ON inscriptions.graffiti_id=figural_graffiti_info.graffiti_id "
			+ "LEFT JOIN featured_graffiti_info ON inscriptions.graffiti_id=featured_graffiti_info.graffiti_id "
			+ "LEFT JOIN properties ON inscriptions.property_id=properties.id "
			+ "LEFT JOIN insula ON properties.insula_id=insula.id "
			+ "LEFT JOIN segments ON segments.id=inscriptions.segment_id "
			+ "LEFT JOIN streets ON streets.id=segments.street_id " + "LEFT JOIN cities ON ancient_city=cities.name "
			+ "LEFT JOIN columns ON column_id=columns.id";

	private static final String FIND_BY_ALL = SELECT_STATEMENT + " WHERE UPPER(inscriptions.graffiti_id) "
			+ "LIKE UPPER(?) OR UPPER(ANCIENT_CITY) LIKE UPPER(?) OR " + "UPPER(FIND_SPOT) LIKE UPPER(?) OR "
			+ "UPPER(MEASUREMENTS) LIKE UPPER(?) OR " + "UPPER(inscriptions.WRITING_STYLE) LIKE UPPER(?) OR "
			+ "UPPER(writing_style_in_english) LIKE UPPER(?) OR " + "UPPER(LANGUAGE) LIKE UPPER(?) OR "
			+ "UPPER(lang_in_english) LIKE UPPER(?) OR " + "UPPER(CONTENT) LIKE UPPER(?) OR "
			+ "UPPER(BIBLIOGRAPHY) LIKE UPPER(?) OR " + "NUMBEROFIMAGES = ? " + ORDER_BY_GRAFFITI_ID_ASC;

	private static final String FIND_BY_FIND_SPOT = SELECT_STATEMENT + " WHERE properties.id = ?  "
			+ ORDER_BY_GRAFFITI_ID_ASC;
	
	public static final String EDR_ID_EXISTS = "SELECT COUNT(*) FROM existing_edr_ids WHERE edr_id=?";

	// need to assign property ids to the inscriptions and cross with that info.

	private static final String FIND_BY_PROPERTY_TYPE = "select *, "
			+ "properties.id AS property_id, featured_graffiti_info.commentary as gh_commentary, inscriptions.id as local_id "
			+ "from inscriptions, figural_graffiti_info, propertyTypes, properties, propertytopropertytype, insula "
			+ "where propertyTypes.id=? and propertyTypes.id=propertytopropertytype.property_type "
			+ "and properties.id=propertytopropertytype.property_id and properties.id=inscriptions.property_id "
			+ "and inscriptions.graffiti_id=properties.insula_id = insula.id "
			+ "AND inscriptions.graffiti_id=figural_graffiti_info.graffiti_id ";

	private static final String FIND_BY_CITY = SELECT_STATEMENT + " WHERE UPPER(ANCIENT_CITY) LIKE UPPER(?) "
			+ ORDER_BY_GRAFFITI_ID_ASC;

	private static final String FIND_BY_CITY_AND_INSULA = SELECT_STATEMENT + " WHERE UPPER(ANCIENT_CITY) = UPPER(?) "
			+ "AND insula.id = ? " + ORDER_BY_GRAFFITI_ID_ASC;

	private static final String FIND_BY_CITY_AND_INSULA_AND_PROPERTY = SELECT_STATEMENT
			+ " WHERE UPPER(ANCIENT_CITY) = UPPER(?) " + "AND insula.id = ? and properties.id = ? "
			+ ORDER_BY_GRAFFITI_ID_ASC;

	public static final String FIND_BY_PROPERTY = "SELECT COUNT(*) FROM inscriptions "
			+ "LEFT JOIN properties ON inscriptions.property_id=properties.id "
			+ "LEFT JOIN insula ON properties.insula_id=insula.id" + " WHERE properties.id = ? ";

	private static final String FIND_BY_CONTENT = SELECT_STATEMENT + " WHERE UPPER(CONTENT) LIKE UPPER(?) "
			+ ORDER_BY_GRAFFITI_ID_ASC;

	private static final String FIND_BY_GRAFFITI_ID = SELECT_STATEMENT
			+ " WHERE UPPER(inscriptions.graffiti_id) = UPPER(?) " + ORDER_BY_GRAFFITI_ID_ASC;

	private static final String SELECT_ALL_DRAWING_INSCRIPTIONS = "SELECT *, " + "inscriptions.id as local_id, "
			+ "featured_graffiti_info.commentary as gh_commentary, properties.id AS property_id " + "FROM inscriptions "
			+ "LEFT JOIN properties ON inscriptions.property_id=properties.id "
			+ "LEFT JOIN figural_graffiti_info ON inscriptions.graffiti_id=figural_graffiti_info.graffiti_id "
			+ "LEFT JOIN featured_graffiti_info ON inscriptions.graffiti_id=featured_graffiti_info.graffiti_id "
			+ "WHERE has_figural_component = true";

	private static final String SELECT_INSCRIPTIONS_BY_DRAWING_TAG = "SELECT *, " + "inscriptions.id AS local_id, "
			+ "featured_graffiti_info.commentary as gh_commentary, properties.id AS property_id "
			+ "FROM graffitotodrawingtags, inscriptions "
			+ "LEFT JOIN properties ON inscriptions.property_id=properties.id "
			+ "LEFT JOIN figural_graffiti_info ON inscriptions.graffiti_id=figural_graffiti_info.graffiti_id "
			+ "LEFT JOIN featured_graffiti_info ON inscriptions.graffiti_id=featured_graffiti_info.graffiti_id "
			+ "WHERE has_figural_component = true AND drawing_tag_id=(?) AND inscriptions.graffiti_id=graffitotodrawingtags.graffito_id";

	private static final String SELECT_INSCRIPTIONS_BY_THEME = "SELECT *, " + "inscriptions.id AS local_id, "
			+ "featured_graffiti_info.commentary as gh_commentary, properties.id AS property_id "
			+ "FROM graffititothemes, inscriptions " + "LEFT JOIN properties ON inscriptions.property_id=properties.id "
			+ "LEFT JOIN figural_graffiti_info ON inscriptions.graffiti_id=figural_graffiti_info.graffiti_id "
			+ "LEFT JOIN featured_graffiti_info ON inscriptions.graffiti_id=featured_graffiti_info.graffiti_id "
			+ "WHERE is_themed = true AND theme_id=(?) AND inscriptions.graffiti_id=graffititothemes.graffito_id";
	
	private static final String SELECT_FEATURED_INSCRIPTIONS_BY_THEME = "SELECT graffiti_id, theme_id, content, translation, "
			+ "cil, image, commentary FROM more_graffititothemes LEFT JOIN more_featured_graffiti_info ON more_graffititothemes.graffito_id="
			+ "more_featured_graffiti_info.graffiti_id WHERE theme_id=(?)";
	
	private static final String FIND_FEATURED_BY_GRAFFITI_ID = "SELECT * FROM more_featured_graffiti_info WHERE graffiti_id=(?)";

	private static final String SELECT_DRAWING_TAGS = "SELECT drawing_tags.id, name, description "
			+ "FROM graffitotodrawingtags, drawing_tags "
			+ "WHERE graffito_id = ? AND drawing_tags.id = graffitotodrawingtags.drawing_tag_id;";

	private static final String SELECT_FEATURED_FIGURAL_GRAFFITI = SELECT_STATEMENT
			+ " WHERE is_featured_figural = True" + ORDER_BY_GRAFFITI_ID_ASC;

	private static final String SELECT_FEATURED_TRANSLATION_GRAFFITI = SELECT_STATEMENT
			+ " WHERE is_featured_translation = True" + ORDER_BY_GRAFFITI_ID_ASC;

	private static final String SELECT_CONTRIBUTIONS_FOR_INSCRIPTION = "SELECT * FROM epidoc_contributions "
			+ "WHERE inscription_id=?";

	private static final String FIND_ALL_FACADES = SELECT_STATEMENT + " WHERE on_facade=true";

	private static final String FIND_FACADES_BY_STREET_ID = SELECT_STATEMENT
			+ " WHERE segments.street_id=? AND on_facade=true";

	private static final String FIND_FACADES_BY_SEGMENT_ID = SELECT_STATEMENT
			+ " WHERE segment_id=? AND on_facade=true";

	@Resource
	private FindspotDao propertyDao;

	@Resource
	private ThemeDao themeDao;

	@Resource
	private PhotosDao photosDao;
	
	/**
	 * This method checks if a graffiti entry exists given its EDR identification number
	 * EDR is Epigraphic Database Roma
	 * 
	 *  @param graffiti_id The EDR id string
	 *  @return boolean of whether an inscription with that id is in the database	
	 */
	public boolean EDRIDExists(String graffiti_id) {
		Integer value = jdbcTemplate.queryForObject(EDR_ID_EXISTS, Integer.class, graffiti_id);
		return value != null && value > 0;
	}

	/**
	 * Retrieves all inscriptions without any filtering
	 * 
	 * @return List of SQL inscription results
	 */
	@Cacheable("inscriptions")
	public List<Inscription> getAllInscriptions() {
		List<Inscription> results = jdbcTemplate.query(SELECT_STATEMENT + DURING_TEST_LIMIT, new InscriptionRowMapper());
		addOtherInfo(results);
		return results;
	}

	/**
	 * Retrieves all featured graffiti with figures without additional filters
	 * 
	 * @return List of SQL inscription results
	 */
	public List<Inscription> getFeaturedFiguralGraffiti() {
		List<Inscription> results = jdbcTemplate.query(SELECT_FEATURED_FIGURAL_GRAFFITI, new InscriptionRowMapper());
		addOtherInfo(results);
		return results;
	}
	
	/**
	 * Retrieves all featured text graffiti without additional filters
	 * 
	 * @return List of SQL inscription results
	 */
	public List<Inscription> getFeaturedTranslationGraffiti() {
		List<Inscription> results = jdbcTemplate.query(SELECT_FEATURED_TRANSLATION_GRAFFITI, new InscriptionRowMapper());
		addOtherInfo(results);
		return results;
	}
	
	/**
	 * Retrieves all graffiti where the search result is like the following fields
	 * 
	 * graffiti EDR ID
	 * inscription text
	 * city name
	 * find spot name
	 * measurements
	 * writing style
	 * language
	 * content
	 * bibliography
	 * number of images
	 * 
	 * @param searchArg The string search term
	 * @return List of SQL inscription results
	 */
	public List<Inscription> getInscriptions(final String searchArg) {
		final Object[] searchArgs = new String[9];
		Arrays.fill(searchArgs, "%" + searchArg + "%");
		List<Inscription> results = jdbcTemplate.query(FIND_BY_ALL, new InscriptionRowMapper(), searchArgs);
		addOtherInfo(results);
		return results;
	}

	/**
	 * Retrieves inscriptions with the matching property type 
	 * 
	 * @param propertyType The integer code number for property type 
	 * @return List of SQL inscription results
	 */
	public List<Inscription> getInscriptionsByPropertyType(final int propertyType) {
		List<Inscription> results = jdbcTemplate.query(FIND_BY_PROPERTY_TYPE, new InscriptionRowMapper(), propertyType);
		addOtherInfo(results);
		return results;
	}

	/**
	 * Retrieves inscriptions with the matching find spot
	 * 
	 * @param property_id The integer code number for the property type
	 * @return List of SQL inscription results
	 */
	public List<Inscription> getInscriptionsByFindSpot(final int property_id) {
		List<Inscription> results = jdbcTemplate.query(FIND_BY_FIND_SPOT, new InscriptionRowMapper(), property_id);
		addOtherInfo(results);
		return results;
	}

	/**
	 * Retrieves inscriptions with content that matches the search input
	 * 
	 * @param searchArg The search input (inscription's language)
	 * @return List of SQL inscription results
	 */
	public List<Inscription> getInscriptionsByContent(final String searchArg) {
		List<Inscription> results = jdbcTemplate.query(FIND_BY_CONTENT, new InscriptionRowMapper(), "%" + searchArg + "%");
		addOtherInfo(results);
		return results;
	}
	
	/**
	 * Retrieves an inscription with the given ID
	 * First performs a check to see if it exists
	 * 
	 * @param graffitiID The string of the graffiti's ID
	 * @return List of SQL inscription results
	 */
	@Cacheable(cacheNames = "inscriptions", key = "#graffitiID")
	public Inscription getInscriptionByID(final String graffitiID) {
		List<Inscription> results = jdbcTemplate.query(FIND_BY_GRAFFITI_ID, new InscriptionRowMapper(), graffitiID);
		
		
		if (results.size() == 0) {
			return null;
		}
		Inscription result = results.get(0);
		addOtherInfo(result);
		return result;
	}

	/**
	 * Retrieves inscriptions with figures if those figures' drawing tag matches the one inputted
	 * Returns all figural inscriptions if all drawings tag is selected 
	 * 
	 * @param drawingTagId The tag that determines which figures will be returned
	 * @return List of SQL inscription results
	 */
	public List<Inscription> getInscriptionByDrawing(String drawingTagId) {
		List<Inscription> results = null;
		if (drawingTagId.equals(ALL_DRAWINGS)) {
			results = jdbcTemplate.query(SELECT_ALL_DRAWING_INSCRIPTIONS, new InscriptionRowMapper());
		} else {
			results = jdbcTemplate.query(SELECT_INSCRIPTIONS_BY_DRAWING_TAG, new InscriptionRowMapper(),
					Integer.parseInt(drawingTagId));
		}

		addOtherInfo(results);

		return results;
	}

	/**
	 * Retrieves inscriptions by their theme id
	 * 
	 * @param themeId The integer code for the theme id
	 * @return List of SQL inscription results
	 */
	public List<Inscription> getInscriptionByTheme(int themeId) {
		List<Inscription> results = null;
		results = jdbcTemplate.query(SELECT_INSCRIPTIONS_BY_THEME, new InscriptionRowMapper(), themeId);
		return results;
	}
	
	/**
	 * Retrieves featured graffiti by ID
	 * 
	 * @param graffitiID The string of the graffiti's ID
	 * @return List of SQL inscription results
	 */
	public FeaturedInscription getFeaturedInscriptionByID(final String graffitiID) {
		List<FeaturedInscription> results = jdbcTemplate.query(FIND_FEATURED_BY_GRAFFITI_ID, new FeaturedInscriptionRowMapper(), graffitiID);
		if (results.size() == 0) {
			return null;
		}
		FeaturedInscription result = results.get(0);
		addInDatabaseToFeaturedInscription(result);
		return result;
	} 
	
	/**
	 * Retrieves featured inscriptions by their theme id
	 * 
	 * @param themeId The integer code for the theme id
	 * @return List of SQL inscription results
	 */
	public List<FeaturedInscription> getFeaturedInscriptionByTheme(int themeId) {
		List<FeaturedInscription> results = null;
		results = jdbcTemplate.query(SELECT_FEATURED_INSCRIPTIONS_BY_THEME, new FeaturedInscriptionRowMapper(), themeId);
		addInDatabaseToFeaturedInscriptions(results);
		return results;
	}
	
	/**
	 * Adds featured inscriptions into the database
	 * 
	 * @param inscriptions
	 */
	private void addInDatabaseToFeaturedInscriptions(List<FeaturedInscription> inscriptions) {
		for (FeaturedInscription i : inscriptions) {
			addInDatabaseToFeaturedInscription(i);
		}
	}
	
	/**
	 * Adds a featured inscription into the database
	 * 
	 * @param inscriptions
	 */
	private void addInDatabaseToFeaturedInscription(FeaturedInscription inscription) {
		if (getInscriptionByID(inscription.getGraffitiId()) == null) {
			inscription.setInDatabase(false);
		}
		else {
			inscription.setInDatabase(true);
		}
	}

	/*
	 * Possibly should be removed; no longer used? public List<Inscription>
	 * getInscriptionById(String id) { List<Inscription> results = query(FIND_BY_ID,
	 * new InscriptionRowMapper(), id); addOtherInfo(results); return results; }
	 */

	/**
	 * Adds the drawing tag information to the Inscription object
	 * 
	 * @param inscription
	 */
	@Cacheable("drawingTags")
	private void retrieveDrawingTagsForInscription(Inscription inscription) {
		List<DrawingTag> drawingTags = jdbcTemplate.query(SELECT_DRAWING_TAGS, new DrawingTagRowMapper(),
				inscription.getGraffitiId());
		inscription.getFiguralInfo().addDrawingTags(drawingTags);
	}

	/**
	 * Gets the themes associated with an inscription.
	 * 
	 * @param inscription
	 */
	private void retrieveThemesForInscription(Inscription inscription) {
		List<Theme> themes = themeDao.getThemesByID(inscription.getGraffitiId());
		inscription.setThemes(themes);
	}

	/**
	 * Adds a property (location) to an existing inscription by pulling it from database
	 * 
	 * @param inscription
	 */
	private void addPropertyToInscription(Inscription inscription) {
		// TODO: SPECIAL HANDLING until we have the info fixed.
		if (inscription.getProperty().getId() == 0) {
			// System.out.println("AGP Property_ID = 0");
			Property property = new Property();
			property.setId(0);
			property.setPropertyName("Not found");
			property.setPropertyNumber("0");
			Insula insula = new Insula();
			insula.setModernCity(inscription.getAncientCity());
			insula.setShortName("Unknown");
			City city = new City();
			city.setName(inscription.getAncientCity());
			insula.setCity(city);
			property.setInsula(insula);
			inscription.setProperty(property);
		} else {
			int id = inscription.getProperty().getId();
			Property property = propertyDao.getPropertyById(id);
			inscription.setProperty(property);
		}
	}

	/**
	 * Retrieves inscriptions by city name
	 * 
	 * @param city
	 * @return List of SQL inscription results
	 */
	// @Cacheable("inscriptions")
	public List<Inscription> getInscriptionsByCity(final String city) {
		List<Inscription> results = jdbcTemplate.query(FIND_BY_CITY, new InscriptionRowMapper(), city);
		addOtherInfo(results);
		return results;
	}

	/**
	 * Retrieves inscriptions with a matching city name and insula ID
	 * 
	 * @param city The string of the city's name
	 * @param insula_id An integer ID for the insula within that city
	 * @return List of SQL inscription results
	 */
	// @Cacheable("inscriptions")
	public List<Inscription> getInscriptionsByCityAndInsula(String city, int insula_id) {
		List<Inscription> results = jdbcTemplate.query(FIND_BY_CITY_AND_INSULA, new InscriptionRowMapper(), city, insula_id);
		addOtherInfo(results);
		return results;
	}

	/**
	 * Retrieves insctiptions with a matching city, insula, and property number
	 * 
	 * @param city The string of the city's name
	 * @param insula_id An integer for the insula within the city 
	 * @param property_id An integer for the property within the insula
	 * @return List of SQL inscription results
	 */
	// @Cacheable("inscriptions")
	public List<Inscription> getInscriptionsByCityAndInsulaAndPropertyNumber(String city, int insula_id,
			int property_id) {
		List<Inscription> results = jdbcTemplate.query(FIND_BY_CITY_AND_INSULA_AND_PROPERTY, new InscriptionRowMapper(), city,
				insula_id, property_id);
		addOtherInfo(results);
		return results;
	}

	// Return all the graffiti on facades in the data base
	public List<Inscription> getFacades() {
		List<Inscription> results = jdbcTemplate.query(FIND_ALL_FACADES, new InscriptionRowMapper());
		addOtherInfo(results);
		return results;
	}

	// Return All the Graffiti Written on Facades of this street
	public List<Inscription> getFacadesByStreet(Integer street_id) {
		List<Inscription> results = jdbcTemplate.query(FIND_FACADES_BY_STREET_ID, new InscriptionRowMapper(), street_id);
		addOtherInfo(results);
		return results;
	}

	// Return All the Graffiti Written on Facades of this segment
	public List<Inscription> getFacadesBySegment(Integer segment_id) {
		List<Inscription> results = jdbcTemplate.query(FIND_FACADES_BY_SEGMENT_ID, new InscriptionRowMapper(), segment_id);
		addOtherInfo(results);
		return results;
	}

	/**
	 * Adds additional information to the inscription
	 * 
	 * @param inscription
	 */
	private void addOtherInfo(Inscription inscription) {
		retrieveDrawingTagsForInscription(inscription);
		addPropertyToInscription(inscription);
		retrieveThemesForInscription(inscription);
		addPhotos(inscription);
		addContributions(inscription);
	}

	/**
	 * Retrieves photos of the inscription and adds them to the inscription object
	 * 
	 * @param inscription The inscription to which the photo pertains
	 */
	private void addPhotos(Inscription inscription) {
		List<Photo> photos = photosDao.getPhotosByID(inscription.getGraffitiId());
		inscription.setPhotos(photos);
	}

	/**
	 * Retrieves contributions data from inscription and adds them to the inscription object
	 * 
	 * @param inscription The inscription to which the contributions data pertains
	 */
	private void addContributions(Inscription inscription) {
		List<Contribution> con = jdbcTemplate.query(SELECT_CONTRIBUTIONS_FOR_INSCRIPTION, new ContributionRowMapper(),
				inscription.getGraffitiId());
		inscription.setContributions(con);
	}

	/**
	 * Adds additional information to each inscription
	 * 
	 * @param inscriptions
	 */
	private void addOtherInfo(List<Inscription> inscriptions) {
		for (Inscription i : inscriptions) {
			addOtherInfo(i);
		}
	}

	// update edr inscription function
	@CacheEvict(value = "inscriptions", key = "#graffitiID")
	public void updateEdrInscription(ArrayList<String> fields, String graffitiID) {
		String sql = "UPDATE inscriptions "
				+ "SET ancient_city=(?),find_spot=(?), language=(?), content=(?),bibliography=(?), writing_style=(?), apparatus=(?) "
				+ "where graffiti_id=(?)";
		fields.add(graffitiID);
		jdbcTemplate.update(sql, fields.toArray());
	}

	// update inscriptions
	@CacheEvict(value = "inscriptions", key = "#graffitiID")
	public void updateAgpInscription(List<Object> fields, String graffitiID) {
		String sql = "UPDATE inscriptions "
				+ "SET caption=?, content_translation= ?, cil=?, langner=?, content_epidocified=?, height_from_ground=(?),graffito_height=(?), "
				+ "graffito_length=?, letter_height_min=?, letter_height_max=?, individual_letter_heights=?, "
				+ " commentary=?, has_figural_component = ?,  is_featured_figural=?, is_featured_translation=?, is_themed=? "
				+ "where graffiti_id=(?)";
		fields.add(graffitiID);
		jdbcTemplate.update(sql, fields.toArray());
	}

	// update featured graffiti info
	public void updateFeaturedGraffitiInfo(String graffitiID, String commentary, String preferredImage) {

		String selectSQL = InsertFeaturedGraffiti.SELECT_GH_INFO;
		String updateSQL = InsertFeaturedGraffiti.UPDATE_GH_INFO;
		String insertSQL = InsertFeaturedGraffiti.INSERT_GH_INFO;

		List<FeaturedGraffitiInfo> inscriptions = jdbcTemplate.query(selectSQL, new FeaturedGraffitiInfoRowMapper(), graffitiID);

		if (inscriptions.size() == 1) {
			// entry already exists, so update it.
			jdbcTemplate.update(updateSQL, commentary, preferredImage, graffitiID);

		} else {
			// insert
			jdbcTemplate.update(insertSQL, graffitiID, commentary, preferredImage);
		}
	}

	// update graffito2drawingtags
	public void updateDrawingTags(List<String> fields) {
		String sql = "UPDATE graffito2drawingtags " + "SET drawing_tag_id=(?) " + "where graffiti_id=(?)";
		jdbcTemplate.update(sql, fields.toArray());
	}

	/** 
	 * Changes the themes of inscriptions given the inscriptions' IDs
	 * 
	 * @param fields
	 */
	public void updateThemes(List<String> fields) {
		String sql = "UPDATE graffititothemes " + "SET theme_id=(?) " + "where graffito_id=(?)";
		jdbcTemplate.update(sql, fields.toArray());
	}

	// insert edr inscription
	public void insertEdrInscription(List<ArrayList<String>> inscriptions) {
		String sql = "INSERT INTO inscriptions "
				+ "(graffiti_id,ancient_city,find_spot, language, content,bibliography, writing_style, apparatus)"
				+ " VALUES (?,?,?,?,?,?,?,?)";
		for (ArrayList<String> fields : inscriptions) {
			jdbcTemplate.update(sql, fields.toArray());
		}
	}

	// insert inscriptions
	public void insertAgpInscription(ArrayList<ArrayList<String>> inscriptions) {
		String sql = "INSERT INTO inscriptions "
				+ "(graffiti_id,floor_to_graffito_height, content_translation, graffito_height, graffito_length, letter_height_min, letter_height_max, cil)"
				+ " VALUES (?,?,?,?,?,?,?,?)";
		for (ArrayList<String> fields : inscriptions) {
			jdbcTemplate.update(sql, fields.toArray());
		}
	}

	/**
	 * Removes drawing tags from graffiti entries by IDs
	 * 
	 * @param graffiti_id The id of the graffiti from which you want to remove the drawing tags 
	 */
	public void clearDrawingTags(String graffiti_id) {
		String sql = "DELETE FROM graffitotodrawingtags " + "WHERE graffito_id=(?)";

		jdbcTemplate.update(sql, graffiti_id);

	}

	/**
	 * Removes themes from graffiti entries by IDs
	 * 
	 * @param graffiti_id The id of the graffiti from which you want to remove the themes
	 */
	public void clearThemes(String graffiti_id) {
		String sql = "DELETE FROM graffititothemes " + "WHERE graffito_id=(?)";

		jdbcTemplate.update(sql, graffiti_id);

	}

	// insert drawing tags
	public void insertDrawingTags(String graffiti_id, String[] dts) {
		String sql = "INSERT INTO graffitotodrawingtags " + "(graffito_id,drawing_tag_id)" + " VALUES (?,?)";
		for (String dt : dts) {
			jdbcTemplate.update(sql, new Object[] { graffiti_id, Integer.parseInt(dt) });
		}
	}

	/**
	 * Associates themes with graffiti entries selected by IDs
	 * 
	 * @param graffiti_id The IDs of the graffiti with which you want to associate the themes
	 * @param themes The themes you want to associate with the graffiti
	 */
	public void insertThemes(String graffiti_id, String[] themes) {
		String sql = "INSERT INTO graffititothemes " + "(graffito_id,theme_id)" + " VALUES (?,?)";
		for (String theme : themes) {
			jdbcTemplate.update(sql, new Object[] { graffiti_id, Integer.parseInt(theme) });
		}
	}

	/**
	 * Associates drawing information with certain graffiti given their IDs
	 * 
	 * @param drawingDescriptionLatin The description of the drawing(s) in Latin
	 * @param drawingDescriptionEnglish The description of the drawing(s) in English
	 * @param graffitiID The IDs of the graffiti with which you want to associate the drawing info 
	 */
	public void updateDrawingInfo(String drawingDescriptionLatin, String drawingDescriptionEnglish, String graffitiID) {
		String checkSQL = "SELECT count(graffiti_id) FROM figural_graffiti_info WHERE graffiti_id = ?";
		Integer count = jdbcTemplate.queryForObject(checkSQL, Integer.class, graffitiID);

		if (count == 0) {
			String isql = "INSERT INTO figural_graffiti_info (graffiti_id) values (?) ";
			jdbcTemplate.update(isql, graffitiID);
		}

		String sql = "UPDATE figural_graffiti_info SET  description_in_latin = ?, description_in_english = ? "
				+ "WHERE graffiti_id = ?";
		jdbcTemplate.update(sql, new Object[] { drawingDescriptionLatin, drawingDescriptionEnglish, graffitiID });

	}

	/**
	 * Associates contribution data with a graffito given their IDs
	 * 
	 * @param inscription_id The ID of the inscription with which the contribution data is associated
	 * @param user_name The name of the contributor
	 * @param comment A comment about the contribution
	 * @param date The date this occurred
	 */
	public void insertContribution(String inscription_id, String user_name, String comment, String date) {
		String sql = "INSERT INTO epidoc_contributions (inscription_id, user_name, comment, date_modified) "
				+ "VALUES ('" + inscription_id + "', '" + user_name + "', '" + comment + "', '" + date + "')";
		jdbcTemplate.update(sql);

	}

}
