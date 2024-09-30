/**
 * 
 */
package edu.wlu.graffiti.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import edu.wlu.graffiti.bean.User;
import edu.wlu.graffiti.data.rowmapper.UserRowMapper;

/**
 * Code to access and manipulate the Users (editors) table
 * 
 * @author sprenkle
 *
 */

@Repository
public class EditorDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final String DELETE_FROM_USERS_WHERE_USERNAME = "DELETE FROM users WHERE username=?";

	private static final String SELECT_STATEMENT = "SELECT * from users";

	private static final String SELECT_BY_USERNAME = "SELECT count(*) from users where username = ?";
	
	private static final String SELECT_BY_ROLE = "SELECT * from users where role = ?";

	private static final String INSERT_USER = "INSERT INTO users " + "(username,password,name,role)"
			+ " VALUES (?,?,?,?)";

	public List<User> getUsers() {
		List<User> users = jdbcTemplate.query(SELECT_STATEMENT, new UserRowMapper());
		return users;
	}
	
	public List<User> getEditors() {
		List<User> users = jdbcTemplate.query(SELECT_BY_ROLE, new UserRowMapper(), "editor");
		return users;
	}

	public boolean deleteUser(String user) {
		return jdbcTemplate.update(DELETE_FROM_USERS_WHERE_USERNAME, user) > 0;
	}

	public boolean insertUser(String username, String password, String name, String role) {
		BCryptPasswordEncoder bcrypt= new BCryptPasswordEncoder();
		password = bcrypt.encode(password);
		return jdbcTemplate.update(INSERT_USER, username, password, name, role) > 0;
	}

	public boolean insertUser(User user) {
		return insertUser(user.getUserName(), user.getPassword(), user.getName(), user.getRole());
	}

	public boolean existingUsername(String username) {
		Integer cnt = jdbcTemplate.queryForObject(SELECT_BY_USERNAME, Integer.class, username);
		return cnt != null && cnt > 0;
	}

	/*
	 * public List<User> getPendingUsers() { List<User> results = query(
	 * "SELECT username FROM users WHERE enabled is NULL", new UserRowMapper());
	 * return results; }
	 * 
	 * public void approveUsers(Object[] users) { String sql = "UPDATE users " +
	 * "SET enabled='true'" + "where username=(?)";
	 * 
	 * for (int x = 0; x < users.length; x++) { update(sql, users[x]); } }
	 */

}