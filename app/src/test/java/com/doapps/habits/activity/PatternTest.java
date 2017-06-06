package com.doapps.habits.activity;

import com.doapps.habits.fragments.RegisterFragment;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

public class PatternTest {
    private static final Pattern EMAIL_PATTERN = RegisterFragment.EMAIL_PATTERN;

    @Test
    public void emailValidCheck() {

        final String[] valid = {"test@test.com",
                "test-100@test.com", "test.100@test.com",
                "test111@test.com", "test-100@test.net",
                "test.100@test.com.au", "test@1.com",
                "test@test.com.com", "test+100@test.com",
                "test-100@test-test.com"};

        for (final String email : valid) {
            Assert.assertThat(String.format("test failed for %s", email),
                    RegisterFragment.isValidPattern(email, EMAIL_PATTERN), CoreMatchers.is(true));
        }

    }

    @Test
    public void emailInvalidCheck() {

        final String[] invalid = {"test", "test@.com.test", "test123@.com",
                "test123@.com.com", ".mkyong@test.com", "test()*@test.com",
                "test@%*.com", "test..2002@test.com", "test.@test.com", "test@test@test.com"};

        for (final String email : invalid) {
            Assert.assertThat(RegisterFragment.isValidPattern(email, EMAIL_PATTERN),
                    CoreMatchers.is(false));
        }

    }
}