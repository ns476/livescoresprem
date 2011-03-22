package in.skeh.LiveScoresGAE;

import in.skeh.LiveScores.Device;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.android.c2dm.server.C2DMessaging;

public class SendUpdateServlet extends HttpServlet {
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();
		
		String deviceRegistrationId = req.getParameter("deviceRegistrationId");
				
		C2DMessaging c2d = C2DMessaging.get(pmf);
		try {
			c2d.sendNoRetry(deviceRegistrationId, "collapseall", new HashMap<String, String[]>(), false);
		} catch (IOException ex) {
			if (ex.getMessage().equals("NotRegistered")) {
				System.err.println("Clearing out stale registration..");
				
				Query q = pm.newQuery(Device.class);
				q.setFilter("deviceRegistrationId==deviceRegistrationIdParam");
				q.declareParameters("String deviceRegistrationIdParam");
				
				@SuppressWarnings("unchecked")
				List<Device> results = (List<Device>) q.execute(deviceRegistrationId);
				
				for (Device d: results) {
					pm.deletePersistent(d);
				}
			} else {
				System.err.println("Error sending cloud-to-device message");
			}
		}
	}
}
