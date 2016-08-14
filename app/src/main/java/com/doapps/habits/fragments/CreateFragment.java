package com.doapps.habits.fragments;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.doapps.habits.BuildConfig;
import com.doapps.habits.R;
import com.doapps.habits.activity.MainActivity;
import com.doapps.habits.helper.HabitListManager;
import com.doapps.habits.models.HabitsDatabase;

import java.util.Arrays;
import java.util.Calendar;

@SuppressWarnings("FeatureEnvy")
public class CreateFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private TextInputEditText editTime;
    private TextInputEditText editQuestion;
    private TextInputEditText editTitle;
    private Spinner sFrequency;
    private TextView tvFreqNum;
    private TextView tvFreqDen;
    private ViewGroup llCustomFrequency;
    private int lastSpinnerSelection;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_create, container, false);
        editTitle = (TextInputEditText) result.findViewById(R.id.edit_title);
        editQuestion = (TextInputEditText) result.findViewById(R.id.edit_question);
        editTime = (TextInputEditText) result.findViewById(R.id.edit_time);
        sFrequency = (Spinner) result.findViewById(R.id.sFrequency);
        tvFreqNum = (TextView) result.findViewById(R.id.input_freq_num);
        tvFreqDen = (TextView) result.findViewById(R.id.input_freq_den);
        llCustomFrequency = (ViewGroup) result.findViewById(R.id.llCustomFrequency);

        sFrequency.setOnItemSelectedListener(this);
        result.findViewById(R.id.btnRegister).setOnClickListener(view -> onHabitCreate());

        return result;
    }

    private void onHabitCreate() {
        final int[] frequency;
        if (lastSpinnerSelection == 0) {
            frequency = new int[]{1, 1};
        } else if (lastSpinnerSelection == 1) {
            frequency = new int[]{7, 1};
        } else if (lastSpinnerSelection == 2) {
            frequency = new int[]{7, 2};
        } else if (lastSpinnerSelection == 3) {
            frequency = new int[]{7, 5};
        } else {
            frequency = new int[2];
            frequency[0] = Integer.valueOf(tvFreqDen.getText().toString());
            frequency[1] = Integer.valueOf(tvFreqNum.getText().toString());
        }

        if (BuildConfig.DEBUG) {
            Log.i("onHabitCreate", "freq = " + Arrays.toString(frequency));
        }

        final int time = editTime.getText().toString().isEmpty() ?
                0 : Integer.valueOf(editTime.getText().toString());
        // Checks what data was entered and adds habit to mUserDatabase
        final HabitsDatabase habitsDatabase = HabitListManager.getInstance(getContext()).getDatabase();
        habitsDatabase.addHabit(
                editTitle.getText().toString(),
                editQuestion.getText().toString(),
                time,
                Calendar.getInstance(),
                0, frequency
        );

        ((MainActivity) getActivity()).getToolbar().setTitle("List");
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new ListFragment()).commit();

    }

    @Override
    public void onItemSelected(final AdapterView<?> adapterView, final View view, final int i,
                               final long l) {
        lastSpinnerSelection = i;
        if (i == 4) {
            sFrequency.setVisibility(View.GONE);
            llCustomFrequency.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onNothingSelected(final AdapterView<?> adapterView) {
        // ignored
    }
}
