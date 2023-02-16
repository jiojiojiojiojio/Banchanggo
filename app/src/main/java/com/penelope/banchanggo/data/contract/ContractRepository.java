package com.penelope.banchanggo.data.contract;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ContractRepository {

    private final CollectionReference contractsCollection;

    @Inject
    public ContractRepository(FirebaseFirestore firestore) {
        contractsCollection = firestore.collection("contracts");
    }

    public void addContract(Contract contract, OnSuccessListener<Void> onSuccessListener) {

        contractsCollection.document(contract.getId())
                .set(contract)
                .addOnSuccessListener(onSuccessListener);
    }

    public void completeContract(String contractId, OnSuccessListener<Void> onSuccessListener) {

        contractsCollection.document(contractId)
                .update("completed", true)
                .addOnSuccessListener(onSuccessListener);
    }

    public void getContracts(OnSuccessListener<List<Contract>> onSuccessListener, OnFailureListener onFailureListener) {

        contractsCollection.addSnapshotListener((value, error) -> {
            if (value == null || error != null) {
                onFailureListener.onFailure(new Exception());
                return;
            }

            List<Contract> contractList = new ArrayList<>();
            for (DocumentSnapshot snapshot : value) {
                Contract contract = snapshot.toObject(Contract.class);
                if (contract == null) {
                    continue;
                }
                contractList.add(contract);
            }

            onSuccessListener.onSuccess(contractList);
        });
    }

    public void getContractRequestedBy(String postId,
                                       String requesterId,
                                       OnSuccessListener<Contract> onSuccessListener,
                                       OnFailureListener onFailureListener) {

        getContracts(contracts -> {
            for (Contract contract : contracts) {
                if (contract.getPostId().equals(postId) && contract.getBuyerId().equals(requesterId) && !contract.isCompleted()) {
                    onSuccessListener.onSuccess(contract);
                    return;
                }
            }
            onSuccessListener.onSuccess(null);
        }, onFailureListener);
    }

    public LiveData<Contract> getContractRequestedBy(String postId, String requesterId) {

        MutableLiveData<Contract> contract = new MutableLiveData<>();
        getContractRequestedBy(postId, requesterId, contract::setValue, e -> contract.setValue(null));
        return contract;
    }

    public void getContractCompleted(String postId,
                                     OnSuccessListener<Contract> onSuccessListener,
                                     OnFailureListener onFailureListener) {

        getContracts(contracts -> {
            for (Contract contract : contracts) {
                if (contract.getPostId().equals(postId) && contract.isCompleted()) {
                    onSuccessListener.onSuccess(contract);
                    return;
                }
            }
            onSuccessListener.onSuccess(null);
        }, onFailureListener);
    }

    public LiveData<Contract> getContractCompleted(String postId) {

        MutableLiveData<Contract> contract = new MutableLiveData<>();
        getContractCompleted(postId, contract::setValue, e -> contract.setValue(null));
        return contract;
    }

    public void getContractsSold(String sellerId, OnSuccessListener<List<Contract>> onSuccessListener, OnFailureListener onFailureListener) {

        getContracts(contracts -> {
            List<Contract> filtered = new ArrayList<>();
            for (Contract contract : contracts) {
                if (contract.getSellerId().equals(sellerId) && contract.isCompleted()) {
                    filtered.add(contract);
                }
            }
            onSuccessListener.onSuccess(filtered);
        }, onFailureListener);
    }

    public LiveData<List<Contract>> getContractsSold(String sellerId) {

        MutableLiveData<List<Contract>> contracts = new MutableLiveData<>();
        getContractsSold(sellerId, contracts::setValue, e -> contracts.setValue(null));
        return contracts;
    }

    public void getContractBought(String buyerId, OnSuccessListener<List<Contract>> onSuccessListener, OnFailureListener onFailureListener) {

        getContracts(contracts -> {
            List<Contract> filtered = new ArrayList<>();
            for (Contract contract : contracts) {
                if (contract.getBuyerId().equals(buyerId) && contract.isCompleted()) {
                    filtered.add(contract);
                }
            }
            onSuccessListener.onSuccess(filtered);
        }, onFailureListener);
    }

    public LiveData<List<Contract>> getContractBought(String buyerId) {

        MutableLiveData<List<Contract>> contracts = new MutableLiveData<>();
        getContractBought(buyerId, contracts::setValue, e -> contracts.setValue(null));
        return contracts;
    }

}
