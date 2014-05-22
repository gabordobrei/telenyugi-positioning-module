package hu.bme.emt.telenyugi.server;

import hu.bme.emt.telenyugi.model.Position;
import hu.bme.emt.telenyugi.model.User;
import hu.bme.emt.telenyugi.server.db.DateUtils;
import hu.bme.emt.telenyugi.server.db.LastPositionsJob;
import hu.bme.emt.telenyugi.server.db.ListFromUserJob;
import hu.bme.emt.telenyugi.server.db.SQLite;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class ListHandler extends ContextHandler {

	private SQLite sqlite;
	private static final String root = "public_html/";
	private SimpleDateFormat normalDayFormat;
	private SimpleDateFormat fullFormat;

	public ListHandler() {
		super("/List");
		sqlite = SQLite.get();
		normalDayFormat = new SimpleDateFormat("EEEEE - yyyy-MM-dd");
		fullFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	}

	@Override
	public void doHandle(String arg0, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);

		String userID = baseRequest.getParameter("userId");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(userID));
		PrintWriter writer = response.getWriter();

		User user;

		try {
			user = sqlite.queue().execute(new ListFromUserJob(userID))
					.complete();

			// Az elmúlt hét minden napjára megadja a pos-listet
			List<List<Position>> week;
			week = GetPositionsOfTheWeek(userID);

			// Az elmúlt hét minden napjára megadja a megtett út hosszát km-ben!
			List<Double> metersOfDay;
			metersOfDay = GetMetersOfTheDay(userID);

			/**********************************************
			 * Header, start of Google Api script
			 **********************************************/
			if (week.isEmpty()) {
				writer.println("<h3>This user has no data!</h3>");
				return;
			}
			Position center, southWest, northEast;
			if (week.get(7).size() > 0) {
				List<Position> initPos = InitializeMap(week.get(7));
				southWest = initPos.get(0);
				northEast = initPos.get(1);
				center = initPos.get(2);

			} else {
				center = new Position(19.09376, 47.55354);
				southWest = new Position(19.093, 47.553);
				northEast = new Position(19.094, 47.554);
			}

			writer.println(MessageFormat.format(
					fileReader(root + "header"),
					user.getName(),
					" <style type=\"text/css\"> html { height: 100% } body { height: 100%; margin: 0; padding: 0 } #panel { width: 100%; height: 100%; position:fixed; text-align:center } #map-canvas { align: center; height: 80%; width: 80%; display:inline-block } </style>",
					Double.toString(center.getLatitude()), Double
							.toString(center.getLongitude()), Double
							.toString(southWest.getLatitude()), Double
							.toString(southWest.getLongitude()), Double
							.toString(northEast.getLatitude()), Double
							.toString(northEast.getLongitude()), metersOfDay
							.get(0).toString(), metersOfDay.get(1).toString(),
					metersOfDay.get(2).toString(), metersOfDay.get(3)
							.toString(), metersOfDay.get(4).toString(),
					metersOfDay.get(5).toString(), metersOfDay.get(6)
							.toString(), metersOfDay.get(7).toString()));

			/**********************************************
			 * Add data to map
			 **********************************************/

			// addMarker(0, new google.maps.LatLng( 47.5571, 19.0895),
			// "2013-04-28 18-09-48");
			for (List<Position> day : week) {
				if (day.size() > 0) {
					for (Position position : day) {
						writer.println(MessageFormat
								.format("\t\t\taddMarker({0}, new google.maps.LatLng({1}, {2}),''{3}'');",
										week.indexOf(day), Double
												.toString(position
														.getLatitude()), Double
												.toString(position
														.getLongitude()),
										fullFormat.format(position
												.getTimestamp())));
					}
				}
			}

			writer.println(fileReader(root + "script1"));

			// google maps controllers
			Calendar today = Calendar.getInstance();

			for (int i = 0; i < 7; i++) {

				String METER = MessageFormat.format("Megtett út: {0} km.",
						metersOfDay.get(i));

				String DATE = normalDayFormat.format(today.getTime());

				String INDEX = Integer.toString(i);
				String COLOR = metersOfDay.get(i) > 2.0 ? "#00ff00"
						: metersOfDay.get(i) < 0.8 ? "#ff0000" : "#ffaa00";

				String OPACITY = metersOfDay.get(i) > 2.0 ? "0.5" : metersOfDay
						.get(i) < 0.8 ? "1" : "0.75";

				/*
				 * String WEIGHT = metersOfDay.get(i) > 2.0 ? "1" : metersOfDay
				 * .get(i) < 0.8 ? "3" : "2";
				 */
				String WEIGHT = "2";

				String message = "\t\t\t\tvar controlOptions"
						+ i
						+ " = { METER: \""
						+ METER
						+ "\", "
						+ "DATE: \""
						+ DATE
						+ "\", index: "
						+ INDEX
						+ ", "
						+ "color: \""
						+ COLOR
						+ "\", opacity: "
						+ OPACITY
						+ ", "
						+ "weight: "
						+ WEIGHT
						+ "};"
						+ "var Control"
						+ i
						+ " = new CreateControl(ControlDiv, map, controlOptions"
						+ i + "); ControlDiv.index = " + (i + 1) + ";";

				writer.println(message);
				today.add(Calendar.DATE, -1);

			}

			/************* Egész hét. *************/
			String METER = MessageFormat.format("Megtett út: {0} km.",
					metersOfDay.get(7));

			String message = "\t\t\t\tvar controlOptions7 = { METER: \""
					+ METER
					+ "\", DATE: 'Egész hét', index: 7, color: \"#188266\", opacity: 1.0, weight: 2};"
					+ "var Control7 = new CreateControl(ControlDiv, map, controlOptions7); ControlDiv.index = 8;";

			writer.println(message);
			/**************************************/

			/**********************************************
			 * End of script
			 **********************************************/
			writer.println(fileReader(root + "script2"));

			/**********************************************
			 * Body, end of html
			 **********************************************/
			writer.println(MessageFormat.format(fileReader(root + "body"),
					user.getName()));

		} catch (Exception e) {
			e.printStackTrace();
			writer.println("Error occurred: " + e.getMessage());
		}

		baseRequest.setHandled(true);

	}

	private List<Double> GetMetersOfTheDay(String userID) {

		List<Double> toReturn = new ArrayList<Double>();
		Calendar today = new GregorianCalendar();
		Double week = 0.0;

		for (int i = 0; i < 7; i++) {
			Double daily = getDistanceByDay(userID, i) / 1000;
			week += daily;
			toReturn.add(daily);
			today.add(Calendar.DATE, -1);
		}

		toReturn.add(week);

		return toReturn;

	}

	private List<List<Position>> GetPositionsOfTheWeek(String userID) {

		List<List<Position>> toReturn = new ArrayList<List<Position>>();

		List<Position> week = new ArrayList<Position>();
		toReturn.add(sqlite
				.queue()
				.execute(
						new LastPositionsJob(userID, DateUtils.getMidnight()
								.getTime())).complete());
		for (int i = 1; i < 7; i++) {

			List<Position> today = sqlite
					.queue()
					.execute(
							new LastPositionsJob(userID, DateUtils.getMidnight(
									i + 1).getTime(), DateUtils.getMidnight(i)
									.getTime())).complete();
			toReturn.add(today);
			week.addAll(today);
		}

		toReturn.add(week);

		return toReturn;
	}

	private List<Position> InitializeMap(List<Position> pos) {

		double minlng, maxlng, minlat, maxlat;
		double centerlat = 0;
		double centerlong = 0;

		minlng = pos.get(0).getLongitude();
		maxlng = pos.get(0).getLongitude();
		minlat = pos.get(0).getLatitude();
		maxlat = pos.get(0).getLatitude();

		for (Position position : pos) {
			minlng = position.getLongitude() <= minlng ? position
					.getLongitude() : minlng;
			maxlng = position.getLongitude() >= maxlng ? position
					.getLongitude() : maxlng;
			minlat = position.getLatitude() <= minlat ? position.getLatitude()
					: minlat;
			maxlat = position.getLatitude() >= maxlat ? position.getLatitude()
					: maxlat;
		}

		for (Position position : pos) {
			centerlat += position.getLatitude();
			centerlong += position.getLongitude();
		}

		centerlat /= pos.size();
		centerlong /= pos.size();

		List<Position> toReturn = new ArrayList<Position>();

		// southWest, northEast, center

		toReturn.add(new Position(maxlng, maxlat));
		toReturn.add(new Position(minlng, minlat));
		toReturn.add(new Position(centerlong, centerlat));
		return toReturn;
	}

	public double getDistanceByDay(String userID, int i) {

		return getDistanceCovered(sqlite
				.queue()
				.execute(
						new LastPositionsJob(userID, DateUtils.getMidnight(i)
								.getTime())).complete())
				- getDistanceCovered(sqlite
						.queue()
						.execute(
								new LastPositionsJob(userID, DateUtils
										.getMidnight(i - 1).getTime()))
						.complete());
	}

	public double getDistanceToday(String userID) {
		return getDistanceCovered(sqlite
				.queue()
				.execute(
						new LastPositionsJob(userID, DateUtils.getMidnight()
								.getTime(), DateUtils.getMidnight(-1).getTime()))
				.complete());
	}

	public double getDistanceCurrentWeek(String userID) {
		return getDistanceCovered(sqlite
				.queue()
				.execute(
						new LastPositionsJob(userID, DateUtils.getStartOfWeek()
								.getTime(), DateUtils.getMidnight(-1).getTime()))
				.complete());
	}

	private double getDistanceCovered(List<Position> positions) {
		if (positions.size() < 2) {
			return 0;
		}
		Collections.sort(positions);
		double result = 0;
		for (int i = 0; i < positions.size() - 1; i++) {
			Position from = positions.get(i);
			Position to = positions.get(i + 1);
			result += haversine(from, to);
		}
		return result;
	}

	public double haversine(Position from, Position to) {

		double R = 6371 * 1000;

		double dLongitude = Math.toRadians(from.getLongitude()
				- to.getLongitude());
		double dLatitude = Math
				.toRadians(from.getLatitude() - to.getLatitude());

		double sindLat = Math.sin(dLatitude / 2.0);
		double sindLong = Math.sin(dLongitude / 2.0);
		double a = sindLat * sindLat
				+ Math.cos(Math.toRadians(from.getLatitude()))
				* Math.cos(Math.toRadians(to.getLatitude())) * sindLong
				* sindLong;
		double b = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		double distance = R * b;

		return distance;

	}

	private String fileReader(String file) {

		StringBuilder text = new StringBuilder();
		String NL = System.getProperty("line.separator");
		Scanner scanner;
		try {
			scanner = new Scanner(new FileInputStream(file));
			while (scanner.hasNextLine()) {
				text.append(scanner.nextLine() + NL);
			}

			scanner.close();
			return text.toString();
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: File Not Found");
			System.exit(-1);
			return null;
		}
	}
}
