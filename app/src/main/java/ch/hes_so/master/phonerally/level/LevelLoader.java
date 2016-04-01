package ch.hes_so.master.phonerally.level;


import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LevelLoader extends AsyncTask<Void, Void, Level> {
    private String levelResId;
    private Context ctx;

    public LevelLoader(Context ctx, String levelResId) {
        this.levelResId = levelResId;
        this.ctx = ctx;
    }


    @Override
    protected Level doInBackground(Void... params) {
        try {
            // read json file from raw/levelXXX
            Resources res = ctx.getResources();
            InputStream ins = res.openRawResource(res.getIdentifier(
                    levelResId, "raw", ctx.getPackageName()));

            byte[] b = new byte[ins.available()];
            ins.read(b);

            // parse json file
            JSONObject jObj = new JSONObject(new String(b));
            String name = jObj.getString("name");

            List<Checkpoint> checkpoints = new ArrayList<>();

            JSONArray jCheckpoints = jObj.getJSONArray("checkpoints");
            int checkpointsLength = jCheckpoints.length();
            for (int i = 0; i < checkpointsLength; i++) {
                JSONObject jCheckpoint = jCheckpoints.getJSONObject(i);
                double latitude = jCheckpoint.getDouble("lat");
                double longitude = jCheckpoint.getDouble("long");
                int range = jCheckpoint.getInt("range");
                String content = jCheckpoint.getString("content");

                Checkpoint checkpoint = new Checkpoint(latitude, longitude, range, content);
                checkpoints.add(checkpoint);
            }


            Level level = new Level(name, checkpoints);
            return level;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
