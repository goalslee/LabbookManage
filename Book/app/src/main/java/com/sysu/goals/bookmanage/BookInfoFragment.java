package com.sysu.goals.bookmanage;



import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.Handler;
import android.util.Log;
import android.os.Message;
import android.widget.Toast;

import com.sysu.goals.bookmanage.http.httpclient;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    ImageView imView;
    Bitmap bmImg;
    Button submit;
    EditText Position;
    EditText Remark;
    EditText Lab2;
    TextView ISBN;
    TextView Author;
    TextView Title;
    TextView Publish;
    TextView Holder;
    TextView Position1;
    TextView Remark1;
    TextView Pages;
    TextView Lab;

    // TODO: Rename and change types of parameters
    private String json;
    private String id;
    private String isbn10;
    private String isbn13;
    private String title;
    private String image;
    private String author;
    private String translator;
    private String publisher;
    private String pub_date;
    private String pages;
    private String position;
    private String remark;
    private String holder;
    private String lab;
    private String lab2;




    private OnFragmentInteractionListener mListener;



    public BookInfoFragment() {
        // Required empty public constructor
    }

    private Handler mHandler = new Handler() {
            public void handleMessage(Message msg){
                if(msg.what==1){
                imView.setImageBitmap((Bitmap) msg.obj);}
                if(msg.what==2){
                    final String sql=(String)msg.obj;
                    new Thread() {
                        public void run() {
                            //httpclient http = new httpclient();
                            httpclient.connect_insert(sql);
                        }
                    }.start();

                }
            }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.v("jj_create", "ok");
        if (getArguments() != null) {
            Bundle arg=new Bundle();
            arg = getArguments().getBundle(ARG_PARAM1);
            json=arg.getString("1");
           try{
               JSONArray ja=new JSONArray(json);
               //JSONObject ob=new JSONObject(json); //最外层是对象用这个
               //id=ja.getJSONObject(0).getString("ID");//数组只有一个对象
               isbn13=ja.getJSONObject(0).getString("ISBN13");
               title=ja.getJSONObject(0).getString("title");
               image=ja.getJSONObject(0).getString("image");
               author=ja.getJSONObject(0).getString("author");
               pages=ja.getJSONObject(0).getString("pages");
               publisher=ja.getJSONObject(0).getString("publisher");

           }
           catch (Exception e){};
           json=arg.getString("2");
            if(!json.equals("0")){
                try{
                    JSONArray jb=new JSONArray(json);
                    position=jb.getJSONObject(0).getString("position");//数组只有一个对象
                    remark=jb.getJSONObject(0).getString("remark");
                    holder=jb.getJSONObject(0).getString("stu_name");
                    lab=jb.getJSONObject(0).getString("lab");
                }
                catch (Exception e){};
            }


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bookinfo, container, false);
        imView=(ImageView)view.findViewById(R.id.imageView);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg=Message.obtain();
                msg.obj=returnBitMap(image);
                msg.what=1;
                mHandler.sendMessage(msg);
            }
        }).start();
        Position=(EditText)view.findViewById(R.id.Position);
        Remark=(EditText)view.findViewById(R.id.Remark);
        submit=(Button)view.findViewById(R.id.submit_button);
        Holder=(TextView)view.findViewById(R.id.Holder);
        Lab2=(EditText)view.findViewById(R.id.Lab);

        //String sql="";
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sql="";
                position=Position.getText().toString();
                lab2=Lab2.getText().toString();
                if(position.equals("")||lab2.equals("")) {

                    return;
                }
                else {
                    holder=Holder.getText().toString();
                    remark=Remark.getText().toString();
                    SharedPreferences sp = getActivity().getSharedPreferences("save",
                            Activity.MODE_PRIVATE);
                    String id=sp.getString("id", null);
                    if(holder.equals("")){
                        sql="insert connect(stu_name,isbn13,lab,position,remark) values(";
                        sql=sql+"\'";
                        sql=sql+id;
                        sql=sql+"\',\'";
                        sql=sql+isbn13;
                        sql=sql+"\',\'";
                        sql=sql+lab2;
                        sql=sql+"\',\'";
                        sql=sql+position;
                        sql=sql+"\',\'";
                        sql=sql+remark;
                        sql=sql+"\')";
                    }
                    else {
                        sql="update connect set stu_name=\'";
                        sql=sql+id;
                        sql=sql+"\',position=\'";
                        sql=sql+position;
                        sql=sql+"\',lab=\'";
                        sql=sql+lab2;
                        sql=sql+"\',remark=\'";
                        sql=sql+remark;
                        sql=sql+"\' where isbn13=\'";
                        sql=sql+isbn13;
                        sql=sql+"\'";
                    }
                    Holder.setText(id);
                    Remark1.setText(remark);
                    Position1.setText(position);
                    Lab.setText(lab2);
                    Message msg=Message.obtain();
                    msg.obj=sql;
                    msg.what=2;
                    mHandler.sendMessage(msg);
                    Toast.makeText(getActivity(),"submit succeed!",Toast.LENGTH_LONG).show();

                }
                //send sql request
            }
        });

        ISBN=(TextView)view.findViewById(R.id.ISBN);
        Author=(TextView)view.findViewById(R.id.Author);
        Title=(TextView)view.findViewById(R.id.Title);
        Publish=(TextView)view.findViewById(R.id.Publisher);
        Position1=(TextView)view.findViewById(R.id.position1);
        Remark1=(TextView)view.findViewById(R.id.remark1);
        Pages=(TextView)view.findViewById(R.id.Pages);
        Lab=(TextView)view.findViewById(R.id.lab);



        ISBN.setText(isbn13);
        Author.setText(author);
        Title.setText(title);
        Publish.setText(publisher);
        Position1.setText(position);
        Remark1.setText(remark);
        Pages.setText(pages);
        Holder.setText(holder);
        Lab.setText(lab);
        return view;
    }


    public Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
