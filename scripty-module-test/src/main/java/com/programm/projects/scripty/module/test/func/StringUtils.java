package com.programm.projects.scripty.module.test.func;

import java.util.ArrayList;
import java.util.List;

class StringUtils {

    public static int findClosing(String text, String open, String closing, String... commentsOpenClose){
        return findClosing(text, 0, open, closing, commentsOpenClose);
    }

    public static int findClosing(String text, int offset, String open, String closing, String... commentsOpenClose){
        if(commentsOpenClose.length % 2 != 0) throw new IllegalArgumentException("Comments must be specified in pairs!");

        int numOpen = 1;
        int index = offset;
        boolean insideComment = false;
        String commentClose = null;

        while(index < text.length()){
            char c = text.charAt(index);

            if(insideComment){
                if(c == commentClose.charAt(0) && text.startsWith(commentClose, index)){
                    insideComment = false;
                    commentClose = null;
                }
            }
            else if(c == open.charAt(0) && text.startsWith(open, index)){
                numOpen++;
            }
            else if(c == closing.charAt(0) && text.startsWith(closing, index)){
                numOpen--;
                if(numOpen == 0){
                    return index;
                }
            }
            else {
                for(int i=0;i<commentsOpenClose.length;i+=2){
                    String cOpen = commentsOpenClose[i];
                    if(c == cOpen.charAt(0) && text.startsWith(cOpen, index)){
                        insideComment = true;
                        commentClose = commentsOpenClose[i+1];
                        break;
                    }
                }
            }

            index++;
        }

        return -1;
    }

    public static String[] advancedSplit(String text, String key, String... comments){
        if(comments.length % 2 != 0) throw new IllegalArgumentException("Comments must be specified in pairs!");

        List<String> split = new ArrayList<>();

        int last = 0;
        int index = 0;
        boolean insideComment = false;
        String commentClose = null;

        while(index < text.length()){
            char c = text.charAt(index);

            if(insideComment){
                if(c == commentClose.charAt(0) && text.startsWith(commentClose, index)){
                    insideComment = false;
                    commentClose = null;
                }
            }
            else if(c == key.charAt(0) && text.startsWith(key, index)){
                String nSplit = text.substring(last, index);
                split.add(nSplit);
                last = index + key.length();
            }
            else {
                for(int i=0;i<comments.length;i+=2){
                    String cOpen = comments[i];
                    if(c == cOpen.charAt(0) && text.startsWith(cOpen, index)){
                        insideComment = true;
                        commentClose = comments[i+1];
                        break;
                    }
                }
            }

            index++;
        }

        if(last < text.length()){
            String rest = text.substring(last);
            split.add(rest);
        }

        return split.toArray(new String[0]);
    }

}
