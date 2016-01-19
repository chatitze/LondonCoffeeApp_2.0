package com.chatitzemoumin.londoncoffeeapp.model;

/**
 * Created by Chatitze Moumin on 15/12/14.
 */
import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable{
    private String source;
    private String content;

    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    @Override
    public int describeContents() {

        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(source);
        dest.writeString(content);
    }

    public Comment(Parcel in){
        this.source = in.readString();
        this.content = in.readString();
    }

    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {

        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
