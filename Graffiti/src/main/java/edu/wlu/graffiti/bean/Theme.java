package edu.wlu.graffiti.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a theme for the featured graffiti
 * 
 * @author Hammad Ahmad
 *
 */
public class Theme implements Comparable<Theme> , Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String description;
	private boolean hasWarmUpResource;

	public Theme() {
		super();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(id);
		builder.append(" ");
		builder.append(name);
		return builder.toString();
	}

	@JsonIgnore
	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonIgnore
	public String getDescription() {
		return description;
	}

	public void setDescription(String desc) {
		description = desc;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result + this.id;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Theme))
			return false;
		final Theme other = (Theme) obj;
		if (this.name == null) {
			if (other.name != null)
				return false;
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		return this.id == other.id;
	}

	/**
	 * compares by name, alphabetically
	 */
	public int compareTo(final Theme theme) {
		return this.getName().compareTo(theme.getName());
	}

	public boolean hasWarmUpResource() {
		return hasWarmUpResource;
	}

	public void setHasWarmUpResource(boolean hasWarmUpResource) {
		this.hasWarmUpResource = hasWarmUpResource;
	}

}
