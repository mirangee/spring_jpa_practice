package com.study.jpa.chap05_practice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Setter @Getter
@ToString(exclude = {"hashTags"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_no")
    private Long id; // 글 번호

    @Column(nullable = false)
    private String writer; // 작성자

    @Column(nullable = false)
    private String title;

    private String content; // 글 내용

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createDate; // 작성 시간

    @UpdateTimestamp
    private LocalDateTime updatedDate; // 수정 시간

    @OneToMany(mappedBy = "post")
    @Builder.Default // Builder 패턴으로 객체를 생성할 때 특정 필드를 직접 지정한 값으로 초기화 강제 명령.
    private List<HashTag> hashTags = new ArrayList<>(); // 해시태그
    
    // 양방향 관계에서 HashTag 리스트에 데이터를 추가하는 편의 메서드 생성
    // entityManager를 이용해 Insert 후에 Select를 진행하는 것도 가능하지만
    // Insert와 동시에 실시간으로 HashTag 리스트도 동기화
    public void addHashTag(HashTag hashTag) {
        this.hashTags.add(hashTag); // 매개값으로 전달받은 HashTag 객체를 리스트에 추가
    
        // 매개값으로 전달된 HashTag 객체가 가지고 있는 Post가
        // 이 메서드를 부르는 Post 객체와 주소값이 서로 다르다면 데이터 불일치가 발생하기 때문에
        // HashTag의 Post도 이 객체로 변경
        if(this != hashTag.getPost()) {
            hashTag.setPost(this);
        }
    }
}
