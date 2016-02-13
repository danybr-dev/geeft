package samurai.geeft.android.geeft.models;

import java.io.Serializable;

/**
 * Created by ugookeadu on 20/01/16.
 */
public class Geeft implements Serializable{
    private String timeStamp;
    private String deadLine;
    private String exptime;
    private String userLocation;
    private String userCap;
    private String username;
    private String geeftDescription;
    private String geeftTitle;
    private String geeftImage;
    private boolean automaticSelection;
    private boolean allowCommunication;
    private String id;
    private boolean isSelected;
    private String userProfilePic;
    private String linkId;

    public byte[] getStreamImage() {
        return streamImage;
    }

    public void setStreamImage(byte[] streamImage) {
        this.streamImage = streamImage;
    }

    private byte[] streamImage;

    private String category;

    public Geeft(){
    }

    public Geeft(String id, String geeftDescription, String geeftImage, String geeftTitle,
                 boolean isSelected, String timeStamp, String exptime, boolean automaticSelection, boolean allowCommunication, String deadLine, String userLocation, String userCap,
                 String username, String userProfilePic, String linkId) {
        this.geeftDescription = geeftDescription;
        this.geeftImage = geeftImage;
        this.geeftTitle = geeftTitle;
        this.isSelected = isSelected;
        this.timeStamp = timeStamp;
        this.exptime = exptime;
        this.automaticSelection = automaticSelection;
        this.allowCommunication = allowCommunication;
        this.userLocation = userLocation;
        this.userCap = userCap;
        this.username = username;
        this.userProfilePic = userProfilePic;
        this.id=id;
        this.deadLine = deadLine;
        this.linkId = linkId;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGeeftDescription() {
        return geeftDescription;
    }

    public void setGeeftDescription(String geeftDescription) {
        this.geeftDescription = geeftDescription;
    }

    public String getGeeftImage() {
        return geeftImage;
    }

    public void setGeeftImage(String geeftImage) {
        this.geeftImage = geeftImage;
    }

    public String getGeeftTitle() {
        return geeftTitle;
    }

    public void setGeeftTitle(String geeftTitle) {
        this.geeftTitle = geeftTitle;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getExpTime() {
        return exptime;
    }

    public void setExpTime(String exptime) {
        this.exptime = exptime;
    }

    public boolean isAllowCommunication() {
        return this.allowCommunication;
    }

    public void setAllowCommunication(boolean allowCommunication) {
        this.allowCommunication = allowCommunication;
    }

    public boolean isAutomaticSelection() {
        return this.automaticSelection;
    }

    public void setAutomaticSelection(boolean automaticSelection) {
        this.automaticSelection = automaticSelection;
    }

    public String getUserLocation() {
        return userLocation;
    }


    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getUserCap() {
        return userCap;
    }

    public void setUserCap(String userCap) {
        this.userCap = userCap;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }

    public String getDeadLine(){return deadLine;}

    public void setDeadLine(String deadLine){this.deadLine = deadLine;}

    public String getLinkId(){ return linkId;}

    public void setLinkId(String linkId){ this.linkId = linkId;}

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExptime() {
        return exptime;
    }

    public void setExptime(String exptime) {
        this.exptime = exptime;
    }
}