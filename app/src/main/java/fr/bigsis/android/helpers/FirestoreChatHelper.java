package fr.bigsis.android.helpers;

import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import fr.bigsis.android.entity.ChatEntity;

public class FirestoreChatHelper {

    public static void addData(String principalCollection, String id, String subCollection, Object object) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection(principalCollection)
                .document(id)
                .collection(subCollection)
                .add(object);
    }
    public static void getDetailFromDB(String principalCollection, String idGroup, String mCurrentUserId, TextView message) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();


        
    }

}
