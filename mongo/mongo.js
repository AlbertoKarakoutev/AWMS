var mongo = new Mongo();
var awms = mongo.getDB("awms");
admin = {
        "email":"admin@gmail.com",
        "password":"$2a$10$h3RzwGbggqh6nSXZyl0g3O/Svw.XUYr/qA1ASTA6mJPCIP82ezErK",
        "firstName":"Admin",
        "lastName":"Admin",
        "role":"ADMIN",
        "nationalID":"",
        "iban": "",
        "level": 0,
        "department": "",
        "accessLevel": "",
        "phoneNumber": "",
        "salary": 0,
        "payPerHour": 0,
        "workWeek": [0, 0],
        "leaves":[],
        "notifications":[],
        "personalDocuments":[],
        "_class":"com.company.awms.modules.base.employees.data.Employee"
}
days = [];
var now = new Date();
var monthLength = new Date(now.getFullYear(),now.getMonth()+1, 0).getDate();
for(var i = 1; i <= monthLength; i++){
    days[i] = {"date":new Date(now.getFullYear(), now.getMonth(), i),
              "employees": [],
              "_class": "com.company.awms.modules.base.schedule.data.Day"
    }
}
var nextMonth;
var nextMonthYear;
if(now.getMonth()==12){
        nextMonth = 1;
        nextMonthYear = now.getFullYear()+1;
}else{
        nextMonth = now.getMonth()+1;
        nextMonthYear = now.getFullYear();
}
var nextMonthLength = new Date(nextMonthYear, nextMonth+1, 0).getDate();
for(var i = 1; i <= nextMonthLength; i++){
        days[i+monthLength] = {"date":new Date(nextMonthYear, nextMonth, i),
                                "employees": [],
                                "_class": "com.company.awms.modules.base.schedule.data.Day"
                                }
}
baseModules = [];
baseModules[0] = {"name":"schedule", "active":true, "base":true};
baseModules[1] = {"name":"documents", "active":true, "base":true};
baseModules[2] = {"name":"employees", "active":true, "base":true};
baseModules[3] = {"name":"forum", "active":true, "base":true};
baseModules[4] = {"name":"contacts", "active":true, "base":true};

awms.day.drop();
awms.doc.drop();
awms.employee.drop();
awms.forumReply.drop();
awms.forumThread.drop();
awms.department.drop();
awms.module.drop();

awms.createCollection("department", null);
awms.module.insertMany(baseModules);
awms.day.insertMany(days);
awms.createCollection("doc", null);
awms.employee.insertOne(admin);
awms.createCollection("forumReply", null);
awms.createCollection("forumThread", null);
