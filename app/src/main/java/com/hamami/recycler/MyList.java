package com.hamami.recycler;

import java.io.File;

public class MyList {
    private File fileSong;
    private String nameSong;
    private String SongLength;

    //constructor initializing values
    MyList(File fileSong,String nameSong, String SongLength) {
        this.fileSong = fileSong;
        this.nameSong = nameSong;
        this.SongLength = SongLength;
    }

    //getters
    public String getNameSong() {
        return nameSong;
    }

    public String getSongLength() {
        return SongLength;
    }
    public File getFileSong(){
        return fileSong;
    }
}
