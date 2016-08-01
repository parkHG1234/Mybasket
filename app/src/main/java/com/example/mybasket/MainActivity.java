package com.example.mybasket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.bumptech.glide.Glide;
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
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends AppCompatActivity {
    static String Id="";
    static String realTime="";
    static String check_status="";
    static String check_time="";
    static String check_court="";
    // SharedPreferences prefs;
    //   SharedPreferences.Editor editor = prefs.edit();
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout tabLayout;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupTabIcons_match();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int a = tab.getPosition();
                if(a == 0){
                    tab.setIcon(R.drawable.basketball_clicked);
                    mViewPager.setCurrentItem(tab.getPosition());
                }
                else if(a == 1){
                    tab.setIcon(R.drawable.trophy_clicked);
                    mViewPager.setCurrentItem(tab.getPosition());
                }
                else if(a == 2){
                    tab.setIcon(R.drawable.document_clicked);
                    mViewPager.setCurrentItem(tab.getPosition());
                }
                else if(a == 3){
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
            }
        });

        SharedPreferences prefs_user = getSharedPreferences("basketball_user", MODE_PRIVATE);

        Intent intent1 = getIntent();
        Id = intent1.getStringExtra("Id");

        realTime = new SimpleDateFormat("HHmm").format(new java.sql.Date(System.currentTimeMillis()));
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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

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
    public static class SectionsFragment1 extends Fragment implements AbsListView.OnScrollListener{
        //매칭 out선언
        Spinner spinner_Address_Do, spinner_Address_si;
        Button NewsFeed_Select_Button;
        SwipeMenuListView NewsFeed_List;
        String address1, address2;
        FloatingActionButton NewsFeed_Writing;

        Match_Out_NewsFeed_Data_Adapter dataadapter;
        ArrayList<Match_Out_NewsFeed_Data_Setting> arrData;

        ImageView NewsFeed_Emblem;
        TextView NewsFeed_Court, NewsFeed_Data, NewsFeed_Person;
        boolean VisibleFlag = false;
        int cnt, pos;
        private int MonthGap[] = {-30, -30, -27, -30, -29, -30, -29, -30, -30, -29, -30, -29};


        JSONObject json;
        JSONArray jArr;
        String[][] parseredData;
        String[] jsonName = {"NewsFeed_Num", "NewsFeed_User", "NewsFeed_Do", "NewsFeed_Si", "NewsFeed_Court", "NewsFeed_UserCount", "NewsFeed_Data", "NewsFeed_Month", "NewsFeed_Day", "NewsFeed_Hour", "NewsFeed_Minute"};
        ProgressBar NewsFeed_ProgressBar;
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Button Match_Button_Out, Match_Button_In, Match_Layout_Out_Button_CheckIn, Match_In_Button_Search;
        FloatingActionButton Match_In_FloatingActionButton_fab;
        ListView Match_In_CustomList;
        LinearLayout Match_Layout_Out, Match_Layout_In;
        Match_In_CustomList_MyAdapter match_In_CustomList_MyAdapter;
        ArrayList<Match_In_CustomList_MyData> match_In_CustomList_MyData;
        Spinner Match_In_Spinner_Address_do, Match_In_Spinner_Address_se;
        ArrayAdapter<CharSequence> adspin1, adspin2;
        String[][] parsedData, parsedData_TeamCheck;

        public SectionsFragment1() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            final View rootView = inflater.inflate(R.layout.layout_match, container, false);

            Match_In_CustomList = (ListView) rootView.findViewById(R.id.Match_In_CustomList);
            Match_Button_Out = (Button) rootView.findViewById(R.id.Match_Button_Out);
            Match_Layout_Out = (LinearLayout) rootView.findViewById(R.id.Match_Layout_Out);
            Match_Layout_In = (LinearLayout) rootView.findViewById(R.id.Match_Layout_In);

            //매칭 out id매칭칭
            NewsFeed_Emblem = (ImageView) rootView.findViewById(R.id.NewsFeed_CustomList_Emblem);
            NewsFeed_Court = (TextView) rootView.findViewById(R.id.NewsFeed_CustomList_Court);
            NewsFeed_Data = (TextView) rootView.findViewById(R.id.NewsFeed_CustomList_Data);
            NewsFeed_Person = (TextView) rootView.findViewById(R.id.NewsFeed_CustomList_Person);
            NewsFeed_Writing = (FloatingActionButton) rootView.findViewById(R.id.NewsFeed_Writing);
            NewsFeed_List = (SwipeMenuListView) rootView.findViewById(R.id.NewsFeed_List);
            NewsFeed_Writing.attachToListView(NewsFeed_List);
           // NewsFeed_ProgressBar = (ProgressBar) rootView.findViewById(R.id.NewsFeed_ProgressBar);

            NewsFeed_Select_Button = (Button) rootView.findViewById(R.id.NewsFeed_Select_Button);
            spinner_Address_Do = (Spinner) rootView.findViewById(R.id.NewsFeed_Spinner_Do);
            spinner_Address_si = (Spinner) rootView.findViewById(R.id.NewsFeed_Spinner_Si);
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //매칭 out 뉴스피드 코딩
            adspin1 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_do, R.layout.zfile_spinner_test);
            adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_Address_Do.setAdapter(adspin1);
            SwipeMenuCreator creator = new SwipeMenuCreator() {
                @Override
                public void create(SwipeMenu menu) {

                    SwipeMenuItem commentItem = new SwipeMenuItem(
                            getContext());
                    commentItem.setBackground(new ColorDrawable(Color.rgb(255, 0, 0)));
                    commentItem.setWidth(90);
                    commentItem.setTitleSize(18);
                    commentItem.setIcon(R.drawable.comment);
                    menu.addMenuItem(commentItem);


                    SwipeMenuItem modifyitem = new SwipeMenuItem(
                            getContext());
                    modifyitem.setBackground(new ColorDrawable(Color.rgb(0, 255, 0)));
                    modifyitem.setWidth(90);
                    modifyitem.setTitleSize(18);
                    modifyitem.setIcon(R.drawable.setting);
                    menu.addMenuItem(modifyitem);


                    SwipeMenuItem deleteItem = new SwipeMenuItem(
                            getContext());
                    deleteItem.setBackground(new ColorDrawable(Color.rgb(0, 0, 255)));
                    deleteItem.setWidth(90);
                    deleteItem.setIcon(R.drawable.wastebasket);
                    menu.addMenuItem(deleteItem);

                }
            };

            NewsFeed_List.setMenuCreator(creator);


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
                        params.add(new BasicNameValuePair("NewsFeed_Do", (String) address1));
                        params.add(new BasicNameValuePair("NewsFeed_Si", (String) address2));
                        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                        post.setEntity(ent);
                        HttpResponse response = client.execute(post);
                        BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                        String line = null;
                        while ((line = bufreader.readLine()) != null) {
                            result += line;
                        }
                        parsedData = jsonParserList(result);
                        setData();
                        dataadapter = new Match_Out_NewsFeed_Data_Adapter(getContext(), arrData);
                        dataadapter.listview(NewsFeed_List);
                        NewsFeed_List.setAdapter(dataadapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            NewsFeed_List.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                    switch (index) {
                        case 0:
                            Intent CommentIntent = new Intent(getContext(), Match_Out_NewsFeed_Comment.class);

                            CommentIntent.putExtra("Num", arrData.get(position).getnum());
                            CommentIntent.putExtra("Court", arrData.get(position).getcourt());
                            CommentIntent.putExtra("Person", arrData.get(position).getperson());
                            CommentIntent.putExtra("Data", arrData.get(position).getdata());
                            CommentIntent.putExtra("Time", GetTime(position));
                            getContext().startActivity(CommentIntent);
                            break;
                        case 1:
                            Intent DataIntent = new Intent(getContext(), Match_Out_NewsFeed_Data_Modify.class);

                            DataIntent.putExtra("Num", arrData.get(position).getnum());
                            DataIntent.putExtra("Do", arrData.get(position).getDo());
                            DataIntent.putExtra("Si", arrData.get(position).getSi());
                            DataIntent.putExtra("court", arrData.get(position).getcourt());
                            DataIntent.putExtra("person", arrData.get(position).getperson());
                            DataIntent.putExtra("data", arrData.get(position).getdata());
                            DataIntent.putExtra("month", arrData.get(position).getMonth());
                            DataIntent.putExtra("day", arrData.get(position).getDay());
                            DataIntent.putExtra("hour", arrData.get(position).getHour());
                            DataIntent.putExtra("minute", arrData.get(position).getMinute());

                            getContext().startActivity(DataIntent);
                            break;
                        case 2:
                            String result = "";

                            try {
                                HttpClient client = new DefaultHttpClient();
                                String postURL = "http://210.122.7.195:8080/gg/newsfeed_data_delete.jsp";
                                HttpPost post = new HttpPost(postURL);
                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                params.add(new BasicNameValuePair("NewsFeed_Num", arrData.get(position).getnum()));
                                params.add(new BasicNameValuePair("NewsFeed_Do", arrData.get(position).getDo()));
                                params.add(new BasicNameValuePair("NewsFeed_Si", arrData.get(position).getSi()));
                                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                post.setEntity(ent);
                                HttpResponse response = client.execute(post);
                                BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                                String line = null;
                                while ((line = bufreader.readLine()) != null) {
                                    result += line;
                                }
                                parsedData = jsonParserList(result);
                                setData();
                                dataadapter = new Match_Out_NewsFeed_Data_Adapter(getContext(), arrData);
                                NewsFeed_List.setAdapter(dataadapter);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                    return false;
                }

            });

            NewsFeed_Writing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent DataIntent = new Intent(getContext(), Match_Out_NewsFeed_Writing.class);
                    startActivity(DataIntent);
                }
            });





