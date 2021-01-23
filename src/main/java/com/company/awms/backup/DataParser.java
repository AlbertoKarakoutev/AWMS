package com.company.awms.backup;

import com.company.awms.data.documents.DocumentRepo;
import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeRepo;
import com.company.awms.data.forum.ForumReply;
import com.company.awms.data.forum.ForumReplyRepo;
import com.company.awms.data.forum.ForumThread;
import com.company.awms.data.documents.Doc;
import com.company.awms.data.forum.ForumThreadRepo;
import com.company.awms.data.schedule.Day;
import com.company.awms.data.schedule.ScheduleRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataParser {

    private DocumentRepo documentRepo;
    private EmployeeRepo employeeRepo;
    private ForumReplyRepo forumReplyRepo;
    private ForumThreadRepo forumThreadRepo;
    private ScheduleRepo scheduleRepo;
    private Database database;

    @Autowired
    public DataParser(DocumentRepo documentRepo, EmployeeRepo employeeRepo, ForumReplyRepo forumReplyRepo,
                      ForumThreadRepo forumThreadRepo, ScheduleRepo scheduleRepo) {
        this.documentRepo = documentRepo;
        this.employeeRepo = employeeRepo;
        this.forumReplyRepo = forumReplyRepo;
        this.forumThreadRepo = forumThreadRepo;
        this.scheduleRepo = scheduleRepo;
    }

    public String parseToJSON() throws JsonProcessingException {
        List<ForumThread> threads = this.forumThreadRepo.findAll();
        List<ForumReply> replies = this.forumReplyRepo.findAll();
        List<Doc> docs = this.documentRepo.findAll();
        List<Employee> employees = this.employeeRepo.findAll();
        List<Day> days = this.scheduleRepo.findAll();

        this.database = new Database(docs, threads, replies, employees, days);

        ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(this.database);
    }

    public void parseFromJSON(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        this.database = mapper.readValue(json, Database.class);

        for (ForumThread thread : this.database.getThreads()){
            this.forumThreadRepo.save(thread);
        }
        for (ForumReply reply : this.database.getReplies()){
            this.forumReplyRepo.save(reply);
        }
        for (Doc document : this.database.getDocuments()){
            this.documentRepo.save(document);
        }
        for (Employee employee : this.database.getEmployees()){
            this.employeeRepo.save(employee);
        }
        for (Day day : this.database.getDays()){
            this.scheduleRepo.save(day);
        }
    }
}
