package com.study.jpa.chap05_practice.dto;

import com.study.jpa.chap05_practice.entity.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Setter @Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PostCreateDTO {

    // @NotNull -> null을 허용하지 않음. "", " "은 허용
    // @NotEmpty -> null, ""은 허용하지 않음, " "은 허용
    @NotBlank // -> null, "", " " 모두를 허용하지 않음(가장 엄격)
    @Size(min = 2, max = 5)
    private String writer;

    @NotBlank
    @Size(min = 1, max = 20)
    private String title;

    private String content;
    private List<String> hashTags;

    //dto를 entity로 변환하는 메서드
    public Post toEntity() {
        return Post.builder()
                .writer(this.writer)
                .title(this.title)
                .content(this.content)
//                .hashTags(hashTags) 해쉬태그는 여기서 넣는 것이 아니다!
//                Post 테이블에 hashTag 컬럼 없음. Post 객체 보면 mappedBy 되어 있으므로 넣는다 해도 테이블에 반영X
                .build();
    }
}
