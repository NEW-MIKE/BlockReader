package com.kaya.blockreader.viewholder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kaya.blockreader.R;

public class AddBookViewHolder  extends RecyclerView.ViewHolder {
    public ImageView add_image;
    public CardView add_cardview;
    public AddBookViewHolder(@NonNull View itemView) {
        super(itemView);
        add_image = itemView.findViewById(R.id.book_add_image);
        add_cardview = itemView.findViewById(R.id.book_add_cardview);
    }
}
