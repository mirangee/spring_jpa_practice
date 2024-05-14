package com.study.jpa.chap05_practice.api;

import com.study.jpa.chap05_practice.entity.Post;
import com.study.jpa.chap05_practice.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@Rollback(value = false)
class PostApiControllerTest {
    @Autowired
    PostRepository postRepository;

    @Test
    @DisplayName("bulk insert")
    void bulkInsert() {
        for(int i=1; i<=378; i++) {
            postRepository.save(
                    Post.builder()
                            .title("하하호호제목" + i)
                            .content("낄낄깔깔내용" + i)
                            .writer("꾸까꾸까" + i)
                            .build()
            );
        }
    }

}