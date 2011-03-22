package in.skeh.LiveScoresGAE;

import in.skeh.gaeutils.CacheUtils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ClearoutCacheServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		CacheUtils.clear("prem-live-scores-hash");
		CacheUtils.clear("prem-fixtures-hash");
		System.err.println("Cleared cache");
	}

}
