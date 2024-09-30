package edu.wlu.graffiti.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import edu.wlu.graffiti.bean.Photo;
import edu.wlu.graffiti.data.rowmapper.PhotoRowMapper;

/**
 * Class to extract photos from the DB
 * 
 * @author Hammad Ahmad
 * 
 */

@Repository
public class PhotosDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final String SELECT_STATEMENT = "SELECT * " + "FROM photos ORDER BY graffiti_id";
	
	private static final String SELECT_BY_graffiti_id = "SELECT * " + "FROM photos WHERE graffiti_id = ?";

	private List<Photo> photos = null;

	public List<Photo> getPhotos() {
		photos = jdbcTemplate.query(SELECT_STATEMENT, new PhotoRowMapper());
		return photos;
	}
	
	public List<Photo> getPhotosByID(String id) {
		photos = jdbcTemplate.query(SELECT_BY_graffiti_id, new PhotoRowMapper(), id);
		return photos;
	}

}
