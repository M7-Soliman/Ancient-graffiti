package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.Contribution;

/**
 * 
 * @author Trevor Stalnaker
 *
 */

public final class ContributionRowMapper implements RowMapper<Contribution> {
	public Contribution mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
		
		Contribution contribution = new Contribution();
		contribution.setInscriptionId(resultSet.getString("inscription_id"));
		contribution.setUserName(resultSet.getString("user_name"));
		contribution.setComment(resultSet.getString("comment"));
		contribution.setDate(resultSet.getString("date_modified"));
		return contribution;
	}
}
