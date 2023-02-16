package com.penelope.banchanggo.data.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class ImageRepository {

    private static final int MEGABYTES = 1024 * 1024;

    private final StorageReference profileImagesReference;
    private final StorageReference postImagesReference;

    @Inject
    public ImageRepository(StorageReference storageReference) {
        profileImagesReference = storageReference.child("profiles");
        postImagesReference = storageReference.child("posts");
    }

    public void getProfileImage(String uid,
                                OnSuccessListener<Bitmap> onSuccessListener,
                                OnFailureListener onFailureListener
    ) {
        // uid 로 검색된 이미지 획득

        profileImagesReference.listAll()
                .addOnSuccessListener(listResult -> {
                    List<StorageReference> items = listResult.getItems();
                    for (StorageReference item : items) {
                        if (item.getName().contains(uid)) {
                            profileImagesReference.child(uid + ".jpg")
                                    .getBytes(10 * MEGABYTES)
                                    .addOnSuccessListener(bytes -> {
                                        Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        onSuccessListener.onSuccess(image);
                                    })
                                    .addOnFailureListener(onFailureListener);
                        }
                    }
                });
    }

    public LiveData<Bitmap> getProfileImageLiveData(String uid) {

        // getProfileImage 의 LiveData 버전

        MutableLiveData<Bitmap> bitmap = new MutableLiveData<>();
        getProfileImage(uid, bitmap::setValue, e -> bitmap.setValue(null));
        return bitmap;
    }

    public void setProfileImage(String uid, Bitmap bitmap,
                                OnSuccessListener<Void> onSuccessListener,
                                OnFailureListener onFailureListener
    ) {

        // uid 유저의 프로필 이미지 설정

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        profileImagesReference.child(uid + ".jpg")
                .putBytes(data)
                .addOnSuccessListener(taskSnapshot -> onSuccessListener.onSuccess(null))
                .addOnFailureListener(onFailureListener);
    }


    public LiveData<Map<String, Bitmap>> getAlbum(List<String> idList) {

        // idList 에 포함된 유저들의 프로필 이미지 획득

        MutableLiveData<Map<String, Bitmap>> album = new MutableLiveData<>(new HashMap<>());

        for (String id : idList) {
            getProfileImage(id,
                    bitmap -> {
                        if (bitmap != null) {
                            Map<String, Bitmap> oldMap = album.getValue();
                            assert oldMap != null;
                            Map<String, Bitmap> map = new HashMap<>(oldMap);
                            map.put(id, bitmap);
                            album.setValue(map);
                        }
                    },
                    e -> {
                    }
            );
        }

        return album;
    }


    // DB 에 글의 이미지 (비트맵) 를 추가하는 메소드

    public void addPostImage(String postId, Bitmap bitmap,
                               OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        postImagesReference.child(postId + ".jpg")
                .putBytes(data)
                .addOnSuccessListener(taskSnapshot -> onSuccessListener.onSuccess(null))
                .addOnFailureListener(onFailureListener);
    }

    // DB 에서 특정 글의 이미지 (비트맵) 을 가져오는 메소드

    public void getPostImage(String postId,
                             OnSuccessListener<Bitmap> onSuccessListener,
                             OnFailureListener onFailureListener
    ) {
        postImagesReference.child(postId + ".jpg")
                .getBytes(10 * MEGABYTES)
                .addOnSuccessListener(bytes -> {
                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    onSuccessListener.onSuccess(image);
                })
                .addOnFailureListener(onFailureListener);
    }

    public LiveData<Bitmap> getPostImageLiveData(String postId) {

        MutableLiveData<Bitmap> bitmap = new MutableLiveData<>();
        getPostImage(postId, bitmap::setValue, e -> bitmap.setValue(null));
        return bitmap;
    }

    // DB 에서 주어진 글들의 대표 이미지들을 (글 아이디 : 비트맵) 형식의 맵으로 가져오는 메소드

    public LiveData<Map<String, Bitmap>> getPostAlbum(List<String> postIdList) {

        MutableLiveData<Map<String, Bitmap>> album = new MutableLiveData<>(new HashMap<>());

        for (String postId : postIdList) {
            getPostImage(postId,
                    bitmap -> {
                        if (bitmap != null) {
                            Map<String, Bitmap> oldMap = album.getValue();
                            assert oldMap != null;
                            Map<String, Bitmap> map = new HashMap<>(oldMap);
                            map.put(postId, bitmap);
                            album.setValue(map);
                        }
                    },
                    e -> { }
            );
        }

        return album;
    }

}






