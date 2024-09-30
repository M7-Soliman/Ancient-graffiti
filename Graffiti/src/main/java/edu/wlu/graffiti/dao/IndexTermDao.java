package edu.wlu.graffiti.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import edu.wlu.graffiti.bean.IndexEntry;
import edu.wlu.graffiti.bean.IndexTerm;
import edu.wlu.graffiti.data.rowmapper.IndexTermRowMapper;

/**
 * Class to query the database for the terms that populate the index.
 * 
 * @author Trevor Stalnaker
 * @author Bancks Holmes
 * 
 */

@Repository
public class IndexTermDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final String SELECT_STATEMENT = "SELECT * FROM terms ORDER BY term";
	private static final String SELECT_BY_CATEGORY = "SELECT * FROM terms WHERE category = ? ORDER BY sort_key";
	private static final String SELECT_BY_DISPLAY = "SELECT * FROM terms WHERE display=?";
	private static final String SELECT_BY_ID = "SELECT * FROM terms WHERE term_id=?";
	
	private static final String GET_ENTRIES_FOR_TERM = "SELECT DISTINCT ON (index.graffiti_id) * FROM index JOIN inscriptions ON "
			+ "index.graffiti_id=inscriptions.graffiti_id WHERE term_id=?";
	
	private static final String SELECT_BY_OCCURRENCE = "SELECT t.term_id, term, category, display, part_of_speech, language "
			  										 + "FROM terms t INNER JOIN (SELECT DISTINCT graffiti_id, term_id FROM index "
			  										 + "GROUP BY graffiti_id, term_id) i ON i.term_id=t.term_id "
			  										 + "GROUP BY t.term_id, term, category, display, "
			  										 + "part_of_speech, language, sort_key ORDER BY COUNT(*) DESC, sort_key ASC";
	
	private static final String SELECT_BY_OCCURRENCE_AND_CATEGORY = "SELECT t.term_id, term, category, display, part_of_speech, language "
																  + "FROM terms t INNER JOIN (SELECT DISTINCT graffiti_id, term_id FROM index "
																  + "GROUP BY graffiti_id, term_id) i ON i.term_id=t.term_id "
																  + "WHERE category=? GROUP BY t.term_id, term, category, display, "
																  + "part_of_speech, language, sort_key ORDER BY COUNT(*) DESC, sort_key ASC";
	
	
	private static final String SELECT_BY_CAT_LANG_POS  = "SELECT terms.term_id, term, category, display, part_of_speech, language FROM terms INNER JOIN ("
			+ "SELECT DISTINCT index.graffiti_id, term_id FROM index JOIN inscriptions ON inscriptions.graffiti_id=index.graffiti_id "
			+ "WHERE ancient_city SIMILAR TO ? GROUP BY index.graffiti_id, term_id) i ON i.term_id=terms.term_id "
			+ "WHERE category=? AND language SIMILAR TO ? AND part_of_speech SIMILAR TO ? "
			+ "GROUP BY terms.term_id, term, category, display, part_of_speech, language, sort_key ORDER BY sort_key";
	
	private static final String SELECT_BY_OCCURR_CATEGORY_LANG_POS = "SELECT t.term_id, term, category, display, part_of_speech, language "
			  + "FROM terms t INNER JOIN (SELECT DISTINCT index.graffiti_id, term_id FROM index JOIN inscriptions ON inscriptions.graffiti_id="
			  + "index.graffiti_id WHERE ancient_city SIMILAR TO ? "
			  + "GROUP BY index.graffiti_id, term_id) i ON i.term_id=t.term_id "
			  + "WHERE category=? AND language SIMILAR TO ? AND part_of_speech SIMILAR TO ? GROUP BY t.term_id, term, category, display, "
			  + "part_of_speech, language, sort_key ORDER BY COUNT(*) DESC, sort_key ASC";
	
	private static final String SELECT_BY_LANG_NAME_PERSON_GENDER = "SELECT terms.term_id, term, category, display, part_of_speech, language FROM terms INNER JOIN names ON terms.term=names.name "
			+ "INNER JOIN (SELECT DISTINCT index.graffiti_id, term_id FROM index JOIN inscriptions ON inscriptions.graffiti_id="
			+ "index.graffiti_id WHERE ancient_city SIMILAR TO ? GROUP BY index.graffiti_id, term_id) i ON i.term_id=terms.term_id "
			+ "WHERE category=? AND language SIMILAR TO ? AND name_type SIMILAR TO ? AND person_type "
			+ "SIMILAR TO ? AND gender SIMILAR TO ? GROUP BY terms.term_id, term, category, display, "
			+ "part_of_speech, language, sort_key, name_type, person_type, gender ORDER BY sort_key";
	
	private static final String SELECT_PEOPLE_BY_OCCURRENCE = "SELECT t.term_id, term, category, display, part_of_speech, language "
			+ "FROM terms t INNER JOIN names ON t.term=names.name "
			+ "INNER JOIN (SELECT DISTINCT index.graffiti_id, term_id FROM index JOIN inscriptions ON index.graffiti_id="
			+ "inscriptions.graffiti_id WHERE ancient_city SIMILAR TO ? GROUP BY index.graffiti_id, term_id) i ON i.term_id=t.term_id "
			+ "WHERE category=? AND language SIMILAR TO ? AND name_type SIMILAR TO ? AND person_type "
			+ "SIMILAR TO ? AND gender SIMILAR TO ? GROUP BY t.term_id, term, category, display, " 
			+ "part_of_speech, language, sort_key, name_type, person_type, gender ORDER BY COUNT(*) DESC, sort_key ASC";

	private List<IndexTerm> IndexTerms = null;
	
	// We can take advantage of the % operator to prevent the need to duplicate terms
	
	//private List<String> termInstances = null;

	//@Cacheable("indexTerms")
	public List<IndexTerm> getIndexTerms(){
		IndexTerms = jdbcTemplate.query(SELECT_STATEMENT, new IndexTermRowMapper());
		addEntries(IndexTerms);
		return IndexTerms;
	}
	
	//@Cacheable("indexTerms")
	public List<IndexTerm> getIndexTermsByOccurrence(){
		IndexTerms = jdbcTemplate.query(SELECT_BY_OCCURRENCE, new IndexTermRowMapper());
		addEntries(IndexTerms);
		return IndexTerms;
	}
	
	//@Cacheable("indexTerms")
	public List<IndexTerm> getIndexTermsByCategory(String category){
		IndexTerms = jdbcTemplate.query(SELECT_BY_CATEGORY, new IndexTermRowMapper(), category);
		addEntries(IndexTerms);
		return IndexTerms;
	}
	
	//@Cacheable("indexTerms")
	public List<IndexTerm> getIndexTermsByOccurrenceAndCategory(String category){
		IndexTerms = jdbcTemplate.query(SELECT_BY_OCCURRENCE_AND_CATEGORY, new IndexTermRowMapper(), category);
		addEntries(IndexTerms);
		return IndexTerms;
	}
	
	//@Cacheable("indexTerms")
	public List<IndexTerm> getIndexTermsByCategoryLanguageAndPOS(String category, String lang, String pos, String city){
		IndexTerms = jdbcTemplate.query(SELECT_BY_CAT_LANG_POS, new IndexTermRowMapper(), city, category, lang, pos);
		addEntries(IndexTerms);
		return IndexTerms;
	}
	
	//@Cacheable("indexTerms")
	public List<IndexTerm> getIndexTermsByOccurrenceCategoryLanguageAndPOS(String category, String lang, String pos, String city){
		IndexTerms = jdbcTemplate.query(SELECT_BY_OCCURR_CATEGORY_LANG_POS, new IndexTermRowMapper(), city, category, lang, pos);
		addEntries(IndexTerms);
		return IndexTerms;
	}
	
	//@Cacheable("indexTerms")
	public List<IndexTerm> getIndexTermsByLangNamePersonGender(String category, String lang, String name, String pers, String gen, String city){
		IndexTerms = jdbcTemplate.query(SELECT_BY_LANG_NAME_PERSON_GENDER, new IndexTermRowMapper(), city, category, lang, name, pers, gen);
		addEntries(IndexTerms);
		return IndexTerms;
	}
	
	//@Cacheable("indexTerms")
	public List<IndexTerm> getIndexPeopleByOccurence(String category, String lang, String name, String pers, String gen, String city){
		IndexTerms = jdbcTemplate.query(SELECT_PEOPLE_BY_OCCURRENCE, new IndexTermRowMapper(), city, category, lang, name, pers, gen);
		addEntries(IndexTerms);
		return IndexTerms;
	}
	
	//@Cacheable("indexTerms")
	public List<IndexTerm> getIndexTermsByDisplay(boolean display){
		IndexTerms = jdbcTemplate.query(SELECT_BY_DISPLAY, new IndexTermRowMapper(), display);
		addEntries(IndexTerms);
		return IndexTerms;
	}
	
	public List<IndexTerm> getIndexTermsByID(int id){
		IndexTerms = jdbcTemplate.query(SELECT_BY_ID, new IndexTermRowMapper(), id);
		addEntries(IndexTerms);
		return IndexTerms;
	}
	
	private void addEntries(List<IndexTerm> terms){	
		for (IndexTerm term : terms) {
			List<IndexEntry> entries = jdbcTemplate.query(GET_ENTRIES_FOR_TERM, new RowMapper<IndexEntry>(){  
			    public IndexEntry mapRow(ResultSet rs, int rownumber) throws SQLException {  
					final IndexEntry entry = new IndexEntry();
					entry.setTermID(rs.getInt("term_id"));
					entry.setGraffitiID(rs.getString("graffiti_id"));
					entry.setHit(rs.getString("hit"));
					entry.setContent(rs.getString("content"));
					entry.setCity(rs.getString("ancient_city"));
					return entry; 
			    }  
			}, term.getTermID());
			term.setEntries(entries);
		}
	}  
}