package com.thefreelancer.youni;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Kushan on 19-03-2016.
 */
public class WatchedAdapter extends RecyclerView.Adapter<WatchedAdapter.WatchedViewHolder> {


    ArrayList<String> videoIds;
    Context context;

    public WatchedAdapter(Context context, ArrayList<String> videoIds) {


        this.videoIds = videoIds;
        this.context = context;
    }

    @Override
    public WatchedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.watched_card, parent, false);


        WatchedViewHolder wh1 = new WatchedViewHolder(v);

        return wh1;
    }

    @Override
    public void onBindViewHolder(WatchedViewHolder holder, int position) {


        holder.tv1.setText(videoIds.get(position));
    }

    @Override
    public int getItemCount() {
        return videoIds.size();
    }

    class WatchedViewHolder extends RecyclerView.ViewHolder {


        TextView tv1;

        public WatchedViewHolder(View itemView) {
            super(itemView);

            tv1 = (TextView) itemView.findViewById(R.id.watchedVideosNames);


        }
    }
}
