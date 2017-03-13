/**
 *	Copyright 2016-2017 Zuercher Hochschule fuer Angewandte Wissenschaften
 *	All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  not use this file except in compliance with the License. You may obtain
 *  a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 */

package ch.zhaw.init.walj.projectmanagement.delete;

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
 * Projectmanagement tool, Page to delete workpackages
 * 
 * @author Janine Walther, ZHAW
 * 
 */
@SuppressWarnings("serial")
@WebServlet("/Projects/deleteWorkpackage")
public class DeleteWorkpackage extends HttpServlet {

	// connection to database
	private DBConnection con;

	@Override
	// method to handle get-requests
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		con = new DBConnection(this.getServletContext().getRealPath("/"));
		
		// prepare response
		response.setContentType("text/html;charset=UTF8");
		PrintWriter out = response.getWriter();

		// get user, project and workpackage ID
		int workpackageID = Integer.parseInt(request.getParameter("workpackageID"));
		int projectID = Integer.parseInt(request.getParameter("projectID"));
		int userID =  (int) request.getSession(false).getAttribute("ID");
		
		// get project
		Project project = null;
		try {
			project = con.getProject(projectID);
		} catch (SQLException e) {
			String url = request.getContextPath() + "/ProjectNotFound";
            response.sendRedirect(url);
            return;
		}

		// check if user is project leader
		if (project.getLeader() == userID){
					
			String message = "";
	
			// delete project from database
			try {
				con.deleteWorkpackage(workpackageID);
				// success message
				message = "<div class=\"row\">"
						+ "<div class=\"small-12 columns align-center\">" 
					    + "<h2>The workpackage has sucessfully been deleted.</h2>"
						+ "<a href=\"/Projektverwaltung/Projects/Edit?projectID=" + projectID + "#workpackages\">Click here to go back to the edit page</a>"
						+ "<br>"
						+ "<a href=\"/Projektverwaltung/Projects/Overview/Project?id=" + projectID + "\">Click here to go to the project overview</a>"
						+ "</div>"
						+ "</div>";
			} catch (SQLException e) {
				// error message
				message = "<div class=\"row\">"
						+ "<div class=\"small-12 columns align-center\">" 
					    + "<h2>The workpackage could not be deleted</h2>"
						+ "<a href=\"/Projektverwaltung/Projects/Edit?projectID=" + projectID + "#workpackages\">Click here to go back to the edit page</a>"
						+ "<br>"
						+ "<a href=\"/Projektverwaltung/Projects/Overview/Project?id=" + projectID + "\">Click here to go to the project overview</a>"
						+ "</div>"
						+ "</div>";			
			}
			
	
			// Print HTML
			out.println(HTMLHeader.getInstance().printHeader("Delete Workpackage", "../", "Delete Workpackage", "")
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
		} else {
			String url = request.getContextPath() + "/AccessDenied";
	        response.sendRedirect(url);
		}
		
	}
}
