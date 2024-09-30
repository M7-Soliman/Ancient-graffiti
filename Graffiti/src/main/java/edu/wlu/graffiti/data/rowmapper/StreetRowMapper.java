package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.City;
import edu.wlu.graffiti.bean.Street;

/**
 * @author Trevor Stalnaker
 */
public final class StreetRowMapper implements RowMapper<Street> {
	public Street mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
		final Street street = new Street();
		final City city = new City();
		street.setId(resultSet.getInt("id"));
		street.setStreetName(resultSet.getString("street_name"));
		city.setName(resultSet.getString("city"));
		city.setPleiadesId(resultSet.getString("pleiades_id"));
		street.setCity(city);
		return street;
	}
	
}
