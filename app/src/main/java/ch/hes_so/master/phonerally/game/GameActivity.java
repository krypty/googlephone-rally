package ch.hes_so.master.phonerally.game;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import ch.hes_so.master.phonerally.R;
import ch.hes_so.master.phonerally.level.Checkpoint;
import ch.hes_so.master.phonerally.level.Level;
import ch.hes_so.master.phonerally.level.LevelConstants;
import ch.hes_so.master.phonerally.level.LevelLoader;
import ch.hes_so.master.phonerally.select_levels.SelectLevelActivity;

public class GameActivity extends Activity {

    private ListView checkpointsListView;
    private CheckpointAdapter checkpointAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); // allow to show a loading icon
        setContentView(R.layout.activity_game);

        checkpointsListView = (ListView) findViewById(R.id.lvCheckpoints);

        // TODO: 01.01.16 think what to do if this activity is restarted, we might lost current level. override onPause/onResume methods to save the level to load
        Bundle bundle = getIntent().getExtras();
        //get level to load from SelectLevelActivity
        String defaultLevel = LevelConstants.LEVEL_PREFIX + "1";
        String levelToLoad = bundle.getString(SelectLevelActivity.LEVEL_TO_LOAD_KEY, defaultLevel);
        Toast.makeText(GameActivity.this, "I must load level: " + levelToLoad, Toast.LENGTH_LONG).show();

        checkpointAdapter = new CheckpointAdapter(this);
        checkpointsListView.setAdapter(checkpointAdapter);

        buildCheckpointList(levelToLoad);


        // TODO: 31.03.16 start game loop here (using a service/thread), code in an other class please...

        // TODO: 31.03.16 remove me: draft code to update the list adapter
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(GameActivity.this, "lalalal", Toast.LENGTH_LONG).show();

                checkpointAdapter.getItem(0).setReached(true);
                checkpointAdapter.notifyDataSetChanged();
            }

        }, 2000); // 2000ms delay
    }

    private void buildCheckpointList(String levelToLoad) {
        new LevelLoader(getApplicationContext(), levelToLoad) {
            @Override
            protected void onPreExecute() {
                // show loading icon
                checkpointsListView.setClickable(false);
                setProgressBarIndeterminateVisibility(true);
            }

            @Override
            protected void onPostExecute(Level level) {
                List<Checkpoint> checkpoints = level.getCheckpoints();

                // insert checkpoint in LIFO
                for (Checkpoint checkpoint : checkpoints) {
                    checkpointAdapter.insert(checkpoint, 0);
                }
                checkpointAdapter.notifyDataSetChanged();

                // dismiss loading icon
                checkpointsListView.setClickable(true);
                setProgressBarIndeterminateVisibility(false);
            }
        }.execute();
    }
}
