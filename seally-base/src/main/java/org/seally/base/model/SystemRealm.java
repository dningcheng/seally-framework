package org.seally.base.model;

import java.util.Date;

/**
 * @Description 系统域，泛指概念，代表系统中管理数据的边界，不同域中的数据严格互相隔离，系统账号可管理多个域，但是同一个账号登录只能操作当前登录域中的数据，主控台会展示账号管理域中的综合数据
 * @Date 2019年5月28日
 * @author 邓宁城
 */
public class SystemRealm {
    private Integer id;

    private Integer pid;

    private String name;

    private String provincessCode;

    private String provincessName;

    private String cityCode;

    private String cityName;

    private String lat;

    private String lng;

    private Date createTime;

    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getProvincessCode() {
        return provincessCode;
    }

    public void setProvincessCode(String provincessCode) {
        this.provincessCode = provincessCode == null ? null : provincessCode.trim();
    }

    public String getProvincessName() {
        return provincessName;
    }

    public void setProvincessName(String provincessName) {
        this.provincessName = provincessName == null ? null : provincessName.trim();
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode == null ? null : cityCode.trim();
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName == null ? null : cityName.trim();
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat == null ? null : lat.trim();
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng == null ? null : lng.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}