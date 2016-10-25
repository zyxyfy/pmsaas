package ch.zhaw.init.walj.projectmanagement;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.zhaw.init.walj.projectmanagement.chart.PieChart;
import ch.zhaw.init.walj.projectmanagement.util.DBConnection;
import ch.zhaw.init.walj.projectmanagement.util.DateFormatter;
import ch.zhaw.init.walj.projectmanagement.util.Employee;
import ch.zhaw.init.walj.projectmanagement.util.NumberFormatter;
import ch.zhaw.init.walj.projectmanagement.util.Project;

/**
 * Servlet implementation class Overview
 */
@SuppressWarnings("serial")
@WebServlet("/Projects/Overview")
public class Overview extends HttpServlet {
	
	private PieChart piechart;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF8");
		
		ArrayList<Project> projects = new ArrayList<Project>();
		
		int id = (int) request.getSession(false).getAttribute("ID");
		String name = (String) request.getSession(false).getAttribute("user");
				
		DBConnection con = new DBConnection();
		
		ArrayList<Employee> employeeList = null;
		try {
			employeeList = con.getAllEmployees(id);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
				
		PrintWriter out = response.getWriter();
		
		out.println("<!DOCTYPE html>" 
				  + "<html>"
				  // HTML head
				  + "<head>" 
				  + "<meta charset=\"UTF-8\">"
				  + "<title>Projects</title>" 
				  + "<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/foundation.css\" />"
				  + "<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/style.css\" />" 
				  + "<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/font-awesome/css/font-awesome.min.css\" />" 
				  // function to redirect after click on select button
				  + "<script>function Redirect(ID) {var url = \"Overview/Project?id=\" + ID; window.location=url;}</script>"
				  + "</head>" 
				  + "<body>"
				  + "<div id=\"wrapper\">" 
				  + "<header>" 
				  + "<div class=\"row\">" 
				  + "<div class=\"small-8 columns\">"
				  + "<img src=\"../img/logo_small.png\" class=\"small-img left\">"
				  // title
				  + "<h1> Projects</h1>"
				  + "</div>"
				  // menu
				  + "<div class=\"small-12 medium-4 columns\">" 
				  + "<div class=\"float-right menu\">"
				  + "<a href=\"/Projektverwaltung/Projects/Overview\" class=\"button\" title=\"All Projects\"><i class=\"fa fa-list fa-fw\"></i></a> "
				  + "<a href=\"/Projektverwaltung/Projects/newProject\" class=\"button\" title=\"New Project\"><i class=\"fa fa-file fa-fw\"></i></a> "
				  + "<a href=\"/Projektverwaltung/Projects/newEmployee\" class=\"button\" title=\"New Employee\"><i class=\"fa fa-user-plus fa-fw\"></i></a> "
				  + "<a href=\"/Projektverwaltung/Projects/employee\" class=\"button\" title=\"My Profile\"><i class=\"fa fa-user fa-fw\"></i></a> "
				  + "<a href=\"/Projektverwaltung/Projects/help\" class=\"button\" title=\"Help\"><i class=\"fa fa-book fa-fw\"></i></a> "
				  + "<a href=\"/Projektverwaltung/Projects/logout\" class=\"button\" title=\"Logout\"><i class=\"fa fa-sign-out fa-fw\"></i></a> "
				  + "</div>" 
				  + "</div>"
				  + "</div>" 
				  + "</header>" 
				  // HTML section with list of all projects
				  + "<section>" 
				  + "<div class=\"row\">"
				  + "<h2>Welcome " + name + "</h2>"
				  + "</div>" 
				  + "<div class=\"row\">"
				  + "<h3>Your Projects</h3>"
				  + "</div>"
				  + "<div class=\"row\">"); 
		
		try {
			// get all projects where the current user is supervisor
			projects = con.getProjects(id, false);
			if (projects != null){
				out.println("<ul class=\"accordion\" data-accordion data-multi-expand=\"true\" data-allow-all-closed=\"true\">");
				// write project information
				for (Project project : projects) {
					
					// get all employees
					ArrayList <Employee> employee = project.getEmployees();
					
					String employees = "";
					
					// string of all employee names
					if (!employee.isEmpty()){
						employees = employee.get(0).getName();
						for (int i = 1; i < employee.size(); i++){
							employees += ", " + employee.get(i).getName();
						}
					} else {
						employees = "no employees assigned";
					}
					
					// get number of days left to end of project
					Date date = new Date();
					String daysUntilEnd = "";
					int days = DateFormatter.getInstance().getDaysBetween(date, project.getEnd());
					if (days == 0){
						daysUntilEnd = "Project finished";
					} else {
						daysUntilEnd = "Project ends in " + days + " days";
					}
					piechart = new PieChart(project);

					
					// print list item
					out.println("<li class=\"accordion-item\" data-accordion-item><a href=\"#\" class=\"accordion-title\">"
							  + "<span class=\"bigtext small-3 columns down\">" + project.getShortname() + "</span>"
							  + "<span class=\"middletext small-6 columns down\">" + project.getName() + "</span>"
							  + "<button class=\"button small-2 columns end down smalltext\" onclick=\"Redirect(" + project.getID() + ");\">Select</button>"
							// TODO  + "<span class=\"success badge errorbadge\">0</span>"
							  + "</a>"
							  + "<div class=\"accordion-content\" data-tab-content>"
							  // Write Duration
							  +"<p>"
							  + "<span class=\"small-3 columns\">Projectduration</span>"
							  + "<span class=\"small-4 columns\">" 
							  + DateFormatter.getInstance().formatDate(project.getStart()) + " - " + DateFormatter.getInstance().formatDate(project.getEnd()) + "</span>"
					  		  + "<span class=\"small-4 end columns align-right\">" + daysUntilEnd + "</span>"
			  		  		  + "</p>"
			  		  		  // Write Budget
			  		  		  + "<p>"
			  		  		  + "<span class=\"small-3 columns\">Budget</span>"
			  		  		  + "<span class=\"small-4 columns\">" + project.getCurrency() 
							  + " " + NumberFormatter.getInstance().formatDouble(project.getBudget()) + "</span>"
			  		  		  + "<span class=\"small-4 end columns align-right\">" + project.getCurrency()
			  		  		  + " " + NumberFormatter.getInstance().formatDouble(piechart.getRemainingBudget()) + " left</span>"
			  		  		  + "</p>"
							  // Write Workpackages
							  + "<p>"
							  + "<span class=\"small-3 columns\">Workpackages</span>"
							  + "<span class=\"small-4 columns\">" + project.nbrOfWorkpackages() + "</span>"
							  + "<span class=\"small-4 end columns align-right\">"
							  + "-"
							  + "</span>"
							  + "</p>"
							  // Write Tasks
							  + "<p>"
							  + "<span class=\"small-3 columns\">Tasks</span>"
							  + "<span class=\"small-4 columns\">" + project.nbrOfTasks() + "</span>"
							  + "<span class=\"small-4 end columns align-right\">" 
							  + "-"
							  + "</span>"
							  + "</p>"
							  // Write Employees
							  + "<p>"
							  + "<span class=\"small-3 columns\">Employees</span>"
							  + "<span class=\"small-4 columns\">" + project.nbrOfEmployees() 
							  + "</span><span class=\"small-4 end columns align-right\">" + employees
							  + "</span></p>"
							  // Write Partners
							  + "<p><span class=\"small-3 columns\">Partner</span>"
							  + "<span class=\"small-4 end columns\">" + project.getPartners() + "</span>"
							  + "</p>"
							  + "</div>"
							  + "</li>");
					
					
				}
				out.println("</ul>");
			} else {
				out.println("<div class=\"callout warning\">"
						  + "<h4>No projects found</h4>"
						  + "<a href=\"/Projektverwaltung/Projects/newProject\">Click here to create a new project</a>"
						  + "</div>");				
			}
		
			out.println("</div>");
			
			if (employeeList.size() > 1) {
							
				projects = new ArrayList<Project>();
				for (Employee e : employeeList){
					if (e.getID() != id){
						// get all projects where the current user is supervisor
						if (con.getProjects(e.getID(), false) != null){
							projects.addAll(con.getProjects(e.getID(), false));
						}
					}
				}
				if (projects != null){

					out.println("<div class=\"row\">"
				     	  + "<h3>Other Projects</h3>"
					      + "</div>"
				     	  + "<div class=\"row\">"
					      + "<ul class=\"accordion\" data-accordion data-multi-expand=\"true\" data-allow-all-closed=\"true\">");
					for (Project project : projects){
						// get all employees
						ArrayList <Employee> employee = project.getEmployees();
						
						Employee supervisor = con.getEmployee(project.getLeader());
						
						String employees = "";
						
						// string of all employee names
						if (!employee.isEmpty()){
							employees = employee.get(0).getName();
							for (int i = 1; i < employee.size(); i++){
								employees += ", " + employee.get(i).getName();
							}
						} else {
							employees = "no employees assigned";
						}
						
						// get number of days left to end of project
						Date date = new Date();
						String daysUntilEnd = "";
						int days = DateFormatter.getInstance().getDaysBetween(date, project.getEnd());
						if (days == 0){
							daysUntilEnd = "Project finished";
						} else {
							daysUntilEnd = "Project ends in " + days + " days";
						}
				
						piechart = new PieChart(project);
						
						
						// print list item
						out.println("<li class=\"accordion-item\" data-accordion-item><a href=\"#\" class=\"accordion-title\">"
								  + "<span class=\"bigtext small-3 columns down\">" + project.getShortname() + "</span>"
								  + "<span class=\"middletext small-6 columns end down\">" + project.getName() + "</span>"
								 // TODO + "<span class=\"success badge errorbadge\">0</span>"
								  + "</a>"
								  + "<div class=\"accordion-content\" data-tab-content>"
								  // Write ProjectLeader
								  + "<p><span class=\"small-3 columns\">Project Leader</span>"
								  + "<span class=\"small-9 end columns\">" + supervisor.getName() + "</span>"
								  + "</p>"
								  // Write Duration
								  + "<p>"
								  + "<span class=\"small-3 columns\">Projectduration</span>"
								  + "<span class=\"small-4 columns\">" 
								  + DateFormatter.getInstance().formatDate(project.getStart()) + " - " + DateFormatter.getInstance().formatDate(project.getEnd()) + "</span>"
						  		  + "<span class=\"small-4 end columns align-right\">" + daysUntilEnd + "</span>"
				  		  		  + "</p>"
				  		  		  // Write Budget
				  		  		  + "<p>"
				  		  		  + "<span class=\"small-3 columns\">Budget</span>"
				  		  		  + "<span class=\"small-4 columns\">" + project.getCurrency() 
								  + " " + NumberFormatter.getInstance().formatDouble(project.getBudget()) + "</span>"
				  		  		  + "<span class=\"small-4 end columns align-right\">" + project.getCurrency()
				  		  		  + " " + NumberFormatter.getInstance().formatDouble(piechart.getRemainingBudget()) + " left</span>"
				  		  		  + "</p>"
								  // Write Workpackages
								  + "<p>"
								  + "<span class=\"small-3 columns\">Workpackages</span>"
								  + "<span class=\"small-9 columns\">" + project.nbrOfWorkpackages() + "</span>"
								  + "</p>"
								  // Write Tasks
								  + "<p>"
								  + "<span class=\"small-3 columns\">Tasks</span>"
								  + "<span class=\"small-9 columns\">" + project.nbrOfTasks() + "</span>"
								  + "</p>"
								  // Write Employees
								  + "<p>"
								  + "<span class=\"small-3 columns\">Employees</span>"
								  + "<span class=\"small-4 columns\">" + project.nbrOfEmployees() 
								  + "</span><span class=\"small-4 end columns align-right\">" + employees
								  + "</span></p>"
								  // Write Partners
								  + "<p><span class=\"small-3 columns\">Partner</span>"
								  + "<span class=\"small-9 end columns\">" + project.getPartners() + "</span>"
								  + "</p>"
								  + "</div>"
								  + "</li>");
					}					
				}				
			}	

			out.println("</div>");
			
			
			projects = con.getProjects(id, true);
			if (projects != null){
				out.println("<div class=\"row\">"
				     	  + "<h3>Archived Projects</h3>"
					      + "</div>"
				     	  + "<div class=\"row\">"
					      + "<ul class=\"accordion\" data-accordion data-multi-expand=\"true\" data-allow-all-closed=\"true\">");
				// write project information
				for (Project project : projects) {
					
					// get all employees
					ArrayList <Employee> employee = project.getEmployees();
					
					String employees = "";
					
					// string of all employee names
					if (!employee.isEmpty()){
						employees = employee.get(0).getName();
						for (int i = 1; i < employee.size(); i++){
							employees += ", " + employee.get(i).getName();
						}
					} else {
						employees = "no employees assigned";
					}
																		
					// print list item
					out.println("<li class=\"accordion-item\" data-accordion-item>"
							  + "<a href=\"#\" class=\"accordion-title\">"
							  + "<span class=\"bigtext small-3 columns down\">" + project.getShortname() + "</span>"
							  + "<span class=\"middletext small-6 columns end down\">" + project.getName() + "</span>"
							  + "</a>"
							  + "<div class=\"accordion-content\" data-tab-content>"
							  // Write Duration
							  +"<p>"
							  + "<span class=\"small-3 columns\">Projectduration</span>"
							  + "<span class=\"small-9 end columns\">" 
							  + DateFormatter.getInstance().formatDate(project.getStart()) + " - " + DateFormatter.getInstance().formatDate(project.getEnd()) + "</span>"
			  		  		  + "</p>"
			  		  		  // Write Budget
			  		  		  + "<p>"
			  		  		  + "<span class=\"small-3 columns\">Budget</span>"
			  		  		  + "<span class=\"small-9 columns\">" + project.getCurrency() 
							  + " " + NumberFormatter.getInstance().formatDouble(project.getBudget()) + "</span>"
			  		  		  + "</p>"
							  // Write Workpackages
							  + "<p>"
							  + "<span class=\"small-3 columns\">Workpackages</span>"
							  + "<span class=\"small-9 columns\">" + project.nbrOfWorkpackages() + "</span>"
							  + "</p>"
							  // Write Tasks
							  + "<p>"
							  + "<span class=\"small-3 columns\">Tasks</span>"
							  + "<span class=\"small-9 columns\">" + project.nbrOfTasks() + "</span>"
							  + "</p>"
							  // Write Employees
							  + "<p>"
							  + "<span class=\"small-3 columns\">Employees</span>"
							  + "</span><span class=\"small-9 end columns\">" + employees
							  + "</span></p>"
							  // Write Partners
							  + "<p><span class=\"small-3 columns\">Partner</span>"
							  + "<span class=\"small-9 end columns\">" + project.getPartners() + "</span>"
							  + "</p>"
							  + "</div>"
							  + "</li>");
				}
				out.println("</ul>");
			}
			
		} catch (SQLException e) {
		}
		out.println("</section>"
				  + "</div>"
				  + "<script src=\"../js/vendor/jquery.js\"></script>"
				  + "<script src=\"../js/vendor/foundation.min.js\"></script>"
				  + "<script>$(document).foundation();</script>"
				  + "</body>"
				  + "</html>");
	}
	
}
