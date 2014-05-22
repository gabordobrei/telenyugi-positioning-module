package hu.bme.emt.telenyugi.server;

import static hu.bme.emt.telenyugi.server.msg.StatusMessage.Status.CLIENT_ERROR;
import static hu.bme.emt.telenyugi.server.msg.StatusMessage.Status.OK;
import static hu.bme.emt.telenyugi.server.msg.StatusMessage.Status.SERVER_ERROR;
import hu.bme.emt.telenyugi.model.Position;
import hu.bme.emt.telenyugi.server.db.LastPositionsJob;
import hu.bme.emt.telenyugi.server.db.SQLite;
import hu.bme.emt.telenyugi.server.log.Logger;
import hu.bme.emt.telenyugi.server.msg.StatusMessage;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gson.Gson;

public class CheckHandler extends ContextHandler {

	private SQLite sqlite;
	private Gson gson;
	private static final Logger LOG = new Logger(CheckHandler.class);

	public CheckHandler() {
		super("/Check");
		sqlite = SQLite.get();
		gson = new Gson();
	}

	@Override
	public void doHandle(String arg0, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		PrintWriter writer = response.getWriter();

		try {
			baseRequest.setHandled(true);
			response.setContentType("text/json;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);

			String userID = baseRequest.getParameter("userId");

			Preconditions.checkArgument(!Strings.isNullOrEmpty(userID));

			try {
				List<Position> lastPositions;
				// DB-ből lekérdezni az utolsó 10 pozíciót
				//lastPositions = sqlite.queue().execute(new LastPositionsJob(userID, 7, 0)).get();
				LOG.i(MessageFormat.format(
						"Querying last 10 positions from {0}", userID));
/*
				for (int i = 0; i < lastPositions.size(); i++) {
					//System.out.println(i + ": "
					writer.println(i + ": "
							+ lastPositions.get(i).getTimestamp() + ", "
							+ lastPositions.get(i).getLongitude() + ", "
							+ lastPositions.get(i).getLatitude() + ", "
							+ lastPositions.get(i).getAltitude());
				}

				// evaluate the client's status, from last positions info
				
	*/			
				boolean successfull = true; //lastPositions.size() != 0.0;
				StatusMessage message = successfull ? OK.createMessage()
						: CLIENT_ERROR.createMessage();
				writer.write(gson.toJson(message));

			} catch (Exception e) {
	            writer.write(gson.toJson(SERVER_ERROR.createMessage(e.getMessage())));
			}
		} catch (Exception e) {
			writer.write(gson.toJson(CLIENT_ERROR.createMessage(e.getMessage())));
		}
	}
}
