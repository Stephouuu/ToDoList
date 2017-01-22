package fr.todolist.todolist.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import fr.todolist.todolist.R;
import fr.todolist.todolist.database.AppDatabase;
import fr.todolist.todolist.fragments.DatePickerFragment;
import fr.todolist.todolist.fragments.TimePickerFragment;
import fr.todolist.todolist.interfaces.AddTodoItemInterface;
import fr.todolist.todolist.receivers.AlarmReceiver;
import fr.todolist.todolist.utils.DateTimeManager;
import fr.todolist.todolist.utils.StaticTools;
import fr.todolist.todolist.utils.TodoItemInfo;

public class AddTodoItemActivity extends AppCompatActivity implements AddTodoItemInterface {

    private View root;

    private EditText titleEditText;
    private SwitchCompat reminderSwitchCompat;
    private EditText contentEditText;
    private EditText dateEditText;
    private EditText timeEditText;
    private FloatingActionButton addFab;

    private AppDatabase database;
    private TodoItemInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_todo_item_activity);

        database = new AppDatabase(getApplicationContext());
        database.open();
        info = new TodoItemInfo();

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        root = findViewById(R.id.add_todo_item_root);
        titleEditText = (EditText)findViewById(R.id.add_item_title);
        contentEditText = (EditText)findViewById(R.id.add_item_content);
        dateEditText = (EditText)findViewById(R.id.add_item_date);
        timeEditText = (EditText)findViewById(R.id.add_item_time);
        addFab = (FloatingActionButton)findViewById(R.id.add_item);
        reminderSwitchCompat = (SwitchCompat)findViewById(R.id.add_todo_item_reminder);

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onValid();
            }
        });

        titleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    confirmTitle();
                }
            }
        });

        contentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    confirmContent();
                }
            }
        });

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticTools.hideKeyboard(getApplicationContext(), root);
                DialogFragment dialog = new DatePickerFragment();
                dialog.show(getSupportFragmentManager(), "Date");
            }
        });

        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticTools.hideKeyboard(getApplicationContext(), root);
                DialogFragment dialog = new TimePickerFragment();
                dialog.show(getSupportFragmentManager(), "Time");
            }
        });

        StaticTools.showKeyboard(getApplicationContext());


    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        StaticTools.hideKeyboard(getApplicationContext(), root);
    }

    private boolean confirm() {
        return (confirmTitle() && confirmContent() && confirmDate()
                && confirmTime());
    }

    private boolean confirmTitle() {
        if (titleEditText.getText().toString().trim().length() == 0) {
            titleEditText.setError(getResources().getString(R.string.empty));
            return false;
        }
        titleEditText.setError(null);
        return true;
    }

    private boolean confirmContent() {
        if (contentEditText.getText().toString().trim().length() == 0) {
            contentEditText.setError(getResources().getString(R.string.empty));
            return false;
        }
        contentEditText.setError(null);
        return true;
    }

    private boolean confirmDate() {
        if (dateEditText.getText().toString().trim().length() == 0) {
            dateEditText.setError(getResources().getString(R.string.empty));
            return false;
        }
        dateEditText.setError(null);
        return true;
    }

    private boolean confirmTime() {
        if (timeEditText.getText().toString().trim().length() == 0) {
            timeEditText.setError(getResources().getString(R.string.empty));
            return false;
        }
        timeEditText.setError(null);
        return true;
    }

    @Override
    public void onDateSet(int year, int month, int day) {
        dateEditText.setText(getString(R.string.format_date_usr, day, DateTimeManager.getMonth(month), year));
        info.year = year;
        info.month = month;
        info.day = day;
    }

    @Override
    public void onTimeSet(int hour, int minutes) {
        info.hour = hour;
        info.minute = minutes;
        String suffixe;
        if (hour > 11) {
            suffixe = " PM";
            if (hour > 12) {
                hour = hour % 12;
            }
        } else {
            suffixe = " AM";
        }
        timeEditText.setText(String.format(getString(R.string.format_time), hour, minutes, suffixe));
    }

    @Override
    public void onValid() {
        if (confirm()) {
            info.title = titleEditText.getText().toString();
            info.content = contentEditText.getText().toString();
            info.remind = reminderSwitchCompat.isChecked();
            info.dateTime = DateTimeManager.formatDateTime(info.year, info.month, info.day, info.hour, info.minute);
            long time = DateTimeManager.castDateTimeToUnixTime(info.dateTime);

            if (DateTimeManager.isDateTimeValid(time)) {
                info = database.insertItem(info);
                AlarmReceiver.addAlarm(this, info, time);
                database.close();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "The due date must be in the future", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
