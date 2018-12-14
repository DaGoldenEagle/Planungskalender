package de.wm.planungskalender;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class RVAdapterTeilnehmer extends RecyclerView.Adapter<RVAdapterTeilnehmer.EventViewHolder> {

    List<String> signedIn = null;

    RVAdapterTeilnehmer(List<String> signedIn) {
        this.signedIn = signedIn;
    }

    @Override
    public int getItemCount() {
        return signedIn.size();
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.signed_in_out, viewGroup, false);
        EventViewHolder pvh = new EventViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {
        if (signedIn.get(i) != "") {
            int j = i + 1;
            eventViewHolder.id.setText("" + j);
            if (signedIn.get(i).contains("Guest")) {

                eventViewHolder.eventSignedIn.setText("");
                eventViewHolder.eventSignedInGuest.setText(signedIn.get(i).replace("Guest", ""));
            } else {
                eventViewHolder.eventSignedIn.setText(signedIn.get(i));
                eventViewHolder.eventSignedInGuest.setText("");
            }

        } else {
            eventViewHolder.id.setVisibility(View.GONE);
            eventViewHolder.eventSignedIn.setVisibility(View.GONE);
            eventViewHolder.divider.setVisibility(View.GONE);

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {


        TextView eventSignedIn;
        TextView eventSignedInGuest;
        TextView id;

        View divider;

        EventViewHolder(View itemView) {
            super(itemView);

            eventSignedIn = (TextView) itemView.findViewById(R.id.name);
            eventSignedInGuest = (TextView) itemView.findViewById(R.id.name2);
            id = (TextView) itemView.findViewById(R.id.id);

            divider = (View) itemView.findViewById(R.id.divider);
        }
    }
}