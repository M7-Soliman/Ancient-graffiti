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
import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.GraffitiDao;
import edu.wlu.graffiti.data.export.GenerateEpidoc;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;

/**
 * Controller for EpiDoc-related exports
 * 
 * @author Hammad Ahmad
 * @editor Trevor Stalnaker
 *
 */
@RestController
//@Api(tags= {"epidoc-controller"})
public class EpidocController {

	private GenerateEpidoc generator = new GenerateEpidoc();

	@Resource
	private GraffitiDao graffitiDao;

	@Resource
	private FindspotDao findspotDao;

//	@ApiOperation(value = "Download the individual graffito as an EpiDoc file.")
	@RequestMapping(value = "/graffito/{agpId}/xml", method = RequestMethod.GET, produces = "application/xml;charset=UTF-8")
	public String getInscription(@PathVariable String agpId, HttpServletResponse response) {
		String graffiti_id = agpId.replaceFirst("AGP-", "");
		response.addHeader("Content-Disposition", "attachment; filename=" + agpId + ".xml");
		return generator.serializeInscriptionToXML(graffitiDao.getInscriptionByID(graffiti_id));
	}

//	@ApiOperation(value = "Download all graffiti as an EpiDoc file.")
	@RequestMapping(value = "/all/xml", method = RequestMethod.GET, produces = "application/xml;charset=UTF-8")
	public String getInscriptions(HttpServletResponse response) {
		response.addHeader("Content-Disposition", "attachment; filename=all.xml");
		return generator.serializeInscriptionsToXML(graffitiDao.getAllInscriptions());
	}

	@SuppressWarnings("unchecked")
//	@ApiOperation(value = "Download the filtered graffiti as an EpiDoc file.")
	@RequestMapping(value = "/filtered-results/xml", method = RequestMethod.GET, produces = "application/xml;charset=UTF-8")
	public String getFilteredInscriptions(final HttpServletRequest request, HttpServletResponse response) {
		HttpSession s = request.getSession();
		response.addHeader("Content-Disposition", "attachment; filename=filtered-results.xml");
		List<Inscription> results = (List<Inscription>) s.getAttribute("filteredList");
		if( results == null ) {
			return "";
		}
		return generator.serializeInscriptionsToXML(results);
	}

	/**
	 * @RequestMapping("/property/{city}/{insula}/{property}/xml") public Property
	 * getProperty(@PathVariable String city, @PathVariable String
	 * insula, @PathVariable String property) { return
	 * findspotDao.getPropertyByCityAndInsulaAndProperty(city, insula, property); }
	 */

}
