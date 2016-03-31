package ch.hes_so.master.phonerally.select_levels;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ch.hes_so.master.phonerally.R;
import ch.hes_so.master.phonerally.game.GameActivity;

public class SelectLevelActivity extends Activity {

    private static final int MAX_LEVEL = 5;
    public static final String LEVEL_PREFIX = "level";
    public static final String LEVEL_TO_LOAD_KEY = "level_to_load";
    private static final String TAG = SelectLevelActivity.class.getSimpleName();
    private ListView mLevelListView;
    private LevelAdapter levelAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_select_level);

        mLevelListView = (ListView) findViewById(R.id.lvLevels);

        List<ListLevelModel> items = new ArrayList<>();
        levelAdapter = new LevelAdapter(SelectLevelActivity.this, items);
        mLevelListView.setAdapter(levelAdapter);

        populateLevelsAsync();

        mLevelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListLevelModel clickedItem = levelAdapter.getItem(position);

                String msg = clickedItem.getName() + "; " + clickedItem.getResourceId();
                Log.d(TAG, "clicked: " + msg);

                // start GameActivity with the selected level
                Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
                intent.putExtra(LEVEL_TO_LOAD_KEY, clickedItem.getResourceId());
                startActivity(intent);
            }
        });
    }

    private void populateLevelsAsync() {
        final List<ListLevelModel> levels = new ArrayList<>();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                mLevelListView.setClickable(false);
                setProgressBarIndeterminateVisibility(true);
            }

            @Override
            protected Void doInBackground(Void... params) {
                // parse levels name from json files

                int level = 1;

                try {
                    // try to load level starting with LEVEL_PREFIX,
                    // stop when exception occurs or MAX_LEVEL reached
                    do {
                        // read json file from raw/levelXXX
                        String resourceId = LEVEL_PREFIX + level;
                        Resources res = getResources();
                        InputStream ins = res.openRawResource(res.getIdentifier(
                                resourceId, "raw", getPackageName()));

                        byte[] b = new byte[ins.available()];
                        ins.read(b);

                        // parse json file
                        JSONObject jObj = new JSONObject(new String(b));
                        String levelName = jObj.getString("name");

                        // add level name to temp list
                        ListLevelModel levelModel = new ListLevelModel(levelName, resourceId);
                        levels.add(levelModel);
                    } while (level++ < MAX_LEVEL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // add parsed levels to adapter
                mLevelListView.setClickable(true);
                setProgressBarIndeterminateVisibility(false);

                levelAdapter.addAll(levels);
            }
        }.execute();
    }

}
