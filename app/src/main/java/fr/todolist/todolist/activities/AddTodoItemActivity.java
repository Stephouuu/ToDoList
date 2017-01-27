package fr.todolist.todolist.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import fr.todolist.todolist.R;
import fr.todolist.todolist.adapters.ImageGridAdapter;
import fr.todolist.todolist.adapters.PhotoAppsAdapter;
import fr.todolist.todolist.database.AppDatabase;
import fr.todolist.todolist.fragments.DatePickerFragment;
import fr.todolist.todolist.fragments.TimePickerFragment;
import fr.todolist.todolist.interfaces.AddTodoItemInterface;
import fr.todolist.todolist.interfaces.ImageGridInterface;
import fr.todolist.todolist.receivers.AlarmReceiver;
import fr.todolist.todolist.utils.DateTimeManager;
import fr.todolist.todolist.utils.StaticTools;
import fr.todolist.todolist.utils.TodoItemInfo;
import fr.todolist.todolist.views.AutoResizableGridView;

/**
 * This class is used to manage the Add/Edition/consultation task
 */
public class AddTodoItemActivity extends AppCompatActivity implements AddTodoItemInterface, ImageGridInterface {

    /**
     * Mode of this activity
     */
    public enum Mode {
        Add,
        Consultation,
        Edit,
    }

    private static final String TEMP_FOLDER = "temp";

    private static final String EXTRA_MODE = "mode";
    private static final String EXTRA_ITEM = "item";
    private static final String EXTRA_PHOTOS = "photos";

    private final static String PHOTO_PREFIX = "photo";
    private final static String PHOTO_SUFFIX = ".jpg";

    private static final int REQUEST_CODE_PHOTO_CAPTURE = 3;
    private static final int REQUEST_CODE_PHOTO_IMPORT = 4;

    private View root;
    private Mode mode;

    private EditText titleEditText;
    private SwitchCompat reminderSwitchCompat;
    private EditText contentEditText;
    private EditText dateEditText;
    private EditText timeEditText;
    private FloatingActionButton addFab;
    private AutoResizableGridView gridView;

    private AppDatabase database;
    private TodoItemInfo info;

    private View recurrenceParent;
    private EditText priorityEditText;
    private EditText recurrenceTimeEditText;
    private EditText recurrenceIntervalEditText;
    private TextView recurrenceLabelIntervalTextView;

    private PhotoAppsAdapter publishAdapter;

    private ImageGridAdapter adapter;

    private String photoToUpload;

    /**
     * Return the photos used by the activity
     * @param intent The intent of the activity
     * @return A list of path corresponding to the photos
     */
    public static ArrayList<String> getPhotos(Intent intent) {
        ArrayList<String> strings = intent.getStringArrayListExtra(EXTRA_PHOTOS);
        return strings;
    }

    /**
     * Set the photos used by the activity
     * @param intent The intent to stock the photos
     * @param value A list of photo path
     */
    public static void setPhotos(Intent intent, ArrayList<String> value) {
        intent.putStringArrayListExtra(EXTRA_PHOTOS, value);
    }

    /**
     * Set the mode of this activity
     * @param intent The intent of the activity
     * @param value See Mode
     */
    static public void setExtraMode(Intent intent, Mode value) {
        intent.putExtra(EXTRA_MODE, value.ordinal());
    }

    /**
     * Get the mode of this activity
     * @param intent The intent of this activity
     * @return The mode
     */
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

    /**
     * Set the default item
     * @param intent The intent
     * @param item The item
     */
    static public void setExtraItem(Intent intent, TodoItemInfo item) {
        intent.putExtra(EXTRA_ITEM, item);
    }

    /**
     * Get the default Item
     * @param intent The intent
     * @return The item
     */
    static public TodoItemInfo getExtraItem(Intent intent) {
        return (intent.getParcelableExtra(EXTRA_ITEM));
    }

    /**
     * Create the activity
     * @param savedInstanceState The saved instance state of the previous activity
     */
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
        gridView = (AutoResizableGridView) findViewById(R.id.image_gridview);

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

        ArrayList<String> list = new ArrayList<>();
        String[] photos = info.photos.split(";");
        for (String photo : photos) {
            if (!photo.isEmpty()) {
                list.add(photo);
            }
        }
        setPhotos(getIntent(), list);

        adapter = new ImageGridAdapter(this, list, this);
        gridView.setAdapter(adapter);

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

