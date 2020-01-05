package com.xxun.watch.storytall.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xxun.watch.storytall.Const;
import com.xxun.watch.storytall.R;
import com.xxun.watch.storytall.storyUtil.StoryTellUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangjun5 on 2017/9/13.
 */

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.PlayViewHolder>{
    private Context mConText;
    private LayoutInflater mInflater;
    private List<String> mStoryPlayList = new ArrayList<String>();

    public PlayListAdapter(Context context, List<String> playlist){
        mConText = context;
        mStoryPlayList = playlist;
        mInflater = (LayoutInflater) mConText
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public PlayListAdapter.PlayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_play_list_view, null);
        return new PlayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlayListAdapter.PlayViewHolder holder,final int position) {
        String mStoryData = mStoryPlayList.get(position);
        holder.playItemName.setText(StoryTellUtil.getNameByStoryInfo(mStoryData));
        holder.playItemImage.setImageResource(R.drawable.item_delete);
        holder.playItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickLitener.onItemClick(view,position);
            }
        });
        holder.playItemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickLitener.onItemClick(view,position);
            }
        });
//        holder.playItemZone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mOnItemClickLitener.onItemClick(view,position);
//            }
//        });
        holder.playItemName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    touchX = motionEvent.getX();
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    if(Math.abs(motionEvent.getX() - touchX) < 20){
                        mOnItemClickLitener.onItemClick(view,position);
                    }else{
                        ((Activity)mConText).finish();
                    }
                    touchX = 0;
                }

                return true;
            }
        });

//        holder.playItemZone.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
//                    touchX = motionEvent.getX();
//                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
//                    if(Math.abs(motionEvent.getX() - touchX) < 20){
//                        mOnItemClickLitener.onItemClick(view,position);
//                    }else{
//                        ((Activity)mConText).finish();
//                    }
//                    touchX = 0;
//                }
//
//                return true;
//            }
//        });
    }
    private float touchX = 0;

    private Const.OnRecyclerViewItemClickListener mOnItemClickLitener;
    public void setOnItemClickLitener(Const.OnRecyclerViewItemClickListener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public int getItemCount() {
        if(mStoryPlayList != null) {
            return mStoryPlayList.size();
        }else{
            return 0;
        }
    }

    class PlayViewHolder extends RecyclerView.ViewHolder {
        ImageView playItemImage;
        TextView playItemName;
        RelativeLayout playItemZone;

        public PlayViewHolder(View itemView) {
            super(itemView);
            playItemImage = (ImageView) itemView.findViewById(R.id.iv_story_icon);
            playItemName = (TextView) itemView.findViewById(R.id.tv_story_name);
            playItemZone = (RelativeLayout) itemView.findViewById(R.id.rl_story_zone);

        }
    }
}
