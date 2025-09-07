package com.emmanuel.chancita.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> toolbarTitle = new MutableLiveData<>();
    private String rifaId;

    public LiveData<String> getToolbarTitle() {
        return toolbarTitle;
    }

    public void setToolbarTitle(String title) {
        toolbarTitle.setValue(title);
    }

    public void setRifaId(String rifaId) {
        this.rifaId = rifaId;
    }

    public String getRifaId() {
        return rifaId;
    }
}
