package jpabasic.jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jpabasic.jpql.domain.*;
import jpabasic.real.domain.item.Item;

import java.util.List;

import static jpabasic.jpql.domain.MemberType.*;

public class JpqlMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            init(em);

            persistenceContextInit(em);



            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }finally {
            em.close();
        }
        emf.close();
    }

    private static void fetchJoinTest(EntityManager em) {
        String query = "select t from Team t";
        List<Team> teams = em.createQuery(query, Team.class)
                .setFirstResult(0)
                .setMaxResults(4)
                .getResultList();

        for (Team team : teams) {
            System.out.println("team.getName() = " + team.getName() + ", team = " + team);
            for (Member member: team.getMembers()) {
                System.out.println("member.getUsername() = " + member.getUsername() + ", member = " + member);
            }
        }
    }

    private static void jpqlBasicFunction(EntityManager em) {
        String query = "select locate('de','abcdef') from Member m";
        List<Integer> resultList = em.createQuery(query, Integer.class)
                .getResultList();

        for (Integer result : resultList) {
            System.out.println("result = " + result);
        }
    }

    private void caseTest2(EntityManager em) {
        // 사용자 이름이 없으면 이름 없는 회원을 반환한다.
        String query = "select coalesce(m.username, '이름 없는 회원') from Member m";
        List<String> resultList = em.createQuery(query, String.class)
                .getResultList();

        for (String result : resultList) {
            System.out.println("result = " + result);
        }

        // 사용자 이름이 관리자면 null 을 반환하고, 나머지는 본인의 이름을 반환한다.
        String query2 = "select nullif(m.username, '관리자') from Member m";
        List<String> resultList1 = em.createQuery(query2, String.class)
                .getResultList();

        for (String result : resultList1) {
            System.out.println("result = " + result);
        }
    }

    private void caseTest1(EntityManager em) {
        // 기본 case 식
        String query =
                "select " +
                        "case when m.age <= 10 then '학생요금' " +
                        "     when m.age >= 60 then '경로요금' " +
                        "     else '일반요금' " +
                        "end " +
                "from Member m";

        List<String> resultList = em.createQuery(query, String.class)
                .getResultList();

        for (String result : resultList) {
            System.out.println("result = " + result);
        }

        String query2 =
                "select " +
                        "case t.name " +
                            "when 'teamA' then '성과급 110%!!' " +
                            "when 'teamB' then '성과급 120%!!' " +
                            "else '성과급 없음.' " +
                        "end " +
                "from Team t";

        List<String> resultList1 = em.createQuery(query2, String.class)
                .getResultList();

        for (String result : resultList1) {
            System.out.println("result = " + result);
        }
    }

    private void typePresentTest(EntityManager em) {
        // JPQL 타입 표현
        String query = "select m.username, 'HELLO', true from Member m where m.memberType = :userType";
        List<Object[]> resultList = em.createQuery(query)
                .setParameter("userType", ADMIN)
                .getResultList();

        for (Object[] objects : resultList) {
            System.out.println("objects[0] = " + objects[0]);
            System.out.println("objects[1] = " + objects[1]);
            System.out.println("objects[현2] = " + objects[2]);
        }

        // 엔티티 타입 표현 ( 상속 관계에서 사용 )
        String query2 = "select i from Item i where type(i) = Book";
        List<Item> resultList1 = em.createQuery(query2, Item.class)
                .getResultList();

        for (Item item : resultList1) {
            System.out.println("item = " + item);
        }
    }

    private void subQueryTest3(EntityManager em) {
        // 팀 A 소속인 회원. exists (subquery)
        String query1 = "select m from Member m where exists (select t from m.team t where t.name = 'teamA')";
        List<Member> resultList1 = em.createQuery(query1, Member.class)
                .getResultList();

        for (Member member : resultList1) {
            System.out.println("member = " + member);
        }

        // 전체 상품 각 재고보다 주문량이 많은 주문들. ALL 모두 만족하면 참
        String query2 = "select o from Orders o where o.orderAmount > ALL (select p.stockAmount from Product p)";
        List<Orders> resultList2 = em.createQuery(query2, Orders.class)
                .getResultList();

        for (Orders orders : resultList2) {
            System.out.println("orders = " + orders);
        }

        // 어떤 팀이든 팀에 소속된 회원. ANY, SOME 조건을 하나라도 만족하면 참
        String query3 = "select m from Member m where m.team = ANY (select t from Team t)";
        List<Member> resultList = em.createQuery(query3, Member.class)
                .getResultList();

        for (Member member : resultList) {
            System.out.println("member = " + member);
        }

        String query4 = "select (select avg(m1.age) From Member m1) as avgAge from Member m";
        List<Object[]> resultList3 = em.createQuery(query4).getResultList();

        for (Object[] objects : resultList3) {
            System.out.println("objects[0] = " + objects[0]);
        }

        String query5 = "select mm.age, mm.username from (select m.age, m.username from Member m) as mm";
        List<Object[]> resultList4 = em.createQuery(query5).getResultList();

        for (Object[] objects : resultList4) {
            System.out.println("objects[0] = " + objects[0]);
        }
    }

    private void subQueryTest2(EntityManager em) {
        // 한 건이라도 주문한 고객
        String query = "select m from Member m where (select count(o) from Orders o where m = o.member) > 0";
        List<Member> resultList = em.createQuery(query, Member.class)
                .getResultList();

        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    private void subQueryTest1(EntityManager em) {
        // 나이가 평균보다 많은 회원 찾기
        String query = "select m from Member m where m.age > (select avg(m2.age) from Member m2)";
        List<Member> resultList = em.createQuery(query, Member.class)
                .getResultList();

        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    private void joinTest(EntityManager em) {
        String query = "select m from Member m right join m.team t on t.name = 'teamA'";
        List<Member> resultList = em.createQuery(query, Member.class)
                .getResultList();

        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    private void pagingTest(EntityManager em) {
        List<Member> resultList = em.createQuery("select m from Member m order by m.age desc", Member.class)
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList();

        System.out.println("resultList.size() = " + resultList.size());
        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    private void forPagingTestInit(EntityManager em) {
        for (int i = 0; i < 100; i++) {
            Member member = new Member();
            member.setUsername("member" + i);
            member.setAge(i);
            em.persist(member);
        }
    }

    private void scalaTypeProjection2(EntityManager em) {
        List<MemberDTO> resultList = em.createQuery("select new jpabasic.jpql.domain.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                .getResultList();

        for (MemberDTO memberDTO : resultList) {
            System.out.println("memberDTO.getUsername() = " + memberDTO.getUsername());
            System.out.println("memberDTO.getAge() = " + memberDTO.getAge());
        }
    }

    private static void persistenceContextInit(EntityManager em) {
        em.flush();
        em.clear();
    }

    private void scalaTypeProjection(EntityManager em) {
        List resultList = em.createQuery("select m.username, m.age from Member m")
                .getResultList();

        for (Object o : resultList) {
            Object[] result = (Object[]) o;
            System.out.println("username = " + result[0]);
            System.out.println("age = " + result[1]);
        }
    }

    private static void init(EntityManager em) {
        Team teamA = new Team();
        teamA.setName("teamA");
        em.persist(teamA);

        Team teamB = new Team();
        teamB.setName("teamB");
        em.persist(teamB);

        Team teamC = new Team();
        teamC.setName("teamC");
        em.persist(teamC);

        Team teamD = new Team();
        teamD.setName("teamD");
        em.persist(teamD);

        Team teamE = new Team();
        teamE.setName("teamE");
        em.persist(teamE);

        Member member1 = new Member();
        member1.setUsername("관리자");
        member1.setAge(20);
        member1.changeTeam(teamA);
        em.persist(member1);

        Member member2 = new Member();
        member2.setUsername("member2");
        member2.setAge(15);
        member2.setMemberType(ADMIN);
        member2.setTeam(teamA);
        em.persist(member2);

        Address address = new Address();
        address.setCity("Seoul");
        address.setStreet("Gangnam");
        address.setZipcode("11111");

        Product product = new Product();
        product.setName("사과");
        product.setPrice(1000);
        product.setStockAmount(100);
        em.persist(product);

        Orders orders = new Orders();
        orders.setOrderAmount(101);
        orders.setAddress(address);
        orders.setMember(member1);
        orders.setProduct(product);
        em.persist(orders);
    }

    private void nativeQueryTest1(EntityManager em) {
        // Query 가 실행되기 전에 flush 호출
        // Native SQL
        String sql = "select * from member where name = 'member1'";
        List<Member> resultList = em.createNativeQuery(sql, Member.class).getResultList();

        for (Member member : resultList) {
            System.out.println("멤버 = " + member);
        }
    }

    private void criteriaTest1(EntityManager em) {
        // Criteria 사용 준비
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Member> query = cb.createQuery(Member.class);

        // Root Class (조회를 시작할 클래스)
        Root<Member> m = query.from(Member.class);

        // 쿼리 생성
        CriteriaQuery<Member> cq = query.select(m).where(cb.equal(m.get("username"), "member1"));
        List<Member> resultList = em.createQuery(cq).getResultList();

        for (Member member : resultList) {
            System.out.println("멤버 = " + member);
        }
    }

    private void jpqlTest1(EntityManager em) {
        // JPQL 검색
        String jpql = "select m From Member m where m.age > 18";
        List<Member> result = em.createQuery(jpql, Member.class)
                .getResultList();

        for (Member member : result) {
            System.out.println("멤버 = " + member);
        }
    }
}
