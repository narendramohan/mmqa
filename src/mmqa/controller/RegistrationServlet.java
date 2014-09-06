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
 * Servlet implementation class RegistrationServlet
 */
@WebServlet("/RegistrationServlet")
public class RegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegistrationServlet() {
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
	       	String user = request.getParameter("userName");
	        String pwd = request.getParameter("userPassWord");
	        Connection conn =null;
	        PreparedStatement psmt = null;
	        ResultSet rs = null;
	        try {
		        conn = MySqlConnection.getConnection();
		        psmt = conn.prepareStatement("SELECT username, password FROM userinfo where username=?");
		        psmt.setString(1, user);        
				//psmt.setString(2, pwd);
				rs = psmt.executeQuery();
				String userName= "";
				String password = "";
				if(rs.next())
				{
					userName= rs.getString(1);
					//password = rs.getString(2);
				}
				
		        if(userName.equalsIgnoreCase(user) ){
		        	RequestDispatcher rd = getServletContext().getRequestDispatcher("/register.html");
		            PrintWriter out= response.getWriter();
		            out.println("<font color=red>Either user name or password is wrong.</font>");
		            rd.include(request, response);
		        } else{
		        	psmt.close();
		        	psmt = conn.prepareStatement("INSERT INTO userinfo (username, password) values (?, ?)");
			        psmt.setString(1, user);        
					psmt.setString(2, pwd);
					psmt.executeUpdate();
					conn.commit();
		            RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.html");
		            PrintWriter out= response.getWriter();
		            out.println("<font color=red>User registration succeeded.</font>");
		            rd.include(request, response);
		        }
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
					
					try {
						if(rs!=null) rs.close();
						if(psmt!=null) psmt.close();
						if(conn!=null) { conn.rollback(); conn.close();	}
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}		
	}

}
