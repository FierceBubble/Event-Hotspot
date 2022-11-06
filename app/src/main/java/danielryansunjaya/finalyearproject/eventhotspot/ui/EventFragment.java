package danielryansunjaya.finalyearproject.eventhotspot.ui;

import android.annotation.SuppressLint;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import danielryansunjaya.finalyearproject.eventhotspot.R;
import danielryansunjaya.finalyearproject.eventhotspot.adapters.EventsAdapter;
import danielryansunjaya.finalyearproject.eventhotspot.models.EventModel;


public class EventFragment extends Fragment implements EventsAdapter.OnClickJoinEventListener {

    RecyclerView recyclerView;
    FirebaseFirestore db;
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
        auth = FirebaseAuth.getInstance();

        recyclerView = root.findViewById(R.id.recyclerView);
        listAll();

        refresh = root.findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
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
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document: task.getResult()){
                                EventModel eventModel = document.toObject(EventModel.class);
                                eventModelList.add(eventModel);
                                eventsAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error"+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void OnClickJoinEventClick(int position) {
       addEventToProfile();
    }

    private void addEventToProfile() {
    }
}