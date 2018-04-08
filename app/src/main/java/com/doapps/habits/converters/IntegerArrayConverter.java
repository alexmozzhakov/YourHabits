package com.doapps.habits.converters;

import android.arch.persistence.room.TypeConverter;

/**
 * The class {@code IntegerArrayConverter} includes methods for converting
 * integer arrays to String and vice versa to make integer arrays
 * available to storage in a Room Database
 */
public class IntegerArrayConverter {

  /**
   * The function converts any given integer array to coma-separated String
   *
   * @param value an integer array to convert
   * @return coma-separated String
   */
  @TypeConverter
  public static String fromArray(int[] value) {
    if (value.length != 0) {
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
    return "";
  }

  /**
   * The function converts coma-separated String to an integer array
   *
   * @param str coma-separated String to convert
   * @return an integer array
   */
  @TypeConverter
  public static int[] toArray(String str) {
    if (str.length() != 0) {
      String[] temp = str.split(",");
      int[] arr = new int[temp.length];
      for (int i = 0; i < arr.length && i < temp.length; i++) {
        arr[i] = Integer.valueOf(temp[i]);
      }
      return arr;
    }
    return new int[0];
  }
}
