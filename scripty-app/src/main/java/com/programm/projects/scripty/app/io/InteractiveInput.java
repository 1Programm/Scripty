package com.programm.projects.scripty.app.io;

import com.programm.projects.ioutils.log.api.in.IInput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class InteractiveInput implements IInput {

    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private boolean initialized;

    private final List<String> history = new ArrayList<>();
    private int index;

    private String buffer = "";
    private int bindex = 0;

    private boolean shouldReturn;

    private void init() {
        try {
            UnixTerminalConfig.setTerminalToNonBlocking();
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String next() {
        if(!initialized){
            init();
            initialized = true;
        }

        while(true) {
            try {
                int c = reader.read();

                //ESCAPE CHAR
                if(c == 27){
                    if(reader.ready()) {
                        escaped(c);
                    }
                    else {
                        escape(c);
                    }
                }
                //TAB
                else if(c == 9){
                    tab(c);
                }
                //ENTER
                else if(c == 10){
                    enter(c);
                }
                //BACKSPACE
                else if(c == 127){
                    backspace(c);
                }
                else {
                    other(c);
                }

                if(shouldReturn()){
                    return returnValue();
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

        return null;
    }

    protected boolean shouldReturn() {
        return shouldReturn;
    }

    private String returnValue() {
        shouldReturn = false;

        String _buffer = buffer;
        history.add(_buffer);
        index = history.size();
        buffer = "";
        bindex = 0;
        System.out.println();
        return _buffer;
    }

    private void escaped(int c) throws IOException {
        c = reader.read();
        //ARROWS
        if(c == 91){
            evalArrows();
        }
    }

    private void enter(int c) throws IOException {
        shouldReturn = true;
    }

    private void backspace(int c) throws IOException {
        if(buffer.length() > 0 && bindex > 0) {
            goLeft(1);
            removeFromCursorToEnd();
            String rest = buffer.substring(bindex);

            if(!rest.isEmpty()){
                System.out.print(rest);
                goLeft(rest.length());
            }

            String pre = buffer.substring(0, bindex - 1);
            String after = buffer.substring(bindex);

            buffer = pre + after;
            bindex--;
        }
    }

    private void escape(int c) throws IOException {

    }

    private void tab(int c) throws IOException {
        String completed = null;//autocompletion.provideAutocompletion(buffer);

        if(completed != null){
            clearBuffer();
            System.out.print(completed);
            bindex = completed.length();
        }
    }

    private void other(int c) throws IOException {
        char _c = (char)c;
        if(bindex == buffer.length()) {
            buffer += _c;
            System.out.print(_c);
        }
        else {
            removeFromCursorToEnd();
            String pre = buffer.substring(0, bindex);
            String after = buffer.substring(bindex);
            System.out.print(_c + after);

            goLeft(after.length());

            buffer = pre + _c + after;
        }

        bindex++;
    }

    private void evalArrows() throws IOException{
        int c = reader.read();

        //UP
        if(c == 65){
            if(index > 0){
                index--;
                setHistory();
            }
        }
        //DOWN
        else if(c == 66){
            if(index < history.size()){
                index++;

                if(index == history.size()){
                    clearBuffer();
                }
                else {
                    setHistory();
                }
            }
        }
        //RIGHT
        else if(c == 67){
            if(bindex < buffer.length()) {
                goRight(1);
                bindex++;
            }
        }
        //LEFT
        else if(c == 68){
            if(bindex > 0) {
                goLeft(1);
                bindex--;
            }
        }
    }

    private void setHistory(){
        String entry = history.get(index);
        clearBuffer();
        System.out.print(entry);
        buffer = entry;
        bindex = buffer.length();
    }

    private void goLeft(int i){
        if(i <= 0) return;
        System.out.print("\u001B[" + i + "D");
    }

    private void goRight(int i){
        if(i <= 0) return;
        System.out.print("\u001B[" + i + "C");
    }

    private void clearBuffer(){
        int len = buffer.length();
        goLeft(len);
        removeFromCursorToEnd();
        buffer = "";
        bindex = 0;
    }

    private void removeFromCursorToEnd(){
        System.out.print("\u001B[0K");
    }

    @Override
    public void close() throws Exception {
        UnixTerminalConfig.resetTerminal();
        reader.close();
    }
}
