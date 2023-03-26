package jpabasic.ex1;

import jakarta.persistence.*;
import jpabasic.ex1.domain.Address;
import jpabasic.ex1.domain.AddressEntity;
import jpabasic.ex1.domain.Member;
import jpabasic.ex1.domain.Period;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
public class Ex1HelloJpaApplication {

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();

		tx.begin();

		try{
			Member member = new Member();
			member.setUsername("member1");
			member.setHomeAddress(new Address("Seoul", "Gangnam", "11111"));

			// 값 타입 저장
			member.getFavoriteFoods().add("치킨");
			member.getFavoriteFoods().add("족발");
			member.getFavoriteFoods().add("피자");

			member.getAddressesHistory().add(new AddressEntity("oldCity1", "oldStreet1", "22222"));
			member.getAddressesHistory().add(new AddressEntity("oldCity2", "oldStreet2", "33333"));

			em.persist(member);

			em.flush();
			em.clear();

			// 값 타입 조회 : 값 타입 컬렉션은 지연 로딩이다.
			Member foundMember = em.find(Member.class, member.getId());
//
//			// 값 타입 수정 : 완전히 새로운 인스턴스로 갈아끼워야 한다.
//			// 치킨 -> 한식 ( String 자체도 값 타입 )
//			foundMember.getFavoriteFoods().remove("치킨");
//			foundMember.getFavoriteFoods().add("한식");
//
//			// equals and hashcode. remove
//			foundMember.getAddressesHistory().remove(new AddressEntity("oldCity1", "oldStreet1", "22222"));
//			foundMember.getAddressesHistory().add(new AddressEntity("newCity1", "newStreet1", "33333"));

			tx.commit();
		}catch (Exception e){
			tx.rollback();
		}finally {
			em.close();
		}

		emf.close();

	}

	private static void valueTypeCollectionTest1(EntityManager em) {
		Address address1 = new Address("Seoul", "Gangnam", "11111");
		Address address2 = new Address("Busan", "Seomyun", "22222");

		Member member = new Member();
		member.setUsername("user");
		em.persist(member);

		Member member2 = new Member();
		member2.setUsername("user2");
//		member2.setAddressesHistory(new ArrayList<>(Arrays.asList(address1, address2)));
		em.persist(member2);
	}

	private static void valueTypeCopyTest(EntityManager em) {
		Address address = new Address("NewYork", "BroadWay", "11111");

		Member memberA = new Member();
		memberA.setUsername("memberA");
		memberA.setHomeAddress(address);
		em.persist(memberA);

		// 값 타입 복사
		Address copyAddress = new Address("Paris", address.getStreet(), address.getZipcode());

		Member memberB = new Member();
		memberB.setUsername("memberB");
		memberB.setHomeAddress(copyAddress);
		em.persist(memberB);
	}

	private static void embeddedTest(EntityManager em) {
		Address homeAddress = new Address("Seoul", "Gangnam", "11111");
		Address companyAddress = new Address("Busan", "Seomyun", "22222");
		Period period = new Period(LocalDateTime.now(), LocalDateTime.now());

		Member userA = new Member();
		userA.setUsername("userA");
		userA.setHomeAddress(homeAddress);
		userA.setCompanyAddress(companyAddress);
		userA.setWorkPeriod(period);
		em.persist(userA);
	}

	private static void proxyTest(EntityManager em) {
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
	}

}
