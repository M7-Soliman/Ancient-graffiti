package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.FeaturedGraffitiInfo;
import edu.wlu.graffiti.bean.Photo;

public final class FeaturedGraffitiInfoRowMapper implements RowMapper<FeaturedGraffitiInfo> {
	public FeaturedGraffitiInfo mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
		final FeaturedGraffitiInfo fgi = new FeaturedGraffitiInfo();
		final Photo p = new Photo();
		String id = resultSet.getString("graffiti_id");
		String img = resultSet.getString("preferred_image");
		fgi.setCommentary(resultSet.getString("commentary"));
		p.setGraffitiId(id);
		p.setPhotoId(img);
		p.setPaths();
		fgi.setPreferredImage(p);
		return fgi;
	}
}