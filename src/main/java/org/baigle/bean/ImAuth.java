package org.baigle.bean;

import lombok.*;
import java.io.Serializable;
import java.util.Date;

/**
 *  
 * @ClassName ImAuth
 * @Description TODO 授权限角色，访问 Access_token 处理
 * @Author baiHoo.chen
 * @Date 2019-07-11 18:09:03
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImAuth  implements Serializable {

	private String id;
	private String enUsName;	//英文名称
	private String zhChName;	//中文名称
	private String createBy;	//创建人
	private Date createTime;	//创建时间
	private String updateBy;	//更新人
	private Date updateTime;	//更新时间
	private String remarks;	//说明
	private String delFlag;	//删除标记: {1: 存在 , 0: 删除}

}

