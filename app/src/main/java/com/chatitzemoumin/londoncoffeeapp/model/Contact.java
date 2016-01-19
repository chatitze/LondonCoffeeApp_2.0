package com.chatitzemoumin.londoncoffeeapp.model;

/**
 * Created by Chatitze Moumin on 15/12/14.
 */
import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable{

    private String formattedPhone;
    private String twitter;
    private String facebook;
    private String instagram;

    public String getFormattedPhone() {
        return formattedPhone;
    }
    public void setFormattedPhone(String formattedPhone) {
        this.formattedPhone = formattedPhone;
    }
    public String getTwitter() {
        return twitter;
    }
    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }
    public String getFacebook() {
        return facebook;
    }
    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }
    public String getInstagram() {
        return instagram;
    }
    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }
    @Override
    public int describeContents() {

        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(formattedPhone);
        dest.writeString(twitter);
        dest.writeString(facebook);
        dest.writeString(instagram);
    }

    public Contact(Parcel in){
        this.formattedPhone = in.readString();
        this.twitter = in.readString();
        this.facebook = in.readString();
        this.instagram = in.readString();
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {

        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}