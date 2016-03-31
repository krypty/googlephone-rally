package ch.hes_so.master.phonerally.select_levels;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

public class LevelAdapter extends ArrayAdapter<ListLevelModel> {


    private final Context mContext;
    private final List<ListLevelModel> mValues;

    public LevelAdapter(Context context, List<ListLevelModel> values) {
        super(context, android.R.layout.simple_list_item_1, values);
        mContext = context;
        mValues = values;
    }


}
