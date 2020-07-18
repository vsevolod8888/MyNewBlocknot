package com.example.mynewblocknot;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Date;

public class EditeNoteActivity extends AppCompatActivity {
    private EditText inputNote;
    private NotesDao dao;
    private Note temp;
    private Toolbar toolbar;
    public static final String NOTE_EXTRA_Key = "Note Id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edite_note);
        toolbar = findViewById(R.id.edit_note_activity_toolbar);
        setSupportActionBar(toolbar);
        inputNote = findViewById(R.id.input_note);
        dao = NotesDB.getInstance(this).notesDao();         // dao это база данных???
        if (getIntent().getExtras() != null) {         //проверяем, были ли какие-то данные переданы в интент при старте(в мейн активити)
            int id = getIntent().getExtras().getInt(NOTE_EXTRA_Key, 0);//достаю id по ключу, который передал в мейн активити
            temp = dao.getNoteById(id);              //открываем блокнот, который по айдишке нашли
            inputNote.setText(temp.getNoteText()); //в эдит текст сетим текст, который раньше там был сохранён
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edite_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save_note)
            onSaveNote();
        return super.onOptionsItemSelected(item);
    }

    private void onSaveNote() {
        //TODO: 18.06.2020
        String text = inputNote.getText().toString();
        if (!text.isEmpty()) {
            long date = new Date().getTime();

            if (temp == null) {
                temp = new Note(text, date);  //создать новый else если существует, то обновить
                dao.insertNote(temp);
            } else {
                temp.setNoteText(text);
                temp.setNoteDate(date);
                dao.updateNote(temp);        //изменить текст и дату и обновить блокнот в базе данных
            }
            finish();          //возврат к Main Activity
        }
    }



}
