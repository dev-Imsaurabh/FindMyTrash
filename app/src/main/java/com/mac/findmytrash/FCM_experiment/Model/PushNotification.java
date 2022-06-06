package com.mac.findmytrash.FCM_experiment.Model;

public class PushNotification {
    private NotificationData data;
    private String to;

    public PushNotification() {
    }

    public PushNotification(NotificationData data, String to) {
        this.data = data;
        this.to = to;
    }

    public NotificationData getData() {
        return data;
    }

    public void setData(NotificationData data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
