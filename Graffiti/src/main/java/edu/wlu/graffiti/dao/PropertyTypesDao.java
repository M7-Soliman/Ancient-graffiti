package edu.wlu.graffiti.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import edu.wlu.graffiti.bean.PropertyType;
import edu.wlu.graffiti.data.rowmapper.PropertyTypeRowMapper;


/**
 * Class to extract property types from the DB
 *
 * @author Sara Sprenkle
 *
 */

@Repository
public class PropertyTypesDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static String GET_FACADE_PROPERTY_ID = "SELECT * FROM propertytypes WHERE name='Facades'";

	private static int facades_id = -1;

	public int getFacadesID() {
		if (facades_id == -1) {
			List<PropertyType> facade_type = jdbcTemplate.query(GET_FACADE_PROPERTY_ID, new PropertyTypeRowMapper());
			facades_id = facade_type.get(0).getId();
		}
		return facades_id;

	}


}
