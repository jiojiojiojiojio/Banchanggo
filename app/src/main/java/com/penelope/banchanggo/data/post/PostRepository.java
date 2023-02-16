package com.penelope.banchanggo.data.post;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

public class PostRepository {

    private final CollectionReference postsCollection;

    @Inject
    public PostRepository(FirebaseFirestore firestore) {

        postsCollection = firestore.collection("posts");
    }

    // DB 에 글을 추가하는 메소드

    public void addPost(Post post, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {

        postsCollection.document(post.getId())
                .set(post)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void deletePost(String postId, OnSuccessListener<Void> onSuccessListener) {

        postsCollection.document(postId)
                .delete()
                .addOnSuccessListener(onSuccessListener);
    }

    // DB 에서 글들을 가져오는 메소드 (최신순)

    public void getPosts(OnSuccessListener<List<Post>> onSuccessListener, OnFailureListener onFailureListener) {

        postsCollection.orderBy("created", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value == null || error != null) {
                        onFailureListener.onFailure(new Exception());
                        return;
                    }

                    List<Post> postList = new ArrayList<>();
                    for (DocumentSnapshot snapshot : value) {
                        Post post = snapshot.toObject(Post.class);
                        if (post == null) {
                            continue;
                        }
                        postList.add(post);
                    }

                    onSuccessListener.onSuccess(postList);
                });

    }

    public LiveData<List<Post>> getPosts() {

        MutableLiveData<List<Post>> posts = new MutableLiveData<>(new ArrayList<>());
        getPosts(posts::setValue, e -> posts.setValue(null));
        return posts;
    }

    // ID로 글 검색

    public void getPost(String postId, OnSuccessListener<Post> onSuccessListener, OnFailureListener onFailureListener) {

        postsCollection.document(postId)
                .addSnapshotListener((value, error) -> {
                    if (value == null || error != null) {
                        onFailureListener.onFailure(new Exception());
                        return;
                    }

                    Post post = value.toObject(Post.class);
                    onSuccessListener.onSuccess(post);
                });
    }

    public LiveData<Post> getPost(String postId) {

        MutableLiveData<Post> post = new MutableLiveData<>();
        getPost(postId, post::setValue, e -> post.setValue(null));
        return post;
    }

    // 인기 글 검색

    public void getPopularPosts(OnSuccessListener<List<Post>> onSuccessListener, OnFailureListener onFailureListener) {
        getPosts(posts -> {
            posts.sort(Comparator.comparingInt(post -> post.getLikes().size()));
            Collections.reverse(posts);
            onSuccessListener.onSuccess(posts);
        }, onFailureListener);
    }

    // DB 에서 글들을 가져오는 메소드 (가격순)

    public void getPostsInPriceOrder(boolean descending, OnSuccessListener<List<Post>> onSuccessListener, OnFailureListener onFailureListener) {

        postsCollection.orderBy("price", descending ? Query.Direction.DESCENDING : Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value == null || error != null) {
                        onFailureListener.onFailure(new Exception());
                        return;
                    }

                    List<Post> postList = new ArrayList<>();
                    for (DocumentSnapshot snapshot : value) {
                        Post post = snapshot.toObject(Post.class);
                        if (post == null) {
                            continue;
                        }
                        postList.add(post);
                    }

                    onSuccessListener.onSuccess(postList);
                });
    }

    public LiveData<List<Post>> getPostsInPriceOrder(boolean descending) {

        MutableLiveData<List<Post>> posts = new MutableLiveData<>(new ArrayList<>());
        getPostsInPriceOrder(descending, posts::setValue, e -> posts.setValue(null));
        return posts;
    }

    // 특정 User 가 Like 를 누른 글을 가져오는 메소드

    public LiveData<List<Post>> getPostsLikedBy(String uid) {

        MutableLiveData<List<Post>> posts = new MutableLiveData<>(new ArrayList<>());

        getPosts(postList -> {
            List<Post> filtered = new ArrayList<>();
            for (Post post : postList) {
                if (post.getLikes().contains(uid)) {
                    filtered.add(post);
                }
            }
            posts.setValue(filtered);
        }, e -> posts.setValue(null));

        return posts;
    }

    public LiveData<List<Post>> getPostsLikedByInPriceOrder(String uid, boolean descending) {

        MutableLiveData<List<Post>> posts = new MutableLiveData<>(new ArrayList<>());

        getPostsInPriceOrder(descending, postList -> {
            List<Post> filtered = new ArrayList<>();
            for (Post post : postList) {
                if (post.getLikes().contains(uid)) {
                    filtered.add(post);
                }
            }
            posts.setValue(filtered);
        }, e -> posts.setValue(null));

        return posts;
    }

    // 주소로 글 검색

    public LiveData<List<Post>> queryPostsByAddress(String query) {

        MutableLiveData<List<Post>> posts = new MutableLiveData<>(new ArrayList<>());

        getPosts(postList -> {
            if (query.isEmpty()) {
                posts.setValue(postList);
                return;
            }

            List<Post> filtered = new ArrayList<>();
            for (Post post : postList) {
                if (post.getAddress().contains(query)) {
                    filtered.add(post);
                }
            }

            posts.setValue(filtered);

        }, e -> posts.setValue(null));

        return posts;
    }

    public LiveData<List<Post>> queryPopularPostsByAddress(String query) {

        MutableLiveData<List<Post>> posts = new MutableLiveData<>(new ArrayList<>());

        getPopularPosts(postList -> {
            if (query.isEmpty()) {
                posts.setValue(postList);
                return;
            }

            List<Post> filtered = new ArrayList<>();
            for (Post post : postList) {
                if (post.getAddress().contains(query)) {
                    filtered.add(post);
                }
            }

            posts.setValue(filtered);

        }, e -> posts.setValue(null));

        return posts;
    }

    // 작성자로 글 검색

    public LiveData<List<Post>> getPostsWrittenBy(String uid) {

        MutableLiveData<List<Post>> posts = new MutableLiveData<>(new ArrayList<>());

        getPosts(postList -> {
            List<Post> filtered = new ArrayList<>();
            for (Post post : postList) {
                if (post.getWriterId().equals(uid)) {
                    filtered.add(post);
                }
            }

            posts.setValue(filtered);

        }, e -> posts.setValue(null));

        return posts;
    }

    // DB 에서 특정 레시피에 좋아요를 누르거나 취소하는 메소드

    public void likeOrUnlikePost(String postId, String userId, OnSuccessListener<List<String>> onSuccessListener) {

        postsCollection.document(postId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Post post = documentSnapshot.toObject(Post.class);
                    if (post == null) {
                        return;
                    }
                    List<String> likes = post.getLikes();
                    if (likes.contains(userId)) {
                        likes.remove(userId);
                    } else {
                        likes.add(userId);
                    }
                    postsCollection.document(postId).update("likes", likes)
                            .addOnSuccessListener(unused -> onSuccessListener.onSuccess(likes));
                });
    }

}
