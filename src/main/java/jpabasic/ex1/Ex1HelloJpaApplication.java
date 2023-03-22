package jpabasic.ex1;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpabasic.ex1.domain.Member;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Ex1HelloJpaApplication {

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();

		tx.begin();

		try{
			Member member1 = new Member();
			Member member2 = new Member();
//			Member member3 = new Member();
			em.persist(member1);
			em.persist(member2);
//			em.persist(member3);

			tx.commit();
		}catch (Exception e){
			tx.rollback();
		}finally {
			em.close();
		}

		emf.close();

	}

}
