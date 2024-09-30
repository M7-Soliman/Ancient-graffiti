package edu.wlu.graffiti.dao;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import edu.wlu.graffiti.data.rowmapper.*;

/**
 * Class to extract information for reports
 * 
 * @author Ana Estrada
 * @author Scott Walters
 * @author Trevor Stalnaker
 * @author Grace MacDonald
 * @author Jared Cordova
 * @author Jack Sorenson
 * @author Connor Lehman
 */

@Repository
public class ReportDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static String MISSING_DATA_TEXTUAL = "SELECT graffiti_id, cil, caption, content, "
			+ "content_translation, bibliography FROM inscriptions "
			+ "WHERE (TRIM(caption) IS NULL OR TRIM(content_translation) IS NULL OR "
			+ "(NOT bibliography LIKE '%http://ancientgraffiti.org%')) "
			+ "AND NOT REGEXP_REPLACE(content, '\\s+$', '') LIKE '((:%))' "
			+ "AND NOT ancient_city = 'Smyrna' ORDER BY ancient_city, cil";

	private static String MISSING_DATA_FIGURAL = "SELECT inscriptions.graffiti_id, cil, langner, caption, "
			+ "content, drawing_tags.name, description_in_english, bibliography FROM inscriptions "
			+ "FULL OUTER JOIN figural_graffiti_info ON inscriptions.graffiti_id=figural_graffiti_info.graffiti_id "
			+ "FULL OUTER JOIN graffitotodrawingtags ON graffitotodrawingtags.graffito_id=inscriptions.graffiti_id "
			+ "FULL OUTER JOIN drawing_tags ON graffitotodrawingtags.drawing_tag_id=drawing_tags.id "
			+ "WHERE content LIKE '%((:%))%' AND ((NOT bibliography LIKE '%http://ancientgraffiti.org%') "
			+ "OR TRIM(caption) IS NULL OR TRIM(description_in_english) IS NULL OR "
			+ "TRIM(description_in_latin) IS NULL OR TRIM(drawing_tags.name) IS NULL) "
			+ "AND (NOT ancient_city='Smyrna') "
			+ "ORDER BY (CASE WHEN langner IS NULL THEN 0 ELSE 1 END), LENGTH(langner), langner";

	private static String UNRECOGNIZED_FIG = "SELECT graffiti_id, cil, content,  "
			+ "caption, ancient_city FROM inscriptions WHERE has_figural_component=false "
			+ " AND content LIKE '%((:%))%' ORDER BY ancient_city, cil";

	private static String ALL_FIG = "SELECT inscriptions.graffiti_id, cil, Langner, ancient_city, "
			+ "english_property_name, drawing_tags.name, caption, content, description_in_latin, "
			+ "description_in_english, find_spot, inscriptions.commentary, contributors FROM inscriptions "
			+ "JOIN figural_graffiti_info ON inscriptions.graffiti_id=figural_graffiti_info.graffiti_id "
			+ "JOIN properties ON inscriptions.property_id=properties.id "
			+ "JOIN graffitotodrawingtags ON graffitotodrawingtags.graffito_id=inscriptions.graffiti_id "
			+ "JOIN drawing_tags ON graffitotodrawingtags.drawing_tag_id=drawing_tags.id "
			+ "ORDER BY ancient_city, langner";

	private static String ALL_TXT = "SELECT graffiti_id, cil, langner, caption, content, "
			+ "content_translation, find_spot, commentary, contributors FROM inscriptions "
			+ "WHERE has_figural_component=false AND NOT content LIKE '%((:%))%' "
			+ "ORDER BY ancient_city, graffiti_id";

	private static String MISSING_LANGNER = "SELECT inscriptions.graffiti_id, cil, langner, "
			+ "description_in_latin, description_in_english, find_spot FROM inscriptions "
			+ "JOIN figural_graffiti_info ON inscriptions.graffiti_id = figural_graffiti_info.graffiti_id "
			+ "WHERE bibliography LIKE '%Langner%' "
			+ "ORDER BY (CASE WHEN langner IS NULL THEN 0 ELSE 1 END), LENGTH(langner), langner";
	
	private static String MISSING_FINDSPOT = "SELECT graffiti_id, cil, content, find_spot from inscriptions "
											+ "where property_id is NULL and segment_id is NULL";
	
	
	private static String UNCONFIRMED_POETRY = "SELECT inscriptions.graffiti_id, inscriptions.content, inscriptions.apparatus, poetic_graffiti_info.meter,"
			+ " poetic_graffiti_info.author FROM inscriptions JOIN poetic_graffiti_info ON"
			+ " inscriptions.graffiti_id = poetic_graffiti_info.graffiti_id WHERE inscriptions.is_poetic = true AND poetic_graffiti_info.confirmed = false";
	
	private static String CONFIRMED_POETRY = "SELECT inscriptions.graffiti_id, inscriptions.content, inscriptions.apparatus, poetic_graffiti_info.meter,"
			+ " poetic_graffiti_info.author FROM inscriptions JOIN poetic_graffiti_info ON"
			+ " inscriptions.graffiti_id = poetic_graffiti_info.graffiti_id WHERE inscriptions.is_poetic = true AND poetic_graffiti_info.confirmed = true";
	
	private static String UPDATE_POEM = "UPDATE poetic_graffiti_info SET author = ?, confirmed = true WHERE graffiti_id = ?";
	
	/*Had to use max for ancient graffiti_id in the query otherwise 
	 * we ran into issues with the Group By statement. It required
	 * that we include it in the Group By or use an aggregate function.*/
	private static String FIGURAL_CAPTION_OCCURRENCES = "SELECT caption, COUNT(*) AS occurrences,"
			+ " max(graffiti_id) as edr_id FROM"
			+ " inscriptions WHERE has_figural_component = true"
			+ " GROUP BY caption ORDER BY occurrences DESC";
	
	/*Need this variable for the city selector in the report*/
	private static final String ALL_CITIES = "All";
	private static String FIGURAL_CAPTION_OCCURRENCES_CITY = "SELECT caption, COUNT(*) AS occurrences,"
			+ " max(graffiti_id) as edr_id FROM "
			+ " inscriptions WHERE has_figural_component = true AND ancient_city=?"
			+ " GROUP BY caption ORDER BY occurrences DESC";
	
	private static String FIGURAL_TERMS = "SELECT caption, COUNT(*) AS occurrences,"
			+ " max(graffiti_id) as edr_id FROM"
			+ " inscriptions WHERE has_figural_component = true"
			+ " GROUP BY caption ORDER BY occurrences DESC"; //TODO sql query for figural terms
	
	public List<List<String>> getMissingInfo() {
		List<List<String>> missingInfo = jdbcTemplate.query(MISSING_DATA_TEXTUAL, new ExportMissingRowMapper());
		return missingInfo;
	}

	public List<List<String>> getMissingFiguralInfo() {
		List<List<String>> missingInfo = jdbcTemplate.query(MISSING_DATA_FIGURAL, new ExportMissingFiguralInfoRowMapper());
		return missingInfo;
	}

	public List<List<String>> getUnrecognizedFig() {
		List<List<String>> unidentifiedFig = jdbcTemplate.query(UNRECOGNIZED_FIG, new ExportFiguralMapper());
		return unidentifiedFig;
	}

	public List<List<String>> getAllFig() {
		List<List<String>> allFigData = jdbcTemplate.query(ALL_FIG, new ExportAllFigMapper());
		return allFigData;
	}

	public List<List<String>> getAllTxt() {
		List<List<String>> allTextData = jdbcTemplate.query(ALL_TXT, new ExportTextMapper());
		return allTextData;
	}



	public List<List<String>> getMissingLangner() {
		List<List<String>> langnerData = jdbcTemplate.query(MISSING_LANGNER, new ExportLangnerMapper());
		return langnerData;
	}
	
	public List<List<String>> getMissingFindspot() {
		List<List<String>> findspotData = jdbcTemplate.query(MISSING_FINDSPOT, new ExportFindspotMapper());
		return findspotData;
	}
	
	public List<List<String>> getFiguralCaptionOccurrences(String city) {
		List<List<String>> figuralCaptionOccurrences = null;
		if (city.equals(ALL_CITIES)) {
			figuralCaptionOccurrences = jdbcTemplate.query(FIGURAL_CAPTION_OCCURRENCES, new FiguralCaptionRowMapper());
		} else {
			figuralCaptionOccurrences = jdbcTemplate.query(FIGURAL_CAPTION_OCCURRENCES_CITY, new FiguralCaptionRowMapper(), city);
		}
		return figuralCaptionOccurrences;
	}
	
	public List<List<String>> getUnconfirmedPoetry() {
		List<List<String>> unconfirmedPoetry = jdbcTemplate.query(UNCONFIRMED_POETRY, new ExportPoetryMapper());
		return unconfirmedPoetry;
	}
	
	public List<List<String>> getConfirmedPoetry() {
		List<List<String>> confirmedPoetry = jdbcTemplate.query(CONFIRMED_POETRY, new ExportConfirmedPoetryRowMapper());
		return confirmedPoetry;
	}
	
	public boolean updatePoetry(String id, String author) {

		return (jdbcTemplate.update(UPDATE_POEM, author, id) > 0);
		
	}
	
	
}