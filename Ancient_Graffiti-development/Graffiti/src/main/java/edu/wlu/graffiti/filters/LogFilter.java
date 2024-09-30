package edu.wlu.graffiti.filters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.*;
import org.springframework.core.annotation.Order;

import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;

/**
 * Filter for logging HTTP requests.
 * 
 * Updated for Apache Tomcat 9. (not yet tested for multipart files)
 * 
 */
@Order(1)
public class LogFilter implements Filter {

	// the maximum size of an uploaded file as a string
	private String stringFileSize;
	// the maximum size of an uploaded file as a integer
	// default is 100M
	private int intFileSize = 1024 * 1024 * 100;
	// the directory to save the log file to
	private String saveDir;
	// the logger
	private Logger log;
	// the format of the date and time
	private SimpleDateFormat sdf = new SimpleDateFormat();
	// the last time that the log file was rolled over

	private static String ENCODING = "UTF-8";

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig filterConfig) throws ServletException {

		// get the maximum file size from the web.xml file
		// and convert the size in the web.xml file to an integer
		if ((stringFileSize = filterConfig.getInitParameter("UploadFileLogLimit")) != null)
			intFileSize = 1024 * 1024 * Integer.parseInt(stringFileSize);

		// get a logger for this class
		log = LogManager.getLogger("LogFilter");
		// set the level of the logger
		log.atLevel(Level.INFO);

	}// end method init

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		log = null;
		stringFileSize = null;
		saveDir = null;
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest origRequest, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		if (origRequest.getCharacterEncoding() == null) {
			origRequest.setCharacterEncoding(ENCODING);
		}
		
		HttpServletRequest request = (HttpServletRequest) origRequest;

		// get the date and time that the request was received
		long currentTime = System.currentTimeMillis();

		// the information to be logged
		StringBuffer logInfo = new StringBuffer();

		// get the IP address of the request
		String ipAddress = request.getRemoteAddr();

		// add the IP address to the buffer
		logInfo.append(ipAddress + " ");

		// convert the time the request object was received into a string
		sdf.applyLocalizedPattern("dd/MMM/yyyy:HH:mm:ss");
		String timeReceived = "[" + sdf.format(new Date(currentTime)) + "]";

		// add the date and time that the request was received to the buffer
		logInfo.append(timeReceived + " ");

		// get the HTTP method of the request
		String method = request.getMethod();

		// add the method to the buffer
		logInfo.append(method + " ");

		// get the URL from the request
		String uri = request.getRequestURI();

		if (method.equals("GET")) {

			String query = request.getQueryString();

			if (query != null)
				uri += "?" + query;

		} // end if

		// add the URL to the buffer
		logInfo.append(uri + " ");

		// get the names of the parameters from the request
		Enumeration<String> paramNames = request.getParameterNames();

		// if the method type is POST, encode the parameters
		if (method.equals("POST")) {

			logInfo.append("--post-data=\"");

			if (paramNames.hasMoreElements())
				encodePostParameters(logInfo, paramNames, request);

			logInfo.append("\"] ");

		} // end if

		// get the cookies from the request
		Cookie[] cookies = request.getCookies();

		// if there is at least one cookie
		if (cookies != null && cookies.length > 0)
			encodeCookies(logInfo, cookies);

		logInfo.append("] ");

		// get the referer from the request
		String referer = request.getHeader("Referer");

		// add the referer to the buffer
		if (referer != null)
			logInfo.append(referer + " ");

		// get the content type of the request
		String contentType = request.getContentType();

		// prevents caching of static pages
		((HttpServletResponse) response).setHeader("Cache-Control", "max-age=0");

		// if the request contains a file
		if ((contentType != null) && (contentType.contains("multipart/form-data"))) {

			// wrap the request to save the input stream
			// FilterRequestWrapper wrapper = new FilterRequestWrapper(request);

			try {

				// get the input stream from the request
				ServletInputStream stream = request.getInputStream();

				// mark the start of the stream
				stream.mark(intFileSize + 1);

				// parse the wrapper request
				MultipartParser parser = new MultipartParser(request, intFileSize);
				Part part;

				// get the date and time for the directory
				sdf.applyLocalizedPattern("dd.MMM.yyyy.HH.mm.ss");
				String dirTime = sdf.format(new Date(currentTime));

				// create the directory name to save the uploaded file in
				String directoryName = ipAddress + "." + dirTime;

				// create the complete directory name
				String compDirName = saveDir + "/" + directoryName;

				// create the directory
				new File(compDirName).mkdir();

				// a buffer to hold the upload parameters
				StringBuffer params = new StringBuffer("--params=\"");

				// get the parts of the parsed request
				while ((part = parser.readNextPart()) != null) {

					// if the part is a file
					if (part.isFile()) {

						// cast the part to a FilePart object
						FilePart filePart = (FilePart) part;

						// get the name used for the file on the upload page
						String formName = filePart.getName();

						// get the name of the file
						String uploadedFileName = filePart.getFileName();

						String dirNfile = null;

						if (uploadedFileName != null) {

							dirNfile = compDirName + "/" + uploadedFileName;

							// create and write to a FileOutputStream
							FileOutputStream fileOut = new FileOutputStream(dirNfile);
							filePart.writeTo(fileOut);
							fileOut.flush();
							fileOut.close();

							uploadedFileName = uploadedFileName.replaceAll("'", "\\'");

							uploadedFileName = URLEncoder.encode(uploadedFileName, "UTF-8");

						} // end if
						else {
							dirNfile = "";
						} // end else

						logInfo.append("--file=\"&" + formName + "=" + dirNfile + "\" ");

					} // end if
					else {

						// cast the part to a ParamPart object
						ParamPart parPart = (ParamPart) part;

						// get the name of the part
						String paramName = URLEncoder.encode(parPart.getName(), "UTF-8");

						// get the value of the part
						String paramValue = URLEncoder.encode(parPart.getStringValue(), "UTF-8");

						// add the parameter pair to the list
						params.append("&" + paramName + "=" + paramValue);

					} // end else

				} // end while

				params.append("\"");

				// log parameters
				logInfo.append(params);

				// reset the input stream of the wrapped request
				stream.reset();

			} // end try
			catch (Exception e) {

				e.printStackTrace();

			} // end catch

			// log the buffer
			log.info(logInfo);

			// pass the wrapped request down the filter chain
			chain.doFilter(request, response);

		} // end if
		else {

			// log the buffer
			log.info(logInfo);

			// pass the request down the filter chain
			chain.doFilter(request, response);

		} // end else

	}

	private void encodeCookies(StringBuffer logInfo, Cookie[] cookies) {
		for (Cookie cookie : cookies) {
			String name = cookie.getName();
			String value = cookie.getValue();
			logInfo.append("--header=\"Cookie:" + name + "=" + value + "\"");

		} // end for

	}// end method encode Cookies

	private void encodePostParameters(StringBuffer logInfo, Enumeration<String> paramNames, ServletRequest request) {

		while (paramNames.hasMoreElements()) {

			String name = (String) paramNames.nextElement();
			String values[] = request.getParameterValues(name);

			String name_encoded = "";

			try {

				name_encoded = URLEncoder.encode(name, "UTF-8");

			} // end try
			catch (java.io.UnsupportedEncodingException e) {

				e.printStackTrace();

			} // end catch

			for (int i = 0; i < values.length; i++) {

				String encodedValue = "";

				logInfo.append("&" + name_encoded + "=");

				try {

					encodedValue = URLEncoder.encode(values[i], "UTF-8");

				} // end try
				catch (java.io.UnsupportedEncodingException e) {

					e.printStackTrace();

				} // end catch

				logInfo.append(encodedValue);

			} // end for

		} // end while

	}// end method encodePostParameters

}
