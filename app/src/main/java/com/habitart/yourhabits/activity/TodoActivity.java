package com.habitart.yourhabits.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.habitart.yourhabits.R;

import java.util.ArrayList;

public class TodoActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        ArrayList<String> todoList = new ArrayList<>();
        todoList.add("lol kek mda");
        todoList.add("mda lol mda");
        todoList.add("kek mda mda");
        todoList.add("lol mda mda");
        todoList.add("mda kek mda");
        todoList.add("lol kek mda");
        todoList.add("mda lol mda");
        todoList.add("kek mda mda");
        todoList.add("lol mda mda");
        todoList.add("mda kek mda");
        todoList.add("lol kek mda");
        todoList.add("mda lol mda");
        todoList.add("kek mda mda");
        todoList.add("lol mda mda");
        todoList.add("mda kek mda");
        todoList.add("lol kek mda");
        todoList.add("mda lol mda");
        todoList.add("kek mda mda");
        todoList.add("lol mda mda");
        todoList.add("mda kek mda");
        todoList.add("lol kek mda");
        todoList.add("mda lol mda");
        todoList.add("kek mda mda");
        todoList.add("lol mda mda");
        todoList.add("mda kek mda");
        todoList.add("lol kek mda");
        todoList.add("mda lol mda");
        todoList.add("kek mda mda");
        todoList.add("lol mda mda");
        todoList.add("mda kek mda");

        ArrayAdapter<String> todoAdapter = new ArrayAdapter<>(this, R.layout.todo_list_item, todoList);
        ListView lw = (ListView) findViewById(R.id.listview_todo);
        lw.setAdapter(todoAdapter);
    }
}