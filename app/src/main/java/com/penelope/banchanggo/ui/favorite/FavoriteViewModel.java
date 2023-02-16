package com.penelope.banchanggo.ui.favorite;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.penelope.banchanggo.data.PostFilter;
import com.penelope.banchanggo.data.image.ImageRepository;
import com.penelope.banchanggo.data.post.Post;
import com.penelope.banchanggo.data.post.PostRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FavoriteViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final MutableLiveData<String> uid = new MutableLiveData<>();

    private final LiveData<List<Post>> posts;
    private final LiveData<Map<String, Bitmap>> album;

    private final MutableLiveData<PostFilter> postFilter = new MutableLiveData<>(PostFilter.RECENT);


    @Inject
    public FavoriteViewModel(PostRepository postRepository, ImageRepository imageRepository) {

        posts = Transformations.switchMap(uid, uidValue ->
                Transformations.switchMap(postFilter, filter -> {
                    switch (filter) {
                        case HIGH_PRICE:
                            return postRepository.getPostsLikedByInPriceOrder(uidValue, true);
                        case LOW_PRICE:
                            return postRepository.getPostsLikedByInPriceOrder(uidValue, false);
                        default:
                            return postRepository.getPostsLikedBy(uidValue);
                    }
                })
        );

        album = Transformations.switchMap(posts, postList -> {
            List<String> postIdList = postList.stream().map(Post::getId).collect(Collectors.toList());
            return imageRepository.getPostAlbum(postIdList);
        });
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<List<Post>> getPosts() {
        return posts;
    }

    public LiveData<Map<String, Bitmap>> getAlbum() {
        return album;
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            event.setValue(new Event.NavigateBack());
        } else {
            uid.setValue(firebaseAuth.getUid());
        }
    }

    public void onPostFilterSelected(int index) {
        postFilter.setValue(PostFilter.values()[index]);
    }

    public void onPostClick(Post post) {
        event.setValue(new Event.NavigateToPostScreen(post));
    }


    public static class Event {

        public static class NavigateBack extends Event {
        }

        public static class NavigateToPostScreen extends Event {
            public final Post post;

            public NavigateToPostScreen(Post post) {
                this.post = post;
            }
        }
    }

}