package com.study.jpa.chap04_relation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"employees"})
@Builder
@Entity
@Table(name = "tbl_dept")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_id")
    private Long id;

    @Column(name = "dept_name", nullable = false)
    private String name;

    // 양방향 맵핑에서는 상대방 엔터티의 갱신에 관여할 수 없다. (실제 테이블에는 존재하지 않는 가상의 컬럼이다)
    // 단순히 읽기 전용(조회)으로만 사용해야 한다.
    // mappedBy에는 상대방 엔터티의 조인되는 필드명을 작성한다.
    // 관계의 주인은 employee(foreignkey를 가짐, 양 엔터티 갱신에 관여 가능)이며,
    // mappedBy는 employee의 정보에 따라 리스트를 보여주는 것 뿐이다.
    @OneToMany(mappedBy = "department") // 관계를 맺고 있는 상대 엔터티의 필드명 명시
    private List<Employee> employees = new ArrayList<>(); // 초기화 필요(NPE(Null Pointer Exception) 방지)
}
