package com.app.incroyable.fitnes_hub.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.incroyable.fitnes_hub.R;
import com.app.incroyable.fitnes_hub.adapter.DietAdapter;
import com.app.incroyable.fitnes_hub.model.WeightDay;
import com.app.incroyable.fitnes_hub.utils.FitnessDatabase;

import java.util.ArrayList;
import java.util.List;

public class DietPlanFragment extends Fragment {

    public static String day_name;
    public static int pos;
    private final ArrayList<WeightDay> daysList = new ArrayList<>();
    private RecyclerView daysRecyclerView;

    private class ResetClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            showResetConfirmationDialog();
        }

        private void showResetConfirmationDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getActivity().getResources().getString(R.string.app_name));
            builder.setMessage("Reset Progress?");
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.setPositiveButton("Yes", (dialog, which) -> resetDietPlan());
            builder.setCancelable(true);
            builder.create().show();
        }

        private void resetDietPlan() {
            resetDatabase();
            loadDietPlanDetails();
            Toast.makeText(getContext(), "Reset Done", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diet_plan, container, false);

        Toolbar toolbar = view.findViewById(R.id.exc_details_layout_mtoolbar);
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());

        TextView resetText = view.findViewById(R.id.reset_txt);
        daysRecyclerView = view.findViewById(R.id.rv_days);
        daysRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        loadDietPlanDetails();
        resetText.setOnClickListener(new ResetClickListener());

        return view;
    }

    private void loadDietPlanDetails() {
        daysList.clear();
        List<WeightDay> dietPlanDays = new FitnessDatabase(getContext()).getDietPlanDays();
        if (dietPlanDays != null) {
            for (WeightDay day : dietPlanDays) {
                daysList.add(new WeightDay(day.getW_day_id(), day.getDay_no(), day.getDay_value()));
            }
        }
        daysRecyclerView.setAdapter(new DietAdapter(daysList, this));
    }

    private void resetDatabase() {
        FitnessDatabase db = new FitnessDatabase(getContext());
        for (int i = 1; i <= 30; i++) {
            db.updateDietPlanDayValue("Day " + i, "false");
        }
    }
}

