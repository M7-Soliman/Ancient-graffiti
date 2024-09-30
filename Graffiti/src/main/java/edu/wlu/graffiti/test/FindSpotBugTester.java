//package edu.wlu.graffiti.test;
//
//import static org.elasticsearch.xcontent.XContentFactory.jsonBuilder;
//import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
//import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
//import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.elasticsearch.action.ActionFuture;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.action.search.ShardSearchFailure;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.settings.Settings;
//import org.elasticsearch.transport.InetSocketTransportAddress;
//import org.elasticsearch.xcontent.XContentBuilder;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.aggregations.Aggregation;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
//import org.elasticsearch.search.sort.SortOrder;
//import org.elasticsearch.transport.client.PreBuiltTransportClient;
//
//import edu.wlu.graffiti.data.setup.Utils;
//
///**
// * Tests that can be run to hunt down the find spot search bug
// * 
// * @author Trevor Stalnaker
// */
//public class FindSpotBugTester
//{
//	private static TransportClient client;
//	
//	private static String[] fields = {"insula.insula_id", "insula.insula_name", "property.property_id", 
//			"property.property_name", "property.property_number", "property.property_types", 
//			"drawing.description_in_english", "drawing.description_in_latin", "drawing.drawing_tag_ids",
//			"drawing.drawing_tags", "id", "city", "caption", "writing_style", "language", "content", 
//			"bibliography", "content_translation", "has_figural_component", "lagner", 
//			"writing_style_in_english", "language_in_english", "measurements", "comment", "cil", "graffiti_id",
//			"content"};
//	
//	private static String DB_DRIVER;
//	private static String DB_URL;
//	private static String DB_USER;
//	private static String DB_PASSWORD;
//	static Connection newDBCon;
//	
//	public static void main(String[] args) throws SQLException
//	{
//		Settings settings = Settings.builder().put("cluster.name", "agp-cluster").build();
//		SearchResponse response;
//		init();
//		
//		try
//		{
//			client = new PreBuiltTransportClient(settings).addTransportAddress(
//					new InetSocketTransportAddress(InetAddress.getByName("servo.cs.wlu.edu"), 9300));
//		}
//		catch(UnknownHostException e)
//		{
//			e.printStackTrace();
//		}
//		String search = "603";
//		ArrayList<ArrayList<String>> lyst = conductBoolSearch(search);
//		for (int x = 0; x < lyst.size(); x++) {
//			System.out.println(fields[x] + ": ");
//			for (String hit : lyst.get(x)) {
//				System.out.println("Hit: " + hit);
//				System.out.println("Why hit: " + whyHit(hit, fields[x], search));
//			}
//			System.out.println("Number of hits: " + (lyst.get(x)).size());
//			System.out.println("EDR Ids for search: " + getEDRIdsForSelectStatement(lyst.get(x)) + "\n");
//		}
//		
//		client.close();
//	}
//	
//	private static String getEDRIdsForSelectStatement(ArrayList<String> lyst) {
//		String temp= "";
//		for (String id : lyst) {
//			if (!temp.equals("")) {
//				temp += ", ";
//			}
//			temp += "'EDR" + id.substring(3) + "'";
//		}
//		return temp;
//	}
//	
//	private static ArrayList<String> whyHit(String graffiti_id, String field, String search) throws SQLException {
//		ArrayList<String> results = new ArrayList<String>();
//		String hit = getData(graffiti_id, field);
//		search = search.toLowerCase();
//		String regex = "";
//		if (hit != null) {
//			hit = hit.toLowerCase();
//			for (int i = 0; i < search.length(); i++) {
//				if (search.charAt(i) == '.') {
//					regex += "\\";
//				}
//				regex += search.charAt(i);
//				regex += "?";	
//			}
//			Matcher matcher = Pattern.compile(regex).matcher(hit);
//			while(matcher.find()) {
//				String temp = matcher.group(0);
//				if (temp.length() > 0) {
//					results.add(temp);
//				}
//			}
//		}
//		return results;
//	}
//	
//	private static String getData(String graffiti_id, String field) throws SQLException {
//		Statement stmt = newDBCon.createStatement();
//		if (field.equals("insula.insula_name")) {
//			ResultSet rs = stmt.executeQuery("SELECT graffiti_id, insula.short_name FROM inscriptions LEFT JOIN "
//					+ "properties ON (inscriptions.property_id = properties.id) LEFT JOIN insula ON "
//					+ "(properties.insula_id = insula.id) WHERE graffiti_id='EDR"+graffiti_id.substring(3)+"'");
//			while (rs.next()) {
//				String insula_name = rs.getString("short_name");
//				System.out.println("Content: '" + insula_name + "'");
//				return insula_name;
//			}
//		}
//		if (field.equals("property.property_number")) {
//			ResultSet rs = stmt.executeQuery("SELECT graffiti_id, properties.property_number FROM inscriptions LEFT JOIN "
//					+ "properties ON (inscriptions.property_id = properties.id) WHERE graffiti_id='EDR"+graffiti_id.substring(3)+"'");
//			while (rs.next()) {
//				String property_number = rs.getString("property_number");
//				System.out.println("Content: '" + property_number + "'");
//				return property_number;
//			}
//		}
//		if (field.equals("bibliography")) {
//			ResultSet rs = stmt.executeQuery("SELECT bibliography FROM inscriptions"
//					+ " WHERE graffiti_id='EDR"+graffiti_id.substring(3)+"'");
//			while (rs.next()) {
//				String bibliography = rs.getString("bibliography");
//				System.out.println("Content: '" + bibliography + "'");
//				return bibliography;
//			}
//		}
//		if (field.equals("measurements")) {
//			ResultSet rs = stmt.executeQuery("SELECT measurements FROM inscriptions"
//					+ " WHERE graffiti_id='EDR"+graffiti_id.substring(3)+"'");
//			while (rs.next()) {
//				String measurements = rs.getString("measurements");
//				System.out.println("Content: '" + measurements + "'");
//				return measurements;
//			}
//		}
//		if (field.equals("comment")) {
//			ResultSet rs = stmt.executeQuery("SELECT comment FROM inscriptions"
//					+ " WHERE graffiti_id='EDR"+graffiti_id.substring(3)+"'");
//			while (rs.next()) {
//				String comment = rs.getString("comment");
//				System.out.println("Content: '" + comment + "'");
//				return comment;
//			}
//		}
//		if (field.equals("insula.insula_id")) {
//			ResultSet rs = stmt.executeQuery("SELECT graffiti_id, insula.id AS insula_id FROM inscriptions LEFT JOIN "
//					+ "properties ON (inscriptions.property_id = properties.id) LEFT JOIN insula ON "
//					+ "(properties.insula_id = insula.id) WHERE graffiti_id='EDR"+graffiti_id.substring(3)+"'");
//			while (rs.next()) {
//				String insula_id = rs.getString("insula_id");
//				System.out.println("Content: '" + insula_id + "'");
//				return insula_id;
//			}
//		}
//		if (field.equals("graffiti_id")) {
//			ResultSet rs = stmt.executeQuery("SELECT graffiti_id FROM inscriptions WHERE graffiti_id='EDR"+graffiti_id.substring(3)+"'");
//			while (rs.next()) {
//				String temp_graffiti_id = rs.getString("graffiti_id");
//				System.out.println("Content: '" + temp_graffiti_id + "'");
//				return temp_graffiti_id;
//			}
//		}
//		else {
//			return "";
//		}
//		return "";
//	}
//
//	
//	private static ArrayList<ArrayList<String>> conductBoolSearch(String keyword) {
//		
//		ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
//		HighlightBuilder hlb = new HighlightBuilder();
//		for (String field : fields) {
//			results.add(new ArrayList<String>());
//			hlb.field(new HighlightBuilder.Field(field));
//		}
//		
//		BoolQueryBuilder query = boolQuery();
//		BoolQueryBuilder globalQuery = boolQuery();
//		globalQuery.should(queryStringQuery(keyword.toLowerCase()).useAllFields(true));
//		globalQuery.should(queryStringQuery("*" + keyword.toLowerCase() + "*").useAllFields(true));
//		query.must(globalQuery);
//		SearchResponse response = client.prepareSearch("agp")
//				.setTypes("inscription")
//				.setQuery(query)
//				.addStoredField("graffiti_id") // field has to be stored in AddInscriptionsToElasticSearch first
//				.setSize(500)
//				.addSort("id", SortOrder.ASC)
//				.highlighter(hlb)	
//				.execute()
//				.actionGet();
//		
//		System.out.println("Searching for '" + keyword + "':");
//		int count = 1;
//		for(SearchHit hit : response.getHits())
//		{
//			String id = hit.getField("graffiti_id").getValue();
//			System.out.println(count + ".) " + id);
//			
//			for (String highlight : hit.getHighlightFields().keySet()) {
//				for (int x = 0; x < fields.length; x++) {
//					if (highlight.equals(fields[x])) {
//						(results.get(x)).add(id);
//					}
//				}
//			}
//			count++;	
//		}
//		count -= 1;
//		System.out.println(count + " total results for this search.\n");
//		return results;
//	}
//	
//	private static void testQuery() {
//		SearchRequest sr = new SearchRequest("agp");
//		sr.types("inscription");
//		SearchSourceBuilder ssb = new SearchSourceBuilder();
//		HighlightBuilder highlightBuilder = new HighlightBuilder(); 
//		HighlightBuilder.Field highlightTitle = new HighlightBuilder.Field("content"); 
//		highlightTitle.highlighterType("unified");  
//		highlightBuilder.field(highlightTitle);
//		BoolQueryBuilder query = boolQuery();
//		query.should(queryStringQuery("X"));
//		ssb.query(query);
//		sr.source(ssb);
//		ActionFuture<SearchResponse> resp = client.search(sr);
//		System.out.println("Resp: " + resp);
//		System.out.println("Sr: " + sr);
//		System.out.println("Ssb: " + ssb);
//		
//	}
//	
//	private static ArrayList<String> conductSearch(String cat, String keyword) {
//		ArrayList<String> results = new ArrayList<String>();
//		QueryBuilder query = matchQuery(cat, keyword).fuzziness("AUTO");
//		SearchResponse response = client.prepareSearch("agp")
//				.setTypes("inscription")
//				.setQuery(query)
//				.addStoredField("graffiti_id") // field has to be stored in AddInscriptionsToElasticSearch first
//				.setSize(66)
//				.addSort("id", SortOrder.ASC)
//				.highlighter(new HighlightBuilder().field(new HighlightBuilder.Field("property.property_number"))
//						.field(new HighlightBuilder.Field("insula.insula_name")))
//				.execute()
//				.actionGet();
//		
//		System.out.println("Searching by " + cat + " for '" + keyword + "':");
//		int count = 1;
//		for(SearchHit hit : response.getHits())
//		{
//			String id = hit.getField("graffiti_id").getValue();
//			System.out.println("Result " + count + ": id=" + id);
//			results.add(id);
//			count++;
//			System.out.println(hit.getHighlightFields());
//		}
//		count -= 1;
//		System.out.println(count + " total results for this search.");
//		return results;
//	}
//	
//	private static ArrayList<String> simulateGlobalSearch(String parameter) {
//		ArrayList<String> results = new ArrayList<String>();
//		BoolQueryBuilder query = boolQuery();  //Root query does an AND on all sub-queries
//		BoolQueryBuilder globalQuery;
//		globalQuery = boolQuery();
//		System.out.println(globalQuery.should(queryStringQuery(parameter)
//				.useAllFields(true))); // exact match
//		System.out.println(globalQuery.should(queryStringQuery("*" + parameter + "*").useAllFields(true))); // partial match
//		query.must(globalQuery);
//		SearchResponse response = client.prepareSearch("agp").setTypes("inscription").setQuery(query)
//				.addStoredField("graffiti_id").setSize(66).addSort("id", SortOrder.ASC).get();
//		
//		System.out.println(response.getSuccessfulShards());
//		System.out.println(response.getTotalShards());
//		System.out.println(query);
//		SearchHit[] searchResults = response.getHits().getHits();
//		for (SearchHit hit : searchResults) {
//			String sourceAsString = hit.getSourceAsString();
//			if (sourceAsString != null) {
//				System.out.println(sourceAsString);
//			}
//			System.out.println("getIndex: " + hit.getIndex());
//			System.out.println("getIndex: " + hit.getSourceAsString());
//			System.out.println("getType: " + hit.getType());
//			System.out.println("getId: " + hit.getId());
//			System.out.println("getScore: " + hit.getScore());
//		}
////		int count = 1;
////		for(SearchHit hit : response.getHits())
////		{
////			
////			String id = hit.getField("graffiti_id").getValue();
////			System.out.println("Result " + count + ": id=" + id);
////			results.add(id);
////			count++;
////		}
////		count -= 1;
////		System.out.println(count + " total results for this search.");
//		return results;
//	}
//	
//	
//	// Data Base Methods
//	
//	private static void init() {
//		getConfigurationProperties();
//
//		try {
//			Class.forName(DB_DRIVER);
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//
//		try {
//			newDBCon = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static void getConfigurationProperties() {
//		Properties prop = Utils.getConfigurationProperties();
//
//		DB_DRIVER = prop.getProperty("db.driverClassName");
//		DB_URL = prop.getProperty("db.url");
//		DB_USER = prop.getProperty("db.user");
//		DB_PASSWORD = prop.getProperty("db.password");
//	}
//}
//
