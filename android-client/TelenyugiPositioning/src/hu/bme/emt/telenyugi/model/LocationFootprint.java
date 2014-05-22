
package hu.bme.emt.telenyugi.model;

public class LocationFootprint implements Comparable<LocationFootprint> {

    public final long id;
    public final double latitude;
    public final double longitude;
    public final double altitude;
    public final long timestamp;
    private boolean uploaded;

    public LocationFootprint(long id, double latitude, double longitude, double altitude,
            long timestamp) {
        super();
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestamp = timestamp;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LocationFootprint [id=").append(id);
        builder.append(", latitude=").append(latitude);
        builder.append(", longitude=").append(longitude);
        builder.append(", altitude=").append(altitude);
        builder.append(", timestamp=").append(timestamp);
        builder.append(", uploaded=").append(uploaded);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int compareTo(LocationFootprint another) {
        return (timestamp < another.timestamp) ? -1 : ((timestamp > another.timestamp) ? 1 : 0);
    }
}
