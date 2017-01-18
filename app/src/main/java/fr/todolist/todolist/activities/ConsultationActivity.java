package fr.todolist.todolist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import fr.todolist.todolist.R;
import fr.todolist.todolist.utils.TodoItemInfo;

public class ConsultationActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM = "consultation.item";

    public static void setExtraItem(Intent intent, TodoItemInfo item) {
        intent.putExtra(EXTRA_ITEM, item);
    }

    @NonNull
    public static TodoItemInfo getExtraItem(Intent intent) {
        return (intent.getParcelableExtra(EXTRA_ITEM));
    }

    private TodoItemInfo item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultation_activity);

        item = getExtraItem(getIntent());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((TextView)toolbar.findViewById(R.id.title)).setText(item.title);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        return true;
    }

}