////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
           Match_In_Spinner_Address_do = (Spinner)rootView.findViewById(R.id.Match_In_Spinner_Address_do);
            Match_In_Spinner_Address_se = (Spinner)rootView.findViewById(R.id.Match_In_Spinner_Address_se);
            Match_In_Button_Search = (Button)rootView.findViewById(R.id.Match_In_Button_Search);
            Match_In_FloatingActionButton_fab = (FloatingActionButton)rootView.findViewById(R.id.Match_In_FloatingActionButton_fab);

            Match_Button_Out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Match_Layout_Out.setVisibility(View.VISIBLE);
                    Match_Layout_In.setVisibility(View.GONE);
                    ////////////////////////////////리스트 뷰 구현////////////////////////////////////////////////
                }
            });

            ////////////////////////////////리스트 뷰 구현////////////////////////////////////////////////

////////////////////////////////            /////매칭 -In 구현/////////////////////////////////////////////////////////////////////////////////
            Match_Button_In = (Button)rootView.findViewById(R.id.Match_Button_In);

            Match_Button_In.setOnClickListener(new View.OnClickListener() {
                String choice_do, choice_se;

                @Override
                public void onClick(View view) {
                    Match_Layout_Out.setVisibility(View.GONE);
                    Match_Layout_In.setVisibility(View.VISIBLE);

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
                        parsedData = inList_jsonParserList(result);
                        inList_setData();
                        match_In_CustomList_MyAdapter = new Match_In_CustomList_MyAdapter(rootView.getContext(), match_In_CustomList_MyData);
                        Match_In_CustomList.setAdapter(match_In_CustomList_MyAdapter);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    //검색 버튼 클릭 이벤트/////////////////////////////////////////////////////////////////////////
                    Match_In_Button_Search.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                HttpClient client = new DefaultHttpClient();
                                String postURL = "http://210.122.7.195:8080/Web_basket/Match_InList.jsp";
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
                                parsedData = inList_jsonParserList(result);
                                inList_setData();
                                match_In_CustomList_MyAdapter = new Match_In_CustomList_MyAdapter(rootView.getContext(), match_In_CustomList_MyData);
                                Match_In_CustomList.setAdapter(match_In_CustomList_MyAdapter);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    /////////////////////////////////////////////////////////////////////////////////////////////////////
                    //플로팅버튼을 리스트에 뜨도록 매칭
                    Match_In_FloatingActionButton_fab.attachToListView(Match_In_CustomList);
                    ///in리스트를 등록합니다.////////////////////////////////////////////////////////
                    Match_In_FloatingActionButton_fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String TeamCheck_result="";
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

                            if(parsedData_TeamCheck[0][0].equals("Unexist")) {
                                Snackbar.make(view,"팀 등록 후 이용해주시기 바랍니다.",Snackbar.LENGTH_SHORT).show();
                            }
                            else {
                                Activity activity = (Activity) rootView.getContext();
                                Intent intent_In_Register = new Intent(rootView.getContext(), Match_In_Register.class);
                                intent_In_Register.putExtra("Id", Id);
                                activity.startActivity(intent_In_Register);
                            }
                        }
                    });

                }
            });
            /////////////////어댑터에 값 넣음./////////////////////////


            return rootView;
        }
        private void inList_setData()
        {
            match_In_CustomList_MyData = new ArrayList<Match_In_CustomList_MyData>();
            for(int i =0; i<parsedData.length; i++) {
                match_In_CustomList_MyData.add(new Match_In_CustomList_MyData(parsedData[i][0],parsedData[i][1],parsedData[i][2],parsedData[i][3],parsedData[i][4],parsedData[i][5]));
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        private void setData() {
            arrData = new ArrayList<Match_Out_NewsFeed_Data_Setting>();

            for (int a = 0; a < cnt; a++) {
                arrData.add(new Match_Out_NewsFeed_Data_Setting(parsedData[a][0], parsedData[a][1], parsedData[a][2], parsedData[a][3], parsedData[a][4], parsedData[a][5], parsedData[a][6], parsedData[a][7], parsedData[a][8], parsedData[a][9], parsedData[a][10]));
            }
        }

        public String[][] jsonParserList(String pRecvServerPage) {
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);

            try {
                json = new JSONObject(pRecvServerPage);
                jArr = json.getJSONArray("List");
                if (jArr.length() > 9) {
                    cnt = 10;
                } else {
                    cnt = jArr.length();
                }
                parseredData = new String[jArr.length()][jsonName.length];

                for (int i = 0; i < cnt; i++) {
                    json = jArr.getJSONObject(i);
                    for (int j = 0; j < jsonName.length; j++) {
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
                return parseredData;

            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView absListView, int scrollState) {

            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && VisibleFlag) {

                if (jArr.length() <= cnt) {
                    cnt = jArr.length();
                    VisibleFlag = false;
                } else {
                    cnt = cnt + 10;
                }
                try {
                    for (int i = 0; i < cnt; i++) {
                        json = jArr.getJSONObject(i);
                        for (int j = 0; j < jsonName.length; j++) {
                            parseredData[i][j] = json.getString(jsonName[j]);
                        }
                    }
                    setData();
                    dataadapter = new Match_Out_NewsFeed_Data_Adapter(getContext(), arrData);

                    NewsFeed_List.setAdapter(dataadapter);
                    NewsFeed_List.setSelection(pos + 1);


                } catch (JSONException e) {
                }

            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            VisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            pos = totalItemCount - visibleItemCount;
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
        /////매칭 탭 - out : 받아온 json 파싱합니다.//////////////////////////////////////////////////////////
        public String[][] outList_jsonParserList(String pRecvServerPage){
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try{
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");

                String[] jsonName = {"msg1","msg2","msg3"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];
                for(int i = 0; i<jArr.length();i++){
                    json = jArr.getJSONObject(i);
                    for (int j=0;j<jsonName.length; j++){
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
                return parseredData;
            }catch (JSONException e){
                e.printStackTrace();
                return null;
            }
        }
        /////매칭 탭 - in : 받아온 json 파싱합니다.//////////////////////////////////////////////////////////
        public String[][] inList_jsonParserList(String pRecvServerPage){
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try{
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");

                String[] jsonName = {"msg1","msg2","msg3","msg4","msg5","msg6"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];
                for(int i = 0; i<jArr.length();i++){
                    json = jArr.getJSONObject(i);
                    for (int j=0;j<jsonName.length; j++){
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
                return parseredData;
            }catch (JSONException e){
                e.printStackTrace();
                return null;
            }
        }
        /////팀이 존재하는지 체크합니다.
        public String[][] jsonParserList_TeamCheck(String pRecvServerPage){
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try{
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");

                String[] jsonName = {"msg1"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];
                for(int i = 0; i<jArr.length();i++){
                    json = jArr.getJSONObject(i);
                    for (int j=0;j<jsonName.length; j++){
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
                return parseredData;
            }catch (JSONException e){
                e.printStackTrace();
                return null;
            }
        }


    }





    public static class SectionsFragment2 extends Fragment {
        Spinner League_League_Spinner_Do, League_League_Spinner_Se;
        ArrayAdapter<CharSequence> adspin1, adspin2;
        Button League_League_Button_Search;
        String choice_do,choice_se;
        String[][] league_parsedData, contest_parsedData;
        League_Contest_CustomList_MyAdapter League_Contest_CustomList_MyAdapter;
        ArrayList<League_Contest_CustomList_MyData> League_Contest_CustomList_MyData;

        League_League_CustomList_MyAdapter League_League_CustomList_MyAdapter;
        ArrayList<League_League_CustomList_MyData> League_League_CustomList_MyData;
        ListView League_League_ListView_League, League_Contest_ListView_Contest;
        public SectionsFragment2() {
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.layout_league, container, false);
            League_League_Spinner_Do = (Spinner)rootView.findViewById(R.id.League_League_Spinner_Do);
            League_League_Spinner_Se = (Spinner)rootView.findViewById(R.id.League_League_Spinner_Se);
            League_League_Button_Search = (Button)rootView.findViewById(R.id.League_League_Button_Search);
            League_League_ListView_League= (ListView)rootView.findViewById(R.id.League_League_ListView_League);
            League_Contest_ListView_Contest = (ListView)rootView.findViewById(R.id.League_Contest_ListView_Contest);

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
                        League_League_Spinner_Se .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
            }
            catch (Exception e) {
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
            }
            catch (Exception e) {
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
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return rootView;
        }
        private void contest_setData()
        {
            League_Contest_CustomList_MyData = new ArrayList<League_Contest_CustomList_MyData>();
            for(int i =0; i<contest_parsedData.length; i++) {
                League_Contest_CustomList_MyData.add(new League_Contest_CustomList_MyData(contest_parsedData[i][0],contest_parsedData[i][1],contest_parsedData[i][2],contest_parsedData[i][3]));
            }
        }
        private void league_setData()
        {
            League_League_CustomList_MyData = new ArrayList<League_League_CustomList_MyData>();
            for(int i =0; i<league_parsedData.length; i++) {
                int Rate = i+1;
                League_League_CustomList_MyData.add(new League_League_CustomList_MyData(Rate,league_parsedData[i][0],league_parsedData[i][1]));
            }
        }
        /////대회 탭  받아온 json 파싱합니다.//////////////////////////////////////////////////////////
        public String[][] contest_jsonParserList(String pRecvServerPage){
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try{
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");

                String[] jsonName = {"msg1","msg2","msg3","msg4"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];
                for(int i = 0; i<jArr.length();i++){
                    json = jArr.getJSONObject(i);
                    for (int j=0;j<jsonName.length; j++){
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
                return parseredData;
            }catch (JSONException e){
                e.printStackTrace();
                return null;
            }
        }
        /////리그  받아온 json 파싱합니다.//////////////////////////////////////////////////////////
        public String[][] league_jsonParserList(String pRecvServerPage){
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try{
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");

                String[] jsonName = {"msg1","msg2"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];
                for(int i = 0; i<jArr.length();i++){
                    json = jArr.getJSONObject(i);
                    for (int j=0;j<jsonName.length; j++){
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
                return parseredData;
            }catch (JSONException e){
                e.printStackTrace();
                return null;
            }
        }
    }
    public static class SectionsFragment3 extends Fragment {


        public SectionsFragment3() {
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.layout_league, container, false);
            return rootView;
        }
    }
    public static class SectionsFragment4 extends Fragment {
        Button Profile_Button_Name,Profile_Button_Position,Profile_Button_Age_Physical,Profile_Button_TeamName;
        Button Profile_Button_TeamMake, Profile_Button_TeamManager,Profile_Button_MyTeam ,Profile_Button_TeamSearch,Profile_Button_Logout;
        ImageView Profile_ImageVIew_Profile;
        String[][] parsedData,parsedData_overLap,parsedData_TeamCheck;
        String ProfileUrl;
        Bitmap bmImg;
        String Profile;
        final int REQ_SELECT=0;
        public SectionsFragment4() {
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            final View rootView = inflater.inflate(R.layout.layout_profile, container, false);
            Profile_Button_Name = (Button)rootView.findViewById(R.id.Profile_Button_Name);
            Profile_Button_Position = (Button)rootView.findViewById(R.id.Profile_Button_Position);
            Profile_Button_Age_Physical = (Button)rootView.findViewById(R.id.Profile_Button_Age_Physical);
            Profile_ImageVIew_Profile = (ImageView)rootView.findViewById(R.id.Profile_ImageVIew_Profile);
            Profile_Button_TeamName = (Button)rootView.findViewById(R.id.Profile_Button_TeamName);
            ///네비게이션 메뉴 선언
            Profile_Button_TeamMake = (Button)rootView.findViewById(R.id.Profile_Button_TeamMake);
            Profile_Button_TeamManager = (Button)rootView.findViewById(R.id.Profile_Button_TeamManager);
            Profile_Button_TeamSearch = (Button)rootView.findViewById(R.id.Profile_Button_TeamSearch);
            Profile_Button_MyTeam = (Button)rootView.findViewById(R.id.Profile_Button_MyTeam);
            Profile_Button_Logout = (Button)rootView.findViewById(R.id.Profile_Button_Logout);

            String result="";
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
                    result += line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            parsedData = jsonParserList_UserInfo(result);
            String Name = parsedData[0][2];
            String Position = parsedData[0][5];
            Profile =parsedData[0][7];
            String Sex = parsedData[0][2];
            String Team1 = parsedData[0][6];
            String Age = ChangeAge(parsedData[0][4]);
            Profile_Button_Name.setText(Name);
            Profile_Button_Position.setText(Position);
            Profile_Button_Age_Physical.setText(Age);
            Profile_Button_TeamName.setText(Team1);
            //유저 개인 이미지를 서버에서 받아옵니다.
            try{
                String En_Profile = URLEncoder.encode(Profile, "utf-8");
                if(Profile.equals(".")) {
                    Glide.with(rootView.getContext()).load(R.drawable.profile_basic_image).into(Profile_ImageVIew_Profile);
                }
                else{
                    Glide.with(rootView.getContext()).load("http://210.122.7.195:8080/Web_basket/imgs/Profile/" + En_Profile + ".jpg").bitmapTransform(new CropCircleTransformation(Glide.get(rootView.getContext()).getBitmapPool()))
                            .into(Profile_ImageVIew_Profile);

                }
            }
            catch (UnsupportedEncodingException e){

            }
            Profile_ImageVIew_Profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Profile.equals(".")) {
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
                                Profile_ImageVIew_Profile.setImageResource(R.drawable.profile_basic_image);
                                Profile = ".";
                                ad.dismiss();
                            }
                        });
                        Layout_CustomDialog_Album_AlbumImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
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
                            }
                        });
                    }
                }
            });
            //팀만들기 버튼 이벤트~
            Profile_Button_TeamMake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //이미 팀 존재하는 지 확인 후 중복 제거
                    String result="";
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
                    if(parsedData_overLap[0][0].equals("overLap"))
                    {
                        Snackbar.make(view,"이미 다른 팀에 가입 중이십니다.",Snackbar.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent_TeamMake = new Intent(rootView.getContext(), Navigation_TeamManager_TeamMake1.class);
                        intent_TeamMake.putExtra("Id", Id);
                        startActivity(intent_TeamMake);
                    }
                }
            });
            //팀 관리 버튼 이벤트~
            Profile_Button_TeamManager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String TeamCheck_result="";
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
                    if(parsedData_TeamCheck[0][0].equals("Unexist")){
                        Snackbar.make(view,"관리할 팀이 없습니다.",Snackbar.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent_TeamManager = new Intent(rootView.getContext(), Navigation_TeamManager.class);
                        intent_TeamManager.putExtra("Id", Id);
                        startActivity(intent_TeamManager);
                    }
                }
            });
            //팀 정보 버튼 이벤트~
            Profile_Button_MyTeam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String TeamCheck_result="";
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
                    if(parsedData_TeamCheck[0][0].equals("Unexist")){
                        Snackbar.make(view,"  팀이 존재하지 않습니다.\n  팀을 생성해주세요.",Snackbar.LENGTH_SHORT).show();
                    }
                    else {
                        final MaterialDialog TeamPlayerDialog = new MaterialDialog(getContext());
                        TeamPlayerDialog.setTitle("내 팀 정보")
                                .setPositiveButton("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        TeamPlayerDialog.dismiss();
                                    }
                                })
                                .show();
                    }
                }
            });
            //전국 팀 정보 버튼 이벤트~
            Profile_Button_TeamSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_TeamIntro = new Intent(rootView.getContext(), Navigation_TeamIntro.class);
                    intent_TeamIntro.putExtra("Id", Id);
                    startActivity(intent_TeamIntro);
                }
            });
            //로그아웃 버튼 이벤트~
            Profile_Button_Logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences prefs1 = rootView.getContext().getSharedPreferences("basketball_user", MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = prefs1.edit();
                    editor1.putString("phone", ".");
                    editor1.putString("pw", ".");
                    editor1.putString("login1", "logout");
                    editor1.commit();
                    Intent intent_Login = new Intent(rootView.getContext(), LoginActivity.class);
                    startActivity(intent_Login);
                    getActivity().finish();
                }
            });
            return rootView;
        }
        //date 입력받아 나이 구하는 함수
        public String ChangeAge(String Age){
            Calendar cal= Calendar.getInstance ();
            String[] str = new String(Age).split(" \\/ ");
            String[] str_day = new String(str[2]).split(" ");
            int year = Integer.parseInt(str[0]);
            int month = Integer.parseInt(str[1]);
            int day = Integer.parseInt(str_day[0]);

            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month-1);
            cal.set(Calendar.DATE, day);

            Calendar now = Calendar.getInstance ();

            int age = now.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
            if (  (cal.get(Calendar.MONTH) > now.get(Calendar.MONTH))
                    || (    cal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                    && cal.get(Calendar.DAY_OF_MONTH) > now.get(Calendar.DAY_OF_MONTH)   )
                    ){
                age--;
            }
            String Str_age = Integer.toString(age);
            return Str_age;
        }
        /////프로필 탭 사용자정보를 파싱합니다.//////////////////////////////////////////////////////////
        public String[][] jsonParserList_UserInfo(String pRecvServerPage){
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try{
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");

                String[] jsonName = {"msg1","msg2","msg3","msg4","msg5","msg6","msg7","msg8"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];
                for(int i = 0; i<jArr.length();i++){
                    json = jArr.getJSONObject(i);
                    for (int j=0;j<jsonName.length; j++){
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
                return parseredData;
            }catch (JSONException e){
                e.printStackTrace();
                return null;
            }
        }
        /////프로필 탭 사용자정보를 파싱합니다.//////////////////////////////////////////////////////////
        public String[][] jsonParserList_TeamMake_OverLap(String pRecvServerPage){
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try{
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");

                String[] jsonName = {"msg1"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];
                for(int i = 0; i<jArr.length();i++){
                    json = jArr.getJSONObject(i);
                    for (int j=0;j<jsonName.length; j++){
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
                return parseredData;
            }catch (JSONException e){
                e.printStackTrace();
                return null;
            }
        }
        /////팀이 존재하는지 체크합니다.
        public String[][] jsonParserList_TeamCheck(String pRecvServerPage){
            Log.i("서버에서 받은 전체 내용", pRecvServerPage);
            try{
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");

                String[] jsonName = {"msg1"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];
                for(int i = 0; i<jArr.length();i++){
                    json = jArr.getJSONObject(i);
                    for (int j=0;j<jsonName.length; j++){
                        parseredData[i][j] = json.getString(jsonName[j]);
                    }
                }
                return parseredData;
            }catch (JSONException e){
                e.printStackTrace();
                return null;
            }
        }
    }
  /*  public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Button Match_Layout_Out_Button_CheckIn = (Button)findViewById(R.id.Match_Layout_Out_Button_CheckIn);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                resultCode, data);
        String[][] parsedData;

        String str = result.getContents();
        String[] result1 = str.split("=");
        Log.i("msg",result1[1]);
        String postResult="";
        try {
            HttpClient client = new DefaultHttpClient();
            String postURL = "http://210.122.7.195:8080/Web_basket/CheckIn.jsp";
            HttpPost post = new HttpPost(postURL);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Id", Id));
            params.add(new BasicNameValuePair("CourtName", result1[1]));
            params.add(new BasicNameValuePair("Time", realTime));
            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            post.setEntity(ent);

            HttpResponse response = client.execute(post);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

            String line = null;

            while ((line = bufreader.readLine()) != null) {
                postResult += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        parsedData = jsonParserList(postResult);
        if(parsedData[0][0].equals("succed")){
            Toast.makeText(this,result1[1]+"\nCheck - In",Toast.LENGTH_SHORT).show();
            Match_Layout_Out_Button_CheckIn.setText(result1[1] + " Check - OUT");
            SharedPreferences prefs2 = getSharedPreferences("Mybasket_CheckIn", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs2.edit();
            editor.putString("status", "checking");
            editor.putString("time", realTime);
            editor.putString("court", result1[1]);
            editor.commit();
        }
        SharedPreferences prefs = getSharedPreferences("Mybasket_CheckIn", MODE_PRIVATE);
        ////////////////////////////////리스트 뷰 구현////////////////////////////////////////////////
    }*/
    public String[][] jsonParserList(String pRecvServerPage){
        Log.i("서버에서 받은 전체 내용", pRecvServerPage);
        try{
            JSONObject json = new JSONObject(pRecvServerPage);
            JSONArray jArr = json.getJSONArray("List");

            String[] jsonName = {"msg1"};
            String[][] parseredData = new String[jArr.length()][jsonName.length];
            for(int i = 0; i<jArr.length();i++){
                json = jArr.getJSONObject(i);
                for (int j=0;j<jsonName.length; j++){
                    parseredData[i][j] = json.getString(jsonName[j]);
                }
            }
            return parseredData;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

