package com.penelope.banchanggo.ui.mypage.mypage;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.penelope.banchanggo.data.notice.Notice;
import com.penelope.banchanggo.databinding.NoticeItemBinding;
import com.penelope.banchanggo.utils.TimeUtils;

public class NoticesAdapter extends ListAdapter<Notice, NoticesAdapter.NoticeViewHolder> {

    class NoticeViewHolder extends RecyclerView.ViewHolder {

        private final NoticeItemBinding binding;

        public NoticeViewHolder(NoticeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(position);
                }
            });
        }

        public void bind(Notice model) {
            binding.textViewNoticeTitle.setText(model.getTitle());
            binding.textViewSummary.setText(model.getContent());

            String strDate = TimeUtils.getDateString(model.getCreated());
            binding.textViewNoticeDate.setText(strDate);
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    private OnItemSelectedListener onItemSelectedListener;


    public NoticesAdapter() {
        super(new DiffUtilCallback());
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener;
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        NoticeItemBinding binding = NoticeItemBinding.inflate(layoutInflater, parent, false);
        return new NoticeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    static class DiffUtilCallback extends DiffUtil.ItemCallback<Notice> {

        @Override
        public boolean areItemsTheSame(@NonNull Notice oldItem, @NonNull Notice newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Notice oldItem, @NonNull Notice newItem) {
            return oldItem.equals(newItem);
        }
    }

}