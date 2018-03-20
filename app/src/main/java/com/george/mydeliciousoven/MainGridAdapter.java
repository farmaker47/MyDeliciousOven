package com.george.mydeliciousoven;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class MainGridAdapter extends RecyclerView.Adapter<MainGridAdapter.MainViewHolder> {

    private Context mContext;
    private ArrayList<Recipes> recipesList;
    private RecipesClickItemListener recipesClickItemListener;

    public MainGridAdapter(Context context, ArrayList<Recipes> list, RecipesClickItemListener listener) {
        mContext = context;
        recipesList = list;
        recipesClickItemListener = listener;

    }

    public interface RecipesClickItemListener {
        void onListItemClick(int itemIndex);
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipes_list_item, parent, false);
        return new MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        Recipes recipes = recipesList.get(position);
        holder.name.setText(recipes.getName());
        Log.e("STRINGeMPTY","-"+recipes.getImage()+"-");
        if (recipes.getImage() != null && !recipes.getImage().isEmpty()) {
            Picasso.with(mContext).load(recipes.getImage()).error(R.drawable.sugar_flo).into(holder.image);
        } else {
            Picasso.with(mContext).load(R.drawable.sugar_flo).into(holder.image);
        }
    }


    @Override
    public int getItemCount() {
        return recipesList.size();
    }

    class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.recipesName)
        TextView name;
        /*@BindView(R.id.linearBackImageMain)LinearLayout linearBack;*/
        @BindView(R.id.imageBackMain)
        ImageView image;


        public MainViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            recipesClickItemListener.onListItemClick(clickedPosition);
        }
    }

    public void setRecipesData(ArrayList<Recipes> list) {
        recipesList = list;
        notifyDataSetChanged();
    }
}
