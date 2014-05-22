package hu.bme.emt.telenyugi.server.db;

import java.text.MessageFormat;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteJob;

public class InsertPosJob extends SQLiteJob<Void> {

	/*
	 * Time TEXT, UserID INTEGER, Long DOUBLE, Lat DOUBLE, Alt DOUBLE;
	 */
	private String Time, UserID, Long, Lat, Alt;

	public InsertPosJob(final String Time, final String UserID,
			final double Long, final double Lat, final double Alt) {
		this.Time = Time;
		this.UserID = UserID;
		
		this.Long = Double.toString(Long);
		this.Lat = Double.toString(Lat);
		this.Alt = Double.toString(Alt);
	}

	@Override
	protected Void job(SQLiteConnection arg0) throws Throwable {
		String statement = MessageFormat
				.format("INSERT INTO Pos (Time, UserID, Long, Lat, Alt) VALUES(''{0}'', ''{1}'', ''{2}'', ''{3}'', ''{4}'')",
						Time, UserID, Long, Lat, Alt);

		try {
		arg0.exec(statement);
		}catch(SQLiteException e) {
		    // We just don't care...
		}
		
		return null;
	}

}
