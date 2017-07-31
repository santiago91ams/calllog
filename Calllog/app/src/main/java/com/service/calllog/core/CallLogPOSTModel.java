package com.service.calllog.core;

/**
 * Created by Ghita on 31/07/2017.
 */

public class CallLogPOSTModel {
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
}
