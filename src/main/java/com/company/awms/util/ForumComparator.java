package com.company.awms.util;

import java.util.Comparator;

import com.company.awms.data.forum.ForumThread;

public class ForumComparator implements Comparator<ForumThread>{
    @Override
    public int compare(ForumThread ft1, ForumThread ft2) {
        return -1 * ft1.getDateTime().compareTo(ft2.getDateTime());
    }
}
