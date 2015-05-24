/*
 * Created by Hanks
 * Copyright (c) 2015 Nashangban. All rights reserved
 *
 */
package app.hanks.com.testadvancerecyclerview;

/**
 * Created by Hanks on 2015/5/24.
 */
public class MyItem {
    private String  content;
    private boolean pinedToSwipeLeft;
    private long    id;
    private int     viewType;

    public MyItem(int i,String s) {
        this.content = s;
        id = i;
        viewType = 0;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isPinedToSwipeLeft() {
        return pinedToSwipeLeft;
    }

    public void setPinedToSwipeLeft(boolean pinedToSwipeLeft) {
        this.pinedToSwipeLeft = pinedToSwipeLeft;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
