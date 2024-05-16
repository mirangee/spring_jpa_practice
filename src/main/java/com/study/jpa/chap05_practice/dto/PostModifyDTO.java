package com.study.jpa.chap05_practice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PostModifyDTO {
    @NotBlank
    @Size(min = 1, max = 20)
    private String title;

    private String content;
    
    // Long 타입은 정수 타입이므로 애초에 공백이나 빈 문자열이 들어올 수 없는 타입이므로 NotNull로 선언

    private Long postNo;
}
