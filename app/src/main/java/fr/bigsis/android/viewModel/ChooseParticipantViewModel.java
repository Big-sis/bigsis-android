package fr.bigsis.android.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChooseParticipantViewModel extends ViewModel {

    private MutableLiveData<String> participant;

    public ChooseParticipantViewModel() {
        participant = new MutableLiveData<>();
        participant.setValue("");
    }

    public MutableLiveData<String> getParticipant() {
        return this.participant;
    }

    public void setParticipant(String participant) {
        this.participant.setValue(participant);
    }
}
