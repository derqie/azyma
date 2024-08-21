package co.ke.daletsys.azyma.ui.home;

import java.util.ArrayList;

public class OfferHolder {
    String requested,requests,likes,liked,shares,shared,peeps,peeped,oUrl,currency,count,id,serial,name,description,
            category,reviews,pick,user,email,phone,url,created,distance;
    ArrayList<ImageHolder> ImageHolderList;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getSerial() {
        return serial;
    }
    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }


    public String getReviews() {
        return reviews;
    }
    public void setReviews(String reviews) {
        this.reviews = reviews;
    }


    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }


    public String getCount() {
        return count;
    }
    public void setCount(String count) {
        this.count = count;
    }

    public String getPick() {
        return pick;
    }
    public void setPick(String pick) {
        this.pick = pick;
    }


    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreated() {
        return created;
    }
    public void setCreated(String created) {
        this.created = created;
    }


    public String getUploads() {
        return oUrl;
    }
    public void setUploads(String oUrl) {
        this.oUrl = oUrl;
    }

    public String getLikes() {
        return likes;
    }
    public void setLikes(String likes) {
        this.likes = likes;
    }
    public String getLiked() {
        return liked;
    }
    public void setLiked(String liked) {
        this.liked = liked;
    }

    public String getPeeps() {
        return peeps;
    }
    public void setPeeps(String peeps) {
        this.peeps = peeps;
    }
    public String getPeeped() {
        return peeped;
    }
    public void setPeeped(String peeped) {
        this.peeped = peeped;
    }

    public String getRequests() {
        return requests;
    }
    public void setRequests(String requests) {
        this.requests = requests;
    }
    public String getRequested() {
        return requested;
    }
    public void setRequested(String requested) {
        this.requested = requested;
    }


    public String getShares() {
        return shares;
    } public void setShares(String shares) {
        this.shares = shares;
    }

    public String getDistance() {
        return distance;
    } public void setDistance(String distance) {
        this.distance = distance;
    }

    public ArrayList<ImageHolder> getImageItemsList(){
        return ImageHolderList;
    }public void setImageHolderList(ArrayList<ImageHolder> imageHolderList){
        ImageHolderList = imageHolderList;
    }
}
