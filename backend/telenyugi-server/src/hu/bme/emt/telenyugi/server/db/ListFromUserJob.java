package hu.bme.emt.telenyugi.server.db;

import hu.bme.emt.telenyugi.model.User;

import java.text.MessageFormat;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteStatement;

public class ListFromUserJob extends SQLiteJob<User> {

	private String ID;

	public ListFromUserJob(String ID) {
		this.ID = ID;
	};

	@Override
	protected User job(SQLiteConnection arg0) throws Throwable {

		String statement = MessageFormat.format(
				"SELECT * FROM Users WHERE ID = ''{0}''", ID);
		SQLiteStatement stmt = arg0.prepare(statement);
		stmt.step();
		try {
			return new User(stmt.columnInt(0), stmt.columnString (1));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return new User(1010, "User#"+ID);
	}
}
