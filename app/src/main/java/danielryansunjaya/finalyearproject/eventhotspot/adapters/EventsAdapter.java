package danielryansunjaya.finalyearproject.eventhotspot.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import danielryansunjaya.finalyearproject.eventhotspot.R;
import danielryansunjaya.finalyearproject.eventhotspot.models.EventModel;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private static final String TAG ="EventAdapter";
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseDatabase rtdb;

    Context context;
    List<EventModel> eventModelList;
    List<String> userEventList;
    OnClickJoinEventListener monClickJoinEventListener;


    public EventsAdapter(Context context, List<EventModel> eventModelList, List<String> userEventList, OnClickJoinEventListener monClickJoinEventListener) {
        this.context = context;
        this.eventModelList = eventModelList;
        this.userEventList = userEventList;
        this.monClickJoinEventListener = monClickJoinEventListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_event, parent,false);
        return new ViewHolder(view, monClickJoinEventListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Display item in cardview
        holder.title.setText(eventModelList.get(position).getTitle());
        holder.organizer.setText(eventModelList.get(position).getOrganizer());
        holder.pic.setText(eventModelList.get(position).getPic());
        holder.picEmail.setText(eventModelList.get(position).getEmail());
        holder.date.setText(eventModelList.get(position).getDate());
        holder.time.setText(eventModelList.get(position).getTime());
        holder.elePoint.setText("Ele Point\n"+ eventModelList.get(position).getElePoint());
        holder.location.setText("Block "+eventModelList.get(position).getLocation());

        /*db.collection("studentsJoinEvents")
                .document(Objects.requireNonNull(auth.getUid()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        eventList = (List<String>) documentSnapshot.get("eventList");
                    }
                });
        Log.d(TAG, String.valueOf(eventList));*/

        if(userEventList.contains(eventModelList.get(position).getTitle())){
            holder.joinBtn.setText("Joined!");
            holder.joinBtn.setClickable(false);
        }

        holder.cardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.event_card_view_anim));

    }


    @Override
    public int getItemCount() {
        return eventModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CardView cardView;
        TextView title, organizer, pic, picEmail, date, time, location, elePoint;
        Button joinBtn;

        OnClickJoinEventListener onClickJoinEventListener;
        public ViewHolder(@NonNull View itemView, OnClickJoinEventListener onClickJoinEventListener) {
            super(itemView);

            this.onClickJoinEventListener = onClickJoinEventListener;

            cardView = itemView.findViewById(R.id.card_view_event);

            title = itemView.findViewById(R.id.title_text);
            organizer = itemView.findViewById(R.id.organizer_text);
            pic = itemView.findViewById(R.id.pic_text);
            picEmail = itemView.findViewById(R.id.picEmail_text);
            date = itemView.findViewById(R.id.date_text);
            time = itemView.findViewById(R.id.time_text);
            location = itemView.findViewById(R.id.location_text);
            elePoint = itemView.findViewById(R.id.elePoint_text);
            joinBtn = itemView.findViewById(R.id.joinBtn);

            Log.d(TAG, String.valueOf(userEventList));

            joinBtn.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onClickJoinEventListener.OnClickJoinEventClick(getAdapterPosition());
        }
    }

    public interface OnClickJoinEventListener {
        @SuppressLint("NotConstructor")
        void OnClickJoinEventClick(int position);
    }
}
