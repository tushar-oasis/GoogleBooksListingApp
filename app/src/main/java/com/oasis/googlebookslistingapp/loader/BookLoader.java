package com.oasis.googlebookslistingapp.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.oasis.googlebookslistingapp.model.Book;
import com.oasis.googlebookslistingapp.utils.QueryUtils;

import java.util.List;

/**
 * Created by tushar on 11-01-2017.
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {
    private String url;

    public BookLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        if(url == null) {
            return null;
        }
        Log.d("tushar", url);
        List<Book> bookList = QueryUtils.fetchBookData(url);
        return bookList;
    }
}
