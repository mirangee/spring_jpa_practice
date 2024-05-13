package com.study.jpa.chap02_querymethod.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Setter // 실무적 측면에서 setter는 신중하게 선택할 것(직접 변경하고자 하는 필드만 따로 설정하는 경우가 많음)
// jpa에서는 setter를 사용하면 update가 진행되기 때문에 setter 사용은 지양한다(특히 id, name)
@Getter @ToString
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id") // 모든 필드값을 다 비교하지 않고 지정 필드("id")가 같으면 같은 객체로 인식
@Builder
@Entity
@Table(name = "tbl_student")
public class Student {
    @Id
    @Column(name = "stu_id")
    @GeneratedValue(generator = "uid") // id가 String일 때 이 방식으로 자동 부여 strategy 지정 가능
    @GenericGenerator(name="uid", strategy = "uuid")
    private String id;

    @Column(name = "stu_name", nullable = false)
    private String name;
    private String city;
    private String major;
}
