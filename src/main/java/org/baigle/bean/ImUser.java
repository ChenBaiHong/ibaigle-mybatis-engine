package org.baigle.bean;

import lombok.*;
import java.io.Serializable;
import java.util.Date;

/**
 *  
 * @ClassName ImUser
 * @Description TODO 聊天通信的用户
 * @Author baiHoo.chen
 * @Date 2019-07-11 19:10:49
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImUser  implements Serializable {

	private String id;
	private String wxIdentId;	//由微信提供的用户身份ID
	private String name;	//用户名称
	private String avatar;	//用户头像
	private String chatIdent;	//聊天身份: {0:患者，1:医生}
	private String chatIdentDict;	//聊天身份: {0:患者，1:医生}
	private String doctorLevel;	//医生级别: {1: 医师，2: 主治医师，3: 副主任医师}
	private String doctorLevelDict;	//医生级别: {1: 医师，2: 主治医师，3: 副主任医师}
	private String deptId;	//医生所在部门ID
	private String deptName;	//医生所在部门名称
	private String tokenPassword;	//身份令牌识别密码
	private String sessionKey;	//会话失效秘匙
	private String defaultGroupId;	//聊天组ID
	private String createBy;	//创建人
	private Date createTime;	//创建时间
	private String updateBy;	//更新人
	private Date updateTime;	//更新时间
	private String remarks;	//说明
	private String delFlag;	//删除标记: {1: 存在 , 0: 删除}
	private String lgIdentId;	//由系统提供的用户ID，分别别是 Lg_doctor 的主键ID；或Lg_wx_user 的主键ID
	private String accountNonExpired;
	private String accountNonLocked;
	private String credentialsNonExpired;
	private String enabled;

}

