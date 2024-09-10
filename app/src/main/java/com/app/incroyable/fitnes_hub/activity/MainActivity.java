package com.app.incroyable.fitnes_hub.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.incroyable.fitnes_hub.R;
import com.app.incroyable.fitnes_hub.adapter.AllDayAdapter;
import com.app.incroyable.fitnes_hub.database.DatabaseOperations;
import com.app.incroyable.fitnes_hub.model.RepeatData;
import com.app.incroyable.fitnes_hub.model.WeightDay;
import com.app.incroyable.fitnes_hub.model.WorkoutData;
import com.app.incroyable.fitnes_hub.utils.AppUtils;
import com.app.incroyable.fitnes_hub.utils.Constants;
import com.app.incroyable.fitnes_hub.utils.FitnessDatabase;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AllDayAdapter.clickMethod {

    private boolean doubleBackPressed = false;
    private RecyclerView recyclerView;
    private AllDayAdapter adapter;
    private ArrayList<String> arrayList;
    private double totalProgress = 0.0;
    private TextView progressTextView;
    private TextView daysLeftTextView;
    private ProgressBar progressBar;
    private DatabaseOperations databaseOperations;
    private List<WorkoutData> workoutDataList;
    private int completedWorkouts = 0;
    private Menu navMenu;
    private NavigationView navView;
    private int workoutPosition = -1;
    private DrawerLayout drawerLayout;

    private FitnessDatabase fitnessDatabase;
    private long databaseSize;

    private final BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double progress = intent.getDoubleExtra(AppUtils.KEY_PROGRESS, 0.0d);

            try {
                updateWorkoutProgress(progress);
                updateProgressViews();
            } catch (Throwable e) {
                Log.e("Error", " => " + e.getMessage());
            }
        }

        private void updateWorkoutProgress(double progress) {
            ((WorkoutData) workoutDataList.get(workoutPosition)).setProgress((float) progress);

            totalProgress = 0.0d;
            completedWorkouts = 0;

            for (int i = 0; i < Constants.TOTAL_DAYS; i++) {
                WorkoutData workout = (WorkoutData) workoutDataList.get(i);
                totalProgress += (workout.getProgress() * 4.348d) / 100.0d;

                if (workout.getProgress() >= 99.0f) {
                    completedWorkouts++;
                }
            }

            completedWorkouts += completedWorkouts / 3;
        }

        private void updateProgressViews() {
            int progressPercentage = (int) totalProgress;
            progressBar.setProgress(progressPercentage);

            String progressText = String.format("%d%%", progressPercentage);
            progressTextView.setText(progressText);

            int daysLeft = Constants.TOTAL_DAYS - completedWorkouts;
            String daysLeftText = String.format("%d Days left", daysLeft);
            daysLeftTextView.setText(daysLeftText);

            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstTime = preferences.getBoolean("isFirstTime", true);
        if (isFirstTime) {
            initializeData();
            preferences.edit().putBoolean("isFirstTime", false).apply();
        }

        progressTextView = findViewById(R.id.percentScore);
        daysLeftTextView = findViewById(R.id.daysLeft);
        SharedPreferences launchDataPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        databaseOperations = new DatabaseOperations(this);

        if (!launchDataPreferences.getBoolean("daysInserted", false) && databaseOperations.isDatabaseEmpty() == 0) {
            databaseOperations.insertAllDayData();
            launchDataPreferences.edit().putBoolean("daysInserted", true).apply();
        }

        if (workoutDataList != null) {
            workoutDataList.clear();
        }
        workoutDataList = databaseOperations.getAllDaysProgress();

        progressBar = findViewById(R.id.progress);
        calculateProgress();
        updateProgressViews();

        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new AllDayAdapter(this, workoutDataList);
        arrayList = new ArrayList<>();
        setAdapter();
        recyclerView.setLayoutManager(layoutManager);

        populateDayList();

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
        navMenu = navView.getMenu();
        navView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ArrayList<View> viewList = new ArrayList<>();
                navView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                for (int i = 0; i < navMenu.size(); i++) {
                    if (i == 0) {
                        SpannableString spannableTitle = new SpannableString(navMenu.getItem(i).getTitle());
                        navMenu.getItem(i).setTitle(spannableTitle);
                    }
                    navView.findViewsWithText(viewList, navMenu.getItem(i).getTitle(), 1);
                }
            }
        });

        registerReceiver(progressReceiver, new IntentFilter(AppUtils.WORKOUT_BROADCAST_FILTER));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = getString(R.string.default_notification_channel_id);
            CharSequence channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            } else {
                throw new AssertionError();
            }
        }

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("IntentExtra", "Key: " + key + " Value: " + value);
            }
        }
    }

    private void calculateProgress() {
        totalProgress = 0.0d;
        completedWorkouts = 0;

        for (int i = 0; i < Constants.TOTAL_DAYS; i++) {
            WorkoutData workout = workoutDataList.get(i);
            totalProgress += (workout.getProgress() * 4.348d) / 100.0d;
            if (workout.getProgress() >= 99.0f) {
                completedWorkouts++;
            }
        }
        completedWorkouts += completedWorkouts / 3;
    }

    private void populateDayList() {
        for (int i = 1; i <= Constants.TOTAL_DAYS; i++) {
            arrayList.add("Day " + i);
        }
    }

    @Override
    public void clickCall(final int position) {
        workoutPosition = position;

        if ((workoutPosition + 1) % 4 == 0) {
            startActivity(new Intent(getApplicationContext(), RestDayActivity.class));
        } else {
            WorkoutData workoutData = (WorkoutData) workoutDataList.get(position);

            if (workoutData.getProgress() < 99.0f) {
                callMethod1(position);
            } else {
                showRepeatWorkoutDialog(position);
            }
        }
    }

    private void showRepeatWorkoutDialog(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Would you like to repeat this workout?")
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    handleRepeatWorkout(position);
                })
                .setCancelable(true)
                .create()
                .show();
    }

    private void handleRepeatWorkout(int position) {
        try {
            String day = (String) arrayList.get(position);
            databaseOperations.updateDayProgress(day, 0.0f);

            if (workoutDataList != null) {
                workoutDataList.clear();
            }
            workoutDataList = databaseOperations.getAllDaysProgress();
            adapter = new AllDayAdapter(this, workoutDataList);
            setAdapter();

            completedWorkouts--;
            totalProgress -= 4.0d;

            updateProgressViews();

            Intent intent = new Intent(getApplicationContext(), DayActivity.class);
            intent.putExtra("day", day);
            intent.putExtra("day_num", position);
            intent.putExtra(NotificationCompat.CATEGORY_PROGRESS, ((WorkoutData) workoutDataList.get(position)).getProgress());
            startActivity(intent);
        } catch (Throwable e) {
            Log.e("Error", " => " + e.getMessage());
        }
    }

    private void updateProgressViews() {
        progressTextView.setText(String.format("%d%%", (int) totalProgress));
        progressBar.setProgress((int) totalProgress);
        daysLeftTextView.setText(String.format("%d Days left", Constants.TOTAL_DAYS - completedWorkouts));
    }

    private void callMethod1(int i) {
        Intent intent = new Intent(getApplicationContext(), DayActivity.class);
        intent.putExtra("day", (String) arrayList.get(i));
        intent.putExtra("day_num", i);
        intent.putExtra(NotificationCompat.CATEGORY_PROGRESS, ((WorkoutData) workoutDataList.get(i)).getProgress());
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();

        if (itemId == R.id.reminder) {
            startActivity(new Intent(getApplicationContext(), AlarmMainActivity.class));

        } else if (itemId == R.id.dietplan) {
            startActivity(new Intent(getApplicationContext(), DietPlanActivity.class));

        } else if (itemId == R.id.restartprogress) {
            callMethod();

        } else if (itemId == R.id.share) {
            shareApp();

        } else if (itemId == R.id.rateus) {
            openPlayStore(getApplicationContext());
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    private void callMethod() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        DatabaseOperations databaseOperations = new DatabaseOperations(this);

        resetDatabase(databaseOperations);
        updateSharedPreferences(sharedPreferences);
        updateUI(databaseOperations);
    }

    private void resetDatabase(DatabaseOperations databaseOperations) {
        databaseOperations.deleteTable();
        databaseOperations.insertAllDayData();
    }

    private void updateSharedPreferences(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("daysInserted", false);
        editor.apply();
        editor.putBoolean("daysInserted", true);
        editor.apply();
    }

    private void updateUI(DatabaseOperations databaseOperations) {
        workoutDataList.clear();
        workoutDataList = databaseOperations.getAllDaysProgress();

        adapter = new AllDayAdapter(this, workoutDataList);
        setAdapter();

        progressBar.setProgress(0);
        progressTextView.setText("0%");

        String daysLeftText = String.format("%d Days left", Constants.TOTAL_DAYS);
        daysLeftTextView.setText(daysLeftText);
    }

    public void initializeData() {
        this.fitnessDatabase = new FitnessDatabase(getApplicationContext());

        if (this.databaseSize == 0) {
            addDefaultWeightDays();
        }

        for (int i = 1; i <= 30; i++) {
            addRepDataForDay(i);
        }
    }

    private void addDefaultWeightDays() {
        for (int i = 1; i <= 30; i++) {
            String dayNumber = "Day " + i;
            this.fitnessDatabase.addDietPlanDay(new WeightDay(dayNumber, "false"));
        }
    }

    private void addRepDataForDay(int day) {
        int headerResId = getResources().getIdentifier("h_day" + day, "string", getPackageName());
        int[] textResIds = {
                getResources().getIdentifier("day" + day + "_m_0", "string", getPackageName()),
                getResources().getIdentifier("day" + day + "_m_1", "string", getPackageName()),
                getResources().getIdentifier("day" + day + "_li_0", "string", getPackageName()),
                getResources().getIdentifier("day" + day + "_li_1", "string", getPackageName()),
                getResources().getIdentifier("day" + day + "_l_0", "string", getPackageName()),
                getResources().getIdentifier("day" + day + "_l_1", "string", getPackageName()),
                getResources().getIdentifier("day" + day + "_d_0", "string", getPackageName()),
                getResources().getIdentifier("day" + day + "_d_1", "string", getPackageName())
        };

        String header = getString(headerResId);
        String[] texts = new String[textResIds.length];
        for (int i = 0; i < textResIds.length; i++) {
            texts[i] = getString(textResIds[i]);
        }

        this.fitnessDatabase.addRepData(new RepeatData(header, texts[0], texts[1], texts[2], texts[3], texts[4], texts[5], texts[6], texts[7]));
    }

    public void shareApp() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = getResources().getString(R.string.app_name) + "\n" + "Download this application using below link\n"
                + "https://play.google.com/store/apps/details?id=" + getPackageName() + "&hl=en";
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void openPlayStore(Context context) {
        if (isNetworkAvailable(context)) {
            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try {
                myAppLinkToMarket.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(myAppLinkToMarket);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "You don't have Google Play installed", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "Please Turn on Internet Connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void setAdapter() {
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackPressed) {
                super.onBackPressed();
            } else {
                doubleBackPressed = true;
                Toast.makeText(this, "Please click Back again to exit", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> doubleBackPressed = false, 2000);
            }
        }
    }
}
