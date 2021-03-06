package com.jlrutilities.burbenrunner.Fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jlrutilities.burbenrunner.Fragments.RouteFragment.OnListFragmentInteractionListener;
import com.jlrutilities.burbenrunner.R;

import java.text.DecimalFormat;
import java.util.List;


public class MyRouteRecyclerViewAdapter extends RecyclerView.Adapter<MyRouteRecyclerViewAdapter.ViewHolder> {

  private final OnListFragmentInteractionListener mListener;

  private final List<String> routesValues;
  private final List<Integer> routesDBIds;
  private final List<Double> routesDistances;
  private final boolean isMetric;


  public MyRouteRecyclerViewAdapter(List<String> list, List<Integer> idList, List<Double> distanceList, boolean isMetric, OnListFragmentInteractionListener listener) {
    routesValues = list;
    routesDBIds = idList;
    routesDistances = distanceList;
    mListener = listener;
    this.isMetric = isMetric;
  }


  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_route_list_item, parent, false);
    return new ViewHolder(view);
  }


  // Set View Holder
  @Override
  public void onBindViewHolder(final ViewHolder holder, final int position) {
    String routeValueStr;

    if(routesValues.get(position).isEmpty()) {
      routeValueStr = "Unnamed Route";
    } else {
      routeValueStr = routesValues.get(position);
    }

    double formattedDist = getFormattedDistance(routesDistances.get(position));
    if (isMetric) {
      holder.mDistanceView.setText("Total Distance: " + formattedDist + " km");
    } else {
      holder.mDistanceView.setText("Total Distance: " + formattedDist + " mi");
    }

    holder.myString =  routesValues.get(position);
    holder.myDbId = routesDBIds.get(position);
    holder.myDistance = routesDistances.get(position);

    holder.mContentView.setText(routeValueStr);
    holder.mImageView.setBackgroundResource(R.drawable.baseline_my_location_black_24);

    holder.mView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (null != mListener) {
          // Notify the active callbacks interface (the activity, if the
          // fragment is attached to one) that an item has been selected.
          mListener.onClickListFragmentInteraction(position, holder.myDbId, holder.myString, holder.myDistance);
        }
      }
    });

    holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View view) {
        if (null != mListener) {
          mListener.onLongClickListFragmentInteraction(position, holder.myDbId);
          //return true;
        }
        return false;
      }
    });
  }


  private double getFormattedDistance(double dist){
    DecimalFormat df = new DecimalFormat("#.##");

    if (isMetric) {
      double metersToKm = dist * 0.001;
      double formatDist = Double.parseDouble(df.format(metersToKm));

      return formatDist;
    } else {
      double metersToMiles = dist * 0.00062137;
      double formatDist = Double.parseDouble(df.format(metersToMiles));

      return formatDist;
    }
  }


  @Override
  public int getItemCount() {
    return routesValues.size();
  }


  // Holder builder for above setting views
  public class ViewHolder extends RecyclerView.ViewHolder {

    public final View mView;
    public final TextView mContentView;
    public final TextView mDistanceView;
    public final ImageView mImageView;

    public String myString;
    public Integer myDbId;
    public double myDistance;


    public ViewHolder(View view) {
      super(view);
      mView = view;
      mContentView = view.findViewById(R.id.content);
      mDistanceView = view.findViewById(R.id.distanceDesc);
      mImageView = view.findViewById(R.id.imageListIcon);
    }


    @Override
    public String toString() {
      return super.toString() + " '" + mContentView.getText() + "'";
    }
  }
}
