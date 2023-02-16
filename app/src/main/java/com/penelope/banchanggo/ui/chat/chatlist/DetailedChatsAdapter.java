package com.penelope.banchanggo.ui.chat.chatlist;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.penelope.banchanggo.data.chat.DetailedChat;
import com.penelope.banchanggo.data.comment.Comment;
import com.penelope.banchanggo.data.user.User;
import com.penelope.banchanggo.databinding.ChatItemBinding;
import com.penelope.banchanggo.utils.NameUtils;
import com.penelope.banchanggo.utils.TimeUtils;

import java.util.Map;

public class DetailedChatsAdapter extends ListAdapter<DetailedChat, DetailedChatsAdapter.DetailedChatViewHolder> {

    class DetailedChatViewHolder extends RecyclerView.ViewHolder {

        private final ChatItemBinding binding;

        public DetailedChatViewHolder(ChatItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // 아이템 클릭 시 리스너 invoke

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(position);
                }
            });
        }

        public void bind(DetailedChat model) {

            // 데이터 표시

            User partner = userId.equals(model.getGuestId()) ? model.getHost() : model.getGuest();
            binding.textViewName.setText(partner.getName());

            Comment comment = model.getLastComment();
            if (comment != null) {
                binding.textViewLastMessage.setText(comment.getContents());
                binding.textViewLastTime.setText(TimeUtils.getTimeString(comment.getCreated()));
            } else {
                binding.textViewLastMessage.setText("");
                binding.textViewLastTime.setText("");
            }

            // 이미지 표시

            Bitmap image = album.get(partner.getUid());
            glide.load(image)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.imageViewProfile);
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    private OnItemSelectedListener onItemSelectedListener;
    private final RequestManager glide;

    private final String userId;
    private final Map<String, Bitmap> album;


    public DetailedChatsAdapter(String userId, Map<String, Bitmap> album, RequestManager glide) {
        super(new DiffUtilCallback());
        this.userId = userId;
        this.album = album;
        this.glide = glide;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener;
    }

    @NonNull
    @Override
    public DetailedChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // 뷰홀더 생성

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ChatItemBinding binding = ChatItemBinding.inflate(layoutInflater, parent, false);
        return new DetailedChatViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailedChatViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    static class DiffUtilCallback extends DiffUtil.ItemCallback<DetailedChat> {

        @Override
        public boolean areItemsTheSame(@NonNull DetailedChat oldItem, @NonNull DetailedChat newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull DetailedChat oldItem, @NonNull DetailedChat newItem) {
            return oldItem.equals(newItem);
        }
    }

}