package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.City;
import edu.wlu.graffiti.bean.Segment;
import edu.wlu.graffiti.bean.Street;

/**
 * @author Trevor Stalnaker
 */
public final class SegmentRowMapper implements RowMapper<Segment> {	
	public Segment mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
		final Segment segment = new Segment();
		final Street street = new Street();
		final City city = new City();
		segment.setId(resultSet.getInt("id"));
		segment.setSegmentName(resultSet.getString("segment_name"));
		segment.setDisplayName(resultSet.getString("display_name"));
		segment.setHidden(resultSet.getBoolean("hidden"));
		street.setId(resultSet.getInt("street_id"));
		street.setStreetName(resultSet.getString("street_name"));
		city.setName(resultSet.getString("city"));
		city.setPleiadesId(resultSet.getString("pleiades_id"));
		street.setCity(city);
		segment.setStreet(street);
		return segment;
	}
}
