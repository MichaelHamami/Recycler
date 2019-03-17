package com.hamami.recycler;

import android.support.v4.media.MediaMetadataCompat;

import com.hamami.recycler.util.MyPreferenceManager;


public interface IMainActivity {


//    void setActionBarTitle(String title);

    void playPause();

    void playNext();

    void playPrev();

    MyApplication getMyApplicationInstance();

    void onMediaSelected(String playlistId, MediaMetadataCompat mediaItem, int queuePosition);

    MyPreferenceManager getMyPreferenceManager();

}
