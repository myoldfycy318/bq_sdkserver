package com.qbao.sdk.server.bo.pay;

import java.io.Serializable;
import java.util.Date;

public class UserInfo implements Serializable {

	private static final long serialVersionUID = -8065665447875404466L;
	
	private long id;
	private String username;
	private long hyipUserId;
	private String nickName;
	private Integer enabled;//0冻结用户，1正常用户
	private String email;
	private String md5PresenterCode;
	private String mobile;
    private String userId;
    private Date createTime;
    private String loginName;
    private String password;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getHyipUserId() {
		return hyipUserId;
	}

	public void setHyipUserId(long hyipUserId) {
		this.hyipUserId = hyipUserId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMd5PresenterCode() {
		return md5PresenterCode;
	}

	public void setMd5PresenterCode(String md5PresenterCode) {
		this.md5PresenterCode = md5PresenterCode;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
