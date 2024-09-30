package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.Photo;

/**
 * 
 * @author Hammad Ahmad
 * @editor Trevor Stalnaker
 *
 */
public final class PhotoRowMapper implements RowMapper<Photo> {
	
	public Photo mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
		final Photo photo = new Photo();
		String graffitiId = resultSet.getString("graffiti_id");
		String photoId  = resultSet.getString("photo_id");
		photo.setId(rowNum);
		photo.setGraffitiId(graffitiId);
		photo.setPhotoId(photoId);
		photo.setPaths();
		return photo;
	}
}