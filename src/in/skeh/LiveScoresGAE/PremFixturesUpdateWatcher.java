package in.skeh.LiveScoresGAE;

import in.skeh.LiveScores.Device;
import in.skeh.LiveScores.LiveScores;
import in.skeh.gaeutils.CacheUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class PremFixturesUpdateWatcher extends UpdateWatcherServlet {
	final static String CACHE_KEY = "prem-fixtures-hash";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp, CACHE_KEY);
	}

	@Override
	protected Serializable getData() {
		try {
			return (Serializable) LiveScores.getFixturesToday(Jsoup.connect(LiveScores.FIXTURES_URL).get()).toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void valueChanged() {
		LiveScores.clearMatches();
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		Query q = pm.newQuery(Device.class);

		@SuppressWarnings("unchecked")
		List<Device> devices = (List<Device>) q.execute();
		Queue queue = QueueFactory.getDefaultQueue();

		for (Device d : devices) {
			queue.add(TaskOptions.Builder.withUrl("/prem-worker").param(
					"deviceRegistrationId", d.getDeviceRegistrationId()));
		}
	}
}
