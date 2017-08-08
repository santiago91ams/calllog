package com.service.calllog.core;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ghita on 31/07/2017.
 */

public class CallLogPOSTModel implements Parcelable{
    String phoneNumber;
    String callType;
    String callDate;
    String callDuration;

    public CallLogPOSTModel(String phoneNumber, String callType, String callDate, String callDuration){
        this.phoneNumber = phoneNumber;
        this.callType = callType;
        this.callDate = callDate;
        this.callDuration = callDuration;
    }

    protected CallLogPOSTModel(Parcel in) {
        phoneNumber = in.readString();
        callType = in.readString();
        callDate = in.readString();
        callDuration = in.readString();
    }

    public static final Creator<CallLogPOSTModel> CREATOR = new Creator<CallLogPOSTModel>() {
        @Override
        public CallLogPOSTModel createFromParcel(Parcel in) {
            return new CallLogPOSTModel(in);
        }

        @Override
        public CallLogPOSTModel[] newArray(int size) {
            return new CallLogPOSTModel[size];
        }
    };

    @Override
    public int describeContents() {
        return CONTENTS_FILE_DESCRIPTOR;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phoneNumber);
        dest.writeString(callType);
        dest.writeString(callDate);
        dest.writeString(callDuration);
    }
}
