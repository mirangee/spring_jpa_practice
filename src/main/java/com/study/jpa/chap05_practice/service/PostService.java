package com.study.jpa.chap05_practice.service;

import com.study.jpa.chap05_practice.dto.*;
import com.study.jpa.chap05_practice.entity.HashTag;
import com.study.jpa.chap05_practice.entity.Post;
import com.study.jpa.chap05_practice.repository.HashTagRepository;
import com.study.jpa.chap05_practice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional // JPA 레파지토리는 트랜잭션 단위로 동작하기 때문에 작성해 주세요.
public class PostService {
    private final PostRepository postRepository;
    private final HashTagRepository hashTagRepository;

    public PostListResponseDTO getPosts(PageDTO pageDTO) {
        //Pageable 객체 생성
        Pageable pageable = PageRequest.of(
                pageDTO.getPage() - 1, pageDTO.getSize(),
                Sort.by("createDate").descending()
        );
        
        // 데이터베이스에서 게시물 목록 조회
        Page<Post> posts = postRepository.findAll(pageable);

        // 게시물 정보만 꺼내기
        List<Post> postList = posts.getContent();

        // 게시물 정보를 응답용 DTO의 형태에 맞게 변환
        List<PostDetailResponseDTO> detailList = postList.stream()
                .map(PostDetailResponseDTO::new)
                .collect(Collectors.toList());

        // DB에서 조회한 정보를 JSON 형태에 맞는 DTO로 전환
        // Page 구성 정보와 위에 있는 게시물 정보를 또 다른 DTO로 한번에 포장해서 리턴할 예정
        // -> PostListResponseDTO

        return PostListResponseDTO.builder()
                .count(detailList.size()) // 총 게시물 수가 아니라 페이징에 의해 조회된 게시물 수
                .pageInfo(new PageResponseDTO(posts))
                // JPA가 준 페이지 정보가 담긴 객체를 DTO에게 전달해서 그쪽에서 알고리즘 돌리게 시킴
                .posts(detailList)
                .build();
    }

    public PostDetailResponseDTO getDetail(Long id) throws Exception {
        Post post = getPost(id);
        return new PostDetailResponseDTO(post);
    }

    private Post getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(()-> new RuntimeException(id +"번 게시물이 존재하지 않습니다."));
        return post;
    }

    public PostDetailResponseDTO insert(PostCreateDTO dto) throws Exception {
        // 게시물 저장(해시태그는 아직 저장되지 않음)
        Post saved = postRepository.save(dto.toEntity());

        // 해시태그 저장
        List<String> hashTags = dto.getHashTags();
        if (hashTags != null && !hashTags.isEmpty()) {
            hashTags.forEach(ht -> {
                HashTag hashTag = HashTag.builder()
                        .tagName(ht)
                        .post(saved) // 게시물 저장된 post 객체를 전달
                        .build();
                HashTag savedTag = hashTagRepository.save(hashTag);

                /*
                    Post Entity는 데이터 베이스에 save할 때 hashTags를 insert하지 않고
                    HashTag Entity는 따로 save를 진행한다.(테이블이 Post, HashTag 각각 나뉘어 있음)

                    HashTag는 양방향 맵핑이 되어 있는 연관관계의 주인이기 때문에
                    save를 진행할 때 Post를 전달하므로 DB와 Entity의 상태가 동일하다.
                    하지만 Post는 연관관계 주인이 아니므로 HashTag의 정보가 비어있으며 조회용(mappedBy)으로만 설정된 상태이다.
                    그러므로 위에 작성된 'saved' 객체의 hashTagList는 null이기 때문에 에러가 난다.
                    
                    이 문제를 해결하기 위해 Post Entity에 연관관계 편의 메서드를 작성해
                    save된 HashTag의 내용을 바로 hashTag List에 동기화해야 한다.
                    Post를 화면단으로 return할 때 HashTag들도 같이 전달되어야 하므로.
                    (Post 객체에서 hashTagList에 @Builder.Defualt 설정 필수!)
                    
                    (다른 해결 방법으로는 Entity Manager를 주입받아 Insert 완료 후 Select를 하게 하는 방법도 있다.
                    Entity Manager를 주입 받아 강제 Flush()하면 Insert를 트랜잭션 종료 후 실행할 수 있다)
                 */

                saved.addHashTag(savedTag);
            });
        }


        // 방금 insert 요청한 게시물 정보를 DTO로 전달
        return new PostDetailResponseDTO(saved);
    }

    public PostDetailResponseDTO modify(PostModifyDTO dto) {
        
        // 수정 전 데이터를 조회
        Post postEntity = getPost(dto.getPostNo());

        // 수정 시작
        postEntity.setTitle(dto.getTitle());
        postEntity.setContent(dto.getContent());

        // 수정 완료
        Post modifiedPost = postRepository.save(postEntity);

        return new PostDetailResponseDTO(modifiedPost);
    }

    public void deletePost(Long id) {
        // 삭제 하기
        postRepository.deleteById(id);
    }
}
