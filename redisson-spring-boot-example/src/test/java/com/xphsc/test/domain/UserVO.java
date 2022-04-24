package com.xphsc.test.domain;




public class UserVO {

    private Long id;

    private String name;

    private Integer age;

    private DeptVO deptVO;

    public UserVO(Long id, String name, Integer age, DeptVO deptVO) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.deptVO = deptVO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public DeptVO getDeptVO() {
        return deptVO;
    }

    public void setDeptVO(DeptVO deptVO) {
        this.deptVO = deptVO;
    }
}
