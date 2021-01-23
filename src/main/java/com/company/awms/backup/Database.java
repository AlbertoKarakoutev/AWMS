package com.company.awms.backup;

import com.company.awms.data.documents.Doc;
import com.company.awms.data.employees.Employee;
import com.company.awms.data.forum.ForumReply;
import com.company.awms.data.forum.ForumThread;
import com.company.awms.data.schedule.Day;

import java.util.List;

public class Database {

    private List<Doc> documents;
    private List<ForumThread> threads;
    private List<ForumReply> replies;
    private List<Employee> employees;
    private List<Day> days;

    Database(List<Doc> documents, List<ForumThread> threads, List<ForumReply> replies, List<Employee> employees,
             List<Day> days) {
        this.documents = documents;
        this.threads = threads;
        this.replies = replies;
        this.employees = employees;
        this.days = days;
    }

    public List<Doc> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Doc> documents) {
        this.documents = documents;
    }

    public List<ForumThread> getThreads() {
        return threads;
    }

    public void setThreads(List<ForumThread> threads) {
        this.threads = threads;
    }

    public List<ForumReply> getReplies() {
        return replies;
    }

    public void setReplies(List<ForumReply> replies) {
        this.replies = replies;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }
}
