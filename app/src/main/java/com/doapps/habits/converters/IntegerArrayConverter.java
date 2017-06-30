package com.doapps.habits.converters;

import android.arch.persistence.room.TypeConverter;

public class IntegerArrayConverter {
    @TypeConverter
    public static String toArray(int[] value) {
        StringBuilder temp = new StringBuilder(2 * value.length + 1);
        for (int integer : value) {
            temp.append(integer).append(",");
        }
        return temp.toString();
    }

    @TypeConverter
    public static int[] fromArray(String str) {
        String[] temp = str.substring(0, str.length() - 1).split(",");
        int[] arr = new int[temp.length];
        for (int i = 0; i < arr.length && i < temp.length; i++) {
            arr[i] = Integer.valueOf(temp[i]);
        }

        return arr;
    }
}
