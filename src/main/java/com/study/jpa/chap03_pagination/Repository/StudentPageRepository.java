package com.study.jpa.chap03_pagination.Repository;

import com.study.jpa.chap02_querymethod.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentPageRepository extends JpaRepository<Student, String> {

    // 학생 조건 없이 전체 조회 페이징(기본 기능 -> 기본 제공되므로 선언하지 않아도 되는 메서드임)
//    Page<Student> findAll(Pageable pageable);

    // 학생의 이름에 특정 단어가 포함된 경우 조회 + 페이징
    Page<Student> findByNameContaining(String name, Pageable pageable);
}
