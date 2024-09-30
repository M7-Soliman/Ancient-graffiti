# AGP Data Set Up

## Database configuration

Need the DB schema, which is in `documentation/agp_schema.sql`.  Set up your database to use the schema.

### configuration.properties

Make sure `src/main/resources/configuration.properties` has the appropriate settings to point to your database.

## Cities

We manually create the `cities.csv` file with the cities' information.

## Get EDR Data

EDR generates CSV files for us, which should be stored in the directory `data/EDRData`

## AGP Data from Classicists on Google Drive

**See the README file in Data directory in GoogleDrive for more info about what is there.**

1. Get Insula and Properties from Google Drive
    - from `Herculaneum Properties`
      - download as CSV from `InsulaeForDB` and `PropertiesForDB` tabs, save as `data/hercluaneum_insulae.csv` and `data/herculaneum_properties.csv`
    - from `Pompeii Property List`
      - There is a field that says whether the property should be included in AGP.  Those properties are selected and put into the `InsulaForDB` and `PropertiesForDB` tabs.
      - download as CSV from `InsulaeForDB` and `PropertiesForDB` tabs, save as `data/pompeii_insulae.csv` and `data/pompeii_properties.csv`
      - **Note:** Because we cannot create [reasonable] rules for automatically ordering the insulae and properties, the insulae and properties must be in the order that we want them presented in the file.
2. Get the Corrected Pompeii Property Links from Google Drive
	- from `CorrectedPIPURLs`
	- Download as CSV, save as `data/AGPData/corrected_pip_urls.csv`
2. Get Facades, Streets, and Segments from Google Drive
  - from `Facades, Streets, and Segments`
    - download as CSV from `streets` tab, save as `data/AGPData/streets.csv`
    - download as CSV from `segments2graffiti` tab, save as `data/AGPData/segments2graffiti.csv`
2. Get Caption data, etc. from Google Drive
	- `Pompeii - TextualGraffiti - Captions and translations` from `ForDB` tab tab, which merges all the tabs together
		- We save in file `data/AGPData/pompeii_summary.csv`
	- `Herculaneum-TextualGraffiti-Captions,Translations,Etc.` from `ForDB` tab
		- We save in file `data/AGPData/herc_summary.csv`
	- `Smyrna-Graffiti-captions and descriptions`
		- We save in file `data/AGPData/smyrna_summary.csv`
3. Get data for Camodeca in `Camodeca` spreadsheet from Google Drive
    - mimics EDR spreadsheets in several tabs within the spreadsheet. Tabs are exported and saved in files named as follows:
        - `data/AGPData/camodeca_apparatus.csv`
        - `data/AGPData/camodeca_editiones.csv`
        - `data/AGPData/camodeca_epigr.csv`
        - `data/AGPData/camodeca_testo.csv`
4. Get information about figural graffiti from Google Drive
    - `Herculaneum-Figural Graffiti - Latin and English descriptions`
        - named as `data/AGPData/herc_figural.csv`
    - `Pompeii - Figural Graffiti - Latin and English descriptions and captions` from `Master` tab
         - named as `data/AGPData/pompeii_figural.csv`
5. Get featured graffiti info from Google Drive
     - `Featured Graffiti` - saved as `data/AGPData/more_featured_graffiti.csv`
     - `Translation Graffiti` - saved as `data/AGPData/translation_graffiti.csv`
7. Get the on-site contributors from Google Drive
     - Download the `ForDB` tab of `Contributors` (which combines all the tabs into one)
     - saved as `data/AGPData/contributors.csv`

## Atypical Findspots

Herculaneum has findspots that do not conform to the typical address structure (`insula.property`). In the file `data/AGPData/atypical_findspots.csv`, we list these atypical findspots.  The names must match the names (either the name or the Italian name) for the property listed in the database.  During the `ImportEDRData` script, if the findspot doesn't match the typical address structure, the code checks if the findspot starts with one of the atypical findspots.

