package com.penelope.banchanggo.api.address;

import androidx.annotation.WorkerThread;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class AddressApi {

    public static final String URL_FORMAT = "https://dapi.kakao.com/v2/local/search/address.json?query=[QUERY]";
    public static final String PLACEHOLDER_QUERY = "[QUERY]";
    public static final String AUTHORIZATION_FORMAT = "KakaoAK [REST_API_KEY]";
    public static final String PLACEHOLDER_REST_API_KEY = "[REST_API_KEY]";
    public static final String REST_API_KEY = "57292ec144696417a38191eb9479bfd2";


    @WorkerThread
    public List<String> getAddresses(String query) {

        try {
            String strUrl = URL_FORMAT
                    .replace(PLACEHOLDER_QUERY, query);

            String strAuthorization = AUTHORIZATION_FORMAT.replace(PLACEHOLDER_REST_API_KEY, REST_API_KEY);

            URL url = new URL(strUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", strAuthorization);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            JSONObject response = new JSONObject(sb.toString());
            return AddressJsonParser.parse(response);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
