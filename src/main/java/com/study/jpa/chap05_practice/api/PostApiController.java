package com.study.jpa.chap05_practice.api;

import com.study.jpa.chap05_practice.dto.*;
import com.study.jpa.chap05_practice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "post API", description = "게시물 조회, 등록 및 수정, 삭제 api입니다.")
// swagger 라이브러리 설치 후 쓸 수 있는 기능(API 명세를 쉽게 관리 가능)
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostApiController {
    private final PostService postService;

    // 리소스: 게시물 (Post)
    /*
        게시물 목록 조회: /posts            - GET, param: (page, size)
        게시물 개별 조회: /posts/{id}       - GET
        게시물 등록:     /posts            - POST, payload: (writer, title, content, hashTags)
        게시물 수정:     /posts            - PATCH
        게시물 삭제:     /posts/{id}       - DELETE
     */

    @GetMapping
    public ResponseEntity<?> list(PageDTO pageDTO) {
        log.info("/api/v1/posts?page={}&size={}", pageDTO.getPage(), pageDTO.getSize());
        PostListResponseDTO posts = postService.getPosts(pageDTO);

        return ResponseEntity.ok().body(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        log.info("/api/v1/posts/{}: GET!!!", id);
        try {
            PostDetailResponseDTO dto = postService.getDetail(id);
            return ResponseEntity.ok().body(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "게시물 등록", description = "게시물 작성 및 등록을 담당하는 메서드입니다.")
    @Parameters({
            @Parameter(name = "writer", description = "게시물의 작성자 이름을 쓰세요!", example = "김뽀삐", required = true),
            @Parameter(name = "title", description = "게시물의 제목을 쓰세요!", example = "제목제목", required = true),
            @Parameter(name = "content", description = "게시물의 내용을 쓰세요!", example = "내용내용"),
            @Parameter(name = "hashTags", description = "게시물의 해시태그를 작성하세요!", example = "['하하', '호호']")
    })
    @PostMapping
    public ResponseEntity<?> create(
            @Validated @RequestBody PostCreateDTO dto,
            BindingResult result // 검증 에러 정보를 가진 객체
    ) {
        log.info("/api/v1/posts  POST!!! - payload: {}", dto);
        if (dto == null) return ResponseEntity.badRequest().body("등록 게시물 정보를 전달해 주세요.");

        ResponseEntity<List<FieldError>> fieldErrors = getValidatedResult(result);
        if (fieldErrors != null) return fieldErrors;

        // 위에 존재하는 If 문을 모두 건너뜀 -> DTO가 null도 아니고 입력값 검증도 모두 통과함 -> service에게 명령
        PostDetailResponseDTO responseDTO = null;
        try {
            responseDTO = postService.insert(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("서버 에러 원인: " + e.getMessage());
        }
        return ResponseEntity.ok().body(responseDTO);
    }

    // 입력값 검증(Validation)의 결과를 처리해 주는 전역 메서드
    private static ResponseEntity<List<FieldError>> getValidatedResult(BindingResult result) {
        if (result.hasErrors()) { // 입력값 검증 단계에서 문제가 있었다면 true
            List<FieldError> fieldErrors = result.getFieldErrors();
            fieldErrors.forEach(error -> {
                log.warn("invalid client data - {}", error.toString());
            });
            return ResponseEntity.badRequest().body(fieldErrors);
        }
        return null;
    }

    @Operation(summary = "게시글 수정", description = "게시물 수정을 담당하는 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 완료!",
                    content = @Content(schema = @Schema(implementation = PostDetailResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패."),
            @ApiResponse(responseCode = "404", description = "NOT FOUND")
    })
    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.PATCH}) // 요청 방식 두 가지를 한번에 처리하려면 이렇게 작성
    public ResponseEntity<?> update(
            @Validated @RequestBody PostModifyDTO dto,
            BindingResult result,
            HttpServletRequest request
    ) {
        log.info("/api/v1/posts {} - payload: {}", request.getMethod(), dto);
        ResponseEntity<List<FieldError>> fieldErrors = getValidatedResult(result);
        if (fieldErrors != null) return fieldErrors;
        PostDetailResponseDTO responseDTO = postService.modify(dto);

        return ResponseEntity.ok().body(responseDTO);
    }

    // 게시물 삭제
    // 테스트 진행 시 해시태그가 없는 글을 삭제해 보세요.
    // 해시태그가 있는 글을 삭제하면 에러가 난다(HashTag 테이블이 해당 Post를 Foreign key로 가지고 있기 때문에)
    // Entity를 설계할 때 참조 대상(부모)이 삭제되면 자식도 같이 삭제되도록 설정할 것
    // HashTag Entity -> ("cascade=CascadeType.Remove") - ON DELETE CASCADE 설정
    // Post Entity -> orphanRemoval = true (고아 객체가 되면 삭제하겠다는 의미)
    // 고아 객체 삭제와 cascadeType.Remove의 차이
    // 고아 객체 삭제: 부모 엔터티와 연결이 끊어지는 경우(부모 삭제, 부모 pk 변경되는 경우)에 삭제
    // cascadeType.Remove(연결이 끊어지는 경우에는 남고, 부모 엔터티가 삭제되는 경우에만 삭제)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.info("/api/v1/posts/{} :DELETE!!!", id);
        postService.deletePost(id);
        return ResponseEntity.ok().body("SUCCESS");
    }
}
