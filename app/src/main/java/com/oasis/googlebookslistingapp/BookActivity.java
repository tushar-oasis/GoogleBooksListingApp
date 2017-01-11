package com.oasis.googlebookslistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oasis.googlebookslistingapp.loader.BookLoader;
import com.oasis.googlebookslistingapp.model.Book;
import com.oasis.googlebookslistingapp.view.BookAdapter;
import com.oasis.googlebookslistingapp.view.ItemSelectedCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class BookActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<List<Book>>{

    public static final String LOG_TAG = BookActivity.class.getName();

    private static final int BOOK_LOADER_ID = 1;

    private String GOOGLE_BOOKS_REQUEST_URL = "";

    private BookAdapter bookAdapter;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private LoaderManager loaderManager;

    @Bind(R.id.activity_book)
    RelativeLayout rootView;

    @Bind(R.id.top_bar)
    RelativeLayout topBar;

    @Bind(R.id.search_edit_done_button)
    ImageView searchEditDoneButton;

    @Bind(R.id.search_edit_text)
    EditText searchEditText;

    @Bind(R.id.book_list)
    ListView bookList;

    @Bind(R.id.loading_indicator)
    ProgressBar loadingIndicator;

    @Bind(R.id.empty_view)
    TextView emptyView;

    @Bind(R.id.edit_screen_overlay)
    View editScreenGreyOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String query = intent.getStringExtra(AppConstants.searchQuery);
        getSearchUrlFromQuery(query);

        ItemSelectedCallback itemSelectedCallback = new ItemSelectedCallback() {
            @Override
            public void onItemSelected(int position) {
                Log.d("tushar", "in onitemclick()");
                Book currentBook = bookAdapter.getItem(position);
                Uri bookUri = Uri.parse(currentBook.getInfoUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                startActivity(websiteIntent);
            }
        };
        bookAdapter = new BookAdapter(this, new ArrayList<Book>(), itemSelectedCallback);
        bookList.setAdapter(bookAdapter);
        bookList.setEmptyView(emptyView);
        bookList.setItemsCanFocus(true);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            loaderManager = getLoaderManager();
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        }else{
            loadingIndicator.setVisibility(View.GONE);
            emptyView.setText(R.string.no_internet_connection);
        }

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                searchEditDoneButton.performClick();
                return false;
            }
        });

        searchEditDoneButton.setOnClickListener(this);
        searchEditText.setOnClickListener(this);
        topBar.setOnClickListener(this);
        editScreenGreyOverlay.setOnClickListener(this);

    }

    private void showSoftInput() {
        if (searchEditText != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideSoftInput(){
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
    }

    private void getSearchUrlFromQuery(String query){
        if(query == null){
            return;
        }
        String[] arr = query.split(" ");
        StringBuilder fullUrl = new StringBuilder();
        fullUrl.append("https://www.googleapis.com/books/v1/volumes?q=author:");
        for(int i=0; i<arr.length; i++){
            if(i != 0){
                fullUrl.append("+");
            }
            fullUrl.append(arr[i]);
        }
        GOOGLE_BOOKS_REQUEST_URL = fullUrl.toString();
        Log.d(LOG_TAG, GOOGLE_BOOKS_REQUEST_URL);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == searchEditText.getId()){
            editScreenGreyOverlay.setVisibility(View.VISIBLE);
        }else if(view.getId() == searchEditDoneButton.getId()){
            editScreenGreyOverlay.setVisibility(GONE);
            hideSoftInput();
            Intent intent = new Intent(BookActivity.this, BookActivity.class);
            intent.putExtra(AppConstants.searchQuery, searchEditText.getText().toString());
            BookActivity.this.startActivity(intent);
            BookActivity.this.finish();
        }else if(view.getId() == topBar.getId()){
            showSoftInput();
            editScreenGreyOverlay.setVisibility(View.VISIBLE);
        }else if(view.getId() == editScreenGreyOverlay.getId()){
            hideSoftInput();
            editScreenGreyOverlay.setVisibility(View.GONE);
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        return new BookLoader(this, GOOGLE_BOOKS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> bookList) {
        loadingIndicator.setVisibility(View.GONE);
        bookAdapter.clear();
        if(bookList != null && !bookList.isEmpty()){
            bookAdapter.addAll(bookList);
        }else{
            emptyView.setText(R.string.no_books_found);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        bookAdapter.clear();
    }
}
