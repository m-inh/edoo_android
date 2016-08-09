package com.fries.edoo;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.fries.edoo.adapter.TimeLineAdapter;
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.communication.RequestServer;
import com.fries.edoo.fragment.FeedFragment;
import com.fries.edoo.fragment.LopKhoaHocFragment;
import com.fries.edoo.fragment.LopMonHocFragment;
import com.fries.edoo.fragment.NhomFragment;
import com.fries.edoo.fragment.ThoiKhoaBieuFragment;
import com.fries.edoo.fragment.TimelineFragment;
import com.fries.edoo.helper.SQLiteHandler;
import com.fries.edoo.helper.SessionManager;
import com.fries.edoo.models.ItemLop;
import com.fries.edoo.models.ItemTimeLine;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private static final String URL_DOWNLOAD_APK = "https://www.dropbox.com/sh/kapr5q60x80c8ua/AAAB3RH1FS9SwPYnYOuYHJJSa?dl=0";

    private static final int REQUEST_CODE_EDIT = 1234;
    public static final int REQUEST_CODE_ITEMWRITEPOSTHOLDER = 9000;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9200;
    private static final int REQUEST_CODE_POST_WRITER = 1202;
    private static final int REQUEST_CODE_POST_DETAIL = 1201;

    private LopMonHocFragment lopMonHocFragment = new LopMonHocFragment();
    private LopKhoaHocFragment lopKhoaHocFragment = new LopKhoaHocFragment();
    private NhomFragment nhomFragment = new NhomFragment();
    private ThoiKhoaBieuFragment thoiKhoaBieuFragment = new ThoiKhoaBieuFragment();

    private SessionManager session;
    private SQLiteHandler sqlite;

    private TextView tvName, tvEmail;
    private CircleImageView ivAva;
    private View header;
    private Toolbar toolbar;

    public static final String PATH_TO_DIR_SAVING_IMAGE = Environment.getExternalStorageDirectory() + File.separator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
//        toolbar.setNavigationContentDescription("BackPressed");

//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //session
        session = new SessionManager(getApplicationContext());

        //lay co so du lieu
        sqlite = new SQLiteHandler(getApplicationContext());
        checkLogDb();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);

        if (!session.isLoggedIn()) {
            logout();
        }

        initViews();

