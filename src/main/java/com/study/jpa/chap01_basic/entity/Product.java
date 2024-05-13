package com.study.jpa.chap01_basic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "tbl_product")
public class Product {

    // 대부분의 jpa는 jakarta.persistence import하면 된다.
    @Id // 프라이머리 키로 사용하겠다는 아노테이션
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB가 무엇이냐에 따라 전략을 다르게 선택해야 함(교안 확인할 것)
    @Column (name = "prod_id") // 변수명과 다르게 column명 정의
    private Long id; // 기본 타입은 null을 못 받지만 객체 타입은 null을 받을 수 있으므로 객체 타입 Long으로 선언

    @Column (name = "prod_name", nullable = false, length = 30)
    private String name;

    private int price;

    @Enumerated(EnumType.STRING) // 이걸 세팅하지 않으면 ORDINAL이 기본 세팅이어서 인덱스 번호로 테이블에 들어간다.
    private Category category;

    @CreationTimestamp // MySQL에서 default current timestamp와 같은 기능
    @Column(updatable = false) // 변경 불가
    private LocalDateTime createDate;

    @UpdateTimestamp // 업데이트 될 때마다 자동으로 Timestamp 들어감
    private LocalDateTime updateDate;

    public enum Category {
        FOOD, FASHION, ELECTRONIC
    }
}
