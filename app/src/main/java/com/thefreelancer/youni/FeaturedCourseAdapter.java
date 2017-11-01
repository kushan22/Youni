package com.thefreelancer.youni;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Kushan on 02-04-2016.
 */
public class FeaturedCourseAdapter extends RecyclerView.Adapter<FeaturedCourseAdapter.FeaturedHolder> {

    String[] topicNames;
    String[] playListsIds;
    Context context;
    private int lastPosition = -1;

    public FeaturedCourseAdapter(Context context, String[] topicNames, String[] playListsIds) {

        this.topicNames = topicNames;
        this.playListsIds = playListsIds;
        this.context = context;

    }

    @Override
    public FeaturedHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.featured_card, parent, false);

        FeaturedHolder fh = new FeaturedHolder(v);

        return fh;
    }

    @Override
    public void onBindViewHolder(FeaturedHolder holder, int position) {

        holder.tv1.setText(topicNames[position]);

        Animation animate = AnimationUtils.loadAnimation(holder.itemView.getContext(), position > lastPosition ? R.anim.animate : R.anim.animate1);
        holder.itemView.startAnimation(animate);
        lastPosition = position;

        switch (position) {

            case 0:

                holder.rl1.setBackgroundColor(Color.rgb(0, 131, 143));

                break;
            case 1:

                holder.rl1.setBackgroundColor(Color.rgb(233, 30, 99));

                break;

            case 2:

                holder.rl1.setBackgroundColor(Color.rgb(156, 204, 101));

                break;

            case 3:

                holder.rl1.setBackgroundColor(Color.rgb(251, 192, 45));
                break;

            case 4:

                holder.rl1.setBackgroundColor(Color.rgb(142, 36, 170));

                break;


        }


    }

    @Override
    public int getItemCount() {
        return topicNames.length;
    }

    class FeaturedHolder extends RecyclerView.ViewHolder {


        TextView tv1;
        RelativeLayout rl1;


        public FeaturedHolder(View itemView) {
            super(itemView);

            tv1 = (TextView) itemView.findViewById(R.id.featuredCourseName);
            rl1 = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);

        }
    }

}
