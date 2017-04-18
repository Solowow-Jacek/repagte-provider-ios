package com.repgate.doctor.data;

/**
 * Created by developer on 6/20/2016.
 */
public class ProviderProfileResponseData {
    public boolean success;
    public DataModel data;
    public ErrorModel error;

    public static class DataModel {
        public String ID;
        public String email;
        public String displayName;
        public String role;
        public String userCode;
        public String deviceId;
        public String deviceType;
        public String logoUrl;
        public String birthday;
        public String gender;
        public String phone;
        public String officeAddr;
        public String company;
        public String education;
        public String certifications;
        public String awards;
        public boolean requestSendAvailable;
        public boolean messageSendAvailable;
        public int messageNew;
        public int requestNew;
    }

    public static class ErrorModel {
        public String err_msg;
    }
}
