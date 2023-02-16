package com.penelope.banchanggo.api.address;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddressJsonParser {

    public static List<String> parse(JSONObject response) {

        Log.d("TAG", "parse: " + response);

        List<String> addressList = new ArrayList<>();

        try {

            JSONObject meta = response.getJSONObject("meta");
            if (meta.getInt("total_count") == 0) {
                return addressList;
            }

            JSONArray documents = response.getJSONArray("documents");

            for (int i = 0; i < documents.length(); i++) {

                JSONObject document = documents.getJSONObject(i);
                String addressName = document.getString("address_name");
                addressList.add(addressName);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return addressList;
    }

}
