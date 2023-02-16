package com.penelope.banchanggo.ui.board.boardimage;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.penelope.banchanggo.data.image.ImageRepository;
import com.penelope.banchanggo.data.post.Post;
import com.penelope.banchanggo.data.post.PostRepository;
import com.penelope.banchanggo.ui.board.boardtext.BoardTextViewModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BoardImageViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final MutableLiveData<String> addressQuery = new MutableLiveData<>("");


    private final LiveData<List<Post>> postsRealtime;
    private final LiveData<Map<String, Bitmap>> albumRealtime;

    private final LiveData<List<Post>> postsPopular;
    private final LiveData<Map<String, Bitmap>> albumPopular;


    @Inject
    public BoardImageViewModel(PostRepository postRepository, ImageRepository imageRepository) {

        postsRealtime = Transformations.switchMap(addressQuery, postRepository::queryPostsByAddress);
        postsPopular = Transformations.switchMap(addressQuery, postRepository::queryPopularPostsByAddress);

        albumRealtime = Transformations.switchMap(postsRealtime, postList -> {
            List<String> postIdList = postList.stream().map(Post::getId).collect(Collectors.toList());
            return imageRepository.getPostAlbum(postIdList);
        });

        albumPopular = Transformations.switchMap(postsPopular, postList -> {
            List<String> postIdList = postList.stream().map(Post::getId).collect(Collectors.toList());
            return imageRepository.getPostAlbum(postIdList);
        });
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<List<Post>> getPostsRealtime() {
        return postsRealtime;
    }

    public LiveData<Map<String, Bitmap>> getAlbumRealtime() {
        return albumRealtime;
    }

    public LiveData<List<Post>> getPostsPopular() {
        return postsPopular;
    }

    public LiveData<Map<String, Bitmap>> getAlbumPopular() {
        return albumPopular;
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            event.setValue(new Event.NavigateBack());
        }
    }

    public void onAddressQueryChange(String text) {
        addressQuery.setValue(text);
    }

    public void onPostClick(Post post) {
        event.setValue(new Event.NavigateToPostScreen(post));
    }

    public void onTextTypeClick() {
        event.setValue(new Event.NavigateToBoardTextScreen());
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

        public static class NavigateToBoardTextScreen extends Event {}

    }

}