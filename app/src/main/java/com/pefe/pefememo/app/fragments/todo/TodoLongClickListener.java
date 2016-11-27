package com.pefe.pefememo.app.fragments.todo;

import android.view.View;

/**
 * Created by Dabin on 2016-11-27.
 */

public class TodoLongClickListener implements View.OnLongClickListener {
    @Override
    public boolean onLongClick(View view) {
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                view);
        view.startDrag(null, shadowBuilder, view, 0);
        view.setVisibility(View.INVISIBLE);
        return true;
    }
}
