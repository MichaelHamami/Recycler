package com.hamami.recycler;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    //recyclerview objects
    private MediaPlayer mediaPlayer;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private String  rootis;
    private ArrayList<File>   mySongs;
    private MusicFragment musicFragment;

    private Button unShowButton;
    private Button showButton;

    //model object for our list data
    private List<MyList> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing views
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

        // get song files from Emulator
          rootis = " "+Environment.getExternalStorageDirectory().getName();
          mySongs = findSongs(Environment.getExternalStorageDirectory());

        //loading list view item with this function
        loadRecyclerViewItem();

        //Button
        unShowButton = (Button) findViewById(R.id.unShowButton);
        showButton = (Button) findViewById(R.id.showButton);
        // fragment
       musicFragment = new MusicFragment();

     //    Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
// Replace the contents of the container with the new fragment
        ft.replace(R.id.fragmentContainer, musicFragment);
// or ft.add(R.id.your_placeholder, new FooFragment());
// Complete the changes added above
        ft.commit();



        //Check if its work
        unShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unShownFragment();
            }
        });
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment();
            }
        });

    }


    public ArrayList<File> findSongs(File root){
        ArrayList<File> al = new ArrayList<File>();
        File[] files = root.listFiles();
        for(File singleFile : files){
            if(singleFile.isDirectory()&& !singleFile.isHidden()){
                al.addAll(findSongs(singleFile));
            }
            else {
                if (singleFile.getName().endsWith(".mp3")){
                    al.add(singleFile);
                }
            }
        }
        return al;
    }

    private void loadRecyclerViewItem() {
//            you can fetch the data from server or some apis
//            for this tutorial I am adding some dummy data directly
            for (int i = 0; i < mySongs.size(); i++) {
        MyList myList = new MyList(
                mySongs.get(i),
                mySongs.get(i).getName(),
                getTimeSong(mySongs.get(i))
        );
        list.add(myList);
    }

    adapter = new CustomAdapter(list, this,mediaPlayer);
            recyclerView.setAdapter(adapter);
    }

    public String getTimeSong(File file )
    {
        // load data file
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(file.getAbsolutePath());

        String time = "";
        // get mp3 info

        // convert duration to minute:seconds
        String duration =
                metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        long dur = Long.parseLong(duration);
        String seconds = String.valueOf((dur % 60000) / 1000);

        String minutes = String.valueOf(dur / 60000);
        if (seconds.length() == 1) {
            time = "0" + minutes + ":0" + seconds;
        }else {
            time = "0" + minutes + ":" + seconds;
        }
//        Toast.makeText(this,time,Toast.LENGTH_LONG).show();
        // close object
        metaRetriever.release();
        return time;
    }
    public void unShownFragment()
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(musicFragment);
        ft.commit();
    }
    public void showFragment()
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.show(musicFragment);
        ft.commit();
    }
}
