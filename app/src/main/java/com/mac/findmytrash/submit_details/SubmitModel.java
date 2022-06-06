package com.mac.findmytrash.submit_details;

public class SubmitModel {

    private String submitName,submitUid,submitPin,submitLat,submitLong,submitLandmark,submitAddress,key;

    public SubmitModel() {
    }

    public SubmitModel(String submitName, String submitUid, String submitPin, String submitLat, String submitLong, String submitLandmark, String submitAddress,String key) {
        this.submitName = submitName;
        this.submitUid = submitUid;
        this.submitPin = submitPin;
        this.submitLat = submitLat;
        this.submitLong = submitLong;
        this.submitLandmark = submitLandmark;
        this.submitAddress = submitAddress;
        this.key=key;
    }

    public String getSubmitName() {
        return submitName;
    }

    public void setSubmitName(String submitName) {
        this.submitName = submitName;
    }

    public String getSubmitUid() {
        return submitUid;
    }

    public void setSubmitUid(String submitUid) {
        this.submitUid = submitUid;
    }

    public String getSubmitPin() {
        return submitPin;
    }

    public void setSubmitPin(String submitPin) {
        this.submitPin = submitPin;
    }

    public String getSubmitLat() {
        return submitLat;
    }

    public void setSubmitLat(String submitLat) {
        this.submitLat = submitLat;
    }

    public String getSubmitLong() {
        return submitLong;
    }

    public void setSubmitLong(String submitLong) {
        this.submitLong = submitLong;
    }

    public String getSubmitLandmark() {
        return submitLandmark;
    }

    public void setSubmitLandmark(String submitLandmark) {
        this.submitLandmark = submitLandmark;
    }

    public String getSubmitAddress() {
        return submitAddress;
    }

    public void setSubmitAddress(String submitAddress) {
        this.submitAddress = submitAddress;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
