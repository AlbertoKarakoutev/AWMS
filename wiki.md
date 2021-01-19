# **Documentation for AWMS**
> ###### *Arranged by packages and classes*  

# **Index**
- ## **[com.company.awms.data](#comcompanyawmsdata)**
	- [com.company.awms.data.documents](#comcompanyawmsdatadocumentsdoc)
	- [com.company.awms.data.employees](#comcompanyawmsdataemployeesemployee)
	- [com.company.awms.data.forum](#comcompanyawmsdataforumforumreply)
	- [com.company.awms.data.schedule](#comcompanyawmsdatascheduleday)
- ## **[com.company.awms.controllers](#comcompanyawmscontrollers)**
	- [com.company.awms.controllers.DocumentController](#comcompanyawmscontrollersdocumentcontroller)
	- [com.company.awms.controllers.EmployeeController](#comcompanyawmscontrollersemployeecontroller)
	- [com.company.awms.controllers.ForumController](#comcompanyawmscontrollersforumcontroller)
	- [com.company.awms.controllers.SalaryController](#comcompanyawmscontrollerssalarycontroller)
	- [com.company.awms.controllers.AdminController](#comcompanyawmscontrollersadmincontroller)
	- [com.company.awms.controllers.ContactsController](#comcompanyawmscontrollerscontactscontroller)
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
        	- int _level_  
		- String _name_
		- String _type_
		- String _department_
        	- String _uploaderID_
        	- LocalDateTime _uploadDateTime_
        	- ArrayList _downloaderIDs_
        	- long _size_
    - ### **com.company.awms.data.documents.DocInfoDTO**
      	***(Class)***  
      	**DTO Object for the Document class, containing generalized information about the documents, and it's appropriate getters and setters**
      	- **Variables:**
		- String _id_
		- String _name_
		- double _size_
		- String _type_
        	- String _ownerID_
		- String _ownerName_
    - ### **com.company.awms.data.documents.DocumentRepo**  
		***(Interface)***  
      	**Document repository, responsible for access to the database and object retrieval**
        - **Methods:**	
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
			- String _password_
			- String _role_
			- String _email_
			- int _level_
			- String _department_
			- String _iban_
			- String _accessLevel_  -  _In the form **char-int**, char being a department code, and int being the employee level in that department_
			- String _phoneNumber_
			- double _salary_
			- double _payPerHour_
			- int[] _workWeek
			- ArrayList<Dictionary<String, Object>> leaves - _Employee paid/unpaid leaves, in the form ("start":LocalDateTime, "end":LocalDateTime, "paid":boolean)  
			- List<Notification> _notifications_  
			- List<Doc> _personalDocuments_
  
	 - ### **com.company.awms.data.employees.EmployeeDailyReference**  
		***(Class)***  
      	**Reference object, containing an Employee's information for a daily shift. It is used in the schedule, and is intended to be instantiated each day, which that 	employee is at work. It contains work time and tasks for the specific day, as well as all getters and setters for it's _variables_**
        - **Variables:**
			- LocalTime[] _workTime_
			- String _firstName_
			- String _lastName_
			- String _nationalID_
			- String _department_
			- int _level_
			- ArrayList _tasks_
			  

	 - ### **com.company.awms.data.employees.EmployeeRepo**  
		***(Interface)***  
      	**Employee repository, responsible for access to the database and object retrieval**
        - **Methods:**
			- Optional _findByNationalID(String nationalID)_
			
			- Optional _findByEmail(String email)_
			
			- List _findByAccessLevel(String accessLevel)_ 

			- List _findByDepartment(String department_
			
			- List _findAllByRole(String role)_  

			- Optional _findByRole(String role)_
  
	- ### **com.company.awms.data.employees.Notification**  
		***(Class)***  
      	**Main notificaiton class, containing the notification data, time and status, as well as all getters and setters for it's _variables_**
        - **Variables:**
			- String _message_
			- LocalDateTime _dateTime_
			- List _data_ - Usually containing some information, distinguishing the way the notification is handled in the .jsp view
			- boolean _read_


	- ### **com.company.awms.data.forum.ForumReply**  
		***(Class)***  
      	**Main forum reply object, containing _variables_, as well as their appropriate getters and setters**
        - **Variables:**
			- String _id_
			- String _threadID_
			- String _issuerID_
			- String _issuerName_
			- String _body_
			- LocalDateTime _dateTime_
	
	- ### **com.company.awms.data.forum.ForumThread**  
		***(Class)***  
      	**Main forum thread object, containing _variables_, as well as their appropriate getters and setters**
        - **Variables:**
			- String _id_
			- String _title_
			- String _issuerID_
			- String _issuerName_
			- String _body_
			- LocalDateTime _dateTime_
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
			- List _employees_
	
	- ### **com.company.awms.data.schedule.Task**  
		***(Class)***  
      	**Main daily task object, containing _variables_, as well as their appropriate getters and setters**
        - **Variables:**
			- String _id_
			- String _taskBody_
			- String _taskTitle_
			- boolean _completed_
			- boolean _paidFor_
			- double _taskReward_
			
	- ### **com.company.awms.data.schedule.ScheduleRepo**  
		***(Interface)***  
      	**Schedule repository, responsible for access to the database and object retrieval**
        - **Methods:**
			- Optional _findByDate(LocalDate date)_  

			- List _findAllDateBetween(LocalDate firstDay, LocalDate lastDay)_
			
- # **com.company.awms.controllers**
	*This package contains all controllers, responsible for receiving, handling and responding to web requests from the client. All controllers contain references to their specific services, and others, if needed. The structure governs that every controller returns a .jsp view to the client,  and injects all necessary data. An EmployeeDetails object, containing information for the logged-in employee is injected into every view through the _injectLoggedInEmployeeInfo(Model model, EmployeeDetails employeeDetails)_ method. The _getActive()_ method returns the state of the current module (controller).*
	
	- ### com.company.awms.controllers.DocumentController
		***(Class)***  
      	**Document controller, responsible for the requests' getting and posting and their appropriate _methods_. **
        - **Methods:**
			- ***@PostMapping*** String _uploadPublicDocument(MultipartFile file, EmployeeDetails employeeDetails, Model model)_  
			Handles upload requests for public documents by calling _documentService.uploadPublicDocument()_ and sends appropriate response 
			
			- ***@GetMapping*** ResponseEntity _downloadPublicDocument(String id, EmployeeDetails employeeDetails)_  
			Handles download requests for public documents by calling _documentService.downloadPublicDocument()_ with the document id and responds appropriately
			- ***@GetMapping*** ResponseEntity _downloadPersonalDocument(String id, String ownerID, EmployeeDetails employeeDetails)_  
			Handles download requests for personal documents by calling _documentService.downloadPersonalDocument()_ with the document id and responds appropriately

			- ***@PostMapping*** String _deletePublicDocument(String documentID, EmployeeDetails employeeDetails, Model model)_  
			Handles delete requests for public documents by calling _documentService.deletePublicDocument()_ with the document id and responds appropriately

			- ***@GetMapping*** String _getAccessibleDocuments(EmployeeDetails employeeDetails, Model model)_  
			Gets all accessible documents' information by the user by calling _documentService.getAccessibleDocumentsInfo()_ with the employe details and responds appropriately

			- ***@GetMapping*** String _getAllPersonalDocuments(EmployeeDetails employeeDetails, Model model)_  
			Gets all personal documents' information by the user by calling _documentService.getPersonalDocumentsInfo()_ with the employe details and responds appropriately

			- ***@GetMapping*** String _searchinDocuments(String name, EmployeeDetails employeeDetails, Model model)_  
			Searches in the accessoble documents and gives results by calling _documentService.searchInDocumentByName()_ with the name pattern and responds appropriately

			
	- ### com.company.awms.controllers.EmployeeController
		***(Class)***  
      	**Employee controller, responsible for the requests' getting and posting and their appropriate _methods_**
        - **Methods:**

			- ***@PostMapping*** String _updatePassword(String newPassword, String confirmedPassword, EmployeeDetails employeeDetails, Model model)_  
			Updates the user's password by calling _employeeService.updatePassword()_ and sends appropriate response 
			
			- ***@GetMapping*** String _getPasswordUpdate(EmployeeDetails employeeDetails, Model model)_  
			Returns the view where the user will update his/her password
			
			- ***@GetMapping*** String _getLeaves(EmployeeDetails employeeDetails, Model model)_  
			Returns the view where the user can manage hir/her leaves
			
			- ***@GetMapping*** String _requestLeave(EmployeeDetails employeeDetails, Model model, String paidStr, String startDate, String endDate)_  
			Handles requests for leaves by calling _employeeService.requestLeave()_
			
			- ***@GetMapping*** String _dismiss(EmployeeDetails employeeDetails, Model model, String noteNum)_  
			Handles requests for the dismissal of notifications through the _noteNum_ param by calling _employeeService.setNotificationRead()_

	- ### com.company.awms.controllers.ForumController
		***(Class)***  
      	**Forum controller, responsible for the requests' getting and posting and their appropriate _methods_**
        - **Methods:**
			- ***@GetMapping*** String _getAllThreads(EmployeeDetails employeeDetails, Model model)_  
			Handles requests for all the threads and retrieving them from the database through _forumService.getAllThreads()_ and responds appropriately  

			- ***@GetMapping*** String _getThreadWithReplies(String threadID, EmployeeDetails employeeDetails, Model model)_  
			Handles requests for a specific thread and if found, retrieves it from the database through _forumService.getThreadWithRepliesByID()_ and responds appropriately
			
			- ***@GetMapping*** String _getAllThreadsFromEmployee(String employeeID, EmployeeDetails employeeDetails, Model model)_  
			Handles requests by for all the threads by a specific employee, retrieving them from the database through _forumService.getAllThreadsFromEmployee()_ and responds appropriately
			
			- ***@GetMapping*** String _getAllAnsweredThreads(EmployeeDetails employeeDetails, Model model)_  
			Handles requests for all answered threads by calling _forumService.getAllAnsweredThreads()_ and responds appropriately

			- ***@GetMapping*** String _getAllUnansweredThreads(EmployeeDetails employeeDetails, Model model)_  
			Handles requests for all unanswered threads by calling _forumService.getAllUnansweredThreads()_ and responds appropriately

			- ***@GetMapping*** String _newThread(EmployeeDetails employeeDetails, Model model)_  
			Returns the view, where the user will create a new thread

			- ***@GetMapping*** String_getAllRepliesFromEmployee(String employeeID, EmployeeDetails employeeDetails, Model model)_  
			Handles requests by for all the replies by a specific employee, retrieving them from the database through _forumService.getAllRepliesFromEmployee()_ and responds appropriately
			
			- ***@GetMapping*** String _getThreadEdit(String threadID, EmployeeDetails employeeDetails, Model model)_  
			Returns the view, where the user will edit an existing thread

			- ***@PostMapping*** String _addThread(String title, String body, EmployeeDetails employeeDetails, Model model)_  
			Handles thread addition requests by calling _forumService.addNewThread()_ and sends appropriate response 

			- ***@PostMapping*** String _addReply(String body, String threadID, EmployeeDetails employeeDetails, Model model)_  
			Handles reply addition requests by calling _forumService.addNewReply()_ and responds appropriately
			
			- ***@PostMapping*** String _markAsAnswered(String threadID, EmployeeDetails employeeDetails, Model model)_  
			Sets thread status as answered by calling _forumService.markAsAnswered()_ and responds appropriately
			
			- ***@PostMapping*** String _editThread(String title, String body, String oldThreadID, EmployeeDetails employeeDetails, Model model)_  
			Handles thread amendment requests by calling _forumService.editThread()_ and sends appropriate response 

			- ***@GetMapping*** String _dismiss(EmployeeDetails employeeDetails, Model model, String noteNum, String threadID)_  
			Handles requests for the dismissal of notifications through the _noteNum_ param by calling _forumService.setNotificationRead()_

	- ### com.company.awms.controllers.SalaryController
		***(Class)***  
      	**Salary controller, responsible for the requests' getting and posting and their appropriate _methods_, as well as the getter and setter for module management by the admin**
        - **Methods:**
			- ***@GetMapping*** String _getSalary(EmployeeDetails employeeDetails, Model model)_  
			Handles requests for a salary estimate for an employee, calls _salaryService.estimateSalary()_ and responds appropriately
			
	- ### com.company.awms.controllers.ScheduleController
		***(Class)***  
      	**Schedule controller, responsible for the requests' getting and posting and their appropriate _methods_**
        - **Methods:**
			- ***@GetMapping*** String _swapRequest(EmployeeDetails employeeDetails, String receiverNationalID, String requesterDate, String receiverDate)_  
			Send a swap request to the receiver for specific shifts by calling _scheduleService.swapRequest()_
  
			- ***@GetMapping*** String _declineSwapRequest(Model model, String receiverNationalID, EmployeeDetails employeeDetails, String noteNum, String date)_  
			Handles requests for declining a swap request for a user by calling _scheduleService.declineSwap()_ and responds appropriately  
  
			- ***@PostMapping*** String _acceptSwapRequest(Model model, String noteNum, String requesterNationalID, EmployeeDetails employeeDetails, String requesterDate, String receiverDate)_  
			Handles requests for swapping employee shifts by calling _scheduleService.swapEmployees()_ and responds appropriately  

			- ***@PostMapping*** String _addTask(Model model, EmployeeDetails employeeDetails, String data)_  
			Handles requests for a new task if the adder is a manager by calling _scheduleService.addTask()_ and responds appropriately
		
			- ***@GetMapping*** String _markTaskAsComplete(Model model, EmployeeDetails employeeDetails, String taskNum, String date)_  
			Sets task status as complete by calling _scheduleService.markTaskAsComplete()_ and responds appropriately
		
			- ***@GetMapping*** String _viewSchedule(Model model, EmployeeDetails employeeDetails, YearMonth month)_  
			Gets the schedule for a specific month by calling _scheduleService.viewSchedule()_ and responds appropriately
		
	- ### com.company.awms.controllers.AdminController
		***(Class)***  
      	**Admin controller, responsible for the requests' getting and posting and their appropriate _methods_**
        - **Methods:**  

			- ***@GetMapping*** String _getEmployees(EmployeeDetails employeeDetails, Model model)_  
			Gets all the employees by calling _employeeService.getAllEmployees()_

			- ***@PostMapping*** String _registerEmployee(String data, EmployeeDetails employeeDetails, Model model)_  
			Handles register requests by calling _employeeService.registerEmployee()_ and sends appropriate response 
			
			- ***@GetMapping*** String _editEmployee(String employeeID, EmployeeDetails employeeDetails, Model model)_  
			Gets the view where the admin can edit an existing employee

			- ***@GetMapping*** String _registerEmployee(EmployeeDetails employeeDetails, Model model)_  
			Gets the view where the admin can regieter a new employee
			
			- ***@GetMapping*** String _getAllPersonalDocuments(EmployeeDetails employeeDetails, String employeeID, Model model)_  
			Handles requests for the personal documents of an employee by calling _documentService.getPersonalDocumentsInfo()_
			
			- ***@PostMapping*** String _updateEmployeeInfo(String data, String employeeId, EmployeeDetails employeeDetails, Model model)_  
			Handles employee info update requests by calling _employeeService.updateEmployeeInfo()_ and sends appropriate response 
			
			- ***@GetMapping*** String _searchEmployees(String searchTerm, String type, EmployeeDetails employeeDetails, Model model)_  
			Searches in the employees by a specific term of type _type_ by calling _employeeService.searchEmployees()_

			- ***@GetMapping*** String _getLeaves(EmployeeDetails employeeDetails, Model model, String employeeID)_  
			Gets leave information for an employee 

			- ***@PostMapping*** String _approveLeave(EmployeeDetails employeeDetails, Model model, String noteNum, String employeeID, String paid, String startDate, String endDate)_  
			Handles requests for approving a requested leave by calling _employeeService.approveLeave()_
			
			- ***@PostMapping*** String _denyLeave(EmployeeDetails employeeDetails, Model model, String noteNum, String employeeID, String startDate, String endDate)_  
			Denies a requested leave by a specific employee
						
			- ***@GetMapping*** String _denyLeave(String employeeID, String leave)_  
			Deletes a requested leave by a specific employee by calling _employeeService.deleteLeave()_
			
			- ***@GetMapping*** String _addWorkDay(Model model, EmployeeDetails employeeDetails, String employeeNationalID, String date, String startShift, String endShift)_  
			Handles manual work day addition requests by calling _scheduleService.addWorkDay()_
			
			- ***@GetMapping*** String _deleteWorkDay(Model model, EmployeeDetails employeeDetails, String employeeNationalID, String date)_  
			Handles manual work day deletion requests by calling _scheduleService.deleteWorkDay()_
			
			- ***@GetMapping*** String _applySchedule()_  
			Handles manual schedule application requests by calling _scheduleService.scheduleApply()_
			
			- ***@PostMapping*** String _uploa dPersonalDocument(MultipartFile file, String employeeID, EmployeeDetails employeeDetails, Model model)_  
			Handles personal document upload requests by calling _documentsService.uploadPersonalDocument()_
			
			- ***@PostMapping*** String _deletePersonalDocument(int documentID, String ownerID, EmployeeDetails employeeDetails, Model model)_  
			Handles personal document deletion requests by calling _documentsService.deletePersonalDocument()_
			
			- static Map<String, Boolean> _getActivesMethod()_  
			Returns all the modules' active status in a Hashmap object  

			- ***@GetMapping*** String _getActives(EmployeeDetails employeeDetails, Model model)_  
			Handles requests for active modules by calling the _getActivesMethod()_

			- ***@PostMapping*** String _setActives(String updatedActives, EmployeeDetails employeeDetails, Model model)_  
			Handles requests for updating the modules by deserialising the request body data and updating appropriately

			- Map<String, String> _getDeaprtmentDTOs()_  
			Returns all the department DTOs as a HashMap

			- ***@GetMapping*** String _getDeaprtments(EmployeeDetails employeeDetails, Model model)_  
			Handles requests for available departments by calling the _getDepartmentDTOs()_

			- ***@GetMapping*** ResponseEntity _getDeaprtment(String departmentCode, EmployeeDetails employeeDetails)_  
			Handles requests for a specific department by calling the _scheduleService.getDepartment()_ and returning a JSONObject

			- ***@PostMapping*** String_setDeaprtment(EmployeeDetails employeeDetails, Model model, Object departmentObj)_  
			Handles requests for updating a specific department by calling the _scheduleService.setDepartment()_ and responding appropriately

			- ***@PostMapping*** String _deleteDeaprtment(EmployeeDetails employeeDetails, Model model, Object departmentObj)_  
			Handles requests for deleting a specific department and responding appropriately  
	
	- ### com.company.awms.controllers.ContactsController
		***(Class)***  
      	**Contacts controller, responsible for the requests' getting and posting and their appropriate _methods_**
        - **Methods:**  	
			- ***@PostMapping*** String _getContacts(EmployeeDetails employeeDetails, Model model)_  
			Handles requests for injecting the contacts information and responds appropriately  
	

- # **com.company.awms.services**
	*This package contains all the services, which are run by the software. All services have a private reference to their repos.*

	- ### com.company.awms.services.DocumentService
		***(Class)***  
      	**Main document backend logic, responsible for the executing the neccessary algorithms and actions, running in the system with their appropriate _methods_.**
        - **Methods:**  

			- Employee _getEmployee(String employeeID)_  
			Ease-of-acces method for retreiving an employee from the database	

			- List _getAccessibleDocumentsInfo(String employeeID)_  
			Gets the employee access level from the Employee object. S for loop iterates over the levels upto the given one and adds the documents, that match that level to the _List accessibleDocumentsinfo_. If the user is admin, all documents are accessible. Returns that _List_
			
			- List _getPersonalDocumentsInfo(String employeeID)_  
			Gets all the employee's personal documents and creates DTO's that are returned in the _List personalDocumentsInfo_
			
			- Doc _createNewDoc(MultipartFile file, String ownerID, Employee owner)_  
			Creates and returns a new Doc object for ease-of-access
			
			- void _uploadPublicDocument(MultipartFile file, String uploaderID)_  
			Creates and saves a new public Doc object in the database

			- void _uploadPersonalDocument(MultipartFile file, String ownerID)_
			Creates and saves a new personal Doc object for a user in the database
			 
			- Doc _downloadPublicDocument(String documentID, String downloaderID)_
			Tries to download the _Doc_ object through the document ID if it is accessible or the user is an Admin. If successful, it adds the downloader ID into the list of downloaders and re-uploads it. Returns the document.

			- Doc _downloadPersonalDocument(int documentID, String ownerID, String downloaderID)_
			Tries to download the _Doc_ object through the document ID if it is accessible or the user is an Admin. Returns the document.

			- Doc _deletePublicDocument(String documentID, String employeeID)_
			Tries to delete the _Doc_ object through the document ID if it is accessible or the user is an Admin
			
			- Doc _deletePersonalDocument(int documentID, String employeeID)_
			Tries to delete the _Doc_ object through the document ID if it is accessible or the user is an Admin and re-saves the user
			
			- List _searchInDocumentsByName(List<DocInfoDTO> documents, String name)_
			Searches for the matching accessible documents through the pattern and matcher and returns the results
			
			- boolean _isAccessible(String department, int level, String employeeID)_
			Determines whether a document is accessible by a user
	
	- ### com.company.awms.services.EmployeeService
		***(Class)***  
      	**Main employee actions backend logic, responsible for the executing the neccessary algorithms and running in the system with their appropriate _methods_.**
        - **Methods:**

			- Employee _getEmployee(String employeeID)_  
			Ease-of-acces method for retreiving an employee from the database	

			- List _getAllEmployees()_  
			Retrieves all employees from the database	
	
			- List _searchEmployees(List<Employee> employees, String searchTerm, String type)_  
			Runs a search on the employees for a _searchTerm_ by the _type_ criteria and returns the results
	
			- void _addLeave(String employeeID, LocalDate start, LocalDate end, boolean paid)_  
			Creates a new paid/unpaid leave and adds it to the employee object, and updates the database  

			- void _requestSwap(String requestorID, LocalDate date, Strign message)_

			- Employee _getOwner()_  
			Ease-of-acces method for retreiving the owner from the database	

			- List _getManagers()_  
			Ease-of-acces method for retreiving the managers from the database	

			- Boolean _requestLeave(String employeeID, boolean paid, String startDate, String endDate)_  
			Send a notification to the admin, containing the leave request information and returns the _paid_ value	

			- void _approveLeave(String employeeID, boolean paid, String startDateStr, String endDateStr)_  
			Send a notification to the user that requested the leave that it has been approved and adds a new leave to the employee's _leaves_ list

			- void _denyLeave(String employeeID, String startDateStr, String endDateStr)_  
			Send a notification to the user that requested the leave that it has been denied

			- void _deleteLeave(String employeeID, String leave)_  
			Send a notification to the user that requested the leave that it has been removed and deletes the leave from the employee's _leave_ list

			- void _setNotificationRead(String employeeID, int notificationNumber)_  
			Sets the notification as read, so it doesn't come up anymore

			- Employee _registerEmployee(String data)_  
			Registers a new employee with his national ID as the default password and the _data_ field as his information. Sends a notification

			- Employee _updatePassword(String newPassword, String employeeID)_  
			Updates an employee's password

			- Employee _updateSalary(double newSalary, String employeeID)_  
			Updates an employee's salary

			- Employee _updateEmployeeInfo(String employeeID, String data)_  
			Updates an employee's information through the _data_ field and sends a notification

			- Employee _setEmployeeInfo(String employeeID, String data)_  
			Initialises an employee's information through the _data_ field

			- void _notify(String employeeID, String message, boolean searchByNationalID)_  
			Send a notification of type _plain_ to the employee with a message

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
			
			- List _getAllAnsweredThreads()  
			Finds all the threads that have been answered from the database
			
			- List _getAllUnansweredThreads()  
			Finds all the threads that have not been answered from the database
			
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
			
			- void editThread(ForumThread newForumThread, String oldThreadID)
            		Updates the thread with _oldThreadID_ with the new information from  _newForumThread_
			
	- ### com.company.awms.services.SalaryService
		***(Class)***  
      	**Main salary module backend logic, responsible for the executing the neccessary algorithms and actions, running in the system with their appropriate _methods_.**
        - **Methods:**
			- double _getTaskRewardBonus(Employee employee)_  
			Runs through all the DOM until the current. Locates the _Day_ objects for them, and the _EDR_'s for each day. Finds the desired _EDR_ through the _nationalID_ and gets all of its tasks. If a task is marked as completed and not paid for, it is marked paid for and its reward is aggregated into _taskRewards_, which is ultimately returned by the method
			
			- double _calculateWorkHours(Employee employee)_  
			Runs through all the DOM until the current. Locates the _Day_ objects for them, and the _EDR_'s for each day. Finds the desired _EDR_ through the _nationalID_ and aggregates its work time into _hours_, which is ultimately returned by the method
			
			- double _estimateSalary(Employee employee)_  
			Aggregates the values of the work hours, multiplied by the _payPerHour_ and the rewards bonuses from the tasks and returns the estimate salary.  
			
	-  ### com.company.awms.services.ScheduleService
		***(Class)***  
      	**Main schedule module backend logic, responsible for the executing the neccessary algorithms and actions, running in the system with their appropriate _methods_.**
        - **Methods:**  

			- Day _getDay(LocalDate date)_  
			Ease-of-acces method for retreiving a Day from the database	

			- void _addWorkDay(String employeeNationalID, String dateStr, boolean onCall, String startShiftStr, String endShiftStr)_  
			Tries to get the employee from the DB. If successfull, makes a new _EDR_ object from it. Tries to find the _Day_ from the DB. Adds the new _EDR_ to the _Day.employees_ list and updates the _Day_ in the DB
			
			- void _deleteWorkDay(String employeeNationalID, String date)_  
			Tries to get the EDR in the date from the DB. If successfull, removes it and updates the Day
			
			- void _declineSwap(String employeeID, LocalDate receiverDate)_  
			Sends a notification to the receiver that his swap request has been declined 
			
			- void _swapEmployees(String requesterNationalID, String receiverNationalID, String requesterDateParam, String receiverDateParam)  
			Tries to get the _EDR_'s form the specific days. If successfull, swaps their work times to preserve the daily work load, then swaps the whole _EDR_'s and updates the _DAY_s in the database. Returns true if successfull
			
			- void _swapRequest(String requesterID, String receiverNationalID, String requesterDateParam, String receiverDateParam)  
			Tries to find the employees from the database. If successful, sends a swap notification to the receiver
			
			- boolean _addTask(String data)_  
			Tries to get the _Day_ and _EDR_ from that day form the DB. If successful, creates a new _Task_ and loads the information from _data_ into it. Saves the updated _Day_ into the DB and sens a notification to the receiver
			
			- void _markAsComplete(String employeeID, String taskNum, String dateStr)_  
			Finds the task for the specific employee through the _taskNum_ and marks it's _complete_ variable as true. the managers receive a notification 
  
			- void _addMonthlyDays()_  
			Adds next month's _Day_ objects in the database

			- void _clearMonthlyDays()_  
			Clears next month's _Day_ objects from the database
			
			- void _removeReadNotifications()_  
			Removes all read notifications from all the employees

			- ***SCHEDULED ON THE FIRST OF EACH MONTH** void _applySchedule()_  
			Runs the above three methods to refresh all the days and employees. Based on the _departments.json_ file, runs through all the departments and ditributes the schedule through the appropriate algorithms

			- List[] _viewSchedule(Employee viewer, YearMonth month)_  
			Iterates over all the dates for the _month_. Finds the _Day_ and all _EDR_ objects from it. Aggregates all _EDR_'s with equal and lower access levels. Returns that list  

			- List[] _viewTasks(Employee employee, YearMonth month)_  
			Iterates through all the tasks for an _employee_ for a _month_. Returns the List[]

			- boolean _applyOnCallSchedule(String department, int level)_  
			Gets all employees from the specified accessLevel. Parses the _departments.json_ configuration file and finds the current department. Distributes shifts according to number of employees and shifts needed. Applies the third scheduling algorithm to that department, if it is specified in the file.  
  
			- boolean _applyIrregularSchedule(String department, int level)_  
			Loads all employees from the specified accessLevel. Parses the _departments.json_ configuration file and finds the current department. Applies the second scheduling algorithm to that department, if it is specified in the file. 
  
			- boolean _applyRegularSchedule(String department, int level)_  
			Loads all employees from the specified accessLevel. Parses the _departments.json_ configuration file and finds the current department. Applies the first scheduling algorithm to that department, if it is specified in the file. 
			
			- boolean _isLeaveDay(Employee employee, Day day)_  
			Checks if a day is marked as a paid/unpaid leave day for en employee  
  
			- JSONObject _getDepartmentAtLevel(String department, int level)_  
			Returns the _department_ JSONObject at a specific _level_

			- JSONObject _getDepartment(String department)_  
			Returns the _department_ JSONObject

			- void _setDepartment(Object departmentObj)_  
			Updates the department information through the _departmentObj_. Relocates department codes iuff necessary but preserves the already existing departmentCode-name connections

			- void_deleteDepartment(String departmentCode)_  
			Deletes the department from the _departments.json_ file
  
