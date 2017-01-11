package com.oasis.googlebookslistingapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tushar on 10-01-2017.
 */

public class Book {

    private String title;

    private List<String> authors = new ArrayList<>();

    private String thumbnailImageUrl;

    private String infoUrl;

    private String publishedDate;

    public Book(String title, List<String> authors, String thumbnailImageUrl, String infoUrl, String publishedDate) {
        this.title = title;
        this.authors = authors;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.infoUrl = infoUrl;
        this.publishedDate = publishedDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }
}
