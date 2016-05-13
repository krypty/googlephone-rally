package ch.hes_so.master.phonerally.level;

public class Checkpoint {
    private static final double EPSILON = 1e6;

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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Checkpoint)) {
            return false;
        }

        Checkpoint other = (Checkpoint) o;

        boolean isEquals = true;
        isEquals &= Math.abs(this.latitude - other.latitude) < EPSILON;
        isEquals &= Math.abs(this.longitude - other.longitude) < EPSILON;
        isEquals &= this.range == other.range;
        isEquals &= this.content.equals(other.content);

        return isEquals;
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
