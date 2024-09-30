#/bin/bash
# Script to make a deployable executable
# Put this into Maven?

CONFIG_TEMPLATES_DIR=src/main/resources/configuration_templates
CONFIG_FILE_DIR=src/main/resources

cd Graffiti

cp $CONFIG_TEMPLATES_DIR/configuration_local.properties $CONFIG_FILE_DIR/configuration.properties

cp $CONFIG_TEMPLATES_DIR/application_local.properties $CONFIG_FILE_DIR/application.properties

rm -f $CONFIG_TEMPLATES_DIR/logback-spring.xml


#mvn package
