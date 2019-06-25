package org.seally.base.dto;

import java.util.List;

import org.seally.base.model.SystemMenu;
import org.seally.base.model.SystemRealm;
import org.seally.base.model.SystemRole;
import org.seally.base.model.SystemUser;
import org.springframework.beans.BeanUtils;

/**
 * @Description 系统用户包装类，除了用户基本数据之外还包含其它扩展属性
 * @Date 2019年5月28日
 * @author 邓宁城
 */
public class SystemUserDto extends SystemUser {
	
	private SystemRole role;//当前账号的角色信息
	private SystemRealm loginRealm;//当前账号登录realm
	//private List<SystemMenu> menus;//当前账号的菜单权限信息
	public SystemRole getRole() {
		return role;
	}
	public void setRole(SystemRole role) {
		this.role = role;
	}
	public SystemRealm getLoginRealm() {
		return loginRealm;
	}
	public void setLoginRealm(SystemRealm loginRealm) {
		this.loginRealm = loginRealm;
	}
	
	//使用账号信息构建账号包装类
	public SystemUserDto(SystemUser user) {
		BeanUtils.copyProperties(user, this);
	}
	
}
