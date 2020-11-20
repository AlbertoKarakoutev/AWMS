package com.company.awms.data.forum;

import java.util.List;

public class ThreadReplyDTO {

    private ForumThread forumThread;
    private List<ForumReply> forumReply;

    public ThreadReplyDTO(ForumThread forumThread, List<ForumReply> forumReply) {
        this.forumThread = forumThread;
        this.forumReply = forumReply;
    }

    public ForumThread getForumThread() {
        return forumThread;
    }

    public List<ForumReply> getForumReply() {
        return forumReply;
    }

    public void setForumThread(ForumThread forumThread) {
        this.forumThread = forumThread;
    }

    public void setForumReply(List<ForumReply> forumReply) {
        this.forumReply = forumReply;
    }
}
