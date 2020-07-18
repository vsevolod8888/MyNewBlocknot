package com.example.mynewblocknot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.mynewblocknot.EditeNoteActivity.NOTE_EXTRA_Key;

public class MainActivity extends AppCompatActivity implements NoteEventListener {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView; //Чтобы отобразить вертикально прокручиваемый список элементов, вы можете использовать виджет RecycleView
    private ArrayList<Note> notes;
    private NotesAdapter adapter;
    Toolbar toolbar;
    private NotesDao dao;
    FloatingActionButton fab;
    private SharedPreferences settings;
    public static final String APP_PREFERENCES = "Настройки блокнота";
    public static final String THEME_Key = "app_theme";
    private int theme;
    private MainActionModeCallBack actionModeCallBack;
    private int chackedCount = 0;
    private boolean isChecked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        theme = settings.getInt(THEME_Key,R.style.AppTheme);
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
       // getSupportActionBar().setIcon(R.mipmap.invert_colors);

  //      ActionBar actionBar = getSupportActionBar();                    //Добавил
   //     assert actionBar != null;
    //    toolbar.se         //(R.layout.switch_item);                  //Добавил
    //    toolbar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_CUSTOM);      //добавил
    //    Switch button = (Switch) findViewById(R.id.app_bar_switch);                                   //добавил
 //       button.setOnCheckedChangeListener(this);                                                      //добавил


        fab = (FloatingActionButton) findViewById(R.id.fab);
       // setupNavigation(savedInstanceState,toolbar);
        recyclerView = findViewById(R.id.notes_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //устанавливаем, что список будет отображаться в виде списка(а не клеточек)
        ;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 14.06.2020 add new note
                onAddNewNote();
            }
        });
        dao = NotesDB.getInstance(this).notesDao();  // доступ к базе данных, синглтон

    }
    private void loadNotes(){
        this.notes = new ArrayList<>();
        List<Note> list = dao.getNotes();       // &#x434;&#x43e;&#x441;&#x442;&#x430;&#x451;&#x43c; &#x432;&#x441;&#x435; Notes &#x438;&#x437; &#x431;&#x430;&#x437;&#x44b; &#x434;&#x430;&#x43d;&#x43d;&#x44b;&#x445;
        this.notes.addAll(list);

        this.adapter = new NotesAdapter(this,notes);
        //применяем listener к адаптеру
        this.adapter.setListener(this);
        this.recyclerView.setAdapter(adapter);

        swipeToDeleteHelper.attachToRecyclerView(recyclerView);
        showEmptyView();


    }
    //когда нету NOTES, то показывать сообщение в main_layout
    private void showEmptyView(){
        if(notes.size()==0){
            this.recyclerView.setVisibility(View.GONE);
            findViewById(R.id.empty_notes_view).setVisibility(View.VISIBLE);
        }else {
            this.recyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.empty_notes_view).setVisibility(View.GONE);
        }

    }
    private void onAddNewNote(){
        //TODO 18.06.2020 start EditeNoteActivity
        startActivity(new Intent(this,EditeNoteActivity.class));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){ //вызывается при нажатии пункта меню. Здесь мы определяем какой пункт меню был нажат.
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem awesomeMenuItem = menu.findItem(R.id.app_bar_switch);
        View awesomeActionView = awesomeMenuItem.getActionView();
        awesomeActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
       // return super.onCreateOptionsMenu(menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {       // обновление меню, вызывается каждый раз перед отображением меню
        MenuItem checkable = menu.findItem(R.id.app_bar_switch);
        Switch actionView = (Switch) checkable.getActionView();
        boolean isLightTheme = (theme == R.style.AppTheme);
        actionView.setChecked(!isLightTheme);
        actionView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onChangeTheme(isChecked);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.app_bar_switch:
                isChecked = !item.isChecked();
                item.setChecked(isChecked);
                onChangeTheme(isChecked);
                return true;
            default:
                return false;
        }
