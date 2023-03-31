# Welcome to my shop!
This Project is to make a small shop to practice spring and jpa.
## 💻 Used Tech
- Spring
- JPA

## 기능 목록
1. 회원 기능
   - 회원을 등록한다.
   - 회원을 조회한다.
2. 상품 기능
   - 상품을 등록한다.
   - 상품을 수정한다.
   - 상품을 조회한다.
3. 주문 기능
   - 상품을 주문한다. -> 상품 재고 관리와 연동, 배송 정보 입력
   - 주문 내역을 조회한다.
   - 주문을 취소한다. -> 상품 재고 관리와 연동

## 기타 요구사항
- 상품은 재고 관리가 필요하다. (need to manage stock amount)
- 상품의 종류는 도서, 음반, 영화가 있다. ( Product includes Book, Album, Movie)
- 상품을 카테고리로 구분할 수 있다.
- 상품 주문시 배송 정보를 입력할 수 있다.

## 연관 관계
- 회원은 여러 상품을 주문할 수 있다.
  - 한 회원은 여러 번 주문할 수 있다.
  - 한 번 주문할 때 여러 상품을 선택할 수 있다.