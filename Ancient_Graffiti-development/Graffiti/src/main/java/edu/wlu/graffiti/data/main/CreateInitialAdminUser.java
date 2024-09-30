package edu.wlu.graffiti.data.main;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import edu.wlu.graffiti.dao.UserDao;

/**
 * Create the default initial admin user.
 * 
 * @author Sara Sprenkle
 */
public class CreateInitialAdminUser extends DBInteraction {

	public static void main(String[] args) {
		CreateInitialAdminUser creator = new CreateInitialAdminUser();
		creator.runDBInteractions();
	}

	@Override
	public void run() {
		try {

			PreparedStatement insertAdminStmt = dbCon.prepareStatement(UserDao.INSERT_ADMIN_STATEMENT);

			BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
			String encryptedPassword = bcrypt.encode("admin");

			insertAdminStmt.setString(1, "admin");
			insertAdminStmt.setString(2, "Admin McAdmin");
			insertAdminStmt.setString(3, encryptedPassword);
			int updates = insertAdminStmt.executeUpdate();
			if (updates != 1) {
				System.err.println("Error inserting initial admin user.");
			} else {
				System.out.println("Successing inserting initial admin user.");
			}

			dbCon.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

}
