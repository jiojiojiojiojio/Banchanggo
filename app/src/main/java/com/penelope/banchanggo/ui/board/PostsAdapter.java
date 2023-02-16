package com.penelope.banchanggo.ui.board;

import android.graphics.Bitmap;
import android.icu.number.NumberFormatter;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.penelope.banchanggo.data.post.Post;
import com.penelope.banchanggo.databinding.PostItemBinding;

import java.text.NumberFormat;
import java.util.Map;

public class PostsAdapter extends ListAdapter<Post, PostsAdapter.PostViewHolder> {

    class PostViewHolder extends RecyclerView.ViewHolder {

        private final PostItemBinding binding;

        public PostViewHolder(PostItemBinding binding) {
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
            binding.textViewPostTitle.setText(model.getTitle());
            binding.textViewPostContent.setText(model.getContent());
            binding.textViewPostAddress.setText(model.getAddress());

            String strPrice;
            if (model.getPrice() == -1) {
                strPrice = "가격 협의 가능";
            } else {
                strPrice = "$ " + NumberFormat.getInstance().format(model.getPrice());
            }
            binding.textViewPostPrice.setText(strPrice);

            int likes = model.getLikes() == null ? 0 : model.getLikes().size();
            binding.textViewLikes.setText(String.valueOf(likes));

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


    public PostsAdapter(RequestManager glide, Map<String, Bitmap> album) {
        super(new DiffUtilCallback());
        this.album = album;
        this.glide = glide;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        PostItemBinding binding = PostItemBinding.inflate(layoutInflater, parent, false);
        return new PostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
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