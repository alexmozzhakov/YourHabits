package com.habit_track.activity;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

public class PatternTest {

    private final Pattern NAME_PATTERN = RegisterActivity.NAME_PATTERN;
    private final Pattern EMAIL_PATTERN = RegisterActivity.EMAIL_PATTERN;

    @Test
    public void emailValidCheck() {

        final String[] valid = {"test@test.com",
                "test-100@test.com", "test.100@test.com",
                "test111@test.com", "test-100@test.net",
                "test.100@test.com.au", "test@1.com",
                "test@test.com.com", "test+100@test.com",
                "test-100@test-test.com"};

        for (final String email : valid) {
            Assert.assertTrue(RegisterActivity.isValidPattern(email, EMAIL_PATTERN));
        }


    }

    @Test
    public void emailInvalidCheck() {

        final String[] invalid = {"test", "test@.com.test",
                "test123@test.a", "test123@.com", "test123@.com.com",
                ".mkyong@test.com", "test()*@test.com", "test@%*.com",
                "test..2002@test.com", "test.@test.com",
                "test@test@test.com", "test@test.com.1a"};

        for (final String email : invalid) {
            Assert.assertFalse(RegisterActivity.isValidPattern(email, EMAIL_PATTERN));
        }

    }


    @Test
    public void validNamesCheck() {

        Assert.assertTrue(RegisterActivity.isValidPattern("A B", NAME_PATTERN));
        Assert.assertTrue(RegisterActivity.isValidPattern("a b", NAME_PATTERN));
        Assert.assertTrue(RegisterActivity.isValidPattern("A B C", NAME_PATTERN));
        Assert.assertTrue(RegisterActivity.isValidPattern("James o'reilly", NAME_PATTERN));
        Assert.assertTrue(RegisterActivity.isValidPattern("a b c", NAME_PATTERN));

    }

    @Test
    public void invalidNamesCheck() {

        Assert.assertFalse(RegisterActivity.isValidPattern("test1", NAME_PATTERN));
        Assert.assertFalse(RegisterActivity.isValidPattern("12 com.habit_track.test", NAME_PATTERN));

    }

}