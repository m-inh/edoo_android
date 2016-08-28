package com.fries.edoo.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fries.edoo.io.*;

import com.android.volley.Request;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.fries.edoo.R;
import com.fries.edoo.activities.PostWriterActivity;
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.communication.MultipartRequest;
import com.fries.edoo.utils.CommonVLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jp.wasabeef.richeditor.RichEditor;

/**
 * Created by tmq on 20/08/2016.
 */
public class PostWriterContentFragment extends Fragment {
    private static final String TAG = PostWriterContentFragment.class.getSimpleName();
    private static final String HTML_PLACE_HOLDER = "<a style='color:#80CBC4'>Write content ...<a><br><br><br><br><br><br><br><br><br><br>";
    private static final int IMAGE_LOCAL_REQUEST = 34242;
    private static final int CAMERA_REQUEST = 23423;

    private View rootView;

    private RichEditor mEditor;
    private int textSizeEditor;
    private EditText edtTitlePost;
    private ImageButton editorInsertImage;

    private ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_post_content, null);

        initViews();

        return rootView;
    }

    private void initViews() {
        edtTitlePost = (EditText) rootView.findViewById(R.id.edt_title_post);


        mEditor = (RichEditor) rootView.findViewById(R.id.editor_rich_editor);
        mEditor.setEditorFontSize(16);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setPadding(8, 8, 8, 8);
        mEditor.setPlaceholder("Write post here ...");

        rootView.findViewById(R.id.editor_action_undo).setOnClickListener(clickToolEditor);
        rootView.findViewById(R.id.editor_action_bold).setOnClickListener(clickToolEditor);
        rootView.findViewById(R.id.editor_action_italic).setOnClickListener(clickToolEditor);
        rootView.findViewById(R.id.editor_action_underline).setOnClickListener(clickToolEditor);
        rootView.findViewById(R.id.editor_action_text_size).setOnClickListener(clickToolEditor);
        rootView.findViewById(R.id.editor_action_bullets).setOnClickListener(clickToolEditor);
        editorInsertImage = (ImageButton) rootView.findViewById(R.id.editor_action_insert_image);
        rootView.findViewById(R.id.editor_action_insert_link).setOnClickListener(clickToolEditor);

        editorInsertImage.setOnClickListener(clickToolEditor);

        mEditor.setHtml(HTML_PLACE_HOLDER);
        mEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                HorizontalScrollView editorToolBar = (HorizontalScrollView) rootView.findViewById(R.id.editor_tool_bar);
                if (b) {
                    editorToolBar.setVisibility(View.VISIBLE);
                    if (mEditor.getHtml().equals(HTML_PLACE_HOLDER)) {
                        mEditor.focusEditor();
                        mEditor.setHtml("");
                    }
                } else {
                    editorToolBar.setVisibility(View.GONE);
                }

            }
        });

        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                if (newImageInserted) {
                    int posTagImg = text.lastIndexOf("img") + 3;

                    String html = text.substring(0, posTagImg) + resizeImage() + text.substring(posTagImg, text.length()) + "<br><br>";
                    mEditor.setHtml(html);
                    newImageInserted = false;
                    mEditor.focusEditor();
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
                    pickImage();
                    break;
                case R.id.editor_action_insert_link:
                    showDialogInsertLink();
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "result code = " + resultCode);
        if (resultCode != Activity.RESULT_OK) return;
        newImageInserted = true;
        switch (requestCode) {
            case IMAGE_LOCAL_REQUEST:
                Log.i(TAG, data.getData().toString());
                File file = new File(FileManager.getPath(getContext(), data.getData()));
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                imgWidth = bitmap.getWidth();
                imgHeight = bitmap.getHeight();
                uploadImage(bitmap);
                mEditor.insertImage(data.getData().toString(), "alt_image");
                break;
            case CAMERA_REQUEST:
                try {
                    Bitmap photo = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
                    if (photo == null) return;
                    imgWidth = photo.getWidth();
                    imgHeight = photo.getHeight();
                    uploadImage(photo);
                    mEditor.insertImage(FileManager.getPath(getContext(), photoUri), "alt_photo");
                    Log.i(TAG, "Size = " + imgWidth + ", " + imgHeight);

                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    // ----------------------------------- Insert Link ---------------------------------------------

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

    // --------------------------------------- Insert Image ----------------------------------------

    private String preHTMLEditor = HTML_PLACE_HOLDER;
    private int imgWidth, imgHeight;
    private boolean newImageInserted = false;
    private ArrayList<String> arrImageCloud = new ArrayList<>(); // Save link Image, what is uploaded to server
    private Uri photoUri;

    private void pickImage() {
        PopupMenu menu = new PopupMenu(getContext(), editorInsertImage);
        menu.getMenuInflater().inflate(R.menu.pick_image, menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_pick_image_gallery:
                        pickImageFromMemory();
                        break;
                    case R.id.action_pick_image_camera:
                        pickImageFromCamera();
                        break;
                }
                return true;
            }
        });
        menu.show();
    }

    private void pickImageFromMemory() {
        Intent iImage = (new Intent("android.intent.action.GET_CONTENT")).setType("image/*");
        preHTMLEditor = mEditor.getHtml() + "";

        if (CommonVLs.isHasStoragePermissions(getActivity())){
            startActivityForResult(iImage, IMAGE_LOCAL_REQUEST);
        } else {
            CommonVLs.verifyStoragePermissions(getActivity());
        }
    }



    private void pickImageFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        photoUri = getContext().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

        if (CommonVLs.isHasCameraPermissions(getActivity())){
            startActivityForResult(intent, CAMERA_REQUEST);
        } else {
            CommonVLs.verifyCameraPermissions(getActivity());
        }
    }

    private String resizeImage() {
        int screenWidth = mEditor.getWidth() - 2 * mEditor.getPaddingLeft() - 2 * mEditor.getPaddingRight();
        Log.i(TAG, "Screen width = " + screenWidth);
        float scale = ((float) screenWidth) / imgWidth;
        imgWidth = screenWidth;
        imgHeight *= scale;

        DisplayMetrics metrics = new DisplayMetrics();
        ((PostWriterActivity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;

        imgWidth = (int) Math.ceil(imgWidth / logicalDensity);
        imgHeight = (int) Math.ceil(imgHeight / logicalDensity);

        Log.i(TAG, "Resize = " + imgWidth + ", " + imgHeight);
        return " width = '" + imgWidth + "' height = '" + imgHeight + "' ";
    }

    public void replaceUrlImage() {
        StringBuilder content = new StringBuilder(getContentPost());
        int posImg = 0;
        for (String url : arrImageCloud) {
            posImg = content.indexOf("src", posImg);

            int pos1 = content.indexOf("\"", posImg);
            int pos2 = content.indexOf("\"", pos1 + 1);

            content.replace(pos1 + 1, pos2, url);

            posImg++;
        }
        mEditor.setHtml(content.toString());
    }

    // ---------------
    private void uploadImage(final Bitmap bmp) {
        final ProgressBar pbrUploadingImage = (ProgressBar) rootView.findViewById(R.id.pbr_uploading_image);
        pbrUploadingImage.setVisibility(View.VISIBLE);

        byte[] fileData = CommonVLs.getFileDataFromBitmap(bmp);
        String filename = "image_post.jpg";
        String fileType = "image/jpg";
        MultipartRequest request = new MultipartRequest(getContext(), Request.Method.POST,
                        AppConfig.URL_POST_IMG, fileData, filename, fileType);

        request.setListener(new MultipartRequest.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                Log.d(TAG, "response: " + response);
                Log.d(TAG, "msg: " + message);

                pbrUploadingImage.setVisibility(View.GONE);

                if (!error) {
                    String urlImg = response.getJSONObject("data").getString("url");
                    Log.i(TAG, "url = " + urlImg);
//                    Toast.makeText(getContext(), "Post xong", Toast.LENGTH_SHORT).show();
                    arrImageCloud.add(urlImg);
                } else {
                    Toast.makeText(getContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        request.sendRequest("update avatar");
    }

    // ---------------------------------------------------------------------------------------------

    public String getTitlePost() {
        try {// Case: edtTitlePost is NULL
            return "" + edtTitlePost.getText().toString();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getContentPost() {
        return "" + mEditor.getHtml();
    }

    public boolean checkFillContent() {
        String titlePost = getTitlePost();
        if (titlePost.isEmpty()) {
            edtTitlePost.requestFocus();
            YoYo.with(Techniques.Tada)
                    .duration(1000)
                    .playOn(rootView.findViewById(R.id.t_i_l_title_post));
            Toast.makeText(getContext(), "Bài viết không có tiêu đề!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (contentIsEmpty()) {
            mEditor.focusEditor();
            mEditor.requestFocus();
            YoYo.with(Techniques.Tada)
                    .duration(1000)
                    .playOn(mEditor);
            Toast.makeText(getContext(), "Bài viết không có nội dung!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean contentIsEmpty() {
        String contentPost = getContentPost();
        return contentPost.isEmpty() || contentPost.equals(HTML_PLACE_HOLDER);
    }

}
