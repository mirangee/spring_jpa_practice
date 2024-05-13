package com.study.jpa.chap01_basic.Repository;

import com.study.jpa.chap01_basic.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
// JpaRepository를 꼭 상속 받아야 하며, 제네릭 두 개를 설정해 준다.
// 첫번째는 엔터티 타입, 두번째는 프라이머리 키 타입 명시
// 이렇게 설정해야 JpaRepository 메서드 사용 시 알아서 이 타입으로 반환 값 돌려준다.

}
