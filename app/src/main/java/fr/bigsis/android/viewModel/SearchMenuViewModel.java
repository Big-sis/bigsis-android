package fr.bigsis.android.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

public class SearchMenuViewModel extends ViewModel {
    private MutableLiveData<String> departureName;

    public SearchMenuViewModel() {
        departureName = new MutableLiveData<>();
    }

    public String getDepartureName() {
        return this.departureName != null ? this.departureName.getValue() : "";
    }

    public void setDepartureName(String name) {
        this.departureName.setValue(name);
    }

    public LiveData<String> getText() {
        return departureName;
    }
}
