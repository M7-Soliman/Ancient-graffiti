package edu.wlu.graffiti.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wlu.graffiti.bean.User;
import edu.wlu.graffiti.dao.EditorDao;
import edu.wlu.graffiti.dao.UserDao;

/**
 * Handles editor-related functionality
 * 
 * @author sprenkle
 */
@Controller
public class EditorController {

	@Resource
	private EditorDao editorDao;
	@Resource
	private UserDao userDao;
	
	@RequestMapping(value = "/admin", method = RequestMethod.POST)
	public String adminPage(final HttpServletRequest request) {
		return "admin/admin_page";		
	}

	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String AdminPage(final HttpServletRequest request) {
		return "admin/admin_page";
	}
	
	/**
	 * Handles post request to add new editor
	 * @param request The HTTP request object that we use to set attributes
	 * @return A string that represents JSP page of the same page the use entered data in
	 */
	@RequestMapping(value = "/admin/addEditor", method = RequestMethod.POST)
	public String addEditor(final HttpServletRequest request) {
		HttpSession session = request.getSession();
		String userRole = (String) session.getAttribute("role");
		if (userRole.equals("admin")) {
		
			String username = request.getParameter("username");
			String name = request.getParameter("name");
			String password = request.getParameter("password2");
			String role = "editor";
			// Clean the input fields
			name = clean_fields(name);
			username = clean_fields(username);
			
			//password drawing null pointer exception
			password = clean_fields(password);
			// Check if user name exists
			if (editorDao.existingUsername(username)) {
				request.setAttribute("msg", "Username already in use. Use a different username.");
			} else {
				
				if (editorDao.insertUser(username, password, name, role)) {
					request.setAttribute("msg", username + " added as an editor.");
				} else {
					request.setAttribute("msg", "Check your information and try again.");
				}
			}
	
			return "admin/addEditor";
		}
		else {
			request.setAttribute("msg", "Editors cannot add new editors");
			return "admin/admin_page";
		}

	}
	
	@RequestMapping(value = "/admin/addEditor", method = RequestMethod.GET)
	public String AddEditor(final HttpServletRequest request) {
		HttpSession session = request.getSession();
		String userRole = (String) session.getAttribute("role");
		if (userRole.equals("admin")) {

			return "admin/addEditor";

		}
		else {
			request.setAttribute("msg", "Editors cannot add new editors");
			return "admin/admin_page";
		}
	}
	
	

	@RequestMapping(value = "/admin/changePassword", method = RequestMethod.POST)
	public String changePassword(final HttpServletRequest request) {
		BCryptPasswordEncoder bcrypt= new BCryptPasswordEncoder();
		String password1=request.getParameter("password1");
		String password2 = request.getParameter("password2");
		// Clean the input fields 
		password1 = clean_fields(password1);
		password2 = clean_fields(password2);

		// Check if the two passwords match
	
			if (password1.equals(password2)) {
				//Change the user object's password:
				HttpSession session =request.getSession();
				String username=(String)session.getAttribute("username");
				String password=(String)session.getAttribute("password");
				User user=userDao.getUser(username,password);
				if (!password.equals(password1)) {
					password1 = bcrypt.encode(password1);
					user.setPassword(password1);
					request.setAttribute("msg", "Password changed for "+username);
					//Alter the data in the database:
					userDao.changePasswordSQL(password1,username);
				}else {
					request.setAttribute("msg", "Cannot change password to current password!");
				}
				
			} else {
				request.setAttribute("msg", "Passwords do not match!");
			}
		return "admin/changePassword";
	}
	
	@RequestMapping(value = "/admin/changePassword", method = RequestMethod.GET)
	public String ChangePassword(final HttpServletRequest request) {
		return "admin/changePassword";
	}
	


	@RequestMapping(value = "/admin/RemoveEditors", method = RequestMethod.POST)
	public String removeEditors(final HttpServletRequest request) {
		HttpSession session = request.getSession();
		String userRole = (String) session.getAttribute("role");
		if (userRole.equals("admin")) {
			int i = 0;
			String[] usernames = request.getParameterValues("removeEditors");
			if (usernames != null) { // Handles if user click Remove Editor button
										// without choosing an editor.
				for (String username : usernames) {
					if (editorDao.deleteUser(username)) {
						i++;
					}
				}
				if (i > 0) {
					request.setAttribute("msg", "Editors Removed");
				} else
					request.setAttribute("msg", "There was an error.  Try again.");
	
			}
			return "admin/removeEditors";
		}
		else {
			request.setAttribute("msg", "Editors cannot remove other editors");
			return "admin/admin_page";
		}
	}
	
	@RequestMapping(value = "/admin/RemoveEditors", method = RequestMethod.GET)
	public String RemoveEditors(final HttpServletRequest request) {
		HttpSession session = request.getSession();
		String userRole = (String) session.getAttribute("role");
		if (userRole.equals("admin")) {
			List<User> editors = editorDao.getEditors();
			request.setAttribute("editors", editors);
			
			return "admin/removeEditors";
		}
		else {
			request.setAttribute("msg", "Editors cannot remove other editors");
			return "admin/admin_page";
		}
	}

	private String clean_fields(String data) {
		/* Cleans the input fields to avoid JS injection. */
		return data.replace("<", "").replace(">", "");
	}

}
