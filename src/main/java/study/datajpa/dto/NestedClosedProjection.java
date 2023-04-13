package study.datajpa.dto;

public interface NestedClosedProjection {
    String getUsername();

    Teaminfo getTeam();

    interface Teaminfo {
        String getTeamName();
    }
}
