package com.app.incroyable.fitnes_hub.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.incroyable.fitnes_hub.R;
import com.app.incroyable.fitnes_hub.database.DatabaseOperations;
import com.app.incroyable.fitnes_hub.model.WorkoutData;
import com.app.incroyable.fitnes_hub.utils.Constants;

import java.util.List;

public class CompletionExcActivity extends AppCompatActivity {

    private int completionCounter = 0;
    private LinearLayoutManager layoutManager;
    private TextView congratsCompleteTextView;
    private int daysCompletionCounter = 0;
    private int totalExercises;
    private int totalMinutes;
    private ImageView shareImageView;
    private TextView durationTextView;
    private TextView exerciseCountTextView;
    private Context context;
    private List<WorkoutData> workoutDataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exc_completion);

        initializeViews();
        loadDataFromIntent();
        setTextViews();
        setupClickListeners();

        workoutDataList = new DatabaseOperations(this).getAllDaysProgress();
        calculateCompletionDays();
    }

    @Override
    public void onBackPressed() {
        if (Constants.TOTAL_DAYS - daysCompletionCounter == 0) {
            showAllCompletionDialog();
        } else if (completionCounter == 0) {
            showShareConfirmDialog();
        } else {
            navigateToMainActivity();
            super.onBackPressed();
        }
    }

    private void initializeViews() {
        shareImageView = findViewById(R.id.shareimage_Congrats);
        durationTextView = findViewById(R.id.congrts_duration);
        exerciseCountTextView = findViewById(R.id.congrts_ExNo);
        congratsCompleteTextView = findViewById(R.id.congrts_complete);
        context = this;
    }

    private void loadDataFromIntent() {
        totalExercises = getIntent().getIntExtra("totalExc", 0);
        totalMinutes = getIntent().getIntExtra("totalTime", 0);
    }

    private void setTextViews() {
        String duration = String.format("%02d:%02d", totalMinutes / 60, totalMinutes % 60);
        durationTextView.setText(duration);
        exerciseCountTextView.setText(String.valueOf(totalExercises));

        Bundle extras = getIntent().getExtras();
        String dayText = extras != null ? extras.getString("day") : "";
        congratsCompleteTextView.setText(dayText + " Completed");
    }

    private void setupClickListeners() {
        shareImageView.setOnClickListener(v -> shareApp());
        findViewById(R.id.closeimage_Congrats).setOnClickListener(v -> {
            if (Constants.TOTAL_DAYS - daysCompletionCounter == 0) {
                showAllCompletionDialog();
            } else if (completionCounter == 0) {
                showShareConfirmDialog();
            } else {
                navigateToMainActivity();
                onBackPressed();
            }
        });
    }

    private void shareApp() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = getString(R.string.app_name) + "\nDownload this application using the link:\n"
                + "https://play.google.com/store/apps/details?id=" + getPackageName() + "&hl=en";
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void showShareConfirmDialog() {
        final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_completion);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(layoutParams);

        dialog.setCancelable(true);
        dialog.findViewById(R.id.shareit).setOnClickListener(v -> {
            dialog.dismiss();
            shareApp();
        });
        dialog.findViewById(R.id.close_share).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        completionCounter++;
    }

    private void showAllCompletionDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.congrats)
                .setMessage(R.string.completedworkout)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    DatabaseOperations dbOps = new DatabaseOperations(getApplicationContext());
                    dbOps.deleteTable();

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("daysInserted", false);
                    editor.apply();

                    dbOps.insertAllDayData();
                    editor.putBoolean("daysInserted", true);
                    editor.apply();

                    finish();
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void calculateCompletionDays() {
        daysCompletionCounter = (int) workoutDataList.stream()
                .filter(data -> data.getProgress() >= 99.0f)
                .count();
        daysCompletionCounter += daysCompletionCounter / 3;
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

