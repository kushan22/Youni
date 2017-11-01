package com.youni.youni;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class Featuredcourses extends Fragment {
    
    RecyclerView rv1;
    LinearLayoutManager linearLayoutManager;
    FeaturedCourseAdapter mAdapter;
    String[] playListsIds = new String[] {"PLEbnTDJUr_IcPtUXFy2b1sGRPsLFMghhS","PLTZbNwgO5ebqnympIYe2GX4hjjsS9Psdm","PL6EE0CD02910E57B8","PL6gx4Cwl9DGBpuvPW0aHa7mKdn_k9SPKO","PL0E131A78ABFBFDD0"};
    String[] topicNames = new String[] {"Compiler Design By Ravindrababu Ravula","Operating Systems by Saurabh School","Computer Science - Artificial Intelligence - Prof. P. Dasgupta By nptelhrd","Computer Networks By saurabhschool","Microprocessors & Microcontrollers by Prof. Ajit Pal By Satish Kashyap"};


    public Featuredcourses() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_featuredcourses,container,false);

        rv1 = (RecyclerView) v.findViewById(R.id.featuredCourses);
        rv1.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rv1.setLayoutManager(linearLayoutManager);
        mAdapter = new FeaturedCourseAdapter(getContext(),topicNames,playListsIds);
        rv1.setAdapter(mAdapter);

       rv1.addOnItemTouchListener(new RecyclerItemListener(getContext(), new RecyclerItemListener.OnItemClickListener() {
           @Override
           public void onItemClick(View view, int position) {


               Intent playIntent = new Intent(getContext(), CourseSpecs.class);
               playIntent.putExtra("playlistid", playListsIds[position]);
               playIntent.putExtra("topicName", topicNames[position]);
               startActivity(playIntent);

           }
       }));

        return v;
    }

}
