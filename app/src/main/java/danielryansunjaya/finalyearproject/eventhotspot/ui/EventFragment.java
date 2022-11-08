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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import danielryansunjaya.finalyearproject.eventhotspot.R;
import danielryansunjaya.finalyearproject.eventhotspot.adapters.EventsAdapter;
import danielryansunjaya.finalyearproject.eventhotspot.models.EventModel;
import danielryansunjaya.finalyearproject.eventhotspot.models.UserModel;
import danielryansunjaya.finalyearproject.eventhotspot.utils.JavaMailAPI;


public class EventFragment extends Fragment implements EventsAdapter.OnClickJoinEventListener {

    private static final String TAG ="EventFragment";
    RecyclerView recyclerView;
    FirebaseFirestore db;
    FirebaseDatabase rtdb;
    FirebaseAuth auth;

    List<EventModel> eventModelList;
    EventsAdapter eventsAdapter;
    SwipeRefreshLayout refresh;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState){
        db = FirebaseFirestore.getInstance();
        rtdb = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        recyclerView = root.findViewById(R.id.recyclerView);
        listAll();

        refresh = root.findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
                Log.i(TAG+" [Refresh]","Successfully refresh EventFragment!");
                listAll();
            }
        });
    }

    private void listAll() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL, false));
        eventModelList = new ArrayList<>();
        eventsAdapter = new EventsAdapter(getActivity(),eventModelList,this);
        recyclerView.setAdapter(eventsAdapter);

        db.collection("eventList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document: task.getResult()){
                                EventModel eventModel = document.toObject(EventModel.class);
                                eventModelList.add(eventModel);
                                eventsAdapter.notifyDataSetChanged();
                            }
                            Log.i(TAG+" [listAll]","Successfully display all events!");
                        } else {
                            Log.i(TAG+" [listAll]","Failed to display all events!");
                            Toast.makeText(getActivity(), "Error"+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG+" [listAll]","Failed to display user's joined event!");
                    }
                });
    }

    @Override
    public void OnClickJoinEventClick(int position) {
        addEventToProfile(position);
    }

    private void addEventToProfile(int position) {
        // Creating a hashmap to structure the data
        HashMap<String, Object> eventInfo=new HashMap<>();

        eventInfo.put("title", eventModelList.get(position).getTitle());
        eventInfo.put("organizer", eventModelList.get(position).getOrganizer());
        eventInfo.put("pic", eventModelList.get(position).getPic());
        eventInfo.put("email", eventModelList.get(position).getEmail());
        eventInfo.put("date", eventModelList.get(position).getDate());
        eventInfo.put("time", eventModelList.get(position).getTime());
        eventInfo.put("elePoint", eventModelList.get(position).getElePoint());
        eventInfo.put("location", eventModelList.get(position).getLocation());

        // Inserting event informations to "joinedEventList" collection
        db.collection("studentsJoinEvents")
                .document(Objects.requireNonNull(auth.getUid()))
                .collection("joinedEventList")
                .document(eventModelList.get(position).getTitle())
                .set(eventInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i(TAG+" [addEventToProfile]","Successfully added/updated new event to joinEventList!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG+" [addEventToProfile]","Failed to added/updated new event to joinEventList!");
                    }
                });

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
                                            Log.i(TAG+" [addEventToProfile]","EventList array is successfully added/updated!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i(TAG+" [addEventToProfile]","Failed to added/updated eventList array!");
                                        }
                                    });
                        } else {
                            Toast.makeText(getActivity(), "Error"+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        sendEmailNotif(eventModelList.get(position).getTitle());

        Toast.makeText(getActivity(),"Event Joined!",Toast.LENGTH_SHORT).show();
    }

    public void sendEmailNotif(String title){
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
                        String sEmail = userModel.getEmail();

                        String subject = "Successfully join "+title+"!";
                        String message = "Dear "+sName+" - ("+sID+"),\n"+"Your request to joining ["+title+"] event has been confirmed!";

                        JavaMailAPI javaMailAPI = new JavaMailAPI(getActivity(), sEmail, subject, message);
                        javaMailAPI.execute();
                        Log.i(TAG+" [sendEmailNotif]","Successfully send email notification for the joined event!");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG+" [sendEmailNotif]","Failed send email notification for the joined event!");
                        throw error.toException();
                    }
                });
    }

}