package fr.bigsis.android.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import fr.bigsis.android.R;
import fr.bigsis.android.fragment.SignUpFragment;
import fr.bigsis.android.fragment.StartSignUpFragment;
import fr.bigsis.android.viewModel.SignUpViewModel;

public class SignUpActivity extends AppCompatActivity implements StartSignUpFragment.OnFragmentInteractionListener,
        SignUpFragment.OnFragmentInteractionListener {

    StartSignUpFragment startSignUpFragment = new StartSignUpFragment();
    SignUpViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        viewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);
        openFragment();
    }

    private void openFragment() {
        FragmentManager fragmentManager = SignUpActivity.this.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.fragmentContaienrSignUp, startSignUpFragment, "FragmentSignUP")
                .commit();
    }

    @Override
    public void onFragmentInteraction() {
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
