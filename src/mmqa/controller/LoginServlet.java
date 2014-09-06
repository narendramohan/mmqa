package mmqa.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mmqa.util.MySqlConnection;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get request parameters for userID and password
        // get request parameters for userID and password
        String user = request.getParameter("userName");
        String pwd = request.getParameter("userPassWord");
        Connection conn =null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
	        conn = MySqlConnection.getConnection();
	        psmt = conn.prepareStatement("SELECT username, password FROM userinfo where username=? and password=?");
	        psmt.setString(1, user);        
			psmt.setString(2, pwd);
			rs = psmt.executeQuery();
			String userName= "";
			String password = "";
			if(rs.next())
			{
				userName= rs.getString(1);
				password = rs.getString(2);
			}
			
	        if(userName.equalsIgnoreCase(user) && password.equals(pwd)){
	            HttpSession session = request.getSession();
	            session.setAttribute("user", userName);
	            //setting session to expiry in 30 mins
	            session.setMaxInactiveInterval(30*60);
	            Cookie userName1 = new Cookie("user", user);
	            userName1.setMaxAge(30*60);
	            response.addCookie(userName1);
	            response.sendRedirect("searchResults.jsp");
	        }else{
	            RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.html");
	            PrintWriter out= response.getWriter();
	            out.println("<font color=red>Either user name or password is wrong.</font>");
	            rd.include(request, response);
	        }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
				try {
					if(rs!=null) rs.close();
					if(psmt!=null) psmt.close();
					if(conn!=null) conn.close();	
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

	}

}
