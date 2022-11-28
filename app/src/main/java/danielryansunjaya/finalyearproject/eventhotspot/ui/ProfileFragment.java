package danielryansunjaya.finalyearproject.eventhotspot.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import danielryansunjaya.finalyearproject.eventhotspot.R;
import danielryansunjaya.finalyearproject.eventhotspot.adapters.UserEventAdapter;
import danielryansunjaya.finalyearproject.eventhotspot.models.EventModel;
import danielryansunjaya.finalyearproject.eventhotspot.models.UserModel;


public class ProfileFragment extends Fragment implements UserEventAdapter.OnClickCancelEventListener {

    private static final String TAG ="ProfileFragment";
    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseDatabase rtdb;

    List<EventModel> eventModelList;
    UserEventAdapter userEventAdapter;
    SwipeRefreshLayout refresh;

    TextView name, programme, id, email, elePoint;
    ImageButton editProfileBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState){
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        rtdb = FirebaseDatabase.getInstance();

        name = root.findViewById(R.id.nameText);
        id = root.findViewById(R.id.idText);
        programme = root.findViewById(R.id.programmeText);
        email = root.findViewById(R.id.emailText);
        elePoint = root.findViewById(R.id.elePointText);
        editProfileBtn = root.findViewById(R.id.editProfileBtn);
        displayUserInfo();

        recyclerView = root.findViewById(R.id.userRecyclerView);
        listAllUserEvent();

        refresh = root.findViewById(R.id.userRefresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
                listAllUserEvent();
                displayUserInfo();
                Log.i(TAG+" [Refresh]","Fragment refreshed!");
            }
        });

    }

    public void displayUserInfo(){
        rtdb.getReference()
                .child("login")
                .child(Objects.requireNonNull(auth.getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        assert userModel != null;
                        String sName = userModel.getName();
                        String sID = userModel.getStudentID();
                        String sProgramme = userModel.getProgramme();
                        String sEmail = userModel.getEmail();
                        int sElePoint = userModel.getElePoints();
                        name.setText(sName);
                        id.setText(sID);
                        programme.setText(sProgramme);
                        email.setText(sEmail);
                        elePoint.setText("Ele Points: "+ sElePoint);
                        Log.i(TAG+" [displayUserInfo]","Successfully display user's info!");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG+" [displayUserInfo]","Failed to display user's info!\n"+ error.getDetails());
                    }
                });

    }

    public void listAllUserEvent(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        eventModelList = new ArrayList<>();
        userEventAdapter = new UserEventAdapter(getActivity(), eventModelList, this);
        recyclerView.setAdapter(userEventAdapter);

        db.collection("studentsJoinEvents")
                .document(Objects.requireNonNull(auth.getUid()))
                .collection("joinedEventList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document: task.getResult()){
                                EventModel eventModel = document.toObject(EventModel.class);
                                eventModelList.add(eventModel);
                                userEventAdapter.notifyDataSetChanged();
                            }
                            Log.i(TAG+" [listAllUserEvent]","Successfully display all user's joined events!");
                        } else {
                            Log.i(TAG+" [listAllUserEvent]","Failed to display all user's joined events!\n"+task.getException());
                            Toast.makeText(getActivity(), "Error"+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void OnClickCancelEventClick(int position) {
        db.collection("studentsJoinEvents")
                .document(Objects.requireNonNull(auth.getUid()))
                .collection("joinedEventList")
                .document(eventModelList.get(position).getTitle())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            eventModelList.remove(eventModelList.get(position));
                            userEventAdapter.notifyDataSetChanged();
                            Log.i(TAG+" [OnClickCancelEvent]","Successfully cancelled user's joined event!");
                            Toast.makeText(getActivity(), "Event is Cancelled!", Toast.LENGTH_SHORT ).show();
                        }else{
                            Log.i(TAG+" [OnClickCancelEvent]","Failed to cancelled user's joined event!\n"+ task.getException());
                        }
                    }
                });

        DocumentReference documentReference = db.collection("studentsJoinEvents").document(auth.getUid());
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("eventList", FieldValue.delete());
        documentReference.update(updates);

        // Update array "eventList"
        db.collection("studentsJoinEvents")
                .document(auth.getUid())
                .collection("joinedEventList")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<String> event = new ArrayList<>();
                            for(QueryDocumentSnapshot document: task.getResult()){
                                EventModel eventModel = document.toObject(EventModel.class);
                                event.add(eventModel.getTitle());
                            }

                            HashMap<String, Object> hashEvent = new HashMap<>();
                            hashEvent.put("eventList", event);
                            db.collection("studentsJoinEvents")
                                    .document(auth.getUid())
                                    .set(hashEvent)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.i(TAG+" [OnClickCancelEvent]","EventList array is successfully added/updated!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i(TAG+" [OnClickCancelEvent]","Failed to added/updated eventList array!");
                                        }
                                    });
                        } else {
                            Toast.makeText(getActivity(), "Error"+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}