package fr.bigsis.android.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatViewModel extends ViewModel {

    private MutableLiveData<String> idMessage;

    public ChatViewModel() {
        idMessage = new MutableLiveData<>();
        idMessage.setValue("");
    }

    public MutableLiveData<String> getIdMessage() {
        return this.idMessage;
    }

    public void setIdMessage(String filterName) {
        this.idMessage.setValue(filterName);
    }

}
