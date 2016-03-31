package ch.hes_so.master.phonerally.game;

public class ListCheckpointModel {
    private float latitude;
    private float longitude;
    private boolean reached;

    public ListCheckpointModel(float latitude, float longitude, boolean reached) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.reached = reached;
    }

    public ListCheckpointModel(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.reached = false;
    }

    public void setReached(boolean reached) {
        this.reached = reached;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public boolean isReached() {
        return reached;
    }
}
