package hu.bme.emt.telenyugi.server.db;

import hu.bme.emt.telenyugi.model.Position;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteStatement;

public class ReadUserPosJob extends SQLiteJob<List<Position>> {

	private String ID;

	public ReadUserPosJob(final String ID) {
		this.ID = ID;
	}

	@Override
	protected List<Position> job(SQLiteConnection arg0) throws Throwable {
		String statement = MessageFormat.format(
				"SELECT * FROM Pos WHERE UserID = ''{0}''", ID);
		SQLiteStatement stmt = arg0.prepare(statement);

		List<Position> result = new ArrayList<Position>();

		while (stmt.step()) {
			
			result.add(new Position(new Date(Long.parseLong(stmt
					.columnString(0))), Integer.parseInt(stmt.columnString(1)),
					stmt.columnDouble(2), stmt.columnDouble(3), stmt
							.columnDouble(4)));
		}
		return result;
	}
}
