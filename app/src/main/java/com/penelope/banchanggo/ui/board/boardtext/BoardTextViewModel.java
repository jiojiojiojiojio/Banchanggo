package com.penelope.banchanggo.ui.board.boardtext;

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
public class BoardTextViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final LiveData<List<Post>> posts;
    private final LiveData<Map<String, Bitmap>> album;

    private final MutableLiveData<PostFilter> postFilter = new MutableLiveData<>(PostFilter.RECENT);


    @Inject
    public BoardTextViewModel(PostRepository postRepository, ImageRepository imageRepository) {

        posts = Transformations.switchMap(postFilter, filter -> {
            switch (filter) {
                case HIGH_PRICE: return postRepository.getPostsInPriceOrder(true);
                case LOW_PRICE: return postRepository.getPostsInPriceOrder(false);
                default: return postRepository.getPosts();
            }
        });

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
        }
    }

    public void onPostFilterSelected(int index) {
        postFilter.setValue(PostFilter.values()[index]);
    }

    public void onPostClick(Post post) {
        event.setValue(new Event.NavigateToPostScreen(post));
    }

    public void onImageTypeClick() {
        event.setValue(new Event.NavigateBack());
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