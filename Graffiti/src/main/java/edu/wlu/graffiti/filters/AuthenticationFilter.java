package edu.wlu.graffiti.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Servlet Filter implementation class AuthenticationFilter
 */
@WebFilter(description = "Checks if an admin is authenticated.", urlPatterns = { "/admin/*" })
public class AuthenticationFilter implements Filter {

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession(false);
		if (session == null) {
			returnError(request, response, "You are not authorized.", "/login");
		return;
		}
		if ((Boolean) session.getAttribute("authenticated") == null) {
			returnError(request, response, "You are not authorized.", "/login");
			return;
		}
		if ((String) session.getAttribute("name") == null) {
			returnError(request, response, "You are not authorized.", "/login");
			return;
		}
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	private void returnError(ServletRequest request, ServletResponse response, String errorString, String toPage)
			throws ServletException, IOException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession();
		session.setAttribute("previousPage", req.getServletPath());	
		request.setAttribute("error_msg", errorString);
		request.getRequestDispatcher(toPage).forward(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// ServletContext servletContext = fConfig.getServletContext();
	}

}
