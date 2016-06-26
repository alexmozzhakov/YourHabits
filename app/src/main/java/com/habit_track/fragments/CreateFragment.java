package com.habit_track.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.habit_track.R;
import com.habit_track.database.HabitDBHandler;
import com.habit_track.helper.ImmManager;

import java.util.Calendar;

public class CreateFragment extends Fragment {
    private HabitDBHandler mHabitsDatabase;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_create, container, false);
        final EditText editTitle = (EditText) result.findViewById(R.id.editTitle);
        final EditText editDescription = (EditText) result.findViewById(R.id.editDescription);
        final EditText editTime = (EditText) result.findViewById(R.id.editTime);

        editTitle.setOnClickListener(v -> ImmManager.getInstance().setImmOpened());
        editDescription.setOnClickListener(v -> ImmManager.getInstance().setImmOpened());
        editTime.setOnClickListener(v -> ImmManager.getInstance().setImmOpened());

        // TODO: 16/06/2016 add new fields to habit
        result.findViewById(R.id.btnRegister).setOnClickListener((view) -> {

            // Checks what data was entered and adds habit to mUserDatabase
            if (mHabitsDatabase == null) {
                mHabitsDatabase = new HabitDBHandler(this.getActivity());
            }

            int time;
            if (editTime.getText().toString().equals("")) {
                time = 60;
            } else {
                time = Integer.valueOf(editTime.getText().toString());
            }

            mHabitsDatabase.addHabit(
                    editTitle.getText().toString(),
                    editDescription.getText().toString(),
                    time,
                    false,
                    Calendar.getInstance(),
                    0, 0, 0
            );

            // Closes keyboard if created new habit
            final ImmManager immManager = ImmManager.getInstance();
            if (immManager.isImmOpened()) {
                immManager.closeImm(getActivity());
            }

            HabitDBHandler.isChecked = false;
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new ListFragment()).commit();

        });

        return result;
    }

    @Override
    public void onPause() {
        ImmManager.getInstance().closeImm(getActivity());
        super.onPause();
    }
}
