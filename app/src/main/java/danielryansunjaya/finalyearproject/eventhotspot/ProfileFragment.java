package danielryansunjaya.finalyearproject.eventhotspot;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import danielryansunjaya.finalyearproject.eventhotspot.adapters.UserEventAdapter;
import danielryansunjaya.finalyearproject.eventhotspot.models.EventModel;


public class ProfileFragment extends Fragment implements UserEventAdapter.OnClickCancelEventListener {

    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseDatabase rtdb;

    List<EventModel> eventModelList;
    UserEventAdapter userEventAdapter;
    SwipeRefreshLayout refresh;


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

        recyclerView = root.findViewById(R.id.userRecyclerView);
        listAllUserEvent();

        refresh = root.findViewById(R.id.userRefresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
                listAllUserEvent();
            }
        });

    }

    public void listAllUserEvent(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        eventModelList = new ArrayList<>();
        userEventAdapter = new UserEventAdapter(getActivity(), eventModelList, this);
        recyclerView.setAdapter(userEventAdapter);

        db.collection("studentsJoinEvents").document(auth.getUid()).collection("eventList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document: task.getResult()){
                                EventModel eventModel = document.toObject(EventModel.class);
                                eventModelList.add(eventModel);
                                userEventAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error"+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void OnClickCancelEventClick(int position) {

    }

}