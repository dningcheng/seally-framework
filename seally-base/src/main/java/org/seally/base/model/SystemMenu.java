package org.seally.base.model;

import java.util.Date;

public class SystemMenu {
    private String id;

    private String pid;

    private String name;

    private String url;

    private Integer type;

    private Integer enable;

    private Integer mindex;//序号
    
    private Integer mlevel;//层级

    private String detail;

    private Date createTime;

    private Date updateTime;

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

	public Integer getMindex() {
		return mindex;
	}

	public void setMindex(Integer mindex) {
		this.mindex = mindex;
	}

	public Integer getMlevel() {
		return mlevel;
	}

	public void setMlevel(Integer mlevel) {
		this.mlevel = mlevel;
	}

	public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail == null ? null : detail.trim();
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