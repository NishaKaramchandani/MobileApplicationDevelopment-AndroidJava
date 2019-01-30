package com.example.nk.multinotesapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nk.multinotesapp.adapters.NotesRecyclerViewAdapter;
import com.example.nk.multinotesapp.R;
import com.example.nk.multinotesapp.objects.MultiNote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mNotesRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private NotesRecyclerViewAdapter mNotesRecyclerViewAdapter;
    private List<MultiNote> mMultiNotesList = new ArrayList<>();
    private TextView mNoNotesTextView;
    private static int REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";
    SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNotesRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_notes);
        mNoNotesTextView = (TextView) findViewById(R.id.textViewNoNotes);

        LoadJSON task = new LoadJSON();
        task.execute();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivity_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_note:
                Intent editNotesIntent = new Intent(this, EditNoteActivity.class);
                startActivityForResult(editNotesIntent, REQUEST_CODE);
                return true;
            case R.id.help:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: Inside onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQUEST_CODE) {
            MultiNote note = data.getParcelableExtra(getResources().getString(R.string.intent_multinote_key));
            mMultiNotesList.add(note);
            Collections.sort(mMultiNotesList, new Comparator<MultiNote>() {
                public int compare(MultiNote multiNote1, MultiNote multiNote2) {
                    try {
                        return formatter.parse(multiNote2.getNoteDateTime()).compareTo(formatter.parse(multiNote1.getNoteDateTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });
            mNoNotesTextView.setVisibility(View.GONE);
            mNotesRecyclerView.setVisibility(View.VISIBLE);
            mNotesRecyclerViewAdapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Inside onPause");
        writeJSONToFile();
    }

    private void writeJSONToFile() {
        try {
            JSONObject mainObject = new JSONObject();
            mainObject.put(getResources().getString(R.string.json_array_key), arrayListToJSONArray());
            SaveJSON saveJSONTask = new SaveJSON();
            saveJSONTask.execute(mainObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONArray arrayListToJSONArray() {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < mMultiNotesList.size(); i++) {
            jsonArray.put(multiNoteToJSON(mMultiNotesList.get(i)));
        }
        return jsonArray;
    }

    private JSONObject multiNoteToJSON(MultiNote multiNote) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(getResources().getString(R.string.json_note_title_key), multiNote.getNoteTitle());
            jsonObject.put(getResources().getString(R.string.json_note_datetime_key), multiNote.getNoteDateTime());
            jsonObject.put(getResources().getString(R.string.json_note_description_key), multiNote.getNoteDescription());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private class LoadJSON extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String result = new JSONObject().toString();
            try {
                InputStream inputStream = openFileInput("notes_json.json");
                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(receiveString);
                    }
                    inputStream.close();
                    result = stringBuilder.toString();
                }
            } catch (FileNotFoundException e) {
                Log.e(TAG, "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e(TAG, "Can not read file: " + e.toString());
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject baseJSONObject = new JSONObject(result);
                if (baseJSONObject.has(getResources().getString(R.string.json_array_key))) {
                    JSONArray notesJSONArray = baseJSONObject.getJSONArray(getResources().getString(R.string.json_array_key));
                    mMultiNotesList = new ArrayList<>();
                    for (int i = 0; i < notesJSONArray.length(); i++) {
                        JSONObject noteJSONObject = notesJSONArray.getJSONObject(i);
                        MultiNote multiNote = new MultiNote(noteJSONObject.getString(getResources().getString(R.string.json_note_title_key)), noteJSONObject.getString(getResources().getString(R.string.json_note_datetime_key)), noteJSONObject.getString(getResources().getString(R.string.json_note_description_key)));
                        mMultiNotesList.add(multiNote);
                    }
                }
                if (mMultiNotesList.size() == 0) {
                    mNoNotesTextView.setVisibility(View.VISIBLE);
                    mNotesRecyclerView.setVisibility(View.GONE);
                } else {
                    mNoNotesTextView.setVisibility(View.GONE);
                    mNotesRecyclerView.setVisibility(View.VISIBLE);
                }
                mLayoutManager = new LinearLayoutManager(MainActivity.this);
                mNotesRecyclerView.setLayoutManager(mLayoutManager);
                Collections.sort(mMultiNotesList, new Comparator<MultiNote>() {
                    public int compare(MultiNote multiNote1, MultiNote multiNote2) {
                        try {
                            return formatter.parse(multiNote2.getNoteDateTime()).compareTo(formatter.parse(multiNote2.getNoteDateTime()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 1;
                        }
                    }
                });
                mNotesRecyclerViewAdapter = new NotesRecyclerViewAdapter(MainActivity.this, mMultiNotesList);
                mNotesRecyclerView.setAdapter(mNotesRecyclerViewAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class SaveJSON extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("notes_json.json", Context.MODE_PRIVATE));
                outputStreamWriter.write(params[0]);
                outputStreamWriter.close();
                return "JSON save success";
            } catch (IOException e) {
                Log.e(TAG, "File write failed: " + e.toString());
                return "JSON save failed";
            }

        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: " + result);
        }
    }
}
