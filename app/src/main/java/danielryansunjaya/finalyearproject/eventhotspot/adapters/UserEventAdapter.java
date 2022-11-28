package danielryansunjaya.finalyearproject.eventhotspot.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import danielryansunjaya.finalyearproject.eventhotspot.MainActivity;
import danielryansunjaya.finalyearproject.eventhotspot.R;
import danielryansunjaya.finalyearproject.eventhotspot.models.EventModel;
import danielryansunjaya.finalyearproject.eventhotspot.models.UserModel;

public class UserEventAdapter extends RecyclerView.Adapter<UserEventAdapter.ViewHolder> {

    private static final String TAG ="UserEventAdapter";
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseDatabase rtdb;

    private Context context;
    private List<EventModel> eventModelList;
    private OnClickCancelEventListener monClickCancelEventListener;

    public UserEventAdapter(Context context, List<EventModel> eventModelList, OnClickCancelEventListener monClickCancelEventListener) {
        this.context = context;
        this.eventModelList = eventModelList;
        this.monClickCancelEventListener = monClickCancelEventListener;

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        rtdb = FirebaseDatabase.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_profile, parent,false);
        return new ViewHolder(view, monClickCancelEventListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserEventAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Display item in CardView
        holder.title.setText(eventModelList.get(position).getTitle());
        holder.organizer.setText(eventModelList.get(position).getOrganizer());
        holder.pic.setText(eventModelList.get(position).getPic());
        holder.picEmail.setText(eventModelList.get(position).getEmail());
        holder.date.setText(eventModelList.get(position).getDate());
        holder.time.setText(eventModelList.get(position).getTime());
        holder.elePoint.setText("Ele Point\n"+ eventModelList.get(position).getElePoint());
        holder.location.setText("Block "+eventModelList.get(position).getLocation());

        if(!eventModelList.get(position).getAttended()){
            holder.cancelBtn.setText("Cancel");
        }else if(eventModelList.get(position).getCompleted()){
            holder.cancelBtn.setVisibility(View.GONE);
            holder.fillFormBtn.setVisibility(View.INVISIBLE);
            holder.elePoint.setText("Ele Point\n"+ eventModelList.get(position).getElePoint()+"\n\nCOMPLETED!");
        }else{
            holder.cancelBtn.setVisibility(View.GONE);
            holder.fillFormBtn.setVisibility(View.VISIBLE);
            holder.fillFormBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DocumentReference documentReference = db.collection("studentsJoinEvents")
                            .document(Objects.requireNonNull(auth.getUid()))
                            .collection("joinedEventList")
                            .document(eventModelList.get(position).getTitle());
                    HashMap<String, Object> updates = new HashMap<>();
                    updates.put("completed", true);
                    documentReference.update(updates);

                    rtdb.getReference()
                            .child("login")
                            .child(Objects.requireNonNull(auth.getUid()))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    UserModel userModel = snapshot.getValue(UserModel.class);
                                    assert userModel != null;
                                    int sElePoint = userModel.getElePoints();

                                    rtdb.getReference()
                                            .child("login")
                                            .child(auth.getUid())
                                            .child("elePoints")
                                            .setValue(sElePoint + eventModelList.get(position).getElePoint());

                                    Log.i(TAG+" [displayUserInfo]","Successfully display user's info!");
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.i(TAG+" [displayUserInfo]","Failed to display user's info!\n"+ error.getDetails());
                                }
                            });

                    // Open link for Fill up ELE Report
                    Uri uri = Uri.parse("https://apps.ucsiuniversity.edu.my/ecas/front.aspx");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);

                    holder.fillFormBtn.setVisibility(View.INVISIBLE);
                    holder.elePoint.setText("Ele Point\n"+ eventModelList.get(position).getElePoint()+"\nCOMPLETED!");
                }
            });
        }

        holder.cardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.profile_event_card_view_anim));
    }

    @Override
    public int getItemCount() { return eventModelList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CardView cardView;
        TextView title, organizer, pic, picEmail, date, time, location, elePoint;
        Button cancelBtn, fillFormBtn;
        ConstraintLayout additionalInfoLayout;

        OnClickCancelEventListener onClickCancelEventListener;

        public ViewHolder (@NonNull View itemView, OnClickCancelEventListener onClickCancelEventListener){
           super(itemView);

           this.onClickCancelEventListener = onClickCancelEventListener;

           cardView = itemView.findViewById(R.id.card_view_profile);
           additionalInfoLayout = itemView.findViewById(R.id.additionalInfoLayout);
           title = itemView.findViewById(R.id.title_text);
           organizer = itemView.findViewById(R.id.organizer_text);
           pic = itemView.findViewById(R.id.pic_text);
           picEmail = itemView.findViewById(R.id.picEmail_text);
           date = itemView.findViewById(R.id.date_text);
           time = itemView.findViewById(R.id.time_text);
           location = itemView.findViewById(R.id.location_text);
           elePoint = itemView.findViewById(R.id.elePoint_text);
           fillFormBtn = itemView.findViewById(R.id.fillFormBtn);
           cancelBtn = itemView.findViewById(R.id.cancelBtn);
           cancelBtn.setOnClickListener(this);

           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if (additionalInfoLayout.getVisibility() == View.VISIBLE){
                       additionalInfoLayout.setVisibility(View.GONE);
                   }else{
                       additionalInfoLayout.setVisibility(View.VISIBLE);
                   }
               }
           });
        }

        @Override
        public void onClick(View view) {
            onClickCancelEventListener.OnClickCancelEventClick(getAdapterPosition());
        }
    }

    public interface OnClickCancelEventListener{
        void OnClickCancelEventClick(int position);
    }
}
