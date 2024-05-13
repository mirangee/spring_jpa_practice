package com.study.jpa.chap02_querymethod.Repository;

import com.study.jpa.chap02_querymethod.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    // native-sql
    // SELECT 컬럼명 FROM 테이블명
    // WHERE 컬럼 = ?

    // JPQL
    // SELECT 별칭 FROM 엔터티클래스명 AS 별칭
    // WHERE 별칭.필드명 = ?
    // SELECT FROM Student AS st
    // WHERE st.name = ?

    // 도시 이름으로 학생 조회
    // ?1 -> 첫번째 파라미터 값이 여기로 들어온다는 뜻. 이렇게도 쓸 수 있음
    // @Query(value = "SELECT * FROM tbl_student WHERE name ?1", nativeQuery = true)
    // AS 생략 가능
     @Query("SELECT s FROM Student s WHERE s.city = ?1")
    List<Student> getByCityWithJPQL(@Param("nm") String city);

     // JPQL에서는 DB 테이블 컬럼명을 쓰는 게 아니라 Entity에 맞춰서 쓰면 된다!
    // DB 신경쓰지 말고 Entity에만 집중해서 쓸 것!
    // 무엇을 SELECT 해서 가지고 오느냐에 따라 반환 타입이 달라진다 (예: s -> List<Student> / s.city -> List<String>)
     @Query("SELECT s FROM Student s WHERE s.name LIKE %:nm%")
     List<Student> searchByNameWithJPQL(@Param("nm") String name);
     
    // JPQL로 수정 삭제 쿼리 쓰기
    @Modifying // 조회가 아닐 경우 무조건 붙여야 된다. SELECT 외에는 모두 변경이 일어나기 때문
    @Query("DELETE FROM Student s WHERE s.name = ?1")
    void deleteByNameWithJPQL(String name);
}
