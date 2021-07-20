package com.programm.projects.scripty.core.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonMapper {

    private static class Index {
        public int i;

        Index(){
            this.i = 0;
        }

        Index(int i){
            this.i = i;
        }

        public String toString(){
            return "" + i;
        }
    }

    private static class Data {
        public final String in;
        public int lastIndex = 0;
        public final List<Integer> nextComma;       int ci = 0;
        public final List<Integer> nextCurlyOpen;   int coi = 0;
        public final List<Integer> nextCurlyClose;  int cci = 0;
        public final List<Integer> nextBlockOpen;   int boi = 0;
        public final List<Integer> nextBlockClose;  int bci = 0;

        public Data(String in, List<Integer> nextComma, List<Integer> nextCurlyOpen, List<Integer> nextCurlyClose, List<Integer> nextBlockOpen, List<Integer> nextBlockClose) {
            this.in = in;
            this.nextComma = nextComma;
            this.nextCurlyOpen = nextCurlyOpen;
            this.nextCurlyClose = nextCurlyClose;
            this.nextBlockOpen = nextBlockOpen;
            this.nextBlockClose = nextBlockClose;
        }

        public int next(){
            int min = -1;
            int chosen = -1;

            if(ci < nextComma.size()){
                    min = nextComma.get(ci);
                    chosen = 0;
            }

            if(coi < nextCurlyOpen.size()){
                if(min == -1 || nextCurlyOpen.get(coi) < min){
                    min = nextCurlyOpen.get(coi);
                    chosen = 1;
                }
            }

            if(cci < nextCurlyClose.size()){
                if(min == -1 || nextCurlyClose.get(cci) < min){
                    min = nextCurlyClose.get(cci);
                    chosen = 2;
                }
            }

            if(boi < nextBlockOpen.size()){
                if(min == -1 || nextBlockOpen.get(boi) < min){
                    min = nextBlockOpen.get(boi);
                    chosen = 3;
                }
            }

            if(bci < nextBlockClose.size()){
                if(min == -1 || nextBlockClose.get(bci) < min){
                    min = nextBlockClose.get(bci);
                    chosen = 4;
                }
            }

            if(chosen == 0){
                ci++;
            }
            else if(chosen == 1){
                coi++;
            }
            else if(chosen == 2){
                cci++;
            }
            else if(chosen == 3){
                boi++;
            }
            else if(chosen == 4){
                bci++;
            }

            return min;
        }

        public char get(int i){
            return in.charAt(i);
        }
    }

    private static String substring(String in, Index from, Index to){
        return in.substring(from.i, to.i);
    }

    private static void rmSpace(String in, Index from, Index to){
        while(from.i < to.i && (in.charAt(from.i) == ' ' || in.charAt(from.i) == '\n')){
            from.i++;
        }
        while(from.i < to.i && in.charAt(to.i - 1) == ' ' || in.charAt(from.i) == '\n'){
            to.i--;
        }
    }


    public static JNode parse(File file) throws IOException, JsonParseException {
        StringBuilder sb = new StringBuilder();

        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
            while((line = reader.readLine()) != null){
                if(sb.length() != 0){
                    sb.append("\n");
                }

                sb.append(line);
            }
        }

        return parse(sb.toString());
    }

    public static JNode parse(String in) throws JsonParseException {
        Data data = analyze(in);

        return parse(data);
    }

    private static Data analyze(String in) {
        List<Integer> nextComma = new ArrayList<>();
        List<Integer> nextCurlyOpen = new ArrayList<>();
        List<Integer> nextCurlyClose = new ArrayList<>();
        List<Integer> nextBlockOpen = new ArrayList<>();
        List<Integer> nextBlockClose = new ArrayList<>();

        boolean insideComment = false;
        int i=0;

        while(i < in.length()){
            char c = in.charAt(i);

            if(insideComment){
                if(c == '"' && (i == 0 || in.charAt(i - 1) != '\\')){
                    insideComment = false;
                }
            }
            else {
                if (c == '"'){
                    insideComment = true;
                }
                else if(c == ','){
                    nextComma.add(i);
                }
                else if(c == '{'){
                    nextCurlyOpen.add(i);
                }
                else if(c == '}'){
                    nextCurlyClose.add(i);
                }
                else if(c == '['){
                    nextBlockOpen.add(i);
                }
                else if(c == ']'){
                    nextBlockClose.add(i);
                }
            }

            i++;
        }

        return new Data(in, nextComma, nextCurlyOpen, nextCurlyClose, nextBlockOpen, nextBlockClose);
    }

    private static JNode parse(Data data) throws JsonParseException {
        int next = data.next();

        if(next == -1) {
            String rest = data.in.substring(data.lastIndex);
            return new JNValue(rest);
        }

        char c = data.get(next);
        if(c == '{') {
            data.lastIndex = next;
            return parseMap(data);
        }

        return null;
    }

    private static JNode parseMap(Data data) throws JsonParseException{
        int next = data.next();

        if(next == -1){
            throw new JsonParseException("Invalid map - no ending } specified!");
        }

        char nextChar = data.get(next);

        Map<String, JNode> map = new HashMap<>();

        while(nextChar != '}'){

        }


        return new JNMap(map);
    }

}
