package com.study.jpa.chap03_pagination.Repository;

import com.study.jpa.chap02_querymethod.entity.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
// JPA는 INSERT, UPDATE, DELETE 시에 트랜잭션을 기준으로 동작하는 경우가 많다.
// 이 기능을 보장받기 위해서는 트랜잭션 기능을 함께 사용해야 한다.
// 나중에 MVC 구조에서 Service 클래스에 아노테이션을 첨부하면 된다.
@Transactional
@Rollback(false) // 원래는 보통 Test에서 진행한 변동사항은 저장하지 않게 하기 위해 true로 설정한다.
class StudentPageRepositoryTest {
    @Autowired
    StudentPageRepository studentPageRepository;


    void bulkInsert() {
        // 학생 더미 데이터 저장
        for (int i = 0; i <= 147 ; i++) {
            Student s = Student.builder()
                    .name("김테스트" + i)
                    .city("아무시" + i)
                    .major("아무 전공" + i)
                    .build();
            studentPageRepository.save(s);
        }
    }

    @Test
    @DisplayName("기본 페이지 테스트")
    void testBasicPagination() {
        // given
        int pageNo = 15;
        int amount = 10;

        // 페이지 정보 생성(Pageable 타입 객체 생성)
        // 페이지 번호가 zero-based(0이 1 페이지)이므로 꼭 1을 뺀 값을 매개값으로 줘야 한다.
        Pageable pageable = PageRequest.of(pageNo-1, amount,
                // 정렬 기준값은 필드명이다! 컬럼명(X) JPA는 엔터티 기준이다! DB table 생각하지 말 것
                // 정렬 기준이 한 개인 경우
//                Sort.by("name").descending()
                // 정렬 기준이 여러 개인 경우
                Sort.by(
                    Sort.Order.desc("name"),
                    Sort.Order.asc("city")
                )
        );

        // when
        Page<Student> students = studentPageRepository.findAll(pageable);
        
        // 페이징이 적용된 총 학생 데이터 묶음
        List<Student> studentList = students.getContent();

        int totalPages = students.getTotalPages(); // 총 페이지 수
        long totalElements = students.getTotalElements(); // 총 항목 수
        boolean next = students.hasNext(); // 다음 페이지가 있는지?
        boolean prev = students.hasPrevious(); // 이전 페이지가 있는지?

        // then
        System.out.println("\n\n\n");
        System.out.println("totalPages = " + totalPages);
        System.out.println("totalElements = " + totalElements);
        System.out.println("next = " + next);
        System.out.println("prev = " + prev);
        studentList.forEach(System.out::println);
        System.out.println("\n\n\n");
    }

    @Test
    @DisplayName("이름 검색 + 페이징")
    void testSearchAndPaging() {
        // given
        int pageNo = 4;
        int size = 9;
        Pageable pageInfo = PageRequest.of(pageNo - 1, size);

        // when
        Page<Student> students = studentPageRepository.findByNameContaining("3", pageInfo);

        // then
        // 페이징이 적용된 총 학생 데이터 묶음
        List<Student> studentList = students.getContent();

        int totalPages = students.getTotalPages(); // 총 페이지 수
        long totalElements = students.getTotalElements(); // 총 항목 수
        boolean next = students.hasNext(); // 다음 페이지가 있는지?
        boolean prev = students.hasPrevious(); // 이전 페이지가 있는지?

        /*
         페이징 처리 시에 버튼 알고리즘은 jpa에서 따로 제공하지 않기 때문에
         버튼 배치 알고리즘을 수행할 클래스는 여전히 필요합니다.
         제공되는 정보는 이전보다 많기 때문에, 좀 더 수월하게 처리가 가능합니다.
         */

        System.out.println("\n\n\n");
        System.out.println("totalPages = " + totalPages);
        System.out.println("totalElements = " + totalElements);
        System.out.println("next = " + next);
        System.out.println("prev = " + prev);
        studentList.forEach(System.out::println);
        System.out.println("\n\n\n");

    }
}