package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper{

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);


    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);
    /*
    * 根据主键动态修改属性
    * */
    @AutoFill(OperationType.UPDATE)
    void update(Employee employee);
    @Select("select * from employee where id=#{id}")
    Employee getById(Long id);

    // 新增：手写 insert 方法，给 AOP 用
    @AutoFill(OperationType.INSERT)
    void insert(Employee employee);
}
