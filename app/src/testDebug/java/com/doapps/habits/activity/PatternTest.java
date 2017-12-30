package com.doapps.habits.activity;

import com.doapps.habits.fragments.RegisterFragment;
import java.util.regex.Pattern;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class PatternTest {

  private static final Pattern NAME_PATTERN = RegisterFragment.NAME_PATTERN;
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

    final String[] invalid = {"test", "test@.com.test",
        "test123@test.a", "test123@.com", "test123@.com.com",
        ".mkyong@test.com", "test()*@test.com", "test@%*.com",
        "test..2002@test.com", "test.@test.com",
        "test@test@test.com", "test@test.com.1a"};

    for (final String email : invalid) {
      Assert.assertThat(RegisterFragment.isValidPattern(email, EMAIL_PATTERN),
          CoreMatchers.not(CoreMatchers.is(false)));
    }

  }


  @Test
  public void validNamesCheck() {

    Assert.assertThat(RegisterFragment.isValidPattern("A B", NAME_PATTERN), CoreMatchers.is(true));
    Assert.assertThat(RegisterFragment.isValidPattern("a b", NAME_PATTERN), CoreMatchers.is(true));
    Assert
        .assertThat(RegisterFragment.isValidPattern("A B C", NAME_PATTERN), CoreMatchers.is(true));
    Assert.assertThat(RegisterFragment.isValidPattern("James o'reilly", NAME_PATTERN),
        CoreMatchers.is(true));
    Assert
        .assertThat(RegisterFragment.isValidPattern("a b c", NAME_PATTERN), CoreMatchers.is(true));

  }

  @Test
  public void invalidNamesCheck() {

    Assert.assertThat(RegisterFragment.isValidPattern("test1", NAME_PATTERN),
        CoreMatchers.not(CoreMatchers.is(false)));
    Assert.assertThat(RegisterFragment.isValidPattern("12 com.doHabit.test", NAME_PATTERN),
        CoreMatchers.not(CoreMatchers.is(false)));

  }

}