package edu.wlu.graffiti.controller;


import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.bean.Segment;
import edu.wlu.graffiti.bean.Street;
import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.GraffitiDao;
import edu.wlu.graffiti.dao.ReportDao;
import edu.wlu.graffiti.data.export.ExportInscriptions;

import edu.wlu.graffiti.data.export.ExportLemma;
import edu.wlu.graffiti.data.export.ExportPartOfSpeechInfo;
import edu.wlu.graffiti.data.export.GenerateCSV;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;

/**
 * CSVController handles all types of requests related to exporting data as CSV file
 * 
 * @author Hammad Ahmad
 * @author Trevor Stalnaker
 * @author Grace MacDonald
 * @author Linh Nguyen
 */
@RestController
//@Api(tags = {"csv-controller"})
public class CSVController {
	
	private GenerateCSV generator = new GenerateCSV();
	
	@Resource
	private GraffitiDao graffitiDao;

	@Resource
	private FindspotDao findspotDao;
	
	@Resource 
	private ReportDao reportDao;
	
//	@ApiOperation(value="Download the individual graffito as a CSV file.")
	@RequestMapping(value = "/graffito/{agpId}/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getInscription(@PathVariable String agpId, HttpServletResponse response) {
		String graffiti_id = agpId.replaceFirst("AGP-", "");
		response.addHeader("Content-Disposition", "attachment; filename="+ agpId +".csv");
		return generator.serializeToCSV(graffitiDao.getInscriptionByID(graffiti_id));
	}
	
//	@ApiOperation(value="Download all graffiti as a CSV file.")
	@RequestMapping(value = "/all/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getInscriptions(HttpServletResponse response) {
		response.addHeader("Content-Disposition", "attachment; filename=all-inscriptions.csv");
		return generator.serializeInscriptionsToCSV(graffitiDao.getAllInscriptions());
	}
	
	@SuppressWarnings("unchecked")
//	@ApiOperation(value="Download the filtered graffiti as a CSV file.")
	@RequestMapping(value = "/filtered-results/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getFilteredInscriptions(final HttpServletRequest request, HttpServletResponse response) {
		HttpSession s = request.getSession();
		List<Inscription> results = (List<Inscription>) s.getAttribute("filteredList");
		response.addHeader("Content-Disposition", "attachment; filename=filtered-results.csv");
		if( results == null ) {
			return "";
		}
		return generator.serializeInscriptionsToCSV(results);
	}
	
//	@ApiOperation(value="Download all properties as a CSV file.")
	@RequestMapping(value = "/properties/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String downloadProperties(final HttpServletRequest request, HttpServletResponse response) {
		
		final List<Property> properties = findspotDao.getProperties();

		for (Property p : properties) {
			p.setPropertyTypes(findspotDao.getPropertyTypeForProperty(p.getId()));
			
		}
		
		response.addHeader("Content-Disposition", "attachment; filename=all-properties.csv");
		return generator.serializePropertiesToCSV(properties);
	}

//	@ApiOperation(value="Download all properties in the city as a CSV file.")
	@RequestMapping(value = "/properties/{city}/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String downloadPropertiesByCity(@PathVariable String city, final HttpServletRequest request, HttpServletResponse response) {
		
		final List<Property> properties = findspotDao.getPropertiesByCity(city);

		for (Property p : properties) {
			p.setPropertyTypes(findspotDao.getPropertyTypeForProperty(p.getId()));
			
		}
		
		response.addHeader("Content-Disposition", "attachment; filename=" + city +"-properties.csv");
		return generator.serializePropertiesToCSV(properties);
	}
	
//	@ApiOperation(value="Download all properties in the city and insula as a CSV file.")
	@RequestMapping(value = "/properties/{city}/{insula}/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String downloadPropertiesByCityInsula(@PathVariable String city, @PathVariable String insula, final HttpServletRequest request, HttpServletResponse response) {
		
		final List<Property> properties = findspotDao.getPropertiesByCityAndInsula(city, insula);

		for (Property p : properties) {
			p.setPropertyTypes(findspotDao.getPropertyTypeForProperty(p.getId()));
			
		}
		
		response.addHeader("Content-Disposition", "attachment; filename=" + city + "-" + insula + "-properties.csv");
		return generator.serializePropertiesToCSV(properties);
	}
	
	@RequestMapping(value = "/lemmaTable/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getLemmaTable(HttpServletResponse response) {
		response.addHeader("Content-Disposition", "attachment; filename=lemma.csv");
		return ExportLemma.serializeToCSV();
	}
	
	@RequestMapping(value = "/figuralTable/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getFiguralTable(HttpServletResponse response) {
		response.addHeader("Content-Disposition", "attachment; filename=all_figural_data.csv");
		List<List<String>> data = reportDao.getAllFig();
		return generator.serializeFiguralInscriptionsToCSV(data);
	}
	
