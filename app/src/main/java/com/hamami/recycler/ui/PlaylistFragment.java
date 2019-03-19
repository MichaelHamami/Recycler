package com.hamami.recycler.ui;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.hamami.recycler.IMainActivity;
import com.hamami.recycler.PlaylistRecyclerAdapter;
import com.hamami.recycler.R;
import com.hamami.recycler.Song;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PlaylistFragment extends Fragment implements PlaylistRecyclerAdapter.IMediaSelector
{
    private static final String TAG = "PlaylistFragment";

    // UI Components
    private RecyclerView mRecyclerView;

    //Vars
    private String playlistTitle;
    private PlaylistRecyclerAdapter mAdapter;
    private ArrayList<MediaMetadataCompat> mMediaList = new ArrayList<>();
    private ArrayList<Song> songsList = new ArrayList<>();
    private IMainActivity mIMainActivity;
    private MediaMetadataCompat mSelectedMedia;

    public static PlaylistFragment newInstance(ArrayList<Song> songsArray,String title){
        Log.d(TAG, "playListFragment new Instance called!");
        PlaylistFragment playlistFragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("songLists",songsArray);
        args.putString("title",title);
        playlistFragment.setArguments(args);
        return playlistFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            Log.d(TAG, "playListFragment, OnCreate: try getArguments!");
            if (songsList.size() ==0)
            {
                Toast.makeText(getContext(),"we get arguments",Toast.LENGTH_LONG).show();
                songsList = getArguments().getParcelableArrayList("songLists");
                addToMediaList(songsList);
                playlistTitle = getArguments().getString("title");
            }
            // only for now
//            playlistTitle = "jokeForNow2";

            setRetainInstance(true);


        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_playlist,container,false);
    }

    // called after onCreateView
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        initRecyclerView(view);

        if(savedInstanceState != null)
        {
            mAdapter.setSelectedIndex(savedInstanceState.getInt("selected_index"));
        }

    }
    private void getSelectedMediaItem(String mediaId)
    {
        for(MediaMetadataCompat mediaItem: mMediaList)
        {
            if(mediaItem.getDescription().getMediaId().equals(mediaId))
            {
                mSelectedMedia = mediaItem;
                mAdapter.setSelectedIndex(mAdapter.getIndexOfItem(mSelectedMedia));
                break;
            }
        }
    }

    public int getSelectedIndex()
    {
       return mAdapter.getSelectedIndex();
    }

    private void initRecyclerView(View view)
    {
            mRecyclerView = view.findViewById(R.id.reycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mAdapter = new PlaylistRecyclerAdapter(getActivity(),songsList,mMediaList,this);
            Log.d(TAG, "initRecyclerView: called , Song list size is:"+songsList.size()+" and MediaList size is:" +mMediaList.size());
            mRecyclerView.setAdapter(mAdapter);

            updateDataSet();

    }

    private void updateDataSet() {
        mAdapter.notifyDataSetChanged();
        if(mIMainActivity.getMyPreferenceManager().getLastPlayedArtist().equals(playlistTitle)){
            getSelectedMediaItem(mIMainActivity.getMyPreferenceManager().getLastPlayedMedia());
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mIMainActivity = (IMainActivity) getActivity();
    }

    @Override
    public void onMediaSelected(int position)
    {
        Log.d(TAG, "onSongSelected: list item is clicked! +List size is: "+mMediaList.size());
        mIMainActivity.getMyApplicationInstance().setMediaItems(mMediaList);
        mSelectedMedia = mMediaList.get(position);
        mAdapter.setSelectedIndex(position);
        mIMainActivity.onMediaSelected(playlistTitle,mSelectedMedia,position);
        saveLastPlayedSongProperties();

    }

    @Override
    public void onSongOptionSelected(int position,View view)
    {
        Log.d(TAG, "onSongOptionSelected: you clicked on menu good job");
        Toast.makeText(getContext(),"you clicked on menu good job",Toast.LENGTH_LONG).show();
        showPopup(position,view);
    }
    public void showPopup(final int postion, View view){
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.inflate(R.menu.options_menu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu1:
                        Toast.makeText(getContext(), "play menu clicked", Toast.LENGTH_SHORT).show();
                        onMediaSelected(postion);
                        return true;
                    case R.id.menu2:
                        Toast.makeText(getContext(), "delete menu  clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu3:
                        Toast.makeText(getContext(), "add to playlist menu clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        //displaying the popup
        popup.show();
    }

    public void updateUI(MediaMetadataCompat mediaItem)
    {
        mAdapter.setSelectedIndex(mAdapter.getIndexOfItem(mediaItem));
        mSelectedMedia = mediaItem;
        saveLastPlayedSongProperties();
    }

    private void addToMediaList(ArrayList<Song> songsList)
    {
        for (int i=0;i<songsList.size();i++)
        {
            MediaMetadataCompat media = new MediaMetadataCompat.Builder()
                    // title = songName , songTime need to change
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID,songsList.get(i).getNameSong())
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE,songsList.get(i).getNameSong())
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,songsList.get(i).getSongLength())
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI,songsList.get(i).getFileSong().toURI().toString())
                    .build();
            mMediaList.add(media);
        }
    }

    private void saveLastPlayedSongProperties()
    {
        // title is like the artist in mitch project
        mIMainActivity.getMyPreferenceManager().savePlaylistId(playlistTitle);
        mIMainActivity.getMyPreferenceManager().saveLastPlayedArtist(playlistTitle);
        mIMainActivity.getMyPreferenceManager().saveLastPlayedMedia(mSelectedMedia.getDescription().getMediaId());

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selected_index",mAdapter.getSelectedIndex());
    }

//    @Override
//    public boolean onMenuItemClick(MenuItem item)
//    {
//        switch (item.getItemId()){
//            case R.id.menu1:
//                Toast.makeText(getContext(),"play menu clicked",Toast.LENGTH_SHORT).show();
////                onMediaSelected()
//                return true;
//            case R.id.menu2:
//                Toast.makeText(getContext(),"delete menu  clicked",Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.menu3:
//                Toast.makeText(getContext(),"add to playlist menu clicked",Toast.LENGTH_SHORT).show();
//                return true;
//            default:
//                return false;
//        }
//    }
}
