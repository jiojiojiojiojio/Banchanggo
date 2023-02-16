package com.penelope.banchanggo.ui.board;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.penelope.banchanggo.data.post.Post;
import com.penelope.banchanggo.databinding.PostImageItemBinding;

import java.util.Map;

public class PostImagesAdapter extends ListAdapter<Post, PostImagesAdapter.PostImageViewHolder> {

    class PostImageViewHolder extends RecyclerView.ViewHolder {

        private final PostImageItemBinding binding;

        public PostImageViewHolder(PostImageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(position);
                }
            });
        }

        public void bind(Post model) {

            Bitmap image = album.get(model.getId());
            if (image != null) {
                glide.load(image).into(binding.imageViewPost);
            } else {
                binding.imageViewPost.setImageBitmap(null);
                glide.clear(binding.imageViewPost);
            }
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    private OnItemSelectedListener onItemSelectedListener;
    private final Map<String, Bitmap> album;
    private final RequestManager glide;


    public PostImagesAdapter(RequestManager glide, Map<String, Bitmap> album) {
        super(new PostsAdapter.DiffUtilCallback());
        this.album = album;
        this.glide = glide;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener;
    }

    @NonNull
    @Override
    public PostImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        PostImageItemBinding binding = PostImageItemBinding.inflate(layoutInflater, parent, false);
        return new PostImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostImageViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    static class DiffUtilCallback extends DiffUtil.ItemCallback<Post> {

        @Override
        public boolean areItemsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            return oldItem.equals(newItem);
        }
    }

}