package com.george.mydeliciousoven;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by farmaker1 on 13/03/2018.
 */

public class StepsRecyclerAdapter extends RecyclerView.Adapter<StepsRecyclerAdapter.StepsViewHolder> {

    private Context mContext;
    private ArrayList<Steps> stepsList;
    private StepsClickItemListener stepsClickItemListener;

    public StepsRecyclerAdapter(Context context, ArrayList<Steps> list, StepsClickItemListener listener) {
        mContext = context;
        stepsList = list;
        stepsClickItemListener = listener;
    }

    public interface StepsClickItemListener {
        void onListItemClick(int itemIndex);
    }

    @Override
    public StepsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.steps_fragment_item, parent, false);
        return new StepsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StepsViewHolder holder, int position) {

        Steps step = stepsList.get(position);

        holder.smallSteps.setText(step.getShortDescription());

        //if there is a thumbnail we load it in recycler view steps
        if (step.getThumbnailURL() != null && !step.getThumbnailURL().isEmpty()) {
            Picasso.with(mContext).load(step.getThumbnailURL()).error(R.drawable.sugar_free).into(holder.imageThumb);
        } else {
            Picasso.with(mContext).load(R.drawable.sugar_free).into(holder.imageThumb);
        }
    }

    @Override
    public int getItemCount() {
        return stepsList.size();
    }

    class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.stepsInstructions)
        TextView smallSteps;
        @BindView(R.id.imageOfSteps)
        ImageView imageThumb;

        public StepsViewHolder(View itemView2) {
            super(itemView2);

            ButterKnife.bind(this, itemView2);
            itemView2.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            stepsClickItemListener.onListItemClick(clickedPosition);
        }
    }

    public void setStepsData(ArrayList<Steps> list) {
        stepsList = list;
        notifyDataSetChanged();
    }
}
