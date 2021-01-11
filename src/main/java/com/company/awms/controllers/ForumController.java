package com.company.awms.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.forum.ForumReply;
import com.company.awms.data.forum.ForumThread;
import com.company.awms.data.forum.ThreadReplyDTO;
import com.company.awms.security.EmployeeDetails;
import com.company.awms.services.EmployeeService;
import com.company.awms.services.ForumService;

@Controller
@RequestMapping("/forum")
public class ForumController {

	private static final boolean active = true;

	private ForumService forumService;
	private EmployeeService employeeService;

	@Autowired
	public ForumController(ForumService forumService, EmployeeService employeeService) {
		this.forumService = forumService;
		this.employeeService = employeeService;
	}

	@GetMapping("")
	public String getAllThreads(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		if (!active) {
			return "notFound";
		}

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
		if (!active) {
			return "notFound";
		}

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

	@GetMapping("answered")
	public String getAllAnsweredThreads(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		if (!active) {
			return "notFound";
		}

		try {
			List<ForumThread> threads = this.forumService.getAllAnsweredThreads();
			model.addAttribute("threads", threads);
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "forum";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	@GetMapping("unanswered")
	public String getAllUnansweredThreads(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		if (!active) {
			return "notFound";
		}

		try {
			List<ForumThread> threads = this.forumService.getAllUnansweredThreads();
			model.addAttribute("threads", threads);
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "forum";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	// maybe this belongs in EmployeeController
	@GetMapping("/employee/threads/{employeeID}")
	public String getAllThreadsFromEmployee(@PathVariable String employeeID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		if (!active) {
			return "notFound";
		}

		try {
			List<ForumThread> threads = this.forumService.getAllThreadsFromEmployee(employeeID);
			model.addAttribute("threads", threads);
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "forum";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	@GetMapping("/thread/new")
	public String newThread(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		if (!active) {
			return "notFound";
		}

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
		if (!active) {
			return "notFound";
		}

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

	// Forum edit thread
	@GetMapping("/thread/{threadID}/edit")
	public String getThreadEdit(@PathVariable String threadID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		if (!active) {
			return "notFound";
		}

		try {
			ForumThread thread = this.forumService.getThread(threadID);

			model.addAttribute("thread", thread);

			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "newThread";
		} catch (IOException e) {
			return "notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	@GetMapping("/thread/{threadID}/reply/new")
	public String newReply(@PathVariable String threadID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		if (!active) {
			return "notFound";
		}

		try {
			ForumThread thread = this.forumService.getThread(threadID);

			model.addAttribute("threadID", threadID);
			model.addAttribute("threadTitle", thread.getTitle());
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "threadReply";
		} catch (IOException e) {
			return "notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	@PostMapping(value = "/add")
	public String addThread(@RequestParam String title, @RequestParam String body, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		if (!active) {
			return "notFound";
		}

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
		if (!active) {
			return "notFound";
		}

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

	@PostMapping(value = "/thread/{threadID}/answered")
	public String markThreadAsAnswered(@PathVariable String threadID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		if (!active) {
			return "notFound";
		}

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

	@PostMapping(value = "/thread/{oldThreadID}/edit")
	public String editThread(@RequestParam String title, @RequestParam String body, @PathVariable String oldThreadID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		if (!active) {
			return "notFound";
		}

		try {
			ForumThread oldThread = this.forumService.getThread(oldThreadID);

			if (!oldThread.getIssuerID().equals(employeeDetails.getID())) {
				return "notAuthorized";
			}

			oldThread = this.forumService.editThread(body, title, oldThread);

			ThreadReplyDTO threadAndReplies = this.forumService.getThreadWithRepliesByID(oldThreadID);
			model.addAttribute("thread", oldThread);
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

	private void injectLoggedInEmployeeInfo(Model model, EmployeeDetails employeeDetails) throws IOException {
		model.addAttribute("employeeName", employeeDetails.getFirstName() + " " + employeeDetails.getLastName());
		model.addAttribute("employeeEmail", employeeDetails.getUsername());
		model.addAttribute("employeeID", employeeDetails.getID());
		Employee user = employeeService.getEmployee(employeeDetails.getID());
		int unread = 0;
		for(int i = 0; i < user.getNotifications().size(); i++) {
			if(!user.getNotifications().get(i).getRead()) {
				unread++;
			}
		}
		model.addAttribute("notifications", user.getNotifications());
		model.addAttribute("unread", unread);
	}

	@GetMapping("/dismiss/{threadID}")
	public String dismiss(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String noteNum, @PathVariable String threadID) {
		try{
			employeeService.setNotificationRead(employeeDetails.getID(), Integer.parseInt(noteNum));
			injectLoggedInEmployeeInfo(model, employeeDetails);
			Employee employee = this.employeeService.getEmployee(employeeDetails.getID());
            model.addAttribute("employee", employee);
            return "redirect:/forum/thread/"+threadID;
		}catch(Exception e) {
			return "internalServerError";
		}
	}
	
	public static boolean getActive() {
		return active;
	}
}
