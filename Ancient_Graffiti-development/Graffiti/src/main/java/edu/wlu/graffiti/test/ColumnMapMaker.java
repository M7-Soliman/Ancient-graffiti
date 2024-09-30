package edu.wlu.graffiti.test;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ColumnMapMaker {
	
	private static String SAVE_LOCATION = "src/main/resources/geoJSON/columns.json";
	
	private static String START_TEXT = "{\"type\":\"FeatureCollection\",\"features\":[";
	private static String END_TEXT = "]}";
	
	private static String START_FEATURE_1 = "{\"type\":\"Feature\",\"properties\":{";
	private static String START_FEATURE_2 = "},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[";
	private static String END_FEATURE = "]}}";
	
	private static ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
	private static ArrayList<Coordinate> new_coords = new ArrayList<Coordinate>();
	
	// The coordinates for column 36
	private static double[] xs = {14.493788480758667,
								  14.493801891803741,
								  14.493801891803741,
								  14.493788480758667,
								  14.493788480758667};
	private static double[] ys = {40.75000827647032,
								  40.75000827647032,
								  40.75002250010044,
								  40.75002250010044,
								  40.75000827647032};
	
	// The slope (rise/run) for columns along the west side
	private static double west_slope =  -1.243703704;
	private static double x_run_west =  -0.000017;
	private static double y_rise_west =  x_run_west * west_slope;
	
	// The slope (rise/run) for columns along the south side
	private static double south_slope =   0.50692308;
	private static double x_run_south =  -0.0000275;
	private static double y_rise_south = x_run_south * south_slope;
	
	// The slope (rise/run) for columns along the north side
	private static double north_slope  =  0.464444444;
	private static double x_run_north  = 0.0000275;
	private static double y_rise_north = x_run_north * north_slope;
	
	// Shifts to the first column of the west side from initial coordinates
	// This starts the west side flush with the south side
	private static double x_shift_west =  -0.0000125 + x_run_west;
	private static double y_shift_west =  -0.000067913 + y_rise_west;
	
	// Shifts to the first column of the south side from initial coordinates
	private static double x_shift_south =  0.00095;
	private static double y_shift_south =  0.00042;
	
	// Shifts to the first column of the north side from initial coordinates
	private static double x_shift_north = -0.0008115 + x_run_north;
	private static double y_shift_north =  0.000925806 + y_rise_north;
	
	// Determines the slope of the top two rows
	private static double x_shift_top = -0.00007875;
	private static double y_shift_top = -0.000035;
	
	// Translates the third row relative to the first row
	private static double x_shift_third_row =  0.00075;
	private static double y_shift_third_row = -0.0009;
	

	public static void main(String[] args) {
		for (int i=0; i < xs.length; i++) {
			coords.add(new Coordinate(xs[i],ys[i]));
		}
		createJSON();
	}
	
	private static void createJSON() {
		try {
			PrintWriter writer = new PrintWriter(SAVE_LOCATION, "UTF-8");
			writer.println(START_TEXT);
			//Create the columns along the South Side
			buildRow(writer, 36, 1, x_shift_south, y_shift_south, x_run_south, y_rise_south, false);
			//Create the columns along the West Side
			buildRow(writer, 47, 37, x_shift_west, y_shift_west, x_run_west, y_rise_west, false);
			//Create the columns along the North Side
			buildRow(writer, 35, 84, x_shift_north, y_shift_north, x_run_north, y_rise_north, true);
			writer.println(END_TEXT);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private static void buildRow(PrintWriter writer, int total, int first_col, double init_x_shift, double init_y_shift, 
			double x_shift, double y_shift, boolean last) {
		int count = 0;
		while (count < total) {
			new_coords = new ArrayList<Coordinate>();
			for (Coordinate coord : coords) {
				new_coords.add(new Coordinate((coord.getX() + init_x_shift) + (x_shift * count),
						   (coord.getY() + init_y_shift) + (y_shift * count)));
			}
			if(count == 46) {
				System.out.println(new_coords);
			}
			writer.println(START_FEATURE_1);
			writer.println("\"column_number\":\"" + (first_col + count) + "\"");
			writer.println(START_FEATURE_2);
			writer.println(new_coords);
			if (last && count == total - 1) { writer.println(END_FEATURE);}
			else{ writer.println(END_FEATURE + ",");}
			count++;
		}
	}
}

