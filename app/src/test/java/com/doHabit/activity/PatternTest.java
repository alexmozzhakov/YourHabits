package com.dohabit.activity;

import com.dohabit.fragments.RegisterFragment;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

public class PatternTest {

    private final Pattern NAME_PATTERN = RegisterFragment.NAME_PATTERN;
    private final Pattern EMAIL_PATTERN = RegisterFragment.EMAIL_PATTERN;

    @Test
    public void emailValidCheck() {

        String[] valid = {"test@test.com",
                "test-100@test.com", "test.100@test.com",
                "test111@test.com", "test-100@test.net",
                "test.100@test.com.au", "test@1.com",
                "test@test.com.com", "test+100@test.com",
                "test-100@test-test.com"};

        for (String email : valid) {
            Assert.assertTrue(RegisterFragment.isValidPattern(email, EMAIL_PATTERN));
        }


    }

    @Test
    public void emailInvalidCheck() {

        String[] invalid = {"test", "test@.com.test",
                "test123@test.a", "test123@.com", "test123@.com.com",
                ".mkyong@test.com", "test()*@test.com", "test@%*.com",
                "test..2002@test.com", "test.@test.com",
                "test@test@test.com", "test@test.com.1a"};

        for (String email : invalid) {
            Assert.assertFalse(RegisterFragment.isValidPattern(email, EMAIL_PATTERN));
        }

    }


    @Test
    public void validNamesCheck() {

        Assert.assertTrue(RegisterFragment.isValidPattern("A B", NAME_PATTERN));
        Assert.assertTrue(RegisterFragment.isValidPattern("a b", NAME_PATTERN));
        Assert.assertTrue(RegisterFragment.isValidPattern("A B C", NAME_PATTERN));
        Assert.assertTrue(RegisterFragment.isValidPattern("James o'reilly", NAME_PATTERN));
        Assert.assertTrue(RegisterFragment.isValidPattern("a b c", NAME_PATTERN));

    }

    @Test
    public void invalidNamesCheck() {

        Assert.assertFalse(RegisterFragment.isValidPattern("test1", NAME_PATTERN));
        Assert.assertFalse(RegisterFragment.isValidPattern("12 com.doHabit.test", NAME_PATTERN));

    }

}