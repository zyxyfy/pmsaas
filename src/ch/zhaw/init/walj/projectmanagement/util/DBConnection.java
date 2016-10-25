package ch.zhaw.init.walj.projectmanagement.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;



/**
 * creates a connection to the database
 * 
 * @author Janine Walther, ZHAW
 *
 */
public class DBConnection {

	private String driver = DataBaseAccess.getInstance().getDriver();
	private String url = DataBaseAccess.getInstance().getURL();
	private String dbName = DataBaseAccess.getInstance().getDBName();
	private String userName	= DataBaseAccess.getInstance().getUsername();
	private String password	= DataBaseAccess.getInstance().getPassword();
	private Connection conn;
	private PreparedStatement st;
	private ResultSet res;

	/**
	 * Constructor to the DBConnection class
	 * creates a connection to a database
	 * 
	 * @param url
	 *            URL to the database
	 * @param dbName
	 *            name of the database
	 * @param userName
	 * 			  username for login
	 * @param password
	 * 			  password for login
	 */
	public DBConnection() {
		// get connection to database
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(this.url + this.dbName, this.userName, this.password);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * creates a new Project object and returns it to the user
	 * 
	 * @param pID
	 *            id of the project
	 * 
	 * @return project with data from the database
	 * @throws SQLException
	 */
	public Project getProject(int pID) throws SQLException {
	
		// variable declaration
		PreparedStatement stProject = conn.prepareStatement("SELECT * FROM  Projects where ProjectIDFS= ?");
		PreparedStatement stWP = conn.prepareStatement("SELECT * FROM  Workpackages where ProjectIDFS= ?");
		PreparedStatement stTask = conn.prepareStatement("SELECT * FROM  Tasks where WorkpackageIDFS= ?");
		PreparedStatement stEmployee = conn.prepareStatement("SELECT Employees.* FROM Employees INNER JOIN Assignments ON Employees.EmployeeID = Assignments.EmployeeIDFS WHERE Assignments.TaskIDFS = ?");
		PreparedStatement stWage = conn.prepareStatement("SELECT WagePerHour FROM  Wage where EmployeeIDFS= ? order by ValidFrom desc");
		PreparedStatement stExpenses = conn.prepareStatement("Select * from Expenses where ProjectIDFS= ?");
	
		ResultSet resProject;
		ResultSet resWP;
		ResultSet resTask;
		ResultSet resEmployee;
		ResultSet resWage;
		ResultSet resExpenses;
	
		// variables for data from project table
		Project project;
		String pName;
		String pShortname;
		int pLeader;
		String pStart;
		String pEnd;
		String pCurrency;
		double pBudget;
		String pPartner;
	
		// variables for data from workpackage table
		int wID;
		int wProjectID;
		String wName;
		String wStart;
		String wEnd;
	
		// variables for data from task table
		int tID;
		int tWorkpackageID;
		String tName;
		String tStart;
		String tEnd;
		int tPMs;
		double tBudget;
	
		// variables for data from empoloyee table
		int eID;
		String eFirstname;
		String eLastname;
		String eKuerzel;
		String eMail;
		String ePassword;
		int eWage;
		int eSupervisor;
	
		// variables for data from expense table
		Expense expense;
		int exID;
		int exProjextID;
		int exEmployeeID;
		double exCosts;
		String exType;
		String exDescription;
		String exDate;
	
		// get the project information from the database
		stProject.setInt(1, pID);
		resProject = stProject.executeQuery();
	
		resProject.next();
		pName = resProject.getString("ProjectName");
		pShortname = resProject.getString("ProjectShortname");
		pLeader = resProject.getInt("ProjectLeader");
		pStart = DateFormatter.getInstance().formatDate(resProject.getString("ProjectStart"));
		pEnd = DateFormatter.getInstance().formatDate(resProject.getString("ProjectEnd"));
		pCurrency = resProject.getString("Currency");
		pBudget = resProject.getDouble("TotalBudget");
		pPartner = resProject.getString("Partner");
	
		project = new Project(pID, pName, pShortname, pLeader, pStart, pEnd, pCurrency, pBudget, pPartner);
	
		// get all expenses
		stExpenses.setInt(1, pID);
		resExpenses = stExpenses.executeQuery();
	
		while (resExpenses.next()) {
	
			exID = resExpenses.getInt("ExpenseID");
			exProjextID = resExpenses.getInt("ProjectIDFS");
			exEmployeeID = resExpenses.getInt("EmployeeIDFS");
			exCosts = resExpenses.getDouble("Costs");
			exType = resExpenses.getString("Type");
			exDescription = resExpenses.getString("Description");
			exDate = resExpenses.getString("Date");
	
			expense = new Expense(exID, exProjextID, exEmployeeID, exCosts, exType, exDescription, exDate);
			project.addExpense(expense);
		}
	
		// get all workpackages
		stWP.setInt(1, pID);
		resWP = stWP.executeQuery();
		while (resWP.next()) {
			wID = resWP.getInt("WorkpackageID");
			wProjectID = resWP.getInt("ProjectIDFS");
			wName = resWP.getString("WPName");
			wStart = DateFormatter.getInstance().formatDate(resWP.getString("WPStart"));
			wEnd = DateFormatter.getInstance().formatDate(resWP.getString("WPEnd"));
	
			Workpackage wp = new Workpackage(wID, wProjectID, wName, wStart, wEnd);
	
			// get all tasks
			stTask.setInt(1, wID);
			resTask = stTask.executeQuery();
			while (resTask.next()) {
				tID = resTask.getInt("TaskID");
				tWorkpackageID = resTask.getInt("WorkpackageIDFS");
				tName = resTask.getString("TaskName");
				tStart = DateFormatter.getInstance().formatDate(resTask.getString("TaskStart"));
				tEnd = DateFormatter.getInstance().formatDate(resTask.getString("TaskEnd"));
				tPMs = resTask.getInt("PMs");
				tBudget = resTask.getDouble("Budget");
	
				ProjectTask task = new ProjectTask(tID, tWorkpackageID, tName, tStart, pStart, tEnd, tPMs, tBudget);
	
				// get all employees that are assigned to at least one of the tasks
				stEmployee.setInt(1, tID);
				resEmployee = stEmployee.executeQuery();
	
				while (resEmployee.next()) {
	
					eID = resEmployee.getInt("EmployeeID");
					eFirstname = resEmployee.getString("Firstname");
					eLastname = resEmployee.getString("Lastname");
					eKuerzel = resEmployee.getString("Kuerzel");
					eMail = resEmployee.getString("Mail");
					ePassword = resEmployee.getString("Password");
					eSupervisor = resEmployee.getInt("Supervisor");
	
					// get the wage of the employee
					stWage.setInt(1, eID);
					resWage = stWage.executeQuery();
					resWage.next();
					eWage = resWage.getInt("WagePerHour");
	
					Employee employee = new Employee(eID, eFirstname, eLastname, eKuerzel, eMail, ePassword, eWage, eSupervisor);
					
					// add employee to the task
					task.addEmployee(employee);
				}
	
				// add the task and his employees to the workpackage
				wp.addTask(task);
				wp.addEmployees();
			}
	
			// add the workpackages with his tasks and the employees to the project
			project.addWorkpackage(wp);
			project.addEmployees();
		}
	
		return project;
	}

	/**
	 * returns all projects from the current user as project leader
	 * 
	 * @param id
	 *            id of the current user
	 * 
	 * @return all projects with current user as project leader
	 * @throws SQLException 
	 */
	public ArrayList<Project> getProjects(int id, boolean archive) throws SQLException {
		ArrayList<Project> projects = new ArrayList<Project>();
		
		if (archive){
			st = conn.prepareStatement("SELECT * FROM  Projects where ProjectLeader=? and Archive=1");
			st.setInt(1, id);
			res = st.executeQuery();
		} else {
			st = conn.prepareStatement("SELECT * FROM  Projects where ProjectLeader=? and Archive=0");
			st.setInt(1, id);
			res = st.executeQuery();			
		}
		
		while (res.next()){
			projects.add(getProject(res.getInt("ProjectIDFS")));
		}
		if (projects.isEmpty()){
			return null;
		}
		return projects;
	}

	/**
	 * returns all workpackages from the current project
	 * 
	 * @param id
	 *            id of the current project
	 * 
	 * @return all workpackages from the current project
	 */
	public ResultSet getWorkpackages(int id) {
		try {
			st = conn.prepareStatement("SELECT * FROM  Workpackages where ProjectIDFS=?");
			st.setInt(1, id);
			res = st.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * returns all tasks from the current workpackage
	 * 
	 * @param id
	 *            id of the current workpackage
	 * 
	 * @return all tasks from the current workpackage
	 */
	public ResultSet getTasks(int id) {
		try {
			st = conn.prepareStatement("SELECT * FROM Tasks where WorkpackageIDFS=?");
			st.setInt(1, id);
			res = st.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * returns the employee with the given ID as an Employee object
	 * 
	 * @param id employee ID
	 * @return a new Employee object
	 */
	public Employee getEmployee(int id) {
		Employee employee = null;
		
		try {
	
			// get the employees
			st = conn.prepareStatement("SELECT * from Employees WHERE EmployeeID=?");
			st.setInt(1, id);
			res = st.executeQuery();
	
			res.next();
			
			String firstname = res.getString("Firstname");
			String lastname = res.getString("Lastname");
			String kuerzel = res.getString("Kuerzel");
			String mail = res.getString("Mail");
			String password = res.getString("Password");
			int supervisor =  res.getInt("Supervisor");
			
			
			st = conn.prepareStatement("SELECT WagePerHour FROM  Wage where EmployeeIDFS=? order by ValidFrom desc");
			st.setInt(1, id);
			
			res = st.executeQuery();
	
			res.next();
			
			int wage = res.getInt("WagePerHour");
			
			
			// create new employee
			employee = new Employee(id, firstname, lastname, kuerzel, mail, password, wage, supervisor);
	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		return employee;
	}

	/**
	 * returns all employees, working at the current project
	 * 
	 * @param id
	 *            id of the current task
	 * 
	 * @return all employees, working at the current project
	 */
	public ResultSet getEmployees(int id) {
		try {
			st = conn.prepareStatement("SELECT Employees.Firstname , Employees.Lastname "
					+ "FROM Employees INNER JOIN Assignments ON Employees.EmployeeID = Assignments.EmployeeIDFS "
					+ "WHERE Assignments.TaskIDFS =?");
			st.setInt(1, id);
			res = st.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Get all the employees with the given supervisor and the supervisor
	 * itself.
	 * 
	 * @param supervisor
	 *            ID of the supervisor
	 * 
	 * @return ArrayList with all employees
	 * 
	 * @throws SQLException
	 */
	public ArrayList<Employee> getAllEmployees(int supervisor) throws SQLException {
		ArrayList<Employee> employees = new ArrayList<Employee>();
		Employee employee;
	
		st = conn.prepareStatement("SELECT * from Employees WHERE Supervisor = ? or EmployeeID = ?");
		st.setInt(1, supervisor);
		st.setInt(2, supervisor);
		
		try {
	
			// get the employees
			res = st.executeQuery();
	
			while (res.next()) {
				
				// create new employees and add them to the ArrayList
				int eID = res.getInt("EmployeeID");
				String eFirstname = res.getString("Firstname");
				String eLastname = res.getString("Lastname");
				String eKuerzel = res.getString("Kuerzel");
				String eMail = res.getString("Mail");
				String ePassword = res.getString("Password");
				int eSupervisor = res.getInt("Supervisor");
	
				// get the wage of the employee
				PreparedStatement stWage = conn.prepareStatement("SELECT WagePerHour FROM  Wage where EmployeeIDFS=? order by ValidFrom desc");
				stWage.setInt(1, eID);
				ResultSet resWage = stWage.executeQuery();
				resWage.next();
				int eWage = resWage.getInt("WagePerHour");
	
				employee = new Employee(eID, eFirstname, eLastname, eKuerzel, eMail, ePassword, eWage, eSupervisor);
				
				employees.add(employee);
			}
	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		return employees;
	}

	/**
	 * get all tasks the given employee is assigned to
	 * 
	 * @param employee
	 *            ID of the employee
	 * @return ArrayList with all task IDs
	 * @throws SQLException
	 */
	public ArrayList<Integer> getAssignments(int employee) throws SQLException {
		ArrayList<Integer> tasks = new ArrayList<Integer>();
	
		// get the task IDs
		st = conn.prepareStatement("SELECT TaskIDFS from Assignments WHERE EmployeeIDFS =?");
		st.setInt(1, employee);
		res = st.executeQuery();
	
		while (res.next()) {
			// add the IDs to the ArrayList
			tasks.add(res.getInt("TaskIDFS"));
		}
	
		return tasks;
	}

	/**
	 * get the ID of the assignment between the given employee and task
	 * 
	 * @param employee
	 *            ID of the employee
	 * @param task
	 *            ID of the task
	 * @return Assignment ID
	 * @throws SQLException
	 */
	public int getAssignment(int employee, int task) throws SQLException {
	
		// get the ID of the assignment
		st = conn.prepareStatement("SELECT AssignmentID from Assignments WHERE EmployeeIDFS = ? and TaskIDFS = ?");
		st.setInt(1, employee);
		st.setInt(2, task);
		res = st.executeQuery();
		res.next();
	
		return res.getInt("AssignmentID");
	}

	public Employee findUser(String user, String password){
		try {
			st = conn.prepareStatement("SELECT * from Employees WHERE (Mail=? OR Kuerzel=?) and Password=?");
			st.setString(1, user);
			st.setString(2, user);
			st.setString(3, password);
			res = st.executeQuery();
			res.next();
			Employee e = new Employee(res.getInt("EmployeeID"), 
									  res.getString("Firstname"), 
									  res.getString("Lastname"), 
									  res.getString("Kuerzel"), 
									  res.getString("Mail"), 
									  res.getString("Password"), 
									  0, 
									  res.getInt("Supervisor"));
			return e;
		} catch (SQLException e) {
			return null;
		}
	}

	public Employee findUser(String user) {
		try {
			st = conn.prepareStatement("SELECT * from Employees WHERE Mail=?");
			st.setString(1, user);
			res = st.executeQuery();
			res.next();
			Employee e = new Employee(res.getInt("EmployeeID"), 
									  res.getString("Firstname"), 
									  res.getString("Lastname"), 
									  res.getString("Kuerzel"), 
									  res.getString("Mail"), 
									  res.getString("Password"), 
									  0, 
									  res.getInt("Supervisor"));
			return e;
		} catch (SQLException e) {
			return null;
		}
	}

	/**
	 * creates a new project in the database
	 * 
	 * 
	 * @param pName
	 *            name of the project
	 * @param pShortname
	 *            shortname for the project
	 * @param pLeader
	 *            the leader of the project
	 * @param pBudget
	 *            Total Budget
	 * @param pCurrency
	 *            Project Budget in EUR or CHF
	 * @param pStart
	 *            start date of the project
	 * @param pEnd
	 *            end date of the project
	 * @param pPartners
	 *            project partners (name of the companies)
	 * 
	 * @return the new ID of the project in the database
	 * 
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public int newProject(String pName, String pShortname, int pLeader, String pBudget, String pCurrency, String pStart, String pEnd,
			String pPartners)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		// create the new project
		st = conn.prepareStatement("INSERT INTO Projects ("
				+ "ProjectShortname, ProjectName, ProjectLeader, TotalBudget, Currency, ProjectStart, ProjectEnd, Partner"
				+ ") VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
		
		st.setString(1, pShortname);
		st.setString(2, pName);
		st.setInt(3, pLeader);
		st.setString(4, pBudget);
		st.setString(5, pCurrency);
		st.setString(6, pStart);
		st.setString(7, pEnd);
		st.setString(8, pPartners);

		st.executeUpdate();

		// get the ID of the new project and return it afterwards
		st = conn.prepareStatement("SELECT ProjectIDFS FROM projectmanagement.Projects ORDER BY ProjectIDFS DESC LIMIT 1");
		res = st.executeQuery();
		res.next();
		int pID = res.getInt("ProjectIDFS");
		
		return pID;
	}

	/**
	 * creates a new workpackage in the database
	 * 
	 * @param projectIDFS
	 *            ID of the project the workpackage belongs to
	 * @param wpName
	 *            name of the workpackage
	 * @param wpStart
	 *            start date of the workpackage
	 * @param wpEnd
	 *            end date of the workpackage
	 * 
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public void newWorkpackage(int projectIDFS, String wpName, String wpStart, String wpEnd)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {

		// create the new workpackage
		st = conn.prepareStatement( "INSERT INTO Workpackages (ProjectIDFS, WPName, WPStart, WPEnd) VALUES (?, ?, ?, ?);");
		st.setInt(1, projectIDFS);
		st.setString(2, wpName);
		st.setString(3, wpStart);
		st.setString(4, wpEnd);

		st.executeUpdate();

	}

	/**
	 * creates a new task in the database
	 * 
	 * @param projectID
	 *            ID of the project the task belongs to
	 * @param wpName
	 *            name of the workpackage the task belongs to
	 * @param taskName
	 *            name of the task
	 * @param taskStart
	 *            start date of the task
	 * @param taskEnd
	 *            end date of the task
	 * @param taskPM
	 *            total PMs planned for the task
	 * @param taskBudget
	 *            total budget planned for the task
	 * 
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public void newTask(int projectID, String wpName, String taskName, String taskStart, String taskEnd, String taskPM,	String taskBudget)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {

		st = conn.prepareStatement("SELECT WorkpackageID FROM projectmanagement.Workpackages WHERE WPName=? AND ProjectIDFS=?");
		st.setString(1, wpName);
		st.setInt(2, projectID);

		// get the id from the workpackage (needed to create the new task)
		res = st.executeQuery();
		res.next();
		int wpID = res.getInt("WorkpackageID");

		// create new task
		newTask(wpID, taskName, taskStart, taskEnd, taskPM, taskBudget);

	}
	
	
	/**
	 * creates a new task in the database
	 * 
	 * @param projectID
	 *            ID of the project the task belongs to
	 * @param wpID
	 *            ID of the workpackage the task belongs to
	 * @param taskName
	 *            name of the task
	 * @param taskStart
	 *            start date of the task
	 * @param taskEnd
	 *            end date of the task
	 * @param taskPM
	 *            total PMs planned for the task
	 * @param taskBudget
	 *            total budget planned for the task
	 * 
	 * @throws SQLException
	 */
	public void newTask(int wpID, String taskName, String taskStart, String taskEnd, String taskPM, String taskBudget) throws SQLException {
		
		st = conn.prepareStatement("INSERT INTO Tasks (WorkpackageIDFS, TaskName, TaskStart, TaskEnd, PMs, Budget) VALUES (?, ?, ?, ?, ?, ?)");
		st.setInt(1, wpID);
		st.setString(2, taskName);
		st.setString(3, taskStart);
		st.setString(4, taskEnd);
		st.setString(5, taskPM);
		st.setString(6, taskBudget);

		st.executeUpdate();
	}


	/**
	 * create a new employee in the database
	 * 
	 * @param employeeID
	 *            ID of the current user and the supervisor of the new user
	 * @param firstname
	 *            firstname of the new user
	 * @param lastname
	 *            lastname of the new user
	 * @param kuerzel
	 *            company intern kuerzel of the new user
	 * @param mail
	 *            mail of the new user
	 * @param wage
	 *            wage per hour of the new user
	 * 
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public Employee newEmployee(int employeeID, String firstname, String lastname, String kuerzel, String mail, String wage)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {

		st = conn.prepareStatement("INSERT INTO Employees (Firstname, Lastname, Kuerzel, Password, Mail, Supervisor) VALUES (?, ?, ?, ?, ?, ?)");
		

		String password = PasswordGenerator.getInstance().getNewPassword();
		
		String passwordEncrypted = PasswordService.getInstance().encrypt(password);

		// create new employee
		st.setString(1, firstname);
		st.setString(2, lastname);
		st.setString(3, kuerzel);
		st.setString(4, passwordEncrypted);
		st.setString(5, mail);
		st.setInt(6, employeeID);
		st.executeUpdate();

		// get the ID of the employee
		st = conn.prepareStatement("Select * from Employees order by EmployeeID desc");
		res = st.executeQuery();

		res.next();
		
		int eID = res.getInt("EmployeeID");
		String eFirstname = res.getString("Firstname");
		String eLastname = res.getString("Lastname");
		String eKuerzel = res.getString("Kuerzel");
		String eMail = res.getString("Mail");
		int eSupervisor = res.getInt("Supervisor");	
		
		Employee user = new Employee(eID, eFirstname, eLastname, eKuerzel, eMail, password, 0, eSupervisor);

		// create new date from the current time
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(cal.getTime());

		// create new wage
		st = conn.prepareStatement("INSERT INTO Wage (EmployeeIDFS, WagePerHour, ValidFrom) VALUES (?, ?, ?)");
		st.setInt(1, eID);
		st.setString(2, wage);
		st.setString(3, date);

		st.executeUpdate();
		
		return user;
	}

	/**
	 * create a new expense in the database
	 * 
	 * @param projectID
	 *            ID of the project
	 * 
	 * @param employeeID
	 *            ID of the employee
	 * 
	 * @param costs
	 *            costs of the expense
	 * 
	 * @param type
	 *            type of the expense (Travel, etc.)
	 * 
	 * @param description
	 *            description
	 * 
	 * @param date
	 *            date of the expense
	 * 
	 * @throws SQLException
	 */
	public void newExpense(int projectID, int employeeID, double costs, String type, String description, String date)
			throws SQLException {

		// create new expense
		st = conn.prepareStatement("INSERT INTO Expenses (ProjectIDFS, EmployeeIDFS, Costs, Type, Description, Date) VALUES (?, ?, ?, ?, ?, ?)");
		st.setInt(1, projectID);
		st.setInt(2, employeeID);
		st.setDouble(3, costs);
		st.setString(4, type);
		st.setString(5, description);
		st.setString(6, date);

		st.executeUpdate();
	}

	/**
	 * create a new assignment for the given task and employee
	 * 
	 * @param taskID
	 *            ID of the task
	 * @param employeeID
	 *            ID of the employee
	 * @throws SQLException
	 */
	public void newAssignment(int taskID, int employeeID) throws SQLException {

		// create new assignment
		st = conn.prepareStatement("INSERT INTO Assignments (TaskIDFS, EmployeeIDFS) VALUES (?, ?)");
		st.setInt(1, taskID);
		st.setInt(2, employeeID);

		st.executeUpdate();
	}

	/**
	 * create a new booking in the database
	 * 
	 * @param assignment
	 *            ID of the assignment
	 * @param month
	 *            number of the month
	 * @param hours
	 *            amount of hours
	 * @throws SQLException
	 */
	public void newBooking(int assignment, int month, double hours) throws SQLException {

		// create new booking
		st = conn.prepareStatement("INSERT INTO Bookings (AssignmentIDFS, Month, Hours) VALUES (?, ?, ?)");
		st.setInt(1, assignment);
		st.setInt(2, month);
		st.setDouble(3, hours);

		st.executeUpdate();
	}
	
	public void newWage(int userID, double wage, String date) throws SQLException {
		st = conn.prepareStatement("INSERT INTO Wage (EmployeeIDFS, WagePerHour, ValidFrom) VALUES (?, ?, ?)");
		
		st.setInt(1, userID);
		st.setDouble(2, wage);
		st.setString(3, date);
		
		st.executeUpdate();
	}

	public void updateProject(int id, String name, String shortname, double budget, String currency, String start, String end, String partners) throws SQLException {
		st = conn.prepareStatement("UPDATE Projects SET ProjectShortname=?,ProjectName=?,TotalBudget=?,Currency=?,ProjectStart=?,ProjectEnd=?,Partner=? WHERE Projects.ProjectIDFS=?");
		st.setString(1, shortname);
		st.setString(2, name);
		st.setDouble(3, budget);
		st.setString(4, currency);
		st.setString(5, start);
		st.setString(6, end);
		st.setString(7, partners);
		st.setInt(8, id);
		
		st.executeUpdate();		
	}

	public void updateWorkpackage(int id, String name, String start, String end) throws SQLException {
		
		st = conn.prepareStatement("UPDATE Workpackages SET WPName=?,WPStart=?,WPEnd=? WHERE WorkpackageID=?");
		st.setString(1, name);
		st.setString(2, start);
		st.setString(3, end);
		st.setInt(4, id);

		st.executeUpdate();
	}

	public void updateTask(int id, String name, String start, String end, int pm, double budget, int wp) throws SQLException {
		st = conn.prepareStatement("UPDATE Tasks SET WorkpackageIDFS=?,TaskName=?,TaskStart=?,TaskEnd=?,PMs=?,Budget=? WHERE TaskID='");

		st.setInt(1, wp);
		st.setString(2, name);
		st.setString(3, start);
		st.setString(4, end);
		st.setInt(5, pm);
		st.setDouble(6, budget);
		st.setInt(7, id);
		
		st.executeUpdate();
	}

	public void updateExpense(int id, int employee, double costs, String type, String description, String date) throws SQLException {
		st = conn.prepareStatement("UPDATE Expenses SET EmployeeIDFS=?,Costs=?,Type=?,Description=?,Date=? WHERE ExpenseID=?");

		st.setInt(1, employee);
		st.setDouble(2, costs);
		st.setString(3, type);
		st.setString(4, description);
		st.setString(5, date);
		st.setInt(6, id);
		
		st.executeUpdate();
	}

	public void updateEffort(int id, String month, String hours) throws SQLException {
		st = conn.prepareStatement("UPDATE Bookings SET Month=?,Hours=? WHERE BookingID=?");

		st.setString(1, month);
		st.setString(2, hours);
		st.setInt(3, id);
		
		st.executeUpdate();
	}

	public void updateUser(int userID, String firstname, String lastname, String kuerzel, String mail) throws SQLException {
		st = conn.prepareStatement("UPDATE Employees SET Firstname=?,Lastname=?,Kuerzel=?,Mail=? WHERE EmployeeID=?");
		
		st.setString(1, firstname);
		st.setString(2, lastname);
		st.setString(3, kuerzel);
		st.setString(4, mail);
		st.setInt(5, userID);
		
		st.executeUpdate();
	}

	public void updatePassword(int userID, String password) throws SQLException {
		st = conn.prepareStatement("UPDATE Employees SET Password=? WHERE EmployeeID=?");
		
		st.setString(1, password);
		st.setInt(2, userID);
		
		st.executeUpdate();
	}

	public void archiveProject(int projectID) throws SQLException {
		st = conn.prepareStatement("UPDATE projectmanagement.Projects SET Archive=1 WHERE Projects.ProjectIDFS = ?");
		st.setInt(1, projectID);
		st.executeUpdate();
	}

	public void deleteWorkpackage(int workpackageID) throws SQLException {
		st = conn.prepareStatement("DELETE FROM Workpackages WHERE WorkpackageID=?");
		
		st.setInt(1, workpackageID);
		
		st.executeUpdate();
	}

	public void deleteTask(int taskID) throws SQLException {
		st = conn.prepareStatement("DELETE FROM Tasks WHERE TaskID=?");
		
		st.setInt(1, taskID);
		
		st.executeUpdate();
	}

	public void deleteExpense(int expenseID) throws SQLException {
		st = conn.prepareStatement("DELETE FROM Expenses WHERE ExpenseID=?");
		
		st.setInt(1, expenseID);
		
		st.executeUpdate();
	}

	public void deleteEffort(int effortID) throws SQLException {
		st = conn.prepareStatement("DELETE FROM Bookings WHERE BookingID=?");
		
		st.setInt(1, effortID);
		
		st.executeUpdate();
	}

}
