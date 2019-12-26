package fr.bigsis.android.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatViewModel extends ViewModel {

    private MutableLiveData<String> idMessage;
    private MutableLiveData<String> organismName;

    public ChatViewModel() {
        idMessage = new MutableLiveData<>();
        organismName = new MutableLiveData<>();
        idMessage.setValue("");
        organismName.setValue("");
    }

    public MutableLiveData<String> getIdMessage() {
        return this.idMessage;
    }

    public void setIdMessage(String filterName) {
        this.idMessage.setValue(filterName);
    }
    public MutableLiveData<String> getOrganismName() {
        return this.organismName;
    }

    public void setOrganismName(String organismName) {
        this.organismName.setValue(organismName);
    }
}
