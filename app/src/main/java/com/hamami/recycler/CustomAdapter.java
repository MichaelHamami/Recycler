package com.hamami.recycler;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    MediaPlayer mediaPlayer;
    private List<MyList> list;
    private Context mCtx;

    CustomAdapter(List<MyList> list, Context mCtx,MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        this.list = list;
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_items, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomAdapter.ViewHolder holder, final int position) {
        final MyList myList = list.get(position);
        holder.textViewSongName.setText(myList.getNameSong());
        holder.textTimeSong.setText(myList.getSongLength());
        holder.linearLayoutOfRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // play the music
                Toast.makeText(mCtx,myList.getFileSong().toString(),Toast.LENGTH_LONG).show();
                playMusic(myList.getFileSong());
            }
        });

        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(mCtx, holder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                //handle menu1 click
                                break;
                            case R.id.menu2:
                                //handle menu2 click
                                break;
                            case R.id.menu3:
                                //handle menu3 click
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewSongName;
        private TextView textTimeSong;
        private TextView buttonViewOption;
        private LinearLayout linearLayoutOfRecycler;

        ViewHolder(View itemView) {
            super(itemView);

            textViewSongName =  itemView.findViewById(R.id.textViewSongName);
            textTimeSong =  itemView.findViewById(R.id.textTimeSong);
            buttonViewOption = itemView.findViewById(R.id.textViewOptions);
            linearLayoutOfRecycler = itemView.findViewById(R.id.linearLayoutOfRecycler);
        }
    }
    public void playMusic(File songFile)
    {
        Toast.makeText(mCtx, "playMusic func running", Toast.LENGTH_SHORT).show();
        if (mediaPlayer != null)
        {
            stopPlayer();
        }

         mediaPlayer = MediaPlayer.create(mCtx, Uri.parse(songFile.toString()));
        Toast.makeText(mCtx, "Media Player.create working?", Toast.LENGTH_SHORT).show();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer)
            {
                    stopPlayer();
            }
        });

        mediaPlayer.start();

    }
    public void stopPlayer()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.release();
            mediaPlayer = null;
            Toast.makeText(mCtx,"MediaPlayer released",Toast.LENGTH_SHORT).show();
        }
    }
}