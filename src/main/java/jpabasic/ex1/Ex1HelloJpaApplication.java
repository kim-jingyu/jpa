package jpabasic.ex1;

import jakarta.persistence.*;
import jpabasic.ex1.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.internal.PersistenceUnitUtilImpl;

@Slf4j
public class Ex1HelloJpaApplication {

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();

		tx.begin();

		try{
			Member member1 = new Member();
			member1.setUsername("user");
			em.persist(member1);
			em.flush();
			em.clear();

			Member member = em.getReference(Member.class, 1L);
			member.getUsername();

			log.info("프록시 인스턴스의 초기화 여부 = {}", Persistence.getPersistenceUtil().isLoaded(member));
			log.info("프록시 클래스 확인 = {}", member.getClass().getName());

			log.info("프록시 강제 초기화");
			org.hibernate.Hibernate.initialize(member);

			tx.commit();
		}catch (Exception e){
			tx.rollback();
		}finally {
			em.close();
		}

		emf.close();

	}

}
