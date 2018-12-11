package com.hamami.recycler;

public class MyList {
    private String head;
    private String time;

    //constructor initializing values
    MyList(String head, String time) {
        this.head = head;
        this.time = time;
    }

    //getters
    public String getHead() {
        return head;
    }

    public String getTime() {
        return time;
    }
}
