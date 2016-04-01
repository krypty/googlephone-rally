package ch.hes_so.master.phonerally.level;

public class Checkpoint {
    private double latitude;
    private double longitude;
    private int range;
    private String content;
    private boolean reached;

    public Checkpoint(double latitude, double longitude, int range, String content, boolean reached) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.range = range;
        this.content = content;
        this.reached = reached;

    }

    public Checkpoint(double latitude, double longitude, int range, String content) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.range = range;
        this.content = content;
        this.reached = false;
    }

    public void setReached(boolean reached) {
        this.reached = reached;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getRange() {
        return range;
    }

    public String getContent() {
        return content;
    }

    public boolean isReached() {
        return reached;
    }
}