//        if (getFragmentManager().getBackStackEntryCount() > 0) {
//            getFragmentManager().popBackStack();
//        }

        //replace fragment moi
        showFragment(thoiKhoaBieuFragment);
        toolbar.setTitle("Thời khoá biểu");

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initViews() {
        tvName = (TextView) header.findViewById(R.id.tv_name);
        tvEmail = (TextView) header.findViewById(R.id.tv_email);
        ivAva = (CircleImageView) header.findViewById(R.id.iv_avatar);

        ivAva.setFillColor(Color.WHITE);
        updateAvatar();
//        String pathSaveImage = PATH_TO_DIR_SAVING_IMAGE + sqlite.getUserDetails().get("mssv") + ".jpg";
//        UserPicture.setCurrentAvatar(ivAva, pathSaveImage);

        HashMap<String, String> user = sqlite.getUserDetails();
        String type = user.get(SQLiteHandler.KEY_TYPE);
        if (type.equalsIgnoreCase("student")) {
            type = "Sinh viên";
        } else if (type.equalsIgnoreCase("teacher")) {
            type = "Giảng viên";
        }
        tvName.setText(user.get(SQLiteHandler.KEY_NAME) + " (" + type + ")");
        tvEmail.setText(user.get(SQLiteHandler.KEY_EMAIL));
    }

    public void updateAvatar() {
        HashMap<String, String> user = sqlite.getUserDetails();
        Picasso.with(this).invalidate(user.get("avatar"));
        Picasso.with(this).load(user.get("avatar")).placeholder(R.mipmap.ic_user).error(R.mipmap.ic_user)
                .into(ivAva);

//        Picasso.with(this).load("http://myclass.tutran.net/v1/avatar/13020285")
//                .into(ivAva);
    }

    private void showFragment(Fragment lopFragment) {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
        getFragmentManager().beginTransaction().replace(R.id.container, lopFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            if (lopKhoaHocFragment.isRefreshing()
                    || lopMonHocFragment.isRefreshing() || timelineFragment.isRefreshing()) {
                Log.i(TAG, "isRefreshing");
                return;
            }
            Log.i(TAG, "onBackPressed");
            getFragmentManager().popBackStack();

            //switch toobal to lop menu
            switchToMenu(LOP_MENU_INT);

            if (timelineFragment != null) {
                switch (timelineFragment.getKeyLopType()) {
                    case LopMonHocFragment.KEY_LOP_MON_HOC:
                        toolbar.setTitle("Lớp môn học");
                        break;
                    case LopKhoaHocFragment.KEY_LOP_KHOA_HOC:
                        toolbar.setTitle("Lớp khoá học");
                        break;
                    case NhomFragment.KEY_NHOM:
                        toolbar.setTitle("Nhom");
                        break;
                    default:
                        break;
                }
            }
            return;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        switch (currentMenuInt) {
            case LOP_MENU_INT:
                getMenuInflater().inflate(R.menu.main, menu);
                break;
            case TIMELINE_MENU_INT:
                getMenuInflater().inflate(R.menu.timeline_menu, menu);
                menu.findItem(R.id.item_post).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                menu.findItem(R.id.item_post).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
                break;
            case POST_DETAIL_MENU_INT:
                getMenuInflater().inflate(R.menu.writepost_menu, menu);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.item_post:
                Intent mIntent = new Intent();
                mIntent.setClass(MainActivity.this, PostWriterActivity.class);
                mIntent.putExtra("idLop", timelineFragment.getIdLop());
                mIntent.putExtra("keyLopType", timelineFragment.getKeyLopType());
                startActivityForResult(mIntent, REQUEST_CODE_POST_WRITER);
//                Toast.makeText(this, "Send to all", Toast.LENGTH_LONG).show();
                break;
            case R.id.item_locbaidangchuatraloi:
                Log.i(TAG, "loc bai dang chua tl");
                if (timelineFragment != null) {
                    timelineFragment.locTheoBaiDang(TimeLineAdapter.BAI_DANG_LOC_THEO_CHUA_TRA_LOI);
                }
                break;
            case R.id.item_locbaidanggiaovien:
                Log.i(TAG, "loc bai dang chua tl");
                if (timelineFragment != null) {
                    timelineFragment.locTheoBaiDang(TimeLineAdapter.BAI_DANG_LOC_THEO_GIAO_VIEN);
                }
                break;
            case R.id.item_tatcabaidang:
                Log.i(TAG, "tat ca bai dang");
                if (timelineFragment != null) {
                    timelineFragment.locTheoBaiDang(TimeLineAdapter.BAI_DANG_BINH_THUONG);
                }
                break;
            case R.id.action_sendAll:
//                Toast.makeText(this, "Send to all", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static final int LOP_MENU_INT = 1;
    public static final int TIMELINE_MENU_INT = 2;
    public static final int POST_DETAIL_MENU_INT = 3;

    private static int currentMenuInt = LOP_MENU_INT;

    private void switchToMenu(int menuInt) {
        currentMenuInt = menuInt;

        supportInvalidateOptionsMenu();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_lopMonHoc:
                switchToMenu(LOP_MENU_INT);
                toolbar.setTitle("Lớp môn học");
                showFragment(lopMonHocFragment);
                break;
            case R.id.nav_lopKhoaHoc:
                switchToMenu(LOP_MENU_INT);
                toolbar.setTitle("Lớp khoá học");
                showFragment(lopKhoaHocFragment);
                break;
            case R.id.nav_feed:
                switchToMenu(LOP_MENU_INT);
                toolbar.setTitle("Bảng tin");
                goToFeed();
                break;
            case R.id.nav_nhom:
                switchToMenu(LOP_MENU_INT);
                toolbar.setTitle("Nhóm");
                showFragment(nhomFragment);
                break;
            case R.id.nav_setting:
                switchToMenu(LOP_MENU_INT);
                toolbar.setTitle("Cài đặt");
                break;
            case R.id.nav_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Ứng dụng \"Lớp tôi\" thật tuyệt vời!\n" + URL_DOWNLOAD_APK);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
//                toolbar.setTitle("Chia sẻ");
                break;
            case R.id.nav_thoikhoabieu:
                //replace fragment TKB
//                getFragmentManager().beginTransaction().replace(R.id.container, thoiKhoaBieuFragment).commit();
                showFragment(thoiKhoaBieuFragment);
                switchToMenu(LOP_MENU_INT);
                toolbar.setTitle("Thời khoá biểu");
                break;
            case R.id.nav_updateAccount:
                startEditActivity();
                break;
            case R.id.nav_logout:
                logout();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        RequestServer requestServer = new RequestServer(getApplicationContext(), Request.Method.GET, AppConfig.URL_LOGOUT);
        requestServer.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                if (!error) {
                    // xoa session
                    session.setLogin(false);

                    // xoa user
                    sqlite.deleteUsers();

                    // thoat ra man hinh dang nhap
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        requestServer.sendRequest("req_log_out");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_EDIT:
                if (resultCode == RESULT_OK) {
                    updateAvatar();
                    checkLogDb();
                }
                break;
            case REQUEST_CODE_POST_WRITER:
                if (resultCode == RESULT_OK) {
                    timelineFragment.onRefresh();
                }
                break;
            case REQUEST_CODE_POST_DETAIL:
                if (resultCode == RESULT_OK) {
                    if (timelineFragment != null && timelineFragment.isAdded()) {
                        timelineFragment.onRefresh();
                    }

                    if (feedFragment != null && feedFragment.isAdded()) {
                        feedFragment.onRefresh();
                    }
                }
                break;
        }
    }

    private TimelineFragment timelineFragment;

    public void goToTimeLine(ItemLop itemLop, String keyLop) {
        //Lay thong tin cac bai dang tren server ve
        //goi timelinefragment
        //set data cho listview
        timelineFragment = new TimelineFragment();

        Bundle b = new Bundle();
        b.putString("idData", itemLop.getIdData());
        b.putString("keyLop", keyLop);
        timelineFragment.setArguments(b);
//        Toast.makeText(getApplicationContext(), "Id: " + idData, Toast.LENGTH_LONG).show();

        getFragmentManager().beginTransaction().
                replace(R.id.container, timelineFragment).addToBackStack(null).commit();

        //Chinh sua toolbar

        toolbar.setTitle(itemLop.getTen());
        switchToMenu(TIMELINE_MENU_INT);
    }

    private FeedFragment feedFragment;

    public void goToFeed() {
        feedFragment = new FeedFragment();

        HashMap<String, String> user = sqlite.getUserDetails();
        String uid = user.get(SQLiteHandler.KEY_UID);

        Bundle b = new Bundle();
        b.putString("uid", uid);
//        b.putString("keyLop", keyLop);
        feedFragment.setArguments(b);
//        Toast.makeText(getApplicationContext(), "Id: " + idData, Toast.LENGTH_LONG).show();

        for (int i = 0; i < getFragmentManager().getBackStackEntryCount(); i++) {
            getFragmentManager().popBackStack();
            Log.i(TAG, "back stack count: " + getFragmentManager().getBackStackEntryCount());
        }

        getFragmentManager().beginTransaction().
                replace(R.id.container, feedFragment).commit();

        //Chinh sua toolbar

//        toolbar.setTitle(itemLop.getTen());
        //Test
        switchToMenu(LOP_MENU_INT);
    }

    private void startEditActivity() {
        Intent mIntent = new Intent(MainActivity.this, EditProfileActivity.class);
        HashMap<String, String> user = sqlite.getUserDetails();
        mIntent.putExtra("name", user.get("name"));
        mIntent.putExtra("email", user.get("email"));
        mIntent.putExtra("lop", user.get("lop"));
        mIntent.putExtra("mssv", user.get("mssv"));
        mIntent.putExtra("type", user.get("type"));

        startActivityForResult(mIntent, REQUEST_CODE_EDIT);
    }

    public void startPostWriterActivity(String idData, String keyLop) {
        Intent intent = new Intent();
        intent.putExtra("idData", idData);
        intent.putExtra("keyLop", keyLop);
        intent.setClass(MainActivity.this, PostWriterActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ITEMWRITEPOSTHOLDER);
    }

    public void startPostDetailActivity(ItemTimeLine itemTimeLine) {
        Intent mIntent = new Intent();
        mIntent.putExtra("timelineItem", itemTimeLine);
        mIntent.setClass(MainActivity.this, PostDetailActivity.class);
        startActivityForResult(mIntent, REQUEST_CODE_POST_DETAIL);
    }

    private void checkLogDb() {
        HashMap<String, String> user = sqlite.getUserDetails();
        String name = user.get("name");
        String email = user.get("email");
        String uid = user.get("uid");
        String createAt = user.get("created_at");
        String mssv = user.get("mssv");
        String lop = user.get("lop");
        String type = user.get("type");
        String ava = user.get("avatar");

        Log.i(TAG, "check db name: " + name);
        Log.i(TAG, "check db email: " + email);
        Log.i(TAG, "check db uid: " + uid);
        Log.i(TAG, "check db create: " + createAt);
        Log.i(TAG, "check db mssv: " + mssv);
        Log.i(TAG, "check db lop: " + lop);
        Log.i(TAG, "check db type: " + type);
        Log.i(TAG, "check db ava: " + ava);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
//    }
//
//    @Override
//    protected void onPause() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
//        super.onPause();
//    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}
