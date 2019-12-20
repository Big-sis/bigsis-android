package fr.bigsis.android.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import fr.bigsis.android.R;

public class AlertFragment extends Fragment {


    private final int REQUEST_PHONE_CALL = 1;
    private OnFragmentInteractionListener mListener;
    private Button btCallPolice;
    private Button btCallFireFighter;
    private Button btAlertStaff;
    private ImageButton imgBtCancel;
    private String callPolice = "tel:0684242687";
    private String callFireFighter= "tel:0684242687";

    public AlertFragment() {
    }

    public static AlertFragment newInstance() {
        AlertFragment fragment = new AlertFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alert, container, false);
        btAlertStaff = view.findViewById(R.id.btAlert);
        btCallFireFighter = view.findViewById(R.id.btCallFireFighter);
        btCallPolice = view.findViewById(R.id.btCallPolice);
        imgBtCancel = view.findViewById(R.id.imgBtCancelAlert);

        imgBtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed();
            }
        });
        btCallPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(callPolice);
            }
        });
        btCallFireFighter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(callFireFighter);
            }
        });
        return view;
    }

    private void showAlertDialog(String number) {
        AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.style_dialog_call_alert, null);
        Button btNo = dialogView.findViewById(R.id.btCancelAlert);
        Button btAlert = dialogView.findViewById(R.id.btActivateAlert);
        btAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(number));
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                } else {
                    startActivity(callIntent);
                }
                dialogBuilder.dismiss();
            }
        });
        btNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }
}
