package com.doapps.habits.converters;

import android.arch.persistence.room.TypeConverter;

public class IntegerArrayConverter {

  @TypeConverter
  public static String fromArray(int[] value) {
    StringBuilder temp = new StringBuilder(2 * value.length + 1);
    for (int i = 0; i < value.length; i++) {
      int integer = value[i];
      temp.append(integer);
      if (i != value.length - 1) {
        temp.append(",");
      }
    }
    return temp.toString();
  }

  @TypeConverter
  public static int[] toArray(String str) {
    String[] temp = str.substring(0, str.length()).split(",");
    int[] arr = new int[temp.length];
    for (int i = 0; i < arr.length && i < temp.length; i++) {
      arr[i] = Integer.valueOf(temp[i]);
    }
    return arr;
  }
}
