package com.thefreelancer.youni;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoContentDetails;
import com.google.api.services.youtube.model.VideoSnippet;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

/**
 * Created by Kushan on 27-02-2016.
 */
public class PlayListCardAdapter extends RecyclerView.Adapter<PlayListCardAdapter.ViewHolder> {

    private static final DecimalFormat sFormatter = new DecimalFormat("#,###,###");
    public static Playlist mPlaylist;
    private MainActivity.LastItemReachedListener mListener;
    PlayListCardAdapter mAdapter;


    public PlayListCardAdapter(Playlist playlist, MainActivity.LastItemReachedListener mListener) {
        mPlaylist = playlist;
        this.mListener = mListener;
        this.mAdapter = this;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_card, parent, false);

        ViewHolder vh = new ViewHolder(v);


        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mPlaylist.size() == 0) {
            return;
        }


        final Video video = mPlaylist.get(position);

        final VideoSnippet videoSnippet = video.getSnippet();

        final VideoContentDetails videoContentDetails = video.getContentDetails();


        //   final VideoStatistics videoStatistics = video.getStatistics();

        holder.mTitleText.setText(videoSnippet.getTitle());
        // holder.mDescriptionText.setText(videoSnippet.getDescription());

        Picasso.with(holder.mContext)
                .load(videoSnippet.getThumbnails().getHigh().getUrl())
                .placeholder(R.drawable.video_placeholder)
                .into(holder.mThumbnailImage);
    /*    holder.mThumbnailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(),MainActivity.class);
                intent.putExtra("videoId",video.getId());
                view.getContext().startActivity(intent);

                //holder.mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video.getId())));
            }
        });*/


        //holder.mDurationText.setText(parseDuration(videoContentDetails.getDuration()));
        // set the video statistics
        //holder.mViewCountText.setText(sFormatter.format(videoStatistics.getViewCount()));
        //holder.mLikeCountText.setText(sFormatter.format(videoStatistics.getLikeCount()));
        //holder.mDislikeCountText.setText(sFormatter.format(videoStatistics.getDislikeCount()));

        if (mListener != null) {
            // get the next playlist page if we're at the end of the current page and we have another page to get
            final String nextPageToken = mPlaylist.getNextPageToken();
            if (!isEmpty(nextPageToken) && position == mPlaylist.size() - 1) {
                holder.itemView.post(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onLastItem(position, nextPageToken);
                    }
                });
            }


        }


    }

    @Override
    public int getItemCount() {
        return mPlaylist.size();
    }

    private boolean isEmpty(String s) {
        if (s == null || s.length() == 0) {
            return true;
        }
        return false;
    }

    private String parseDuration(String in) {
        boolean hasSeconds = in.indexOf('S') > 0;
        boolean hasMinutes = in.indexOf('M') > 0;

        String s;
        if (hasSeconds) {
            s = in.substring(2, in.length() - 1);
        } else {
            s = in.substring(2, in.length());
        }

        String minutes = "0";
        String seconds = "00";

        if (hasMinutes && hasSeconds) {
            String[] split = s.split("M");
            minutes = split[0];
            seconds = split[1];
        } else if (hasMinutes) {
            minutes = s.substring(0, s.indexOf('M'));
        } else if (hasSeconds) {
            seconds = s;
        }

        // pad seconds with a 0 if less than 2 digits
        if (seconds.length() == 1) {
            seconds = "0" + seconds;
        }

        return minutes + ":" + seconds;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public final Context mContext;
        public final TextView mTitleText;
        //  public final TextView mDescriptionText;
        public final ImageView mThumbnailImage;

        //public final ImageView mShareIcon;
        //public final TextView mShareText;
        //public final TextView mDurationText;
        //public final TextView mViewCountText;
        //public final TextView mLikeCountText;
        //public final TextView mDislikeCountText;

        CardView cv1;

        public ViewHolder(View v) {
            super(v);
            mContext = v.getContext();
            mTitleText = (TextView) v.findViewById(R.id.video_title);
            //  mDescriptionText = (TextView) v.findViewById(R.id.video_description);
            mThumbnailImage = (ImageView) v.findViewById(R.id.video_thumbnail);

            cv1 = (CardView) itemView.findViewById(R.id.card_view);
            // mShareIcon = (ImageView) v.findViewById(R.id.video_share);
            //mShareText = (TextView) v.findViewById(R.id.video_share_text);
            //mDurationText = (TextView) v.findViewById(R.id.video_dutation_text);
            //mViewCountText = (TextView) v.findViewById(R.id.video_view_count);
            //mLikeCountText = (TextView) v.findViewById(R.id.video_like_count);
            //mDislikeCountText = (TextView) v.findViewById(R.id.video_dislike_count);

        }


    }
}



