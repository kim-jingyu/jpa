package study.querydsl;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

/**
 * 샘플 데이터 추가
 */
@Profile("local")
@Component
@RequiredArgsConstructor
@Slf4j
public class InitMember {

    @Autowired
    InitMemberService initMemberService;

    @PostConstruct
    public void init() {
        log.info("@PostConstruct - init()");
        initMemberService.init();
    }

    @Component
    @RequiredArgsConstructor
    static class InitMemberService {
        private final EntityManager em;

        @Transactional
        public void init() {
            Team teamA = new Team("teamA");
            Team teamB = new Team("teamB");
            em.persist(teamA);
            em.persist(teamB);

            for (int i = 0; i < 100; i++) {
                Team selectedTeam = i % 2 == 0 ? teamA : teamB;
                em.persist(new Member("user" + i, i, selectedTeam));
            }

        }
    }
}
