package com.hamami.recycler;

public class MyList {
    private String head;
    private String time;

    //constructor initializing values
    public MyList(String head, String desc) {
        this.head = head;
        this.time = desc;
    }

    //getters
    public String getHead() {
        return head;
    }

    public String getDesc() {
        return time;
    }
}
