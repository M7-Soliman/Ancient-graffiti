package edu.wlu.graffiti.bean;

import java.io.Serializable;

/**
 * Represents a contribution, i.e., an addition to the EpiDoc of the graffito.
 * 
 * @author Trevor Stalnaker
 *
 */
public class Contribution implements Serializable {

	private static final long serialVersionUID = 1L;
	private String inscription_id;
	private String user_name;
	private String comment;
	private String date;

	public Contribution() {
		super();
	}

	public Contribution(String inscription_id, String user_name, String comment, String date) {
		super();
		this.inscription_id = inscription_id;
		this.user_name = user_name;
		this.comment = comment;
		this.date = date;
	}

	@Override
	public String toString() {
		return "Contribution [inscription_id=" + inscription_id + ", user_name=" + user_name + ", comment=" + comment
				+ ", date=" + date + "]";
	}

	public String getInscriptionId() {
		return this.inscription_id;
	}

	public void setInscriptionId(final String inscription_id) {
		this.inscription_id = inscription_id;
	}

	public String getUserName() {
		return this.user_name;
	}

	public void setUserName(final String user_name) {
		this.user_name = user_name;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(final String date) {
		this.date = date;
	}
}
