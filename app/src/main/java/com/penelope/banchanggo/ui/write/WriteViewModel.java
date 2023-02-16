package com.penelope.banchanggo.ui.write;

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
import com.penelope.banchanggo.data.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WriteViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private String uid;

    private String title = "";
    private String content = "";
    private final MutableLiveData<Integer> price = new MutableLiveData<>(0);
    private final MutableLiveData<List<String>> categories = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Bitmap> image = new MutableLiveData<>();
    private final MutableLiveData<String> address = new MutableLiveData<>();

    private final LiveData<Boolean> isNegotiablePrice;

    private final PostRepository postRepository;
    private final ImageRepository imageRepository;


    @Inject
    public WriteViewModel(PostRepository postRepository, ImageRepository imageRepository) {
        isNegotiablePrice = Transformations.map(price, priceValue -> (priceValue == -1));
        this.postRepository = postRepository;
        this.imageRepository = imageRepository;
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<Boolean> isNegotiablePrice() {
        return isNegotiablePrice;
    }

    public LiveData<List<String>> getCategories() {
        return categories;
    }

    public LiveData<Bitmap> getImage() {
        return image;
    }

    public LiveData<String> getAddress() {
        return address;
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            event.setValue(new Event.NavigateBack());
        } else {
            uid = firebaseAuth.getUid();
        }
    }

    public void onImageClick() {
        event.setValue(new Event.PromptImage());
    }

    public void onImageSubmit(Bitmap bitmap) {
        if (bitmap != null) {
            image.setValue(bitmap);
        } else {
            event.setValue(new Event.ShowGeneralMessage("이미지 업로드에 실패했습니다"));
        }
    }

    public void onTitleChange(String text) {
        title = text;
    }

    public void onPriceChange(String text) {
        try {
            price.setValue(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void onNegotiablePriceClick() {

        Integer priceValue = price.getValue();
        assert priceValue != null;

        if (priceValue == -1) {
            price.setValue(0);
        } else {
            price.setValue(-1);
        }
    }

    public void onAddCategoryClick(String text) {

        if (text.isEmpty()) {
            return;
        }

        List<String> oldList = categories.getValue();
        assert oldList != null;

        List<String> newList = new ArrayList<>(oldList);
        newList.add(text);

        categories.setValue(newList);
    }

    public void onContentChange(String text) {
        content = text;
    }

    public void onSubmitPost() {

        if (uid == null) {
            return;
        }

        Integer priceValue = price.getValue();
        List<String> categoryList = categories.getValue();
        Bitmap imageValue = image.getValue();
        String addressValue = address.getValue();
        assert priceValue != null && categoryList != null;

        if (title.trim().isEmpty()) {
            event.setValue(new Event.ShowGeneralMessage("제목을 입력해주세요"));
            return;
        }

        if (content.trim().isEmpty()) {
            event.setValue(new Event.ShowGeneralMessage("내용을 입력해주세요"));
            return;
        }

        if (priceValue == 0) {
            event.setValue(new Event.ShowGeneralMessage("가격을 입력해주세요"));
            return;
        }

        if (imageValue == null) {
            event.setValue(new Event.ShowGeneralMessage("이미지를 설정해주세요"));
            return;
        }

        if (categoryList.isEmpty()) {
            event.setValue(new Event.ShowGeneralMessage("카테고리를 1개 이상 입력해주세요"));
            return;
        }

        if (addressValue == null || addressValue.isEmpty()) {
            event.setValue(new Event.ShowGeneralMessage("주소를 설정해주세요"));
        }

        Post post = new Post(uid, title, content, priceValue, categoryList, addressValue);

        postRepository.addPost(post,
                unused -> imageRepository.addPostImage(post.getId(), imageValue,
                        unused1 -> event.setValue(new Event.NavigateBack()),
                        e -> {
                        }
                ),
                e -> event.setValue(new Event.ShowGeneralMessage("글 작성에 실패했습니다")));
    }

    public void onSetAddressClick() {
        event.setValue(new Event.NavigateToAddressScreen());
    }

    public void onAddressSearched(String value) {
        if (value != null) {
            address.setValue(value);
        }
    }


    public static class Event {

        public static class NavigateBack extends Event {
        }

        public static class PromptImage extends Event {
        }

        public static class ShowGeneralMessage extends Event {
            public final String message;

            public ShowGeneralMessage(String message) {
                this.message = message;
            }
        }

        public static class NavigateToAddressScreen extends Event {}
    }

}