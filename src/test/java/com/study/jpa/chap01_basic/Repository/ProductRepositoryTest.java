package com.study.jpa.chap01_basic.Repository;

import com.fasterxml.jackson.databind.deser.std.AtomicBooleanDeserializer;
import com.study.jpa.chap01_basic.entity.Product;
import org.hibernate.annotations.CreationTimestamp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.study.jpa.chap01_basic.entity.Product.Category.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // jpa 사용 시 달아주어야 하는 아노테이션(spring framework) : jpa는 트랜젝션 단위로 동장하기 때문에 설정
@Rollback(value = false) // 테스트 클래스에서 트랜잭션 사용하면 자동 Rollback하므로 방지
class ProductRepositoryTest {
    @Autowired
    ProductRepository productRepository;

    @Test
    @DisplayName("데이터 베이스에 상품을 저장해야 한다.")
    void testSave() {
        // given
        Product p = Product.builder()
                .name("구두")
                .price(300000)
                .category(FASHION)
                .build();
        // when
        Product saved = productRepository.save(p);// 상속받은 JpaRepository의 메서드인 save로 인해 insert됨

        // then
        assertNotNull(saved);
    }

    @Test
    @DisplayName("데이터 베이스에 id가 2인 데이터를 삭제해야 한다")
    void testRemove() {
        // given
        Long id = 2L;
        // when
        productRepository.deleteById(id); // 상속받은 JpaRepository의 메서드
        // then
    }

    @Test
    @DisplayName("상품 전체조회를 하면 상품의 개수는 2개여야 한다")
    void testFindAll() {
        // given

        // when
        List<Product> products = productRepository.findAll();
        // then
        products.forEach(System.out::println);
        assertEquals(2, products.size());
    }

    @Test
    @DisplayName("3번 상품을 조회하면 상품명이 구두일 것이다")
    void testFindOne() {
        // given
        Long id = 3L;
        // when
        Optional<Product> product = productRepository.findById(id);
        //Optional<T>: NPE(NullPointerException)을 방지하기 위한 객체 타입으로 Null check를 강제화한다(java 8 이상).
        // Optional 타입이 아니라면 null 값이 반환되었을 시 바로 에러가 발생할 수 있지만
        // Optional은 null 값을 객체로 감싸서 바로 에러가 나는 것을 방지하고, 후속 작업들을 메서드로 처리할 수 있도록 제공한다.
        
        // then
        product.ifPresent(p -> { // Null 값이 아니면 다음 코드 진행
            assertEquals("구두", p.getName());
        });
        Product foundProduct = product.orElseThrow(); // 만약 null이면 예외를 던지겠다
        assertNotNull(foundProduct);
    }

    @Test
    @DisplayName("1번 상품의 이름과 가격, 카테고리를 변경해야 한다")
    void testModify() {
        // given
        Long id = 1L;
        String newName = "짜장면";
        int newPrice = 6000;
        Product.Category newCategory = FOOD;

        // when
        /*
        JPA에서는 update 메서드를 따로 제공하지 않는다.
        조회한 후에 setter로 변경하면 자동으로 update문이 나간다.
        변경 후 save를 호출하면 update가 된다.
         */
        Optional<Product> product = productRepository.findById(id);
        product.ifPresent(p -> {
            p.setName(newName);
            p.setPrice(newPrice);
            p.setCategory(newCategory);
            productRepository.save(p);
        });

        // then
    }
}