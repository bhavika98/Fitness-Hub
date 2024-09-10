package com.app.incroyable.fitnes_hub.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.incroyable.fitnes_hub.R;
import com.app.incroyable.fitnes_hub.library.FAImageView;

public class ExcDetailsActivity extends AppCompatActivity {

    private FAImageView animImageFull;
    private TextView descriptionTextView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exc_details);

        initializeViews();
        setupToolbar();
        setupAnimation();
    }

    private void initializeViews() {
        descriptionTextView = findViewById(R.id.description_exDetail);
        animImageFull = findViewById(R.id.animation_exDetail);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.exc_details_layout_mtoolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.exc_details_layout_toolbar_title);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String exerciseName = extras.getString("excName", "").replace("_", " ").toUpperCase();
            toolbarTitle.setText(exerciseName);
            int descriptionResId = extras.getInt("excNameDescResId", -1);
            descriptionTextView.setText(descriptionResId);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupAnimation() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int[] frameIds = extras.getIntArray("framesIdArray");
            if (frameIds != null) {
                animImageFull.setInterval(1000);
                animImageFull.setLoop(true);
                animImageFull.reset();
                for (int frameId : frameIds) {
                    animImageFull.addImageFrame(frameId);
                }
                animImageFull.startAnimation();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

