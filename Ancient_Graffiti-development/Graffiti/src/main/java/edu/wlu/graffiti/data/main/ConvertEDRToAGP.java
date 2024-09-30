package edu.wlu.graffiti.data.main;

import edu.wlu.graffiti.data.setup.AddEDRLinksToApparatus;
import edu.wlu.graffiti.data.setup.ExtractEDRLanguageForAGPInfo;
import edu.wlu.graffiti.data.setup.ExtractWritingStyleForAGPInfo;

/**
 * Much of EDR is in Latin. We need to convert the EDR info into English for use in AGP.
 * 
 * @author Sara Sprenkle
 * @author Trevor Stalnaker
 *
 */
public class ConvertEDRToAGP {

	public static void main(String[] args) {
		AddEDRLinksToApparatus addEDRLinksToApparatus = new AddEDRLinksToApparatus();
		addEDRLinksToApparatus.addEDRLinksToApparatus();
		ExtractEDRLanguageForAGPInfo extractEDRLanguageForAGPInfo = new ExtractEDRLanguageForAGPInfo();
		extractEDRLanguageForAGPInfo.updateAGPLanguage();
		ExtractWritingStyleForAGPInfo extractWritingStyleForAGPInfo = new ExtractWritingStyleForAGPInfo();
		extractWritingStyleForAGPInfo.updateWritingStyle();
		System.out.println("Done converting EDR data to AGP");
	}
}
