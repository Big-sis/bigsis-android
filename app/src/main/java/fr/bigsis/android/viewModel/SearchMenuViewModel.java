package fr.bigsis.android.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

public class SearchMenuViewModel extends ViewModel {
    private MutableLiveData<String> text = new MutableLiveData<>();



    public void setText(String input) {
        text.setValue(input);
    }

    public LiveData<String> getText() {
        return text;
    }
}
