package fr.bigsis.android.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

import fr.bigsis.android.entity.UserEntity;

public class ChooseUsersViewModel extends ViewModel {

    private MutableLiveData<String> mName = new MutableLiveData<>();

    private MutableLiveData<List<UserEntity>> userListLiveData = new MutableLiveData<List<UserEntity>>();
    private List<UserEntity> userList = new ArrayList<>();

    public void setName(String name) {
        mName.setValue(name);
    }

    public LiveData<String> getName() {
        return mName;
    }


    public LiveData<List<UserEntity>> getUserList(){

        return userListLiveData;
    }

    public void addUser(UserEntity user){
        userList.add(user);
        userListLiveData.setValue(userList);
    }

    public void removeUser(UserEntity user) {
        userList.remove(user);
        userListLiveData.setValue(userList);
    }

    public void reset() {
        userList.clear();
        userListLiveData.setValue(userList);
    }

}
