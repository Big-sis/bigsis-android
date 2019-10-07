package fr.bigsis.android.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SearchContactViewModel extends ViewModel {

    private MutableLiveData<String> contact;

    public SearchContactViewModel() {
        contact = new MutableLiveData<>();
        contact.setValue("");
    }

    public MutableLiveData<String> getContact() {
        return this.contact;
    }

    public void setContact(String name) {
        this.contact.setValue(name);
    }
}
