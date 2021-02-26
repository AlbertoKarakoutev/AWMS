# AWMS - Administrative worker management system
Software, intended for a functional and utilised company worflow. It incomporates an array of useful tools and modules, which help every company employee understand and manage their individual work. The package offers 5 main sub-systems:

# Deployment:
### Please follow the steps to initialize the system correctly!
*   Database - Navigate to the project folder. In the Mongo Shell, run ***load("mongo.js")*** to initialize the database. One administrator account is created with the credentials:
    > e-mail: admin@gmail.com  
    > Password: admin
*   Module Manager - Run the *ModuleManager.jar* file in the system's base directory. Install or remove any modules you wish. The modules should follow the following schematic:
    * Zip Archive: \<ModuleName\>.zip (Uppercase)  
        * Folder: \<ModuleName\> (Uppercase)  
            * Folder: \<moduleName\> (Lowercase)  
                * \<ModuleName\>Controller.java (Uppercase, containing all the endpoints of the network components)  
                * \<ModuleName\>Service.java (Uppercase, containing all the business logic of the module)  
                * \<moduleName\>.jsp (Lowercase, being the JSP view file of the module)
                * (Optional) Folder: data (Containing any repos and POJO classes for the module)  
                __The database would need to be updated if new POJOs and repos are added as a module!__
                __Please ensure that the system is not currently running while working with the Module Manager to avoid bugs and system crashes!__
*   Run - Navigate to the system's base directory. Go into the _target_ folder. Open a shell and run ***java -jar awms-1.0.0.war***     

## Schedule 
*   A system-wide scheduling service, offering functionality for viewing and changing individual work schedules, as well as calculating the optimal company work-day distribution. It encorporates the department-level(DL) limitations which filter the viewable and editable values of the programme depending on the employee DL status.

## Documents
*   A centralized document database and repository, working as a company archive. It is DL-limited and presents users with the option to view, upload and download specific work related files.

## Forum
*   The forum encapsulates user discussions, equiped with thread information and the functionallity to view and answer on specific topics. It is a company gathering point, which lets employees get their questions answered and help their coleagues.
    
## Employee
*   Employees have a unique system page, containing all the key information they need, and have in the system, so that it allows for an easier access if needed. Additionally, it contains all personal company-related documents at their disposal.
    
.  
.  
. 
  
# The system offers administrative functionallity. Pages contain useful tools, through which the system administrator can perform appropriate CRUD operations, in order to fix or resolve issues from within the software.

## departments.json

**There are several properties in each department as explained below:**
*   **String** _name_ - The name of the department for the specific department code  
  
*   **boolean** _universalSchedule_ - Specifies whether the schedule applies to all levels from this department, or is different for each of them  
  
*   **String** _scheduleType_ - ***Explained in the section below this one***  
  
*   **int[4]** _dailyHours_ - Specifies the daily restrictions for working, in the sense that shifts can only be taken in that interval (i.e. when the department/level is operational)  
  
*   **int** _shiftLength_ - The length of the work day in that department/level  
  
*   **boolean** _workOnWeekends_ - Specifies whether employees from this department/level take shifts on the weekends  
  
*   **int** _breakBetweenDays_ - Specifies the minimal amount (in hours) that employees must be allowed to rest between shifts  
  
*   **int** _workloadRequirement_ - Specifies the required combined work hours per day (_workloadRequirement = employeesPerDay * shiftlength_)  
  
*   **int** _dailyBreakDurationTotal_ - Specifies the accumulated break time per one day for that department/level  
  
*   **int** _monthlyWorkDays_ - Specifies the total work days for the month, so the OnCall schedule can distribute accordingly  
  
.  
.  
.  
  
## Schedule Types  
  
***!Work week refers to the cyclic ratio of work/break days of an employee!***  
  
***!Due to different date formats, MongoDB displays times 2 hours behind, and dates 1 day behind. This does not impact normal system function, but is misleading to the eyes. No attempt to fix is should be made, as the system is working fine.***  
  
**In order to provide a better and more equipped service, the scheduling is divided into three main types. Depending on the type of department/level, each one can be selected in order to best suit the needs of the workers and the company.**  
  
### Types:
*   ***Regular*** - This scheduling type is modelled after the every-day work ethic of the regular person. The work week is in a 5/2 ratio, and the work hours per day are constant. Weekends are off. Thre previous month is not taken into account.  
  
*   ***Irregular*** - This scheduling type is more appropriate for a specific set of professions. It consists of an irregular work week (e.g. 3/2, 2/2, 4/1, etc.). The daily work hours are a constant. The previous month is taken into account, in order to properly continue the employee work or break period.  
  
*   ***OnCall*** - This scheduling type is entirely reliant on the company requirement work work completed. It is highly dependent of the amount of people in a certain department/level, as more workers would mean less woek for each of them. This scheduling type is restricted by the limitations of the law, regarding the _breakBetweenDays_ and does not have a particular work week/shiftLength schema. 
