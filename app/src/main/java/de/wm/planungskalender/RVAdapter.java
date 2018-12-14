package de.wm.planungskalender;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.EventViewHolder> {

    List<Event> events;
    boolean isOffline;

    RVAdapter(List<Event> events, boolean isOffline) {
        this.events = events;
        this.isOffline = isOffline;
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        EventViewHolder pvh = new EventViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final EventViewHolder eventViewHolder, int i) {
        if (events.get(i) != null) {
            List<String> months = Arrays.asList("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember");

            String dateMonth = events.get(i).get_date().substring(3, 4).replace("0", "") + events.get(i).get_date().substring(4, 5);

            eventViewHolder.eventDateDay.setText(events.get(i).get_date().substring(0, 3));
            eventViewHolder.eventDateMonth.setText(months.get(Integer.parseInt(dateMonth) - 1));
            eventViewHolder.eventDateYear.setText(events.get(i).get_date().substring(6, 10));

            eventViewHolder.eventName.setText(events.get(i).get_eventname());
            eventViewHolder.eventTime.setText(events.get(i).get_time());

            eventViewHolder.eventUsers.setText(String.valueOf(events.get(i).get_users()));
            if (isOffline) {
                eventViewHolder.eventSignedUp.setText(events.get(i).get_isSignedIn());
            } else if (events.get(i).get_isSignedIn().contains("?")) {
                eventViewHolder.eventSignedUp.setText(events.get(i).get_isSignedIn());
            } else {
                eventViewHolder.eventSignedUp.setText(events.get(i).get_isSignedIn());
            }
            eventViewHolder.eventNum.setText(String.valueOf(events.get(i).get_id()));

            eventViewHolder.id.setText(String.valueOf(i));
            eventViewHolder.id.setVisibility(View.GONE);



        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView eventName;
        TextView eventDateDay;
        TextView eventDateMonth;
        TextView eventDateYear;
        TextView eventTime;
        TextView eventUsers;
        TextView eventSignedUp;
        View eventSignedUpPic;
        TextView eventNum;
        CardView eventCV;

        TextView id;


        EventViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.EventName);
            eventDateDay = itemView.findViewById(R.id.dateday);
            eventDateMonth = itemView.findViewById(R.id.month);
            eventDateYear = itemView.findViewById(R.id.year);
            eventTime = itemView.findViewById(R.id.time);
            eventUsers = itemView.findViewById(R.id.users);
            eventSignedUp = itemView.findViewById(R.id.signedUp);
            eventSignedUpPic = itemView.findViewById(R.id.imageView4);
            eventNum = itemView.findViewById(R.id.Eventnum);
            eventCV = itemView.findViewById(R.id.cv);
            id = itemView.findViewById(R.id.id);
        }
    }




}