package com.mysports.basketbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.IntegerRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.melnykov.fab.FloatingActionButton;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends AppCompatActivity {
    static String Id = "";
    static String yourTeamStatus = "reset";

    static String realTime = "";
    static String Team1 = "";
    static int MaxNum_out;
    static int in_MinNum;
    static String Token = "";
    static String MyTeam = "";
    View rootView;
    static String Alarm = "";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout tabLayout;
    //게임 관련 선언
    static LinearLayout League_Layout_1, League_Layout_2;
    static String[][] parsedData_BasicSetting;
    static String[][] parsedData_gameStatus;
    static String[][] parsedData_gameRefuse;
    static String[][] parsedData_gameScoreInfo;
    static String[][] parsedData_gameScoreInfo_succed;
    static String[][] parsedData_Profile;
    static int allowtime = 0;
    static String HomeTeam, AwayTeam;
    ////
    static ImageView Profile_ImageVIew_Profile;
    static ListView NewsFeed_List;
    static ListView Match_In_CustomList;
    static LinearLayout Match_Layout_Tab;
    static LinearLayout Match_Layout_Out_Address;
    static LinearLayout Match_Layout_In_Address;
    static String out_in_choice = "out";
    static View layout;
    static View layout_GameAdd;
    static View layout_GameScoreInfo;
    ///////탭 아이콘 불러오기/////////////////
    private int[] tabIcons_match = {
            R.drawable.basketball_clicked,
            R.drawable.trophy,
            R.drawable.document,
            R.drawable.user
    };
    private int[] tabIcons = {
            R.drawable.basketball,
            R.drawable.trophy,
            R.drawable.document,
            R.drawable.user
    };
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    GestureDetector gd = new GestureDetector(new GestureDetector.SimpleOnGestureListener());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupTabIcons_match();
////// 리그 탭 다이얼로그 사용을 위한 커스텀 다이얼로그 선언
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.layout_customdialog_teamchoice, (ViewGroup) findViewById(R.id.Layout_CustomDialog_teamChoice_Root));
        //////    ////// 리그 탭 다이얼로그_시합종료 사용을 위한 커스텀 다이얼로그 선언
        layout_GameAdd = inflater.inflate(R.layout.layout_customdialog_gameadd, (ViewGroup) findViewById(R.id.Layout_CustomDialog_GameAdd_Root));
        ///스코어 점수 확인을 위한 커스텀 다이얼로그 선언
        layout_GameScoreInfo = inflater.inflate(R.layout.layout_customdialog_gamescoreinfo, (ViewGroup) findViewById(R.id.Layout_CustomDialog_GameScoreInfo_Root));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int a = tab.getPosition();
                //매칭 탭 클릭시
                if (a == 0) {
                    tab.setIcon(R.drawable.basketball_clicked);
                    mViewPager.setCurrentItem(tab.getPosition());
                } else if (a == 1) {
                    tab.setIcon(R.drawable.trophy_clicked);
                    mViewPager.setCurrentItem(tab.getPosition());
                } else if (a == 2) {
                    tab.setIcon(R.drawable.document_clicked);
                    mViewPager.setCurrentItem(tab.getPosition());
                } else if (a == 3) {
                    tab.setIcon(R.drawable.user_clicked);
                    mViewPager.setCurrentItem(tab.getPosition());

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                setupTabIcons();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                if (out_in_choice.equals("out")) {
                    NewsFeed_List.setSelectionFromTop(0, 0);
                    new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                        @Override
                        public void run() {
                            Match_Layout_Tab.setVisibility(View.VISIBLE);
                            Match_Layout_Out_Address.setVisibility(View.VISIBLE);
                        }
                    }, 500);
                } else if (out_in_choice.equals("in")) {
                    Match_In_CustomList.setSelectionFromTop(0, 0);
                    new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                        @Override
                        public void run() {
                            Match_Layout_Tab.setVisibility(View.VISIBLE);
                            Match_Layout_In_Address.setVisibility(View.VISIBLE);
                        }
                    }, 500);
                }
            }
        });
        SharedPreferences prefs_user = getSharedPreferences("basketball_user", MODE_PRIVATE);

        Intent intent1 = getIntent();
        Id = intent1.getStringExtra("Id");
      /*  if(intent1.hasExtra("approach")){
            approach = intent1.getStringExtra("approach");
            approach_sendTeam = intent1.getStringExtra("sendTeam");
        }*/
        realTime = new SimpleDateFormat("HHmm").format(new java.sql.Date(System.currentTimeMillis()));

        //gcm 데이터 등록
        Token = FirebaseInstanceId.getInstance().getToken();
        Log.i("Id", Id);
        Log.i("token", Token);
        String result = "";
        try {
            HttpClient client = new DefaultHttpClient();
            String postURL = "http://210.122.7.195:8080/Web_basket/Gcm_IdAdd_Alarm.jsp";
            HttpPost post = new HttpPost(postURL);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Id", Id));
            params.add(new BasicNameValuePair("Token", Token));

            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            post.setEntity(ent);

            HttpResponse response = client.execute(post);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

            String line = null;
            while ((line = bufreader.readLine()) != null) {
                result += line;
            }
            parsedData_BasicSetting = jsonParserList_BasicSetting(result);
            Log.i("결과", result);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("실패", "실패");
        }
        ///////////////////////////////////////////////////////////////////
        String result_profile = "";
        try {
            HttpClient client = new DefaultHttpClient();
            String postURL = "http://210.122.7.195:8080/Web_basket/Profile.jsp";
            HttpPost post = new HttpPost(postURL);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Id", Id));

            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            post.setEntity(ent);

            HttpResponse response = client.execute(post);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

            String line = null;
            while ((line = bufreader.readLine()) != null) {
                result_profile += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        parsedData_Profile = jsonParserList_UserInfo(result_profile);
        MyTeam = parsedData_Profile[0][6];
        /////////////////////////////////////////
        //일반 접근인 경우
        ///일반적 접근일 경우 처리
        ///jsp문 돌려서 어떤 접근인지 check
        String result_GameStatus = "";
        try {
            HttpClient client = new DefaultHttpClient();
            String postURL = "http://210.122.7.195:8080/Web_basket/GameStatus.jsp";
            HttpPost post = new HttpPost(postURL);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("MyTeam", MyTeam));

            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            post.setEntity(ent);

            HttpResponse response = client.execute(post);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

            String line = null;
            while ((line = bufreader.readLine()) != null) {
                result_GameStatus += line;
            }
            parsedData_gameStatus = jsonParserList_GameStatus(result_GameStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HomeTeam = parsedData_gameStatus[0][1];
        AwayTeam = parsedData_gameStatus[0][2];
        //시합 신청한 팀으로 접근하는 경우
        if (HomeTeam.equals(MyTeam)) {
            if (parsedData_gameStatus[0][0].equals("NotGame")) {

            } else if (parsedData_gameStatus[0][0].equals(".")) {
                yourTeamStatus = "suggest";

                allowtime = Integer.parseInt(realTime) - Integer.parseInt(parsedData_gameStatus[0][5]);
                Log.i("realTime", Integer.toString(allowtime));
            } else if (parsedData_gameStatus[0][0].equals("allow")) {
                yourTeamStatus = "allow";
            } else if (parsedData_gameStatus[0][0].equals("ing")) {
                yourTeamStatus = "ing";
            }
            else if (parsedData_gameStatus[0][0].equals("ScoreCheck")) {
                yourTeamStatus = "ScoreCheck";
            }
        }
        //시합 신청받은 팀으로 접근하는 경우
        else {
            if (parsedData_gameStatus[0][0].equals(".")) {
                final MaterialDialog DropOutDialog = new MaterialDialog(MainActivity.this);
                DropOutDialog
                        .setTitle("시합신청")
                        .setMessage(HomeTeam + "팀에서 시합신청하였습니다.")
                        .setNegativeButton("취소", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String result = "";
                                try {
                                    HttpClient client = new DefaultHttpClient();
                                    String postURL = "http://210.122.7.195:8080/Web_basket/GameRefuse.jsp";
                                    HttpPost post = new HttpPost(postURL);
                                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                                    params.add(new BasicNameValuePair("SendTeam", HomeTeam));
                                    UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                    post.setEntity(ent);
                                    HttpResponse response = client.execute(post);
                                    BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                    String line = null;
                                    while ((line = bufreader.readLine()) != null) {
                                        result += line;
                                    }
                                    parsedData_gameRefuse = jsonParserList_gameDelete(result);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (parsedData_gameRefuse[0][0].equals("succed")) {
                                    yourTeamStatus = "reset";
                                    DropOutDialog.dismiss();
                                } else {
                                }

                            }
                        })
                        .setPositiveButton("수락", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String result = "";
                                try {
                                    HttpClient client = new DefaultHttpClient();
                                    String postURL = "http://210.122.7.195:8080/Web_basket/GameSucced.jsp";
                                    HttpPost post = new HttpPost(postURL);
                                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                                    params.add(new BasicNameValuePair("SendTeam", HomeTeam));
                                    UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                    post.setEntity(ent);
                                    HttpResponse response = client.execute(post);
                                    BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                    String line = null;
                                    while ((line = bufreader.readLine()) != null) {
                                        result += line;
                                    }
                                    parsedData_gameRefuse = jsonParserList_gameDelete(result);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (parsedData_gameRefuse[0][0].equals("succed")) {
                                    yourTeamStatus = "allow_away";
                                    DropOutDialog.dismiss();
                                    mViewPager.setCurrentItem(2);
                                    League_Layout_1.setVisibility(View.GONE);
                                    League_Layout_2.setVisibility(View.VISIBLE);
                                } else {

                                }
                                DropOutDialog.dismiss();
                            }
                        });
                DropOutDialog.show();
            }
            else if (parsedData_gameStatus[0][0].equals("allow")) {
                yourTeamStatus = "allow_away";
            }
            else if (parsedData_gameStatus[0][0].equals("ing")) {
                yourTeamStatus = "ing";
            }
            else if (parsedData_gameStatus[0][0].equals("ScoreCheck")) {
                String result_ScoreCheck = "";
                try {
                    HttpClient client = new DefaultHttpClient();
                    String postURL = "http://210.122.7.195:8080/Web_basket/GameScoreInfo.jsp";
                    HttpPost post = new HttpPost(postURL);
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("SendTeam", parsedData_Profile[0][6]));
                    UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                    post.setEntity(ent);
                    HttpResponse response = client.execute(post);
                    BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                    String line = null;
                    while ((line = bufreader.readLine()) != null) {
                        result_ScoreCheck += line;
                    }
                    parsedData_gameScoreInfo = jsonParserList_gameScoreInfo(result_ScoreCheck);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final String HomeTeam = parsedData_gameScoreInfo[0][0];
                final String AwayTeam = parsedData_gameScoreInfo[0][1];
                final String HomeScore = parsedData_gameScoreInfo[0][2];
                final String AwayScore = parsedData_gameScoreInfo[0][3];

                final Button Layout_CustomDialog_GameScoreInfo_Button_HomeTeam = (Button)layout_GameScoreInfo.findViewById(R.id.Layout_CustomDialog_GameScoreInfo_Button_HomeTeam);
                final TextView Layout_CustomDialog_GameScoreInfo_TextView_HomeTeam = (TextView)layout_GameScoreInfo.findViewById(R.id.Layout_CustomDialog_GameScoreInfo_TextView_HomeTeam);
                final Button Layout_CustomDialog_GameScoreInfo_Button_AwayTeam = (Button)layout_GameScoreInfo.findViewById(R.id.Layout_CustomDialog_GameScoreInfo_Button_AwayTeam);
                final TextView Layout_CustomDialog_GameScoreInfo_TextView_AwayTeam = (TextView)layout_GameScoreInfo.findViewById(R.id.Layout_CustomDialog_GameScoreInfo_TextView_AwayTeam);

                Layout_CustomDialog_GameScoreInfo_Button_HomeTeam.setText(HomeTeam);
                Layout_CustomDialog_GameScoreInfo_TextView_HomeTeam.setText(HomeScore);
                Layout_CustomDialog_GameScoreInfo_Button_AwayTeam.setText(AwayTeam);
                Layout_CustomDialog_GameScoreInfo_TextView_AwayTeam.setText(AwayScore);

                final MaterialDialog DropOutDialog = new MaterialDialog(MainActivity.this);
                DropOutDialog
                        .setTitle("시합점수 확인")
                        .setView(layout_GameScoreInfo)
                        .setNegativeButton("취소", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String result_ScoreCheck_refuse = "";
                                String Orderteam="";
                                try {
                                    if(HomeTeam.equals(MyTeam)){
                                        Orderteam = AwayTeam;
                                    }
                                    if(AwayTeam.equals(MyTeam)){
                                        Orderteam = HomeTeam;
                                    }
                                    HttpClient client = new DefaultHttpClient();
                                    String postURL = "http://210.122.7.195:8080/Web_basket/GameScoreInfoRefuse.jsp";
                                    HttpPost post = new HttpPost(postURL);
                                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                                    params.add(new BasicNameValuePair("Myteam", parsedData_Profile[0][6]));
                                    params.add(new BasicNameValuePair("Orderteam", Orderteam));
                                    UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                    post.setEntity(ent);
                                    HttpResponse response = client.execute(post);
                                    BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                    String line = null;
                                    while ((line = bufreader.readLine()) != null) {
                                        result_ScoreCheck_refuse += line;
                                    }
                                    parsedData_gameScoreInfo_succed = jsonParserList_BasicSetting(result_ScoreCheck_refuse);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                DropOutDialog.dismiss();
                            }
                        })
                        .setPositiveButton("확인", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String result_ScoreCheck_agree = "";
                                try {
                                    HttpClient client = new DefaultHttpClient();
                                    String postURL = "http://210.122.7.195:8080/Web_basket/GameScoreInfoAgree.jsp";
                                    HttpPost post = new HttpPost(postURL);
                                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                                    params.add(new BasicNameValuePair("hometeamName", HomeTeam));
                                    params.add(new BasicNameValuePair("awayteamName", AwayTeam));
                                    params.add(new BasicNameValuePair("hometeamScore", HomeScore));
                                    params.add(new BasicNameValuePair("awayteamScore", AwayScore));
                                    params.add(new BasicNameValuePair("myteam", parsedData_Profile[0][6]));
                                    UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                    post.setEntity(ent);
                                    HttpResponse response = client.execute(post);
                                    BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                    String line = null;
                                    while ((line = bufreader.readLine()) != null) {
                                        result_ScoreCheck_agree += line;
                                    }
                                    parsedData_gameScoreInfo_succed = jsonParserList_BasicSetting(result_ScoreCheck_agree);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                DropOutDialog.dismiss();
                            }
                        }).show();
            }
        }
    }

    ///////game상태 접근법 파서
    /////프로필 탭 사용자정보를 파싱합니다.//////////////////////////////////////////////////////////
    public String[][] jsonParserList_GameStatus(String pRecvServerPage) {
        Log.i("서버에서 받은 전체 내용", pRecvServerPage);
        try {
            JSONObject json = new JSONObject(pRecvServerPage);
            JSONArray jArr = json.getJSONArray("List");

            String[] jsonName = {"msg1", "msg2", "msg3", "msg4", "msg5", "msg6"};
            String[][] parseredData = new String[jArr.length()][jsonName.length];
            for (int i = 0; i < jArr.length(); i++) {
                json = jArr.getJSONObject(i);
                for (int j = 0; j < jsonName.length; j++) {
                    parseredData[i][j] = json.getString(jsonName[j]);
                }
            }
            return parseredData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /////프로필 탭 사용자정보를 파싱합니다.//////////////////////////////////////////////////////////
    public String[][] jsonParserList_UserInfo(String pRecvServerPage) {
        Log.i("서버에서 받은 전체 내용", pRecvServerPage);
        try {
            JSONObject json = new JSONObject(pRecvServerPage);
            JSONArray jArr = json.getJSONArray("List");

            String[] jsonName = {"msg1", "msg2", "msg3", "msg4", "msg5", "msg6", "msg7", "msg8"};
            String[][] parseredData = new String[jArr.length()][jsonName.length];
            for (int i = 0; i < jArr.length(); i++) {
                json = jArr.getJSONObject(i);
                for (int j = 0; j < jsonName.length; j++) {
                    parseredData[i][j] = json.getString(jsonName[j]);
                }
            }
            return parseredData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String[][] jsonParserList_gameDelete(String pRecvServerPage) {
        Log.i("서버에서 받은 전체 내용", pRecvServerPage);
        try {
            JSONObject json = new JSONObject(pRecvServerPage);
            JSONArray jArr = json.getJSONArray("List");

            String[] jsonName = {"msg1"};
            String[][] parseredData = new String[jArr.length()][jsonName.length];
            for (int i = 0; i < jArr.length(); i++) {
                json = jArr.getJSONObject(i);
                for (int j = 0; j < jsonName.length; j++) {
                    parseredData[i][j] = json.getString(jsonName[j]);
                }
            }
            return parseredData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String[][] jsonParserList_BasicSetting(String pRecvServerPage) {
        Log.i("서버에서 받은 전체 내용", pRecvServerPage);
        try {
            JSONObject json = new JSONObject(pRecvServerPage);
            JSONArray jArr = json.getJSONArray("List");

            String[] jsonName = {"msg1"};
            String[][] parseredData = new String[jArr.length()][jsonName.length];
            for (int i = 0; i < jArr.length(); i++) {
                json = jArr.getJSONObject(i);
                for (int j = 0; j < jsonName.length; j++) {
                    parseredData[i][j] = json.getString(jsonName[j]);
                }
            }
            return parseredData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String[][] jsonParserList_gameScoreInfo(String pRecvServerPage) {
        Log.i("서버에서 받은 전체 내용", pRecvServerPage);
        try {
            JSONObject json = new JSONObject(pRecvServerPage);
            JSONArray jArr = json.getJSONArray("List");

            String[] jsonName = {"msg1", "msg2", "msg3", "msg4"};
            String[][] parseredData = new String[jArr.length()][jsonName.length];
            for (int i = 0; i < jArr.length(); i++) {
                json = jArr.getJSONObject(i);
                for (int j = 0; j < jsonName.length; j++) {
                    parseredData[i][j] = json.getString(jsonName[j]);
                }
            }
            return parseredData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                new AlertDialog.Builder(this)
                        .setTitle("바스켓북")
                        .setMessage("어플리케이션을 종료 하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                finish();
                            }
                        })
                        .setNegativeButton("아니오", null).show();
                return false;
            default:
                return false;
        }
    }

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            return rootView;
        }
    }

    ////탭 아이콘 탭에 저장.//////////////////////////////////
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    private void setupTabIcons_match() {
        tabLayout.getTabAt(0).setIcon(tabIcons_match[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons_match[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons_match[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons_match[3]);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            // return PlaceholderFragment.newInstance(position + 1);
            Fragment fragment = null;
            Bundle args = null;
            switch (position) {
                case 0:
                    fragment = new SectionsFragment1();
                    args = new Bundle();
                    break;
                case 1:
                    fragment = new SectionsFragment2();
                    args = new Bundle();
                    break;
                case 2:
                    fragment = new SectionsFragment3();
                    args = new Bundle();
                    break;
                case 3:
                    fragment = new SectionsFragment4();
                    args = new Bundle();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

    }

    public static class SectionsFragment1 extends Fragment {
        //매칭 out선언
        Spinner spinner_Address_Do, spinner_Address_si;
        Button NewsFeed_Select_Button;

        String address1 = "서울", address2 = "전 체";
        ImageView NewsFeed_Writing;

        Match_Out_NewsFeed_Data_Adapter dataadapter;
        ArrayList<Match_Out_NewsFeed_Data_Setting> arrData;

        ImageView NewsFeed_Emblem;
        TextView NewsFeed_Court, NewsFeed_Data;
        String Choice_Tab = "out";
        int cnt_out, cnt_in, pos;
        static int Position = 0;
        private int MonthGap[] = {-30, -30, -27, -30, -29, -30, -29, -30, -30, -29, -30, -29};


        JSONObject json_out, json_in;
        JSONArray jArr_out, jArr_in;
        String[] jsonName = {"NewsFeed_Num", "NewsFeed_User", "NewsFeed_Do", "NewsFeed_Si", "NewsFeed_Name", "NewsFeed_Court", "NewsFeed_Data", "NewsFeed_Month", "NewsFeed_Day", "NewsFeed_Hour", "NewsFeed_Minute", "NewsFeed_Image", "Name", "Birth", "Sex", "Position", "Team", "Profile", "Height", "Weight", "Phone", "Comment_Count"};
        ProgressBar NewsFeed_ProgressBar;
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ImageButton Match_Button_Out;
        Button Match_Button_In, Match_In_Button_Search;
        FloatingActionButton Match_In_FloatingActionButton_fab;
        LinearLayout Match_Layout_Out, Match_Layout_In;
        Match_In_CustomList_MyAdapter match_In_CustomList_MyAdapter;
        ArrayList<Match_In_CustomList_MyData> match_In_CustomList_MyData;
        Spinner Match_In_Spinner_Address_do, Match_In_Spinner_Address_se;
        ArrayAdapter<CharSequence> adspin1, adspin2;
        String[][] parsedData_out, parsedData_TeamCheck;
        String[][] parsedData_in;
        String choice_do = "서울", choice_se = "전 체";
        int in_minScheduleId = 10000000;
        int out_minScheduleId = 10000000;
        boolean lastitemVisibleFlag_out = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크
        boolean firstitemVIsibleFlag_out = false;
        boolean lastitemVisibleFlag_in = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크
        boolean firstitemVIsibleFlag_in = false;

        public SectionsFragment1() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            final View rootView = inflater.inflate(R.layout.layout_match, container, false);
            Match_Layout_Tab = (LinearLayout) rootView.findViewById(R.id.Match_Layout_Tab);
            Match_Layout_Out_Address = (LinearLayout) rootView.findViewById(R.id.Match_Layout_Out_Address);
            Match_Layout_In_Address = (LinearLayout) rootView.findViewById(R.id.Match_Layout_In_Address);
            Match_In_CustomList = (ListView) rootView.findViewById(R.id.Match_In_CustomList);
            Match_Button_Out = (ImageButton) rootView.findViewById(R.id.Match_Button_Out);
            Match_Layout_Out = (LinearLayout) rootView.findViewById(R.id.Match_Layout_Out);
            Match_Layout_In = (LinearLayout) rootView.findViewById(R.id.Match_Layout_In);

            //매칭 out id매칭칭
            NewsFeed_Emblem = (ImageView) rootView.findViewById(R.id.NewsFeed_CustomList_Emblem);
            NewsFeed_Court = (TextView) rootView.findViewById(R.id.NewsFeed_CustomList_Court);
            NewsFeed_Data = (TextView) rootView.findViewById(R.id.NewsFeed_CustomList_Data);
            NewsFeed_Writing = (ImageView) rootView.findViewById(R.id.NewsFeed_Writing);
            NewsFeed_List = (ListView) rootView.findViewById(R.id.NewsFeed_List);

            NewsFeed_Select_Button = (Button) rootView.findViewById(R.id.NewsFeed_Select_Button);
            spinner_Address_Do = (Spinner) rootView.findViewById(R.id.NewsFeed_Spinner_Do);
            spinner_Address_si = (Spinner) rootView.findViewById(R.id.NewsFeed_Spinner_Si);

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //매칭 out 뉴스피드 코딩
            adspin1 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do, R.layout.zfile_spinner_test);
            adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_Address_Do.setAdapter(adspin1);
            spinner_Address_Do.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    address1 = adspin1.getItem(i).toString();
                    if (adspin1.getItem(i).equals("서울")) {
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_seoul, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_Address_si.setAdapter(adspin2);
                        spinner_Address_si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                address2 = adspin2.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    }
                    if (adspin1.getItem(i).equals("인천")) {
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_incheon, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_Address_si.setAdapter(adspin2);
                        spinner_Address_si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                address2 = adspin2.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    }
                    if (adspin1.getItem(i).equals("광주")) {
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_gwangju, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_Address_si.setAdapter(adspin2);
                        spinner_Address_si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                address2 = adspin2.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    }
                    if (adspin1.getItem(i).equals("대구")) {
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_DaeGu, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_Address_si.setAdapter(adspin2);
                        spinner_Address_si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                address2 = adspin2.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    }
                    if (adspin1.getItem(i).equals("울산")) {
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Ulsan, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_Address_si.setAdapter(adspin2);
                        spinner_Address_si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                address2 = adspin2.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    }
                    if (adspin1.getItem(i).equals("대전")) {
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_DaeJeon, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_Address_si.setAdapter(adspin2);
                        spinner_Address_si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                address2 = adspin2.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    }
                    if (adspin1.getItem(i).equals("부산")) {
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Busan, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_Address_si.setAdapter(adspin2);
                        spinner_Address_si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                address2 = adspin2.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    }
                    if (adspin1.getItem(i).equals("강원도")) {
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Gangwondo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_Address_si.setAdapter(adspin2);
                        spinner_Address_si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                address2 = adspin2.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    }
                    if (adspin1.getItem(i).equals("경기도")) {
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Gyeonggido, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_Address_si.setAdapter(adspin2);
                        spinner_Address_si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                address2 = adspin2.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    }
                    if (adspin1.getItem(i).equals("충청북도")) {
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Chungcheongbukdo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_Address_si.setAdapter(adspin2);
                        spinner_Address_si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                address2 = adspin2.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    }
                    if (adspin1.getItem(i).equals("충청남도")) {
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Chungcheongnamdo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_Address_si.setAdapter(adspin2);
                        spinner_Address_si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                address2 = adspin2.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    }
                    if (adspin1.getItem(i).equals("전라북도")) {
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Jeolabukdo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_Address_si.setAdapter(adspin2);
                        spinner_Address_si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                address2 = adspin2.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    }
                    if (adspin1.getItem(i).equals("전라남도")) {
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Jeolanamdo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_Address_si.setAdapter(adspin2);
                        spinner_Address_si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                address2 = adspin2.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    }
                    if (adspin1.getItem(i).equals("경상북도")) {
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Gyeongsangbukdo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_Address_si.setAdapter(adspin2);
                        spinner_Address_si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                address2 = adspin2.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    }
                    if (adspin1.getItem(i).equals("경상남도")) {
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Gyeongsangnamdo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_Address_si.setAdapter(adspin2);
                        spinner_Address_si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                address2 = adspin2.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    }
                    if (adspin1.getItem(i).equals("제주도")) {
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Jejudo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_Address_si.setAdapter(adspin2);
                        spinner_Address_si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                address2 = adspin2.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            NewsFeed_Select_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String result = "";
                    try {
                        HttpClient client = new DefaultHttpClient();
                        String postURL = "http://210.122.7.195:8080/gg/newsfeed_data_download.jsp";
                        HttpPost post = new HttpPost(postURL);
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("NewsFeed_Do", address1));
                        params.add(new BasicNameValuePair("NewsFeed_Si", address2));
                        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                        post.setEntity(ent);
                        HttpResponse response = client.execute(post);
                        BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                        String line = null;
                        while ((line = bufreader.readLine()) != null) {
                            result += line;
                        }
                        parsedData_out = jsonParserList(result);
                        setData();
                        dataadapter = new Match_Out_NewsFeed_Data_Adapter(getContext(), arrData, Id, MaxNum_out);
                        dataadapter.listview(NewsFeed_List);
                        NewsFeed_List.setAdapter(dataadapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            NewsFeed_Writing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent DataIntent = new Intent(getContext(), Match_Out_NewsFeed_Writing.class);
                    DataIntent.putExtra("Id", Id);
                    startActivity(DataIntent);
                }
            });
            String result = "";
            try {
                HttpClient client = new DefaultHttpClient();
                String postURL = "http://210.122.7.195:8080/gg/newsfeed_data_download.jsp";
                HttpPost post = new HttpPost(postURL);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("NewsFeed_Do", (String) "서울"));
                params.add(new BasicNameValuePair("NewsFeed_Si", (String) "전 체"));
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                post.setEntity(ent);
                HttpResponse response = client.execute(post);
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                String line = null;
                while ((line = bufreader.readLine()) != null) {
                    result += line;
                }
                parsedData_out = jsonParserList(result);
                setData();
                dataadapter = new Match_Out_NewsFeed_Data_Adapter(getContext(), arrData, Id, MaxNum_out);
                dataadapter.listview(NewsFeed_List);
                NewsFeed_List.setAdapter(dataadapter);
            } catch (Exception e) {
                e.printStackTrace();
            }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            Match_In_Spinner_Address_do = (Spinner) rootView.findViewById(R.id.Match_In_Spinner_Address_do);
            Match_In_Spinner_Address_se = (Spinner) rootView.findViewById(R.id.Match_In_Spinner_Address_se);
            Match_In_Button_Search = (Button) rootView.findViewById(R.id.Match_In_Button_Search);
            Match_In_FloatingActionButton_fab = (FloatingActionButton) rootView.findViewById(R.id.Match_In_FloatingActionButton_fab);
            Match_Button_Out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    out_in_choice = "out";
                    Match_Layout_Out.setVisibility(View.VISIBLE);
                    Match_Layout_In.setVisibility(View.GONE);
                    Choice_Tab = "out";
                    out_minScheduleId = 10000;
                    try {
                        HttpClient client = new DefaultHttpClient();
                        String postURL = "http://210.122.7.195:8080/gg/newsfeed_data_download.jsp";
                        HttpPost post = new HttpPost(postURL);
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("NewsFeed_Do", (String) "서울"));
                        params.add(new BasicNameValuePair("NewsFeed_Si", (String) "전 체"));

                        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                        post.setEntity(ent);
                        HttpResponse response = client.execute(post);
                        BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

                        String line = null;
                        String result = "";
                        while ((line = bufreader.readLine()) != null) {
                            result += line;
                        }
                        parsedData_out = jsonParserList(result);
                        setData();
                        dataadapter = new Match_Out_NewsFeed_Data_Adapter(getContext(), arrData, Id, MaxNum_out);
                        dataadapter.listview(NewsFeed_List);
                        NewsFeed_List.setAdapter(dataadapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ////////////////////////////////리스트 뷰 구현////////////////////////////////////////////////
                }
            });
            NewsFeed_List.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
                    lastitemVisibleFlag_out = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
                    firstitemVIsibleFlag_out = (totalItemCount > 0) && (firstVisibleItem == 0);
                }

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
                    //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastitemVisibleFlag_out) {
                        String result = "";
                        try {
                            HttpClient client = new DefaultHttpClient();
                            String postURL = "http://210.122.7.195:8080/gg/newsfeed_data_download_scroll.jsp";
                            HttpPost post = new HttpPost(postURL);
                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            params.add(new BasicNameValuePair("NewsFeed_Do", (String) address1));
                            params.add(new BasicNameValuePair("NewsFeed_Si", (String) address2));
                            params.add(new BasicNameValuePair("minScheduleId", Integer.toString(out_minScheduleId)));
                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                            post.setEntity(ent);
                            HttpResponse response = client.execute(post);
                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                            String line = null;
                            while ((line = bufreader.readLine()) != null) {
                                result += line;
                            }
                            parsedData_out = jsonParserList(result);
                            for (int a = 0; a < parsedData_out.length; a++) {
                                arrData.add(new Match_Out_NewsFeed_Data_Setting(parsedData_out[a][0], parsedData_out[a][1], parsedData_out[a][2], parsedData_out[a][3], parsedData_out[a][4], parsedData_out[a][5], parsedData_out[a][6], parsedData_out[a][7], parsedData_out[a][8], parsedData_out[a][9], parsedData_out[a][10], parsedData_out[a][11], parsedData_out[a][12], parsedData_out[a][13], parsedData_out[a][14], parsedData_out[a][15], parsedData_out[a][16], parsedData_out[a][17], parsedData_out[a][18], parsedData_out[a][19], parsedData_out[a][20], parsedData_out[a][21]));
                            }
                            dataadapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && firstitemVIsibleFlag_out) {
                        Match_Layout_Tab.setVisibility(View.VISIBLE);
                        Match_Layout_Out_Address.setVisibility(View.VISIBLE);
                    }
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                        Match_Layout_Tab.setVisibility(View.GONE);
                        Match_Layout_Out_Address.setVisibility(View.GONE);
                    }
                }
            });
            ////////////////////////////////리스트 뷰 구현////////////////////////////////////////////////

