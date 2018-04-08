package com.doapps.habits.converters;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("HardCodedStringLiteral")
public class IntegerArrayConverterTest {

  @Test
  public void toArrayTest() {
    String arrayString1 = "1,2,3";
    String arrayString2 = "1,2";
    String arrayString3 = "1";
    String arrayString4 = "";
    Assert.assertArrayEquals(new int[]{1, 2, 3}, IntegerArrayConverter.toArray(arrayString1));
    Assert.assertArrayEquals(new int[]{1, 2}, IntegerArrayConverter.toArray(arrayString2));
    Assert.assertArrayEquals(new int[]{1}, IntegerArrayConverter.toArray(arrayString3));
    Assert.assertArrayEquals(new int[]{}, IntegerArrayConverter.toArray(arrayString4));
  }

  @Test
  public void fromArrayTest() {
    int[] arr1 = new int[]{1, 2, 3};
    int[] arr2 = new int[]{1, 6};
    int[] arr3 = new int[]{1, 1, 3};
    int[] arr4 = new int[]{};
    Assert.assertEquals("1,2,3", IntegerArrayConverter.fromArray(arr1));
    Assert.assertEquals("1,6", IntegerArrayConverter.fromArray(arr2));
    Assert.assertEquals("1,1,3", IntegerArrayConverter.fromArray(arr3));
    Assert.assertEquals("", IntegerArrayConverter.fromArray(arr4));
  }
}