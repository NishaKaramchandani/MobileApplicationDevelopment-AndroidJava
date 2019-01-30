package com.example.nk.multinotesapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nk.multinotesapp.R;
import com.example.nk.multinotesapp.objects.MultiNote;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditNoteActivity extends AppCompatActivity {

    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private static int NOTE_REQUEST_CODE = 1;
    private static int NO_NOTE_REQUEST_CODE = 2;
    private MultiNote mMultiNote;
    SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss a");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notes);

        mTitleEditText = findViewById(R.id.editTextTitle);
        mDescriptionEditText = findViewById(R.id.editTextDescription);

        Bundle data = getIntent().getExtras();
        if (data != null) {
            mMultiNote = (MultiNote) data.getParcelable(getResources().getString(R.string.intent_multinote_key));
        }

        if (mMultiNote != null) {
            mTitleEditText.setText(mMultiNote.getNoteTitle());
            mDescriptionEditText.setText(mMultiNote.getNoteDescription());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editnotes_action_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if ((mMultiNote == null && !mTitleEditText.getText().toString().trim().equals("")) || (mMultiNote != null && !mTitleEditText.getText().toString().trim().equals("") && (!mTitleEditText.getText().toString().equals(mMultiNote.getNoteTitle()) || !mDescriptionEditText.getText().toString().trim().equals(mMultiNote.getNoteDescription())))) {
            showDiscardChangesDialog();
        } else if (mTitleEditText.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Add/Edit note not saved!", Toast.LENGTH_SHORT).show();
            if (mMultiNote != null) {
                saveNoteAndNavigateBack();
            } else {
                navigateWithoutSavingNote();
            }
        } else {
            navigateWithoutSavingNote();
        }
    }

    private void showDiscardChangesDialog() {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Your note is not saved!")
                .setMessage("Save note \"" + mTitleEditText.getText() + "\"?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveNoteAndNavigateBack();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        navigateWithoutSavingNote();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save_note:
                if ((mMultiNote == null && !mTitleEditText.getText().toString().trim().equals("")) || (mMultiNote != null && !mTitleEditText.getText().toString().trim().equals("") && (!mTitleEditText.getText().toString().equals(mMultiNote.getNoteTitle()) || !mDescriptionEditText.getText().toString().trim().equals(mMultiNote.getNoteDescription())))) {
                    saveNoteAndNavigateBack();
                }  else {
                    Toast.makeText(this, "Add/Edit note not saved!", Toast.LENGTH_SHORT).show();
                    if (mMultiNote != null) {
                        saveNoteAndNavigateBack();
                    } else {
                        navigateWithoutSavingNote();
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNoteAndNavigateBack() {
        try {
            Intent intent = new Intent();
            if (mMultiNote == null) {
                mMultiNote = new MultiNote();
            }
            mMultiNote.setNoteTitle(mTitleEditText.getText().toString());
            mMultiNote.setNoteDescription(mDescriptionEditText.getText().toString());
            mMultiNote.setNoteDateTime(formatter.format(new Date()));
            intent.putExtra(getResources().getString(R.string.intent_multinote_key), mMultiNote);
            setResult(NOTE_REQUEST_CODE, intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateWithoutSavingNote() {
        Intent intent = new Intent();
        setResult(NO_NOTE_REQUEST_CODE, intent);
        finish();
    }
}