////////////////////////////////            /////매칭 -In 구현/////////////////////////////////////////////////////////////////////////////////
            Match_Button_In = (Button) rootView.findViewById(R.id.Match_Button_In);

            Match_Button_In.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    in_minScheduleId = 10000;
                    Match_Layout_Out.setVisibility(View.GONE);
                    Match_Layout_In.setVisibility(View.VISIBLE);
                    out_in_choice = "in";
                    Choice_Tab = "in";

                    adspin1 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do, R.layout.zfile_spinner_test);
                    adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Match_In_Spinner_Address_do.setAdapter(adspin1);
                    Match_In_Spinner_Address_do.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                            if (adspin1.getItem(i).equals("서울")) {
                                choice_do = "서울";
                                //두번째 스피너 이벤트
                                adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_seoul, R.layout.zfile_spinner_test);
                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Match_In_Spinner_Address_se.setAdapter(adspin2);
                                Match_In_Spinner_Address_se.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        choice_se = adspin2.getItem(i).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });
                            } else if (adspin1.getItem(i).equals("인천")) {
                                choice_do = "인천";
                                adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_incheon, R.layout.zfile_spinner_test);
                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Match_In_Spinner_Address_se.setAdapter(adspin2);
                                Match_In_Spinner_Address_se.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        choice_se = adspin2.getItem(i).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });
                            } else if (adspin1.getItem(i).equals("광주")) {
                                choice_do = "광주";
                                adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_gwangju, R.layout.zfile_spinner_test);
                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Match_In_Spinner_Address_se.setAdapter(adspin2);
                                Match_In_Spinner_Address_se.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        choice_se = adspin2.getItem(i).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });
                            } else if (adspin1.getItem(i).equals("대구")) {
                                choice_do = "대구";
                                adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_DaeGu, R.layout.zfile_spinner_test);
                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Match_In_Spinner_Address_se.setAdapter(adspin2);
                                Match_In_Spinner_Address_se.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        choice_se = adspin2.getItem(i).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });
                            } else if (adspin1.getItem(i).equals("울산")) {
                                choice_do = "울산";
                                adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Ulsan, R.layout.zfile_spinner_test);
                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Match_In_Spinner_Address_se.setAdapter(adspin2);
                                Match_In_Spinner_Address_se.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        choice_se = adspin2.getItem(i).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });
                            } else if (adspin1.getItem(i).equals("대전")) {
                                choice_do = "대전";
                                adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_DaeJeon, R.layout.zfile_spinner_test);
                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Match_In_Spinner_Address_se.setAdapter(adspin2);
                                Match_In_Spinner_Address_se.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        choice_se = adspin2.getItem(i).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });
                            } else if (adspin1.getItem(i).equals("부산")) {
                                choice_do = "부산";
                                adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Busan, R.layout.zfile_spinner_test);
                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Match_In_Spinner_Address_se.setAdapter(adspin2);
                                Match_In_Spinner_Address_se.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        choice_se = adspin2.getItem(i).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });
                            } else if (adspin1.getItem(i).equals("강원도")) {
                                choice_do = "강원도";
                                adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Gangwondo, R.layout.zfile_spinner_test);
                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Match_In_Spinner_Address_se.setAdapter(adspin2);
                                Match_In_Spinner_Address_se.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        choice_se = adspin2.getItem(i).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });
                            } else if (adspin1.getItem(i).equals("경기도")) {
                                choice_do = "경기도";
                                adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Gyeonggido, R.layout.zfile_spinner_test);
                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Match_In_Spinner_Address_se.setAdapter(adspin2);
                                Match_In_Spinner_Address_se.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        choice_se = adspin2.getItem(i).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });
                            } else if (adspin1.getItem(i).equals("충청남도")) {
                                choice_do = "충청남도";
                                adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Chungcheongnamdo, R.layout.zfile_spinner_test);
                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Match_In_Spinner_Address_se.setAdapter(adspin2);
                                Match_In_Spinner_Address_se.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        choice_se = adspin2.getItem(i).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });
                            } else if (adspin1.getItem(i).equals("충청북도")) {
                                choice_do = "충청북도";
                                adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Chungcheongbukdo, R.layout.zfile_spinner_test);
                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Match_In_Spinner_Address_se.setAdapter(adspin2);
                                Match_In_Spinner_Address_se.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        choice_se = adspin2.getItem(i).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });
                            } else if (adspin1.getItem(i).equals("전라북도")) {
                                choice_do = "전라북도";
                                adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Jeolabukdo, R.layout.zfile_spinner_test);
                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Match_In_Spinner_Address_se.setAdapter(adspin2);
                                Match_In_Spinner_Address_se.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        choice_se = adspin2.getItem(i).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });
                            } else if (adspin1.getItem(i).equals("전라남도")) {
                                choice_do = "전라남도";
                                adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Jeolanamdo, R.layout.zfile_spinner_test);
                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Match_In_Spinner_Address_se.setAdapter(adspin2);
                                Match_In_Spinner_Address_se.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        choice_se = adspin2.getItem(i).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });
                            } else if (adspin1.getItem(i).equals("경상북도")) {
                                choice_do = "경상북도";
                                adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Gyeongsangbukdo, R.layout.zfile_spinner_test);
                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Match_In_Spinner_Address_se.setAdapter(adspin2);
                                Match_In_Spinner_Address_se.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        choice_se = adspin2.getItem(i).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });
                            } else if (adspin1.getItem(i).equals("경상남도")) {
                                choice_do = "경상남도";
                                adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Gyeongsangnamdo, R.layout.zfile_spinner_test);
                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Match_In_Spinner_Address_se.setAdapter(adspin2);
                                Match_In_Spinner_Address_se.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        choice_se = adspin2.getItem(i).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });
                            } else if (adspin1.getItem(i).equals("제주도")) {
                                choice_do = "제주도";
                                adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Jejudo, R.layout.zfile_spinner_test);
                                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Match_In_Spinner_Address_se.setAdapter(adspin2);
                                Match_In_Spinner_Address_se.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        choice_se = adspin2.getItem(i).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                    ///처음에 서울 전체 리스트 불러옵니다.////////////////////////////////////////////////////////
                    try {
                        HttpClient client = new DefaultHttpClient();
                        String postURL = "http://210.122.7.195:8080/Web_basket/Match_InList.jsp";
                        HttpPost post = new HttpPost(postURL);

                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("do", "서울"));
                        params.add(new BasicNameValuePair("se", "전 체"));

                        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                        post.setEntity(ent);

                        HttpResponse response = client.execute(post);
                        BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

                        String line = null;
                        String result = "";
                        while ((line = bufreader.readLine()) != null) {
                            result += line;
                        }
                        parsedData_in = inList_jsonParserList(result);
                        inList_setData();
                        match_In_CustomList_MyAdapter = new Match_In_CustomList_MyAdapter(rootView.getContext(), match_In_CustomList_MyData);
                        Match_In_CustomList.setAdapter(match_In_CustomList_MyAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //검색 버튼 클릭 이벤트/////////////////////////////////////////////////////////////////////////
                    Match_In_Button_Search.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            in_minScheduleId = 10000;
                            Http_In_Search Http_In_Search = new Http_In_Search();
                            Http_In_Search.execute(choice_do, choice_se);

                        }
                    });

                    /////////////////////////////////////////////////////////////////////////////////////////////////////
                    //플로팅버튼을 리스트에 뜨도록 매칭
                    Match_In_FloatingActionButton_fab.attachToListView(Match_In_CustomList);
                    ///in리스트를 등록합니다.////////////////////////////////////////////////////////
                    Match_In_FloatingActionButton_fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String TeamCheck_result = "";
                            try {
                                HttpClient client = new DefaultHttpClient();
                                String postURL = "http://210.122.7.195:8080/Web_basket/TeamCheck.jsp";
                                HttpPost post = new HttpPost(postURL);

                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                params.add(new BasicNameValuePair("Id", Id));

                                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                post.setEntity(ent);

                                HttpResponse response = client.execute(post);
                                BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

                                String line = null;
                                while ((line = bufreader.readLine()) != null) {
                                    TeamCheck_result += line;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            parsedData_TeamCheck = jsonParserList_TeamCheck(TeamCheck_result);

                            if (parsedData_TeamCheck[0][0].equals("Unexist")) {
                                Snackbar.make(view, "팀 등록 후 이용해주시기 바랍니다.", Snackbar.LENGTH_SHORT).show();
                            } else {
                                Activity activity = (Activity) rootView.getContext();
                                Intent intent_In_Register = new Intent(rootView.getContext(), Match_In_Register.class);
                                intent_In_Register.putExtra("Id", Id);
                                activity.startActivity(intent_In_Register);
                            }
                        }
                    });

                    Match_In_CustomList.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
                            lastitemVisibleFlag_in = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
                            firstitemVIsibleFlag_in = (totalItemCount > 0) && (firstVisibleItem == 0);
                        }

                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                            //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
                            //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
                            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastitemVisibleFlag_in) {
                                Http_In_Scroll Http_In_Scroll = new Http_In_Scroll();
                                Http_In_Scroll.execute();

                            }
                            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && firstitemVIsibleFlag_in) {
                                Match_Layout_Tab.setVisibility(View.VISIBLE);
                                Match_Layout_In_Address.setVisibility(View.VISIBLE);
                            }
                            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                                Match_Layout_Tab.setVisibility(View.GONE);
                                Match_Layout_In_Address.setVisibility(View.GONE);
                            }
                        }

                    });
                }
            });
            /////////////////어댑터에 값 넣음./////////////////////////


            return rootView;
        }

        private void inList_setData() {
            match_In_CustomList_MyData = new ArrayList<Match_In_CustomList_MyData>();
            for (int i = 0; i < parsedData_in.length; i++) {
                match_In_CustomList_MyData.add(new Match_In_CustomList_MyData(parsedData_in[i][0], parsedData_in[i][1], parsedData_in[i][2], parsedData_in[i][3], parsedData_in[i][4], parsedData_in[i][5], parsedData_in[i][6], parsedData_in[i][7], parsedData_in[i][8], parsedData_in[i][9], parsedData_in[i][10], parsedData_in[i][11], parsedData_in[i][12], Id, parsedData_in[i][13]));
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////
        private void setData() {
            arrData = new ArrayList<Match_Out_NewsFeed_Data_Setting>();
            for (int a = 0; a < parsedData_out.length; a++) {
                arrData.add(new Match_Out_NewsFeed_Data_Setting(parsedData_out[a][0], parsedData_out[a][1], parsedData_out[a][2], parsedData_out[a][3], parsedData_out[a][4], parsedData_out[a][5], parsedData_out[a][6], parsedData_out[a][7], parsedData_out[a][8], parsedData_out[a][9], parsedData_out[a][10], parsedData_out[a][11], parsedData_out[a][12], parsedData_out[a][13], parsedData_out[a][14], parsedData_out[a][15], parsedData_out[a][16], parsedData_out[a][17], parsedData_out[a][18], parsedData_out[a][19], parsedData_out[a][20], parsedData_out[a][21]));

            }
        }

        public class Http_In_Search extends AsyncTask<String, Void, String> {
            ProgressDialog asyncDialog = new ProgressDialog(getContext());
            String[][] parsedData;

            @Override
            protected void onPreExecute() {
                asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                asyncDialog.setMessage("잠시만 기다려주세요..");
                // show dialog
                asyncDialog.show();
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    String postURL = "http://210.122.7.195:8080/Web_basket/Match_InList.jsp";
                    HttpPost post = new HttpPost(postURL);

                    List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                    params1.add(new BasicNameValuePair("do", choice_do));
                    params1.add(new BasicNameValuePair("se", choice_se));

                    UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params1, HTTP.UTF_8);
                    post.setEntity(ent);

                    HttpResponse response = client.execute(post);
                    BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

                    String line = null;
                    String result = "";
                    while ((line = bufreader.readLine()) != null) {
                        result += line;
                    }
                    parsedData_in = inList_jsonParserList(result);

                    return "succed";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "failed";
                }
            }

            @Override
            protected void onPostExecute(String result) {
                inList_setData();
                match_In_CustomList_MyAdapter = new Match_In_CustomList_MyAdapter(getContext(), match_In_CustomList_MyData);
                Match_In_CustomList.setAdapter(match_In_CustomList_MyAdapter);
                asyncDialog.dismiss();
                super.onPostExecute(result);
            }
        }

        public class Http_In_Scroll extends AsyncTask<String, Void, String> {
            ProgressDialog asyncDialog = new ProgressDialog(getContext());
            String[][] parsedData;

            @Override
            protected void onPreExecute() {
                asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                asyncDialog.setMessage("잠시만 기다려주세요..");
                // show dialog
                asyncDialog.show();
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    String postURL = "http://210.122.7.195:8080/Web_basket/Match_InList_Scroll.jsp";
                    HttpPost post = new HttpPost(postURL);

                    List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                    params1.add(new BasicNameValuePair("do", choice_do));
                    params1.add(new BasicNameValuePair("se", choice_se));
                    params1.add(new BasicNameValuePair("minScheduleId", Integer.toString(in_minScheduleId)));

                    UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params1, HTTP.UTF_8);
                    post.setEntity(ent);

                    HttpResponse response = client.execute(post);
                    BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

                    String line = null;
                    String result = "";
                    while ((line = bufreader.readLine()) != null) {
                        result += line;
                    }
                    parsedData_in = inList_jsonParserList(result);
                    for (int i = 0; i < parsedData_in.length; i++) {
                        match_In_CustomList_MyData.add(new Match_In_CustomList_MyData(parsedData_in[i][0], parsedData_in[i][1], parsedData_in[i][2], parsedData_in[i][3], parsedData_in[i][4], parsedData_in[i][5], parsedData_in[i][6], parsedData_in[i][7], parsedData_in[i][8], parsedData_in[i][9], parsedData_in[i][10], parsedData_in[i][11], parsedData_in[i][12], Id, parsedData_in[i][13]));
                    }

                    return "succed";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "failed";
                }
            }

            @Override
            protected void onPostExecute(String result) {
                match_In_CustomList_MyAdapter.notifyDataSetChanged();
                asyncDialog.dismiss();
                super.onPostExecute(result);
            }
        }

        public String[][] jsonParserList(String pRecvServerPage) {
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try {
                json_out = new JSONObject(pRecvServerPage);
                jArr_out = json_out.getJSONArray("List");
                parsedData_out = new String[jArr_out.length()][jsonName.length];
                for (int i = 0; i < jArr_out.length(); i++) {
                    json_out = jArr_out.getJSONObject(i);
                    for (int j = 0; j < jsonName.length; j++) {
                        parsedData_out[i][j] = json_out.getString(jsonName[j]);
                        if (MaxNum_out < Integer.valueOf(parsedData_out[i][0])) {
                            MaxNum_out = Integer.valueOf(parsedData_out[i][0]);
                        }
                    }
                    if (out_minScheduleId > Integer.parseInt(parsedData_out[i][0])) {
                        out_minScheduleId = Integer.parseInt(parsedData_out[i][0]);
                    }
                }
                return parsedData_out;
            } catch (JSONException e) {
                return null;
            }
        }

        /////매칭 탭 - in : 받아온 json 파싱합니다.//////////////////////////////////////////////////////////
        public String[][] inList_jsonParserList(String pRecvServerPage) {
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try {
                json_in = new JSONObject(pRecvServerPage);
                jArr_in = json_in.getJSONArray("List");
                String[] jsonName = {"msg1", "msg2", "msg3", "msg4", "msg5", "msg6", "msg7", "msg8", "msg9", "msg10", "msg11", "msg12", "msg13", "msg14"};
                parsedData_in = new String[jArr_in.length()][jsonName.length];
                for (int i = 0; i < jArr_in.length(); i++) {
                    json_in = jArr_in.getJSONObject(i);
                    for (int j = 0; j < jsonName.length; j++) {
                        parsedData_in[i][j] = json_in.getString(jsonName[j]);
                    }
                    if (in_minScheduleId > Integer.parseInt(parsedData_in[i][4])) {
                        in_minScheduleId = Integer.parseInt(parsedData_in[i][4]);
                    }
                    Log.i("minScheduleId", Integer.toString(in_minScheduleId));
                }
                return parsedData_in;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        public String GetTime(int position) {
            String Time;
            Integer Month, Day, Hour, Minute;

            Month = (Integer.parseInt(new SimpleDateFormat("MM").format(new java.sql.Date(System.currentTimeMillis())))) - Integer.parseInt(arrData.get(position).getMonth());
            Day = (Integer.parseInt(new SimpleDateFormat("dd").format(new java.sql.Date(System.currentTimeMillis())))) - Integer.parseInt(arrData.get(position).getDay());
            Hour = (Integer.parseInt(new SimpleDateFormat("kk").format(new java.sql.Date(System.currentTimeMillis())))) - Integer.parseInt(arrData.get(position).getHour());
            Minute = (Integer.parseInt(new SimpleDateFormat("mm").format(new java.sql.Date(System.currentTimeMillis())))) - Integer.parseInt(arrData.get(position).getMinute());
            if (Month > 0) {
                //매달 1일 일 경우
                if (Day == MonthGap[(Integer.parseInt(new SimpleDateFormat("MM").format(new java.sql.Date(System.currentTimeMillis()))))]) {
                    if (Hour > 1) {
                        return Hour + "시간전";
                    } else if (Hour == 1 && Minute >= 0) {
                        return Hour + "시간전";
                    } else if (Hour > 0 && Minute <= 0) {
                        return 60 + Minute + "분전";
                    }
                } else {
                    Month = Integer.parseInt(arrData.get(position).getMonth());
                    Day = Integer.parseInt(arrData.get(position).getDay());
                    Hour = Integer.parseInt(arrData.get(position).getHour());
                    Minute = Integer.parseInt(arrData.get(position).getMinute());
                    Time = Month + "월 " + Day + "일 " + Hour + "시 " + Minute + "분 ";
                    return Time;
                }
            } else {
                if (Day > 0) {
                    return Day + "일전";
                } else {
                    if (Hour > 1 && Minute > 0) {
                        return Hour + "시간전";
                    } else if (Hour > 1 && Minute < 0) {
                        return Hour - 1 + "시간전";
                    } else if (Hour == 1 && Minute >= 0) {
                        return Hour + "시간전";
                    } else if (Hour == 1 && Minute < 0) {
                        return 60 + Minute + "분전";
                    } else if (Hour == 0 && Minute > 0) {
                        return Minute + "분전";
                    } else if (Hour == 0 && Minute < 0) {
                        return 60 + Minute + "분전";
                    } else {
                        return "방금";
                    }
                }
            }
            return "Time Error";
        }


        /////팀이 존재하는지 체크합니다.
        public String[][] jsonParserList_TeamCheck(String pRecvServerPage) {
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try {
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");

                String[] jsonName = {"msg1"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];
                for (int i = 0; i < jArr.length(); i++) {
                    json = jArr.getJSONObject(i);
                    for (int j = 0; j < jsonName.length; j++) {
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
                return parseredData;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class SectionsFragment2 extends Fragment {
        Spinner League_League_Spinner_Do, League_League_Spinner_Se;
        ArrayAdapter<CharSequence> adspin1, adspin2;
        Button League_League_Button_Search;
        String choice_do, choice_se;
        String[][] league_parsedData, contest_parsedData;
        League_Contest_CustomList_MyAdapter League_Contest_CustomList_MyAdapter;
        ArrayList<League_Contest_CustomList_MyData> League_Contest_CustomList_MyData;

        com.mysports.basketbook.League_League_CustomList_MyAdapter League_League_CustomList_MyAdapter;
        ArrayList<com.mysports.basketbook.League_League_CustomList_MyData> League_League_CustomList_MyData;
        ListView League_League_ListView_League, League_Contest_ListView_Contest;

        public SectionsFragment2() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.layout_contest, container, false);
            League_League_Spinner_Do = (Spinner) rootView.findViewById(R.id.League_League_Spinner_Do);
            League_League_Spinner_Se = (Spinner) rootView.findViewById(R.id.League_League_Spinner_Se);
            League_League_Button_Search = (Button) rootView.findViewById(R.id.League_League_Button_Search);
            League_League_ListView_League = (ListView) rootView.findViewById(R.id.League_League_ListView_League);
            League_Contest_ListView_Contest = (ListView) rootView.findViewById(R.id.League_Contest_ListView_Contest);

            adspin1 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do, R.layout.zfile_spinner_test);
            adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            League_League_Spinner_Do.setAdapter(adspin1);
            League_League_Spinner_Do.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if (adspin1.getItem(i).equals("서울")) {
                        choice_do = "서울";
                        //두번째 스피너 이벤트
                        adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_seoul, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        League_League_Spinner_Se.setAdapter(adspin2);
                        League_League_Spinner_Se.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                choice_se = adspin2.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    } else if (adspin1.getItem(i).equals("경기도")) {
                        choice_do = "경기도";
                        adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Gyeonggido, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        League_League_Spinner_Se.setAdapter(adspin2);
                        League_League_Spinner_Se.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                choice_se = adspin2.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            //리그 데이터를 가져옵니다.
            try {
                HttpClient client = new DefaultHttpClient();
                String postURL = "http://210.122.7.195:8080/Web_basket/League_League.jsp";
                HttpPost post = new HttpPost(postURL);

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("do", "서울"));
                params.add(new BasicNameValuePair("se", "전 체"));

                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                post.setEntity(ent);

                HttpResponse response = client.execute(post);
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

                String line = null;
                String result = "";
                while ((line = bufreader.readLine()) != null) {
                    result += line;
                }
                league_parsedData = league_jsonParserList(result);
                league_setData();
                League_League_CustomList_MyAdapter = new League_League_CustomList_MyAdapter(rootView.getContext(), League_League_CustomList_MyData);
                League_League_ListView_League.setAdapter(League_League_CustomList_MyAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //대회데이터를 가져옵니다.
            try {
                HttpClient client = new DefaultHttpClient();
                String postURL = "http://210.122.7.195:8080/Web_basket/League_Contest.jsp";
                HttpPost post = new HttpPost(postURL);

                List<NameValuePair> params = new ArrayList<NameValuePair>();

                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                post.setEntity(ent);

                HttpResponse response = client.execute(post);
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

                String line = null;
                String result = "";
                while ((line = bufreader.readLine()) != null) {
                    result += line;
                }
                contest_parsedData = contest_jsonParserList(result);
                contest_setData();
                League_Contest_CustomList_MyAdapter = new League_Contest_CustomList_MyAdapter(rootView.getContext(), League_Contest_CustomList_MyData);
                League_Contest_ListView_Contest.setAdapter(League_Contest_CustomList_MyAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }

            League_League_Button_Search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        HttpClient client = new DefaultHttpClient();
                        String postURL = "http://210.122.7.195:8080/Web_basket/League_League.jsp";
                        HttpPost post = new HttpPost(postURL);

                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("do", choice_do));
                        params.add(new BasicNameValuePair("se", choice_se));

                        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                        post.setEntity(ent);

                        HttpResponse response = client.execute(post);
                        BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

                        String line = null;
                        String result = "";
                        while ((line = bufreader.readLine()) != null) {
                            result += line;
                        }
                        league_parsedData = league_jsonParserList(result);
                        league_setData();
                        League_League_CustomList_MyAdapter = new League_League_CustomList_MyAdapter(rootView.getContext(), League_League_CustomList_MyData);
                        League_League_ListView_League.setAdapter(League_League_CustomList_MyAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return rootView;
        }

        private void contest_setData() {
            League_Contest_CustomList_MyData = new ArrayList<League_Contest_CustomList_MyData>();
            for (int i = 0; i < contest_parsedData.length; i++) {
                League_Contest_CustomList_MyData.add(new League_Contest_CustomList_MyData(contest_parsedData[i][0], contest_parsedData[i][1], contest_parsedData[i][2], contest_parsedData[i][3]));
            }
        }

        private void league_setData() {
            League_League_CustomList_MyData = new ArrayList<com.mysports.basketbook.League_League_CustomList_MyData>();
            for (int i = 0; i < league_parsedData.length; i++) {
                int Rate = i + 1;
                League_League_CustomList_MyData.add(new League_League_CustomList_MyData(Rate, league_parsedData[i][0], league_parsedData[i][1]));
            }
        }

        /////대회 탭  받아온 json 파싱합니다.//////////////////////////////////////////////////////////
        public String[][] contest_jsonParserList(String pRecvServerPage) {
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try {
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");

                String[] jsonName = {"msg1", "msg2", "msg3", "msg4"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];
                for (int i = 0; i < jArr.length(); i++) {
                    json = jArr.getJSONObject(i);
                    for (int j = 0; j < jsonName.length; j++) {
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
                return parseredData;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        /////리그  받아온 json 파싱합니다.//////////////////////////////////////////////////////////
        public String[][] league_jsonParserList(String pRecvServerPage) {
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try {
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");

                String[] jsonName = {"msg1", "msg2"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];
                for (int i = 0; i < jArr.length(); i++) {
                    json = jArr.getJSONObject(i);
                    for (int j = 0; j < jsonName.length; j++) {
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
                return parseredData;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class SectionsFragment3 extends Fragment {
        Button League_Button_1, League_Button_2;
        ////////////////랭크 탭 선언
        String choice_do = "서울", choice_si = "전 체";
        String[][] help_parsedData;
        String[][] helpData;
        String[][] myteamData;
        TextView league_Rank_TextView_myrank;
        TextView league_Rank_TextView_myteam;
        TextView league_Rank_TextView_myteampoint;
        ImageView league_Rank_Image_myteam;
        SwipeMenuListView League_Rank_List;
        FloatingActionButton League_Rank_Help;

        League_Rank_Customlist_Adapter dataadapter;
        ArrayList<League_Rank_Customlist_Setting> arrData;
        ///////////////////////////////내 팀 게임 탭 선언
        Button League_Button_MyTeam, League_Button_YourTeam, League_Button_Start, League_Button_Cancel;
        String TabChoice = "1";
        String Do, Si, Team;
        int spinnum1, spinnum2, spinnum3;
        ArrayAdapter<CharSequence> adspin1, adspin2, adspin3;
        String[][] parsedData, parsedData_gameGenerate, parsedData_gameDelete, parsedData_gameStatus, parsedData_gameScoreAdd;
        ArrayList arr;
        static TimerTask myTask;
        static Timer timer;
///////////내 팀 기록 탭 선언//////////////////////////////////////////////////////////
ListView Leauge_myRecord_ListView;
        ////////////////////////////////////////////////
        public SectionsFragment3() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            final View rootView = inflater.inflate(R.layout.layout_league, container, false);
            League_Button_1 = (Button) rootView.findViewById(R.id.League_Button_1);
            League_Button_2 = (Button) rootView.findViewById(R.id.League_Button_2);
            League_Layout_1 = (LinearLayout) rootView.findViewById(R.id.League_Layout_1);
            League_Layout_2 = (LinearLayout) rootView.findViewById(R.id.League_Layout_2);

////////////게임 탭 선언
            League_Button_MyTeam = (Button) rootView.findViewById(R.id.League_Button_MyTeam);
            League_Button_YourTeam = (Button) rootView.findViewById(R.id.League_Button_YourTeam);
            League_Button_Start = (Button) rootView.findViewById(R.id.League_Button_Start);
            League_Button_Cancel = (Button) rootView.findViewById(R.id.League_Button_Cancel);
            ////랭크 탭 선언///////////////////////////////////////

            Spinner League_Rank_Spinner_Do = (Spinner) rootView.findViewById(R.id.League_Rank_Spinner_Do);
            final Spinner League_Rank_Spinner_Si = (Spinner) rootView.findViewById(R.id.League_Rank_Spinner_Si);
            Button League_Rank_Search_Button = (Button) rootView.findViewById(R.id.League_Rank_Search_Button);
            LinearLayout league_Rank_mylayout = (LinearLayout) rootView.findViewById(R.id.league_Rank_mylayout);

            league_Rank_TextView_myrank = (TextView) rootView.findViewById(R.id.league_Rank_TextView_myrank);
            league_Rank_TextView_myteam = (TextView) rootView.findViewById(R.id.league_Rank_TextView_myteam);
            league_Rank_TextView_myteampoint = (TextView) rootView.findViewById(R.id.league_Rank_TextView_myteampoint);
            league_Rank_Image_myteam = (ImageView) rootView.findViewById(R.id.league_Rank_Image_myteam);
            League_Rank_List = (SwipeMenuListView) rootView.findViewById(R.id.League_Rank_List);
            League_Rank_Help = (FloatingActionButton) rootView.findViewById(R.id.League_Rank_Help);
            //////////내 팀 기록 선언


            ///////////////////랭크 탭
            adspin1 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do, R.layout.zfile_spinner_test);
            adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            League_Rank_Spinner_Do.setAdapter(adspin1);
            League_Rank_Spinner_Do.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (adspin1.getItem(position).toString().equals("서울")) {
                        choice_do = adspin1.getItem(position).toString();
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_seoul, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        League_Rank_Spinner_Si.setAdapter(adspin2);
                        League_Rank_Spinner_Si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                choice_si = adspin2.getItem(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (adspin1.getItem(position).toString().equals("인천")) {
                        choice_do = adspin1.getItem(position).toString();
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_incheon, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        League_Rank_Spinner_Si.setAdapter(adspin2);
                        League_Rank_Spinner_Si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                choice_si = adspin2.getItem(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (adspin1.getItem(position).toString().equals("광주")) {
                        choice_do = adspin1.getItem(position).toString();
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_gwangju, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        League_Rank_Spinner_Si.setAdapter(adspin2);
                        League_Rank_Spinner_Si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                choice_si = adspin2.getItem(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (adspin1.getItem(position).toString().equals("대구")) {
                        choice_do = adspin1.getItem(position).toString();
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_DaeGu, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        League_Rank_Spinner_Si.setAdapter(adspin2);
                        League_Rank_Spinner_Si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                choice_si = adspin2.getItem(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (adspin1.getItem(position).toString().equals("울산")) {
                        choice_do = adspin1.getItem(position).toString();
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Ulsan, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        League_Rank_Spinner_Si.setAdapter(adspin2);
                        League_Rank_Spinner_Si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                choice_si = adspin2.getItem(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (adspin1.getItem(position).toString().equals("대전")) {
                        choice_do = adspin1.getItem(position).toString();
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_DaeJeon, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        League_Rank_Spinner_Si.setAdapter(adspin2);
                        League_Rank_Spinner_Si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                choice_si = adspin2.getItem(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (adspin1.getItem(position).toString().equals("부산")) {
                        choice_do = adspin1.getItem(position).toString();
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Busan, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        League_Rank_Spinner_Si.setAdapter(adspin2);
                        League_Rank_Spinner_Si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                choice_si = adspin2.getItem(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (adspin1.getItem(position).toString().equals("강원도")) {
                        choice_do = adspin1.getItem(position).toString();
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Gangwondo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        League_Rank_Spinner_Si.setAdapter(adspin2);
                        League_Rank_Spinner_Si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                choice_si = adspin2.getItem(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (adspin1.getItem(position).toString().equals("경기도")) {
                        choice_do = adspin1.getItem(position).toString();
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Gyeonggido, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        League_Rank_Spinner_Si.setAdapter(adspin2);
                        League_Rank_Spinner_Si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                choice_si = adspin2.getItem(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (adspin1.getItem(position).toString().equals("충청북도")) {
                        choice_do = adspin1.getItem(position).toString();
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Chungcheongbukdo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        League_Rank_Spinner_Si.setAdapter(adspin2);
                        League_Rank_Spinner_Si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                choice_si = adspin2.getItem(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (adspin1.getItem(position).toString().equals("충청남도")) {
                        choice_do = adspin1.getItem(position).toString();
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Chungcheongnamdo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        League_Rank_Spinner_Si.setAdapter(adspin2);
                        League_Rank_Spinner_Si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                choice_si = adspin2.getItem(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (adspin1.getItem(position).toString().equals("전라북도")) {
                        choice_do = adspin1.getItem(position).toString();
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Jeolabukdo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        League_Rank_Spinner_Si.setAdapter(adspin2);
                        League_Rank_Spinner_Si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                choice_si = adspin2.getItem(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (adspin1.getItem(position).toString().equals("전라남도")) {
                        choice_do = adspin1.getItem(position).toString();
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Jeolanamdo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        League_Rank_Spinner_Si.setAdapter(adspin2);
                        League_Rank_Spinner_Si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                choice_si = adspin2.getItem(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (adspin1.getItem(position).toString().equals("경상북도")) {
                        choice_do = adspin1.getItem(position).toString();
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Gyeongsangbukdo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        League_Rank_Spinner_Si.setAdapter(adspin2);
                        League_Rank_Spinner_Si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                choice_si = adspin2.getItem(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (adspin1.getItem(position).toString().equals("경상남도")) {
                        choice_do = adspin1.getItem(position).toString();
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Gyeongsangnamdo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        League_Rank_Spinner_Si.setAdapter(adspin2);
                        League_Rank_Spinner_Si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                choice_si = adspin2.getItem(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (adspin1.getItem(position).toString().equals("제주도")) {
                        choice_do = adspin1.getItem(position).toString();
                        adspin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do_Jejudo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        League_Rank_Spinner_Si.setAdapter(adspin2);
                        League_Rank_Spinner_Si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                choice_si = adspin2.getItem(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            String result = "";
            try {
                result = "";
                HttpClient client = new DefaultHttpClient();
                String postURL = "http://210.122.7.195:8080/gg/myteam_information_download.jsp";
                HttpPost post = new HttpPost(postURL);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("ID", Id));
                params.add(new BasicNameValuePair("Rank_Do", choice_do));
                params.add(new BasicNameValuePair("Rank_Si", choice_si));
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                post.setEntity(ent);
                HttpResponse response = client.execute(post);
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                String line = null;
                while ((line = bufreader.readLine()) != null) {
                    result += line;
                }
                myteamData = rank_jsonParserList(result);

                result = "";

                client = new DefaultHttpClient();
                postURL = "http://210.122.7.195:8080/gg/team_information_download.jsp";
                post = new HttpPost(postURL);
                params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("Rank_Do", choice_do));
                params.add(new BasicNameValuePair("Rank_Si", choice_si));
                ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                post.setEntity(ent);
                response = client.execute(post);
                bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                line = null;
                while ((line = bufreader.readLine()) != null) {
                    result += line;
                }
                help_parsedData = rank_jsonParserList(result);
                setData();
                dataadapter = new League_Rank_Customlist_Adapter(getContext(), arrData);
                dataadapter.listview(League_Rank_List);
                League_Rank_List.setAdapter(dataadapter);
            } catch (Exception e) {
                e.printStackTrace();
            }


            String En_Profile = null;
            try {
                En_Profile = URLEncoder.encode(myteamData[0][4], "utf-8");
                if (myteamData[0][4].equals(".")) {
                    Glide.with(rootView.getContext()).load(R.drawable.profile_basic_image).into(league_Rank_Image_myteam);
                } else {
                    Glide.with(rootView.getContext()).load("http://210.122.7.195:8080/Web_basket/imgs/Emblem/" + En_Profile + ".jpg").bitmapTransform(new CropCircleTransformation(Glide.get(rootView.getContext()).getBitmapPool()))
                            .into(league_Rank_Image_myteam);
                }
            } catch (UnsupportedEncodingException e) {

            }
            if (myteamData[0][0].equals(".")) {
                league_Rank_TextView_myrank.setText("");
                league_Rank_TextView_myteam.setText("팀가입후이용해주세요.");
                league_Rank_TextView_myteam.setGravity(2);
                league_Rank_TextView_myteampoint.setText("");
                League_Button_2.setEnabled(false);
            } else if (!(myteamData[0][2].equals(choice_do))&&myteamData[0][3].equals(choice_si)){
                league_Rank_TextView_myrank.setText("-");
                league_Rank_TextView_myteampoint.setText("-");
            } else {
                league_Rank_mylayout.setVisibility(View.VISIBLE);
                league_Rank_TextView_myrank.setText(myteamData[0][0]);
                league_Rank_TextView_myteam.setText(myteamData[0][1]);
                league_Rank_TextView_myteampoint.setText(myteamData[0][5]);
            }


            League_Rank_Help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.layout_customdialog_rank_help, (ViewGroup) rootView.findViewById(R.id.Layout_CustomDialog_help_Root));

                    final Button Layout_CustomDialog_help_Button1 = (Button) layout.findViewById(R.id.Layout_CustomDialog_help_Button1);
                    final Button Layout_CustomDialog_help_Button2 = (Button) layout.findViewById(R.id.Layout_CustomDialog_help_Button2);
                    final Button Layout_CustomDialog_help_Button3 = (Button) layout.findViewById(R.id.Layout_CustomDialog_help_Button3);
                    final TextView Layout_CustomDialog_help_Scroll1 = (TextView) layout.findViewById(R.id.Layout_CustomDialog_help_Scroll1);
                    final TextView Layout_CustomDialog_help_Scroll2 = (TextView) layout.findViewById(R.id.Layout_CustomDialog_help_Scroll2);
                    final TextView Layout_CustomDialog_help_Scroll3 = (TextView) layout.findViewById(R.id.Layout_CustomDialog_help_Scroll3);
                    final AlertDialog.Builder aDialog = new AlertDialog.Builder(getContext());
                    aDialog.setView(layout);
                    final AlertDialog ad = aDialog.create();
                    ad.show();
                    String result = "";
                    try {
                        HttpClient client = new DefaultHttpClient();
                        String postURL = "http://210.122.7.195:8080/gg/help_download.jsp";
                        HttpPost post = new HttpPost(postURL);
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                        post.setEntity(ent);
                        HttpResponse response = client.execute(post);
                        BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                        String line = null;
                        while ((line = bufreader.readLine()) != null) {
                            result += line;
                        }
                        helpData = help_jsonParserList(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < helpData.length; i++) {
                        if (!(helpData[i][0].equals(" "))) {
                            Layout_CustomDialog_help_Scroll1.setText(Layout_CustomDialog_help_Scroll1.getText() + helpData[i][0].toString() + "\n");
                        }
                        if (!(helpData[i][1].equals(" "))) {
                            Layout_CustomDialog_help_Scroll2.setText(Layout_CustomDialog_help_Scroll2.getText() + helpData[i][1].toString() + "\n");
                        }
                        if (!(helpData[i][2].equals(" "))) {
                            Layout_CustomDialog_help_Scroll3.setText(Layout_CustomDialog_help_Scroll3.getText() + helpData[i][2].toString() + "\n");
                        }
                    }


                    Layout_CustomDialog_help_Button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Layout_CustomDialog_help_Button1.setBackgroundColor(Color.parseColor("#2C9FD3"));
                            Layout_CustomDialog_help_Button2.setBackgroundColor(Color.WHITE);
                            Layout_CustomDialog_help_Button3.setBackgroundColor(Color.WHITE);
                            Layout_CustomDialog_help_Scroll1.setVisibility(View.VISIBLE);
                            Layout_CustomDialog_help_Scroll2.setVisibility(View.GONE);
                            Layout_CustomDialog_help_Scroll3.setVisibility(View.GONE);
                        }
                    });
                    Layout_CustomDialog_help_Button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Layout_CustomDialog_help_Button1.setBackgroundColor(Color.WHITE);
                            Layout_CustomDialog_help_Button2.setBackgroundColor(Color.parseColor("#2C9FD3"));
                            Layout_CustomDialog_help_Button3.setBackgroundColor(Color.WHITE);
                            Layout_CustomDialog_help_Scroll1.setVisibility(View.GONE);
                            Layout_CustomDialog_help_Scroll2.setVisibility(View.VISIBLE);
                            Layout_CustomDialog_help_Scroll3.setVisibility(View.GONE);
                        }
                    });
                    Layout_CustomDialog_help_Button3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Layout_CustomDialog_help_Button1.setBackgroundColor(Color.WHITE);
                            Layout_CustomDialog_help_Button2.setBackgroundColor(Color.WHITE);
                            Layout_CustomDialog_help_Button3.setBackgroundColor(Color.parseColor("#2C9FD3"));
                            Layout_CustomDialog_help_Scroll1.setVisibility(View.GONE);
                            Layout_CustomDialog_help_Scroll2.setVisibility(View.GONE);
                            Layout_CustomDialog_help_Scroll3.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });


            League_Rank_Search_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String result = "";
                    try {
                        result = "";
                        HttpClient client = new DefaultHttpClient();
                        String postURL = "http://210.122.7.195:8080/gg/myteam_information_download.jsp";
                        HttpPost post = new HttpPost(postURL);
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("ID", Id));
                        params.add(new BasicNameValuePair("Rank_Do", choice_do));
                        params.add(new BasicNameValuePair("Rank_Si", choice_si));
                        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                        post.setEntity(ent);
                        HttpResponse response = client.execute(post);
                        BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                        String line = null;
                        while ((line = bufreader.readLine()) != null) {
                            result += line;
                        }
                        myteamData = rank_jsonParserList(result);

                        result = "";

                        client = new DefaultHttpClient();
                        postURL = "http://210.122.7.195:8080/gg/team_information_download.jsp";
                        post = new HttpPost(postURL);
                        params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("Rank_Do", choice_do));
                        params.add(new BasicNameValuePair("Rank_Si", choice_si));
                        ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                        post.setEntity(ent);
                        response = client.execute(post);
                        bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                        line = null;
                        while ((line = bufreader.readLine()) != null) {
                            result += line;
                        }
                        help_parsedData = rank_jsonParserList(result);
                        setData();
                        dataadapter = new League_Rank_Customlist_Adapter(getContext(), arrData);
                        dataadapter.listview(League_Rank_List);
                        League_Rank_List.setAdapter(dataadapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    if (myteamData[0][0].equals(".")) {
                        league_Rank_TextView_myrank.setText("");
                        league_Rank_TextView_myteam.setText("팀가입후이용해주세요.");
                        league_Rank_TextView_myteam.setGravity(2);
                        league_Rank_TextView_myteampoint.setText("");
                        League_Button_2.setEnabled(false);
                    } else if (!(myteamData[0][2].equals(choice_do))) {
                        league_Rank_TextView_myrank.setText("-");
                        league_Rank_TextView_myteampoint.setText("-");
                    }else if(!(myteamData[0][3].equals(choice_si))){
                        league_Rank_TextView_myrank.setText("-");
                        league_Rank_TextView_myteampoint.setText("-");
                    }
                    else {
                        league_Rank_TextView_myrank.setText(myteamData[0][0]);
                        league_Rank_TextView_myteam.setText(myteamData[0][1]);
                        league_Rank_TextView_myteampoint.setText(myteamData[0][5]);
                    }
                }

            });
            /////////////////////내 팀 게임탭/////////////////////////////////////////////////////////////////////
            final Spinner Layout_CustomDialog_teamChoice_Do = (Spinner) layout.findViewById(R.id.Layout_CustomDialog_teamChoice_Do);
            final Spinner Layout_CustomDialog_teamChoice_Se = (Spinner) layout.findViewById(R.id.Layout_CustomDialog_teamChoice_Se);
            final Spinner Layout_CustomDialog_teamChoice_team = (Spinner) layout.findViewById(R.id.Layout_CustomDialog_teamChoice_team);


            League_Button_MyTeam.setText(Team1);
            if (yourTeamStatus.equals("allow_away")) {
                Team = HomeTeam;
                League_Button_Start.setText("신청 접수중");
                League_Button_Cancel.setVisibility(View.VISIBLE);
                League_Button_MyTeam.setText(Team);
                League_Button_YourTeam.setText(Team1);
                League_Button_YourTeam.setEnabled(false);
                myTask = new TimerTask() {
                    int i = 300;

                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                League_Button_Start.setText("신청 접수중 " + i);
                                if (i % 3 == 0) {
                                    String result = "";
                                    try {
                                        HttpClient client = new DefaultHttpClient();
                                        String postURL = "http://210.122.7.195:8080/Web_basket/GameStatusCheck.jsp";
                                        HttpPost post = new HttpPost(postURL);
                                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                                        params.add(new BasicNameValuePair("SendTeam", Team));
                                        params.add(new BasicNameValuePair("ReceiveTeam", Team1));
                                        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                        post.setEntity(ent);
                                        HttpResponse response = client.execute(post);
                                        BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                        String line = null;
                                        while ((line = bufreader.readLine()) != null) {
                                            result += line;
                                        }
                                        parsedData_gameStatus = jsonParserList_gameGenerate(result);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (parsedData_gameStatus[0][0].equals(".")) {
                                        Log.i("bbbbb", "bbbbb");
                                    } else if (parsedData_gameStatus[0][0].equals("ing")) {
                                        timer.cancel();
                                        League_Button_Start.setText("시합 중");
                                        League_Button_Start.setTextColor(Color.WHITE);
                                        League_Button_Start.setBackgroundColor(Color.RED);
                                    }
                                }
                            }
                        });
                        i--;
                    }
                };
                timer = new Timer();
                //timer.schedule(myTask, 5000);  // 5초후 실행하고 종료
                timer.schedule(myTask, 500, 1000); // 5초후 첫실행, 3초마다 계속실행
            }
            // int a=300
            else if (yourTeamStatus.equals("suggest")) {
                Team = AwayTeam;
                League_Button_Cancel.setVisibility(View.VISIBLE);
                League_Button_MyTeam.setText(HomeTeam);
                League_Button_YourTeam.setText(AwayTeam);
                League_Button_YourTeam.setEnabled(false);
                myTask = new TimerTask() {
                    int i = 300 - (allowtime * 60);

                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 해당 작업을 처리함
                                League_Button_Start.setText("신청 중 " + i);
                                if (i % 3 == 0) {
                                    String result = "";
                                    try {
                                        HttpClient client = new DefaultHttpClient();
                                        String postURL = "http://210.122.7.195:8080/Web_basket/GameStatusCheck.jsp";
                                        HttpPost post = new HttpPost(postURL);
                                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                                        params.add(new BasicNameValuePair("SendTeam", Team1));
                                        params.add(new BasicNameValuePair("ReceiveTeam", Team));
                                        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                        post.setEntity(ent);
                                        HttpResponse response = client.execute(post);
                                        BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                        String line = null;
                                        while ((line = bufreader.readLine()) != null) {
                                            result += line;
                                        }
                                        parsedData_gameStatus = jsonParserList_gameGenerate(result);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (parsedData_gameStatus[0][0].equals(".")) {
                                        Log.i("bbbbb", "bbbbb");
                                    }
                                    ///시합 신청이 수락된 경우
                                    else if (parsedData_gameStatus[0][0].equals("allow")) {
                                        timer.cancel();
                                        final MaterialDialog DropOutDialog = new MaterialDialog(rootView.getContext());
                                        DropOutDialog
                                                .setTitle("시합신청")
                                                .setMessage(Team + "팀에서 시합신청을 수락하였습니다.")
                                                .setNegativeButton("시합하기", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        String result = "";
                                                        try {
                                                            HttpClient client = new DefaultHttpClient();
                                                            String postURL = "http://210.122.7.195:8080/Web_basket/Gameing.jsp";
                                                            HttpPost post = new HttpPost(postURL);
                                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                                            params.add(new BasicNameValuePair("SendTeam", Team1));
                                                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                                            post.setEntity(ent);
                                                            HttpResponse response = client.execute(post);
                                                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                                            String line = null;
                                                            while ((line = bufreader.readLine()) != null) {
                                                                result += line;
                                                            }
                                                            parsedData_gameStatus = jsonParserList_gameGenerate(result);
                                                        } catch (Exception e) {

                                                            e.printStackTrace();
                                                        }
                                                        if (parsedData_gameStatus[0][0].equals("succed")) {
                                                            League_Button_Start.setText("시합 중");
                                                            League_Button_Start.setTextColor(Color.WHITE);
                                                            League_Button_Start.setBackgroundColor(Color.RED);
                                                            DropOutDialog.dismiss();
                                                        }
                                                    }
                                                });
                                        DropOutDialog.show();
                                    }
                                    //시합 신청이 거절된 경우
                                    else if (parsedData_gameStatus[0][0].equals("refuse")) {
                                        timer.cancel();
                                        final MaterialDialog DropOutDialog = new MaterialDialog(rootView.getContext());
                                        DropOutDialog
                                                .setTitle("시합신청")
                                                .setMessage(Team + "팀에서 시합신청을 거절하였습니다.")
                                                .setNegativeButton("확인", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        String result = "";
                                                        try {
                                                            HttpClient client = new DefaultHttpClient();
                                                            String postURL = "http://210.122.7.195:8080/Web_basket/GameDelete.jsp";
                                                            HttpPost post = new HttpPost(postURL);
                                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                                            params.add(new BasicNameValuePair("SendTeam", Team1));
                                                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                                            post.setEntity(ent);
                                                            HttpResponse response = client.execute(post);
                                                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                                            String line = null;
                                                            while ((line = bufreader.readLine()) != null) {
                                                                result += line;
                                                            }
                                                            parsedData_gameDelete = jsonParserList_gameDelete(result);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                        if (parsedData_gameDelete[0][0].equals("succed")) {
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    yourTeamStatus = "reset";
                                                                    League_Button_Cancel.setVisibility(View.GONE);
                                                                    League_Button_YourTeam.setText("팀 찾기");
                                                                    League_Button_YourTeam.setEnabled(true);
                                                                    League_Button_Start.setText("시합신청");
                                                                }
                                                            });
                                                        }
                                                        if (layout != null) {
                                                            ViewGroup parentViewGroup = (ViewGroup) layout.getParent();
                                                            if (null != parentViewGroup) {
                                                                parentViewGroup.removeView(layout);
                                                            }
                                                        }
                                                        DropOutDialog.dismiss();
                                                    }
                                                });
                                        DropOutDialog.show();
                                    }
                                }
                            }
                        });

                        Log.d("myTask", Integer.toString(i));
                        i--;
                        //시간이 초과된 경우 game 내 데이터 삭제 및 초기화.
                        if (i == 1) {
                            timer.cancel();
                            String result = "";
                            try {
                                HttpClient client = new DefaultHttpClient();
                                String postURL = "http://210.122.7.195:8080/Web_basket/GameDelete.jsp";
                                HttpPost post = new HttpPost(postURL);
                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                params.add(new BasicNameValuePair("SendTeam", Team1));
                                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                post.setEntity(ent);
                                HttpResponse response = client.execute(post);
                                BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                String line = null;
                                while ((line = bufreader.readLine()) != null) {
                                    result += line;
                                }
                                parsedData_gameDelete = jsonParserList_gameDelete(result);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (parsedData_gameDelete[0][0].equals("succed")) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        yourTeamStatus = "reset";
                                        League_Button_Cancel.setVisibility(View.GONE);
                                        League_Button_YourTeam.setText("팀 찾기");
                                        League_Button_YourTeam.setEnabled(true);
                                        League_Button_Start.setText("시합신청");
                                    }
                                });
                            }
                            if (layout != null) {
                                ViewGroup parentViewGroup = (ViewGroup) layout.getParent();
                                if (null != parentViewGroup) {
                                    parentViewGroup.removeView(layout);
                                }
                            }
                        }
                    }
                };
                timer = new Timer();
                //timer.schedule(myTask, 5000);  // 5초후 실행하고 종료
                timer.schedule(myTask, 500, 1000); // 5초후 첫실행, 3초마다 계속실행
            } else if (yourTeamStatus.equals("allow")) {
                final MaterialDialog DropOutDialog = new MaterialDialog(rootView.getContext());
                DropOutDialog
                        .setTitle("시합신청")
                        .setMessage(Team + "팀에서 시합신청을 수락하였습니다.")
                        .setNegativeButton("시합하기", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String result = "";
                                try {
                                    HttpClient client = new DefaultHttpClient();
                                    String postURL = "http://210.122.7.195:8080/Web_basket/Gameing.jsp";
                                    HttpPost post = new HttpPost(postURL);
                                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                                    params.add(new BasicNameValuePair("SendTeam", Team1));
                                    UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                    post.setEntity(ent);
                                    HttpResponse response = client.execute(post);
                                    BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                    String line = null;
                                    while ((line = bufreader.readLine()) != null) {
                                        result += line;
                                    }
                                    parsedData_gameStatus = jsonParserList_gameGenerate(result);
                                } catch (Exception e) {

                                    e.printStackTrace();
                                }
                                if (parsedData_gameStatus[0][0].equals("succed")) {
                                    League_Button_Start.setText("시합 중");
                                    League_Button_Start.setTextColor(Color.WHITE);
                                    League_Button_Start.setBackgroundColor(Color.RED);
                                    DropOutDialog.dismiss();
                                }
                            }
                        });
                DropOutDialog.show();
            } else if (yourTeamStatus.equals("ing")) {
                League_Button_Cancel.setVisibility(View.VISIBLE);
                League_Button_MyTeam.setText(HomeTeam);
                League_Button_YourTeam.setText(AwayTeam);
                League_Button_YourTeam.setEnabled(false);
                League_Button_Start.setText("시합 중");
                League_Button_Start.setTextColor(Color.WHITE);
                League_Button_Start.setBackgroundColor(Color.RED);
            }
            else if (yourTeamStatus.equals("ScoreCheck")) {
                League_Button_Cancel.setVisibility(View.VISIBLE);
                League_Button_MyTeam.setText(HomeTeam);
                League_Button_YourTeam.setText(AwayTeam);
                League_Button_YourTeam.setEnabled(false);
                League_Button_Start.setText("완료 대기중");
                League_Button_Start.setTextColor(Color.WHITE);
                League_Button_Start.setBackgroundColor(Color.RED);
            }
            //////////////////////////////////////////////
         //////////////////////////////   내 팀 기록 하단


//./////////////////////////////////탭 구분.//////////////////////////////////////////////////////////////////////////
            League_Button_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TabChoice = "1";
                    League_Layout_1.setVisibility(View.VISIBLE);
                    League_Layout_2.setVisibility(View.GONE);
                }
            });
            League_Button_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TabChoice = "2";
                    League_Layout_1.setVisibility(View.GONE);
                    League_Layout_2.setVisibility(View.VISIBLE);

                    ////////////내 팀 기록 //////////////////////////////////////////////////////////
                    String result = "";
                    try {
                        HttpClient client = new DefaultHttpClient();
                        String postURL = "http://210.122.7.195:8080/pp/getMyTeamName.jsp";
                        HttpPost post = new HttpPost(postURL);

                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("Id", Id));

                        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                        post.setEntity(ent);

                        HttpResponse response = client.execute(post);
                        BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

                        String line = null;
                        while ((line = bufreader.readLine()) != null) {
                            result += line;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.i("서버에서 받아온 팀 이름", result);
                    String[][] getTeamParsedData = jsonParserList_getTeamName(result);
                    if(getTeamParsedData != null) {
                        String myTeamName = getTeamParsedData[0][0];
                        result = "";
                        try {
                            HttpClient client = new DefaultHttpClient();
                            String postURL = "http://210.122.7.195:8080/pp/Leuage_myrecord.jsp";
                            HttpPost post = new HttpPost(postURL);

                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            params.add(new BasicNameValuePair("Id", Id));
                            params.add(new BasicNameValuePair("TeamName",myTeamName));

                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                            post.setEntity(ent);

                            HttpResponse response = client.execute(post);
                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

                            String line = null;
                            while ((line = bufreader.readLine()) != null) {
                                result += line;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String[][] MyRecordParsedList = jsonParserList_getMyRecord(result);

                        ArrayList<com.mysports.basketbook.Leauge_MyRecord_MyData> Leauge_Myrecord_MyData;
                        Leauge_Myrecord_MyData = new ArrayList<Leauge_MyRecord_MyData>();
                        String isWin;
                        if(MyRecordParsedList != null) {
                            for (int i = 0; i < MyRecordParsedList.length; i++) {
                                if(MyRecordParsedList[i][6].equals(getTeamParsedData[0][0])) {
                                    isWin = "win";
                                } else {
                                    isWin = "lose";
                                }
                                Leauge_Myrecord_MyData.add(new Leauge_MyRecord_MyData(isWin,MyRecordParsedList[i][5],MyRecordParsedList[i][4],MyRecordParsedList[i][3],MyRecordParsedList[i][2],MyRecordParsedList[i][1],MyRecordParsedList[i][0]));
                            }
                            Leauge_myRecord_ListView = (ListView)rootView.findViewById(R.id.Leauge_myrecord_listview);
                            Leauge_MyRecord_MyAdapter Adapter =new Leauge_MyRecord_MyAdapter(rootView.getContext(), Leauge_Myrecord_MyData);
                            Leauge_myRecord_ListView.setAdapter(Adapter);
                        }else {
                            //정보 없다 표시
                        }
                    }else {


                    }
                }
            });
            ///게임 탭 액션
            final Button Layout_CustomDialog_GameAdd_Button_HomeTeam = (Button) layout_GameAdd.findViewById(R.id.Layout_CustomDialog_GameAdd_Button_HomeTeam);
            final EditText Layout_CustomDialog_GameAdd_EditText_HomeTeam = (EditText) layout_GameAdd.findViewById(R.id.Layout_CustomDialog_GameAdd_EditText_HomeTeam);
            final Button Layout_CustomDialog_GameAdd_Button_AwayTeam = (Button) layout_GameAdd.findViewById(R.id.Layout_CustomDialog_GameAdd_Button_AwayTeam);
            final EditText Layout_CustomDialog_GameAdd_EditText_AwayTeam = (EditText) layout_GameAdd.findViewById(R.id.Layout_CustomDialog_GameAdd_EditText_AwayTeam);

            League_Button_Start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (League_Button_YourTeam.getText().toString().equals("팀 찾기")) {
                        Snackbar.make(view, "팀 찾기 후 이용해주시기 바랍니다.", Snackbar.LENGTH_SHORT).show();
                    } else if (League_Button_Start.getText().toString().equals("시합 중")) {
                        Layout_CustomDialog_GameAdd_Button_HomeTeam.setText(League_Button_MyTeam.getText().toString());
                        Layout_CustomDialog_GameAdd_Button_AwayTeam.setText(League_Button_YourTeam.getText().toString());
                        final MaterialDialog GameAddDialog = new MaterialDialog(rootView.getContext());
                        GameAddDialog
                                .setTitle("시합 종료")
                                .setView(layout_GameAdd)
                                .setNegativeButton("취소", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        GameAddDialog.dismiss();
                                    }
                                })
                                .setPositiveButton("확인", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (Layout_CustomDialog_GameAdd_EditText_HomeTeam.equals("")) {
                                            Snackbar.make(v, "점수를 입력해주세요.", Snackbar.LENGTH_SHORT);
                                        } else if (Layout_CustomDialog_GameAdd_EditText_AwayTeam.equals("")) {
                                            Snackbar.make(v, "점수를 입력해주세요.", Snackbar.LENGTH_SHORT);
                                        } else {
                                            String HomeScore = Layout_CustomDialog_GameAdd_EditText_HomeTeam.getText().toString();
                                            String AwayScore = Layout_CustomDialog_GameAdd_EditText_AwayTeam.getText().toString();
                                            String result = "";
                                            try {
                                                HttpClient client = new DefaultHttpClient();
                                                String postURL = "http://210.122.7.195:8080/Web_basket/GameScoreCheck.jsp";
                                                HttpPost post = new HttpPost(postURL);
                                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                                params.add(new BasicNameValuePair("SendTeam", Layout_CustomDialog_GameAdd_Button_HomeTeam.getText().toString()));
                                                params.add(new BasicNameValuePair("AwayTeam", Layout_CustomDialog_GameAdd_Button_AwayTeam.getText().toString()));
                                                params.add(new BasicNameValuePair("HomeScore", HomeScore));
                                                params.add(new BasicNameValuePair("AwayScore", AwayScore));
                                                params.add(new BasicNameValuePair("MyTeam", Team1));
                                                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                                post.setEntity(ent);
                                                HttpResponse response = client.execute(post);
                                                BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                                String line = null;
                                                while ((line = bufreader.readLine()) != null) {
                                                    result += line;
                                                }
                                                parsedData_gameScoreAdd = jsonParserList_gameGenerate(result);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            //시합 완료 처리
                                            if (parsedData_gameScoreAdd[0][0].equals("succed")) {
                                                League_Button_Start.setText("완료 대기중");
                                            }
                                        }
                                        GameAddDialog.dismiss();
                                    }
                                });
                        GameAddDialog.show();
                    }
                    else {
                        League_Button_Cancel.setVisibility(View.VISIBLE);
                        League_Button_YourTeam.setEnabled(false);
                        if (yourTeamStatus.equals("teamchoice")) {
                            String result = "";
                            try {
                                HttpClient client = new DefaultHttpClient();
                                String postURL = "http://210.122.7.195:8080/Web_basket/GameGenerate.jsp";
                                HttpPost post = new HttpPost(postURL);
                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                params.add(new BasicNameValuePair("SendTeam", Team1));
                                params.add(new BasicNameValuePair("ReceiveTeam", Team));
                                params.add(new BasicNameValuePair("Time", realTime));
                                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                post.setEntity(ent);
                                HttpResponse response = client.execute(post);
                                BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                String line = null;
                                while ((line = bufreader.readLine()) != null) {
                                    result += line;
                                }
                                parsedData_gameGenerate = jsonParserList_gameGenerate(result);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ///게임생성.
                            if (parsedData_gameGenerate[0][0].equals("succed")) {
                                myTask = new TimerTask() {
                                    int i = 300;

                                    public void run() {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // 해당 작업을 처리함
                                                League_Button_Start.setText("신청 중 " + i);
                                                if (i % 3 == 0) {
                                                    String result = "";
                                                    try {
                                                        HttpClient client = new DefaultHttpClient();
                                                        String postURL = "http://210.122.7.195:8080/Web_basket/GameStatusCheck.jsp";
                                                        HttpPost post = new HttpPost(postURL);
                                                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                                                        params.add(new BasicNameValuePair("SendTeam", Team1));
                                                        params.add(new BasicNameValuePair("ReceiveTeam", Team));
                                                        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                                        post.setEntity(ent);
                                                        HttpResponse response = client.execute(post);
                                                        BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                                        String line = null;
                                                        while ((line = bufreader.readLine()) != null) {
                                                            result += line;
                                                        }
                                                        parsedData_gameStatus = jsonParserList_gameGenerate(result);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    if (parsedData_gameStatus[0][0].equals(".")) {
                                                        Log.i("bbbbb", "bbbbb");
                                                    }
                                                    ///시합 신청이 수락된 경우
                                                    else if (parsedData_gameStatus[0][0].equals("allow")) {
                                                        timer.cancel();
                                                        final MaterialDialog DropOutDialog = new MaterialDialog(rootView.getContext());
                                                        DropOutDialog
                                                                .setTitle("시합신청")
                                                                .setMessage(Team + "팀에서 시합신청을 수락하였습니다.")
                                                                .setNegativeButton("시합하기", new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        String result = "";
                                                                        try {
                                                                            HttpClient client = new DefaultHttpClient();
                                                                            String postURL = "http://210.122.7.195:8080/Web_basket/Gameing.jsp";
                                                                            HttpPost post = new HttpPost(postURL);
                                                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                                                            params.add(new BasicNameValuePair("SendTeam", Team1));
                                                                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                                                            post.setEntity(ent);
                                                                            HttpResponse response = client.execute(post);
                                                                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                                                            String line = null;
                                                                            while ((line = bufreader.readLine()) != null) {
                                                                                result += line;
                                                                            }
                                                                            parsedData_gameStatus = jsonParserList_gameGenerate(result);
                                                                        } catch (Exception e) {

                                                                            e.printStackTrace();
                                                                        }
                                                                        if (parsedData_gameStatus[0][0].equals("succed")) {
                                                                            League_Button_Start.setText("시합 중");
                                                                            League_Button_Start.setTextColor(Color.WHITE);
                                                                            League_Button_Start.setBackgroundColor(Color.RED);
                                                                            DropOutDialog.dismiss();
                                                                        }
                                                                    }
                                                                });
                                                        DropOutDialog.show();
                                                    }
                                                    //시합 신청이 거절된 경우
                                                    else if (parsedData_gameStatus[0][0].equals("refuse")) {
                                                        timer.cancel();
                                                        final MaterialDialog DropOutDialog = new MaterialDialog(rootView.getContext());
                                                        DropOutDialog
                                                                .setTitle("시합신청")
                                                                .setMessage(Team + "팀에서 시합신청을 거절하였습니다.")
                                                                .setNegativeButton("확인", new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        String result = "";
                                                                        try {
                                                                            HttpClient client = new DefaultHttpClient();
                                                                            String postURL = "http://210.122.7.195:8080/Web_basket/GameDelete.jsp";
                                                                            HttpPost post = new HttpPost(postURL);
                                                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                                                            params.add(new BasicNameValuePair("SendTeam", Team1));
                                                                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                                                            post.setEntity(ent);
                                                                            HttpResponse response = client.execute(post);
                                                                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                                                            String line = null;
                                                                            while ((line = bufreader.readLine()) != null) {
                                                                                result += line;
                                                                            }
                                                                            parsedData_gameDelete = jsonParserList_gameDelete(result);
                                                                        } catch (Exception e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                        if (parsedData_gameDelete[0][0].equals("succed")) {
                                                                            getActivity().runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    yourTeamStatus = "reset";
                                                                                    League_Button_Cancel.setVisibility(View.GONE);
                                                                                    League_Button_YourTeam.setText("팀 찾기");
                                                                                    League_Button_YourTeam.setEnabled(true);
                                                                                    League_Button_Start.setText("시합신청");
                                                                                }
                                                                            });
                                                                        }
                                                                        if (layout != null) {
                                                                            ViewGroup parentViewGroup = (ViewGroup) layout.getParent();
                                                                            if (null != parentViewGroup) {
                                                                                parentViewGroup.removeView(layout);
                                                                            }
                                                                        }
                                                                        DropOutDialog.dismiss();
                                                                    }
                                                                });
                                                        DropOutDialog.show();
                                                    }
                                                }
                                            }
                                        });

                                        Log.d("myTask", Integer.toString(i));
                                        i--;
                                        //시간이 초과된 경우 game 내 데이터 삭제 및 초기화.
                                        if (i == 1) {
                                            timer.cancel();
                                            String result = "";
                                            try {
                                                HttpClient client = new DefaultHttpClient();
                                                String postURL = "http://210.122.7.195:8080/Web_basket/GameDelete.jsp";
                                                HttpPost post = new HttpPost(postURL);
                                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                                params.add(new BasicNameValuePair("SendTeam", Team1));
                                                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                                post.setEntity(ent);
                                                HttpResponse response = client.execute(post);
                                                BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                                String line = null;
                                                while ((line = bufreader.readLine()) != null) {
                                                    result += line;
                                                }
                                                parsedData_gameDelete = jsonParserList_gameDelete(result);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            if (parsedData_gameDelete[0][0].equals("succed")) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        yourTeamStatus = "reset";
                                                        League_Button_Cancel.setVisibility(View.GONE);
                                                        League_Button_YourTeam.setText("팀 찾기");
                                                        League_Button_YourTeam.setEnabled(true);
                                                        League_Button_Start.setText("시합신청");
                                                    }
                                                });
                                            }
                                            if (layout != null) {
                                                ViewGroup parentViewGroup = (ViewGroup) layout.getParent();
                                                if (null != parentViewGroup) {
                                                    parentViewGroup.removeView(layout);
                                                }
                                            }
                                        }
                                    }
                                };
                            }
                        }
                        timer = new Timer();
                        //timer.schedule(myTask, 5000);  // 5초후 실행하고 종료
                        timer.schedule(myTask, 500, 1000); // 5초후 첫실행, 3초마다 계속실행
                    }
                }
            });
            League_Button_Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!yourTeamStatus.equals("ScoreCheck")){
                        timer.cancel();
                    }
                    new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                        @Override
                        public void run() {
                            String result = "";
                            try {
                                HttpClient client = new DefaultHttpClient();
                                String postURL = "http://210.122.7.195:8080/Web_basket/GameDelete.jsp";
                                HttpPost post = new HttpPost(postURL);
                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                params.add(new BasicNameValuePair("SendTeam", Team1));
                                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                post.setEntity(ent);
                                HttpResponse response = client.execute(post);
                                BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                String line = null;
                                while ((line = bufreader.readLine()) != null) {
                                    result += line;
                                }
                                parsedData_gameDelete = jsonParserList_gameDelete(result);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (parsedData_gameDelete[0][0].equals("succed")) {
                                yourTeamStatus = "reset";
                                League_Button_Cancel.setVisibility(View.GONE);
                                League_Button_YourTeam.setText("팀 찾기");
                                League_Button_YourTeam.setEnabled(true);
                                League_Button_Start.setText("시합신청");
                            }
                            if (layout != null) {
                                ViewGroup parentViewGroup = (ViewGroup) layout.getParent();
                                if (null != parentViewGroup) {
                                    parentViewGroup.removeView(layout);
                                }
                            }
                        }
                    }, 1000);
                }
            });
            League_Button_YourTeam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (League_Button_YourTeam.getText().toString().equals("팀 찾기")) {
                        Team = "팀 선택";
                        final MaterialDialog TeamSearchDialog = new MaterialDialog(view.getContext());
                        TeamSearchDialog
                                .setTitle("팀 선택")
                                .setView(layout)
                                .setNegativeButton("취소", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        League_Button_YourTeam.setText("팀 찾기");
                                        TeamSearchDialog.dismiss();
                                        if (layout != null) {
                                            ViewGroup parentViewGroup = (ViewGroup) layout.getParent();
                                            if (null != parentViewGroup) {
                                                parentViewGroup.removeView(layout);
                                            }
                                        }
                                    }
                                })
                                .setPositiveButton("선택 완료", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (Team.equals("팀 선택")) {
                                            Snackbar.make(v, "팀을 선택해주세요.", Snackbar.LENGTH_SHORT).show();
                                        } else {
                                            TeamSearchDialog.dismiss();
                                            yourTeamStatus = "teamchoice";
                                            if (layout != null) {
                                                ViewGroup parentViewGroup = (ViewGroup) layout.getParent();
                                                if (null != parentViewGroup) {
                                                    parentViewGroup.removeView(layout);
                                                }
                                            }
                                        }
                                    }
                                });
                        TeamSearchDialog.show();
                    }
                }
            });
            //////////일반적 접근 - 신청 중인 경우.
            adspin1 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do, R.layout.zfile_spinner_test);
            adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Layout_CustomDialog_teamChoice_Do.setAdapter(adspin1);

            Layout_CustomDialog_teamChoice_Do.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    spinnum1 = i;
                    Do = adspin1.getItem(i).toString();
                    if (adspin1.getItem(i).equals("서울")) {
                        adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_seoul, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Layout_CustomDialog_teamChoice_Se.setAdapter(adspin2);
                        Layout_CustomDialog_teamChoice_Se.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        spinnum2 = i;
                                        Si = adspin2.getItem(i).toString();
                                        String result = "";
                                        try {
                                            HttpClient client = new DefaultHttpClient();
                                            String postURL = "http://210.122.7.195:8080/Web_basket/TeamInformation.jsp";
                                            HttpPost post = new HttpPost(postURL);
                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                            params.add(new BasicNameValuePair("TeamSearch_Do", (String) adspin1.getItem(spinnum1)));
                                            params.add(new BasicNameValuePair("TeamSearch_Si", (String) adspin2.getItem(spinnum2)));
                                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                            post.setEntity(ent);
                                            HttpResponse response = client.execute(post);
                                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                            String line = null;
                                            while ((line = bufreader.readLine()) != null) {
                                                result += line;
                                            }
                                            parsedData = jsonParserList_TeamSearch(result);
                                            arr = new ArrayList();
                                            arr.add("팀 선택");
                                            for (int a = 0; a < parsedData.length; a++) {
                                                arr.add(parsedData[a][0]);
                                            }
                                            adspin3 = new ArrayAdapter<CharSequence>(rootView.getContext(), android.R.layout.simple_spinner_item, arr);
                                            adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            Layout_CustomDialog_teamChoice_team.setAdapter(adspin3);
                                            Layout_CustomDialog_teamChoice_team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                                    Team = adspin3.getItem(i).toString();
                                                    League_Button_YourTeam.setText(Team);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                }
                        );
                    } else if (adspin1.getItem(i).equals("인천")) {
                        adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_incheon, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Layout_CustomDialog_teamChoice_Se.setAdapter(adspin2);
                        Layout_CustomDialog_teamChoice_Se.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        spinnum2 = i;
                                        Si = adspin2.getItem(i).toString();
                                        String result = "";
                                        try {
                                            HttpClient client = new DefaultHttpClient();
                                            String postURL = "http://210.122.7.195:8080/Web_basket/TeamInformation.jsp";
                                            HttpPost post = new HttpPost(postURL);
                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                            params.add(new BasicNameValuePair("TeamSearch_Do", (String) adspin1.getItem(spinnum1)));
                                            params.add(new BasicNameValuePair("TeamSearch_Si", (String) adspin2.getItem(spinnum2)));
                                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                            post.setEntity(ent);
                                            HttpResponse response = client.execute(post);
                                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                            String line = null;
                                            while ((line = bufreader.readLine()) != null) {
                                                result += line;
                                            }
                                            parsedData = jsonParserList_TeamSearch(result);
                                            arr = new ArrayList();
                                            arr.add("팀 선택");
                                            for (int a = 0; a < parsedData.length; a++) {
                                                arr.add(parsedData[a][0]);
                                            }
                                            adspin3 = new ArrayAdapter<CharSequence>(rootView.getContext(), android.R.layout.simple_spinner_item, arr);
                                            adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            Layout_CustomDialog_teamChoice_team.setAdapter(adspin3);
                                            Layout_CustomDialog_teamChoice_team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    Team = adspin3.getItem(i).toString();
                                                    League_Button_YourTeam.setText(Team);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                }
                        );
                    } else if (adspin1.getItem(i).equals("광주")) {
                        adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_gwangju, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Layout_CustomDialog_teamChoice_Se.setAdapter(adspin2);
                        Layout_CustomDialog_teamChoice_Se.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        spinnum2 = i;
                                        Si = adspin2.getItem(i).toString();
                                        String result = "";
                                        try {
                                            HttpClient client = new DefaultHttpClient();
                                            String postURL = "http://210.122.7.195:8080/Web_basket/TeamInformation.jsp";
                                            HttpPost post = new HttpPost(postURL);
                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                            params.add(new BasicNameValuePair("TeamSearch_Do", (String) adspin1.getItem(spinnum1)));
                                            params.add(new BasicNameValuePair("TeamSearch_Si", (String) adspin2.getItem(spinnum2)));
                                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                            post.setEntity(ent);
                                            HttpResponse response = client.execute(post);
                                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                            String line = null;
                                            while ((line = bufreader.readLine()) != null) {
                                                result += line;
                                            }
                                            parsedData = jsonParserList_TeamSearch(result);
                                            arr = new ArrayList();
                                            arr.add("팀 선택");
                                            for (int a = 0; a < parsedData.length; a++) {
                                                arr.add(parsedData[a][0]);
                                            }
                                            adspin3 = new ArrayAdapter<CharSequence>(rootView.getContext(), android.R.layout.simple_spinner_item, arr);
                                            adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            Layout_CustomDialog_teamChoice_team.setAdapter(adspin3);
                                            Layout_CustomDialog_teamChoice_team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    Team = adspin3.getItem(i).toString();
                                                    League_Button_YourTeam.setText(Team);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                }
                        );
                    } else if (adspin1.getItem(i).equals("대구")) {
                        adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_DaeGu, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Layout_CustomDialog_teamChoice_Se.setAdapter(adspin2);
                        Layout_CustomDialog_teamChoice_Se.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        spinnum2 = i;
                                        Si = adspin2.getItem(i).toString();
                                        String result = "";
                                        try {
                                            HttpClient client = new DefaultHttpClient();
                                            String postURL = "http://210.122.7.195:8080/Web_basket/TeamInformation.jsp";
                                            HttpPost post = new HttpPost(postURL);
                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                            params.add(new BasicNameValuePair("TeamSearch_Do", (String) adspin1.getItem(spinnum1)));
                                            params.add(new BasicNameValuePair("TeamSearch_Si", (String) adspin2.getItem(spinnum2)));
                                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                            post.setEntity(ent);
                                            HttpResponse response = client.execute(post);
                                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                            String line = null;
                                            while ((line = bufreader.readLine()) != null) {
                                                result += line;
                                            }
                                            parsedData = jsonParserList_TeamSearch(result);
                                            arr = new ArrayList();
                                            arr.add("팀 선택");
                                            for (int a = 0; a < parsedData.length; a++) {
                                                arr.add(parsedData[a][0]);
                                            }
                                            adspin3 = new ArrayAdapter<CharSequence>(rootView.getContext(), android.R.layout.simple_spinner_item, arr);
                                            adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            Layout_CustomDialog_teamChoice_team.setAdapter(adspin3);
                                            Layout_CustomDialog_teamChoice_team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    Team = adspin3.getItem(i).toString();
                                                    League_Button_YourTeam.setText(Team);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                }
                        );
                    } else if (adspin1.getItem(i).equals("울산")) {
                        adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Ulsan, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Layout_CustomDialog_teamChoice_Se.setAdapter(adspin2);
                        Layout_CustomDialog_teamChoice_Se.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        spinnum2 = i;
                                        Si = adspin2.getItem(i).toString();
                                        String result = "";
                                        try {
                                            HttpClient client = new DefaultHttpClient();
                                            String postURL = "http://210.122.7.195:8080/Web_basket/TeamInformation.jsp";
                                            HttpPost post = new HttpPost(postURL);
                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                            params.add(new BasicNameValuePair("TeamSearch_Do", (String) adspin1.getItem(spinnum1)));
                                            params.add(new BasicNameValuePair("TeamSearch_Si", (String) adspin2.getItem(spinnum2)));
                                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                            post.setEntity(ent);
                                            HttpResponse response = client.execute(post);
                                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                            String line = null;
                                            while ((line = bufreader.readLine()) != null) {
                                                result += line;
                                            }
                                            parsedData = jsonParserList_TeamSearch(result);
                                            arr = new ArrayList();
                                            arr.add("팀 선택");
                                            for (int a = 0; a < parsedData.length; a++) {
                                                arr.add(parsedData[a][0]);
                                            }
                                            adspin3 = new ArrayAdapter<CharSequence>(rootView.getContext(), android.R.layout.simple_spinner_item, arr);
                                            adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            Layout_CustomDialog_teamChoice_team.setAdapter(adspin3);
                                            Layout_CustomDialog_teamChoice_team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    Team = adspin3.getItem(i).toString();
                                                    League_Button_YourTeam.setText(Team);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                }
                        );
                    } else if (adspin1.getItem(i).equals("대전")) {
                        adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_DaeJeon, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Layout_CustomDialog_teamChoice_Se.setAdapter(adspin2);
                        Layout_CustomDialog_teamChoice_Se.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        spinnum2 = i;
                                        Si = adspin2.getItem(i).toString();
                                        String result = "";
                                        try {
                                            HttpClient client = new DefaultHttpClient();
                                            String postURL = "http://210.122.7.195:8080/Web_basket/TeamInformation.jsp";
                                            HttpPost post = new HttpPost(postURL);
                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                            params.add(new BasicNameValuePair("TeamSearch_Do", (String) adspin1.getItem(spinnum1)));
                                            params.add(new BasicNameValuePair("TeamSearch_Si", (String) adspin2.getItem(spinnum2)));
                                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                            post.setEntity(ent);
                                            HttpResponse response = client.execute(post);
                                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                            String line = null;
                                            while ((line = bufreader.readLine()) != null) {
                                                result += line;
                                            }
                                            parsedData = jsonParserList_TeamSearch(result);
                                            arr = new ArrayList();
                                            arr.add("팀 선택");
                                            for (int a = 0; a < parsedData.length; a++) {
                                                arr.add(parsedData[a][0]);
                                            }
                                            adspin3 = new ArrayAdapter<CharSequence>(rootView.getContext(), android.R.layout.simple_spinner_item, arr);
                                            adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            Layout_CustomDialog_teamChoice_team.setAdapter(adspin3);
                                            Layout_CustomDialog_teamChoice_team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    Team = adspin3.getItem(i).toString();
                                                    League_Button_YourTeam.setText(Team);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                }
                        );
                    } else if (adspin1.getItem(i).equals("부산")) {
                        adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Busan, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Layout_CustomDialog_teamChoice_Se.setAdapter(adspin2);
                        Layout_CustomDialog_teamChoice_Se.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        spinnum2 = i;
                                        Si = adspin2.getItem(i).toString();
                                        String result = "";
                                        try {
                                            HttpClient client = new DefaultHttpClient();
                                            String postURL = "http://210.122.7.195:8080/Web_basket/TeamInformation.jsp";
                                            HttpPost post = new HttpPost(postURL);
                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                            params.add(new BasicNameValuePair("TeamSearch_Do", (String) adspin1.getItem(spinnum1)));
                                            params.add(new BasicNameValuePair("TeamSearch_Si", (String) adspin2.getItem(spinnum2)));
                                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                            post.setEntity(ent);
                                            HttpResponse response = client.execute(post);
                                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                            String line = null;
                                            while ((line = bufreader.readLine()) != null) {
                                                result += line;
                                            }
                                            parsedData = jsonParserList_TeamSearch(result);
                                            arr = new ArrayList();
                                            arr.add("팀 선택");
                                            for (int a = 0; a < parsedData.length; a++) {
                                                arr.add(parsedData[a][0]);
                                            }
                                            adspin3 = new ArrayAdapter<CharSequence>(rootView.getContext(), android.R.layout.simple_spinner_item, arr);
                                            adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            Layout_CustomDialog_teamChoice_team.setAdapter(adspin3);
                                            Layout_CustomDialog_teamChoice_team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    Team = adspin3.getItem(i).toString();
                                                    League_Button_YourTeam.setText(Team);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                }
                        );
                    } else if (adspin1.getItem(i).equals("강원도")) {
                        adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Gangwondo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Layout_CustomDialog_teamChoice_Se.setAdapter(adspin2);
                        Layout_CustomDialog_teamChoice_Se.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        spinnum2 = i;
                                        Si = adspin2.getItem(i).toString();
                                        String result = "";
                                        try {
                                            HttpClient client = new DefaultHttpClient();
                                            String postURL = "http://210.122.7.195:8080/Web_basket/TeamInformation.jsp";
                                            HttpPost post = new HttpPost(postURL);
                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                            params.add(new BasicNameValuePair("TeamSearch_Do", (String) adspin1.getItem(spinnum1)));
                                            params.add(new BasicNameValuePair("TeamSearch_Si", (String) adspin2.getItem(spinnum2)));
                                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                            post.setEntity(ent);
                                            HttpResponse response = client.execute(post);
                                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                            String line = null;
                                            while ((line = bufreader.readLine()) != null) {
                                                result += line;
                                            }
                                            parsedData = jsonParserList_TeamSearch(result);
                                            arr = new ArrayList();
                                            arr.add("팀 선택");
                                            for (int a = 0; a < parsedData.length; a++) {
                                                arr.add(parsedData[a][0]);
                                            }
                                            adspin3 = new ArrayAdapter<CharSequence>(rootView.getContext(), android.R.layout.simple_spinner_item, arr);
                                            adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            Layout_CustomDialog_teamChoice_team.setAdapter(adspin3);
                                            Layout_CustomDialog_teamChoice_team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    Team = adspin3.getItem(i).toString();
                                                    League_Button_YourTeam.setText(Team);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                }
                        );
                    } else if (adspin1.getItem(i).equals("경기도")) {
                        adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Gyeonggido, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Layout_CustomDialog_teamChoice_Se.setAdapter(adspin2);
                        Layout_CustomDialog_teamChoice_Se.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        spinnum2 = i;
                                        Si = adspin2.getItem(i).toString();
                                        String result = "";
                                        try {
                                            HttpClient client = new DefaultHttpClient();
                                            String postURL = "http://210.122.7.195:8080/Web_basket/TeamInformation.jsp";
                                            HttpPost post = new HttpPost(postURL);
                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                            params.add(new BasicNameValuePair("TeamSearch_Do", (String) adspin1.getItem(spinnum1)));
                                            params.add(new BasicNameValuePair("TeamSearch_Si", (String) adspin2.getItem(spinnum2)));
                                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                            post.setEntity(ent);
                                            HttpResponse response = client.execute(post);
                                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                            String line = null;
                                            while ((line = bufreader.readLine()) != null) {
                                                result += line;
                                            }
                                            parsedData = jsonParserList_TeamSearch(result);
                                            arr = new ArrayList();
                                            arr.add("팀 선택");
                                            for (int a = 0; a < parsedData.length; a++) {
                                                arr.add(parsedData[a][0]);
                                            }
                                            adspin3 = new ArrayAdapter<CharSequence>(rootView.getContext(), android.R.layout.simple_spinner_item, arr);
                                            adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            Layout_CustomDialog_teamChoice_team.setAdapter(adspin3);
                                            Layout_CustomDialog_teamChoice_team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    Team = adspin3.getItem(i).toString();
                                                    League_Button_YourTeam.setText(Team);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                }
                        );
                    } else if (adspin1.getItem(i).equals("충청북도")) {
                        adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Chungcheongbukdo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Layout_CustomDialog_teamChoice_Se.setAdapter(adspin2);
                        Layout_CustomDialog_teamChoice_Se.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        spinnum2 = i;
                                        Si = adspin2.getItem(i).toString();
                                        String result = "";
                                        try {
                                            HttpClient client = new DefaultHttpClient();
                                            String postURL = "http://210.122.7.195:8080/Web_basket/TeamInformation.jsp";
                                            HttpPost post = new HttpPost(postURL);
                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                            params.add(new BasicNameValuePair("TeamSearch_Do", (String) adspin1.getItem(spinnum1)));
                                            params.add(new BasicNameValuePair("TeamSearch_Si", (String) adspin2.getItem(spinnum2)));
                                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                            post.setEntity(ent);
                                            HttpResponse response = client.execute(post);
                                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                            String line = null;
                                            while ((line = bufreader.readLine()) != null) {
                                                result += line;
                                            }
                                            parsedData = jsonParserList_TeamSearch(result);
                                            arr = new ArrayList();
                                            arr.add("팀 선택");
                                            for (int a = 0; a < parsedData.length; a++) {
                                                arr.add(parsedData[a][0]);
                                            }
                                            adspin3 = new ArrayAdapter<CharSequence>(rootView.getContext(), android.R.layout.simple_spinner_item, arr);
                                            adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            Layout_CustomDialog_teamChoice_team.setAdapter(adspin3);
                                            Layout_CustomDialog_teamChoice_team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    Team = adspin3.getItem(i).toString();
                                                    League_Button_YourTeam.setText(Team);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                }
                        );
                    } else if (adspin1.getItem(i).equals("충청남도")) {
                        adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Chungcheongnamdo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Layout_CustomDialog_teamChoice_Se.setAdapter(adspin2);
                        Layout_CustomDialog_teamChoice_Se.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        spinnum2 = i;
                                        Si = adspin2.getItem(i).toString();
                                        String result = "";
                                        try {
                                            HttpClient client = new DefaultHttpClient();
                                            String postURL = "http://210.122.7.195:8080/Web_basket/TeamInformation.jsp";
                                            HttpPost post = new HttpPost(postURL);
                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                            params.add(new BasicNameValuePair("TeamSearch_Do", (String) adspin1.getItem(spinnum1)));
                                            params.add(new BasicNameValuePair("TeamSearch_Si", (String) adspin2.getItem(spinnum2)));
                                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                            post.setEntity(ent);
                                            HttpResponse response = client.execute(post);
                                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                            String line = null;
                                            while ((line = bufreader.readLine()) != null) {
                                                result += line;
                                            }
                                            parsedData = jsonParserList_TeamSearch(result);
                                            arr = new ArrayList();
                                            arr.add("팀 선택");
                                            for (int a = 0; a < parsedData.length; a++) {
                                                arr.add(parsedData[a][0]);
                                            }
                                            adspin3 = new ArrayAdapter<CharSequence>(rootView.getContext(), android.R.layout.simple_spinner_item, arr);
                                            adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            Layout_CustomDialog_teamChoice_team.setAdapter(adspin3);
                                            Layout_CustomDialog_teamChoice_team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    Team = adspin3.getItem(i).toString();
                                                    League_Button_YourTeam.setText(Team);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                }
                        );
                    } else if (adspin1.getItem(i).equals("전라북도")) {
                        adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Jeolabukdo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Layout_CustomDialog_teamChoice_Se.setAdapter(adspin2);
                        Layout_CustomDialog_teamChoice_Se.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        spinnum2 = i;
                                        Si = adspin2.getItem(i).toString();
                                        String result = "";
                                        try {
                                            HttpClient client = new DefaultHttpClient();
                                            String postURL = "http://210.122.7.195:8080/Web_basket/TeamInformation.jsp";
                                            HttpPost post = new HttpPost(postURL);
                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                            params.add(new BasicNameValuePair("TeamSearch_Do", (String) adspin1.getItem(spinnum1)));
                                            params.add(new BasicNameValuePair("TeamSearch_Si", (String) adspin2.getItem(spinnum2)));
                                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                            post.setEntity(ent);
                                            HttpResponse response = client.execute(post);
                                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                            String line = null;
                                            while ((line = bufreader.readLine()) != null) {
                                                result += line;
                                            }
                                            parsedData = jsonParserList_TeamSearch(result);
                                            arr = new ArrayList();
                                            arr.add("팀 선택");
                                            for (int a = 0; a < parsedData.length; a++) {
                                                arr.add(parsedData[a][0]);
                                            }
                                            adspin3 = new ArrayAdapter<CharSequence>(rootView.getContext(), android.R.layout.simple_spinner_item, arr);
                                            adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            Layout_CustomDialog_teamChoice_team.setAdapter(adspin3);
                                            Layout_CustomDialog_teamChoice_team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    Team = adspin3.getItem(i).toString();
                                                    League_Button_YourTeam.setText(Team);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                }
                        );
                    } else if (adspin1.getItem(i).equals("전라남도")) {
                        adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Jeolanamdo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Layout_CustomDialog_teamChoice_Se.setAdapter(adspin2);
                        Layout_CustomDialog_teamChoice_Se.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        spinnum2 = i;
                                        Si = adspin2.getItem(i).toString();
                                        String result = "";
                                        try {
                                            HttpClient client = new DefaultHttpClient();
                                            String postURL = "http://210.122.7.195:8080/Web_basket/TeamInformation.jsp";
                                            HttpPost post = new HttpPost(postURL);
                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                            params.add(new BasicNameValuePair("TeamSearch_Do", (String) adspin1.getItem(spinnum1)));
                                            params.add(new BasicNameValuePair("TeamSearch_Si", (String) adspin2.getItem(spinnum2)));
                                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                            post.setEntity(ent);
                                            HttpResponse response = client.execute(post);
                                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                            String line = null;
                                            while ((line = bufreader.readLine()) != null) {
                                                result += line;
                                            }
                                            parsedData = jsonParserList_TeamSearch(result);
                                            arr = new ArrayList();
                                            arr.add("팀 선택");
                                            for (int a = 0; a < parsedData.length; a++) {
                                                arr.add(parsedData[a][0]);
                                            }
                                            adspin3 = new ArrayAdapter<CharSequence>(rootView.getContext(), android.R.layout.simple_spinner_item, arr);
                                            adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            Layout_CustomDialog_teamChoice_team.setAdapter(adspin3);
                                            Layout_CustomDialog_teamChoice_team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    Team = adspin3.getItem(i).toString();
                                                    League_Button_YourTeam.setText(Team);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                }
                        );
                    } else if (adspin1.getItem(i).equals("경상북도")) {
                        adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Gyeongsangbukdo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Layout_CustomDialog_teamChoice_Se.setAdapter(adspin2);
                        Layout_CustomDialog_teamChoice_Se.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        spinnum2 = i;
                                        Si = adspin2.getItem(i).toString();
                                        String result = "";
                                        try {
                                            HttpClient client = new DefaultHttpClient();
                                            String postURL = "http://210.122.7.195:8080/Web_basket/TeamInformation.jsp";
                                            HttpPost post = new HttpPost(postURL);
                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                            params.add(new BasicNameValuePair("TeamSearch_Do", (String) adspin1.getItem(spinnum1)));
                                            params.add(new BasicNameValuePair("TeamSearch_Si", (String) adspin2.getItem(spinnum2)));
                                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                            post.setEntity(ent);
                                            HttpResponse response = client.execute(post);
                                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                            String line = null;
                                            while ((line = bufreader.readLine()) != null) {
                                                result += line;
                                            }
                                            parsedData = jsonParserList_TeamSearch(result);
                                            arr = new ArrayList();
                                            arr.add("팀 선택");
                                            for (int a = 0; a < parsedData.length; a++) {
                                                arr.add(parsedData[a][0]);
                                            }
                                            adspin3 = new ArrayAdapter<CharSequence>(rootView.getContext(), android.R.layout.simple_spinner_item, arr);
                                            adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            Layout_CustomDialog_teamChoice_team.setAdapter(adspin3);
                                            Layout_CustomDialog_teamChoice_team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    Team = adspin3.getItem(i).toString();
                                                    League_Button_YourTeam.setText(Team);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                }
                        );
                    } else if (adspin1.getItem(i).equals("경상남도")) {
                        adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Gyeongsangnamdo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Layout_CustomDialog_teamChoice_Se.setAdapter(adspin2);
                        Layout_CustomDialog_teamChoice_Se.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        spinnum2 = i;
                                        Si = adspin2.getItem(i).toString();
                                        String result = "";
                                        try {
                                            HttpClient client = new DefaultHttpClient();
                                            String postURL = "http://210.122.7.195:8080/Web_basket/TeamInformation.jsp";
                                            HttpPost post = new HttpPost(postURL);
                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                            params.add(new BasicNameValuePair("TeamSearch_Do", (String) adspin1.getItem(spinnum1)));
                                            params.add(new BasicNameValuePair("TeamSearch_Si", (String) adspin2.getItem(spinnum2)));
                                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                            post.setEntity(ent);
                                            HttpResponse response = client.execute(post);
                                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                            String line = null;
                                            while ((line = bufreader.readLine()) != null) {
                                                result += line;
                                            }
                                            parsedData = jsonParserList_TeamSearch(result);
                                            arr = new ArrayList();
                                            arr.add("팀 선택");
                                            for (int a = 0; a < parsedData.length; a++) {
                                                arr.add(parsedData[a][0]);
                                            }
                                            adspin3 = new ArrayAdapter<CharSequence>(rootView.getContext(), android.R.layout.simple_spinner_item, arr);
                                            adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            Layout_CustomDialog_teamChoice_team.setAdapter(adspin3);
                                            Layout_CustomDialog_teamChoice_team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    Team = adspin3.getItem(i).toString();
                                                    League_Button_YourTeam.setText(Team);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                }
                        );
                    } else if (adspin1.getItem(i).equals("제주도")) {
                        adspin2 = ArrayAdapter.createFromResource(rootView.getContext(), R.array.spinner_do_Jejudo, R.layout.zfile_spinner_test);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Layout_CustomDialog_teamChoice_Se.setAdapter(adspin2);
                        Layout_CustomDialog_teamChoice_Se.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        spinnum2 = i;
                                        Si = adspin2.getItem(i).toString();
                                        String result = "";
                                        try {
                                            HttpClient client = new DefaultHttpClient();
                                            String postURL = "http://210.122.7.195:8080/Web_basket/TeamInformation.jsp";
                                            HttpPost post = new HttpPost(postURL);
                                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                                            params.add(new BasicNameValuePair("TeamSearch_Do", (String) adspin1.getItem(spinnum1)));
                                            params.add(new BasicNameValuePair("TeamSearch_Si", (String) adspin2.getItem(spinnum2)));
                                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                            post.setEntity(ent);
                                            HttpResponse response = client.execute(post);
                                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                            String line = null;
                                            while ((line = bufreader.readLine()) != null) {
                                                result += line;
                                            }
                                            parsedData = jsonParserList_TeamSearch(result);
                                            arr = new ArrayList();
                                            arr.add("팀 선택");
                                            for (int a = 0; a < parsedData.length; a++) {
                                                arr.add(parsedData[a][0]);
                                            }
                                            adspin3 = new ArrayAdapter<CharSequence>(rootView.getContext(), android.R.layout.simple_spinner_item, arr);
                                            adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            Layout_CustomDialog_teamChoice_team.setAdapter(adspin3);
                                            Layout_CustomDialog_teamChoice_team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    Team = adspin3.getItem(i).toString();
                                                    League_Button_YourTeam.setText(Team);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                }
                        );
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            return rootView;
        }

        public String[][] jsonParserList_TeamSearch(String pRecvServerPage) {
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try {
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");

                String[] jsonName = {"msg1"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];
                for (int i = 0; i < jArr.length(); i++) {
                    json = jArr.getJSONObject(i);
                    for (int j = 0; j < jsonName.length; j++) {
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
                return parseredData;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        public String[][] jsonParserList_gameGenerate(String pRecvServerPage) {
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try {
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");

                String[] jsonName = {"msg1"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];
                for (int i = 0; i < jArr.length(); i++) {
                    json = jArr.getJSONObject(i);
                    for (int j = 0; j < jsonName.length; j++) {
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
                return parseredData;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        public String[][] jsonParserList_gameDelete(String pRecvServerPage) {
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try {
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");

                String[] jsonName = {"msg1"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];
                for (int i = 0; i < jArr.length(); i++) {
                    json = jArr.getJSONObject(i);
                    for (int j = 0; j < jsonName.length; j++) {
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
                return parseredData;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
///////////////기록 탭 함수 선언
        public String[][] rank_jsonParserList(String pRecvServerPage) {
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try {
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");
                String[] jsonName = {"Rank", "TeamName", "TeamAddress_do", "TeamAddress_si", "Emblem", "Point"};
                help_parsedData = new String[jArr.length()][jsonName.length];
                for (int i = 0; i < jArr.length(); i++) {
                    json = jArr.getJSONObject(i);
                    for (int j = 0; j < jsonName.length; j++) {
                        help_parsedData[i][j] = json.getString(jsonName[j]);

                    }

                }
                return help_parsedData;
            } catch (JSONException e) {
                return null;
            }
        }

        public String[][] help_jsonParserList(String pRecvServerPage) {
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try {
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");

                String[] jsonName = {"help1", "help2", "help3"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];
                for (int i = 0; i < jArr.length(); i++) {
                    json = jArr.getJSONObject(i);
                    for (int j = 0; j < jsonName.length; j++) {
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
                return parseredData;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
/////////내 팀 관리 함수 선언
public String[][] jsonParserList_getTeamName(String pRecvServerPage) {
    Log.i("서버에서 받은 전체 내용", pRecvServerPage);
    try {
        JSONObject json = new JSONObject(pRecvServerPage);
        JSONArray jArr = json.getJSONArray("List");

        String[] jsonName = {"teamName"};
        String[][] parseredData = new String[jArr.length()][jsonName.length];
        for (int i = 0; i < jArr.length(); i++) {
            json = jArr.getJSONObject(i);
            for (int j = 0; j < jsonName.length; j++) {
                parseredData[i][j] = json.getString(jsonName[j]);
            }
        }
        return parseredData;
    } catch (JSONException e) {
        e.printStackTrace();
        return null;
    }
}

        public String[][] jsonParserList_getMyRecord(String pRecvServerPage) {
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try {
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");

                String[] jsonName = {"hometeamname", "awayteamname", "hometeamscore", "awayteamscore", "matchdate", "matchtime", "result"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];
                for (int i = 0; i < jArr.length(); i++) {
                    json = jArr.getJSONObject(i);
                    for (int j = 0; j < jsonName.length; j++) {
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
                return parseredData;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        //////////////////////////////////////////////////////////////
        private void setData() {
            arrData = new ArrayList<League_Rank_Customlist_Setting>();
            for (int a = 0; a < help_parsedData.length; a++) {
                arrData.add(new League_Rank_Customlist_Setting(help_parsedData[a][0], help_parsedData[a][1], help_parsedData[a][2], help_parsedData[a][3], help_parsedData[a][4], help_parsedData[a][5]));
            }
        }
    }

    public static class SectionsFragment4 extends Fragment {
        Button Profile_Button_Name, Profile_Button_Position, Profile_Button_Age_Physical, Profile_Button_TeamName;
        Button Profile_Button_TeamMake, Profile_Button_TeamManager, Profile_Button_TeamSearch, Profile_Button_Logout;
        Button Profile_Button_Password;
        FloatingActionButton Profile_Button_setting;
        String[][] parsedData_overLap, parsedData_TeamCheck, parsedData_Alarm;
        String ProfileUrl;
        Bitmap bmImg;
        String Profile;
        final int REQ_SELECT = 0;

        public SectionsFragment4() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            final View rootView = inflater.inflate(R.layout.layout_profile, container, false);
            Profile_Button_Name = (Button) rootView.findViewById(R.id.Profile_Button_Name);
            Profile_Button_Position = (Button) rootView.findViewById(R.id.Profile_Button_Position);
            Profile_Button_Age_Physical = (Button) rootView.findViewById(R.id.Profile_Button_Age_Physical);
            Profile_ImageVIew_Profile = (ImageView) rootView.findViewById(R.id.Profile_ImageVIew_Profile);
            Profile_Button_TeamName = (Button) rootView.findViewById(R.id.Profile_Button_TeamName);
            ///네비게이션 메뉴 선언
            Profile_Button_TeamMake = (Button) rootView.findViewById(R.id.Profile_Button_TeamMake);
            Profile_Button_TeamManager = (Button) rootView.findViewById(R.id.Profile_Button_TeamManager);
            Profile_Button_TeamSearch = (Button) rootView.findViewById(R.id.Profile_Button_TeamSearch);
            Profile_Button_Password = (Button) rootView.findViewById(R.id.Profile_Button_Password);
            Profile_Button_Logout = (Button) rootView.findViewById(R.id.Profile_Button_Logout);
            Profile_Button_setting = (FloatingActionButton) rootView.findViewById(R.id.Profile_Button_setting);
            Profile_Button_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent settingIntent = new Intent(getContext(), Setting.class);
                    settingIntent.putExtra("Id", Id);
                    settingIntent.putExtra("Token", Token);
                    startActivity(settingIntent);
                }
            });

            String Name = parsedData_Profile[0][2];
            String Position = parsedData_Profile[0][5];
            Profile = parsedData_Profile[0][7];
            String Sex = parsedData_Profile[0][2];
            Team1 = parsedData_Profile[0][6];
            String Age = ChangeAge(parsedData_Profile[0][4]);
            Profile_Button_Name.setText(Name);
            Profile_Button_Position.setText(Position);
            Profile_Button_Age_Physical.setText(Age);
            Profile_Button_TeamName.setText(Team1);
            //유저 개인 이미지를 서버에서 받아옵니다.
            try {
                String En_Profile = URLEncoder.encode(Profile, "utf-8");
                if (Profile.equals(".")) {
                    Glide.with(rootView.getContext()).load(R.drawable.profile_basic_image).into(Profile_ImageVIew_Profile);
                } else {
                    Glide.with(rootView.getContext()).load("http://210.122.7.195:8080/Web_basket/imgs/Profile/" + En_Profile + ".jpg").bitmapTransform(new CropCircleTransformation(Glide.get(rootView.getContext()).getBitmapPool()))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(Profile_ImageVIew_Profile);
                }
            } catch (UnsupportedEncodingException e) {

            }
            Profile_ImageVIew_Profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Profile.equals(".")) {
                        String result = "";
                        try {
                            HttpClient client = new DefaultHttpClient();
                            String postURL = "http://210.122.7.195:8080/Web_basket/Profile_Image.jsp";
                            HttpPost post = new HttpPost(postURL);

                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            params.add(new BasicNameValuePair("Id", Id));
                            params.add(new BasicNameValuePair("Image", Id));
                            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                            post.setEntity(ent);

                            HttpResponse response = client.execute(post);
                            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

                            String line = null;
                            while ((line = bufreader.readLine()) != null) {
                                result += line;
                            }
                            Profile = "exist";
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //사진 읽어오기위한 uri 작성하기.
                        Uri uri = Uri.parse("content://media/external/images/media");
                        //무언가 보여달라는 암시적 인텐트 객체 생성하기.
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        //인텐트에 요청을 덛붙인다.
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        //모든 이미지
                        intent.setType("image/*");
                        //결과값을 받아오는 액티비티를 실행한다.
                        startActivityForResult(intent, REQ_SELECT);
                    } else {
                        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.layout_customdialog_album, (ViewGroup) view.findViewById(R.id.Layout_CustomDialog_Album_Root));
                        final Button Layout_CustomDialog_Album_BasicImage = (Button) layout.findViewById(R.id.Layout_CustomDialog_Album_BasicImage);
                        final Button Layout_CustomDialog_Album_AlbumImage = (Button) layout.findViewById(R.id.Layout_CustomDialog_Album_AlbumImage);
                        final Button Layout_CustomDialog_Album_Cancel = (Button) layout.findViewById(R.id.Layout_CustomDialog_Album_Cancel);
                        final AlertDialog.Builder aDialog = new AlertDialog.Builder(view.getContext());
                        aDialog.setTitle("이미지 변경");
                        aDialog.setView(layout);
                        final AlertDialog ad = aDialog.create();
                        ad.show();
                        Layout_CustomDialog_Album_Cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ad.dismiss();
                            }
                        });
                        Layout_CustomDialog_Album_BasicImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String result = "";
                                try {
                                    HttpClient client = new DefaultHttpClient();
                                    String postURL = "http://210.122.7.195:8080/Web_basket/Profile_Image.jsp";
                                    HttpPost post = new HttpPost(postURL);

                                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                                    params.add(new BasicNameValuePair("Id", Id));
                                    params.add(new BasicNameValuePair("Image", "."));
                                    UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                    post.setEntity(ent);

                                    HttpResponse response = client.execute(post);
                                    BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

                                    String line = null;
                                    while ((line = bufreader.readLine()) != null) {
                                        result += line;
                                    }
                                    Profile_ImageVIew_Profile.setImageResource(R.drawable.profile_basic_image);
                                    ad.dismiss();
                                    Profile = ".";
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Snackbar.make(view, "다시 시도해 주시기 바랍니다.", Snackbar.LENGTH_SHORT).show();
                                }

                            }
                        });
                        Layout_CustomDialog_Album_AlbumImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String result = "";
                                try {
                                    HttpClient client = new DefaultHttpClient();
                                    String postURL = "http://210.122.7.195:8080/Web_basket/Profile_Image.jsp";
                                    HttpPost post = new HttpPost(postURL);

                                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                                    params.add(new BasicNameValuePair("Id", Id));
                                    params.add(new BasicNameValuePair("Image", Id));
                                    UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                    post.setEntity(ent);

                                    HttpResponse response = client.execute(post);
                                    BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

                                    String line = null;
                                    while ((line = bufreader.readLine()) != null) {
                                        result += line;
                                    }
                                    //사진 읽어오기위한 uri 작성하기.
                                    Uri uri = Uri.parse("content://media/external/images/media");
                                    //무언가 보여달라는 암시적 인텐트 객체 생성하기.
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    //인텐트에 요청을 덛붙인다.
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    //모든 이미지
                                    intent.setType("image/*");
                                    //결과값을 받아오는 액티비티를 실행한다.
                                    startActivityForResult(intent, REQ_SELECT);
                                    ad.dismiss();
                                    Profile = "exist";
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Snackbar.make(view, "다시 시도해 주시기 바랍니다.", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
            Profile_Button_TeamMake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //이미 팀 존재하는 지 확인 후 중복 제거
                    String result = "";
                    try {
                        HttpClient client = new DefaultHttpClient();
                        String postURL = "http://210.122.7.195:8080/Web_basket/TeamMake_OverLap.jsp";
                        HttpPost post = new HttpPost(postURL);

                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("Id", Id));

                        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                        post.setEntity(ent);

                        HttpResponse response = client.execute(post);
                        BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

                        String line = null;
                        while ((line = bufreader.readLine()) != null) {
                            result += line;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    parsedData_overLap = jsonParserList_TeamMake_OverLap(result);
                    if (parsedData_overLap[0][0].equals("overLap")) {
                        Snackbar.make(view, "이미 다른 팀에 가입 중이십니다.", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Intent intent_TeamMake = new Intent(rootView.getContext(), Navigation_TeamManager_TeamMake1.class);
                        intent_TeamMake.putExtra("Id", Id);
                        startActivity(intent_TeamMake);
                    }
                }
            });
            Profile_Button_TeamManager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String TeamCheck_result = "";
                    try {
                        HttpClient client = new DefaultHttpClient();
                        String postURL = "http://210.122.7.195:8080/Web_basket/TeamCheck.jsp";
                        HttpPost post = new HttpPost(postURL);

                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("Id", Id));

                        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                        post.setEntity(ent);

                        HttpResponse response = client.execute(post);
                        BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

                        String line = null;
                        while ((line = bufreader.readLine()) != null) {
                            TeamCheck_result += line;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    parsedData_TeamCheck = jsonParserList_TeamCheck(TeamCheck_result);
                    if (parsedData_TeamCheck[0][0].equals("Unexist")) {
                        Snackbar.make(view, "관리할 팀이 없습니다.", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Intent intent_TeamManager = new Intent(rootView.getContext(), Navigation_TeamManager.class);
                        intent_TeamManager.putExtra("Id", Id);
                        startActivity(intent_TeamManager);
                    }
                }
            });
            Profile_Button_TeamSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_TeamIntro = new Intent(rootView.getContext(), Navigation_TeamIntro.class);
                    intent_TeamIntro.putExtra("Id", Id);
                    startActivity(intent_TeamIntro);
                }
            });
            Profile_Button_Password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_Changepw = new Intent(rootView.getContext(), ChangePw1Activity.class);
                    intent_Changepw.putExtra("Id", Id);
                    startActivity(intent_Changepw);
                }
            });
            Profile_Button_Logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SharedPreferences prefs1 = rootView.getContext().getSharedPreferences("autoLogin", MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = prefs1.edit();
                    editor1.putString("id", "");
                    editor1.putString("pw", "");
                    editor1.putString("auto", "false");
                    editor1.commit();

                    onClickLogout();
                }
            });


            return rootView;
        }

        private void onClickLogout() {
            UserManagement.requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    redirectLoginActivity();
                }
            });
        }

        protected void redirectLoginActivity() {
            final Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            getActivity().finish();
        }

        //date 입력받아 나이 구하는 함수
        public String ChangeAge(String Age) {
            Calendar cal = Calendar.getInstance();
            String[] str = new String(Age).split(" \\/ ");
            String[] str_day = new String(str[2]).split(" ");
            int year = Integer.parseInt(str[0]);
            int month = Integer.parseInt(str[1]);
            int day = Integer.parseInt(str_day[0]);

            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month - 1);
            cal.set(Calendar.DATE, day);

            Calendar now = Calendar.getInstance();

            int age = now.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
            if ((cal.get(Calendar.MONTH) > now.get(Calendar.MONTH))
                    || (cal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                    && cal.get(Calendar.DAY_OF_MONTH) > now.get(Calendar.DAY_OF_MONTH))
                    ) {
                age--;
            }
            String Str_age = Integer.toString(age);
            return Str_age;
        }


        /////프로필 탭 사용자정보를 파싱합니다.//////////////////////////////////////////////////////////
        public String[][] jsonParserList_TeamMake_OverLap(String pRecvServerPage) {
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try {
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");

                String[] jsonName = {"msg1"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];
                for (int i = 0; i < jArr.length(); i++) {
                    json = jArr.getJSONObject(i);
                    for (int j = 0; j < jsonName.length; j++) {
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
                return parseredData;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        /////팀이 존재하는지 체크합니다.
        public String[][] jsonParserList_TeamCheck(String pRecvServerPage) {
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try {
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");

                String[] jsonName = {"msg1"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];
                for (int i = 0; i < jArr.length(); i++) {
                    json = jArr.getJSONObject(i);
                    for (int j = 0; j < jsonName.length; j++) {
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
                return parseredData;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        public String[][] jsonParserList_Alarm(String pRecvServerPage) {
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try {
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");

                String[] jsonName = {"msg1"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];
                for (int i = 0; i < jArr.length(); i++) {
                    json = jArr.getJSONObject(i);
                    for (int j = 0; j < jsonName.length; j++) {
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
                return parseredData;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

    }

    public String[][] jsonParserList(String pRecvServerPage) {
        Log.i("서버에서 받은 전체 내용", pRecvServerPage);
        try {
            JSONObject json = new JSONObject(pRecvServerPage);
            JSONArray jArr = json.getJSONArray("List");

            String[] jsonName = {"msg1"};
            String[][] parseredData = new String[jArr.length()][jsonName.length];
            for (int i = 0; i < jArr.length(); i++) {
                json = jArr.getJSONObject(i);
                for (int j = 0; j < jsonName.length; j++) {
                    parseredData[i][j] = json.getString(jsonName[j]);
                }
            }
            return parseredData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            //인텐트에 데이터가 담겨 왔다면
            if (!intent.getData().equals(null)) {
                //해당경로의 이미지를 intent에 담긴 이미지 uri를 이용해서 Bitmap형태로 읽어온다.
                Bitmap selPhoto = MediaStore.Images.Media.getBitmap(getContentResolver(), intent.getData());
                //이미지의 크기 조절하기.
                selPhoto = Bitmap.createScaledBitmap(selPhoto, 100, 100, true);
                //image_bt.setImageBitmap(selPhoto);//썸네일
                //화면에 출력해본다.
                //Profile_ImageVIew_Profile.setImageBitmap(selPhoto);
                Log.e("선택 된 이미지 ", "selPhoto : " + selPhoto);

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                //선택한 이미지의 uri를 읽어온다.
                Uri selPhotoUri = intent.getData();
                Log.e("전송", "시~~작 ~~~~~!");
                //업로드할 서버의 url 주소
                String urlString = "";
                urlString = "http://210.122.7.195:8080/Web_basket/Upload_Profile.jsp";
                //절대경로를 획득한다!!! 중요~
                Cursor c = getContentResolver().query(Uri.parse(selPhotoUri.toString()), null, null, null, null);
                c.moveToNext();
                //업로드할 파일의 절대경로 얻어오기("_data") 로 해도 된다.
                String absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
                Log.e("###파일의 절대 경로###", absolutePath);
                //파일 업로드 시작!
                HttpFileUpload(urlString, "", absolutePath);
                String En_Profile = URLEncoder.encode(Id, "utf-8");
                Glide.with(MainActivity.this).load("http://210.122.7.195:8080/Web_basket/imgs/Profile/" + En_Profile + ".jpg").bitmapTransform(new CropCircleTransformation(Glide.get(MainActivity.this).getBitmapPool()))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(Profile_ImageVIew_Profile);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {

        }

    }

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";

    public void HttpFileUpload(String urlString, String params, String fileName) {
        // fileName=TeamName;
        try {
            //선택한 파일의 절대 경로를 이용해서 파일 입력 스트림 객체를 얻어온다.
            FileInputStream mFileInputStream = new FileInputStream(fileName);
            //파일을 업로드할 서버의 url 주소를이용해서 URL 객체 생성하기.
            URL connectUrl = new URL(urlString);
            //Connection 객체 얻어오기.
            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
            conn.setDoInput(true);//입력할수 있도록
            conn.setDoOutput(true); //출력할수 있도록
            conn.setUseCaches(false);  //캐쉬 사용하지 않음

            //post 전송
            conn.setRequestMethod("POST");
            //파일 업로드 할수 있도록 설정하기.
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            //DataOutputStream 객체 생성하기.
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            //전송할 데이터의 시작임을 알린다.
            //String En_TeamName = URLEncoder.encode(TeamName, "utf-8");
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + URLEncoder.encode(Id, "utf-8") + ".jpg" + "\"" + lineEnd);
            dos.writeBytes(lineEnd);
            //한번에 읽어들일수있는 스트림의 크기를 얻어온다.
            int bytesAvailable = mFileInputStream.available();
            //byte단위로 읽어오기 위하여 byte 배열 객체를 준비한다.
            byte[] buffer = new byte[bytesAvailable];
            int bytesRead = 0;
            // read image
            while (bytesRead != -1) {
                //파일에서 바이트단위로 읽어온다.
                bytesRead = mFileInputStream.read(buffer);
                if (bytesRead == -1) break; //더이상 읽을 데이터가 없다면 빠저나온다.
                Log.d("Test", "image byte is " + bytesRead);
                //읽은만큼 출력한다.
                dos.write(buffer, 0, bytesRead);
                //출력한 데이터 밀어내기
                dos.flush();
            }
            //전송할 데이터의 끝임을 알린다.
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            //flush() 타이밍??
            //dos.flush();
            dos.close();//스트림 닫아주기
            mFileInputStream.close();//스트림 닫아주기.
            // get response
            int ch;
            //입력 스트림 객체를 얻어온다.
            InputStream is = conn.getInputStream();
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            String s = b.toString();
            Log.e("Test", "result = " + s);

        } catch (Exception e) {
            Log.d("Test", "exception " + e.getMessage());
            Snackbar.make(rootView, "업로드중 에러발생!.", Snackbar.LENGTH_SHORT).show();

        }
    }
}
