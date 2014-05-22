package hu.bme.emt.telenyugi.server;

import hu.bme.emt.telenyugi.server.db.SQLite;
import hu.bme.emt.telenyugi.server.log.Logger;

import java.util.Locale;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;

public class TelenyugiServer {

	public static void main(String[] args) throws Exception {
		Server server = new Server(8082);
		boolean demoParam = false;
		for (String param : args) {
            if("-d".matches(param)) {
                demoParam = true;
            }
        }
		Logger.getAnonymusLogger().i("Debug flag: " + Boolean.toString(demoParam));
		      

		SQLite.get(demoParam);
		HandlerList handlers = new HandlerList();

		handlers.addHandler(new HelloHandler());
		handlers.addHandler(new JSONHandler());
		handlers.addHandler(new ListHandler());
		handlers.addHandler(new CheckHandler());

		Locale.setDefault(Locale.US);

		server.setHandler(handlers);
		server.start();
		server.join();
	}

}