## Populate the Database

1. Populate the default data. This data isn't expected to change frequently, so we separate it from the rest of the populating scripts.  Run `edu.wlu.graffiti.data.setup.main.PopulateDefaultDataInDatabase` to automatically run the following scripts (all in `edu.wlu.graffiti.data.setup.main`)
    - Insert Property Types - `InsertPropertyTypes`
    - Insert Drawing Tags - `InsertDrawingTags`
    - Insert Themes - `InsertThemes`
2. Populate Data -- run `edu.wlu.graffiti.data.setup.main.PopulateDatabase` to execute all the following scripts together to populate the database.
	- Insert cities and insula - `InsertCitiesAndInsulae`
		- For Herculaneum, Pompeii, and Smyrna
		- Cities info is contained in `data/cities.csv`
		- The insulae info is contained in `data/{city}_insulae.csv`
	- Insert properties, including the mapping to their property type(s) - `InsertProperties`
	- Insert graffiti - `ImportEDRData`.  Automatically calls
		- `HandleFindspotsWithoutAddresses.populateFindSpotPropertyIdsMapping();` -- to help with handling addresses that aren't typical, i.e. don't have the typical Regio.Insula.Property address
		- `StorePropertiesFromDatabaseForgeoJsonMap.storeProperties();` -- merges property data from database with the map to display how many graffiti are in a property
		- `StoreInsulaeFromDatabaseForgeoJsonMap.storeInsulae();` -- merges insula data from database with the map to display how many graffiti are in an insula
		- `InsertContributors.insertContributors();` -- so we can keep track of the contributors, as listed in EDR.
	- Translate EDR data (typically in Latin) for use in AGP - `ConvertEDRToAGP` automatically calls
		- `AddEDRLinksToApparatus.addEDRLinksToApparatus();`
		- `ExtractEDRLanguageForAGPInfo.updateAGPLanguage();`
		- `ExtractWritingStyleForAGPInfo.updateWritingStyle();`
	- Import Smyrna inscriptions: `ImportSmyrnaInscriptions` automatically calls
		- `ReadFromEpidoc.readInData()`
		- `InsertSmyrnaData.insertSmyrnaData()` - reads from `data/AGPData/smydata.csv`
	- Import AGP's original content: `UpdateAGPInfo`; automatically calls
		- `InsertFiguralInformation.insertFiguralInfo();`
		- `UpdateSummaryTranslationCommentaryPlus.updateInfo();`
		- `InsertFeaturedGraffiti.insertFeaturedGraffiti();`
		- `InsertTranslations.insertTranslationGraffiti();`
		- `InsertOnSiteContributors.insertContributors();` - to keep track of the people who measured/recorded the data on site
	- Add street and street section information to the database - `InsertStreetsAndSegments`
	- Remove inscriptions where the properties are not "known" in this set of data (typically in Pompeii): `CleanUpDBForDeployment`
	- Generate the indices based on the content in the inscriptions: `AddIndices`

## Autogenerated Information

Some data, we do not store in the database but instead generate automatically. This is an incomplete list of that information:

  * Pompeii properties' Pompeii in Pictures links
  * Poehler's linked open data demo site links. This seems to not work anymore. We should probably get rid of it.

## Populate the Elasticsearch Index

After the database is populated with all the data, run `edu.wlu.graffiti.data.setup.main.AddInscriptionsToElasticSearch`

## Make the Maps

After the database is populated, generate the Java Script files for the maps by running `MakeMaps`; automatically calls
 * `CreateDisplayOnlyJSFilesForMap`
 * `CreatePropertyJSFilesForMap`
 * `CreateInsulaJSFilesForMap`
 * `CreateStreetJSFilesForMap`
 * `CreateStreetSectionJSFilesForMap`

## Generate AGP

To generate everything needed for AGP (populating the database, populating elasticsearch, making maps), run `Main`.
