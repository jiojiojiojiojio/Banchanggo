package com.penelope.banchanggo.data.notice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.penelope.banchanggo.data.post.Post;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class NoticeRepository {

    private final CollectionReference noticesCollection;

    @Inject
    public NoticeRepository(FirebaseFirestore firestore) {
        noticesCollection = firestore.collection("notices");
    }

    public void addNotice(Notice notice, OnSuccessListener<Void> onSuccessListener) {
        noticesCollection.document(notice.getId())
                .set(notice)
                .addOnSuccessListener(onSuccessListener);
    }

    public void getNotices(OnSuccessListener<List<Notice>> onSuccessListener, OnFailureListener onFailureListener) {
        noticesCollection.orderBy("created", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value == null || error != null) {
                        onFailureListener.onFailure(new Exception());
                        return;
                    }

                    List<Notice> noticeList = new ArrayList<>();
                    for (DocumentSnapshot snapshot : value) {
                        Notice notice = snapshot.toObject(Notice.class);
                        if (notice == null) {
                            continue;
                        }
                        noticeList.add(notice);
                    }

                    onSuccessListener.onSuccess(noticeList);
                });
    }

    public LiveData<List<Notice>> getNotices() {

        MutableLiveData<List<Notice>> notices = new MutableLiveData<>(new ArrayList<>());
        getNotices(notices::setValue, e -> notices.setValue(null));
        return notices;
    }

}
