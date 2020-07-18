package com.example.mynewblocknot;



import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.view.ActionMode;

public abstract class MainActionModeCallBack implements ActionMode.Callback {
    private ActionMode action;
    private MenuItem countItem;
    private MenuItem shareItem;

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.main_action_mode,menu);
        this.action = mode;
        this.countItem = menu.findItem(R.id.action_checked_count);
        this.shareItem = menu.findItem(R.id.action_share_notes);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

//    @Override
//    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//        return false;
//    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }
    public void setCount(String checkedCount){
        if (countItem!=null)
            this.countItem.setTitle(checkedCount);

    }

    public  void changeShareItemVizible(boolean b){
        shareItem.setVisible(b);
    }

    public ActionMode getAction() {
        return action;
    }

    public abstract boolean onActionItemClicked(androidx.appcompat.view.ActionMode mode, MenuItem item);
}
