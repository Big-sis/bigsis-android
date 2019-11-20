package fr.bigsis.android.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MenuFilterViewModel extends ViewModel {

    private MutableLiveData<String> filterName;

    public MenuFilterViewModel() {
        filterName = new MutableLiveData<>();
        filterName.setValue("partner");
    }

    public MutableLiveData<String> getfilterName() {
        return this.filterName;
    }

    public void setfilterName(String filterName) {
        this.filterName.setValue(filterName);
    }
}
