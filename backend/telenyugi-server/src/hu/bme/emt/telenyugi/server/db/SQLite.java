
package hu.bme.emt.telenyugi.server.db;

import hu.bme.emt.telenyugi.server.ExitingErrorHandler;

import java.io.File;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteQueue;

public class SQLite {

    // private SQLiteConnection conn;
    private File databaseFile;
    private static final String DB_PATH = "db/indoorpos";
    private SQLiteQueue queue;
    private static SQLite instance = null;

    public static synchronized SQLite get(boolean demo) {
        if (instance == null) {
            try {
                instance = new SQLite();
                instance.queue().execute(new InitJob(demo));
            } catch (SQLiteException e) {
                new ExitingErrorHandler().handleError(e);
            }
        }
        return instance;
    }

    public static synchronized SQLite get() {
        return get(false);
    }

    public SQLite() throws SQLiteException {
        databaseFile = new File(DB_PATH);
        // conn = new SQLiteConnection();
        // conn.open(true);
        queue = new SQLiteQueue(databaseFile);
        queue.start();
    }

    public static boolean deleteDBFile() {
        File file = new File(DB_PATH);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    public SQLiteQueue queue() {
        return queue;
    }

    public void dispose() throws InterruptedException {
        if (queue != null) {
            queue.stop(true).join();
        }
    }

}
