package vc.wifilt;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import vc.wifilt.DeviceFragment.OnListFragmentInteractionListener;
import vc.wifilt.dummy.DummyContent.DummyItem;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.ViewHolder> {

    private final List<WifiP2pDevice> mValues;
    private final OnListFragmentInteractionListener mListener;

    public DeviceRecyclerViewAdapter(List<WifiP2pDevice> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).deviceName);
        String status;
        switch (mValues.get(position).status) {
            case 3:
                status = "Available";
                break;
            case 0:
                status = "Connected";
                break;
            case 2:
                status = "Failed";
                break;
            case 1:
                status = "Invited";
                break;
            case 4:
                status = "Unavailable";
                break;
            default:
                status = "Unknown";
        }
        holder.mStatusView.setText(status);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mStatusView;
        public WifiP2pDevice mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.deviceName);
            mStatusView = (TextView) view.findViewById(R.id.deviceStatus);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mStatusView.getText() + "'";
        }
    }
}
