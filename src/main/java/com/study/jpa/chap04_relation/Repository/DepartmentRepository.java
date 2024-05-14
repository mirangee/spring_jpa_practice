package com.study.jpa.chap04_relation.Repository;

import com.study.jpa.chap04_relation.entity.Department;
import com.study.jpa.chap04_relation.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
