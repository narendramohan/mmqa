<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
   pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*"%>  
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

  <html>
    <head>
		<meta http-equiv="content-type" content="text/html;charset=UTF-8" />
		<meta http-equiv="X-UA-Compatible" content="chrome=1">
		
		<title>MMQA</title>
		
		<style>
			body { background-color:lightblue; }
			.MatchedText { background-color:#FFFF00; }
			p { background-color:#FFFFFF; }

		    body {
		      font-size: 16px;
		      line-height: 24px;
		       bgcolor=lightblue;
		      color: #022;
		      height: 100%;
		      font-family: "Palatino Linotype", "Book Antiqua", Palatino, FreeSerif, serif;
		    }
		    div.container {
		      width: 720px;
		      margin: 50px 0 50px 50px;
		    }
		    p, li {
		      margin: 16px 0 16px 0;
		      width: 550px;
		    }
		      p.break {
		        margin-top: 35px;
		      }
		    ol {
		      padding-left: 24px;
		    }
		      ol li {
		        font-weight: bold;
		        margin-left: 0;
		      }
		    a, a:visited {
		      padding: 0 2px;
		      text-decoration: none;
		      background: #f0c095;
		      color: #252519;
		    }
		    a:active, a:hover {
		      color: #FFF;
		      background: #C25D00;
		    }
		    h1, h2, h3, h4, h5, h6 {
		      margin-top: 40px;
		    }
		    b.header {
		      font-size: 18px;
		    }
		    span.alias {
		      font-size: 14px;
		      font-style: italic;
		      margin-left: 20px;
		    }
		    table {
		      margin: 16px 0; padding: 0;
		    }
		      tr, td, th {
		        margin: 0; padding: 0;
		        text-align: left;
		      }
		        th {
		          padding: 24px 0 0;
		        }
		        tr:first-child th {
		          padding-top: 0;
		        }
		        td {
		          padding: 6px 15px 6px 0;
		        }
		          td.definition {
		            line-height: 18px;
		            font-size: 14px;
		          }
		    table.downloads td {
		      padding-left: 18px;
		    }
		    .demo-hint {
		      font-size: 13px;
		      margin: 0 0 12px 12px;
		      font-weight: normal;
		    }
		    #VS code, #VS pre, #VS tt {
		      font-family: Monaco, Consolas, "Lucida Console", monospace;
		      font-size: 12px;
		      line-height: 18px;
		      color: #444;
		      background: none;
		    }
		      #VS code {
		        margin-left: 8px;
		        padding: 0 0 0 12px;
		        font-weight: normal;
		      }
		      #VS pre {
		        font-size: 12px;
		        padding: 2px 0 2px 0;
		        border-left: 6px solid #829C37;
		        margin: 12px 0;
		      }
		    #search_query {
		      margin: 18px 0;
		      opacity: 0;
		    }
		      #search_query .raquo {
		        font-size: 18px;
		        line-height: 12px;
		        font-weight: bold;
		        margin-right: 4px;
		      }
		    #search_query2 {
		      margin: 18px 0;
		      opacity: 0;
		    }
		      #search_query2 .raquo {
		        font-size: 18px;
		        line-height: 12px;
		        font-weight: bold;
		        margin-right: 4px;
		      }
		  </style>
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>	  

  </head>
  <body>
  <h1><font color="black">Welcome to the MMQA</font></h1>

	<div align="right" style="position:absolute;top: 0px;right: 0px"><font size="5"><%=session.getAttribute("user") %></font> 
		<a style="text-decoration: none;" href="LogoutServlet"><font style="padding-left : 20px;" size="3" color="white">Logout</font></a>
	</div>
  	<script type="text/javascript" src="/path/to/jquery.plug-in.js"></script>
        <script type="text/javascript">
            $('#my-element').plugIn();
        </script>
	<%
		String searchText = (String)request.getAttribute("searchText");
		if(searchText==null) searchText="";
		List<String> checkedValue = (List<String>)request.getAttribute("type");
		if(checkedValue==null){
			checkedValue = new ArrayList<String>();
		}
	%>
   <form name="searchForm" action="doSearch" method="post">
	<table>
	<tr><td>
	<input type="text" name="searchText" width="400px" value="<%=searchText%>"/></td><td>&nbsp;<input type="submit" name="search" value="Search"></td></tr>
	<tr><td colspan="2">
		<input type="checkbox" value="all" name="type" <%= checkedValue.contains("all") ? "checked='checked'" : "" %> > All
		<input type="checkbox" value="text" name="type" <%= checkedValue.contains("text") ? "checked='checked'" : "" %> > Text
		<input type="checkbox" value="image" name="type"<%= checkedValue.contains("image") ? "checked='checked'" : "" %> > Image
		<input type="checkbox" value="video" name="type"<%= checkedValue.contains("video") ? "checked='checked'" : "" %> > Video
	</td></tr>
	</table>
</form>

   <br><br>

 <table width="100%" cellpadding="5" bordercolor="#000066" 
  bgcolor="#FFFFFF" border="0"   cellspacing="0">
	<% if(checkedValue.contains("all") || checkedValue.contains("text"))  {%>
     <c:forEach var="result" items="${resultList}">
         <%-- <tr><td><div align="center"><b> <c:out  value="${result.content}"/>
             </b></div></td> </tr> --%>
          <tr><td>
          	<div align="left"><b>Question:</b></div>
          	<div align="left"> <a href='<c:out value="${result.link}" />'><c:out value="${result.title}"  escapeXml="false"/></a>  </div>
          	
          	<div align="left"><b>Answer:</b></div>
          	<div align="left"> <c:out value="${result.text}"  escapeXml="false"/>   </div>
           </td> </tr>

        </c:forEach>
	<%} %>
	
	<% if(checkedValue.contains("all") || checkedValue.contains("image"))  { %>
	<tr><td><div align="left"><b>Image:</b></div></td> </tr>
     <c:forEach var="result" items="${imgResultList}">
          <tr><td>
          	<%-- <div align="left"> <c:out value="${result.title}"  escapeXml="false"/>  </div> --%>
            
             <div align="left"> <a href='<c:out value="${result.link}" />'><img alt="${result.title}" src="<c:out value="${result.image}"  escapeXml="false"/>">
             </a></div>
             </td> </tr>

        </c:forEach>
	<%} %>
	
	<% if(checkedValue.contains("all") || checkedValue.contains("video"))  {%>
	<tr><td><div align="left"><b>Video:</b></div></td> </tr>
     <c:forEach var="result" items="${vdoResultList}">
          <tr><td>
          	
             <div align="left"> <c:out value="${result.video}"  escapeXml="false"/></div>
             <div align="left"> <c:out value="${result.title}"  escapeXml="false"/>  </div>
             
            </td> </tr>

        </c:forEach>
	<%} %>

 </table>

</body>
 </html>