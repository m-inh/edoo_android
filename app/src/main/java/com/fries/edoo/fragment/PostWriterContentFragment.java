package com.fries.edoo.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fries.edoo.R;

import jp.wasabeef.richeditor.RichEditor;

/**
 * Created by tmq on 20/08/2016.
 */
public class PostWriterContentFragment extends Fragment {
    private View rootView;

    private RichEditor mEditor;
    private int textSizeEditor;
    private EditText edtTitlePost;

    private ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_post_content, null);

        initViews();
        return rootView;
    }

    private void initViews(){
        edtTitlePost = (EditText) rootView.findViewById(R.id.edt_title_post);


        mEditor = (RichEditor) rootView.findViewById(R.id.editor_rich_editor);
//        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(16);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setPadding(8, 8, 8, 8);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        //    mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        mEditor.setPlaceholder("Write post here ...");

//        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
//            @Override
//            public void onTextChange(String text) {
//                mPreview.setText(text);
//                mWebView.getSettings().setJavaScriptEnabled(true);
//                mWebView.loadData(text, "text/html", "UTF-8");
//            }
//        });

        rootView.findViewById(R.id.editor_action_undo).setOnClickListener(clickToolEditor);
        rootView.findViewById(R.id.editor_action_bold).setOnClickListener(clickToolEditor);
        rootView.findViewById(R.id.editor_action_italic).setOnClickListener(clickToolEditor);
        rootView.findViewById(R.id.editor_action_underline).setOnClickListener(clickToolEditor);
        rootView.findViewById(R.id.editor_action_text_size).setOnClickListener(clickToolEditor);
        rootView.findViewById(R.id.editor_action_bullets).setOnClickListener(clickToolEditor);
        rootView.findViewById(R.id.editor_action_insert_image).setOnClickListener(clickToolEditor);
        rootView.findViewById(R.id.editor_action_insert_link).setOnClickListener(clickToolEditor);

        mEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                HorizontalScrollView editorToolBar = (HorizontalScrollView) rootView.findViewById(R.id.editor_tool_bar);
                if (b) {
                    editorToolBar.setVisibility(View.VISIBLE);
                } else {
                    editorToolBar.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Receive event click to choose tool in Editor: Undo, TextSize, Bold, Italic, Underline, InsertImage, InsertLink
     */
    View.OnClickListener clickToolEditor = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.editor_action_undo:
                    mEditor.undo();
                    break;
                case R.id.editor_action_text_size:
                    textSizeEditor = (textSizeEditor + 1) % 3;
                    mEditor.setHeading((textSizeEditor + 1) * 2);
                    break;
                case R.id.editor_action_bold:
                    mEditor.setBold();
                    break;
                case R.id.editor_action_italic:
                    mEditor.setItalic();
                    break;
                case R.id.editor_action_underline:
                    mEditor.setUnderline();
                    break;
                case R.id.editor_action_bullets:
                    mEditor.setBullets();
                    break;
                case R.id.editor_action_insert_image:
                    mEditor.insertImage("http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG", "dachshund");
                    break;
                case R.id.editor_action_insert_link:
                    showDialogInsertLink();
                    break;
            }
        }
    };

    private void showDialogInsertLink() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), R.style.DialogAnimation);
        dialog.setTitle(R.string.dialog_title_insert_link);
        dialog.setIcon(R.drawable.ic_editor_insert_link_color);
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_insert_link_editor, null);
        dialog.setView(view);

        final EditText link = (EditText) view.findViewById(R.id.edt_insert_url);
        final ImageView error = (ImageView) view.findViewById(R.id.iv_link_invalid);
        link.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (!b) {
                    String strLink = link.getText().toString();
                    if (!strLink.contains("http")) {
                        strLink = "http://" + strLink;
                        link.setText(strLink);
                    }
                    if (!Patterns.WEB_URL.matcher(strLink).matches()) {
                        Toast.makeText(getContext(), "Link error", Toast.LENGTH_SHORT).show();
                        error.setVisibility(View.VISIBLE);
                    }
                } else {
                    error.setVisibility(View.GONE);
                }
            }
        });

        dialog.setPositiveButton(R.string.insert, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText linkTitle = (EditText) view.findViewById(R.id.edt_insert_url_title);

                String txtLink = link.getText().toString();
                String txtLinkTitle = linkTitle.getText().toString();

                if (txtLink.isEmpty()) {
                    Toast.makeText(getContext(), "Link rỗng", Toast.LENGTH_SHORT).show();
                } else {
                    if (txtLinkTitle.isEmpty()) {
                        mEditor.insertLink(txtLink, txtLink);
                    } else mEditor.insertLink(txtLink, txtLinkTitle);
                }
            }
        });

        dialog.show();
    }

}
