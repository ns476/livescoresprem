package in.skeh.LiveScoresGAE;

import in.skeh.LiveScores.PremMatch;
import in.skeh.LiveScores.LiveScores;
import in.skeh.gaeutils.CacheUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.*;

import org.jsoup.Jsoup;

import flexjson.*;

@SuppressWarnings("serial")
public class LiveScoresGAEServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		List<PremMatch> matches = new ArrayList<PremMatch>();
		
		resp.setContentType("application/json");
		resp.setHeader("Cache-Control", "no-cache");
				
		try {
			matches = LiveScores.getMatches();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		
		resp.getWriter().println(new JSONSerializer().rootName("matches").serialize(matches));
	}
}
