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

    private MutableLiveData<List<UserEntity>> staffListLiveData = new MutableLiveData<List<UserEntity>>();
    private ArrayList<UserEntity> staffList = new ArrayList<>();

    public LiveData<List<UserEntity>> getStaffList(){
        return staffListLiveData;
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
