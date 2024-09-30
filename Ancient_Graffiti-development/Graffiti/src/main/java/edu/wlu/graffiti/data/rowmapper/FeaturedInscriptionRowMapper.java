package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.FeaturedInscription;
import edu.wlu.graffiti.bean.Photo;

public final class FeaturedInscriptionRowMapper implements RowMapper<FeaturedInscription> {

	public FeaturedInscription mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
		
		final FeaturedInscription inscription = new FeaturedInscription();	
		
		inscription.setGraffitiId(resultSet.getString("graffiti_id"));
		inscription.setContent(resultSet.getString("CONTENT"));
		inscription.setContentTranslation(resultSet.getString("translation"));
		inscription.setCil(resultSet.getString("cil"));
		inscription.setCommentary(resultSet.getString("commentary"));
	
		// Only set a preferred image if one exists
		if (resultSet.getString("image") != null) {
			final Photo p = new Photo();
			p.setGraffitiId(resultSet.getString("graffiti_id"));
			p.setPhotoId(resultSet.getString("image"));
			p.setPaths();
			inscription.setPreferredImage(p);
		}
		
		return inscription;
	}
}
