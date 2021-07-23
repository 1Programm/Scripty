package com.programm.projects.scripty.app.utils;

import com.programm.projects.scripty.core.StringUtils;

public class AlignmentFormatter {

    private static final char ALIGN_LEFT   = '<';
    private static final char ALIGN_CENTER = '|';
    private static final char ALIGN_RIGHT  = '>';

    private static final String KEY = "%";

    public static String format(String message) {
        int index = message.indexOf(KEY);
        int last = 0;

        if(index == -1) return message;

        StringBuilder sb = new StringBuilder();

        while(index != -1){
            sb.append(message, last, index);

            int rBracketOpen = message.indexOf('(', index);

            if(rBracketOpen == -1) {
                last = index;
                break;
            }

            int rBracketClose = StringUtils.findClosing(message, rBracketOpen + 1, "(", ")");

            if(rBracketClose == -1) {
                last = index;
                break;
            }

            String content = message.substring(rBracketOpen + 1, rBracketClose);
            String replace = " ";
            int indexOperator = rBracketOpen - 1;
            boolean err = false;

            if(message.charAt(rBracketOpen - 1) == ']'){
                int sBracketOpen = message.indexOf('[', index);

                if(sBracketOpen == -1){
                    err = true;
                }
                else {
                    replace = message.substring(sBracketOpen + 1, rBracketOpen - 1);
                    indexOperator = sBracketOpen - 1;
                }
            }

            if(!err){
                char operator = message.charAt(indexOperator);
                String _num = message.substring(index + 1, indexOperator);
                int num = 0;

                try {
                    num = Integer.parseInt(_num);
                }
                catch (NumberFormatException e){
                    err = true;
                }

                if(!err) {
                    if (operator == ALIGN_LEFT) {
                        alignLeft(sb, content, replace, num);
                    }
                    else if (operator == ALIGN_CENTER) {
                        alignCenter(sb, content, replace, num);
                    }
                    else if (operator == ALIGN_RIGHT) {
                        alignRight(sb, content, replace, num);
                    }
                }
            }

            if(err) {
                sb.append(KEY);
                last = index + 1;
            }
            else {
                last = rBracketClose + 1;
            }

            index = message.indexOf(KEY, last);
        }

        sb.append(message, last, message.length());

        return sb.toString();
    }

    private static void alignLeft(StringBuilder sb, String content, String replace, int size){
        sb.append(content);
        size -= lengthOfContent(content);

        int len = replace.length();

        for(int i=0;i<size;i++){
            int ri = i % len;
            sb.append(replace.charAt(ri));
        }
    }

    private static void alignCenter(StringBuilder sb, String content, String replace, int size){
        size -= lengthOfContent(content);

        int s1 = size / 2;
        int s2 = s1 + size % 2;
        int len = replace.length();

        for(int i=0;i<s1;i++){
            int ri = i % len;
            sb.append(replace.charAt(ri));
        }

        sb.append(content);

        for(int i=0;i<s2;i++){
            int ri = i % len;
            sb.append(replace.charAt(ri));
        }
    }

    private static void alignRight(StringBuilder sb, String content, String replace, int size){
        size -= lengthOfContent(content);

        int len = replace.length();

        for(int i=0;i<size;i++){
            int ri = i % len;
            sb.append(replace.charAt(ri));
        }

        sb.append(content);
    }

    private static int lengthOfContent(String content){
        String resultString = content.replaceAll("\u001B\\[[0-9]+m", "");
        return resultString.length();
    }

}
