package com.oasis.googlebookslistingapp.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.oasis.googlebookslistingapp.R;
import com.oasis.googlebookslistingapp.model.Book;

import java.util.List;

import static android.R.attr.resource;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * Created by tushar on 10-01-2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    Context context;
    ItemSelectedCallback itemSelectedCallback;

    /**
     *
     * @param context
     * @param bookList
     */
    public BookAdapter(Context context, List<Book> bookList, ItemSelectedCallback itemSelectedCallback) {
        super(context, 0, bookList);
        this.context = context;
        this.itemSelectedCallback = itemSelectedCallback;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);
        }
        ImageView thumbnailImageView = (ImageView) listItemView.findViewById(R.id.thumbnail);
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.book_title);
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author);
        TextView publishedDateTextView = (TextView) listItemView.findViewById(R.id.published_date);
        Book currentBook = getItem(position);
        titleTextView.setText(currentBook.getTitle());
        StringBuilder authorTextString = new StringBuilder();
        List<String> authors = currentBook.getAuthors();
        for (String author : authors){
            authorTextString.append(author);
            if (authors.size() > 1){
                authorTextString.append(", ");
            }
        }
        authorTextView.setText(authorTextString.toString());
        publishedDateTextView.setText(currentBook.getPublishedDate());
        Glide.with(context)
                .load(currentBook.getThumbnailImageUrl())
                .override(44, 44)
                .into(thumbnailImageView);
        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemSelectedCallback.onItemSelected(position);
            }
        });
        return listItemView;
    }
}
