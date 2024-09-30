package edu.wlu.graffiti.test;

public class Coordinate {
	
	double x;
	double y;
	
	public Coordinate(double x, double y) {	
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
}
