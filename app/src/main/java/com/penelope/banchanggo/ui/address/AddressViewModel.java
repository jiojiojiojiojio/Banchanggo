package com.penelope.banchanggo.ui.address;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penelope.banchanggo.api.address.AddressApi;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddressViewModel extends ViewModel {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private String queryAddress = "";
    private String detailAddress = "";

    private final AddressApi addressApi;


    @Inject
    public AddressViewModel() {
        addressApi = new AddressApi();
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public void onQueryAddressChange(String text) {
        queryAddress = text;
    }

    public void onDetailAddressChange(String text) {
        detailAddress = text;
    }

    public void onSearchAddressClick() {

        if (queryAddress.trim().isEmpty() || detailAddress.trim().isEmpty()) {
            event.setValue(new Event.ShowGeneralMessage("주소를 입력해주세요"));
            return;
        }

        new Thread(() -> {
            List<String> addresses = addressApi.getAddresses(queryAddress);
            if (addresses.isEmpty()) {
                event.postValue(new Event.ShowGeneralMessage("검색된 주소가 없습니다"));
                return;
            }
            String fullAddress = addresses.get(0) + " " + detailAddress;
            event.postValue(new Event.NavigateBackWithResult(fullAddress));
        }).start();
    }


    public static class Event {

        public static class ShowGeneralMessage extends Event {
            public final String message;

            public ShowGeneralMessage(String message) {
                this.message = message;
            }
        }

        public static class NavigateBackWithResult extends Event {
            public final String address;

            public NavigateBackWithResult(String address) {
                this.address = address;
            }
        }

    }

}