package fr.bigsis.android.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import fr.bigsis.android.entity.UserEntity;

public class SignUpViewModel extends ViewModel {

    private MutableLiveData<UserEntity> user = new MutableLiveData<>();

    public SignUpViewModel() {
        user = new MutableLiveData<>();
    }

    public MutableLiveData<UserEntity> getUser() {
        return this.user;
    }

    public void setUser(UserEntity userEntity) {
        this.user.setValue(userEntity);
    }
}
