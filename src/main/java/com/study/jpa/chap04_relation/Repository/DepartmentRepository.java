package com.study.jpa.chap04_relation.Repository;

import com.study.jpa.chap04_relation.entity.Department;
import com.study.jpa.chap04_relation.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    //FETCH JOIN 문법 -> JPQL을 직접 작성해야 함
    @Query("SELECT d FROM Department d JOIN FETCH d.employees")
    List<Department> findAllIncludesEmployees();


}
