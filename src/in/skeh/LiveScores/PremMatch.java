package in.skeh.LiveScores;

import in.skeh.gaeutils.HashCodeUtil;

import java.io.Serializable;

public class PremMatch implements Comparable<PremMatch>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3610195342015545040L;
	
	private String homeTeamName;
	private Integer homeScore;

	private String awayTeamName;
	private Integer awayScore;

	private String status;

	public PremMatch(String homeTeamName, Integer homeScore, String awayTeamName,
			Integer awayScore, String status) {
		this.homeTeamName = homeTeamName;
		this.homeScore = homeScore;
		this.awayTeamName = awayTeamName;
		this.awayScore = awayScore;
		this.status = status;
	}

	public void setHomeTeamName(String homeTeamName) {
		this.homeTeamName = homeTeamName;
	}

	public String getHomeTeamName() {
		return homeTeamName;
	}

	public void setHomeScore(Integer homeScore) {
		this.homeScore = homeScore;
	}

	public Integer getHomeScore() {
		return homeScore;
	}

	public void setAwayTeamName(String awayTeamName) {
		this.awayTeamName = awayTeamName;
	}

	public String getAwayTeamName() {
		return awayTeamName;
	}

	public void setAwayScore(Integer awayScore) {
		this.awayScore = awayScore;
	}

	public Integer getAwayScore() {
		return awayScore;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public boolean isMatchCurrentlyPlaying() {
		return (awayScore != null && homeScore != null && !status.equals("FT"));
	}

	public String toString() {
		if (isMatchCurrentlyPlaying()) {
			return String.format("%13s %2d-%-2d %-13s %s", homeTeamName,
					homeScore, awayScore, awayTeamName, status);
		} else {
			return String.format("%13s   v   %-13s %s", homeTeamName,
					awayTeamName, status);
		}
	}

	public int compareTo(PremMatch otherMatch) {
		if (this.isMatchCurrentlyPlaying()
				&& !otherMatch.isMatchCurrentlyPlaying()) {
			return -1;
		} else if (!this.isMatchCurrentlyPlaying()
				&& otherMatch.isMatchCurrentlyPlaying()) {
			return 1;
		} else {
			return this.getHomeTeamName().compareTo(
					otherMatch.getHomeTeamName());
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PremMatch)) {
			return false;
		}
		PremMatch m = (PremMatch) obj;
		return homeTeamName.equals(m.getHomeTeamName()) &&
			awayTeamName.equals(m.getAwayTeamName()) &&
			homeScore == m.getHomeScore() &&
			awayScore == m.getAwayScore() &&
			status.equals(m.getStatus());
	}
	
	@Override
	public int hashCode() {
		int result = HashCodeUtil.SEED;
		
		result = HashCodeUtil.hash(result, homeTeamName);
		result = HashCodeUtil.hash(result, awayTeamName);
		result = HashCodeUtil.hash(result, homeScore);
		result = HashCodeUtil.hash(result, awayScore);
		result = HashCodeUtil.hash(result, status);
		
		return result;
	}
	
	public static void main(String[] args) {
		PremMatch m1 = new PremMatch("Man U", 1, "Chelsea", 0, "L");
		PremMatch m2 = new PremMatch("Man U", 1, "Chelsea", 0, "L");
		
		System.out.println(m1.equals(m2));
	}
}