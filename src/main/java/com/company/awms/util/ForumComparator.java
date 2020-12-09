package com.company.awms.util;

import com.company.awms.data.forum.ForumThread;

import java.time.LocalDateTime;
import java.util.Comparator;

public class ForumComparator implements Comparator<ForumThread>{
    @Override
    public int compare(ForumThread ft1, ForumThread ft2) {
        return -1 * ft1.getDateTime().compareTo(ft2.getDateTime());
    }
}
