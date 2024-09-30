package edu.wlu.graffiti.bean;

import java.io.Serializable;

/**
 * 
 * @author Trevor Stalnaker
 *
 */
public class Column implements Serializable{

	private static final long serialVersionUID = 1L;
	private int id;
	private String romanNumeral;
	private int decimal;
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getRomanNumeral() {
		return this.romanNumeral;
	}
	
	public void setRomanNumeral(String romanNumeral) {
		this.romanNumeral = romanNumeral;
	}
	
	public int getDecimal() {
		return this.decimal;
	}
	
	public void setDecimal(int decimal) {
		this.decimal = decimal;
	}
}
