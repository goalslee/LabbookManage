package com.sysu.goals.bookmanage;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sysu.goals.bookmanage.http.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.LogRecord;

/**
 * Created by goals on 2015/10/29.
 */


public class ScanFragment extends Fragment {
    private String mISBN;
    HttpURLConnection conn = null;

    Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg){
            String info1;
            String info2;
            if(msg.what==1){
                info1=(String)msg.obj;
                query_connect(info1);
            }
            if(msg.what==2){
                Bundle arg = new Bundle();
            arg=(Bundle)msg.obj;

            Fragment book = new BookInfoFragment();
            Bundle arg1 = new Bundle();
                arg1.putBundle("param1",arg);
            //arg.putString("param1",info1);
               // arg.putString("param1",info2);
            FragmentManager fragmentManager = getFragmentManager();
            book.setArguments(arg1);
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame,book)
                    .commit();}

        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle =getArguments();
        if(bundle!=null) mISBN=bundle.getString("ISBN");
        Toast.makeText(getActivity(),
                "ISBN:" + mISBN,
                Toast.LENGTH_SHORT).show();



    }

   /* @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.capture,container,false);
        return  rootView;
    }*/

    @Override
    public void onResume() {
        super.onResume();

        new Thread() {
            public void run() {
                //httpclient http = new httpclient();
                httpclient.connect_isbn(mISBN,handler);
            }
        }.start();

    }

    private void query_connect(String info1){
        final String info=info1;
        new Thread() {
            public void run() {
                //httpclient http = new httpclient();
                String sql="select * from connect where isbn13=\'";
                sql=sql+mISBN;
                sql=sql+"\'";
                httpclient.connect_book(info, sql, handler);
            }
        }.start();
    }

}
