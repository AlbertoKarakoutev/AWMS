# **Documentation for AWMS**
> ###### *Arranged by packages and classes*  

# **Index**
- ## **[com.company.awms.data](#comcompanyawmsdata)**
	- [com.company.awms.data.documents](#comcompanyawmsdatadocumentsdoc)
	- [com.company.awms.data.employees](#comcompanyawmsdataemployeesemployee)
	- [com.company.awms.data.forum](#comcompanyawmsdataforumforumreply)
	- [com.company.awms.data.schedule](#comcompanyawmsdatascheduleday)
- ## **[com.company.awms.controllers](#comcompanyawmscontrollers)**
	- [com.company.awms.controllers.DocumentsController](#comcompanyawmscontrollersdocumentscontroller)
	- [com.company.awms.controllers.EmployeesController](#comcompanyawmscontrollersemployeescontroller)
	- [com.company.awms.controllers.ForumController](#comcompanyawmscontrollersforumcontroller)
	- [com.company.awms.controllers.SalaryController](#comcompanyawmscontrollerssalarycontroller)
- ## **[com.company.awms.services](#comcompanyawmsservices)**  
	- [com.company.awms.services.ScheduleService](#comcompanyawmsservicesscheduleservice)
	- [com.company.awms.services.EmployeeService](#comcompanyawmsservicesemployeeservice)
	- [com.company.awms.services.ForumService](#comcompanyawmsservicesforumservice)
	- [com.company.awms.services.SalaryService](#comcompanyawmsservicessalaryservice)

# Contents

- ## **com.company.awms.data**  
	*This package contains all links to the database and their appropriate attributes*  
    - ### **com.company.awms.data.documents.Doc**
      	***(Class)***  
      	**Main document object, containing _variables_ for its content, as well as their appropriate getters and setters**
      	- **Variables:**
		- String _id_
       		- Binary _data_
        	- String _accessLevel_
        	- String _uploaderID_
        	- LocalDateTime _uploadDateTime_
        	- ArrayList _downloaderIDs_
        	- double _size_
    - ### **com.company.awms.data.documents.DocumentRepo**  
		***(Interface)***  
      	**Document repository, responsible for access to the database and object retrieval**
        - **Methods:**
			- ArrayList<Doc> _findByData(Binary data)_  
	Returns the document, containing the specified data
	
			- ArrayList<Doc> _findByAccessLevel(String accessLevel)_  		    
			
	- ### **com.company.awms.data.employees.Employee**
		***(Class)***  
		**Main employee object, containing _methods_ and _variables_, as well as their appropriate getters and setters**
		- **Methods:**
			- String _info()_  
			Returns all information about the object in a formatted string
			
		- **Variables:**
			- String _id_
			- String _nationalID_
			- String _firstName_
			- String _lastName_
			- String _iban_
			- String _accessLevel_  -  _In the form **char-int**, char being a department code, and int being the employee level in that department_
			- String _phoneNumber_
			- double _salary_
			- int[] _workWeek
			- ArrayList<Dictionary<String, Object>> leaves - _Employee paid/unpaid leaves, in the form ("Start":Date, "End":Date, "Paid":boolean)  
  
	 - ### **com.company.awms.data.employees.EmployeeDailyReference**  
		***(Class)***  
      	**Reference object, child of Employee, containing all of its information. It is used in the schedule, and is intended to be instantiated each day, which that employee is at work. It contains work time and tasks for the specific day, ass well as all getters and setters for it's _variables_**
        - **Variables:**
			- @Autowired EmployeeRepo _employeeRepo_
			- LocalTime[] _workTime_
			- String _refFirstName_
			- String _refLastName_
			- String _refNationalID_
			- ArrayList _tasks_
			- LocalDate _date_
			  

	 - ### **com.company.awms.data.employees.EmployeeRepo**  
		***(Interface)***  
      	**Employee repository, responsible for access to the database and object retrieval**
        - **Methods:**
			- Employee _findByNationalID(String nationalID)_
			
			- Employee _findByEmail(String email)_
			
			- ArrayList _findByFirstName(String firstName)_
			
			- ArrayList _findByLastName(String lastName)_
			
			- ArrayList _findByAccessLevel(String accessLevel)_ 
			
	- ### **com.company.awms.data.forum.ForumReply**  
		***(Class)***  
      	**Main forum reply object, containing _variables_, as well as their appropriate getters and setters**
        - **Variables:**
			- String _id_
			- String _threadID_
			- String _issuerID_
			- String _body_
			- LocalDateTime _time_
	
	- ### **com.company.awms.data.forum.ForumThread**  
		***(Class)***  
      	**Main forum thread object, containing _variables_, as well as their appropriate getters and setters**
        - **Variables:**
			- String _id_
			- String _title_
			- String _issuerID_
			- String _body_
			- LocalDateTime _time_
			- boolean _isAnswered_
	
	- ### **com.company.awms.data.forum.ThreadReplyDTO**  
		***(Class)***  
      	**Main forum thread-reply object, containing information about entire threads and all their replies with _variables_, as well as their appropriate getters and setters**
        - **Variables:**
			- ForumThread _forumThread_
			- List _threadReply_
			
	- ### **com.company.awms.data.forum.ForumThreadRepo**  
		***(Interface)***  
      	**Forum thread repository, responsible for access to the database and object retrieval**
        - **Methods:**
			- List _findByIssuerID(Strind issuerID)_
	
	- ### **com.company.awms.data.forum.ForumReplyRepo**  
		***(Interface)***  
      	**Forum reply repository, responsible for access to the database and object retrieval**
        - **Methods:**
			- List _findByThreadID(String threadID)_
			
			- List _findByIssuerID(Strind issuerID)_
	
	- ### **com.company.awms.data.schedule.Day**  
		***(Class)***  
      	**Main day object, containing _variables_, as well as their appropriate getters and setters**
        - **Variables:**
			- String _id_
			- LocalDate _date_
			- ArrayList _employees_
	
	- ### **com.company.awms.data.schedule.Task**  
		***(Class)***  
      	**Main daily task object, containing _variables_, as well as their appropriate getters and setters**
        - **Variables:**
			- String _id_
			- String _receiverNationalID_
			- String _taskBody_
			- String _taskTitle_
			- boolean _completed_
			- boolean _paidFor_
			- double _taskReward_
			
	- ### **com.company.awms.data.schedule.ScheduleRepo**  
		***(Interface)***  
      	**Schedule repository, responsible for access to the database and object retrieval**
        - **Methods:**
			- Day _findByDate(LocalDate date)_
			
- # **com.company.awms.controllers**
	*This package contains all controllers, responsible for receiving, handling and responding to web requests from the client. All controllers contain references to their specific services, and others, if needed
	
	- ### com.company.awms.controllers.DocumentsController
		***(Class)***  
      	**Document controller, responsible for the requests' getting and posting and their appropriate _methods_**
        - **Methods:**
			- ***@PostMapping*** ResponseEntity _uploadDocument(MultipartFile file, Model model)_  
			Handles upload requests by calling _documentService.uploadDocument()_ and sends appropriate response 
			
			- ***@GetMapping*** ResponseEntity _downloadDocument(String id, Model model)_  
			Handles download requests by calling _documentService.downloadDocument()_ with the document id and responds appropriately
			
	- ### com.company.awms.controllers.EmployeesController
		***(Class)***  
      	**Employee controller, responsible for the requests' getting and posting and their appropriate _methods_**
        - **Methods:**
			- ***@PostMapping*** ResponseEntity _register(Employee newEmployee)_  
			Handles register requests by calling _employeeService.register()_ and sends appropriate response 
			
			- ***@GetMapping*** ResponseEntity _getEmployee(String username)_  
			Handles employee object requests by retrieving that employee from the database if it is found and responds appropriately
			
	- ### com.company.awms.controllers.ForumController
		***(Class)***  
      	**Forum controller, responsible for the requests' getting and posting and their appropriate _methods_**
        - **Methods:**
			- ***@GetMapping*** ResponseEntity _getAllThreads()_  
			Handles requests for all the threads and retrieving them from the database through _forumService.getAllThreads()_ and responds appropriately  
			
			- ***@GetMapping*** ResponseEntity _getThread(String threadID)_  
			Handles requests for a specific thread and if found, retrieves it from the database through _forumService.getThreadWithRepliesByID()_ and responds appropriately
			
			- ***@GetMapping*** ResponseEntity _getAllThreadsFromEmployee(String employeeID)_  
			Handles requests by for all the threads by a specific employee, retrieving them from the database through _forumService.getAllThreadsFromEmployee()_ and responds appropriately
			
			- ***@GetMapping*** ResponseEntity _getAllRepliesFromEmployee(String employeeID)_  
			Handles requests by for all the replies by a specific employee, retrieving them from the database through _forumService.getAllRepliesFromEmployee()_ and responds appropriately
			
			- ***@PostMapping*** ResponseEntity _addThread(ForumThread forumThread)_  
			Handles thread addition requests by calling 
_forumService.addNewThread()_ and sends appropriate response 

			- ***@PostMapping*** ResponseEntity _addReply(ForumReply forumReply, String threadID)_  
			Handles reply addition requests by calling _forumService.addNewReply()_ and responds appropriately
			
			- ***@PutMapping*** ResponseEntity<String> _setThreadAnswered(@PathVariable String threadID)_
			Marks thread as answered by calling _forumService.markAsAnswered()_ and responds approprately
			
			- ***@PutMapping*** ResponseEntity<String> _editThread(@RequestBody ForumThread newForumThread, @PathVariable String oldThreadID)_
			Handles edit thead requests by calling _forumService.editThread()_ and responds appropriately
			
	- ### com.company.awms.controllers.SalaryController
		***(Class)***  
      	**Salary controller, responsible for the requests' getting and posting and their appropriate _methods_**
        - **Methods:**
			- ***@GetMapping*** ResponseEntity _getByName(String nationalID)_  
			Handles requests for calculating monthly worktime through _salaryService.calculateWOrkHours()_ and responds appropriately  
			
			- ***@GetMapping*** ResponseEntity _getSalary(String nationalID)_  
			Handles requests for a salary estimate for an employee, calls _salaryService.estimateSalary()_ and responds appropriately
			
	- ### com.company.awms.controllers.ScheduleController
		***(Class)***  
      	**Schedule controller, responsible for the requests' getting and posting and their appropriate _methods_**
        - **Methods:**
			- ***@GetMapping*** String _addDays(int month)_  
			Populates the database with all the days for a specific month  
  
			- ***@GetMapping*** ResponseEntity _swapEmployees(String requestorNationalID, String receiverNationalID, String requestorDate, String receiverDate)_  
			Handles requests for swapping employee shifts in the schedule through _scheduleService.swapEmployees()_ and responds appropriately  
  
			- ***@GetMapping*** ResponseEntity _applyRegularSchedule()_  
			Calls scheduleService.applyRegularSchedule() and responds appropriately  
  
			- ***@GetMapping*** ResponseEntity _applyIrregularSchedule()_  
			Calls scheduleService.applyIrregularSchedule() and responds appropriately  
  
			- ***@GetMapping*** ResponseEntity _applyOnCallSchedule()_  
			Calls scheduleService.applyOnCallSchedule() and responds appropriately  
  
			- ***@GetMapping*** ResponseEntity _addTask(String taskDay, String receiverNationalID)_  
			Handles requests for a new task on an employee on a specific day by calling _scheduleService.addTask()_ and responds appropriately
		
- # **com.company.awms.services**
	*This package contains all the services, which are run by the software. All services have a private reference to their repos, as well as a public getter to access them from elsewhere.*

	- ### com.company.awms.services.DocumentService
		***(Class)***  
      	**Main schedule backend logic, responsible for the executing the neccessary algorithms and actions, running in the system with their appropriate _methods_.**
        - **Methods:**
			- ArrayList _getAccessableDocumentIDs(String accessLevel)_  
			Splits the provided access level into a _char_ and _int_ and tries to parse into variables for them. If successful, a for loop iterates over the levels upto the given one and adds the documents, that match that level to the _ArrayList accessableDocumentIDs_. Returns that _ArrayList_
			
			- void _uploadDocument(MultipartFile file, String uploaderID)_
			Finds the user by his ID. If successfull, it creates a new _Day_ object and fills it with the information for the uploader and the data from _file_. Sets the upload date and size. Uploads to the database.
			 
			- Doc _downloadDocument(String documentID, String downloaderID)_
			Tries to create a _Doc_ object through the document ID. If successful, it add the downloader ID into the list of downloaders and re-uploads it. Returns the document.
			
	- ### com.company.awms.services.EmployeeService
		***(Class)***  
      	**Main employee actions backend logic, responsible for the executing the neccessary algorithms and running in the system with their appropriate _methods_.**
        - **Methods:**
			- EmployeeDailyReference _createEmployeeDailyReference(Employee employee, LocalDate date, LocalTime[] workTime)_  
			Creates new _EDR_ object from the employee. Feeds it the date and work hours. Returns that _EDR_ object  

			- void _addLeave(String employeeID, LocalDate start, LocalDate end, boolean paid)_  
			Creates a new paid/unpaid leave and adds it to the employee object, and updates the database  

			- void _requestSwap(String requestorID, LocalDate date, Strign message)_
			
	- ### com.company.awms.services.ForumService
		***(Class)***  
      	**Main forum backend logic, responsible for the executing the neccessary algorithms and actions, running in the system with their appropriate _methods_.**
        - **Methods:**
			- ThreadReplyDTO _getThreadWithRepliesByID(String threadID)_  
			Tries to find the specified thread with all its replies through the id, and returns a _DTO_ with them
			
			- ForumThread _getThread(String threadID)
			Returns thread with _threadID_ from the database
			
			- ThreadReplyDTO getThreadWithRepliesByID(String threadID)
			Returns a ThreadReplyDTO object containing the thread with _threadID_ and all of it's replies 
			
			- List _getAllThreads()  
			Finds all the threads from the database
			
			- List _getAllThreadsFromEmployee()  
			Finds all the threads from a specific employee from the database 
			
			- List _getAlRepliesFromEmployee()  
			Finds all the replies from a specific employee from the database 
			
			- void addNewThread(ForumThread newThread)  
			Adds the _newThread_ object to the database
			
			- void addNewReply(ForumThread newThread, String threadId)  
			Adds the _newReply_ object with the thread id to the database
			
			- void markAsAnswered(String threadID)
			Marks thread with _threadID_ as answered
			
			- void editThreadForumThread newForumThread, String oldThreadID)
            Updates the thread with _oldThreadID_ with the new information from  _newForumThread_
			
	- ### com.company.awms.services.SalaryService
		***(Class)***  
      	**Main salary module backend logic, responsible for the executing the neccessary algorithms and actions, running in the system with their appropriate _methods_.**
        - **Methods:**
			- double _taskRewardBonus(String nationalID)_  
			Runs through all the DOM until the current. Locates the _Day_ objects for them, and the _EDR_'s for each day. Finds the desired _EDR_ through the _nationalID_ and gets all of its tasks. If a task is marked as completed and not paid for, it is marked paid for and its reward is aggregated into _taskRewards_, which is ultimately returned by the method
			
			- double _calculateWorkHours(String nationalID)_  
			Runs through all the DOM until the current. Locates the _Day_ objects for them, and the _EDR_'s for each day. Finds the desired _EDR_ through the _nationalID_ and aggregates its work time into _hours_, which is ultimately returned by the method
			
			- double _estimateSalary(String nationalID, Double payPerHour)_  
			Aggregates the values of the work hours, multiplied by the _payPerHour_ and the rewards bonuses from the tasks and returns the estimate salary.  
			
	-  ### com.company.awms.services.ScheduleService
		***(Class)***  
      	**Main schedule module backend logic, responsible for the executing the neccessary algorithms and actions, running in the system with their appropriate _methods_.**
        - **Methods:**
			- double _addWorkDay(String employeeID, LocalDate date, JSONObject thisDepartment, int level)_  
			Tries to get the employee from the DB. If successfull, makes a new _EDR_ object from it. Tries to find the _Day_ from the DB. Adds the new _EDR_ to the _Day.employees_ list and updates the _Day_ in the DB
			
			- boolean _swapEmployees(String requestorNationalID, String receiverNationalID, String requestorDate, String receiverDate)  
			Tries to get the _EDR_'s form the specific days. If successfull, swaps their work times to preserve the daily work load, then swaps the whole _EDR_'s and updates the _DAY_s in the database. Returns true if successfull
			
			- boolean _addTask(String taskDay, String receiverNationalID)_  
			Tries to get the _Day_ and _EDR_ from that day form the DB. If successful, creates a new _Task_ and loads the information into it. Saves the updated _Day_ into the DB and returns true if successful
			
			- Task _createTask(String receiverNationalID, Day date, String taskBody, String taskTitle)_  
			Creates a bew _Task_ objects and returns it  
  
			- boolean _isLeaveDay(Employee employee, Day day)_  
			Checks if a day is marked as a paid/unpaid leave day for en employee  
  
			- JSONObject _setDepartment(String department, int level, String type)_  
			Finds the appropriate department for the key values from _departments.json_  
  
			- boolean _applyOnCallSchedule(String department, int level)_  
			Gets all employees from the specified accessLevel. Parses the _departments.json_ configuration file and finds the current department. Distributes shifts according to number of employees and shifts needed. Applies the third scheduling algorithm to that department, if it is specified in the file.  
  
			- boolean _applyIrregularSchedule(String department, int level)_  
			Loads all employees from the specified accessLevel. Parses the _departments.json_ configuration file and finds the current department. Applies the second scheduling algorithm to that department, if it is specified in the file. 
  
			- boolean _applyRegularSchedule(String department, int level)_  
			Loads all employees from the specified accessLevel. Parses the _departments.json_ configuration file and finds the current department. Applies the first scheduling algorithm to that department, if it is specified in the file. 
			
			- ArrayList _viewSchedule(String accessLevel)_  
			Iterates through all the dates upto the current one. Finds the _Day_ and all _EDR_ objects from it. Aggregates all _EDR_'s with equal and lower access levels. Returns that list
