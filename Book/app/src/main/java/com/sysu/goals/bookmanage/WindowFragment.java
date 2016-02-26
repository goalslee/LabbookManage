package com.sysu.goals.bookmanage;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.sysu.goals.bookmanage.http.httpclient;


/**
 * A simple {@link Fragment} subclass.
 */
public class WindowFragment extends DialogFragment {

    private String ISBN;
    private String id;
    public WindowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ISBN = getArguments().getString("isbn");
        }
        SharedPreferences sp = getActivity().getSharedPreferences("save",
                Activity.MODE_PRIVATE);
        id=sp.getString("id", null);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view= inflater.inflate(R.layout.fragment_window, container, false);
        Button fav=(Button)view.findViewById(R.id.btu1);
        Button rec=(Button)view.findViewById(R.id.btu2);

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sql1="insert into favourite(stu_name,isbn13) values(\'";
                sql1=sql1+id;
                sql1=sql1+"\',\'";
                sql1=sql1+ISBN;
                final String sql=sql1+"\')";
                new Thread() {
                    public void run() {
                        //httpclient http = new httpclient();
                        httpclient.connect_insert(sql);
                    }
                }.start();
                Toast.makeText(getActivity(), "Favourite succeed!", Toast.LENGTH_LONG).show();
                dismiss();
            }
        });

        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        //httpclient http = new httpclient();
                        httpclient.connect_recommend(ISBN);
                    }
                }.start();
                Toast.makeText(getActivity(), "Recommend succeed!", Toast.LENGTH_LONG).show();
                dismiss();
            }
        });

        return view;
    }


}
