package in.skeh.LiveScoresGAE;

import in.skeh.LiveScores.Device;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import flexjson.JSONDeserializer;

public class RegisterServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Device sent = new JSONDeserializer<HashMap<String, Device>>().deserialize(new InputStreamReader(req.getInputStream())).get("device");
		
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();
		
		Query q = pm.newQuery(Device.class);
		q.setFilter("deviceId==deviceIdParam");
		q.declareParameters("String deviceIdParam");
		
		@SuppressWarnings("unchecked")
		List<Device> results = (List<Device>) q.execute(sent.getDeviceId());
		
		if (results.size() > 1) {
			// This shouldn't happen!
			for (Device d: results) {
				pm.deletePersistent(d);
			}
			pm.makePersistent(sent);
		} else if (results.size() == 1) {
			for (Device d: results) {
				d.setDeviceId(sent.getDeviceId());
				d.setDeviceRegistrationId(sent.getDeviceRegistrationId());
				pm.makePersistent(d);
			}
		} else {
			pm.makePersistent(sent);
		}
		
		pm.close();
	}
}

