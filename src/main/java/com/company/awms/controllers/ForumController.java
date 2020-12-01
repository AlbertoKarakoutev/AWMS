package com.company.awms.controllers;

import com.company.awms.data.forum.ForumReply;
import com.company.awms.data.forum.ForumThread;
import com.company.awms.data.forum.ThreadReplyDTO;
import com.company.awms.security.CustomUserDetails;
import com.company.awms.services.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
public class ForumController {

	private static final boolean active = true;

	private ForumService forumService;

	@Autowired
	public ForumController(ForumService forumService) {
		this.forumService = forumService;
	}

	@GetMapping(value = "/forum")
	public String getAllThreads(Model model) {
		try {
			List<ForumThread> threads = this.forumService.getAllThreads();
			model.addAttribute("threads", threads);

			return "forum";
		} catch (Exception e) {
			e.printStackTrace();
			return "notFound";
		}
	}

	@GetMapping(value = "/forum/thread/{threadID}")
	public String getThread(@PathVariable String threadID, Model model) {
		try {
			ForumThread forumThread = this.forumService.getThread(threadID);
			model.addAttribute("thread", forumThread);

			return "thread";
		} catch (IOException e) {
			return "notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	@GetMapping(value = "/forum/thread/{threadID}/replies")
	public String getThreadWithReplies(@PathVariable String threadID, Model model) {
		try {
			ThreadReplyDTO threadAndReplies = this.forumService.getThreadWithRepliesByID(threadID);
			model.addAttribute("thread", threadAndReplies.getForumThread());
			model.addAttribute("replies", threadAndReplies.getForumReplies());

			return "threadAndReplies";
		} catch (IOException e) {
			return "notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	// maybe this belongs in EmployeeController
	@GetMapping(value = "forum/employee/threads/{employeeID}")
	public String getAllThreadsFromEmployee(@PathVariable String employeeID, Model model) {
		try {
			List<ForumThread> threads = this.forumService.getAllThreadsFromEmployee(employeeID);
			model.addAttribute("threads", threads);

			return "threadsFromEmployee";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	// maybe this belongs in EmployeeController
	@GetMapping(value = "forum/employee/replies/{employeeID}")
	public String getAllRepliesFromEmployee(@PathVariable String employeeID, Model model) {
		try {
			List<ForumReply> replies = this.forumService.getAllRepliesFromEmployee(employeeID);
			model.addAttribute("replies", replies);

			return "repliesFromEmployee";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	@PostMapping(value = "/forum/add", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String addThread(@RequestBody ForumThread forumThread, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		if(!forumThread.getIssuerID().equals(userDetails.getID())){
			return "notAuthorized";
		}

		try {
			this.forumService.addNewThread(forumThread);

			model.addAttribute("thread", forumThread);

			return "thread";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	@PostMapping(value = "/forum/thread/{threadID}/add", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String addReply(@RequestBody ForumReply forumReply, @PathVariable String threadID, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		if(!forumReply.getIssuerID().equals(userDetails.getID())){
			return "notAuthorized";
		}
		if(!forumReply.getThreadID().equals(threadID)){
			return "badRequest";
		}
		try {
			this.forumService.addNewReply(forumReply);
			ThreadReplyDTO threadAndReplies = forumService.getThreadWithRepliesByID(threadID);
			model.addAttribute("thread", threadAndReplies.getForumThread());
			model.addAttribute("replies", threadAndReplies.getForumReplies());

			return "threadAndReplies";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	@PutMapping(value = "/forum/thread/{threadID}/answered", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String markThreadAsAnswered(@PathVariable String threadID, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		try {
			ForumThread forumThread = this.forumService.getThread(threadID);

			if(!userDetails.getID().equals(forumThread.getIssuerID())){
				return "notAuthorized";
			}

			this.forumService.markAsAnswered(forumThread);
			model.addAttribute("thread", forumThread);

			return "thread";
		} catch (IOException e) {
			return "notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalSeverError";
		}
	}

	@PutMapping(value = "/forum/thread/{oldThreadID}/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String editThread(@RequestBody ForumThread newForumThread, @PathVariable String oldThreadID, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		if(!newForumThread.getIssuerID().equals(userDetails.getID())){
			return "notAuthorized";
		}
		try {
			ForumThread oldThread = this.forumService.getThread(oldThreadID);

			if(!oldThread.getIssuerID().equals(userDetails.getID())){
				return "notAuthorized";
			}

			oldThread = this.forumService.editThread(newForumThread, oldThread);
			model.addAttribute("thread", oldThread);

			return "thread";
		} catch (IOException e) {
			return "notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	public static boolean getActive() {
		return active;
	}
}
