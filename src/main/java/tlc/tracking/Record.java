package tlc.tracking;

public class Record {
    // Needed by Jackson: https://stackoverrun.com/fr/q/9179093#33362329
    public Record() {}

    public Record(int id, double lat, double lon, String user, long timestamp) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.user = user;
        this.timestamp = timestamp;
    }

    public String toString() {
        return "Record(" +this.id+ ", " +this.lat+ ", " +this.lon+ ", " +this.user+ ", " +this.timestamp+")";
    }

    public int id;
    public double lat;
    public double lon;
    public String user;
    public long timestamp;
}