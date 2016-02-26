package com.sysu.goals.bookmanage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sysu.goals.bookmanage.http.httpclient;
import com.sysu.goals.bookmanage.scanner.Intents;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Map<String,Object> item;
    ListView lv;
    private View mProgressView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PersonFragment() {
        // Required empty public constructor
    }

    String id;
    Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg){
            if(msg.what==1) {
                Bundle bundle = (Bundle) msg.obj;
                Fragment main = new MainFragment();
                FragmentManager fragmentManager = getFragmentManager();
                main.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, main)
                        .commit();
                showProgress(false);
            }
            else if(msg.what==2){

                String json=(String)msg.obj;
                try{
                    JSONArray ja=new JSONArray(json);
                    String count_temp=ja.getJSONObject(0).getString("count(*)");
                    int count = Integer.parseInt(count_temp);

                    final int cut=count/20;

                    final String stu_name=(String)item.get("ID");
                    final String tmp="select book.ISBN13,book.title,book.author from book INNER JOIN connect on book.ISBN13=connect.isbn13 where connect.stu_name=\'";
                    final String tmp2=tmp+stu_name;
                    final String sql=tmp2+"\' limit ";

                    new Thread() {
                        public void run() {
                            httpclient http = new httpclient();
                            http.connect_cut(sql, handler, 1, cut);
                        }
                    }.start();
                }
                catch (Exception e){}
            }

        }
    };
    private void query_connect(String info1,String isbn){
        final String info=info1;
        final String Isbn=isbn;
        new Thread() {
            public void run() {
                //httpclient http = new httpclient();
                String sql="select * from connect where isbn13=\'";
                sql=sql+Isbn;
                sql=sql+"\'";
                httpclient.connect_book(info, sql, handler);
            }
        }.start();
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            lv.setVisibility(show ? View.GONE : View.VISIBLE);
            lv.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    lv.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            lv.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    ArrayList<Map<String,Object>> data=new ArrayList<Map<String,Object>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString("info");
            // mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_person, container, false);
        mProgressView = view.findViewById(R.id.progress);
        lv = (ListView)view.findViewById(R.id.lv);
        try{
            JSONArray ja=new JSONArray(mParam1);

            Map<String,Object> item;

            for(int i=0;i<ja.length();i++){
                item=new HashMap<String, Object>();
                item.put("ID",ja.getJSONObject(i).getString("stu_name"));
                data.add(item);
            }
        }
        catch (Exception e){}

        String[] ColumnNames = { "ID"};
        ListAdapter adapter = new MySimpleCursorAdapter(getActivity(),data,
                R.layout.person_layout, ColumnNames, new int[] { R.id.id,});
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//position是第几个行
                showProgress(true);
                item=(Map<String,Object>)lv.getItemAtPosition(position);
                final String stu_name=(String)item.get("ID");
                //final String tmp="select book.ISBN13,book.title,book.author from book INNER JOIN connect on book.ISBN13=connect.isbn13 where connect.stu_name=\'";
                final String tmp="select count(*) from book INNER JOIN connect on book.ISBN13=connect.isbn13 where connect.stu_name=\'";
                final String tmp2=tmp+stu_name;
                final String sql=tmp2+"\'";
                //final String sql="select book.ISBN13,book.title,book.author from book INNER JOIN connect on book.ISBN13=connect.isbn13";
                new Thread() {
                    public void run() {
                        //httpclient http = new httpclient();
                        httpclient.connect_all(sql,handler,2);
                    }
                }.start();
                //Toast.makeText(getActivity(),ISBN,Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

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
