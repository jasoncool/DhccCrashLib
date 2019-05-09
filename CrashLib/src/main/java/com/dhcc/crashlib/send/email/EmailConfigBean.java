package com.dhcc.crashlib.send.email;

import android.os.Parcel;
import android.os.Parcelable;

public class EmailConfigBean implements Parcelable {
    private String sendEmailAddress;
    private String toEmailAddress;
    private String sendEmailPwd;

    private EmailConfigBean(){

    }

    public EmailConfigBean(String sendEmailAddress,String toEmailAddress,String sendEmailPwd){
        this.sendEmailAddress=sendEmailAddress;
        this.toEmailAddress=toEmailAddress;
        this.sendEmailPwd=sendEmailPwd;
    }

    public String getSendEmailAddress() {
        return sendEmailAddress;
    }

    public void setSendEmailAddress(String sendEmailAddress) {
        this.sendEmailAddress = sendEmailAddress;
    }

    public String getToEmailAddress() {
        return toEmailAddress;
    }

    public void setToEmailAddress(String toEmailAddress) {
        this.toEmailAddress = toEmailAddress;
    }

    public String getSendEmailPwd() {
        return sendEmailPwd;
    }

    public void setSendEmailPwd(String sendEmailPwd) {
        this.sendEmailPwd = sendEmailPwd;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sendEmailAddress);
        dest.writeString(this.toEmailAddress);
        dest.writeString(this.sendEmailPwd);
    }

    protected EmailConfigBean(Parcel in) {
        this.sendEmailAddress = in.readString();
        this.toEmailAddress = in.readString();
        this.sendEmailPwd = in.readString();
    }

    public static final Creator<EmailConfigBean> CREATOR = new Creator<EmailConfigBean>() {
        @Override
        public EmailConfigBean createFromParcel(Parcel source) {
            return new EmailConfigBean(source);
        }

        @Override
        public EmailConfigBean[] newArray(int size) {
            return new EmailConfigBean[size];
        }
    };
}
