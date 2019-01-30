package com.example.nk.multinotesapp.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nk.multinotesapp.R;
import com.example.nk.multinotesapp.activities.EditNoteActivity;
import com.example.nk.multinotesapp.objects.MultiNote;

import java.util.List;

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<NotesRecyclerViewAdapter.ViewHolder> {
    private List<MultiNote> mMultiNotesList;
    private Context mContext;
    private static int REQUEST_CODE = 1;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mMultiNoteCardView;
        public TextView mNotesTitleTextView;
        public TextView mNotesDateTimeTextView;
        public TextView mNotesDescription;

        public ViewHolder(CardView multiNoteCardView) {
            super(multiNoteCardView);
            mMultiNoteCardView = multiNoteCardView;
            mNotesTitleTextView = mMultiNoteCardView.findViewById(R.id.textViewTitle);
            mNotesDateTimeTextView = mMultiNoteCardView.findViewById(R.id.textViewDateTime);
            mNotesDescription = mMultiNoteCardView.findViewById(R.id.textViewDescription);
        }
    }

    public NotesRecyclerViewAdapter(Context context, List<MultiNote> multiNotesList) {
        mContext = context;
        mMultiNotesList = multiNotesList;
    }

    @Override
    public NotesRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        CardView multiNoteCardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notes_row_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(multiNoteCardView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final MultiNote multiNote = mMultiNotesList.get(position);
        holder.mNotesTitleTextView.setText(multiNote.getNoteTitle());
        holder.mNotesDateTimeTextView.setText(multiNote.getNoteDateTime());
        if (multiNote.getNoteDescription().length() > 79) {
            holder.mNotesDescription.setText(multiNote.getNoteDescription().substring(0,79)+"...");
        } else {
            holder.mNotesDescription.setText(multiNote.getNoteDescription());
        }

        holder.mMultiNoteCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToEditActivity(multiNote);
            }
        });
        holder.mMultiNoteCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteDialog(multiNote);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMultiNotesList.size();
    }

    private void navigateToEditActivity(MultiNote multiNote) {
        Intent editNoteActivityIntent = new Intent(mContext, EditNoteActivity.class);
        mMultiNotesList.remove(multiNote);
        editNoteActivityIntent.putExtra(mContext.getResources().getString(R.string.intent_multinote_key), multiNote);
        ((AppCompatActivity) mContext).startActivityForResult(editNoteActivityIntent, REQUEST_CODE);
    }

    private void showDeleteDialog(final MultiNote multiNote) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setTitle("Delete Note \"" + multiNote.getNoteTitle() + "\"?")
                .setPositiveButton(mContext.getResources().getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMultiNoteFromList(multiNote);
                    }
                })
                .setNegativeButton(mContext.getResources().getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteMultiNoteFromList(MultiNote multiNote) {
        mMultiNotesList.remove(multiNote);
        NotesRecyclerViewAdapter.this.notifyDataSetChanged();
    }
}

