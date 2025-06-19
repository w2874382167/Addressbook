package com.jiong.addressbook.bean;

public class PeoBean {
    /**
     * id id
     * name 姓名
     * sex 性别
     * num 号码
     * remark 备注
     * beginZ 字母
     * 自定义数据类
     */
    private String id;
    private String name;
    private String num;
    private String sex;
    private String remark;
    private String beginZ;

    private String numberLocated;

    public String getnumberLocated() {
        return numberLocated;
    }

    public void setnumberLocated(String numberLocated) {
        this.numberLocated = numberLocated;
    }

    @Override
    public String toString() {
        return "PeoBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", num='" + num + '\'' +
                ", sex='" + sex + '\'' +
                ", remark='" + remark + '\'' +
                ", beginZ='" + beginZ + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBeginZ() {
        return beginZ;
    }

    public void setBeginZ(String beginZ) {
        this.beginZ = beginZ;
    }


    public PeoBean(String id, String name, String num, String sex, String remark, String beginZ) {
        this.id = id;
        this.name = name;
        this.num = num;
        this.sex = sex;
        this.remark = remark;
        this.beginZ = beginZ;
    }

    public PeoBean(String id, String name, String num, String sex, String remark) {
        this.id = id;
        this.name = name;
        this.num = num;
        this.sex = sex;
        this.remark = remark;
    }
}
