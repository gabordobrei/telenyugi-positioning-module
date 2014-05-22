
package hu.bme.emt.telenyugi.server.db;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteJob;

public class InitJob extends SQLiteJob<Object> {

    private boolean demo;

    public InitJob(boolean demo) {
        this.demo = demo;
    }

    @Override
    protected Object job(SQLiteConnection conn) throws Throwable {

        String statement = "CREATE TABLE IF NOT EXISTS Users (ID TEXT PRIMARY KEY, Name TEXT)";
        conn.exec(statement);
        conn.exec("CREATE INDEX IF NOT EXISTS INDEX_ID ON Users(ID)");

        statement = "CREATE TABLE IF NOT EXISTS Pos (Time TEXT PRIMARY KEY, UserID TEXT, Long REAL, Lat REAL, Alt REAL)";
        conn.exec(statement);
        conn.exec("CREATE INDEX IF NOT EXISTS INDEX_ID ON Pos(Time)");

        conn.exec(statement);
        if (demo) {
            conn.exec("DELETE FROM Users");
            conn.exec("DELETE FROM Pos");
            statement = "INSERT INTO Users(ID, Name) VALUES(1111, \"Döbrei Gábor\")";
            conn.exec(statement);
            Calendar today = new GregorianCalendar();
            for (int j = 0; j < 10; j++) {
                for (int i = 0; i < 5; i++) {
                    String Time = Long
                            .toString((long) (today.getTimeInMillis() + Math
                                    .random() * 10000));

                    String Long = Double.toString(19.093 + 0.087 * (double) i / 20
                            * Math.random());

                    String Lat = Double.toString(47.553 + 0.076 * (double) i / 20
                            * Math.random());

                    statement = MessageFormat
                            .format("INSERT INTO Pos (Time, UserID, Long, Lat, Alt) VALUES(''{0}'', ''1111'', ''{1}'', ''{2}'', ''0.0'')",
                                    Time, Long, Lat);
                    System.out.println(statement);
                    conn.exec(statement);
                }
                today.add(Calendar.DATE, -1);
            }
        }
        return null;
    }
}
