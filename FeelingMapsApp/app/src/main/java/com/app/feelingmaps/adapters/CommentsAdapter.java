package com.app.feelingmaps.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.app.feelingmaps.R;
import com.app.feelingmaps.models.Categories;
import com.app.feelingmaps.models.Comments;

import java.util.ArrayList;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder>{
    private LayoutInflater inflater;
    private Context context;
    private List<Comments> comments;

    public CommentsAdapter(Context context, List comments) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.comments=comments;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.comments_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Comments comments = this.comments.get(position);
        holder.rating.setRating(comments.getRating());
        holder.comments.setText(comments.getComment());
        holder.username.setText(comments.getUser());

        final CategoriesAdapter categoriesAdapter = new CategoriesAdapter(context, comments.getCategories());
        holder.categories.setHasFixedSize(true);
        holder.categories.setAdapter(categoriesAdapter);
        holder.categories.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        RecyclerView categories;
        TextView comments;
        TextView username;
        RatingBar rating;

        public MyViewHolder(View itemView) {
            super(itemView);
            categories = itemView.findViewById(R.id.rv_comment_item);
            comments = itemView.findViewById(R.id.tv_comment_item);
            username = itemView.findViewById(R.id.tv_username);
            rating = itemView.findViewById(R.id.rb_comment_item);
        }
    }
}