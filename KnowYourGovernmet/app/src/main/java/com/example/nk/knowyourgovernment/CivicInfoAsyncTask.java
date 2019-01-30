package com.example.nk.knowyourgovernment;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CivicInfoAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "CivicInfoAsyncTask";
    private MainActivity mainActivity;
    private List<Official> officials = null;
    private SearchLocation location = null;

    public CivicInfoAsyncTask(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected void onPostExecute(String s) {
        mainActivity.getOfficialsData(location, officials);
    }

    @Override
    protected String doInBackground(String... params) {

        Uri.Builder buildURL = Uri.parse(AppConstants.URL_GOOGLE_CIVIC + AppConstants.API_KEY + AppConstants.URL_GOOGLE_CIVIC_TRAILING + params[0]).buildUpon();
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Log.d(TAG, "doInBackground: Response Code: " + conn.getResponseCode() + ", " + conn.getResponseMessage());

            conn.setRequestMethod("GET");

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }
        if (sb == null) {
            Toast.makeText(mainActivity, "The Civic Info service is unavailable", Toast.LENGTH_SHORT).show();
        } else if (sb.toString().isEmpty()) {
            Toast.makeText(mainActivity, "No Data is available for the specified location.", Toast.LENGTH_SHORT).show();
        } else {
            parseJSON(sb.toString());
        }


        return null;
    }

    private void parseJSON(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONObject normalizedInputJSONObject = jsonObject.getJSONObject(AppConstants.JSON_NORMALIZED_INPUT_KEY);

            //Get location data from JSON
            location = new SearchLocation();
            officials = new ArrayList<>();

            location.setCity(normalizedInputJSONObject.getString(AppConstants.JSON_CITY_KEY));
            location.setState(normalizedInputJSONObject.getString(AppConstants.JSON_STATE_KEY));
            location.setZip(normalizedInputJSONObject.getString(AppConstants.JSON_ZIP_KEY));

            JSONArray officialJSONArray = jsonObject.getJSONArray(AppConstants.JSON_OFFICIAL_KEY);
            for (int i = 0; i < officialJSONArray.length(); i++) {
                Official official = new Official();
                JSONObject officialJSONObject = officialJSONArray.getJSONObject(i);

                official.setName(officialJSONObject.getString(AppConstants.JSON_NAME_KEY));
                if (officialJSONObject.has(AppConstants.JSON_ADDRESS_KEY)) {
                    JSONObject addressJSON = officialJSONObject.getJSONArray(AppConstants.JSON_ADDRESS_KEY).getJSONObject(0);
                    official.setLineAddress(addressJSON.getString(AppConstants.JSON_LINE1_KEY));
                    if (addressJSON.has(AppConstants.JSON_LINE2_KEY)) {
                        official.setLineAddress(official.getLineAddress() + ", " + addressJSON.getString(AppConstants.JSON_LINE2_KEY));
                    }
                    if (addressJSON.has(AppConstants.JSON_LINE3_KEY)) {
                        official.setLineAddress(official.getLineAddress() + ", " + addressJSON.getString(AppConstants.JSON_LINE3_KEY));
                    }
                    official.setCity(addressJSON.getString(AppConstants.JSON_CITY_KEY));
                    official.setState(addressJSON.getString(AppConstants.JSON_STATE_KEY));
                    official.setZip(addressJSON.getString(AppConstants.JSON_ZIP_KEY));
                }
                if (officialJSONObject.has(AppConstants.JSON_PARTY_KEY)) {
                    official.setParty(officialJSONObject.getString(AppConstants.JSON_PARTY_KEY));
                } else {
                    official.setParty("Unknown");
                }
                if (officialJSONObject.has(AppConstants.JSON_PHONE_KEY)) {
                    official.setPhone(officialJSONObject.getJSONArray(AppConstants.JSON_PHONE_KEY).get(0).toString());
                } else {
                    official.setPhone("No Data Provided");
                }
                if (officialJSONObject.has(AppConstants.JSON_URL_KEY)) {
                    official.setUrl(officialJSONObject.getJSONArray(AppConstants.JSON_URL_KEY).get(0).toString());
                } else {
                    official.setUrl("No Data Provided");
                }
                if (officialJSONObject.has(AppConstants.JSON_EMAIL_KEY)) {
                    official.setEmail(officialJSONObject.getJSONArray(AppConstants.JSON_EMAIL_KEY).get(0).toString());
                } else {
                    official.setEmail("No Data Provided");
                }
                if (officialJSONObject.has(AppConstants.JSON_PHOTO_URL)) {
                    official.setPhotoURL(officialJSONObject.getString(AppConstants.JSON_PHOTO_URL));
                } else {
                    official.setPhotoURL("No Data Provided");
                }

                if (officialJSONObject.has(AppConstants.JSON_CHANNEL_KEY)) {
                    JSONArray channelsJSONArray = officialJSONObject.getJSONArray(AppConstants.JSON_CHANNEL_KEY);
                    HashMap<String, String> channels = new HashMap<>();
                    for (int j = 0; j < channelsJSONArray.length(); j++) {
                        JSONObject channelJSONObject = channelsJSONArray.getJSONObject(j);
                        channels.put(channelJSONObject.getString(AppConstants.JSON_TYPE_KEY), channelJSONObject.getString(AppConstants.JSON_ID_KEY));
                    }
                    official.setChannels(channels);
                }
                officials.add(official);
            }

            JSONArray officeJSONArray = jsonObject.getJSONArray(AppConstants.JSON_OFFICE_KEY);
            for (int i = 0; i < officeJSONArray.length(); i++) {
                JSONObject officeJSONObject = officeJSONArray.getJSONObject(i);

                JSONArray officialIndices = officeJSONObject.getJSONArray(AppConstants.JSON_OFFICIAL_INDICES_KEY);
                for (int j = 0; j < officialIndices.length(); j++) {
                    officials.get(officialIndices.getInt(j)).setOfficeName(officeJSONObject.getString(AppConstants.JSON_NAME_KEY));
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
