package com.example.instagram.Adapers;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Models.Reel;
import com.example.instagram.R;
import com.example.instagram.databinding.ReelDgBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReelAdapter extends RecyclerView.Adapter<ReelAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Reel> reelList;

    public ReelAdapter(Context context, ArrayList<Reel> reelList) {
        this.context = context;
        this.reelList = reelList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ReelDgBinding binding;

        public ViewHolder(ReelDgBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReelDgBinding binding = ReelDgBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return reelList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reel reel = reelList.get(position);

        Picasso.get().load(reel.getProfileLink())
                .placeholder(R.drawable.user)
                .into(holder.binding.profileImage);

        holder.binding.caption.setText(reel.getCaption());

        holder.binding.videoView.setVideoURI(Uri.parse(reel.getReelUrl()));
        holder.binding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                holder.binding.progressBar.setVisibility(View.GONE);
                mp.setLooping(true);
                holder.binding.videoView.start();
            }
        });

        holder.binding.videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(context, "Error playing video", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        holder.binding.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
            }
        });
    }
}
