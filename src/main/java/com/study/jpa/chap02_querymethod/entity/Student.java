package com.study.jpa.chap02_querymethod.entity;

import lombok.*;

@Setter // 실무적 측면에서 setter는 신중하게 선택할 것(직접 변경하고자 하는 필드만 따로 설정하는 경우가 많음)
@Getter @ToString
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode @Builder
public class Student {

}
