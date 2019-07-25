package org.yyzh.hos.sys;

import lombok.*;
import java.io.Serializable;
import java.util.Date;

/**
 *  
 * @ClassName SUser
 * @Description TODO 系统用户
 * @Author baiHoo.chen
 * @Date 2019-06-02 20:38:25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SUser  implements Serializable {

	private Long id;
	private String loginName;
	private String password;
	private Date createDate;
	private Date updateDate;
	private Integer isDelete;	//0： 删除，1：启用

}

