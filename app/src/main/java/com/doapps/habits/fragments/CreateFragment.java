package com.doapps.habits.fragments;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doapps.habits.R;
import com.doapps.habits.activity.MainActivity;
import com.doapps.habits.helper.HabitListManager;
import com.doapps.habits.helper.ImmManager;
import com.doapps.habits.models.HabitsDatabase;

import java.util.Calendar;

public class CreateFragment extends Fragment {
    private TextInputEditText editTime;
    private TextInputEditText editQuestion;
    private TextInputEditText editTitle;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_create, container, false);
        editTitle = (TextInputEditText) result.findViewById(R.id.edit_title);
        editQuestion = (TextInputEditText) result.findViewById(R.id.edit_question);
        editTime = (TextInputEditText) result.findViewById(R.id.edit_time);

        editTitle.setOnClickListener(v -> ImmManager.getInstance().setImmOpened());
        editTitle.setOnKeyListener((final View view, final int keyCode, final KeyEvent event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER) {
                editQuestion.requestFocus();
                return true;
            }
            return false;
        });

        editQuestion.setOnClickListener(v -> ImmManager.getInstance().setImmOpened());
        editQuestion.setOnKeyListener((final View view, final int keyCode, final KeyEvent event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER) {
                editTime.requestFocus();
                return true;
            }
            return false;
        });

        editTime.setOnClickListener(v -> ImmManager.getInstance().setImmOpened());
        editTime.setOnKeyListener((final View view, final int keyCode, final KeyEvent event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER) {
                ImmManager.getInstance().setImmOpened();
                onHabitCreate();
                return true;
            }
            return false;
        });

        // TODO: 16/06/2016 add new fields to habit
        result.findViewById(R.id.btnRegister).setOnClickListener(view -> onHabitCreate());

        return result;
    }

    private void onHabitCreate() {
        // Checks what data was entered and adds habit to mUserDatabase
        final HabitsDatabase habitsDatabase = new HabitListManager(getContext()).getDatabase();

        final int freq = editTime.getText().toString().isEmpty() ?
                0 : Integer.valueOf(editTime.getText().toString());

        habitsDatabase.addHabit(
                editTitle.getText().toString(),
                editQuestion.getText().toString(),
                freq,
                Calendar.getInstance(),
                0, 0, 0
        );

        // Closes keyboard when created new habit
        final ImmManager immManager = ImmManager.getInstance();
        if (immManager.isImmOpened()) {
            immManager.closeImm(getActivity());
        }

        ((MainActivity) getActivity()).getToolbar().setTitle("List");
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new ListFragment()).commit();

    }


    @Override
    public void onPause() {
        ImmManager.getInstance().closeImm(getActivity());
        super.onPause();
    }
}
