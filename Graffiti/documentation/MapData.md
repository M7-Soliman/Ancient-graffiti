# Generating Data for Map

To create our interactive maps, we need **geoJSON** data, which describes the shapes of what we want to show on the maps (often, properties or insulae) and also includes information about the number of inscriptions in those shapes.  Our maps are then implemented using LeafletJS.

## Herculaneum

We generated a map in OpenStreetMap and extracted the geoJSON files with the required information.

## Pompeii

We used Eric Poehler's PBMP map, with modifications, to get the data for our map.  

## GeoJSON Data Files

The data files we need for the Herculaneum maps are
  - herculaneum_insula.geojson -- this one doesn't seem to be directly used
  - herculaneum.geojson

The data files we need for the Pompeii maps are
  - pompeii_insula.geojson
  - agp_pompeii_properties.json

## Generating JavaScript files

For Leaflet to read in the data, the geoJSON needs to be saved as a variable, so we add a little bit of code to the geoJSON files to create JavaScript files.

We have two types of JavaScript files:
1. containing shapes that need to be displayed (only)
2. containing shapes that are clickable and need to be combined with the database to learn how many graffiti are in them

### Generating JavaScript for Display-Only Shapes


### Generating JavaScript for Clickable Shapes

#### map_data.csv

This file contains the name of the original geoJSON data file (without the .geojson extension).
