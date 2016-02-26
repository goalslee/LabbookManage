package com.sysu.goals.bookmanage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sysu.goals.bookmanage.http.httpclient;
import com.sysu.goals.bookmanage.scanner.CaptureActivity;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public String ISBN=null;
    public ListView lv;
    private View mProgressView;
    private long cli_time=0;
    private String NAME;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg){
            if(msg.what==3){
                String json=(String)msg.obj;
                setPersonFragment(json);
            }
            else if(msg.what==1){//for all book funtion
            String json=(String)msg.obj;
                try{
                JSONArray ja=new JSONArray(json);
                String count_temp=ja.getJSONObject(0).getString("count(*)");
                    int count = Integer.parseInt(count_temp);

                    final int cut=count/20;
                    new Thread() {
                        public void run() {
                            httpclient http = new httpclient();
                            http.connect_cut("select * from book limit ", mHandler, 5, cut);
                        }
                    }.start();
                }
                catch (Exception e){}
            //setDefaultFragment(json);
            }
            else if(msg.what==2){
                Bundle bundle=(Bundle)msg.obj;
                setRecFragment(bundle);
            }
            else if(msg.what==4){//for recommend function


                String json=(String)msg.obj;
                try{
                    JSONArray ja=new JSONArray(json);
                    String count_temp=ja.getJSONObject(0).getString("count(*)");
                    int count = Integer.parseInt(count_temp);

                    final int cut=count/20;
                    new Thread() {
                        public void run() {
                            httpclient http = new httpclient();
                            http.connect_cut("select recommend.count,book.ISBN13,book.title,book.author from book INNER JOIN recommend on recommend.isbn13=book.ISBN13 ORDER BY count DESC limit ", mHandler, 2, cut);
                        }
                    }.start();
                }
                catch (Exception e){}

            }
            else if(msg.what==5){
                Bundle bundle=(Bundle)msg.obj;
                setDefaultFragment(bundle);
            }
            else if(msg.what==6){//for favourite function
                String json=(String)msg.obj;
                try{
                    JSONArray ja=new JSONArray(json);
                    String count_temp=ja.getJSONObject(0).getString("count(*)");
                    int count = Integer.parseInt(count_temp);

                    final int cut=count/20;

                    String temp="select book.ISBN13,book.title,book.author from book INNER JOIN favourite on favourite.isbn13=book.ISBN13 where favourite.stu_name=\'";
                    temp=temp+NAME;
                    final String sql=temp+"\' limit ";

                    new Thread() {
                        public void run() {
                            httpclient http = new httpclient();
                            http.connect_cut(sql, mHandler,5, cut);
                        }
                    }.start();
                }
                catch (Exception e){}
            }


        }
    };
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressView=findViewById(R.id.main_progress);
        SharedPreferences sp = getSharedPreferences("save",
                Activity.MODE_PRIVATE);
        NAME=sp.getString("id", null);
        Log.v("jj", NAME);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView name=(TextView)navigationView.inflateHeaderView(R.layout.nav_header_main).findViewById(R.id.name);
        name.setText(NAME);

        showProgress(true);
//获取全部书籍信息并存在arraylist中传给mainfragment

        new Thread() {
            public void run() {
                httpclient http = new httpclient();
                http.connect_all("select count(*) from book",mHandler,1);
            }
        }.start();
       //setDefaultFragment();
    }

    private void setDefaultFragment(Bundle bundle){
        Fragment main = new MainFragment();
        main.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame,main)
                .commit();
        showProgress(false);
    }

    private void setRecFragment(Bundle bundle){
        Fragment rec = new RecFragment();
        rec.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame,rec)
                .commit();
        showProgress(false);
    }

    private void setPersonFragment(String data){
        Fragment main = new PersonFragment();
        Bundle arg = new Bundle();
        arg.putString("info",data);
        main.setArguments(arg);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame,main)
                .commit();
        showProgress(false);
    }

    @Override
    public void onBackPressed() {
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
            if(System.currentTimeMillis()-cli_time>2000) {
                cli_time=System.currentTimeMillis();
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            }else {
                super.onBackPressed();
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            startActivityForResult(new Intent(this, CaptureActivity.class), 0);

            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

            startActivity(new Intent(this,MainActivity.class));
            finish();

        }
        else if (id == R.id.Book_Per_Person) {
            showProgress(true);
            new Thread() {
                public void run() {
                    httpclient http = new httpclient();
                    http.connect_all("select * from student",mHandler,3);
                }
            }.start();

        }
         else if (id == R.id.Book_favourite) {
            showProgress(true);
            //String temp="select book.ISBN13,book.title,book.author from book INNER JOIN favourite on favourite.isbn13=book.ISBN13 where favourite.stu_name=\'";
            String temp="select count(*) from book INNER JOIN favourite on favourite.isbn13=book.ISBN13 where favourite.stu_name=\'";
            temp=temp+NAME;
            final String sql=temp+"\'";
            new Thread() {
                public void run() {
                    httpclient http = new httpclient();
                    http.connect_all(sql,mHandler,6);
                }
            }.start();
        }
        else if (id == R.id.Book_recommend) {
            showProgress(true);
            //final String sql="select recommend.count,book.ISBN13,book.title,book.author from book INNER JOIN recommend on recommend.isbn13=book.ISBN13 ORDER BY count DESC";
            new Thread() {
                public void run() {
                    httpclient http = new httpclient();
                    http.connect_all("select count(*) from book INNER JOIN recommend on recommend.isbn13=book.ISBN13",mHandler,4);
                    //http.connect_all(sql,mHandler,4);
                }
            }.start();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        ISBN=null;
         try {
             Bundle a = data.getExtras();
             ISBN = a.getString("ISBN");

         }catch (Exception a){
             ISBN=null;
         }
        if(ISBN!=null){
            Fragment scan = new ScanFragment();
            Bundle arg = new Bundle();
            arg.putString("ISBN",ISBN);
            FragmentManager fragmentManager = getFragmentManager();
            scan.setArguments(arg);
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame,scan)
                    .commit();
        }
    }


}
