package it.richkmeli.jframework.util.regex;

import it.richkmeli.jframework.util.regex.exception.RegexException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexManager {

    public static final String EMAIL_REGEX = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    public static final String PHONE_NUMBER_REGEX = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$";


    public static void validate(String text, String regex) throws RegexException {
        if (text.matches(regex)) {
        } else {
            throw new RegexException("text: " + text + " doesn't match regex: " + regex);
        }
    }

    public static List<String> getGroupsFromRegex(String text, String regex) throws RegexException {
        validate(text, regex);
        List<String> groupList = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        for (int i = 0; matcher.find(i + 1); i++) {
            groupList.add(matcher.group(i));
        }
        return groupList;
    }

    public static void checkEmailIntegrity(String email) throws RegexException {
        RegexManager.validate(email, EMAIL_REGEX);
    }

    public static void checkPhoneNumberIntegrity(String phoneNumber) throws RegexException {
        RegexManager.validate(phoneNumber, PHONE_NUMBER_REGEX);
    }
}
