package com.repgate.doctor.data;

import java.util.ArrayList;

/**
 * Created by developer on 3/18/16.
 */
public class SpecialtyResponseData {

    public boolean success;
    public ArrayList<DataModel> parent;
    public ArrayList<DataModel> child;
    public ErrorModel error;

    public static class DataModel {
        public String id;
        public String name;
        public String parent;
    }

    public static class ErrorModel {
        public String err_msg;
    }
}
