package edu.wlu.graffiti.test;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import edu.wlu.graffiti.controller.GraffitiController;
import edu.wlu.graffiti.data.setup.Utils;

public class TestSearchQueries {
	private static RestHighLevelClient client;
	private static Properties prop = null;

	/** elastic search configuration properties */
	private static String ES_HOSTNAME;
	private static int ES_PORT_NUM;
	private static String ES_INDEX_NAME;

	private static final int NUM_RESULTS_TO_RETURN = 2000;

	public static void main(String[] args) {
		List<String> parameters = new ArrayList<>();
		parameters.add("SMYD00154");
		init();
		client = new RestHighLevelClient(RestClient.builder(new HttpHost(ES_HOSTNAME, ES_PORT_NUM, "http")));
		BoolQueryBuilder query = boolQuery();
		BoolQueryBuilder globalQuery = GraffitiController.createGlobalSearchQuery(parameters, 0);
		query.must(globalQuery);

		SearchRequest searchRequest = new SearchRequest(ES_INDEX_NAME);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(query);
		searchSourceBuilder.storedField("graffiti_id");
		searchSourceBuilder.size(NUM_RESULTS_TO_RETURN);
		searchRequest.source(searchSourceBuilder);
		try {
			int count=0;
			SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
			for (SearchHit hit : response.getHits()) {
				String graffitiID = hit.field("graffiti_id").getValue();
				System.out.println(graffitiID);
				count++;
			}
			System.out.println("Found: " + count);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done!");

	}

	private static void init() {
		if (prop == null) {
			prop = Utils.getConfigurationProperties();
			ES_HOSTNAME = prop.getProperty("es.loc");
			ES_PORT_NUM = Integer.parseInt(prop.getProperty("es.port"));
			ES_INDEX_NAME = prop.getProperty("es.index");
		}
	}
}
			
