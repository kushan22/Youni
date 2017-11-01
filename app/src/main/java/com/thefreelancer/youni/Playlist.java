package com.thefreelancer.youni;

import com.google.api.services.youtube.model.Video;

import java.util.ArrayList;

/**
 * Created by Kushan on 27-02-2016.
 */
public class Playlist extends ArrayList<Video> {


    public final String playlistId;
    private String mNextPageToken;


    public Playlist(String id) {
        playlistId = id;
    }

    public void setNextPageToken(String nextPageToken) {
        mNextPageToken = nextPageToken;
    }

    public String getNextPageToken() {
        return mNextPageToken;
    }

}
