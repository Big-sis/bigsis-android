package fr.bigsis.android.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AlertLocateViewModel extends ViewModel {

    private MutableLiveData<Double> latitudeAlert;
    private MutableLiveData<Double> longitudeAlert;

    public AlertLocateViewModel() {
        latitudeAlert = new MutableLiveData<>();
        longitudeAlert = new MutableLiveData<>();
    }

    public MutableLiveData<Double> getLongitudeAlert() {
        return this.longitudeAlert;
    }

    public void setLongitudeAlert(Double longitudeAlert) {
        this.longitudeAlert.setValue(longitudeAlert);
    }
    public MutableLiveData<Double> getLatitudeAlert() {
        return this.latitudeAlert;
    }

    public void setLatitudeAlert(Double latitudeAlert) {
        this.latitudeAlert.setValue(latitudeAlert);
    }
}
