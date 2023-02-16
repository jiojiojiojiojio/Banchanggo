package com.penelope.banchanggo.ui.mypage.addnotice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penelope.banchanggo.data.notice.Notice;
import com.penelope.banchanggo.data.notice.NoticeRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddNoticeViewModel extends ViewModel {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private String title;
    private String content;

    private NoticeRepository noticeRepository;


    @Inject
    public AddNoticeViewModel(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }


    public void onTitleChange(String text) {
        title = text;
    }

    public void onContentChange(String text) {
        content = text;
    }

    public void onSubmitClick() {

        if (title.isEmpty() || content.isEmpty()) {
            event.setValue(new Event.ShowGeneralMessage("모두 입력해주세요"));
            return;
        }

        Notice notice = new Notice(title, content);
        noticeRepository.addNotice(notice, unused -> event.setValue(new Event.NavigateBackWithResult()));
    }


    public static class Event {

        public static class ShowGeneralMessage extends Event {
            public final String message;

            public ShowGeneralMessage(String message) {
                this.message = message;
            }
        }

        public static class NavigateBackWithResult extends Event {

        }
    }

}