package com.company.awms.modules.base.forum;

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

import com.company.awms.modules.base.employees.EmployeeService;
import com.company.awms.modules.base.employees.data.Employee;
import com.company.awms.modules.base.employees.data.Notification;
import com.company.awms.modules.base.forum.data.ForumThread;
import com.company.awms.modules.base.forum.data.ThreadReplyDTO;
import com.company.awms.security.EmployeeDetails;

@Controller
@RequestMapping("/forum")
public class ForumController {

	private ForumService forumService;
	private EmployeeService employeeService;

	@Autowired
	public ForumController(ForumService forumService, EmployeeService employeeService) {
		this.forumService = forumService;
		this.employeeService = employeeService;
	}

	@GetMapping("")
	public String getAccessibleThreads(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			List<ForumThread> threads = forumService.getAccessibleThreads(employeeDetails.getID());
			model.addAttribute("threads", threads);

			return "base/forum/forum";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/notFound";
		}
	}

	@GetMapping("/thread/{threadID}")
	public String getThreadWithReplies(@PathVariable String threadID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			ThreadReplyDTO threadAndReplies = forumService.getThreadWithRepliesByID(threadID, employeeDetails.getID());
			model.addAttribute("thread", threadAndReplies.getForumThread());
			model.addAttribute("replies", threadAndReplies.getForumReplies());

			return "base/forum/thread";
		} catch (IOException e) {
			return "errors/notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@GetMapping("answered")
	public String getAllAnsweredThreads(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			List<ForumThread> threads = forumService.getAllAnsweredThreads(employeeDetails.getID());
			model.addAttribute("threads", threads);

			return "base/forum/forum";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@GetMapping("unanswered")
	public String getAllUnansweredThreads(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			List<ForumThread> threads = forumService.getAllUnansweredThreads(employeeDetails.getID());
			model.addAttribute("threads", threads);

			return "base/forum/forum";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@GetMapping("/employee/threads/{employeeID}")
	public String getAllThreadsFromEmployee(@PathVariable String employeeID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			List<ForumThread> threads = forumService.getAllThreadsFromEmployee(employeeID);
			model.addAttribute("threads", threads);

			return "base/forum/forum";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@GetMapping("/thread/new")
	public String newThread(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		return "base/forum/newThread";
	}

	@GetMapping("/thread/{threadID}/edit")
	public String getThreadEdit(@PathVariable String threadID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			ForumThread thread = forumService.getThread(threadID);
			model.addAttribute("thread", thread);

			return "base/forum/newThread";
		} catch (IOException e) {
			return "errors/notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@GetMapping("/thread/{threadID}/reply/new")
	public String newReply(@PathVariable String threadID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			ForumThread thread = forumService.getThread(threadID);

			model.addAttribute("threadID", threadID);
			model.addAttribute("threadTitle", thread.getTitle());

			return "base/forum/threadReply";
		} catch (IOException e) {
			return "errors/notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@PostMapping(value = "/add")
	public String addThread(@RequestParam String title, @RequestParam String body, @RequestParam boolean limitedAccess, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			ForumThread forumThread = this.forumService.addNewThread(employeeDetails, title, body, limitedAccess);

			model.addAttribute("thread", forumThread);

			return "base/forum/thread";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@PostMapping(value = "/thread/{threadID}/add")
	public String addReply(@RequestParam String body, @PathVariable String threadID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			forumService.addNewReply(employeeDetails, body, threadID);
			ThreadReplyDTO threadAndReplies = forumService.getThreadWithRepliesByID(threadID, employeeDetails.getID());
			model.addAttribute("thread", threadAndReplies.getForumThread());
			model.addAttribute("replies", threadAndReplies.getForumReplies());

			return "base/forum/thread";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@PostMapping(value = "/thread/{threadID}/answered")
	public String markThreadAsAnswered(@PathVariable String threadID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			ForumThread forumThread = forumService.getThread(threadID);

			if (!employeeDetails.getID().equals(forumThread.getIssuerID())) {
				return "errors/notAuthorized";
			}

			forumService.markAsAnswered(forumThread);
			model.addAttribute("thread", forumThread);

			return "base/forum/thread";
		} catch (IOException e) {
			return "errors/notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalSeverError";
		}
	}

	@PostMapping(value = "/thread/{oldThreadID}/edit")
	public String editThread(@RequestParam String title, @RequestParam String body, @PathVariable String oldThreadID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			ForumThread oldThread = forumService.getThread(oldThreadID);

			if (!oldThread.getIssuerID().equals(employeeDetails.getID())) {
				return "errors/notAuthorized";
			}

			oldThread = forumService.editThread(body, title, oldThread);

			ThreadReplyDTO threadAndReplies = forumService.getThreadWithRepliesByID(oldThreadID, employeeDetails.getID());
			model.addAttribute("thread", oldThread);
			model.addAttribute("replies", threadAndReplies.getForumReplies());

			return "base/forum/thread";
		} catch (IOException e) {
			return "errors/notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@GetMapping("/search")
	public String searchForum(@RequestParam String searchTerm, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			List<ForumThread> threads = forumService.getAccessibleThreads(employeeDetails.getID());
			List<ForumThread> foundThreads = forumService.searchForum(threads, searchTerm);
			model.addAttribute("threads", foundThreads);

			return "base/forum/forum";
		} catch (IOException e) {
			return "errors/notFound";
		} catch (Exception e) {
			return "errors/internalServerError";
		}
	}

	@GetMapping("/dismiss/{threadID}")
	public String dismiss(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String noteNum, @PathVariable String threadID) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			Notification.setAsRead(employeeService, employeeDetails.getID(), Integer.parseInt(noteNum));
			Employee employee = this.employeeService.getEmployee(employeeDetails.getID());
			model.addAttribute("employee", employee);
			return "redirect:/forum/thread/" + threadID;
		} catch (Exception e) {
			return "errors/internalServerError";
		}
	}

}
