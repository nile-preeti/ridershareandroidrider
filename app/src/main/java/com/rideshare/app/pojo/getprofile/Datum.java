
package com.rideshare.app.pojo.getprofile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Datum {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("name_title")
    @Expose
    private String nameTitle;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;

    @SerializedName("identification_document_id")
    @Expose
    private String identificationDocID;
    @SerializedName("identification_issue_date")
    @Expose
    private String identificationIssueDate;
    @SerializedName("identification_expiry_date")
    @Expose
    private String identificationExpiryDate;
    @SerializedName("verification_id")
    @Expose
    private String verificationId;


    @SerializedName("vehicle_detail")
    @Expose
    private List<VehicleDetail> vehicleDetail = null;

    @SerializedName("rider_total_rating")
    @Expose
    private String riderTotalRating;

    public String getIdentificationDocID() {
        return identificationDocID;
    }

    public void setIdentificationDocID(String identificationDocID) {
        this.identificationDocID = identificationDocID;
    }

    public String getIdentificationIssueDate() {
        return identificationIssueDate;
    }

    public void setIdentificationIssueDate(String identificationIssueDate) {
        this.identificationIssueDate = identificationIssueDate;
    }

    public String getIdentificationExpiryDate() {
        return identificationExpiryDate;
    }

    public void setIdentificationExpiryDate(String identificationExpiryDate) {
        this.identificationExpiryDate = identificationExpiryDate;
    }

    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public List<VehicleDetail> getVehicleDetail() {
        return vehicleDetail;
    }

    public void setVehicleDetail(List<VehicleDetail> vehicleDetail) {
        this.vehicleDetail = vehicleDetail;
    }

    public String getRiderTotalRating() {
        return riderTotalRating;
    }
    public void setRiderTotalRating(String riderTotalRating) {
        this.riderTotalRating = riderTotalRating;
    }

    public String getNameTitle() {
        return nameTitle;
    }

    public void setNameTitle(String nameTitle) {
        this.nameTitle = nameTitle;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
