package com.doapps.habits.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.doapps.habits.BuildConfig;
import com.doapps.habits.R;
import com.doapps.habits.activity.MainActivity;
import com.doapps.habits.dao.HabitDao;
import com.doapps.habits.helper.HabitListManager;
import com.doapps.habits.helper.HabitNotifier;
import com.doapps.habits.models.Habit;
import java.util.Arrays;
import java.util.Calendar;

public class CreateFragment extends Fragment implements AdapterView.OnItemSelectedListener {

  private TextInputEditText editTime;
  private TextInputEditText editQuestion;
  private TextInputEditText editTitle;
  private Spinner sFrequency;
  private TextView tvFreqNum;
  private TextView tvFreqDen;
  private ViewGroup llCustomFrequency;
  private int lastSpinnerSelection;
  private HabitDao habitsDatabase;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View result = inflater.inflate(R.layout.fragment_create, container, false);
    editTitle = result.findViewById(R.id.edit_title);
    editQuestion = result.findViewById(R.id.edit_question);
    editTitle.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        editTitle.setError("Can't be empty");
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() != 0) {
          char start = Character.toLowerCase(charSequence.charAt(0));
          editQuestion.setText(String.format("Did you %s%s today?", start,
              charSequence.subSequence(1, charSequence.length())));
        }
      }

      @Override
      public void afterTextChanged(Editable editable) {
        if (editable.length() == 0) {
          editTitle.setError("Can't be empty");
        }
      }
    });
    editTime = result.findViewById(R.id.edit_time);
    sFrequency = result.findViewById(R.id.sFrequency);
    tvFreqNum = result.findViewById(R.id.input_freq_num);
    tvFreqDen = result.findViewById(R.id.input_freq_den);
    llCustomFrequency = result.findViewById(R.id.llCustomFrequency);

    sFrequency.setOnItemSelectedListener(this);
    result.findViewById(R.id.btnCreate).setOnClickListener(view -> onHabitCreate());
    ((MainActivity) getActivity()).getToolbar().setTitle(R.string.habit_create);

    return result;
  }

  private void onHabitCreate() {
    if (editTitle.length() != 0) {
      int[] frequency;
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

      int time = editTime.getText().toString().isEmpty() ?
          0 : Integer.valueOf(editTime.getText().toString());
      // Checks what data was entered and adds habit to habitsDatabase
      habitsDatabase = HabitListManager.getInstance(getContext()).getDatabase().habitDao();
      Calendar upd = Calendar.getInstance();
      Habit habit = new Habit(
          editTitle.getText().toString(),
          editQuestion.getText().toString(),
          false,
          upd.get(Calendar.DATE),
          upd.get(Calendar.MONTH),
          upd.get(Calendar.YEAR),
          time,
          System.currentTimeMillis(),
          0, frequency);
      new InsertHabitTask().execute(habit);
    } else {
      Toast.makeText(getContext().getApplicationContext(), "Please enter habit's title",
          Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onItemSelected(AdapterView<?> adapterView, View view, int i,
      long l) {
    lastSpinnerSelection = i;
    if (i == 4) {
      sFrequency.setVisibility(View.GONE);
      llCustomFrequency.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> adapterView) {
    // ignored
  }

  @Override
  public void onPause() {
    ((MainActivity) getActivity()).closeImm();
    super.onPause();
  }

  @SuppressLint("StaticFieldLeak")
  private class InsertHabitTask extends AsyncTask<Habit, Void, Long> {

    @Override
    protected Long doInBackground(Habit... habit) {
      Log.i("DatabaseContains", "New program added");
      return habitsDatabase.insert(habit[0]);
    }

    @Override
    protected void onPostExecute(Long id) {
      HabitNotifier habitNotifier = new HabitNotifier(getContext(),
          editQuestion.getText().toString(), id);
      habitNotifier.initiate();

      ((MainActivity) getActivity()).getToolbar().setTitle("List");
      getFragmentManager().beginTransaction().replace(R.id.content_frame, new ListFragment())
          .commit();
      super.onPostExecute(id);
    }
  }
}
