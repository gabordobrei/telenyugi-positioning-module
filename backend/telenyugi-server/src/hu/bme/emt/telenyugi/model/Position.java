package hu.bme.emt.telenyugi.model;

import java.text.MessageFormat;
import java.util.Date;

public class Position implements Comparable<Position> {

	private int userID;
	private double longitude, latitude, altitude;
	private Date timestamp;

	public Position(Date timestamp, int userID, double longitude,
			double latitude, double altitude) {

		this.timestamp = timestamp;
		this.userID = userID;
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
	}

	public Position(double longitude, double latitude, double altitude) {
		this.timestamp = null;
		this.userID = -1;
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
	}

	public Position(double longitude, double latitude) {
		this.timestamp = null;
		this.userID = -1;
		this.altitude = -3.14;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public int getUserID() {
		return userID;
	}

	@Override
	public int compareTo(Position another) {
		return (this.timestamp.getTime() < another.timestamp.getTime()) ? -1
				: ((this.timestamp.getTime() > another.timestamp.getTime()) ? 1
						: 0);
	}

	@Override
	public String toString() {
		return MessageFormat
				.format("[{0}: {1}, {2}]", getTimestamp().toString(), Double.toString(getLatitude()),
						Double.toString(getLongitude()));
	}
}
