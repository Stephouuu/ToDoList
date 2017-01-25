package fr.todolist.todolist.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    public enum Mode {
        Add,
        Consultation,
        Edit,
    }

    static public final String EXTRA_MODE = "mode";
    static public final String EXTRA_ITEM = "item";

    private View root;
    private Mode mode;

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

    static public void setExtraMode(Intent intent, Mode value) {
        intent.putExtra(EXTRA_MODE, value.ordinal());
    }

    static public Mode getExtraMode(Intent intent) {
        int mode = intent.getIntExtra(EXTRA_MODE, Mode.Add.ordinal());
        Mode ret = Mode.Add;
        if (mode == Mode.Add.ordinal()) {
            ret = Mode.Add;
        } else if (mode == Mode.Consultation.ordinal()) {
            ret = Mode.Consultation;
        } else if (mode == Mode.Edit.ordinal()) {
            ret = Mode.Edit;
        }
        return (ret);
    }

    static public void setExtraItem(Intent intent, TodoItemInfo item) {
        intent.putExtra(EXTRA_ITEM, item);
    }

    static public TodoItemInfo getExtraItem(Intent intent) {
        return (intent.getParcelableExtra(EXTRA_ITEM));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_todo_item_activity);

        database = new AppDatabase(getApplicationContext());
        database.open();

        info = getExtraItem(getIntent());
        if (info == null) {
            info = new TodoItemInfo();
        }

        mode = getExtraMode(getIntent());

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
                if (mode == Mode.Add || mode == Mode.Edit) {
                    onValid();
                } else {
                    supportInvalidateOptionsMenu();
                    mode = Mode.Edit;
                    childSetEnabled(true);
                    addFab.setImageResource(R.mipmap.ic_valid_white);
                }
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

        if (mode == Mode.Consultation) {
            addFab.setImageResource(R.mipmap.menu_icon_edit);
            refreshEditMode();
        } else if (mode == Mode.Add) {
            addFab.setImageResource(R.mipmap.menu_icon_send);
            StaticTools.showKeyboard(getApplicationContext());
        } else if (mode == Mode.Edit) {
            addFab.setImageResource(R.mipmap.ic_valid_white);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mode == Mode.Consultation) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.add_item_activity_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_item_menu_done) {
            done();
            return true;
        } else if (item.getItemId() == R.id.add_item_menu_delete) {
            delete();
            return true;
        }
        return false;
    }

    private void done() {
        AlarmReceiver.deleteAlarm(getApplicationContext(), (int)info.id);
        info.status = TodoItemInfo.Status.Done;
        database.updateItem(info);
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void delete() {
        AlarmReceiver.deleteAlarm(getApplicationContext(), (int)info.id);
        database.deleteItem(info.id);
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void refreshEditMode() {
        titleEditText.setText(info.title);
        contentEditText.setText(info.content);
        onDateSet(info.year, info.month, info.day);
        onTimeSet(info.hour, info.minute);
        reminderSwitchCompat.setChecked(info.remind);

        if (info.priority == NotificationCompat.PRIORITY_DEFAULT) {
            priorityEditText.setText(getString(R.string.priority_default));
        } else if (info.priority == NotificationCompat.PRIORITY_HIGH) {
            priorityEditText.setText(getString(R.string.priority_high));
        } else if (info.priority == NotificationCompat.PRIORITY_MAX) {
            priorityEditText.setText(getString(R.string.priority_max));
        }

        recurrenceTimeEditText.setText(String.valueOf(info.nbRecurrence + 1));
        recurrenceIntervalEditText.setText(String.valueOf(DateTimeManager.getValueFromMs(info.intervalType, info.intervalRecurrence)));
        recurrenceLabelIntervalTextView.setText(info.intervalType);

        childSetEnabled(false);
    }

    private void childSetEnabled(boolean value) {
        titleEditText.setEnabled(value);
        contentEditText.setEnabled(value);
        dateEditText.setEnabled(value);
        timeEditText.setEnabled(value);
        reminderSwitchCompat.setEnabled(value);
        priorityEditText.setEnabled(value);
        recurrenceTimeEditText.setEnabled(value);
        recurrenceIntervalEditText.setEnabled(value);
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
                if (mode == Mode.Add) {
                    info = database.insertItem(info);
                    AlarmReceiver.addAlarm(this, info, time);
                    database.close();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    info.status = TodoItemInfo.Status.ToDo;
                    database.updateItem(info);
                    AlarmReceiver.deleteAlarm(this, (int)info.id);
                    AlarmReceiver.addAlarm(this, info, time);
                    childSetEnabled(false);
                    mode = Mode.Consultation;
                    addFab.setImageResource(R.mipmap.menu_icon_edit);
                    Toast.makeText(this, R.string.edit_save, Toast.LENGTH_SHORT).show();
                    supportInvalidateOptionsMenu();
                }
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
                        info.intervalType = spinner.getSelectedItem().toString();
                        String v = value.getText().toString();

                        if (v.length() > 0) {
                            recurrenceIntervalEditText.setText(v);
                            recurrenceLabelIntervalTextView.setText(info.intervalType);
                            info.intervalRecurrence = DateTimeManager.getMs(Integer.valueOf(v), info.intervalType);
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
