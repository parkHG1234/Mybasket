package com.mysports.basketbook;

/**
 * Created by ldong on 2016-10-12.
 */

public class Leauge_MyRecord_MyData {
    String HomeTeam_name;
    String AwayTeam_name;
    String HomeTeam_score;
    String AwayTeam_score;
    String Match_date;
    String isWin;

    public void setHomeTeam_name(String homeTeam_name) {
        HomeTeam_name = homeTeam_name;
    }

    public void setAwayTeam_name(String awayTeam_name) {
        AwayTeam_name = awayTeam_name;
    }

    public void setHomeTeam_score(String homeTeam_score) {
        HomeTeam_score = homeTeam_score;
    }

    public void setAwayTeam_score(String awayTeam_score) {
        AwayTeam_score = awayTeam_score;
    }

    public void setMatch_date(String match_date) {
        Match_date = match_date;
    }

    public void setIsWin(String isWin) {
        this.isWin = isWin;
    }

    public String getHomeTeam_name() {
        return HomeTeam_name;
    }

    public String getAwayTeam_name() {
        return AwayTeam_name;
    }

    public String getHomeTeam_score() {
        return HomeTeam_score;
    }

    public String getAwayTeam_score() {
        return AwayTeam_score;
    }

    public String getMatch_date() {
        return Match_date;
    }

    public String getIsWin() {
        return isWin;
    }
}
