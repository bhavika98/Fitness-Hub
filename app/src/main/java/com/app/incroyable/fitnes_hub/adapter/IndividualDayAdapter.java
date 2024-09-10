package com.app.incroyable.fitnes_hub.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.incroyable.fitnes_hub.R;
import com.app.incroyable.fitnes_hub.library.FAImageView;
import com.app.incroyable.fitnes_hub.model.WorkoutData;

import java.util.ArrayList;

public class IndividualDayAdapter extends RecyclerView.Adapter<IndividualDayAdapter.ViewHolder> {

    private final ArrayList<WorkoutData> workoutDataList;
    private final clickMethod clickHandler;

    public IndividualDayAdapter(Context context, ArrayList<WorkoutData> workoutDataList) {
        this.workoutDataList = workoutDataList;
        this.clickHandler = (clickMethod) context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        TextView rotation;
        FAImageView animationView;
        CardView cardView;
        LinearLayout mainLayout;

        public ViewHolder(View view) {
            super(view);
            exerciseName = view.findViewById(R.id.exerciseName);
            rotation = view.findViewById(R.id.rotation);
            animationView = view.findViewById(R.id.animation);
            cardView = view.findViewById(R.id.cardViewInRecycler);
            mainLayout = view.findViewById(R.id.mainLayout);
        }
    }

    @Override
    public int getItemCount() {
        return workoutDataList.size() + 1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_days, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (position < workoutDataList.size()) {
            holder.cardView.setVisibility(View.VISIBLE);
            setupWorkout(holder, position);
        } else {
            holder.cardView.setVisibility(View.GONE);
        }

        holder.mainLayout.setOnClickListener(v -> clickHandler.clickCall(position));
    }

    private void setupWorkout(ViewHolder holder, int position) {
        WorkoutData workoutData = workoutDataList.get(position);

        holder.animationView.setInterval(1000);
        holder.animationView.setLoop(true);
        holder.animationView.reset();

        try {
            for (int imageId : workoutData.getWorkoutList()) {
                holder.animationView.addImageFrame(imageId);
            }
            holder.animationView.startAnimation();
        } catch (Exception e) {
            Log.e("Error", " => " + e.getMessage());
        }

        holder.exerciseName.setText(workoutData.getExcName().replace("_", " ").toUpperCase());

        String cyclesText = workoutData.getExcName().equals("inverted board") || workoutData.getExcName().equals("unstable sit ups")
                ? workoutData.getExcCycles() + "s"
                : "x" + workoutData.getExcCycles();
        holder.rotation.setText(cyclesText);
    }

    public interface clickMethod {
        void clickCall(int pos);
    }
}