    /**
     * Called after startActivityForResult
     * @param requestCode The requestCode
     * @param resultCode The resultCode
     * @param data The data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_PHOTO_CAPTURE:
                if (resultCode == RESULT_OK) {
                    addPhotoCallback(Uri.fromFile(getCapturePhotoFile()));
                }
                break;
            case REQUEST_CODE_PHOTO_IMPORT:
                if (resultCode == RESULT_OK && data != null) {
                    addPhotoCallback(data.getData());
                }
                break;
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

    /**
     * Resize the grid of the illustrations
     * @param size the number of item in
     */
    public void resizeGrid(int size) {
        float nbLine = (float) (size) / (float) gridView.getNumColumns();
        nbLine = (float) Math.ceil(nbLine);
        gridView.setNbLine((int) nbLine);
    }

    /**
     * Set the status to "done" for the current item
     */
    private void done() {
        AlarmReceiver.deleteAlarm(getApplicationContext(), (int)info.id);
        info.status = TodoItemInfo.Status.Done;
        database.updateItem(info);
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    /**
     * Delete the item
     */
    private void delete() {
        StaticTools.deleteFiles(StaticTools.deserializeFiles(info.photos, ";"));
        AlarmReceiver.deleteAlarm(getApplicationContext(), (int)info.id);
        database.deleteItem(info.id);
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    /**
     * Refresh the edit mode
     */
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
        refreshImages();
    }

    /**
     * Enable or disable the fields
     * @param value True or False
     */
    private void childSetEnabled(boolean value) {
        titleEditText.setEnabled(value);
        contentEditText.setEnabled(value);
        dateEditText.setEnabled(value);
        timeEditText.setEnabled(value);
        reminderSwitchCompat.setEnabled(value);
        priorityEditText.setEnabled(value);
        recurrenceTimeEditText.setEnabled(value);
        recurrenceIntervalEditText.setEnabled(value);
        gridView.setEnabled(value);
    }

    /**
     * Refresh the illustrations
     */
    private void refreshImages() {
        String[] photos = info.photos.split(";");
        ArrayList<String> list = new ArrayList<>();
        Log.i("image", "nb: " + photos.length);
        for (String photo : photos) {
            Log.i("image", photo);
            list.add(photo);
        }
        adapter.setAll(list);
    }

    /**
     * Confirm field for save the data
     * @return
     */
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
                addPhotoToTodoItem();
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

    /**
     * Add photos in the to do item for save it in the database
     */
    private void addPhotoToTodoItem() {
        info.photos = "";
        ArrayList<String> photos = getPhotos(getIntent());
        if (photos != null) {
            for (int i = 0 ; i < photos.size() ; ++i) {
                if (i > 0) {
                    info.photos += ";";
                }
                info.photos += photos.get(i);
            }
        }
    }

    /**
     * Create the interval dialog for define the interval of the reminder
     */
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

    /**
     * Create the dialog of the priority notification select
     */
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(publishAdapter.getIntent(0), publishAdapter.getRequestCode(0));
                final LinearLayout popUpBg = (LinearLayout) findViewById(R.id.add_image_parent);
                popUpBg.setVisibility(View.GONE);
            }
        }
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

    private void selectPhotoSource() {
        File captureFile = getCapturePhotoFile();
        if (captureFile != null) {

            ListView publishList = (ListView) findViewById(R.id.publish_popup_list);
            final LinearLayout popUpBg = (LinearLayout) findViewById(R.id.add_image_parent);
            TextView title = (TextView) findViewById(R.id.publish_popup_title);

            popUpBg.setVisibility(View.VISIBLE);

            publishAdapter = new PhotoAppsAdapter(this, captureFile,
                    REQUEST_CODE_PHOTO_CAPTURE, REQUEST_CODE_PHOTO_IMPORT);
            publishList.setAdapter(publishAdapter);
            publishList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (publishAdapter.getPage() == 0) {
                        if (position == 0) {
                            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(AddTodoItemActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
                            } else {
                                startActivityForResult(publishAdapter.getIntent(position), publishAdapter.getRequestCode(position));
                                popUpBg.setVisibility(View.GONE);
                            }
                        } else if (position == 1) {
                            publishAdapter.nextPage();
                        } else {
                            publishAdapter.setPage(2);
                        }
                    } else if (publishAdapter.getPage() == 1) {
                        startActivityForResult(publishAdapter.getIntent(position), publishAdapter.getRequestCode(position));
                        publishAdapter.setPage(0);
                        popUpBg.setVisibility(View.GONE);
                    } else if (publishAdapter.getPage() == 2) {
                        startActivityForResult(publishAdapter.getIntent(position), publishAdapter.getRequestCode(position));
                        publishAdapter.setPage(0);
                        popUpBg.setVisibility(View.GONE);
                    }
                }
            });

            popUpBg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popUpBg.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * Get the capture photo file
     * @return The new File
     */
    private File getCapturePhotoFile() {
        File dir = getTempPhotoDir();
        if (dir != null) {
            dir.mkdirs();
            return new File(dir, PHOTO_PREFIX + PHOTO_SUFFIX);
        }
        return null;
    }

    @Nullable
    private File getTempPhotoDir() {
        File dir = getExternalCacheDir();
        if (dir != null) {
            String path = dir.getAbsolutePath();
            path += File.separator + TEMP_FOLDER;
            return new File(path);
        }
        return null;
    }

    private File createTempPhotoFile() {
        File dir = getTempPhotoDir();
        if (dir != null) {
            dir.mkdirs();
            try {
                return File.createTempFile(PHOTO_PREFIX, PHOTO_SUFFIX, dir);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void addPhotoCallback(Uri uri) {
        InputStream input = null;
        try {
            input = getContentResolver().openInputStream(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (input == null) {
            Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
            return;
        }
        final String extension = uri.toString().substring(uri.toString().length() - 4);
        File file = null;
        int pos = uri.toString().indexOf(".");

        if (pos == -1 || extension.equalsIgnoreCase(".jpg") || extension.equalsIgnoreCase(".png") || extension.equalsIgnoreCase(".jpeg")) {
            file = createTempPhotoFile();
        }

        if (file == null) {
            Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
            return;
        }
        boolean copied = StaticTools.copyStreamToFile(input, file);
        if (!copied) {
            Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
            return;
        }
        ArrayList<String> photos = getPhotos(getIntent());
        if (photos == null) {
            photos = new ArrayList<>();
        }
        photoToUpload = file.getAbsolutePath();
        rotateImage(photoToUpload);

        final String ext = photoToUpload.substring(photoToUpload.lastIndexOf('.'));
        if ((ext.equalsIgnoreCase(".jpeg") || ext.equalsIgnoreCase(".png")
                || ext.equalsIgnoreCase(".jpg")) && !uri.toString().contains("video")) {
            photos.add(photoToUpload);
            resizeGrid(photos.size() + 1);
            setPhotos(getIntent(), photos);
            adapter.setAll(getPhotos(getIntent()));
        } else {
            Toast.makeText(this, "Format de fichier non pris en charge", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Rotate the image accordingly to the camera orientation
     * @param photo The path of the photo
     */
    private void rotateImage(String photo) {
        ExifInterface exif;
        try {
            exif = new ExifInterface(photo);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            if (orientation != 0) {
                File file = new File(photo);
                Bitmap bitmap = BitmapFactory.decodeFile(photo);
                Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                FileOutputStream out = new FileOutputStream(file);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                scaledBitmap.recycle();
                bitmap.recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove the photo
     * @param photo
     * @param confirm
     */
    private void removePhoto(final String photo, boolean confirm) {
        ArrayList<String> photos = getPhotos(getIntent());
        if (photos != null) {
            if (photos.contains(photo)) {
                if (!confirm) {
                        new android.app.AlertDialog.Builder(this)
                                .setTitle("Delete Illustration")
                                .setMessage("Do you want to delete this picture ?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        removePhoto(photo, true);
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                } else if (photos.remove(photo)) {
                    setPhotos(getIntent(), photos);
                    adapter.setAll(getPhotos(getIntent()));
                }
            }
        }
    }

    /**
     * Callback called when the user want to delete an illustration
     * @param toDelete The photo to delete
     */
    @Override
    public void onDeleteClick(String toDelete) {
        if (mode != Mode.Consultation) {
            removePhoto(toDelete, false);
            resizeGrid(adapter.getCount() + 1);
        }
    }

    /**
     * Callback called when the user want to add an illustration
     */
    @Override
    public void onAddButtonClick() {
        if (mode != Mode.Consultation) {
            selectPhotoSource();
        }
    }

    /**
     * Callback called when the user want to examine an illustration
     * @param photo The photo
     */
    @Override
    public void onItemClick(String photo) {
        Intent intent = new Intent(this, ImageActivity.class);
        ImageActivity.setExtraPhoto(intent, photo);
        startActivity(intent);
    }
}
