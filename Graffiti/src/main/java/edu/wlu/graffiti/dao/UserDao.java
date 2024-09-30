package edu.wlu.graffiti.dao;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import edu.wlu.graffiti.bean.User;
import edu.wlu.graffiti.data.rowmapper.UserRowMapper;

@Repository
public class UserDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final String INSERT_USER_STATEMENT = "INSERT INTO users " + "(username,password,enabled)"
			+ " VALUES (?,?,'True')";

	
	public static final String INSERT_ADMIN_STATEMENT = "INSERT INTO users (username, name, role, password, enabled)"
			+ " VALUES (?,?, 'admin', ?, 'True')";
	
	private static final String GET_USER_STATEMENT = "SELECT * FROM users WHERE username = ?";

	/**
	 * Inserts the user into the database
	 * 
	 * @param user
	 *            - list is in order: username, password, enabled
	 */
	public void insertUser(ArrayList<String> user) {
		jdbcTemplate.update(INSERT_USER_STATEMENT, user.toArray());
	}
	
	
	/**
	 * Inserts an admin user into the database
	 * 
	 */
	public void insertAdminUser(String username, String name, String password) {
		jdbcTemplate.update(INSERT_ADMIN_STATEMENT, username, name, password);
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return the User object if the username and password match the database;
	 *         otherwise, returns null.
	 */
	public User getUser(String username, String password) {
		BCryptPasswordEncoder bcrypt= new BCryptPasswordEncoder();
		List<User> userList = jdbcTemplate.query(GET_USER_STATEMENT, new UserRowMapper(), username);
		
		if (userList.size() == 0) {
			return null;
		}
		
		User user = userList.get(0);
		
		if (userList.isEmpty() || !bcrypt.matches(password, user.getPassword())) {
			return null;
		} else {
			return user;
		}
	}

	// A function to update an SQL statement in the SQL table
	public void changePasswordSQL(String newPassword, String username) {
		String SQL = "UPDATE users SET PASSWORD=? WHERE username=?;";
		jdbcTemplate.update(SQL, newPassword, username);
		return;
	}

}
