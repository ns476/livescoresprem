package in.skeh.LiveScores;

import in.skeh.gaeutils.CacheUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LiveScores {
	public static final String SCORES_URL = "http://news.bbc.co.uk/sport1/hi/football/eng_prem/live_scores/default.stm";
	public static final String FIXTURES_URL = "http://news.bbc.co.uk/sport1/hi/football/eng_prem/fixtures/default.stm";

	private static final SimpleDateFormat format = new SimpleDateFormat(
			"EEEEEEE, d MMMMMM yyyy", Locale.US);
	private static final Pattern timePattern = Pattern.compile("\\d\\d:\\d\\d");
	private static final String CACHE_KEY = "prem-matches";

	public static List<PremMatch> getFixturesToday(Document doc)
			throws ParseException {
		List<PremMatch> matches = new ArrayList<PremMatch>();
		Date d = format.parse(format.format(new Date()));
		// d = format.parse("Saturday, 15 January 2011");

		for (Element e : doc.select("div.mvb")) {
			Date matchDay = null;
			try {
				matchDay = format.parse(e.children().text());
			} catch (ParseException err) {
				continue;
			}
			if (matchDay.equals(d)) {
				Element child = e.nextElementSibling();
				while (!child.nodeName().equals("hr")) {
					for (Element match : child.select(".mvb")) {
						Elements teams = match.select(".stats");
						String home = teams.get(0).text();
						String away = teams.get(1).text();

						Matcher m = timePattern.matcher(match.html());
						m.find();
						String time = m.group(0);
						matches
								.add(new PremMatch(home, null, away, null, time));

					}
					child = child.nextElementSibling();
				}
			}
		}

		return matches;

	}

	public static List<PremMatch> getLiveMatches(Document doc) {
		ArrayList<PremMatch> matches = new ArrayList<PremMatch>();
		Elements scores = doc.select(".matchScore");
		for (Element e : scores) {
			String homeTeamName = e.select(".homeTeam a").text();
			String awayTeamName = e.select(".awayTeam a").text();

			String matchScore = e.select(".score").text();
			String matchScores[] = matchScore.split("-");

			Integer homeScore;
			Integer awayScore;

			if (matchScores.length == 1) {
				homeScore = null;
				awayScore = null;
			} else {
				homeScore = Integer.parseInt(matchScores[0]);
				awayScore = Integer.parseInt(matchScores[1]);
			}

			String status = e.select(".matchStatus").text();

			PremMatch m = new PremMatch(homeTeamName, homeScore, awayTeamName,
					awayScore, status);
			matches.add(m);
		}
		return matches;
	}

	public static List<PremMatch> getMatches()
			throws IOException, ParseException {
			@SuppressWarnings("unchecked")
			List<PremMatch> premMatches = (List<PremMatch>) CacheUtils
					.get(CACHE_KEY);
			if (premMatches != null) {
				return premMatches;
			}
		List<PremMatch> cur = getLiveMatches(Jsoup.connect(SCORES_URL).get());
		if (cur.size() != 0) {
			return setMatches(cur);
		} else {
			return setMatches(getFixturesToday(Jsoup.connect(FIXTURES_URL)
					.get()));
		}
	}

	public static void clearMatches() {
		CacheUtils.clear(CACHE_KEY);
	}

	private static List<PremMatch> setMatches(List<PremMatch> cur) {
		CacheUtils.put(CACHE_KEY, cur);
		return cur;
	}

	public static void main(String[] args) throws IOException, ParseException {
		for (PremMatch m : getMatches()) {
			System.out.println(m.toString());
		}
	}
}