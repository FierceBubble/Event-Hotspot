package danielryansunjaya.finalyearproject.eventhotspot.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import danielryansunjaya.finalyearproject.eventhotspot.R;
import danielryansunjaya.finalyearproject.eventhotspot.models.EventModel;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private Context context;
    private List<EventModel> eventModelList;
    private OnClickJoinEventListener monClickJoinEventListener;


    public EventsAdapter(Context context, List<EventModel> eventModelList, OnClickJoinEventListener monClickJoinEventListener) {
        this.context = context;
        this.eventModelList = eventModelList;
        this.monClickJoinEventListener = monClickJoinEventListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_event, parent,false);
        return new ViewHolder(view, monClickJoinEventListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Display item in cardview
        holder.title.setText(eventModelList.get(position).getTitle());
        holder.organizer.setText(eventModelList.get(position).getOrganizer());
        holder.pic.setText(eventModelList.get(position).getPic());
        holder.picEmail.setText(eventModelList.get(position).getEmail());
        holder.date.setText(eventModelList.get(position).getDate());
        holder.time.setText(eventModelList.get(position).getTime());
        holder.elePoint.setText(eventModelList.get(position).getElePoint());
        holder.location.setText(eventModelList.get(position).getLocation());

    }

    @Override
    public int getItemCount() {
        return eventModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title, organizer, pic, picEmail, date, time, location, elePoint;
        Button joinBtn;


        OnClickJoinEventListener onClickJoinEventListener;
        public ViewHolder(@NonNull View itemView, OnClickJoinEventListener onClickJoinEventListener) {
            super(itemView);

            this.onClickJoinEventListener = onClickJoinEventListener;

            title = itemView.findViewById(R.id.title_text);
            organizer = itemView.findViewById(R.id.organizer_text);
            pic = itemView.findViewById(R.id.pic_text);
            picEmail = itemView.findViewById(R.id.picEmail_text);
            date = itemView.findViewById(R.id.date_text);
            time = itemView.findViewById(R.id.time_text);
            location = itemView.findViewById(R.id.location_text);
            elePoint = itemView.findViewById(R.id.elePoint_text);
            joinBtn = itemView.findViewById(R.id.joinBtn);

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
