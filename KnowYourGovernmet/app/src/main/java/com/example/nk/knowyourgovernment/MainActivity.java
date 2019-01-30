package com.example.nk.knowyourgovernment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private int MY_PERM_REQUEST_CODE = 12345;
    private Location bestLocation;
    private boolean showingInfo = false;

    private RecyclerView mRecyclerView;
    private OfficeRecyclerViewAdapter mAdapter;
    private TextView mLocationTextView;
    private SearchLocation mSearchLocation;
    private TextView mNetworkErrorTextView;

    private List<Official> mOfficialList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationTextView = (TextView) findViewById(R.id.location_textview);
        mNetworkErrorTextView = (TextView) findViewById(R.id.errorTextView);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        mAdapter = new OfficeRecyclerViewAdapter(this, mOfficialList);
        mRecyclerView.setAdapter(mAdapter);

        callCivicInfo();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.location:
                //Toast.makeText(this, "SearchLocation clicked!", Toast.LENGTH_SHORT).show();
                showEnterZipDialog();
                return true;
            case R.id.about:
                //Toast.makeText(this,"Help clicked",Toast.LENGTH_SHORT).show();
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onDestroy: Inside On Stop!");
        saveToSharedPref("");
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // I do not yet have permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERM_REQUEST_CODE);

            Log.d(TAG, "checkPermission: ACCESS_FINE_LOCATION Permission requested, awaiting response.");
            return false; // Do not yet have permission - but I just asked for it
        } else {
            Log.d(TAG, "checkPermission: Already have ACCESS_FINE_LOCATION Permission for this app.");
            return true;  // I already have this permission
        }
    }

    private void callCivicInfo() {
        SharedPreferences prefs = getSharedPreferences(AppConstants.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        String location = prefs.getString(AppConstants.SHARED_PREF_LOCATION_KEY, "");
        Log.d(TAG, "callCivicInfo: Location::" + location);
        if (!location.equalsIgnoreCase("")) {
            new CivicInfoAsyncTask(this).execute(location);
        } else {
            boolean havePermission = checkPermission();
            if (havePermission) {
                findLocation();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        if (requestCode == MY_PERM_REQUEST_CODE) {
            if (grantResults.length == 0) {
                Log.d(TAG, "onRequestPermissionsResult: Somehow I got an empty 'grantResults' array");
                return;
            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG", "Fine location permission granted");
                findLocation();

            } else {
                Toast.makeText(this, "Address cannot be acquired from the provided latitude/longitude", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    public void findLocation() {
        bestLocation = null;
        long timeNow = System.currentTimeMillis();

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager == null) {
            Toast.makeText(this, "No SearchLocation services available", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String providerName : locationManager.getAllProviders()) {
            sb.append("PROVIDER: ").append(providerName).append("\n");
            // You have to do this check here ot Android Studio complains. :(
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            android.location.Location loc = locationManager.getLastKnownLocation(providerName);

            if (loc != null) {
                sb.append("SearchLocation found:\n");
                sb.append("  Accuracy: ").append(loc.getAccuracy()).append("m\n");
                sb.append("  Time: ").append((timeNow - loc.getTime()) / 1000).append("sec\n");
                sb.append("  Latitude: ").append(loc.getLatitude()).append("\n");
                sb.append("  Longitude: ").append(loc.getLongitude()).append("\n\n");
                if (bestLocation == null || loc.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = new Location(loc);
                }
            } else {
                sb.append("No location for ").append(providerName).append("\n");
            }
        }
        if (bestLocation == null)
            sb.append("No location provider available :(");
        else
            sb.append(String.format(Locale.US, "\n\nBest Provider: %s (%.2f)",
                    bestLocation.getProvider(), bestLocation.getAccuracy()));
        getInfo();
        //((TextView) findViewById(R.id.textView)).setText(sb.toString());


    }

    public void getInfo() {
        int numResults = 10;
        if (bestLocation == null) {
            mLocationTextView.setText("No Data for Location");
            mNetworkErrorTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            //Toast.makeText(this, "No location available for Geocoder to use", Toast.LENGTH_LONG).show();
            return;
        }
        if (!showingInfo) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses;
            StringBuilder sb = new StringBuilder();
            try {
                addresses = geocoder.getFromLocation(
                        bestLocation.getLatitude(), bestLocation.getLongitude(), numResults);
                for (Address a : addresses) {
                    if (TextUtils.isDigitsOnly(a.getFeatureName().replace("-", ""))) {
                        //Toast.makeText(this, "Address found::" + a.getPostalCode(), Toast.LENGTH_SHORT).show();
                        saveToSharedPref(a.getPostalCode());
                        callCivicInfo();
                        break;
                    } else
                        sb.append(a.getFeatureName()).append("\n");
                }

                //((TextView) findViewById(R.id.textView)).setText(sb.toString());
            } catch (Exception e) {
                Toast.makeText(this, "Address cannot be acquired from provided latitude/longitude", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            showingInfo = true;
        } else {
            findLocation();
            showingInfo = false;
        }

    }

    public void getOfficialsData(SearchLocation searchLocation, List<Official> officials) {
        mOfficialList.clear();
        mSearchLocation = searchLocation;
        if (searchLocation != null && officials != null) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNetworkErrorTextView.setVisibility(View.GONE);
            mLocationTextView.setText(searchLocation.getCity() + ", " + searchLocation.getState() + " " + searchLocation.getZip());
            mOfficialList.addAll(officials);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mNetworkErrorTextView.setVisibility(View.VISIBLE);
            mLocationTextView.setText("No Data For Location");
        }
        mAdapter.notifyDataSetChanged();
    }

    public void showEnterZipDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.enter_city_state_zip_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText enterCityEditText = (EditText) dialogView.findViewById(R.id.enter_city_edittext);
        enterCityEditText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        dialogBuilder.setTitle("Enter a city, State or a Zip code");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                saveToSharedPref(enterCityEditText.getText().toString());
                callCivicInfo();
                //new CivicInfoAsyncTask(MainActivity.this).execute(enterCityEditText.getText().toString());
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog enterStockSymbolEditTextDialog = dialogBuilder.create();
        enterStockSymbolEditTextDialog.show();
    }

    public SearchLocation getSearchLocation() {
        return mSearchLocation;
    }

    private void saveToSharedPref(String address) {
        Log.d(TAG, "saveToSharedPref: ");
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AppConstants.SHARED_PREF_LOCATION_KEY, address);
        editor.apply();

        SharedPreferences prefs = getSharedPreferences(AppConstants.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        String location = prefs.getString(AppConstants.SHARED_PREF_LOCATION_KEY, "");
        Log.d(TAG, "saveToSharedPref: Location::" + location);
    }

    private void removeFromSavedPref() {

    }
}
