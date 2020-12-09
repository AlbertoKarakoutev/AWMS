package com.company.awms.controllers;

import com.company.awms.data.forum.ForumReply;
import com.company.awms.data.forum.ForumThread;
import com.company.awms.data.forum.ThreadReplyDTO;
import com.company.awms.security.EmployeeDetails;
import com.company.awms.services.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/forum")
public class ForumController {

	private static final boolean active = true;

	private ForumService forumService;

	@Autowired
	public ForumController(ForumService forumService) {
		this.forumService = forumService;
	}

	@GetMapping("")
	public String getAllThreads(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			List<ForumThread> threads = this.forumService.getAllThreads();
			model.addAttribute("threads", threads);
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "forum";
		} catch (Exception e) {
			e.printStackTrace();
			return "notFound";
		}
	}

	@GetMapping("/thread/{threadID}")
	public String getThreadWithReplies(@PathVariable String threadID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			ThreadReplyDTO threadAndReplies = this.forumService.getThreadWithRepliesByID(threadID);
			model.addAttribute("thread", threadAndReplies.getForumThread());
			model.addAttribute("replies", threadAndReplies.getForumReplies());
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "thread";
		} catch (IOException e) {
			return "notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	// maybe this belongs in EmployeeController
	@GetMapping("/employee/threads/{employeeID}")
	public String getAllThreadsFromEmployee(@PathVariable String employeeID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			List<ForumThread> threads = this.forumService.getAllThreadsFromEmployee(employeeID);
			model.addAttribute("threads", threads);
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "threadsFromEmployee";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	@GetMapping("/thread/new")
	public String newThread(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			injectLoggedInEmployeeInfo(model, employeeDetails);
			
			return "newThread";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	// maybe this belongs in EmployeeController
	@GetMapping("/employee/replies/{employeeID}")
	public String getAllRepliesFromEmployee(@PathVariable String employeeID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			List<ForumReply> replies = this.forumService.getAllRepliesFromEmployee(employeeID);
			model.addAttribute("replies", replies);
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "repliesFromEmployee";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	@GetMapping("/thread/{threadID}/reply/new")
	public String newReply(@PathVariable String threadID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			ForumThread thread = this.forumService.getThread(threadID);

			model.addAttribute("threadID", threadID);
			model.addAttribute("threadTitle", thread.getTitle());
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "threadReply";
		} catch (IOException e){
			return "notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	@PostMapping(value = "/add")
	public String addThread(@RequestParam String title, @RequestParam String body, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			ForumThread forumThread = this.forumService.addNewThread(employeeDetails, title, body);

			model.addAttribute("thread", forumThread);
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "thread";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	@PostMapping(value = "/thread/{threadID}/add")
	public String addReply(@RequestParam String body, @PathVariable String threadID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			this.forumService.addNewReply(employeeDetails, body, threadID);
			ThreadReplyDTO threadAndReplies = forumService.getThreadWithRepliesByID(threadID);
			model.addAttribute("thread", threadAndReplies.getForumThread());
			model.addAttribute("replies", threadAndReplies.getForumReplies());
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "thread";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	@PutMapping(value = "/thread/{threadID}/answered")
	public String markThreadAsAnswered(@PathVariable String threadID,
			@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			ForumThread forumThread = this.forumService.getThread(threadID);

			if (!employeeDetails.getID().equals(forumThread.getIssuerID())) {
				return "notAuthorized";
			}

			this.forumService.markAsAnswered(forumThread);
			model.addAttribute("thread", forumThread);
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "thread";
		} catch (IOException e) {
			return "notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalSeverError";
		}
	}

	@PutMapping(value = "/thread/{oldThreadID}/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String editThread(@RequestBody ForumThread newForumThread, @PathVariable String oldThreadID,
			@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		newForumThread.setIssuerID(employeeDetails.getID());
		try {
			ForumThread oldThread = this.forumService.getThread(oldThreadID);

			if (!oldThread.getIssuerID().equals(employeeDetails.getID())) {
				return "notAuthorized";
			}

			oldThread = this.forumService.editThread(newForumThread, oldThread);
			model.addAttribute("thread", oldThread);
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "thread";
		} catch (IOException e) {
			return "notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	private void injectLoggedInEmployeeInfo(Model model, EmployeeDetails employeeDetails){
		model.addAttribute("employeeName", employeeDetails.getFirstName() + " " + employeeDetails.getLastName());
		model.addAttribute("employeeEmail", employeeDetails.getUsername());
		model.addAttribute("employeeID", employeeDetails.getID());
	}

	public static boolean getActive() {
		return active;
	}
}
