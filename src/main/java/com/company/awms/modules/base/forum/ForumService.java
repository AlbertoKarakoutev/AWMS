package com.company.awms.modules.base.forum;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.awms.modules.base.employees.data.Employee;
import com.company.awms.modules.base.employees.data.EmployeeRepo;
import com.company.awms.modules.base.employees.data.Notification;
import com.company.awms.modules.base.forum.data.ForumReply;
import com.company.awms.modules.base.forum.data.ForumReplyRepo;
import com.company.awms.modules.base.forum.data.ForumThread;
import com.company.awms.modules.base.forum.data.ForumThreadRepo;
import com.company.awms.modules.base.forum.data.ThreadReplyDTO;
import com.company.awms.security.EmployeeDetails;
import com.company.awms.util.ForumComparator;

@Service
public class ForumService {
	private ForumThreadRepo forumThreadRepo;
	private ForumReplyRepo forumReplyRepo;
	private EmployeeRepo employeeRepo;

	@Autowired
	public ForumService(ForumThreadRepo forumThreadRepo, ForumReplyRepo forumReplyRepo, EmployeeRepo employeeRepo) {
		this.forumThreadRepo = forumThreadRepo;
		this.forumReplyRepo = forumReplyRepo;
		this.employeeRepo = employeeRepo;
	}

	public ForumThread getThread(String threadID) throws IOException {
		Optional<ForumThread> thread = this.forumThreadRepo.findById(threadID);

		if (thread.isEmpty()) {
			throw new IOException("Thread not found!");
		}

		return thread.get();
	}

	public ThreadReplyDTO getThreadWithRepliesByID(String threadID) throws IOException {
		ForumThread thread = getThread(threadID);

		List<ForumReply> replies = this.forumReplyRepo.findByThreadID(threadID);

		return new ThreadReplyDTO(thread, replies);
	}

	public List<ForumThread> getAllThreads() {
		List<ForumThread> allThreads = this.forumThreadRepo.findAll();
		allThreads.sort(new ForumComparator());

		return allThreads;
	}

	public List<ForumThread> getAllAnsweredThreads() {
		List<ForumThread> allThreads = getAllThreads();
		allThreads.removeIf(t -> !t.getAnswered());

		return allThreads;
	}

	public List<ForumThread> getAllUnansweredThreads() {
		List<ForumThread> allThreads = getAllThreads();
		allThreads.removeIf(ForumThread::getAnswered);

		return allThreads;
	}

	public List<ForumThread> getAllThreadsFromEmployee(String issuerID) {
		List<ForumThread> employeeThreads = this.forumThreadRepo.findByIssuerID(issuerID);
		employeeThreads.sort(new ForumComparator());

		return employeeThreads;
	}

	public List<ForumReply> getAllRepliesFromEmployee(String issuerID) {
		return this.forumReplyRepo.findByIssuerID(issuerID);
	}

	public ForumThread addNewThread(EmployeeDetails employeeDetails, String title, String body) {
		ForumThread newThread = new ForumThread(employeeDetails.getID(), body, title, LocalDateTime.now(), false, employeeDetails.getFirstName() + " " + employeeDetails.getLastName());

		Employee uploader = employeeRepo.findById(employeeDetails.getID()).get();

		List<Employee> sameDepartmentEmployees = employeeRepo.findByDepartment(uploader.getDepartment());
		
		this.forumThreadRepo.save(newThread);

		for (Employee admin : employeeRepo.findAllByRole("ADMIN")) {
			if (admin.getID().equals(uploader.getID())) {
				sameDepartmentEmployees = employeeRepo.findAll();
				break;
			}
		}

		ForumThread saved = null;
		for (ForumThread thread : forumThreadRepo.findByIssuerID(uploader.getID())) {
			if (thread.getAnswered() == false && thread.getTitle().equals(newThread.getTitle()) && thread.getBody().equals(newThread.getBody())) {
				saved = thread;
				break;
			}
		}
		String message = uploader.getFirstName() + " " + uploader.getLastName() + " has uploaded a new topic in the Forum, called \"" + saved.getTitle() + "\".";

		for (Employee notified : sameDepartmentEmployees) {
			new Notification(message).add("new-thread").add(uploader.getID()).add(saved.getID()).sendAndSave(notified, employeeRepo);
		}

		return newThread;
	}

	public void addNewReply(EmployeeDetails employeeDetails, String body, String threadID) {
		ForumReply newReply = new ForumReply(threadID, employeeDetails.getID(), body, LocalDateTime.now(), employeeDetails.getFirstName() + " " + employeeDetails.getLastName());

		Employee replier = employeeRepo.findById(employeeDetails.getID()).get();
		ForumThread answered = forumThreadRepo.findById(threadID).get();
		
		String message = replier.getFirstName() + " " + replier.getLastName() + " has added a reply on the thread \"" + answered.getTitle() + "\" you uploaded.";
		Employee issuer = employeeRepo.findById(answered.getIssuerID()).get();
		new Notification(message).add("new-reply").add(replier.getID()).add(newReply).sendAndSave(issuer, employeeRepo);

		forumReplyRepo.save(newReply);
	}

	public void markAsAnswered(ForumThread forumThread) {
		forumThread.setAnswered(true);
		forumThreadRepo.save(forumThread);
	}

	public ForumThread editThread(String body, String title, ForumThread oldThread) {
		// We don't update the issuerID, time and isAnswered because they are presumed
		// to be the same.
		oldThread.setBody(body);
		oldThread.setTitle(title);
		oldThread.setDateTime(LocalDateTime.now());

		this.forumThreadRepo.save(oldThread);

		return oldThread;
	}
}
