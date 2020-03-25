package it.richkmeli.jframework.util.regex;

import it.richkmeli.jframework.util.regex.exception.RegexException;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RegexManagerTest {

    @Test
    public void checkEmail() {
    }

    @Test
    public void checkTelephone() {
    }

    @Test
    public void getGroupsFromRegex() throws RegexException {
        final String expense = "21.90|LG,XX,SC \"Dinner out\"";
        String regex = "(\\d+(?:\\.\\d{1,2})?)\\|((?:[A-Z]{2},)*(?:[A-Z]{2}))(?:\\s\\\"((?:\\w+\\s)*(?:\\w+))?\\\")?";

        List<String> groupList = RegexManager.getGroupsFromRegex(expense,regex);
        for(String s : groupList){
            System.out.println(s);
        }
    }
}