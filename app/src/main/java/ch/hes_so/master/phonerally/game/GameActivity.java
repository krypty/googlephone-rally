package ch.hes_so.master.phonerally.game;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.Toast;

import ch.hes_so.master.phonerally.R;

public class GameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        ListView listView = (ListView) findViewById(R.id.lvCheckpoints);

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

        }, 2000); // 5000ms delay
    }
}
