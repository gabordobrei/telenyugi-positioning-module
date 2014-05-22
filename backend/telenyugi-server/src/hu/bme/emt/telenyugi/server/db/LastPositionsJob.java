package hu.bme.emt.telenyugi.server.db;

import hu.bme.emt.telenyugi.model.Position;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteStatement;

public class LastPositionsJob extends SQLiteJob<List<Position>> {

	private String ID;
	private long startTime, endTime;

	public LastPositionsJob(String userID, long startTime, long endTime) {
		this.ID = userID;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public LastPositionsJob(String userID, long startTime) {
		this.ID = userID;
		this.startTime = startTime;
		this.endTime = 0;
	}

	@Override
	protected List<Position> job(SQLiteConnection arg0) throws Throwable {

		String statement;
		if (endTime != 0) {
			statement = MessageFormat
					.format("SELECT * FROM Pos WHERE UserID = ''{0}'' AND Time >= {1} AND Time <= {2}",
							ID, Long.toString(startTime),
							Long.toString(endTime));
		} else {
			statement = MessageFormat.format(
					"SELECT * FROM Pos WHERE UserID = ''{0}'' AND Time >= {1}",
					ID, Long.toString(startTime));
		}

		SQLiteStatement stmt = arg0.prepare(statement);

		List<Position> result = new ArrayList<Position>();

		while (stmt.step()) {
			Date d = new Date(stmt.columnLong(0));
			int userID = stmt.columnInt(1);
			double lan = stmt.columnDouble(2);
			double lat = stmt.columnDouble(3);
			double alt = stmt.columnDouble(4);
			result.add(new Position(d, userID, lan, lat, alt));
		}

		return result;
	}
}
