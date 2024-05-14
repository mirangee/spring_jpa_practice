package com.study.jpa.chap04_relation.Repository;

import com.study.jpa.chap04_relation.entity.Department;
import com.study.jpa.chap04_relation.entity.Employee;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class DepartmentRepositoryTest {
    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    /*
        JPA에서 사용하는 엔터티들을 관리하는 역할을 수행하는 클래스(영속성 컨텍스트를 관리함)
        영속성 컨텍스트: 엔터티를 저장하는 환경 -> Spring data JPA에서만 사용하는 것이 아니라 JPA 사용하는 곳에서는 모두 사용.
        영속성 컨텍스트 내의 내용들을 DB에 반영하거나 비워내거나 수명을 관리할 수 있는 객체(Commit, Rollback, Flush).
        Spring Data JPA에서는 자동으로 해주지만 업데이트 시점을 직접 관리하기 위해 entity Manager 가져옴.
     */
    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("특정 부서를 조회하면 해당 부서원들도 함께 조회되어야 한다")
    void testFindDept() {
        // given
        Long id = 2L;
        // when
        Department department = departmentRepository.findById(id).orElseThrow();
        // then
        System.out.println("\n\n\n");
        System.out.println("department = " + department);
        // department 출력하면서 StackOverflowError 발생 : 순환해서 계속 ToString을 호출하기 때문에 무한 루프에 빠짐
        // JPA 연관관계 맵핑에서 연관관계 데이터는 toString에서 제외해야 한다. 안 그러면 순환 참조 발생
        System.out.println("department.getEmployees() = " + department.getEmployees());
        System.out.println("\n\n\n");
    }

    @Test
    @DisplayName("Lazy 로딩과 Eager 로딩의 차이")
    void testLazyAndEager() {
        // 3번 사원을 조회하고 싶은데 굳이 부서 정보는 필요 없긴 함
        // given
        Long id = 3L;
        // when
        Employee employee = employeeRepository.findById(id).orElseThrow();
        // then
        System.out.println("\n\n\n");
        System.out.println("employee = " + employee);
        System.out.println("dept_name = " + employee.getDepartment().getName());
        System.out.println("\n\n\n");
    }
    
    @Test
    @DisplayName("양방향 연관관계에서 연관 데이터의 수정")
    void testChangeDept() {

        Employee foundEmp = employeeRepository.findById(3L).orElseThrow();
//        foundEmp.getDepartment().setId(2L);
//        department 객체에는 id, name이 있기 때문에 이렇게 하면 Department 정보 자체가 바뀌지 않는다.

        Department newDept = departmentRepository.findById(1L).orElseThrow();
        foundEmp.setDepartment(newDept);
        employeeRepository.save(foundEmp);
        // 문제 발생!
        // 영속성 컨텍스트 내의 정보에 이미 진행된 SELECT가 있으면 그 작업을 먼저 처리함(?정확한 내용 확인 필요).
        // 그렇기 때문에 entityManager를 사용하지 않으면 save를 해도 아래 출력문에서 결과가 반영되지 않고
        // update가 나중에 진행 됨.
        // entityManager로 바로 쿼리를 진행시키도록 해야 아래 출력문에서 save가 반영된 결과를 출력함

        // 변경감지 (더티 체크) 후 변경된 내용을 DB에 즉시 반영하는 메서드 사용
        entityManager.flush(); // DB로 밀어내기
        entityManager.clear(); // 영속성 컨텍스트 비우기(비우지 않으면 컨텍스트 내의 정보를 참조하려 하기 때문)

        // when
        // 2번 부서 정보를 조회해서 모든 사원 리스트를 보자.
        Department foundDept = departmentRepository.findById(2L).orElseThrow();

        // then
        System.out.println("\n\n\n");
        foundDept.getEmployees().forEach(System.out::println);
        System.out.println("\n\n\n");
    }

    @Test
    @DisplayName("N+1 문제 발생 예시")
    void testNPlusOneEx() {
        // given
        List<Department> departments = departmentRepository.findAll();

        // when
        departments.forEach(dept -> {

            System.out.println("\n\n============ 사원 리스트 =========");
            List<Employee> employees = dept.getEmployees();
            employees.forEach(System.out::println);
        });

        // 문제 발생!
        // dept.getEmployees()가 호출 될 때 행의 갯수만큼 계속 쿼리문이 호출된다.
        // 데이터가 많아질 수록 성능저하 문제 초래
        // JPQL을 작성해서 직접 쿼리를 짜보자! DepartmentRepository로 이동

        // then
    }

    @Test
    @DisplayName("N+1 문제 해결 예시")
    void testNPlusOneSolution() {
        // given
        List<Department> departments = departmentRepository.findAllIncludesEmployees();

        // when
        departments.forEach(dept -> {

            System.out.println("\n\n============ 사원 리스트 =========");
            List<Employee> employees = dept.getEmployees();
            employees.forEach(System.out::println);
        });
    }
}