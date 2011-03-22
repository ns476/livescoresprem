package in.skeh.LiveScoresGAE;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

public abstract class UpdateWatcherServlet extends HttpServlet {
	
	protected abstract Serializable getData();
	protected abstract void valueChanged();
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp, "update-watcher-" + req.getServletPath());
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp, Serializable cacheKey)
			throws ServletException, IOException {
		resp.setContentType("text/plain");
		System.err.println(cacheKey.toString());
		System.out.println("No problems");
		try {
			if(req.getHeader("X-AppEngine-Cron") == null || !req.getHeader("X-AppEngine-Cron").equals("true")) {
				System.err.println("DENIED");
				resp.setStatus(403);
				return;
			}
		} catch (NullPointerException e) {}
		
		Cache cache = null;
		
		try {
			CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			cache = cacheFactory.createCache(Collections.emptyMap());
		} catch (CacheException e) {
		}
		
		PrintStream os = System.err;
		
		Serializable newVal = getData();
		Serializable oldVal = null;
		if (!cache.containsKey(cacheKey)) {
			valueChanged();
			os.println("Changed - didn't exist before, sent messages");
		} else {
			oldVal = (Serializable) cache.get(cacheKey);
			if (newVal != null && oldVal != null && !newVal.equals(oldVal)) {
				os.println(newVal);
				os.println(oldVal);
				os.println("Changed - sending message");
				valueChanged();
			} else {
				os.println("Hasn't changed");
				os.println(newVal);
				os.println(oldVal);
			}
		}
		cache.put(cacheKey, newVal);

	}
}
