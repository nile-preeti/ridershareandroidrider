package com.rideshare.app.pojo.payment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DefaultCardData {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("card_number")
    @Expose
    private Boolean cardNumber;
    @SerializedName("expiry_month")
    @Expose
    private Boolean expiryMonth;
    @SerializedName("expiry_date")
    @Expose
    private Boolean expiryDate;
    @SerializedName("card_holder_name")
    @Expose
    private String cardHolderName;
    @SerializedName("customer_id")
    @Expose
    private String customerId;
    @SerializedName("is_default")
    @Expose
    private String isDefault;
    @SerializedName("card_type ")
    @Expose
    private String cardType;
    @SerializedName("bank_name ")
    @Expose
    private Boolean bankName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(Boolean cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Boolean getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(Boolean expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public Boolean getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Boolean expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Boolean getBankName() {
        return bankName;
    }

    public void setBankName(Boolean bankName) {
        this.bankName = bankName;
    }
}
