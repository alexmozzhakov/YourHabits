package com.habit_track.activity;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RegisterActivity_Test {

    public final Pattern NAME_PATTERN = RegisterActivity.NAME_PATTERN;
    public final Pattern EMAIL_PATTERN = RegisterActivity.EMAIL_PATTERN;

    @Test
    public void validator() {
        //Emails check
        String[] valid = {"test@yahoo.com",
                "test-100@yahoo.com", "test.100@yahoo.com",
                "test111@test.com", "test-100@test.net",
                "test.100@test.com.au", "test@1.com",
                "test@gmail.com.com", "test+100@gmail.com",
                "test-100@yahoo-test.com"};
        String[] invalid = {"test", "test@.com.my",
                "test123@gmail.a", "test123@.com", "test123@.com.com",
                ".mkyong@test.com", "test()*@gmail.com", "test@%*.com",
                "test..2002@gmail.com", "test.@gmail.com",
                "test@test@gmail.com", "test@gmail.com.1a"};

        for (String email : valid) {
            assertThat(RegisterActivity.isValidPattern(email, EMAIL_PATTERN), is(true));
        }
        for (String email : invalid) {
            assertThat(RegisterActivity.isValidPattern(email, EMAIL_PATTERN), is(false));
        }

        //Names check
        assertThat(RegisterActivity.isValidPattern("A B", NAME_PATTERN), is(true));
        assertThat(RegisterActivity.isValidPattern("a b", NAME_PATTERN), is(true));
        assertThat(RegisterActivity.isValidPattern("A B C", NAME_PATTERN), is(true));
        assertThat(RegisterActivity.isValidPattern("James o'reilly", NAME_PATTERN), is(true));
        assertThat(RegisterActivity.isValidPattern("a b c", NAME_PATTERN), is(true));
        assertThat(RegisterActivity.isValidPattern("test1", NAME_PATTERN), is(false));
        assertThat(RegisterActivity.isValidPattern("12 test", NAME_PATTERN), is(false));
    }

}