package com.pefe.pefememo.app.fragments.memo;

import android.util.Log;
import android.view.DragEvent;
import android.view.View;

import com.pefe.pefememo.R;
import com.pefe.pefememo.app.fragments.todo.TodoHandler;
import com.pefe.pefememo.app.fragments.todo.TodoViewType;

import java.util.Date;

/**
 * Created by Dabin on 2016-11-27.
 */

public class MemoDragListener implements View.OnDragListener {

    private TodoHandler handler;

    public MemoDragListener(TodoHandler handler) {
        this.handler = handler;
    }

    private int pickedIndex = -1;
    private String pickedType = "";
    private int targetIndex = -1;
    private String targetType = "";
    private Date pickedBelongDate = null;

    @Override           // 드래그 목표
    public boolean onDrag(View view, DragEvent dragEvent) {

        //드래깅 대상정보
        View view_dragging = (View) dragEvent.getLocalState();

        pickedType = ((TodoViewType) view_dragging.getTag()).getType();
        pickedIndex = ((TodoViewType) view_dragging.getTag()).getIndex();
        pickedBelongDate = (((TodoViewType) view_dragging.getTag()).getBelongDate());

        switch (dragEvent.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                if (pickedType.equals("Today")) {
                    handler.setSpinner(pickedIndex);
                }
                handler.change_mode(true);
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                if (view.getId() == R.id.ib_before || view.getId() == R.id.ib_next) {
                    handler.moveDate(view.getId());
                    targetIndex = -1;
                    targetType = "";
                } else if (view.getId() == R.id.delete_layout) {
                    handler.isEntered(true);
                    targetIndex = -1;
                    targetType = "";
                } else {
                    if (view.getTag() != null) {
                        targetIndex = ((TodoViewType) view.getTag()).getIndex();
                        targetType = ((TodoViewType) view.getTag()).getType();
                        Log.e("draglistener", "target index = " + targetIndex + ", type" + targetType);
                    }

                }
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                if (view.getId() == R.id.ib_before || view.getId() == R.id.ib_next) {
                    handler.stop();
                } else if (view.getId() == R.id.delete_layout) {
                    handler.isEntered(false);
                }
                break;
            case DragEvent.ACTION_DROP:
                handler.stop();
                if (view.getId() == R.id.delete_layout) {
                    handler.delete(pickedType, pickedIndex, pickedBelongDate);
                } else if (pickedType.equals("Once") || pickedType.equals("Repeat") || pickedType.equals("Old")) {

                    if (targetType.equals(pickedType)) {
                        handler.swapPosition(pickedIndex, targetIndex, targetType);
                    } else if (targetType.equals("Today") || targetType.equals("Today_list")) {
                        handler.register_todo(pickedType, pickedIndex, targetType);
                    }

                } else if (pickedType.equals("Today") || pickedType.equals("Today_list")) {
                    Log.e("view", "view get tag" + targetType);
                    if (targetType.equals("Today") || targetType.equals("Today_list")) {
                        if (pickedBelongDate != null) {
                            handler.changeBelongDate(pickedBelongDate, pickedIndex);
                        }
                    } else if (targetType.equals("Bottom") || targetType.equals("Once") || targetType.equals("Repeat")) {
                        if (pickedBelongDate != null) {
                            handler.unRegister_mode(pickedBelongDate, pickedIndex);
                        }

                    }
                }
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                handler.stop();
                handler.change_mode(false);
                if (view_dragging != null)
                    view_dragging.setVisibility(View.VISIBLE);
                break;
            default:
                break;

        }

        return true;
    }
}
