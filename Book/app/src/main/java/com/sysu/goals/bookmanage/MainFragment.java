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
import java.util.Set;
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
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ListView lv;
    private View mProgressView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Set<String> keyset;
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

    public MainFragment() {
        // Required empty public constructor
    }

    String id;
    Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg){
            String info1;
            String info2;
            if(msg.what==1){
                info1=(String)msg.obj;
                String isbn13;
                try{
                JSONArray ja=new JSONArray(info1);
                isbn13=ja.getJSONObject(0).getString("ISBN13");
                    query_connect(info1,isbn13);}
                catch (Exception e){}

            }
            if(msg.what==2){
                Bundle arg = (Bundle)msg.obj;
                //arg=(Bundle)msg.obj;

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
            Bundle bundle=getArguments();
             keyset= getArguments().keySet();
           // mParam2 = getArguments().getString(ARG_PARAM2);
            int a=0;
            for(String key:keyset) {
                try {
                    //JSONArray ja = new JSONArray(mParam1);
                    String json=(String)bundle.get(key);
                    JSONArray ja = new JSONArray(json);
                    Map<String, Object> item;

                    for (int i = 0; i < ja.length(); i++) {
                        item = new HashMap<String, Object>();
                        item.put("ID", i + 1+20*a);
                        item.put("ISBN", ja.getJSONObject(i).getString("ISBN13"));
                        item.put("Title", ja.getJSONObject(i).getString("title"));
                        item.put("Author", ja.getJSONObject(i).getString("author"));
                        data.add(item);
                    }
                } catch (Exception e) {
                }
                a++;
            }
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_item_lv, container, false);
        mProgressView = view.findViewById(R.id.progress);
        lv = (ListView)view.findViewById(R.id.lv);



        String[] ColumnNames = { "ID", "ISBN", "Title", "Author"};
        ListAdapter adapter = new MySimpleCursorAdapter(getActivity(),data,
                R.layout.listview_layout, ColumnNames, new int[] { R.id.id,
                R.id.isbn,  R.id.title,  R.id.author});
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//position是第几个行
                showProgress(true);
                Map<String, Object> item = (Map<String, Object>) lv.getItemAtPosition(position);
                final String ISBN = (String) item.get("ISBN");
                new Thread() {
                    public void run() {
                        //httpclient http = new httpclient();
                        httpclient.connect_isbn(ISBN, handler);
                    }
                }.start();
                //Toast.makeText(getActivity(),ISBN,Toast.LENGTH_SHORT).show();
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {//position是第几个行
                Map<String, Object> item = (Map<String, Object>) lv.getItemAtPosition(position);
                final String ISBN = (String) item.get("ISBN");
                FragmentManager fm = getFragmentManager();
                Bundle args = new Bundle();
                args.putString("isbn", ISBN);
                WindowFragment wf = new WindowFragment();
                wf.setArguments(args);
                wf.show(fm, "good");
                //Toast.makeText(getActivity(),ISBN,Toast.LENGTH_SHORT).show();
                return true;
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
