package com.fries.edoo.activities;


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
import com.fries.edoo.R;
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.communication.RequestServer;
import com.fries.edoo.fragment.LopMonHocFragment;
import com.fries.edoo.fragment.ThoiKhoaBieuFragment;
import com.fries.edoo.helper.SQLiteHandler;
import com.fries.edoo.helper.PrefManager;
import com.fries.edoo.models.ItemLop;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private static final String URL_DOWNLOAD_APK = "http://uetf.me/";

    private static final int REQUEST_CODE_EDIT = 1234;

    private LopMonHocFragment lopMonHocFragment = new LopMonHocFragment();
    //    private LopKhoaHocFragment lopKhoaHocFragment = new LopKhoaHocFragment();
//    private NhomFragment nhomFragment = new NhomFragment();
    private ThoiKhoaBieuFragment thoiKhoaBieuFragment = new ThoiKhoaBieuFragment();

    private PrefManager session;
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //session
        session = new PrefManager(getApplicationContext());

        //lay co so du lieu
        sqlite = new SQLiteHandler(getApplicationContext());
//        checkLogDb();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);

        if (!session.isLoggedIn()) {
            logout();
            return;
        }

        initViews();

        //replace fragment moi
        showFragment(thoiKhoaBieuFragment);
        toolbar.setTitle("Thời khoá biểu");

        //start timeline activity if click noti
        Intent mIntent = getIntent();
        ItemLop itemLop = (ItemLop) mIntent.getSerializableExtra("item_class");
        if (itemLop != null) {
            goToTimeLine(itemLop, "");
        }

        // Register FCM token to server
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "FCM token: " + fcmToken);
        if (fcmToken == null || fcmToken.isEmpty()){
            sendRegistrationToServer(fcmToken);
        }
    }

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
//        Log.i(TAG, "update ava: " + user.get("avatar"));
        Picasso.with(this)
                .load(user.get("avatar")).fit()
                .placeholder(R.mipmap.ic_user)
                .error(R.mipmap.ic_user)
                .into(ivAva);
        Log.i(TAG, "update avatar + " + user.get("avatar"));
    }

    private void showFragment(Fragment lopFragment) {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
        getFragmentManager().beginTransaction().replace(R.id.container, lopFragment).commit();
    }

    private long prevTime = 0;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime-prevTime<=1500){
                super.onBackPressed();
            } else {
                Toast.makeText(this, getResources().getString(R.string.lert_press_back), Toast.LENGTH_SHORT).show();
            }
            prevTime = currentTime;
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
//            case R.id.nav_lopKhoaHoc:
//                switchToMenu(LOP_MENU_INT);
//                toolbar.setTitle("Lớp khoá học");
//                showFragment(lopKhoaHocFragment);
//                break;
//            case R.id.nav_feed:
//                switchToMenu(LOP_MENU_INT);
//                toolbar.setTitle("Bảng tin");
//                goToFeed();
//                break;
//            case R.id.nav_nhom:
//                switchToMenu(LOP_MENU_INT);
//                toolbar.setTitle("Nhóm");
//                showFragment(nhomFragment);
//                break;
//            case R.id.nav_setting:
//                switchToMenu(LOP_MENU_INT);
//                toolbar.setTitle("Cài đặt");
//                break;
            case R.id.nav_introduction:
                Intent intro = new Intent(this, IntroSliderActivity.class);
                startActivity(intro);
                break;
            case R.id.nav_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Cùng trải nghiệm môi trường học tập khác biệt với " + URL_DOWNLOAD_APK);
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
                Intent mIntent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivityForResult(mIntent, REQUEST_CODE_EDIT);
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
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
        requestServer.sendRequest("req_log_out");
        // xoa session
        session.setLogin(false);
        session.setIsSaveClass(false);

        // xoa user, classes
        sqlite.deleteUsers();
        sqlite.deleteClasses();

        // thoat ra man hinh dang nhap
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_EDIT:
                if (resultCode == RESULT_OK) {
                    updateAvatar();
//                    checkLogDb();
                }
                break;
        }
    }

//    private TimelineFragment timelineFragment;

    public void goToTimeLine(ItemLop itemLop, String keyLop) {
        //Lay thong tin cac bai dang tren server ve
        //goi timelinefragment
        //set data cho listview
//        Log.i(TAG, itemLop.getId());
//        Log.i(TAG, itemLop.getIdData());
        Intent mIntent = new Intent(this, TimelineActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("item_class", itemLop);

        mIntent.putExtras(b);

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(),
//                    R.anim.anim_enter_activity, R.anim.anim_exit_activity);
//            startActivity(mIntent, optionsCompat.toBundle());
//        } else
        startActivity(mIntent);

//        timelineFragment = new TimelineFragment();

//        Bundle b = new Bundle();
//        b.putString("idData", itemLop.getIdData());
//        b.putString("keyLop", keyLop);
//        timelineFragment.setArguments(b);
//        Toast.makeText(getApplicationContext(), "Id: " + idData, Toast.LENGTH_LONG).show();

//        getFragmentManager().beginTransaction().
//                replace(R.id.container, timelineFragment).addToBackStack(null).commit();

        //Chinh sua toolbar

//        toolbar.setTitle(itemLop.getTen());
//        switchToMenu(TIMELINE_MENU_INT);
    }

//    private FeedFragment feedFragment;

//    public void goToFeed() {
//        feedFragment = new FeedFragment();
//
//        HashMap<String, String> user = sqlite.getUserDetails();
//        String uid = user.get(SQLiteHandler.KEY_UID);
//
//        Bundle b = new Bundle();
//        b.putString("uid", uid);
////        b.putString("keyLop", keyLop);
//        feedFragment.setArguments(b);
////        Toast.makeText(getApplicationContext(), "Id: " + idData, Toast.LENGTH_LONG).show();
//
//        for (int i = 0; i < getFragmentManager().getBackStackEntryCount(); i++) {
//            getFragmentManager().popBackStack();
//            Log.i(TAG, "back stack count: " + getFragmentManager().getBackStackEntryCount());
//        }
//
//        getFragmentManager().beginTransaction().
//                replace(R.id.container, feedFragment).commit();
//
//        //Chinh sua toolbar
//
////        toolbar.setTitle(itemLop.getTen());
//        //Test
//        switchToMenu(LOP_MENU_INT);
//    }

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

    private void sendRegistrationToServer(String token) {
        String url = AppConfig.URL_REGISTER_FCM;
        JSONObject params = new JSONObject();
        try {
            params.put("type", "android");
            params.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestServer requestServer = new RequestServer(this, Request.Method.POST, url,  params);
        requestServer.sendRequest("register FCM");
    }

}