//        int id = item.getItemId();
//        if (id == R.id.app_bar_switch)
//            onChangeTheme(isChecked);                                                                         //// добавил + в класс Main Activity добавил boolean isChecked
//       return super.onOptionsItemSelected(item);

  }

    private void onChangeTheme(boolean isChecked) {
        if(isChecked){
            settings.edit().putInt(THEME_Key,R.style.AppTheme_Dark).apply();
        }else {
            settings.edit().putInt(THEME_Key,R.style.AppTheme).apply();
        }
        MainActivity.this.recreate();
    }

    protected void onResume(){                               //
        super.onResume();
        loadNotes();
    }


    @Override
    public void onNoteClick(Note note) {
        //TODO 19.06.2020 note clicked: edit note
        Log.d(TAG,"OnNoteClick "+note.toString());
        Intent edit = new Intent(this,EditeNoteActivity.class);
        edit.putExtra(NOTE_EXTRA_Key,note.getId()); //в этот интент добавляю id блокнота, который хочу поменять
        startActivity(edit);

    }

    @SuppressLint("RectrictedApi")
    @Override
    public void onNoteLongClick(Note note) {
        //TODO 19.06.2020 note long clicked: delete, share...
        note.setChecked(true);
        chackedCount = 1;
        adapter.setMultiCheckMode(true);        //для чего этот метод ?????


        adapter.setListener(new NoteEventListener() {
            @Override
            public void onNoteClick(Note note) {
                //   note.setChecked(!note.isChecked());    // обратный выбор
                if(note.isChecked()){
                    chackedCount++;}
                else {chackedCount--;}
                if (chackedCount>1){
                    actionModeCallBack.changeShareItemVizible(false);
                }else
                    actionModeCallBack.changeShareItemVizible(true);
                if(chackedCount==0){
                    actionModeCallBack.getAction();//.finish();
                }
                actionModeCallBack.setCount(chackedCount+" из "+notes.size());
                recyclerView.post(new Runnable()
                {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onNoteLongClick(Note note) {


            }
        });
        actionModeCallBack = new MainActionModeCallBack() {
            @Override
            public boolean onActionItemClicked(androidx.appcompat.view.ActionMode mode, MenuItem item) {
                if(item.getItemId() == R.id.action_delete_notes)
                    onDeleteMultiNotes();
                else if (item.getItemId()==R.id.action_share_notes){
                    onShareNote();}
                mode.finish();
                return false;
            }
        };
        startSupportActionMode(actionModeCallBack);    //actionMode = startSupportActionMode(this);
        fab.setVisibility(View.GONE);
        actionModeCallBack.setCount(chackedCount + "/"+notes.size());

    }
    private void onShareNote() {
        // TODO 21.06.2020 расшариваем только один блокнот
        Note note = adapter.getCheckedNotes().get(0);             //?????????????
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");                              //?????????????
        String noteText = note.getNoteText()+"\n\n Создано "+NoteUtils.dateFromLong(note.getNoteDate())+"\n " + getString(R.string.app_name);
        share.putExtra(Intent.EXTRA_TEXT,noteText);
        startActivity(share);
    }

    private void onDeleteMultiNotes() {
        // TODO 21.06.2020 удаляем выделенные блокноты
        List<Note>checkedNotes = adapter.getCheckedNotes();
        if(checkedNotes.size()!=0){
            for(Note note:checkedNotes){
                dao.deleteNote(note);
            }
            loadNotes();
            Toast.makeText(this,checkedNotes.size()+" блокнот(ов) удалено",Toast.LENGTH_SHORT).show();
        }else Toast.makeText(this,"блокнот(ы) не выбраны",Toast.LENGTH_SHORT).show();
    }
    @SuppressLint("RestrictedApi")
    @Override
    public void onSupportActionModeFinished(@NonNull androidx.appcompat.view.ActionMode mode) {
        super.onSupportActionModeFinished(mode);
        adapter.setMultiCheckMode(false);  //set back the old listener
        adapter.setListener(this);
        fab.setVisibility(View.VISIBLE);
    }

    private ItemTouchHelper swipeToDeleteHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //TODO 22.06.2020  удалить блокнот когда swipe
            if(notes!=null){
                //get swiped note
                Note swipedNote = notes.get(viewHolder.getAdapterPosition());
                if(swipedNote!=null){
                    swipeToDelete(swipedNote, viewHolder);
                }

            }

        }
    });
    private void swipeToDelete(final Note swipedNote, final RecyclerView.ViewHolder viewHolder) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("Удалить запись?")
                .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO 22.06.2020 удалить запись
                        dao.deleteNote(swipedNote);
                        notes.remove(swipedNote);
                        adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        showEmptyView();
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO 22.06.2020 отменить и восстановить
                        recyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());

                    }
                })
                .setCancelable(false)
                .create().show();
    }
//
//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        if(isChecked){
//            settings.edit().putInt(THEME_Key,R.style.AppTheme_Dark).apply();
//        }else {
//            settings.edit().putInt(THEME_Key,R.style.AppTheme).apply();
//        }
//        MainActivity.this.recreate();
//
//    }
}
