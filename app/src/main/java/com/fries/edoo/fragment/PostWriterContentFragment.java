package com.fries.edoo.fragment;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Toast;

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
    private static final int REQUEST_CODE_IMAGE = 34242;

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

    private void initViews() {
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

        rootView.findViewById(R.id.editor_action_undo).setOnClickListener(clickToolEditor);
        rootView.findViewById(R.id.editor_action_bold).setOnClickListener(clickToolEditor);
        rootView.findViewById(R.id.editor_action_italic).setOnClickListener(clickToolEditor);
        rootView.findViewById(R.id.editor_action_underline).setOnClickListener(clickToolEditor);
        rootView.findViewById(R.id.editor_action_text_size).setOnClickListener(clickToolEditor);
        rootView.findViewById(R.id.editor_action_bullets).setOnClickListener(clickToolEditor);
        rootView.findViewById(R.id.editor_action_insert_image).setOnClickListener(clickToolEditor);
        rootView.findViewById(R.id.editor_action_insert_link).setOnClickListener(clickToolEditor);


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

                    String html = text.substring(0, posTagImg) + resizeImage() + text.substring(posTagImg, text.length()) + "<br>";
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
//                    mEditor.insertImage("http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG", "dachshund");
                    pickImageFromMemory();
                    break;
                case R.id.editor_action_insert_link:
                    showDialogInsertLink();
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_IMAGE:
                Log.i(TAG, data.getData().toString());
                Bitmap bmp = getBitmap(data.getData());
                uploadImage(bmp);
                mEditor.insertImage(data.getData().toString(), "alt_image");
                break;
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


    private void pickImageFromMemory() {
        Intent iImage = (new Intent("android.intent.action.GET_CONTENT")).setType("image/*");
        preHTMLEditor = mEditor.getHtml() + "";
        newImageInserted = true;
        startActivityForResult(iImage, REQUEST_CODE_IMAGE);
    }

    private String resizeImage() {
        int screenWidth = mEditor.getWidth() - 2 * mEditor.getPaddingLeft() - 2 * mEditor.getPaddingRight();
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

    private Bitmap getBitmap(Uri uri) {
        File file = new File(SelectedFilePath.getPath(getContext(), uri));
//        Log.i(TAG, "path = " + file.getAbsolutePath());
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        imgWidth = bitmap.getWidth();
        imgHeight = bitmap.getHeight();
        return bitmap;
    }

    public void replaceUrlImage() {
        StringBuilder content = new StringBuilder(getContentPost());
        int posImg = 0;
        for (String url : arrImageCloud) {
            posImg = content.indexOf("src", posImg);

            int pos1 = content.indexOf("\"", posImg);
            int pos2 = content.indexOf("\"", pos1 + 1);

            content.replace(pos1+1, pos2, url);

            posImg++;
        }
        mEditor.setHtml("  ");
        Log.i(TAG, "Html prepare post = " + content.toString());
        mEditor.setHtml(content.toString());
    }

    // ---------------
    private void uploadImage(final Bitmap bmp) {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getContext(), "Uploading...", "Please wait...", false, false);

        byte[] fileData = CommonVLs.getFileDataFromBitmap(bmp);
        String filename = "avatar.jpg";
        String fileType = "image/jpg";
        MultipartRequest request =
                new MultipartRequest(getContext(), Request.Method.POST,
                        AppConfig.URL_POST_IMG, fileData, filename, fileType);

        request.setListener(new MultipartRequest.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                Log.d(TAG, "response: " + response);
                Log.d(TAG, "msg: " + message);
                //Disimissing the progress dialog
                loading.dismiss();

                if (!error) {
                    String urlAva = response.getJSONObject("data").getString("url");
                    Log.i(TAG, "url = " + urlAva);
                    Toast.makeText(getContext(), "Post xong", Toast.LENGTH_SHORT).show();

//                    mEditor.insertImage(urlAva, "image");

                    arrImageCloud.add(urlAva);


                    //Send url avatar to handler
//                    Message msg = new Message();
//                    Bundle b = new Bundle();
//                    b.putString("avatar", urlAva);
//                    msg.setData(b);
//                    msg.setTarget(mHandler);
//                    msg.sendToTarget();
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

    // ---------------------------------------------------------------------------------------------
    private static class SelectedFilePath {
        /**
         * Get a file path from a Uri. This will get the the path for Storage Access
         * Framework Documents, as well as the _data field for the MediaStore and
         * other file-based ContentProviders.
         *
         * @param context The context.
         * @param uri     The Uri to query.
         * @author paulburke
         */
        @TargetApi(Build.VERSION_CODES.KITKAT)
        public static String getPath(final Context context, final Uri uri) {

            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }

            return null;
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context       The context.
         * @param uri           The Uri to query.
         * @param selection     (Optional) Filter used in the query.
         * @param selectionArgs (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */
        public static String getDataColumn(Context context, Uri uri, String selection,
                                           String[] selectionArgs) {

            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {
                    column
            };

            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                        null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int column_index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(column_index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        }


        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        public static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        public static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        public static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }
    }
}
