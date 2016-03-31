package ch.hes_so.master.phonerally.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.hes_so.master.phonerally.R;

public class CheckpointAdapter extends ArrayAdapter<ListCheckpointModel> {


    private final Context mContext;
    private final ListCheckpointModel[] mValues;

    public CheckpointAdapter(Context context, ListCheckpointModel[] values) {
        super(context, android.R.layout.activity_list_item, values);
        mContext = context;

        // reverse array to make items appear bottom to top
        mValues = reverseArray(values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.checkpoint_row_layout, parent, false);

        if (mValues[position].isReached()) {
            ImageView ivCheckpoint = (ImageView) rowView.findViewById(R.id.ivCheckpoint);
            ivCheckpoint.setImageResource(R.drawable.checkpoint_reached);
        }

        return rowView;
    }

    @Override
    public ListCheckpointModel getItem(int position) {
        // reverse items to make items appear bottom to top
        return super.getItem(getCount() - 1 - position);
    }

    private static ListCheckpointModel[] reverseArray(ListCheckpointModel[] arr) {
        List<ListCheckpointModel> list = Arrays.asList(arr);
        Collections.reverse(list);
        return (ListCheckpointModel[]) list.toArray();
    }
}
