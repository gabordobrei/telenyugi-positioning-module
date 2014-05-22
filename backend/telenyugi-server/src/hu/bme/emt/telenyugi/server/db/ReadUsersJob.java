
package hu.bme.emt.telenyugi.server.db;

import hu.bme.emt.telenyugi.model.User;

import java.util.ArrayList;
import java.util.List;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteStatement;

public class ReadUsersJob extends SQLiteJob<List<User>> {
	
    @Override
    protected List<User> job(SQLiteConnection arg0) throws Throwable {
        SQLiteStatement stmt = arg0.prepare("SELECT * FROM Users");
        List<User> result = new ArrayList<User>();
        while (stmt.step()) {
            result.add(new User(stmt.columnInt(0), stmt.columnString(1)));
        }
        return result;
    }
}
