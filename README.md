# AGP_WA_Development
Development of the AGP Web Application.  Since this is a private repository, the notes in the README are different from what we'd have in a public repository.

## Repository Branches:

  * `main` - what will be deployed to the server
  * `development` - what we will work from

## Checking out from GitHub into Eclipse

2. Open the Git Repositories view, by navigating to `Window` -> `Show View` -> 'Other' -> `Git` -> `Git Repositories`
3. Click "Clone a Git Repository"
   * Add the link for this repository (https://github.com/WLU-CSCI335-S24/CSCI335-AGP.git)
      * Enter your username and password/API token for Github
   * Select the `development` branch
   * The defaults are probably okay on this screen, BUT make sure to check the "Import all existing Eclipse projects after clone finishes" checkbox
   * Click `Finish`
4. Import the project
   * In the Git repository view, you should see the repository
   * Expand "Working Tree"
   * Right-click on "Graffiti" and select "Import projects"
   * All of the defaults should be fine.  Click "Finish"
   
## Prerequisites

Install the latest version of [Tomcat 9.x](https://tomcat.apache.org/download-90.cgi)

If you are off-campus, you need to get on the VPN to be able to access the database and elasticsearch servers.


## Setting up project in Eclipse

After following the directions for checking out the project, you're ready to get set up within Eclipse:

1. Create a new Tomcat 9 server
1. Expand the project.  You should see a typical Web Application project folder.  You'll see greater than marks and question marks on folders/files that were added.
2. Under Java Resources, you should see the top-level directories `src/main/java` and `src/test/java` and no other directories.
3. Right-click on the project, go to [Maven](https://maven.apache.org/), then select "Update Project".  Click ok to update the project.
4. Two configuration files need to be set up to run within the desired environment: `application.properties` and `configuration.properties`.  We have multiple versions of these files for the various environments.

    1. Copy the file `src/main/resources/configuration_local.properties` as `configuration.properties` in the same directory.
    2. Copy the file `src/main/resources/application_local.properties` as `application.properties` in the same directory.	

  Alternatively, you can run the script `setupLocal.sh` in the parent directory.  Similarly, you can run `makeDeployable.sh` to set up the code for deploying it on the server.
  
  ** Do not add the configuration files to the git repository.  They are set to be ignored. **

5. Right-click on the project, go to "Run As" and select "Run on Server" and run on the server as usual on the Tomcat 9 server. This step is to show that it can run, however the maps should not show up correctly yet. Stop the server.
6. In Eclipse, navigate to `src/main/java/edu.wlu.graffiti.data.main` and find `MakeMaps.java`. Right-click on `MakeMaps.java`, go to "Run As" and select "Java Application."
7. After it runs, hit the F5 key to refresh, restart the server and now the maps should show up correctly.
	1. Note that `MakeMaps.java` will create new JavaScript files in `src/main/webapp/resources/js`.

## Our Workflow
When you work on something new,

1. create a new branch (locally), named by that feature
2. develop, adding and removing files, and committing your changes as you go
3. develop code until you are satisfied with it and do a final commit
4. push your branch
5. Go to GitHub and do a pull request to the `development` branch, explaining your changes
6.  You or, ideally, one of your teammates will approve and merge the pull request.

The `development` branch will be deployed to the development server, periodically.  We'll need to test this code to make sure things are working.  I'll test, but I need you all to test too.

Eventually, when we are satisfied that the code is all working and ready, we'll merge the development branch into the `main` branch and push the main branch to GitHub.  The main branch will be deployed on the production server.


## Setting Up the Data

See `documentation/SetUpData.md`

## Generating the Data for the Maps

See `documentation/MapData.md`

## Set up and Configuring Server

This is _not_ your local machine but the web application server.

### Postgresql

### Elasticsearch

Install Elasticsearch

Edit configuration file: /etc/elasticsearch/elasticsearch.yml

  * cluster.name: agp-cluster
  * network.host: 0
  * node.name: "My Node"

Install analysis-icu plugin
  * Find where the code for elastic search is (e.g., /usr/share/elasticsearch)
  * Run bin/elasticsearch-plugin install analysis-icu
