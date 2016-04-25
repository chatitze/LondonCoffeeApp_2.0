package com.chatitzemoumin.londoncoffeeapp.model;

/**
 * Created by Chatitze Moumin on 05/12/14.
 */
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CoffeeShop implements Parcelable{

    private int id;
    private String name;
    private String region;
    private String address;
    private String rating;
    private double lat;
    private double lng;
    //private String category;
    private String roaster;
    //private String machine;
    //private String grinder;
    //private String brewMethods;
    private String webAddress;
    private Contact contact;
    private String openingHours;
    //private String owner;
    @SerializedName("comments")
    private List<Comment> commentList = new ArrayList<Comment>();
    private String venueUrl;
    private String coffeeUrl;
    //private String extraUrl;
    //private String extraCoffeeUrl;

    private float distance = -1;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getRating() {
        return rating;
    }
    public void setRating(String rating) {
        this.rating = rating;
    }
    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public double getLng() {
        return lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
    /*public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }*/
    public String getRoaster() {
        return roaster;
    }
    public void setRoaster(String roaster) {
        this.roaster = roaster;
    }
    /*public String getMachine() {
        return machine;
    }
    public void setMachine(String machine) {
        this.machine = machine;
    }
    public String getGrinder() {
        return grinder;
    }
    public void setGrinder(String grinder) {
        this.grinder = grinder;
    }
    public String getBrewMethods() {
        return brewMethods;
    }
    public void setBrewMethods(String brewMethods) {
        this.brewMethods = brewMethods;
    }*/
    public String getWebAddress() {
        return webAddress;
    }
    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }
    public Contact getContact() {
        return contact;
    }
    public void setContact(Contact contact) {
        this.contact = contact;
    }
    public String getOpeningHours() {
        return openingHours;
    }
    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }
    /*public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }*/
    public List<Comment> getCommentList() {
        return commentList;
    }
    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }
    public String getVenueUrl() {
        return venueUrl;
    }
    public void setVenueUrl(String venueUrl) {
        this.venueUrl = venueUrl;
    }
    public String getCoffeeUrl() {
        return coffeeUrl;
    }
    public void setCoffeeUrl(String coffeeUrl) {
        this.coffeeUrl = coffeeUrl;
    }
    /*public String getExtraUrl() {
        return extraUrl;
    }
    public void setExtraUrl(String extraUrl) {
        this.extraUrl = extraUrl;
    }
    public String getExtraCoffeeUrl() {
        return extraUrl;
    }
    public void setExtraCoffeeUrl(String extraUrl) {
        this.extraUrl = extraUrl;
    }*/
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(region);
        dest.writeString(address);
        dest.writeString(rating);
        dest.writeString(coffeeUrl);
        dest.writeString(venueUrl);
        //dest.writeString(extraUrl);
        //dest.writeString(extraCoffeeUrl);
        //dest.writeString(category);
        dest.writeString(roaster);
        //dest.writeString(machine);
        //dest.writeString(grinder);
        dest.writeString(webAddress);
        //dest.writeString(owner);
        dest.writeString(openingHours);
        //dest.writeString(brewMethods);

        dest.writeInt(id);

        dest.writeDouble(lat);
        dest.writeDouble(lng);

        dest.writeFloat(distance);

        dest.writeTypedList(commentList);
        dest.writeValue(contact);
    }

    private CoffeeShop(Parcel in){
        this.name = in.readString();
        this.region = in.readString();
        this.address = in.readString();
        this.rating = in.readString();
        this.coffeeUrl = in.readString();
        this.venueUrl = in.readString();
        //this.extraUrl = in.readString();
        //this.extraCoffeeUrl = in.readString();
        //this.category = in.readString();
        this.roaster = in.readString();
        //this.machine = in.readString();
        //this.grinder = in.readString();
        this.webAddress = in.readString();
        //this.owner = in.readString();
        this.openingHours = in.readString();
        //this.brewMethods = in.readString();

        this.id = in.readInt();

        this.lat = in.readDouble();
        this.lng = in.readDouble();

        this.distance = in.readFloat();

        in.readTypedList(this.commentList,Comment.CREATOR);
        this.contact = (Contact) in.readValue(Contact.class.getClassLoader());

    }

    public static final Parcelable.Creator<CoffeeShop> CREATOR = new Parcelable.Creator<CoffeeShop>() {

        @Override
        public CoffeeShop createFromParcel(Parcel source) {
            return new CoffeeShop(source);
        }

        @Override
        public CoffeeShop[] newArray(int size) {
            return new CoffeeShop[size];
        }
    };
}
