/**
 * 
 */
package edu.wlu.graffiti.data.main;

import edu.wlu.graffiti.data.setup.InsertOnSiteContributors;
import edu.wlu.graffiti.data.setup.ExtractWritingStyleForAGPInfo;
import edu.wlu.graffiti.data.setup.InsertFeaturedGraffiti;
import edu.wlu.graffiti.data.setup.InsertFiguralInformation;
import edu.wlu.graffiti.data.setup.InsertMoreFeaturedGraffiti;
import edu.wlu.graffiti.data.setup.InsertTranslations;
import edu.wlu.graffiti.data.setup.UpdateSummaryTranslationCommentaryPlus;

/**
 * Handles importing the [new, modified] AGP data.
 * 
 * @author Sara Sprenkle
 */
public class UpdateAGPInfo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		InsertFiguralInformation.main(args);
		UpdateSummaryTranslationCommentaryPlus.main(args);
		InsertMoreFeaturedGraffiti.main(args);
		// InsertFeaturedGraffiti.insertFeaturedGraffiti();
		InsertTranslations.main(args);
		InsertOnSiteContributors.main(args);
		ExtractWritingStyleForAGPInfo.main(args);
		System.out.println("Done updating AGP info");
	}

}
