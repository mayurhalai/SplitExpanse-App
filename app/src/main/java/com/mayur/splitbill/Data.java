package com.mayur.splitbill;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Mayur on 6/29/2015.
 */
public class Data {
    String id;
    String amount;
    String description;
    String date;
    List<String> members;

    public Data(String Amount, String Description, String Date, List<String> Members) {
        amount = Amount;
        description = Description;
        date = Date;
        members = Members;
    }
    public Data(String Id, String Amount, String Description, String Date, List<String> Members) {
        id = Id;
        amount = Amount;
        description = Description;
        date = Date;
        members = Members;
    }

    /*public String toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("amount", amount);
            json.put("description", description);
            json.put("date", date);
            JSONArray member = new JSONArray(members);
            json.put("members", member);
            return json.toString().replace("\\","");
        } catch (JSONException e) {
            Log.d("App", "Exception: " + e);
        }
        return null;
    }*/
}
