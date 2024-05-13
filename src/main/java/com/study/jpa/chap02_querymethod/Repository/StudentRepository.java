package com.study.jpa.chap02_querymethod.Repository;

import com.study.jpa.chap02_querymethod.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {

    List<Student> findByName(String name);

    List<Student> findByCityAndMajor(String city, String major);

    List<Student> findByMajorContaining(String major); //Containing은 Like 절로 처리됨(포함되기만 하면 SELECT)


    // 네이티브 쿼리 사용(쿼리 직접 사용)
    @Query(value = "SELECT*FROM tbl_student WHERE stu_name = :nm", nativeQuery = true)
    List<Student> findNameWithSQL(@Param("nm") String name);
}
