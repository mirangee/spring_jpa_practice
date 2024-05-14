package com.study.jpa.chap04_relation.entity;

import jakarta.persistence.*;
import lombok.*;


@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(of = "id")
// JPA 연관관계 맵핑에서 연관관계 데이터는 toString에서 제외해야 한다.
// 안 그러면 순환 참조 발생
@ToString(exclude = {"department"})
@Builder
@Entity
@Table(name = "tbl_emp")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id")
    private Long id;

    @Column(name = "emp_name", nullable = false)
    private String name;
    
    // DB에서는 ForeignKey 설정을 위해 dept_id 컬럼을 만든다(원자성).
    // 하지만 JPA는 객체가 객체를 담을 수 있기 때문에 꼭 dept_id라는 단일 정보만 담을 필요가 없다.
    // 아예 객체를 담고 어떤 관계(M:1, M:M, 1:M 등)인지, 어떤 컬럼을 기준으로 Join할지 명시해주면 된다.
    // DB 테이블 생성 시 dept_id 컬럼이 생성된 것을 확인할 수 있다.

    // 단방향 연관 맵핑(Employee만 Department 정보를 가지고 있다)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id") // 테이블 컬럼 명
    private Department department;
}
