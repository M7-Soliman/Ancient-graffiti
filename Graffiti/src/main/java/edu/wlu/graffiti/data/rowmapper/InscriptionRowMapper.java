package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.FiguralInfo;
import edu.wlu.graffiti.bean.City;
import edu.wlu.graffiti.bean.Column;
import edu.wlu.graffiti.bean.FeaturedGraffitiInfo;
import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.Photo;
import edu.wlu.graffiti.bean.Property;

import edu.wlu.graffiti.bean.Segment;
import edu.wlu.graffiti.bean.Street;

/**
 * @editor Trevor Stalnaker
 */
public final class InscriptionRowMapper implements RowMapper<Inscription> {

	public Inscription mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
		final Inscription inscription = new Inscription();
		final FiguralInfo figinfo = new FiguralInfo();
		final FeaturedGraffitiInfo featuredGraffitoInfo = new FeaturedGraffitiInfo();

		inscription.setId(resultSet.getInt("local_id"));
		inscription.setGraffitiId(resultSet.getString("graffiti_id"));
		inscription.setAncientCity(resultSet.getString("ANCIENT_CITY"));
		inscription.setDateBeginning(resultSet.getString("date_beginning"));
		inscription.setDateEnd(resultSet.getString("date_end"));
		inscription.setDateExplanation(resultSet.getString("date_explanation"));

		inscription.setSourceFindSpot(resultSet.getString("find_spot"));
		inscription.setMeasurements(resultSet.getString("MEASUREMENTS"));
		inscription.setLanguage(resultSet.getString("language"));
		inscription.setContent(resultSet.getString("CONTENT"));
		inscription.setBibliography(resultSet.getString("BIBLIOGRAPHY"));
		inscription.setWritingStyle(resultSet.getString("writing_style"));
		inscription.setApparatus(resultSet.getString("APPARATUS"));
		inscription.setApparatusDisplay(resultSet.getString("apparatus_displayed"));

		inscription.setPrincipleContributors(resultSet.getString("principle_contributors"));
		inscription.setLastRevision(resultSet.getString("last_revision"));
		inscription.setEditor(resultSet.getString("editor"));

		inscription.setCaption(resultSet.getString("caption"));
		inscription.setCommentary(resultSet.getString("commentary"));
		inscription.setContentTranslation(resultSet.getString("content_translation"));
		inscription.setCil(resultSet.getString("cil"));
		inscription.setLangner(resultSet.getString("langner"));
		inscription.setWritingStyleInEnglish(resultSet.getString("writing_style_in_english"));
		inscription.setLanguageInEnglish(resultSet.getString("lang_in_english"));
		inscription.setEpidoc(resultSet.getString("content_epidocified"));
		inscription.setContributors(resultSet.getString("contributors"));
		inscription.setUpdateOfCil(resultSet.getBoolean("update_of_cil"));
		inscription.setSupportDesc(resultSet.getString("support_desc"));
		inscription.setLayoutDesc(resultSet.getString("layout_desc"));
		inscription.setHandnoteDesc(resultSet.getString("handnote_desc"));

		Property p = new Property(resultSet.getInt("property_id"));
		inscription.setProperty(p);

		inscription.setHasFiguralComponent(resultSet.getBoolean("has_figural_component"));
		inscription.setIsPoetic(resultSet.getBoolean("is_poetic"));

		inscription.setGraffitoHeight(resultSet.getString("graffito_height"));
		inscription.setGraffitoLength(resultSet.getString("graffito_length"));
		inscription.setHeightFromGround(resultSet.getString("height_from_ground"));
		inscription.setFeaturedFigural(resultSet.getBoolean("is_featured_figural"));
		inscription.setFeaturedTranslation(resultSet.getBoolean("is_featured_translation"));
		inscription.setIndividualLetterHeights(resultSet.getString("individual_letter_heights"));
		inscription.setMaxLetterHeight(resultSet.getString("letter_height_max"));
		inscription.setMinLetterHeight(resultSet.getString("letter_height_min"));
		inscription.setMaxLetterWithFlourishesHeight(resultSet.getString("letter_with_flourishes_height_max"));
		inscription.setMinLetterWithFlourishesHeight(resultSet.getString("letter_with_flourishes_height_min"));
		inscription.setEpidoc(resultSet.getString("content_epidocified"));
		inscription.setThemed(resultSet.getBoolean("is_themed"));
		inscription.setPreciseLocation(resultSet.getString("precise_location"));

		boolean onFacade = resultSet.getBoolean("on_facade");
		inscription.setOnFacade(onFacade);
		if (onFacade) {

			int streetID = resultSet.getInt("street_id");

			// only if the street, etc., exists:
			if (streetID != 0) {
				Street street = new Street();
				street.setId(streetID);
				street.setStreetName(resultSet.getString("street_name"));
				City city = new City();
				city.setName(resultSet.getString("city_name"));
				city.setPleiadesId(resultSet.getString("city_pleiades_id"));
				street.setCity(city);
				Segment s = new Segment(resultSet.getInt("segment_id"), resultSet.getString("segment_name"), street);
				s.setDisplayName(resultSet.getString("display_name"));
				s.setHidden(resultSet.getBoolean("hidden"));
				inscription.setSegment(s);
			}
		}

		boolean onColumn = resultSet.getBoolean("on_column");
		inscription.setOnColumn(onColumn);
		if (onColumn) {
			Column col = new Column();
			col.setId(resultSet.getInt("column_id"));
			col.setDecimal(resultSet.getInt("decimal_number"));
			col.setRomanNumeral(resultSet.getString("roman_numeral"));
			inscription.setColumn(col);
		}

		if (inscription.hasFiguralComponent()) {
			figinfo.setDescriptionInEnglish(resultSet.getString("description_in_english"));
			figinfo.setDescriptionInLatin(resultSet.getString("description_in_latin"));
		}

		if (inscription.isThemed()) { // inscription.isFeaturedGraffitiFigural() ||
										// inscription.isFeaturedGraffitiTranslation() ) { //isThemed is probably more
										// appropriate
			featuredGraffitoInfo.setCommentary(resultSet.getString("gh_commentary"));
			Photo ph = new Photo();
			ph.setGraffitiId(resultSet.getString("graffiti_id"));
			ph.setPhotoId(resultSet.getString("preferred_image"));
			ph.setPaths();
			featuredGraffitoInfo.setPreferredImage(ph);
		}

		inscription.setFiguralInfo(figinfo);
		inscription.setFeaturedGraffitInfo(featuredGraffitoInfo);

		return inscription;
	}
}