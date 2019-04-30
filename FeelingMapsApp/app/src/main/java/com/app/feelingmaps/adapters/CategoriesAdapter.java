package com.app.feelingmaps.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.feelingmaps.R;
import com.app.feelingmaps.models.Categories;

import java.util.List;

import androidx.recyclerview.selection.ItemDetailsLookup;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder>{
    private LayoutInflater inflater;
    private Context context;
    private List<Categories> categories;

    public CategoriesAdapter(Context context,List categories) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.categories=categories;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.categories_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Categories category = categories.get(position);
        holder.category.setText(category.getCategory());
        if(category.isSelected()){
            holder.background.setBackgroundColor(Color.parseColor("#2e2eff"));
            holder.category.setTextColor(Color.parseColor("#FFFFFF"));
        }else{
            holder.background.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.category.setTextColor(Color.parseColor("#000000"));
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView category;
        LinearLayout background;

        public MyViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.tv_cat_item);
            background = itemView.findViewById(R.id.ll_cat_item_back);
        }
    }
}