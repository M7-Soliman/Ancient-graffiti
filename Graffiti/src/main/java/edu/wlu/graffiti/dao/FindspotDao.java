/**
 * 
 */
package edu.wlu.graffiti.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.bean.PropertyLink;
import edu.wlu.graffiti.bean.PropertyType;
import edu.wlu.graffiti.bean.Segment;
import edu.wlu.graffiti.bean.Street;
import edu.wlu.graffiti.data.rowmapper.PropertyLinkRowMapper;
import edu.wlu.graffiti.data.rowmapper.PropertyRowMapper;
import edu.wlu.graffiti.data.rowmapper.PropertyTypeRowMapper;
import edu.wlu.graffiti.data.rowmapper.SegmentRowMapper;
import edu.wlu.graffiti.data.rowmapper.StreetRowMapper;
import edu.wlu.graffiti.bean.City;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Class to extract property information
 * 
 * @author Sara Sprenkle
 * @editor Trevor Stalnaker
 * 
 */

@Repository
public class FindspotDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * Order by the properties.id --> if order by properties.property_number,
	 * ordered as strings because property_number is a var char.
	 */
	private static final String ORDER_BY_CLAUSE = "ORDER BY properties.id ASC";

	private static final String SELECT_STATEMENT = "SELECT *, " + "cities.name as city_name, "
			+ "cities.pleiades_id as city_pleiades_id, " + "insula.pleiades_id as insula_pleiades_id, "
			+ "properties.pleiades_id as property_pleiades_id " + " FROM properties "
			+ "LEFT JOIN insula ON properties.insula_id=insula.id "
			+ "LEFT JOIN cities ON insula.modern_city=cities.name " + ORDER_BY_CLAUSE;

	private static final String SELECT_BY_CITY_STATEMENT = "SELECT *, " + "cities.name as city_name, "
			+ "cities.pleiades_id as city_pleiades_id, " + "insula.pleiades_id as insula_pleiades_id, "
			+ "properties.pleiades_id as property_pleiades_id " + " FROM properties "
			+ "LEFT JOIN insula ON properties.insula_id=insula.id "
			+ "LEFT JOIN cities ON insula.modern_city=cities.name " + "WHERE UPPER(modern_city) = UPPER(?) "
			+ ORDER_BY_CLAUSE;

	public static final String SELECT_BY_CITY_AND_INSULA_STATEMENT = "SELECT *, " + "cities.name as city_name, "
			+ "cities.pleiades_id as city_pleiades_id, " + "insula.pleiades_id as insula_pleiades_id, "
			+ "properties.pleiades_id as property_pleiades_id " + " FROM properties "
			+ "LEFT JOIN insula ON properties.insula_id=insula.id "
			+ "LEFT JOIN cities ON insula.modern_city=cities.name "
			+ "WHERE UPPER(modern_city) = UPPER(?) and insula.short_name = ? " + ORDER_BY_CLAUSE;

	public static final String SELECT_BY_CITY_AND_INSULA_AND_PROPERTY_STATEMENT = "SELECT *, "
			+ "cities.name as city_name, " + "cities.pleiades_id as city_pleiades_id, "
			+ "insula.pleiades_id as insula_pleiades_id, " + "properties.pleiades_id as property_pleiades_id "
			+ "FROM properties " + "LEFT JOIN insula ON properties.insula_id=insula.id "
			+ "LEFT JOIN cities ON insula.modern_city=cities.name "
			+ "WHERE UPPER(modern_city) = UPPER(?) and insula.short_name = ? and property_number = ?";

	public static final String SELECT_BY_CITY_AND_PROPERTY_NAME_STATEMENT = "SELECT *, "
			+ "cities.name as city_name, cities.pleiades_id as city_pleiades_id, "
			+ "insula.pleiades_id as insula_pleiades_id, " + "properties.pleiades_id as property_pleiades_id "
			+ "FROM properties " + "LEFT JOIN insula ON properties.insula_id=insula.id "
			+ "LEFT JOIN cities ON insula.modern_city=cities.name "
			+ "WHERE UPPER(modern_city) = UPPER(?) and property_name = ?";

	public static final String SELECT_BY_PROPERTY_ID_STATEMENT = "SELECT *, " + "cities.name as city_name, "
			+ "cities.pleiades_id as city_pleiades_id, " + "insula.pleiades_id as insula_pleiades_id, "
			+ "properties.pleiades_id as property_pleiades_id " + " FROM properties "
			+ "LEFT JOIN insula ON properties.insula_id=insula.id "
			+ "LEFT JOIN cities ON insula.modern_city=cities.name WHERE properties.id = ?";
	
	public static final String SELECT_PROPERTIES_WITH_GRAFFITI = "SELECT *, " + "cities.name as city_name, "
			+ "cities.pleiades_id as city_pleiades_id, " + "insula.pleiades_id as insula_pleiades_id, "
			+ "p1.pleiades_id as property_pleiades_id " + " FROM properties p1 "
			+ "LEFT JOIN insula ON p1.insula_id=insula.id "
			+ "LEFT JOIN cities ON insula.modern_city=cities.name " 
			+ "where (SELECT count(*) from inscriptions, properties p2 where property_id = p2.id and inscriptions.property_id=p1.id) > 0 "
			+ "ORDER BY p1.id ASC";
	
	private static final String SELECT_PROPERTY_LINKS = "SELECT * FROM property_links";
	
	private static final String SELECT_PROPERTY_LINKS_BY_ID = "SELECT * from property_links WHERE property_id = ?";

	public static final String SELECT_PROPERTY_TYPES = "SELECT * " + " FROM propertytypes";
	
	public static final String SELECT_PROPERTY_TYPES_BY_PARENT_ID = "SELECT * FROM propertytypes WHERE parent_id=?";

	private static final String SELECT_PROP_TYPES_BY_ID_STMT = "SELECT *" + " FROM propertytypes WHERE id = ?";

	public static final String SELECT_PROP_TYPES_BY_PROP_ID = "SELECT propertytypes.id, propertytypes.name, propertytypes.commentary," 
			+" propertytypes.parent_id, propertytypes.is_parent from propertytypes, propertytopropertytype "
			+ "WHERE propertytopropertytype.property_id = ? AND propertytopropertytype.property_type = propertytypes.id";

	public static final String SELECT_BY_OSM_WAY_ID_STATEMENT = "SELECT * " + " FROM properties WHERE osm_way_id = ?";
	public static final String SELECT_BY_OSM_ID_STATEMENT = "SELECT * " + " FROM properties WHERE osm_id = ?";

	public static final String SELECT_CITY_NAMES = "SELECT name from cities ORDER BY name";
	
	public static final String SELECT_SEGMENTS_BY_STREET_ID = "SELECT * FROM segments LEFT JOIN streets ON (segments.street_id=streets.id) WHERE street_id = ?";
	
	public static final String SELECT_SEGMENTS_BY_STREET_NAME = "SELECT * FROM segments "
			+ "LEFT JOIN streets ON (segments.street_id=streets.id) "
			+ "LEFT JOIN cities ON streets.city=cities.name "
			+ "WHERE street_name = ? AND segments.hidden=false ORDER BY segments.segment_name ASC";
	
	public static final String SELECT_PROPERTIES_WITH_FACADES = SELECT_STATEMENT + " LEFT JOIN agp_inscription_info ON (properties.id = agp_inscription_info.property_id) "
			+ "WHERE on_facade=true";
	
	public static final String SELECT_SEGMENTS_WITH_FACADES = "SELECT DISTINCT segments.id, segment_name, street_id, street_name,"
			+ "cities.pleiades_id, streets.city, segments.display_name, segments.hidden "
			+ "FROM segments LEFT JOIN inscriptions ON (segments.id = inscriptions.segment_id) "
			+ "LEFT JOIN streets ON (segments.street_id=streets.id) "
			+ "LEFT JOIN cities ON cities.name=streets.city WHERE inscriptions.on_facade=true AND "
			+ "segments.hidden=false ORDER BY streets.city ASC, street_name ASC";
	
	public static final String SELECT_SEG_FOR_PROPERTY = "SELECT * FROM property_on_segments LEFT JOIN segments ON "
			+ "(property_on_segments.seg_id=segments.id) WHERE property_id=?";
	
	public static final String SELECT_STREETS = "SELECT * FROM streets JOIN cities ON streets.city=cities.name ORDER BY city, street_name ASC";
	
	public static final String SELECT_STREETS_WITH_FACADES = "SELECT DISTINCT streets.id, streets.street_name, streets.city, "
			+ " cities.pleiades_id FROM streets "
			+ "LEFT JOIN segments ON segments.street_id=streets.id "
			+ "LEFT JOIN inscriptions ON inscriptions.segment_id=segments.id "
			+ "LEFT JOIN cities ON streets.city=cities.name "
			+ "WHERE inscriptions.on_facade=true ORDER BY street_name ASC";	
	
	public static final String SELECT_SEGMENT_BY_NAME_STREET_AND_CITY = "SELECT * FROM segments "
			+ "LEFT JOIN streets ON streets.id=segments.street_id "
			+ "LEFT JOIN cities ON cities.name=streets.city "
			+ "WHERE UPPER(segment_name)=UPPER(?) AND UPPER(street_name)=UPPER(?) "
			+ "AND UPPER(streets.city)=UPPER(?)";
	
	public static final String SELECT_STREET_BY_NAME_AND_CITY = "SELECT * FROM streets "
			+ "LEFT JOIN cities ON cities.name=streets.city "
			+ "WHERE UPPER(street_name)=UPPER(?) "
			+ "AND UPPER(streets.city)=UPPER(?)";
	
	public static final String SELECT_CITIES_WITH_STREETS = "SELECT DISTINCT ON (cities.name) * FROM cities LEFT JOIN streets "
			+ "ON streets.city=cities.name WHERE NOT streets.street_name=''";

	public static final String SELECT_COUNT = "SELECT COUNT(*) " + "FROM inscriptions "
			+ "LEFT JOIN figural_graffiti_info ON inscriptions.graffiti_id=figural_graffiti_info.graffiti_id "
			+ "LEFT JOIN featured_graffiti_info ON inscriptions.graffiti_id=featured_graffiti_info.graffiti_id "
			+ "LEFT JOIN properties ON inscriptions.property_id=properties.id "
			+ "LEFT JOIN insula ON properties.insula_id=insula.id";
	
	private static final String FIND_COUNT_BY_FIND_SPOT = SELECT_COUNT + " WHERE properties.id = ?";

