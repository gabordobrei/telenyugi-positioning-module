package hu.bme.emt.telenyugi.server.db;

import java.text.MessageFormat;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteJob;

public class InsertUsernameJob extends SQLiteJob<Void> {

	private String name;
	private String ID;
	
	public InsertUsernameJob(String ID) {
		this.ID = ID;
		this.name = "John Doe";
	}
	
	public InsertUsernameJob(String ID, String name) {
		this.ID = ID;
		this.name = name;
	}

	@Override
	protected Void job(SQLiteConnection arg0) throws Throwable {
		String statement = MessageFormat.format(
				"INSERT INTO Users(ID, Name) VALUES(''{0}'', ''{1}'')", ID, name);
		
		arg0.exec(statement);
		return null;
	}

}
