# Preparing Public Release

This repository is for our authenticated development.  When we make a public release of the code, we don't want to include all of this code, files, etc. 

## Files to Remove

Should create a script to handle the deletion.

  * EDRData2* -- these are the old data files; they can all be removed
  * configuration_*.properties
  * web_*.properties
  * the unversioned files -- don't want to accidentally put them into the public repo
  * LogFilter.java

## Files to Update

  * `configuration.properties` should be a template file -- fill in the username/password placeholders
  * `web.xml` should be a template file -- doesn't include the logging