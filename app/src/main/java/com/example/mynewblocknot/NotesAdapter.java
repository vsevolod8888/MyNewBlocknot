package com.example.mynewblocknot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteHolder>  {
    private Context context;
    private ArrayList<Note> notes;                  // и отображает их в ViewHolder-ах
    private NoteEventListener listener;
    private boolean multiCheckMode = false;

    public NotesAdapter(Context context,ArrayList<Note> notes) {
        this.context = context;
        this.notes = notes;
    }
    @NonNull
    @Override
    public NotesAdapter.NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.note_layout, parent, false);
        return new NoteHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.NoteHolder holder, final int position) {
        final Note note = getNote(position);                                       //засетить данные в Holder
        if (note != null) {                                  // это условие не понял?????????
            holder.noteText.setText(note.getNoteText());
            holder.noteDate.setText(NoteUtils.dateFromLong(note.getNoteDate()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {          //отсюда и дальше не понятно????????
                @Override
                public void onClick(View v) {
                    if(multiCheckMode){
                        note.setChecked(!note.isChecked());
                        notes.set(position,note);
                    }
                    listener.onNoteClick(note );
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onNoteLongClick(note);
                    return false;
                }
            });
            //check checkBox if note selected
            if (multiCheckMode){  // ?????????
                holder.checkBox.setChecked(note.isChecked());
                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(note.isChecked()==isChecked){
                            return;
                        }
                        note.setChecked(isChecked);
                        notes.set(position,note);
                        listener.onNoteClick(note );
                    }
                });
                holder.checkBox.setVisibility(View.VISIBLE);//show checkBox if multimode on
             //   holder.checkBox.setChecked(note.isChecked());
            }else holder.checkBox.setVisibility(View.GONE); //hide checkBox if multimode off
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
    private Note getNote(int pozition) {
        return notes.get(pozition);
    }


    public List<Note> getCheckedNotes() {
        List<Note>checkedNotes = new ArrayList<>();
        for (Note n: this.notes){
            if (n.isChecked())
                checkedNotes.add(n);
        }
        return checkedNotes;
    }




    class NoteHolder extends RecyclerView.ViewHolder {
        TextView noteText, noteDate;
        CheckBox checkBox;

        public NoteHolder(@NonNull View itemView) {

            super(itemView);
            noteDate = itemView.findViewById(R.id.not_date);
            noteText = itemView.findViewById(R.id.note_text);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
    public void setListener(NoteEventListener listener){
        this.listener = listener;
    }
    public void setMultiCheckMode(boolean multiCheckMode){
        this.multiCheckMode = multiCheckMode;
        notifyDataSetChanged();
    }
}
