package fr.bigsis.android.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.bigsis.android.entity.UserEntity;

public class ChooseUsersViewModel extends ViewModel {

    private MutableLiveData<List<UserEntity>> participantListLiveData = new MutableLiveData<List<UserEntity>>();
    private ArrayList<UserEntity> participantList = new ArrayList<>();

    private MutableLiveData<List<UserEntity>> staffListLiveData = new MutableLiveData<List<UserEntity>>();
    private ArrayList<UserEntity> staffList = new ArrayList<>();

    public void init() {
        participantListLiveData = new MutableLiveData<List<UserEntity>>();
    }

    public LiveData<List<UserEntity>> getParticipantList(){
        return participantListLiveData;
    }

    public LiveData<List<UserEntity>> getStaffList(){
        return staffListLiveData;
    }

    public void addParticipant(UserEntity user){
        participantList.add(user);
        participantListLiveData.setValue(participantList);
    }

    public void removeParticipant(UserEntity user) {
        participantList.remove(user);
    }

    public void reset() {
        participantList.clear();
        participantListLiveData.setValue(participantList);
    }

    public void addStaffMember(UserEntity user){
        staffList.add(user);
        staffListLiveData.setValue(staffList);
    }

    public void removeStaffMember(UserEntity user) {
        staffList.remove(user);
    }

    public void resetStaffMember() {
        staffList.clear();
        staffListLiveData.setValue(staffList);
    }
}