//	private static final class PropertyTypeRowMapper implements RowMapper<PropertyType> {
//		public PropertyType mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
//			final PropertyType propType = new PropertyType();
//			propType.setId(resultSet.getInt("id"));
//			propType.setName(resultSet.getString("name"));
//			propType.setDescription(resultSet.getString("commentary"));
//			propType.setParentId(resultSet.getInt("parent_id"));
//			propType.setIsParent(resultSet.getBoolean("is_parent"));
//			return propType;
//		}
//	}

	@Cacheable("cities")
	public List<String> getCityNames() {
		List<String> results = jdbcTemplate.queryForList(SELECT_CITY_NAMES, String.class);
		return results;
	}

	// TODO: set up caching for this
	// @Cacheable("citiesUpperCase")
	public List<String> getCityNamesUpperCase() {
		List<String> results = jdbcTemplate.queryForList(SELECT_CITY_NAMES, String.class);
		// change names to upper case to allow for either lower or upper case in
		// URIs
		ListIterator<String> iterator = results.listIterator();
		while (iterator.hasNext()) {
			iterator.set(iterator.next().toUpperCase());
		}
		return results;
	}

	@Cacheable("propertyTypes")
	public List<PropertyType> getPropertyTypes() {
		List<PropertyType> results = jdbcTemplate.query(SELECT_PROPERTY_TYPES, new PropertyTypeRowMapper());
		for (PropertyType pt : results) {
			pt.setChildren(getChildrenFromPropertyType(pt.getId()));
		}
		Collections.sort(results);
		return results;
	}
	
	public List<PropertyType> getChildrenFromPropertyType(int propertyTypeId){
		List<PropertyType> results = jdbcTemplate.query(SELECT_PROPERTY_TYPES_BY_PARENT_ID, new PropertyTypeRowMapper(), propertyTypeId);
		return results;
	}

	// TODO: set up caching for this
	public List<PropertyLink> getPropertyLinks() {
		List<PropertyLink> results = jdbcTemplate.query(SELECT_PROPERTY_LINKS, new PropertyLinkRowMapper());
		return results;
	}
	
	@Cacheable("propertyNames")
	public String getPropertyName(int propertyTypeID) {
		PropertyType results = jdbcTemplate.queryForObject(SELECT_PROP_TYPES_BY_ID_STMT, new PropertyTypeRowMapper(),
				propertyTypeID);
		return results.getName();
	}

	// TODO: set up caching for this
	public List<PropertyLink> getPropertyLinksForProperty(int propertyID) {
		List<PropertyLink> propertyLinks = jdbcTemplate.query(SELECT_PROPERTY_LINKS_BY_ID, new PropertyLinkRowMapper(), propertyID);
		return propertyLinks;
	}
	
	@Cacheable("propertyTypesByPropertyId")
	public List<PropertyType> getPropertyTypeForProperty(int propertyID) {
		List<PropertyType> propertyTypes = jdbcTemplate.query(SELECT_PROP_TYPES_BY_PROP_ID, new PropertyTypeRowMapper(), propertyID);
		return propertyTypes;
	}

	// TODO: Set up to cache this
	public List<Property> getProperties() {
		List<Property> results = jdbcTemplate.query(SELECT_STATEMENT, new PropertyRowMapper());
		return results;
	}
	
	@Cacheable("properties_with_graffiti")
	public List<Property> getPropertiesWithGraffiti() {
		List<Property> results = jdbcTemplate.query(SELECT_PROPERTIES_WITH_GRAFFITI, new PropertyRowMapper());
		return results;
	}

	// TODO: Set up to cache this
	public List<Property> getPropertiesByCity(String city) {
		List<Property> results = jdbcTemplate.query(SELECT_BY_CITY_STATEMENT, new PropertyRowMapper(), city);
		for (Property prop : results) {
			prop.setPropertyTypes(getPropertyTypeForProperty(prop.getId()));
			prop.setPropertyLinks(getPropertyLinksForProperty(prop.getId()));
		}
		return results;
	}

	// TODO: Set up to cache this
	public List<Property> getPropertiesByCityAndInsula(String city, String insulaName) {
		List<Property> results = jdbcTemplate.query(SELECT_BY_CITY_AND_INSULA_STATEMENT, new PropertyRowMapper(), city, insulaName);
		for (Property prop : results) {
			prop.setPropertyTypes(getPropertyTypeForProperty(prop.getId()));
			prop.setPropertyLinks(getPropertyLinksForProperty(prop.getId()));
		}
		return results;
	}

	// TODO: Set up to cache this
	@JsonInclude(Include.NON_NULL)
	public Property getPropertyByCityAndInsulaAndProperty(String city, String insulaName, String property_number) {
		Property result = jdbcTemplate.queryForObject(SELECT_BY_CITY_AND_INSULA_AND_PROPERTY_STATEMENT, new PropertyRowMapper(),
				city, insulaName, property_number);
		result.setPropertyTypes(getPropertyTypeForProperty(result.getId()));
		result.setPropertyLinks(getPropertyLinksForProperty(result.getId()));
		result.setNumberOfGraffiti(getInscriptionCountByFindSpot(result.getId()));
		return result;
	}

	public Property getPropertyByCityAndProperty(String city, String propertyName) {
		Property result = jdbcTemplate.queryForObject(SELECT_BY_CITY_AND_PROPERTY_NAME_STATEMENT, new PropertyRowMapper(), city,
				propertyName);
		if (result == null) {
			return result;
		}
		result.setPropertyTypes(getPropertyTypeForProperty(result.getId()));
		result.setPropertyLinks(getPropertyLinksForProperty(result.getId()));
		result.setNumberOfGraffiti(getInscriptionCountByFindSpot(result.getId()));
		return result;
	}

	@Cacheable("propertyById")
	public Property getPropertyById(int property_id) {
		Property result = jdbcTemplate.queryForObject(SELECT_BY_PROPERTY_ID_STATEMENT, new PropertyRowMapper(), property_id);
		result.setPropertyTypes(getPropertyTypeForProperty(property_id));
		result.setNumberOfGraffiti(getInscriptionCountByFindSpot(result.getId()));
		result.setPropertyLinks(getPropertyLinksForProperty(result.getId()));
		return result;
	}
	
	public Segment getSegmentByNameStreetAndCity(String name, String str, String city) {
		Segment result = jdbcTemplate.queryForObject(SELECT_SEGMENT_BY_NAME_STREET_AND_CITY, new SegmentRowMapper(), name, str, city);
		return result;
	}
	
	public Street getStreetByNameAndCity(String str, String city) {
		Street result = jdbcTemplate.queryForObject(SELECT_STREET_BY_NAME_AND_CITY, new StreetRowMapper(), str, city);
		return result;
	}
	
	public List<Segment> getSegmentsByStreet(int street_id){
		List<Segment> results = jdbcTemplate.query(SELECT_SEGMENTS_BY_STREET_ID, new SegmentRowMapper(), street_id);
		return results;	
	}
	
	public List<Segment> getSegmentsByStreetName(String street){
		List<Segment> results = jdbcTemplate.query(SELECT_SEGMENTS_BY_STREET_NAME, new SegmentRowMapper(), street);
		return results;	
	}
	
	//Returns all properties that have graffiti on facades
	public List<Property> getPropertiesWithFacades(){
		List<Property> results = jdbcTemplate.query(SELECT_PROPERTIES_WITH_FACADES, new PropertyRowMapper());
		return results;
	}
	
	/**
	 * Returns all segments that have graffiti on facades
	 * @return all segments that have graffiti on facades
	 */
	@Cacheable("segments_with_facades")
	public List<Segment> getSegmentsWithFacades(){
		List<Segment> results = jdbcTemplate.query(SELECT_SEGMENTS_WITH_FACADES, new SegmentRowMapper());
		return results;
	}
	
	@Cacheable("streets_with_facades")
	public List<Street> getStreetsWithFacades(){
		List<Street> results = jdbcTemplate.query(SELECT_STREETS_WITH_FACADES, new StreetRowMapper());
		return results;
	}
	
	//Returns all streets in the database
	public List<Street> getStreets(){
		List<Street> results = jdbcTemplate.query(SELECT_STREETS, new StreetRowMapper());
		return results;
	}
	
	@Cacheable("cities_with_streets")
	public List<City> getCitiesWithStreets(){
		List<City> cities = jdbcTemplate.query(SELECT_CITIES_WITH_STREETS, new RowMapper<City>(){  
		    public City mapRow(ResultSet rs, int rownumber) throws SQLException {  
				final City city = new City();
				city.setName(rs.getString("name"));
				city.setPleiadesId(rs.getString("pleiades_id"));
				return city; 
		    }  
		});
		return cities;
	}
	
	public int getInscriptionCountByFindSpot(final int property_id) {
		return jdbcTemplate.queryForObject(FIND_COUNT_BY_FIND_SPOT, Integer.class, property_id);
	}
}
