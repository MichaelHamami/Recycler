package com.hamami.recycler.client;

import android.content.ComponentName;
import android.content.Context;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.media.MediaBrowserServiceCompat;

public class MediaBrowserHelper {

    private static final String TAG = "MediaBrowserHelper";

    private final Context mContext;
    private final Class<? extends MediaBrowserServiceCompat> mMediaBrowserServiceClass;

    private MediaBrowserCompat mMediaBrowser;
    private MediaControllerCompat mMediaController;

    private MediaBrowserConnectionCallback mMediaBrowserConnectionCallback;
    private MediaBrowserSubscriptionCallBack mMediaBrowserSubscriptionCallBack;
    private MediaControllerCallback mMediaControllerCallback;
    private MediaBrowserHelperCallback mMediaBrowserCallback;
    private boolean mWasConfigurationChanged;



    public MediaBrowserHelper(Context context, Class<? extends MediaBrowserServiceCompat> mediaBrowserServiceClass) {
        mContext = context;
        mMediaBrowserServiceClass = mediaBrowserServiceClass;

        mMediaBrowserConnectionCallback = new MediaBrowserConnectionCallback();
        mMediaBrowserSubscriptionCallBack = new MediaBrowserSubscriptionCallBack();
        mMediaControllerCallback = new MediaControllerCallback();
    }

    public void setMediaBrowserHelperCallback(MediaBrowserHelperCallback mediaBrowserHelperCallback)
    {
        mMediaBrowserCallback = mediaBrowserHelperCallback;
    }
    private class MediaControllerCallback extends MediaControllerCompat.Callback
    {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata)
        {
            Log.d(TAG, "onMetadataChanged: called.");

            if(mMediaBrowserCallback != null)
            {
                mMediaBrowserCallback.onMetadataChanged(metadata);
            }
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            Log.d(TAG, "onPlaybackStateChanged: called.");

            if(mMediaBrowserCallback != null)
            {
                mMediaBrowserCallback.onPlayBackStateChanged(state);
            }

        }
        // This might happen if the MusicService is killed while the Activity is in the
        // foreground and onStart() has been called (but not onStop()).
        @Override
        public void onSessionDestroyed() {
            onPlaybackStateChanged(null);
        }


    }

    public void subscribeToNewPlaylist(String currentPlaylistId, String newPlaylistId){
        if(!currentPlaylistId.equals("")){
            Log.d(TAG, "subscribeToNewPlaylist:  unsubscribed ...");
            mMediaBrowser.unsubscribe(currentPlaylistId);
        }
        Log.d(TAG, "subscribeToNewPlaylist: playlistId is: "+newPlaylistId +" and MediaBrowserSubscriptionCallBack is: " +mMediaBrowserSubscriptionCallBack);
        Log.d(TAG, "subscribeToNewPlaylist: mMediaBrowser is : "+mMediaBrowser);
        mMediaBrowser.subscribe(newPlaylistId,mMediaBrowserSubscriptionCallBack);
    }
    public void onStart(boolean wasConfigurationChanged)
    {
        mWasConfigurationChanged = wasConfigurationChanged;
        if(mMediaBrowser == null)
        {
            mMediaBrowser = new MediaBrowserCompat(
                    mContext,
                    new ComponentName(mContext,mMediaBrowserServiceClass),
                    mMediaBrowserConnectionCallback,
                    null);

            mMediaBrowser.connect();
        }
        Log.d(TAG, "onStart: CALLED: Creating MediaBrowser, and connecting");
    }
    public void onStop()
    {
        if (mMediaController != null)
        {
            mMediaController.unregisterCallback(mMediaControllerCallback);
            mMediaController = null;
        }
        if(mMediaBrowser != null && mMediaBrowser.isConnected())
        {
            mMediaBrowser.disconnect();
            mMediaBrowser = null;
        }
        Log.d(TAG, "onStop: CALLED: Releasing MediaController, Disconnecting from MediaBrowser");
    }

    private class MediaBrowserConnectionCallback extends MediaBrowserCompat.ConnectionCallback{
        // Happens as a result of onStart().
        @Override
        public void onConnected() {
            Log.d(TAG,"onConnected: called");

            try
            {
                // Get a MediaController for the MediaSession.
                mMediaController = new MediaControllerCompat(mContext,mMediaBrowser.getSessionToken());
                mMediaController.registerCallback(mMediaControllerCallback);


            }catch (RemoteException e)
            {
                Log.d(TAG,"onConnected: connection problem: "+e.toString());
                throw new RuntimeException(e);
            }
            mMediaBrowser.subscribe(mMediaBrowser.getRoot(),mMediaBrowserSubscriptionCallBack);
            Log.d(TAG,"onConnected: called: subscribing to: "+mMediaBrowser.getRoot());

            mMediaBrowserCallback.onMediaControllerConnected(mMediaController);

        }
    }

    public class MediaBrowserSubscriptionCallBack extends MediaBrowserCompat.SubscriptionCallback
    {
        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
            Log.d(TAG,"onChildrenLoaded: called: "+parentId + ", "+  children.toString()+", size:"+children.size());


                for(final MediaBrowserCompat.MediaItem mediaItem : children)
                {
                    Log.d(TAG,"onChildrenLoaded: CALLED: queue item:" + mediaItem.getMediaId());
                    mMediaController.addQueueItem(mediaItem.getDescription());
                }
        }
    }

    public MediaControllerCompat.TransportControls getTransportControls()
    {
        if(mMediaController == null)
        {
            Log.d(TAG,"getTransportControls: MediaController is null !");
            throw new IllegalStateException("Media Controller is null");
        }
        return mMediaController.getTransportControls();
    }

}