	@RequestMapping(value = "/unconfirmedPoetryTable/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getUnconfirmedPoetryTable(HttpServletResponse response) {
		response.addHeader("Content-Disposition", "attachment; filename=unconfirmed_poetry_data.csv");
		List<List<String>> data = reportDao.getUnconfirmedPoetry();
		return generator.serializePoetryToCSV(data);
	}
	@RequestMapping(value = "/confirmedPoetryTable/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getConfirmedPoetryTable(HttpServletResponse response) {
		response.addHeader("Content-Disposition", "attachment; filename=confirmed_poetry_data.csv");
		List<List<String>> data = reportDao.getConfirmedPoetry();
		return generator.serializePoetryToCSV(data);
	}
	
	@RequestMapping(value = "/unrecognizedFiguralTable/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getUnrecognizedFiguralTable(HttpServletResponse response) {
		response.addHeader("Content-Disposition", "attachment; filename=unidentified_figural.csv");
		List<List<String>> missing = reportDao.getUnrecognizedFig();
		return generator.serializeUnrecognizedFiguralToCSV(missing);
	}
	
	@RequestMapping(value = "/missingTable/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getmissingTable(HttpServletResponse response) {
		response.addHeader("Content-Disposition", "attachment; filename=missing_data.csv");
		List<List<String>> missing = reportDao.getMissingInfo();
		return generator.serializeMissingInfoToCSV(missing);
	}
	
	@RequestMapping(value = "/missingFiguralTable/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getMissingFiguralTable(HttpServletResponse response) {
		response.addHeader("Content-Disposition", "attachment; filename=missing_data.csv");
		List<List<String>> missing = reportDao.getMissingFiguralInfo();
		return generator.serializeMissingFiguralInfoToCSV(missing);
	}
	
	@RequestMapping(value = "/langnerTable/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getLangnerTable(HttpServletResponse response) {
		response.addHeader("Content-Disposition", "attachment; filename=langner.csv");
		List<List<String>> data = reportDao.getMissingLangner();
		return generator.serializeLangnerInfoToCSV(data);
	}
	
	@RequestMapping(value = "/textualTable/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getTextualTable(HttpServletResponse response) {
		response.addHeader("Content-Disposition", "attachment; filename=all_textual.csv");
		List<List<String>> data = reportDao.getAllTxt();
		return generator.serializeTextualInscriptionsToCSV(data);
	}
	
	@RequestMapping(value = "/missingFindspotTable/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getMissingFindspot(HttpServletResponse response) {
		response.addHeader("Content-Disposition", "attachment; filename=missing_findspot.csv");
		List<List<String>> missing = reportDao.getMissingFindspot();
		return generator.serializeMissingFindspotToCSV(missing);
	}
	
	@RequestMapping(value = "/posTable/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getPosTable(HttpServletResponse response) {
		response.addHeader("Content-Disposition", "attachment; filename=pos.csv");
		return ExportPartOfSpeechInfo.serializeToCSV();
	}
	
	@RequestMapping(value = "/streets/sections/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getSegments(HttpServletResponse response) {
		final List<Segment> segments = findspotDao.getSegmentsWithFacades();	
		response.addHeader("Content-Disposition", "attachment; filename=segments.csv");
		return generator.serializeSegmentsToCSV(segments);
	}
	
	@RequestMapping(value = "/streets/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getStreets(HttpServletResponse response) {
		final List<Street> streets = findspotDao.getStreetsWithFacades();	
		response.addHeader("Content-Disposition", "attachment; filename=streets.csv");
		return generator.serializeStreetsToCSV(streets);
	}
	@RequestMapping(value = "/inscriptions/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getInscriptionsTable(HttpServletResponse response) {
		response.addHeader("Content-Disposition", "attachment; filename=inscriptions.csv");
		return ExportInscriptions.serializeToCSV();
	}
	/**
	@RequestMapping("/property/{city}/{insula}/{property}/csv")
	public Property getProperty(@PathVariable String city, @PathVariable String insula, @PathVariable String property) {
		return findspotDao.getPropertyByCityAndInsulaAndProperty(city, insula, property);
	}
	*/
	@RequestMapping(value = "/figuralCaptionOccurrences/{city}/csv", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	public String getFiguralCaptionOccurrences(@PathVariable String city, HttpServletResponse response) {
		response.addHeader("Content-Disposition", "attachment; filename=figuralcaptionoccurrences"+ city + ".csv");
		List<List<String>> figuralCaptions = reportDao.getFiguralCaptionOccurrences(city);
		return generator.serializeFiguralCaptionsToCSV(figuralCaptions);
	}
}
