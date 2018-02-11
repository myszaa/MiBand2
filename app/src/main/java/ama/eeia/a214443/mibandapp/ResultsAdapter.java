package ama.eeia.a214443.mibandapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {
    private final Context context;
    ArrayList<String> addressList;
// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public Button buttonView;
        public ViewHolder(View v) {
            super(v);
            buttonView = v.findViewById(R.id.button3);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ResultsAdapter(ArrayList<String> addressList, Context context) {
        this.addressList = addressList;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_buttom_view, parent, false);

        // set the view's size, margins, paddings and layout parameters
        return  new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.buttonView.setText(addressList.get(position));
        holder.buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + addressList.get(position));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public void setAddresses(ArrayList<String> addressList) {
        this.addressList = addressList;
    }
}
