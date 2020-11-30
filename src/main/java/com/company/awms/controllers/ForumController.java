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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class ForumController {

	private static final boolean active = true;

	private ForumService forumService;

	@Autowired
	public ForumController(ForumService forumService) {
		this.forumService = forumService;
	}

	@GetMapping(value = "/forum")
	public ResponseEntity<List<ForumThread>> getAllThreads(@AuthenticationPrincipal CustomUserDetails userDetails) {
		// Get the userDetails of the currentlyLoggedInUser. Just testing
		System.out.println(userDetails.getAuthorities().toArray()[0]);

		try {
			List<ForumThread> threads = this.forumService.getAllThreads();

			return new ResponseEntity<>(threads, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping(value = "/forum/thread/{threadID}")
	public ResponseEntity<ForumThread> getThread(@PathVariable String threadID) {
		try {
			ForumThread forumThread = this.forumService.getThread(threadID);

			return new ResponseEntity<>(forumThread, HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/forum/thread/{threadID}/replies")
	public ResponseEntity<ThreadReplyDTO> getThreadWithReplies(@PathVariable String threadID) {
		try {
			ThreadReplyDTO threadAndReplies = this.forumService.getThreadWithRepliesByID(threadID);

			return new ResponseEntity<>(threadAndReplies, HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// maybe this belongs in EmployeeController
	@GetMapping(value = "forum/employee/threads/{employeeID}")
	public ResponseEntity<List<ForumThread>> getAllThreadsFromEmployee(@PathVariable String employeeID) {
		try {
			List<ForumThread> threads = this.forumService.getAllThreadsFromEmployee(employeeID);

			return new ResponseEntity<>(threads, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// maybe this belongs in EmployeeController
	@GetMapping(value = "forum/employee/replies/{employeeID}")
	public ResponseEntity<List<ForumReply>> getAllRepliesFromEmployee(@PathVariable String employeeID) {
		try {
			List<ForumReply> replies = this.forumService.getAllRepliesFromEmployee(employeeID);

			return new ResponseEntity<>(replies, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping(value = "/forum/add", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addThread(@RequestBody ForumThread forumThread) {
		// TODO:
		// Authenticate that current user is the same as the issuerId from forumThread.
		// If not return 401 Not Authorized
		try {
			this.forumService.addNewThread(forumThread);

			return new ResponseEntity<>("Added new Thread", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/forum/thread/{threadID}/add", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addReply(@RequestBody ForumReply forumReply, @PathVariable String threadID) {
		// TODO:
		// Authenticate that current user is the same as the issuerId from forumReply.
		// If not return 401 Not Authorized
		try {
			this.forumService.addNewReply(forumReply);

			return new ResponseEntity<>("Added new Reply", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping(value = "/forum/thread/{threadID}/answered", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> markThreadAsAnswered(@PathVariable String threadID) {
		// TODO:
		// Authenticate that current user is the same as the issuerId from forumThread.
		// If not return 401 Not Authorized
		try {
			this.forumService.markAsAnswered(threadID);

			return new ResponseEntity<>("Thread set to answered", HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping(value = "/forum/thread/{oldThreadID}/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> editThread(@RequestBody ForumThread newForumThread, @PathVariable String oldThreadID) {
		// TODO:
		// Authenticate that current user is the same as the issuerId from
		// newForumThread. If not return 401 Not Authorized
		try {
			this.forumService.editThread(newForumThread, oldThreadID);

			return new ResponseEntity<>("Edited Thread", HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	public static boolean getActive() {
		return active;
	}
}
