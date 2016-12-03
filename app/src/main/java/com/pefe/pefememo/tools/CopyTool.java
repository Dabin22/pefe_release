package com.pefe.pefememo.tools;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

/**
 * Created by dodoproject on 2016-12-01.
 */

public class CopyTool {
    //출저 http://iw90.tistory.com/154
    private static final String COPYCLIP_LABEL = "COPIED_MEMO";
    public static void copyMemo( TextView view){
        String content = view.getText().toString();
        ClipboardManager clipboardManager = (ClipboardManager)view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(COPYCLIP_LABEL, content);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(view.getContext(),"Memo Copied", Toast.LENGTH_SHORT).show();
    }
    //출저 http://stackoverflow.com/questions/19177231/android-copy-paste-from-clipboard-manager
    public static void pasteText(EditText view){
        ClipboardManager clipboard = (ClipboardManager)view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        String pasteData = "";
        int position = view.getSelectionEnd();
        if (clipboard.hasPrimaryClip()) {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            pasteData = item.getText().toString();
        }
        CharSequence currentCharS = view.getText();
        int nChars = currentCharS.length();
        String result;
        if(nChars==0){
            result = currentCharS.toString()+pasteData;
        }else {
            String head  = currentCharS.subSequence(0, position).toString();
            String tail = currentCharS.subSequence(position, currentCharS.length()).toString();
            result = head + pasteData + tail;
        }
        int pasteDatLength = pasteData.length();
        view.setText(result);
        view.requestFocus();
        view.setSelection(position+pasteDatLength);
    }
}

