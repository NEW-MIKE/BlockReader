package com.kaya.blockreader.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kaya.blockreader.R;
import com.kaya.blockreader.model.bookshelf_item;
import com.kaya.blockreader.ui.ReadActivity;
import com.kaya.blockreader.utils.FileUtils;
import com.kaya.blockreader.viewholder.AddBookViewHolder;
import com.kaya.blockreader.viewholder.ShowBookViewHolder;
import com.nbsp.materialfilepicker.MaterialFilePicker;

import java.util.List;
import java.util.regex.Pattern;

public class BookListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<bookshelf_item> datalist;
    private Context context;

    public BookListAdapter(List<bookshelf_item> datalist,Context context) {
        this.datalist = datalist;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case bookshelf_item.ADD_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booklist_add_item,parent,false);
                return new AddBookViewHolder(view);
            case bookshelf_item.SHOW_TYEP:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booklist_show_items,parent,false);
                return new ShowBookViewHolder(view);
            default:
                break;

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ShowBookViewHolder) {
            ((ShowBookViewHolder) holder).bookshow_text.setText(datalist.get(position).getBookname());
            ((ShowBookViewHolder) holder).bookshow_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReadActivity.actionStart(context,datalist.get(position).getBookurl(),datalist.get(position).getBookid());
                }
            });
            ((ShowBookViewHolder) holder).bookshow_text.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(context, R.style.AlertDialog);
                    mBuilder.setTitle(R.string.delete_book);
                    mBuilder.setNegativeButton(R.string.cancel_delete, null);
                    mBuilder.setPositiveButton(R.string.sure_delete, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if(onAddItemClickListener != null) {
                                onAddItemClickListener.DeleteBook(position);
                            }
                        }
                    });
                    mBuilder.create().show();
                    return true;
                }
            });
        }
        else if(holder instanceof AddBookViewHolder)
        {
            ((AddBookViewHolder) holder).add_image.setImageResource(R.drawable.add_book);
            ((AddBookViewHolder) holder).add_cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileUtils.openFolder(context);
                }
            });
        }
        else {

        }
    }

    @Override
    public int getItemViewType(int position) {
        return datalist.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public interface OnAddItemClickListener{
        public void DeleteBook(int position);
    }
    private OnAddItemClickListener onAddItemClickListener;

    public void setOnItemClickListener(OnAddItemClickListener onItemClickListener){
        onAddItemClickListener = onItemClickListener;
    }
}
