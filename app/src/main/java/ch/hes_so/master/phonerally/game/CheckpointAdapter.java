package ch.hes_so.master.phonerally.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.hes_so.master.phonerally.R;
import ch.hes_so.master.phonerally.level.Checkpoint;

public class CheckpointAdapter extends ArrayAdapter<Checkpoint> {
    private final Context mContext;
    private List<Checkpoint> listCheckpoints;
    private int currentCheckpointPosition;
    private float distance = -1;

    public CheckpointAdapter(Context context) {
        super(context, android.R.layout.activity_list_item);
        mContext = context;

        listCheckpoints = new ArrayList<>();
        this.currentCheckpointPosition = 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.checkpoint_row_layout, parent, false);

        // get the real position, not the hacked/overrided one using this.getItem()
        Checkpoint checkpoint = super.getItem(position);

        if (checkpoint.isReached()) {
            ImageView ivCheckpoint = (ImageView) rowView.findViewById(R.id.ivCheckpoint);
            ivCheckpoint.setImageResource(R.drawable.checkpoint_reached);
        }

        TextView checkpointRightLabel = (TextView) rowView.findViewById(R.id.tvCheckpointRight);
        String rightLabel = "";
        if (position == currentCheckpointPosition) {
            rightLabel = getFormattedDistance();
        }

        checkpointRightLabel.setText(rightLabel);

        return rowView;
    }

    private String getFormattedDistance() {
        if (distance > 10000) {
            return (int) (distance / 1000) + " km";
        } else {
            return (int) distance + " m";
        }
    }


    /**
     * return the item in <b>reversed</b> position
     */
    @Override
    public Checkpoint getItem(int position) {
        // reverse items to make items appear bottom to top
        return super.getItem(getCount() - 1 - position);
    }


    @Override
    public void insert(Checkpoint object, int index) {
        super.insert(object, index);
        this.listCheckpoints.add(index, object);
        currentCheckpointPosition = getCount() - 1;
    }

    public void markCheckpointAsReached() {
        Checkpoint chkpt = this.getItem(currentCheckpointPosition);
        chkpt.setReached(true);
        currentCheckpointPosition++;

        notifyDataSetChanged();
    }

    public void setDistance(float distance) {
        this.distance = distance;

        notifyDataSetChanged();
    }
}
