package org.yyzh.hos.sys;

import lombok.*;
import java.io.Serializable;
import java.util.Date;

/**
 *  
 * @ClassName SUserAppMenu
 * @Description TODO 用户菜单中间表
 * @Author baiHoo.chen
 * @Date 2019-06-02 20:38:25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SUserAppMenu  implements Serializable {

	private Long id;
	private Long sUserId;
	private Long appMenuId;
	private Integer isDelete;	//0： 删除，1：启用

}

