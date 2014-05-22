package hu.bme.emt.telenyugi.server;

import hu.bme.emt.telenyugi.model.User;
import hu.bme.emt.telenyugi.server.db.InsertUsernameJob;
import hu.bme.emt.telenyugi.server.db.ReadUsersJob;
import hu.bme.emt.telenyugi.server.db.SQLite;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;

import com.google.common.base.Strings;

public class HelloHandler extends ContextHandler {

	private SQLite sqlite;

	public HelloHandler() {
		super("/Insert");
		sqlite = SQLite.get();
	}

	@Override
	public void doHandle(String arg0, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);

		String userID = baseRequest.getParameter("userId");
		String userName = baseRequest.getParameter("userName");

		if (!Strings.isNullOrEmpty(userID)) {
			if (!Strings.isNullOrEmpty(userName)) {
				sqlite.queue().execute(
				// new InsertUsernameJob(Integer.parseInt(userID),
				// userName))
						new InsertUsernameJob(userID, userName)).complete();
			} else {
				sqlite.queue().execute(
				// new InsertUsernameJob(Integer.parseInt(userID)))
						new InsertUsernameJob(userID)).complete();
			}
		}

		baseRequest.setHandled(true);

		PrintWriter writer = response.getWriter();
		writer.println("<h1>Users</h1>");
		List<User> list;
		try {
			list = sqlite.queue().execute(new ReadUsersJob()).get();
			writer.println("<ul>");
			System.out.println(System.currentTimeMillis());
			for (User user : list) {
				/*
				 * writer.println("<li>" + user.getName() + ": " + new
				 * Date(Long.
				 * parseLong(user.getAsdf())-Long.parseLong("1366268442392"))
				 * +"</li>");
				 */
				try {
					/*
					 * long d = new
					 * Date(Long.parseLong(user.getAsdf())).getTime() - new
					 * Date(Long.parseLong("1366268807539")).getTime();
					 * 
					 * long d = new
					 * Date(Long.parseLong("1366268807539")).getTime();
					 */

					writer.println("<li>" + user.getName() + ": "
							+ user.getID() + "</li>");
				} catch (Exception e) {
					writer.println("Error occurred: " + e.getMessage());
				}

			}
			writer.println("</ul>");
		} catch (Exception e) {
			writer.println("Error occurred: " + e.getMessage());
		}

	}
}
