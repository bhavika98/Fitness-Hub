package com.app.incroyable.fitnes_hub.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.app.incroyable.fitnes_hub.R;
import com.app.incroyable.fitnes_hub.fragment.DietPlanDetailsFragment;
import com.app.incroyable.fitnes_hub.fragment.DietPlanFragment;
import com.app.incroyable.fitnes_hub.model.WeightDay;

import java.util.ArrayList;

public class DietAdapter extends RecyclerView.Adapter<DietAdapter.MyViewHolder> {

    private final ArrayList<WeightDay> dietPlanList;
    private final DietPlanFragment dietPlanFragment;

    public DietAdapter(ArrayList<WeightDay> dietPlanList, DietPlanFragment dietPlanFragment) {
        this.dietPlanList = dietPlanList;
        this.dietPlanFragment = dietPlanFragment;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout clickLayout;
        ImageView completedImage;
        TextView dayText;
        CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            dayText = view.findViewById(R.id.day_txt);
            clickLayout = view.findViewById(R.id.click_rel);
            completedImage = view.findViewById(R.id.completed_img);
            cardView = view.findViewById(R.id.cardView);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_diet, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        WeightDay currentDay = dietPlanList.get(position);

        if (currentDay.getDay_value().equals("false")) {
            holder.completedImage.setVisibility(View.GONE);
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.dayText.setTextColor(dietPlanFragment.getResources().getColor(R.color.lightestgrey));
        } else {
            holder.completedImage.setVisibility(View.VISIBLE);
            holder.cardView.setCardBackgroundColor(dietPlanFragment.getResources().getColor(R.color.colorPrimary));
            holder.dayText.setTextColor(dietPlanFragment.getResources().getColor(R.color.white));
        }

        holder.dayText.setText(dietPlanFragment.getResources().getString(R.string.day) + " " + (position + 1));

        holder.clickLayout.setOnClickListener(view -> {
            DietPlanFragment.pos = position;
            DietPlanFragment.day_name = currentDay.getDay_no();
            showFragment(dietPlanFragment, new DietPlanDetailsFragment());
        });
    }

    private void showFragment(DietPlanFragment context, Fragment fragment) {
        FragmentTransaction transaction = context.getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentview, fragment, "DietPlanDetailsFragment");
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();

    }

    @Override
    public int getItemCount() {
        return dietPlanList.size();
    }
}

