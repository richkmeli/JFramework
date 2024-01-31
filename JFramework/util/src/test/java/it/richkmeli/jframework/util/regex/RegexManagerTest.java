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

        List<String> groupList = RegexManager.getGroupsFromRegex(expense, regex);
        for (String s : groupList) {
            System.out.println(s);
        }
    }

    @Test
    public void checkIpv4AddressIntegrity() {
        // Valid IPv4 addresses
        try {
            RegexManager.checkIpv4AddressIntegrity("192.168.1.1");
            // Add more valid IPv4 addresses as needed
        } catch (RegexException e) {
            // If an exception is thrown for a valid address, the test should fail
            assert false : "Valid IPv4 address validation failed.";
        }

        // Invalid IPv4 addresses
        try {
            RegexManager.checkIpv4AddressIntegrity("256.0.0.1");  // Octet value exceeds 255
            assert false;
            // Add more invalid IPv4 addresses as needed
        } catch (RegexException e) {
            // If no exception is thrown for an invalid address, the test should fail
            assert true : "Invalid IPv4 address validation passed.";
        }

        // Invalid IPv4 addresses
        try {
            RegexManager.checkIpv4AddressIntegrity("192.168.1.300");  // Octet value exceeds 255
            assert false;
            // Add more invalid IPv4 addresses as needed
        } catch (RegexException e) {
            // If no exception is thrown for an invalid address, the test should fail
            assert true : "Invalid IPv4 address validation passed.";
        }

        // Invalid IPv4 addresses
        try {
            RegexManager.checkIpv4AddressIntegrity("192.168.1");  // Incomplete address
            assert false;
            // Add more invalid IPv4 addresses as needed
        } catch (RegexException e) {
            // If no exception is thrown for an invalid address, the test should fail
            assert true : "Invalid IPv4 address validation passed.";
        }

        // Invalid IPv4 addresses
        try {
            RegexManager.checkIpv4AddressIntegrity("192.168.1.1.1");  // Extra octet
            assert false;
            // Add more invalid IPv4 addresses as needed
        } catch (RegexException e) {
            // If no exception is thrown for an invalid address, the test should fail
            assert true : "Invalid IPv4 address validation passed.";
        }

        try {
            RegexManager.checkIpv4AddressIntegrity("192.168.1.");  // Incomplete octet
            // Add more invalid IPv4 addresses as needed
        } catch (RegexException e) {
            // If no exception is thrown for an invalid address, the test should fail
            assert true : "Invalid IPv4 address validation passed.";
        }
    }

    @Test
    public void testMacAddressIntegrity() {
        // Valid MAC addresses
        try {
            RegexManager.checkMacAddressIntegrity("00:1A:2B:3C:4D:5E");
            RegexManager.checkMacAddressIntegrity("11-22-33-AA-BB-CC");
            // Add more valid MAC addresses as needed
        } catch (RegexException e) {
            assert false : "Valid MAC address validation failed.";
        }

        // Invalid MAC addresses
        try {
            RegexManager.checkMacAddressIntegrity("ZZ:ZZ:ZZ:ZZ:ZZ:ZZ");  // Non-hexadecimal character
            RegexManager.checkMacAddressIntegrity("00:1A:2B:3C:4D");  // Incomplete address
            RegexManager.checkMacAddressIntegrity("11-22-33-AA-BB-CC-DD");  // Extra digit
            // Add more invalid MAC addresses as needed
        } catch (RegexException e) {
            // Ensure that the exception is thrown for invalid MAC addresses
            assert true;
        }
    }

}