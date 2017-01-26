package fr.todolist.todolist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.File;

import fr.todolist.todolist.R;

public class ImageActivity extends AppCompatActivity {

    private static final String EXTRA_PHOTO = "photo.path";

    static void setExtraPhoto(Intent intent, String photo) {
        intent.putExtra(EXTRA_PHOTO, photo);
    }

    static String getExtraPhoto(Intent intent) {
        return  intent.getStringExtra(EXTRA_PHOTO);
    }

    private SubsamplingScaleImageView imageView;

    private String photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        photo = getExtraPhoto(getIntent());

        imageView = (SubsamplingScaleImageView)findViewById(R.id.image);

        refreshImage();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        return true;
    }

    private void refreshImage() {
        File file = new File(photo);
        if (file.exists()) {
            imageView.setImageUri(photo);
        }
    }

}
