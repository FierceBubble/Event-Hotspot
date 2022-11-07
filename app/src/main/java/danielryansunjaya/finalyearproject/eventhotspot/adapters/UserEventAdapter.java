package danielryansunjaya.finalyearproject.eventhotspot.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import danielryansunjaya.finalyearproject.eventhotspot.R;
import danielryansunjaya.finalyearproject.eventhotspot.models.EventModel;

public class UserEventAdapter extends RecyclerView.Adapter<UserEventAdapter.ViewHolder> {

    private Context context;
    private List<EventModel> eventModelList;
    private OnClickCancelEventListener monClickCancelEventListener;

    public UserEventAdapter(Context context, List<EventModel> eventModelList, OnClickCancelEventListener monClickCancelEventListener) {
        this.context = context;
        this.eventModelList = eventModelList;
        this.monClickCancelEventListener = monClickCancelEventListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_profile, parent,false);
        return new ViewHolder(view, monClickCancelEventListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserEventAdapter.ViewHolder holder, int position) {
        // Display item in cardview
        holder.title.setText(eventModelList.get(position).getTitle());
        holder.organizer.setText(eventModelList.get(position).getOrganizer());
        holder.pic.setText(eventModelList.get(position).getPic());
        holder.picEmail.setText(eventModelList.get(position).getEmail());
        holder.date.setText(eventModelList.get(position).getDate());
        holder.time.setText(eventModelList.get(position).getTime());
        holder.elePoint.setText("Ele Point\n"+ eventModelList.get(position).getElePoint());
        holder.location.setText("Block "+eventModelList.get(position).getLocation());

    }

    @Override
    public int getItemCount() { return eventModelList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title, organizer, pic, picEmail, date, time, location, elePoint;
        Button cancelBtn;
        ConstraintLayout additionalInfoLayout;

        OnClickCancelEventListener onClickCancelEventListener;

        public ViewHolder (@NonNull View itemView, OnClickCancelEventListener onClickCancelEventListener){
           super(itemView);

           this.onClickCancelEventListener = onClickCancelEventListener;

           additionalInfoLayout = itemView.findViewById(R.id.additionalInfoLayout);
           title = itemView.findViewById(R.id.title_text);
           organizer = itemView.findViewById(R.id.organizer_text);
           pic = itemView.findViewById(R.id.pic_text);
           picEmail = itemView.findViewById(R.id.picEmail_text);
           date = itemView.findViewById(R.id.date_text);
           time = itemView.findViewById(R.id.time_text);
           location = itemView.findViewById(R.id.location_text);
           elePoint = itemView.findViewById(R.id.elePoint_text);
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
