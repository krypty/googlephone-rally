package ch.hes_so.master.phonerally.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ch.hes_so.master.phonerally.R;
import ch.hes_so.master.phonerally.level.Checkpoint;

public class CheckpointAdapter extends ArrayAdapter<Checkpoint> {
    private final Context mContext;

    public CheckpointAdapter(Context context) {
        super(context, android.R.layout.activity_list_item);
        mContext = context;

//        // reverse array to make items appear bottom to top
//        mValues = reverseArray(values);
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
        String rightLabel = checkpoint.getRange() + " {";
        checkpointRightLabel.setText(rightLabel);

        return rowView;
    }


    /**
     * return the item in <b>reversed</b> position
     */
    @Override
    public Checkpoint getItem(int position) {
        // reverse items to make items appear bottom to top
        return super.getItem(getCount() - 1 - position);
    }
}
