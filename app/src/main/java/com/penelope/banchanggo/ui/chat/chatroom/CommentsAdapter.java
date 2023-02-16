package com.penelope.banchanggo.ui.chat.chatroom;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.penelope.banchanggo.data.comment.DetailedComment;
import com.penelope.banchanggo.databinding.CommentLeftItemBinding;
import com.penelope.banchanggo.databinding.CommentRightItemBinding;
import com.penelope.banchanggo.utils.TimeUtils;

public class CommentsAdapter extends ListAdapter<DetailedComment, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LEFT = 0;
    private static final int VIEW_TYPE_RIGHT = 1;


    static class LeftCommentViewHolder extends RecyclerView.ViewHolder {

        private final CommentLeftItemBinding binding;

        public LeftCommentViewHolder(CommentLeftItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DetailedComment model) {

            // 데이터 표시
            binding.textViewCommentContents.setText(model.getContents());

            String strTime = TimeUtils.getTimeString(model.getCreated());
            binding.textViewCommentTime.setText(strTime);

            if (model.getUser() != null) {
                binding.textViewCommentName.setText(model.getUser().getName());
            } else {
                binding.textViewCommentName.setText("");
            }
        }
    }

    static class RightCommentViewHolder extends RecyclerView.ViewHolder {

        private final CommentRightItemBinding binding;

        public RightCommentViewHolder(CommentRightItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DetailedComment model) {

            binding.textViewCommentContents.setText(model.getContents());

            String strTime = TimeUtils.getTimeString(model.getCreated());
            binding.textViewCommentTime.setText(strTime);

            if (model.getUser() != null) {
                binding.textViewCommentName.setText(model.getUser().getName());
            } else {
                binding.textViewCommentName.setText("");
            }
        }
    }


    private final String userId;


    public CommentsAdapter(String userId) {
        super(new DiffUtilCallback());
        this.userId = userId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        // 뷰타입에 따라 다른 뷰홀더 생성

        if (viewType == VIEW_TYPE_LEFT) {
            CommentLeftItemBinding binding = CommentLeftItemBinding.inflate(layoutInflater, parent, false);
            return new LeftCommentViewHolder(binding);
        } else {
            CommentRightItemBinding binding = CommentRightItemBinding.inflate(layoutInflater, parent, false);
            return new RightCommentViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // 뷰타입에 따라 다른 바인드 메소드 호출

        if (holder instanceof LeftCommentViewHolder) {
            LeftCommentViewHolder viewHolder = (LeftCommentViewHolder) holder;
            viewHolder.bind(getItem(position));
        } else if (holder instanceof RightCommentViewHolder) {
            RightCommentViewHolder viewHolder = (RightCommentViewHolder) holder;
            viewHolder.bind(getItem(position));
        }
    }

    @Override
    public int getItemViewType(int position) {

        // 나/상대 여부에 따라 다른 뷰타입 할당

        DetailedComment detailedComment = getItem(position);
        if (detailedComment.getUserId().equals(userId)) {
            return VIEW_TYPE_RIGHT;
        } else {
            return VIEW_TYPE_LEFT;
        }
    }

    static class DiffUtilCallback extends DiffUtil.ItemCallback<DetailedComment> {

        @Override
        public boolean areItemsTheSame(@NonNull DetailedComment oldItem, @NonNull DetailedComment newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull DetailedComment oldItem, @NonNull DetailedComment newItem) {
            return oldItem.equals(newItem);
        }
    }

}