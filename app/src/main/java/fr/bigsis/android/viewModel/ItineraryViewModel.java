package fr.bigsis.android.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ItineraryViewModel extends ViewModel {
    private MutableLiveData<Double> latitudeItinerary;
    private MutableLiveData<Double> longitudeItinerary;
    private MutableLiveData<String> modeItinerary;

    public ItineraryViewModel() {
        latitudeItinerary = new MutableLiveData<>();
        longitudeItinerary = new MutableLiveData<>();
        modeItinerary = new MutableLiveData<>();
        modeItinerary.setValue("WALKING");
    }

    public MutableLiveData<Double> getLongitudeItinerary() {
        return this.longitudeItinerary;
    }

    public void setLongitudeItinerary(Double longitudeItinerary) {
        this.longitudeItinerary.setValue(longitudeItinerary);
    }
    public MutableLiveData<Double> getLatitudeItinerary() {
        return this.latitudeItinerary;
    }

    public void setLatitudeItinerary(Double latitudeItinerary) {
        this.latitudeItinerary.setValue(latitudeItinerary);
    }

    public MutableLiveData<String> getModeItinerary() {
        return this.modeItinerary;
    }

    public void setModeItinerary(String modeItinerary) {
        this.modeItinerary.setValue(modeItinerary);
    }



}
