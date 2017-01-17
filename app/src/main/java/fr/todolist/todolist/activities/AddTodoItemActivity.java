package fr.todolist.todolist.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

import fr.todolist.todolist.R;
import fr.todolist.todolist.database.TodoItemDatabase;
import fr.todolist.todolist.fragments.DatePickerFragment;
import fr.todolist.todolist.fragments.TimePickerFragment;
import fr.todolist.todolist.interfaces.AddTodoItemInterface;
import fr.todolist.todolist.utils.StaticTools;
import fr.todolist.todolist.utils.TodoItemInfo;

public class AddTodoItemActivity extends AppCompatActivity implements AddTodoItemInterface {

    private View root;

    private EditText titleEditText;
    private EditText contentEditText;
    private EditText dateEditText;
    private EditText timeEditText;
    private Button addButton;

    private TodoItemDatabase database;
    private TodoItemInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_todo_item_activity);

        database = new TodoItemDatabase(getApplicationContext());
        database.open();
        info = new TodoItemInfo();

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        titleEditText = (EditText)findViewById(R.id.add_item_title);
        contentEditText = (EditText)findViewById(R.id.add_item_content);
        dateEditText = (EditText)findViewById(R.id.add_item_date);
        timeEditText = (EditText)findViewById(R.id.add_item_time);
        addButton = (Button)findViewById(R.id.add_item_button);
        root = findViewById(R.id.add_todo_item_root);

        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (StaticTools.keyboardIsDisplay(root)) {
                    addButton.setVisibility(View.GONE);
                } else {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addButton.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }, 100);
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onValid();
            }
        });

        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                info.title = s.toString();
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

        contentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                info.content = s.toString();
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

    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }

    private boolean confirm() {
        return (confirmTitle() && confirmContent());
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

    @Override
    public void onDateSet(int year, int month, int day) {
        dateEditText.setText(String.format(getString(R.string.format_date), year, month + 1, day));
        info.year = year;
        info.month = month;
        info.day = day;
    }

    @Override
    public void onTimeSet(int hour, int minutes) {
        timeEditText.setText(String.format(getString(R.string.format_time), hour, minutes));
        info.hour = hour;
        info.minute = minutes;
    }

    @Override
    public void onValid() {
        if (confirm()) {
            database.insertItem(info);
            setResult(RESULT_OK);
            finish();
        }
    }
}
