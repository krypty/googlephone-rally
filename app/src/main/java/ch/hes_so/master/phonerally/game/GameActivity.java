package ch.hes_so.master.phonerally.game;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import ch.hes_so.master.phonerally.R;
import ch.hes_so.master.phonerally.level.Checkpoint;
import ch.hes_so.master.phonerally.level.Level;
import ch.hes_so.master.phonerally.level.LevelConstants;
import ch.hes_so.master.phonerally.level.LevelLoader;

public class GameActivity extends Activity implements GameService.IGameService {
    private static final String TAG = GameActivity.class.getSimpleName();
    public static final String LEVEL_TO_LOAD_KEY = "level_to_load";

    private ListView checkpointsListView;
    private CheckpointAdapter checkpointAdapter;

    private boolean bounded;
    private GameService gameService;
    private String levelToLoad;

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
        this.levelToLoad = bundle.getString(GameActivity.LEVEL_TO_LOAD_KEY, defaultLevel);
        Toast.makeText(GameActivity.this, "I must load level: " + levelToLoad, Toast.LENGTH_LONG).show();

        checkpointAdapter = new CheckpointAdapter(this);
        checkpointsListView.setAdapter(checkpointAdapter);

        buildCheckpointList(levelToLoad);

        // TODO: 31.03.16 remove me: draft code to update the list adapter
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                checkpointAdapter.getItem(0).setReached(true);
                checkpointAdapter.notifyDataSetChanged();
            }

        }, 2000); // 2000ms delay
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "startGameService");
        Intent gameServiceIntent = new Intent(getApplicationContext(), GameService.class);
        gameServiceIntent.putExtra(GameActivity.LEVEL_TO_LOAD_KEY, this.levelToLoad);
        // TODO: 30.04.16 add checkpoint extra too
        bindService(gameServiceIntent, serviceConn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.bounded) {
            unbindService(serviceConn);
            this.bounded = false;
        }
    }

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            gameService = ((GameService.Binder) service).getService();
            gameService.register(GameActivity.this);
            bounded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            if (gameService != null)
                gameService.unregister(GameActivity.this);
            bounded = false;
        }
    };

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

    @Override
    public void onDistanceChanged(float distance) {
        Log.d(TAG, "distance: " + distance);
    }
}
