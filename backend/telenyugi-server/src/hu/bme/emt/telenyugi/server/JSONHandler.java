package hu.bme.emt.telenyugi.server;

import static hu.bme.emt.telenyugi.server.msg.StatusMessage.Status.CLIENT_ERROR;
import static hu.bme.emt.telenyugi.server.msg.StatusMessage.Status.OK;
import static hu.bme.emt.telenyugi.server.msg.StatusMessage.Status.SERVER_ERROR;
import hu.bme.emt.telenyugi.server.db.InsertPosJob;
import hu.bme.emt.telenyugi.server.db.SQLite;
import hu.bme.emt.telenyugi.server.log.Logger;
import hu.bme.emt.telenyugi.server.msg.StatusMessage;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gson.Gson;

public class JSONHandler extends ContextHandler {

	private SQLite sqlite;
	private Gson gson;
	private static final Logger LOG = new Logger(JSONHandler.class);

	public JSONHandler() {
		super("/Add");
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
			String timestampParameter = baseRequest.getParameter("time");
			String userID = baseRequest.getParameter("userId");
			String latitudeParameter = baseRequest.getParameter("lat");
			String longitudeParameter = baseRequest.getParameter("long");
			String altitudeParameter = baseRequest.getParameter("alt");

			Preconditions.checkArgument(!Strings.isNullOrEmpty(userID));
			Preconditions.checkArgument(!Strings
					.isNullOrEmpty(timestampParameter));
			Preconditions.checkArgument(!Strings
					.isNullOrEmpty(latitudeParameter));
			Preconditions.checkArgument(!Strings
					.isNullOrEmpty(longitudeParameter));

			double longitude = 0.0, latitude = 0.0, altitude = 0.0;
			// int userId;

			try {

				longitude = Double.valueOf(longitudeParameter);
				latitude = Double.valueOf(latitudeParameter);
				if (!Strings.isNullOrEmpty(altitudeParameter)) {
					altitude = Double.valueOf(altitudeParameter);
				}

			} catch (NumberFormatException e) {
				writer.print(gson.toJson(CLIENT_ERROR
						.createMessage("Wrong number format!")));
				return;
			}

			try {
				LOG.i(MessageFormat
						.format("Inserting pos from {0}. (lat={1}, long={2}, alt={3}, timestamp={4} ({5}) )",
								userID, Double.toString(latitude), Double
										.toString(longitude), Double
										.toString(altitude),
								timestampParameter, new SimpleDateFormat("yyyy-MM-dd").format(new Date(Long.parseLong(timestampParameter)))));
				// Insert something meaningful into DB
				InsertPosJob insertJob = new InsertPosJob(timestampParameter,
						userID, longitude, latitude, altitude);

				sqlite.queue().execute(insertJob).complete();
			} catch (Exception e) {
				writer.write(gson.toJson(SERVER_ERROR.createMessage(e
						.getMessage())));
				return;
			}
			// Rock & Roll
			boolean successfull = latitude != 0.0 && longitude != 0.0;
			StatusMessage message = successfull ? OK.createMessage()
					: CLIENT_ERROR.createMessage();
			writer.write(gson.toJson(message));
			

		} catch (Exception e) {
			writer.write(gson.toJson(SERVER_ERROR.createMessage(e.getMessage())));
		}

	}
}
