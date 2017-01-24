package fr.todolist.todolist.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
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

    private View recurrenceParent;
    private EditText priorityEditText;
    private EditText recurrenceTimeEditText;
    private EditText recurrenceIntervalEditText;
    private TextView recurrenceLabelIntervalTextView;

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

        recurrenceParent = findViewById(R.id.add_item_recurrence_parent);
        priorityEditText = (EditText)findViewById(R.id.add_item_priority);
        recurrenceTimeEditText = (EditText)findViewById(R.id.add_item_recurrence_time);
        recurrenceIntervalEditText = (EditText)findViewById(R.id.add_item_recurrence_interval);
        recurrenceLabelIntervalTextView = (TextView)findViewById(R.id.add_item_recurrence_interval_label);

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

        reminderSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    recurrenceParentOpen();
                } else {
                    recurrenceParentClose();
                }
            }
        });

        recurrenceIntervalEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRecurrenceIntervalDialog();
            }
        });

        priorityEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPriorityDialog();
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

            if (recurrenceTimeEditText.length() > 0) {
                info.nbRecurrence = Integer.valueOf(recurrenceTimeEditText.getText().toString()) - 1;
                info.nbBaseRecurrence = info.nbRecurrence;
            }

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

    private void createRecurrenceIntervalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();

        final View view = inflater.inflate(R.layout.recurrence_interval, null);

        final Spinner spinner = (Spinner)view.findViewById(R.id.recurrence_unit_time);
        final EditText value = (EditText)view.findViewById(R.id.recurrence_value);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.unit_time, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        builder.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedType = spinner.getSelectedItem().toString();
                        String v = value.getText().toString();

                        if (v.length() > 0) {
                            recurrenceIntervalEditText.setText(v);
                            recurrenceLabelIntervalTextView.setText(selectedType);

                            info.intervalRecurrence = DateTimeManager.getMs(Integer.valueOf(v), selectedType);
                            StaticTools.hideKeyboard(getApplicationContext(), view);
                        }
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        StaticTools.hideKeyboard(getApplicationContext(), view);
                    }
                })
                .create()
                .show();

        StaticTools.showKeyboard(getApplicationContext());
    }

    private void createPriorityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();

        final View view = inflater.inflate(R.layout.priority_dialog, null);

        RadioGroup radioGroup = (RadioGroup)view.findViewById(R.id.radio_parent);
        if (info.priority == NotificationCompat.PRIORITY_DEFAULT) {
            ((RadioButton)radioGroup.findViewById(R.id.radio_default)).setChecked(true);
        } else if (info.priority == NotificationCompat.PRIORITY_HIGH) {
            ((RadioButton)radioGroup.findViewById(R.id.radio_high)).setChecked(true);
        } else if (info.priority == NotificationCompat.PRIORITY_MAX) {
            ((RadioButton)radioGroup.findViewById(R.id.radio_max)).setChecked(true);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.findViewById(checkedId) == group.findViewById(R.id.radio_default)) {
                    priorityEditText.setText(getString(R.string.priority_default));
                    info.priority = NotificationCompat.PRIORITY_DEFAULT;
                } else if (group.findViewById(checkedId) == group.findViewById(R.id.radio_high)) {
                    priorityEditText.setText(getString(R.string.priority_high));
                    info.priority = NotificationCompat.PRIORITY_HIGH;
                } else if (group.findViewById(checkedId) == group.findViewById(R.id.radio_max)) {
                    priorityEditText.setText(getString(R.string.priority_max));
                    info.priority = NotificationCompat.PRIORITY_MAX;
                }
            }
        });

        builder.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    private void recurrenceParentOpen() {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_from_left);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                recurrenceParent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        recurrenceParent.startAnimation(animation);
    }

    private void recurrenceParentClose() {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_to_left);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                recurrenceParent.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        recurrenceParent.startAnimation(animation);
    }
}
