/*
 	Copyright 2016-2017 Zuercher Hochschule fuer Angewandte Wissenschaften
 	All Rights Reserved.

   Licensed under the Apache License, Version 2.0 (the "License"); you may
   not use this file except in compliance with the License. You may obtain
   a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
   WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
   License for the specific language governing permissions and limitations
   under the License.
 */

package ch.zhaw.init.walj.projectmanagement.admin.properties;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.zhaw.init.walj.projectmanagement.util.DBConnection;
import ch.zhaw.init.walj.projectmanagement.util.HTMLFooter;
import ch.zhaw.init.walj.projectmanagement.util.HTMLHeader;
import ch.zhaw.init.walj.projectmanagement.util.dbclasses.Project;

/**
 * Projectmanagement tool, Page to delete projects
 * 
 * @author Janine Walther, ZHAW
 * 
 */
@SuppressWarnings("serial")
@WebServlet("/admin/deleteProject")
public class DeleteProject extends HttpServlet {

    /*
     *	method to handle get requests
     *	deletes the project and shows success/error message to the user
     */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        DBConnection con = new DBConnection(this.getServletContext().getRealPath("/"));
		
		// prepare response
		response.setContentType("text/html;charset=UTF8");
		PrintWriter out = response.getWriter();

		// get user and project ID
		int projectID = Integer.parseInt(request.getParameter("projectID"));

		// get project
		Project project;
		try {
			project = con.getProject(projectID);
		} catch (SQLException e) {
			String url = request.getContextPath() + "/ProjectNotFound";
            response.sendRedirect(url);
            return;
		}

		String message;
		try {
			// set archive flag for project in database
			con.deleteProject(projectID);
			// success message
			message = "<div class=\"row\">"
					+ "<div class=\"small-12 columns align-center\">" 
				    + "<h2>The Project " + project.getName() + " has sucessfully been deleted.</h2>"
					+ "<a href=\"/admin/properties\">"
					+ "<i class=\"fa fa-chevron-left fa-4x\" aria-hidden=\"true\"></i></br>"
					+ "Click here to go back to the properties page</a>"
					+ "</div>"
					+ "</div>";
		} catch (SQLException e) {
			// error message
			message =  "<div class=\"row\">"
					+ "<div class=\"small-12 columns align-center\">" 
				    + "<h2>The Project " + project.getName() + " could not be deleted.</h2>"
					+ "<a href=\"/admin/properties\">"
					+ "<i class=\"fa fa-chevron-left fa-4x\" aria-hidden=\"true\"></i></br>"
					+ "Click here to go back to the properties page</a>"
					+ "</div>"
					+ "</div>";			
		}

		// Print HTML
		out.println(HTMLHeader.getInstance().printHeader("Delete Project", "../", "Delete Project", "", "", true)
				  + "<section>"
				  + message
				  + "</section>"
				  + HTMLFooter.getInstance().printFooter(false)
				  + "</div>"
				  + "<script src=\"../js/vendor/jquery.js\"></script>"
				  + "<script src=\"../js/vendor/foundation.min.js\"></script>"
				  + "<script>$(document).foundation();</script>"
				  + "</body>"
				  + "</html>");
		
	}
}
