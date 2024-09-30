package edu.wlu.graffiti.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import edu.wlu.graffiti.bean.User;
import edu.wlu.graffiti.dao.UserDao;
/**
 * This class works with the login.jsp file to control the login of users into the page.
 */
@Controller
public class LoginController {

	/**
	 * Data Access Object class that interacts with the database
	 */
	@Resource
	private UserDao userDao;
		
	/** 
	 * The method fetches the username and password that the user entered in the login form. 
	 * The method sets the user information as session attributes and sends the user to the admin page 
	 * if the login information is correct. If the login information is incorrect the method sends the user back 
	 * to the login page. 
	 * POST request
   * @param request, the request variable sent by the JSP to the servlet.
	 * @return a string representing the user.
	 * */
	@RequestMapping(value = "/LoginValidator", method = RequestMethod.POST)
	public String loginValidator(final HttpServletRequest request) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		HttpSession session = request.getSession();

		String error_msg = "";
		boolean error = false;
		if (username == null || username.equals("")) {
			error_msg += "Username is required.<br/>";
			error = true;
		}
		if (password == null || password.equals("")) {
			error_msg += "Password is required.<br/>";
			error = true;
		}
		if (error) {
			request.setAttribute("error_msg", error_msg);
			return "/login";
		}
		
		User user = userDao.getUser(username, password);
		
		if (user != null) {
			session.setAttribute("authenticated", true);
			session.setAttribute("name", user.getName());
			session.setAttribute("username", username);
			session.setAttribute("password", password);
			session.setAttribute("role", user.getRole());
			session.setMaxInactiveInterval(15 * 60);
			
			
			if (session.getAttribute("previousPage") != null) {
				return "redirect:" + (String) session.getAttribute("previousPage");
			}
			else {
				return "/admin/admin_page";
			}
		} else {	
			error_msg = "Username and/or Password is not correct";
			request.setAttribute("error_msg", error_msg);
			return "/login";
		}

	}
  
	/**
	 * This method returns the user to the login page.
	 * @param model, a ModemMap representing the login.
	 * @return a string representing the login page.
	 */
	@RequestMapping(value = "/login")
	public String login(ModelMap model) {
		return "/login";
	}

	/**
	 * This method represents a failed login attempt.
	 * @param model, a ModelMap representing the login.
	 * @return a string representing the login page
	 */
	@RequestMapping(value = "/loginfailed", method = RequestMethod.GET)
	public String loginerror(ModelMap model) {

		model.addAttribute("error", "true");
		return "/login";

	}
	/**
	 * This method deals with allowing the logged in user to logout of their account.
	 * @param request, the servlet request variable.
	 * @param response, the servlet response variable.
	 * @return a string representing the index web page.
	 * @throws ServletException
	 * @throws IOException
	 */

	/** 
	 * The logout method returns the user to the index (home) page and 
	 * invalidates the session.
	 * */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getSession().invalidate();
		return "/index";
	}

}