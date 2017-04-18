package com.repgate.doctor.data;

/**
 * Created by developer on 3/18/16.
 */
public class UploadResponseData {

    public boolean success;
    public DataModel data;
    public ErrorModel error;

    public static class DataModel {
        public String url;
    }

    public static class ErrorModel {
        public String err_msg;
    }
}
