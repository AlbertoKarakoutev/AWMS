package com.company.awms.modules.base.forum.data;

import java.util.List;

public class ThreadReplyDTO {

    private ForumThread forumThread;
    private List<ForumReply> forumReplies;

    public ThreadReplyDTO(ForumThread forumThread, List<ForumReply> forumReplies) {
        this.forumThread = forumThread;
        this.forumReplies = forumReplies;
    }

    public ForumThread getForumThread() {
        return forumThread;
    }

    public List<ForumReply> getForumReplies() {
        return forumReplies;
    }

    public void setForumThread(ForumThread forumThread) {
        this.forumThread = forumThread;
    }

    public void setForumReplies(List<ForumReply> forumReplies) {
        this.forumReplies = forumReplies;
    }
}
