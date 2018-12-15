package de.wm.planungskalender;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class RVAdapterAbgesagt extends RecyclerView.Adapter<RVAdapterAbgesagt.EventViewHolder> {

    List<String> signedOut = null;

    RVAdapterAbgesagt(List<String> signedOut) {
        this.signedOut = signedOut;
    }

    @Override
    public int getItemCount() {
        return signedOut.size();
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.signed_in_out, viewGroup, false);
        EventViewHolder pvh = new EventViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {
        if (signedOut.get(i) != "") {
            int j = i + 1;
            eventViewHolder.id.setText("" + j);
            eventViewHolder.eventSignedOut.setText(signedOut.get(i));
            eventViewHolder.eventSignedOutGuest.setText("");
        } else {
            eventViewHolder.id.setVisibility(View.GONE);
            eventViewHolder.eventSignedOut.setVisibility(View.GONE);
            eventViewHolder.divider.setVisibility(View.GONE);

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {


        TextView eventSignedOut;
        TextView eventSignedOutGuest;
        TextView id;

        View divider;

        EventViewHolder(View itemView) {
            super(itemView);

            eventSignedOut = itemView.findViewById(R.id.name);
            eventSignedOutGuest = itemView.findViewById(R.id.name2);
            id = itemView.findViewById(R.id.id);

            divider = itemView.findViewById(R.id.divider);
        }
    }
}