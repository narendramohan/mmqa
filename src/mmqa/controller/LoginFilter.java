package mmqa.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet Filter implementation class LoginFilter
 */
public class LoginFilter implements Filter {

    /**
     * Default constructor. 
     */
    public LoginFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		this.filterConfig = null;
		
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;
        HttpSession session = req.getSession(false);
        String uri = req.getRequestURI();
        //System.out.println("session: "+session);
        System.out.println("getRequestURI(): "+req.getRequestURI());
    	System.out.println("getRequestURL(): "+req.getRequestURL());
    	if ( uri.indexOf("/css") > 0){
            chain.doFilter(request, response);
        }
        else if( uri.indexOf("/images") > 0){
            chain.doFilter(request, response);
        }
        else if( uri.indexOf("/index.html") >= 0 || uri.indexOf("LoginServlet") >= 0 || uri.indexOf("LogoutServlet") >= 0){
            chain.doFilter(request, response);
        }
        else if( uri.indexOf("/register.html") > 0 || uri.indexOf("RegistrationServlet") >= 0){
            chain.doFilter(request, response);
       } else   if(session!=null){
	        String userName = (String) session.getAttribute("user");
	      //  System.out.println("userName: "+userName);
	        if (userName == null || "".equals(userName)) {
	        		RequestDispatcher rd = request.getServletContext().getRequestDispatcher("/index.html");
		            PrintWriter out= response.getWriter();
		            out.println("<font color=red>Please login before using the MMQA search.</font>");
		            rd.include(request, response);
	        }
	        chain.doFilter(request, response);  
       } else {
    	   System.out.println("else");
    	   RequestDispatcher rd = request.getServletContext().getRequestDispatcher("/index.html");
           PrintWriter out= response.getWriter();
           out.println("<font color=red>Please login before using the MMQA search.</font>");
           rd.include(request, response);
       }
	}
	 protected ServletContext servletContext;
	    protected FilterConfig filterConfig;
	/**
	 * @see Filter#init(FilterConfig)
	 */
	    public void init(FilterConfig filterConfig) {
	        servletContext = filterConfig.getServletContext();
	        this.filterConfig = filterConfig;
	    }

}
