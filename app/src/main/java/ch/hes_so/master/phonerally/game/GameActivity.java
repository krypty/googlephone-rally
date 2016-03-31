package ch.hes_so.master.phonerally.game;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.Toast;

import ch.hes_so.master.phonerally.R;
import ch.hes_so.master.phonerally.select_levels.SelectLevelActivity;

public class GameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ListView listView = (ListView) findViewById(R.id.lvCheckpoints);

        // TODO: 31.03.16 think what to do if this activity is restarted, we might lost current level
        Bundle bundle = getIntent().getExtras();
        //get level to load from SelectLevelActivity
        String defaultLevel = SelectLevelActivity.LEVEL_PREFIX + "1";
        String levelToLoad = bundle.getString(SelectLevelActivity.LEVEL_TO_LOAD_KEY, defaultLevel);
        Toast.makeText(GameActivity.this, "I must load level: " + levelToLoad, Toast.LENGTH_LONG).show();


        // TODO: 31.03.16 parse checkpoint from json file
        final ListCheckpointModel[] items = new ListCheckpointModel[]{
                new ListCheckpointModel(1.2f, 1.5f, true),
                new ListCheckpointModel(1.2f, 1.5f, true),
                new ListCheckpointModel(1.2f, 1.5f, false),
                new ListCheckpointModel(1.2f, 1.5f, false),
                new ListCheckpointModel(1.2f, 1.5f, false),
                new ListCheckpointModel(1.2f, 1.5f, false),
                new ListCheckpointModel(1.2f, 1.5f, false),
                new ListCheckpointModel(1.2f, 1.5f, false),
                new ListCheckpointModel(1.2f, 1.5f, false),
                new ListCheckpointModel(1.2f, 1.5f, false),
                new ListCheckpointModel(1.2f, 1.5f, false),
                new ListCheckpointModel(1.2f, 1.5f, false),
                new ListCheckpointModel(1.2f, 1.5f, false),
                new ListCheckpointModel(1.2f, 1.5f, false),
                new ListCheckpointModel(1.2f, 1.5f, false),
                new ListCheckpointModel(1.2f, 1.5f, false),
                new ListCheckpointModel(1.2f, 1.5f, false),
                new ListCheckpointModel(1.2f, 1.5f, false),
                new ListCheckpointModel(1.2f, 1.5f, false),
                new ListCheckpointModel(1.2f, 1.5f, false),
                new ListCheckpointModel(1.2f, 1.5f, false)
        };
        final CheckpointAdapter adapter = new CheckpointAdapter(this, items);
        listView.setAdapter(adapter);

        buildCheckpointList();


        // TODO: 31.03.16 start game loop here (using a service/thread), code in an other class please...

        // TODO: 31.03.16 remove me: draft code to update the list adapter
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(GameActivity.this, "lalalal", Toast.LENGTH_LONG).show();

                // DO NOT use items[i] directly !
                adapter.getItem(2).setReached(true);
                adapter.notifyDataSetChanged();
            }

        }, 2000); // 2000ms delay
    }

    private void buildCheckpointList() {
        // TODO: 31.03.16 parse levelToLoad json file and add parsed checkpoints to listview
    }
}
