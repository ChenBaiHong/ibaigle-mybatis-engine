package org.yyzh.hos.sys;

import lombok.*;
import java.io.Serializable;
import java.util.Date;

/**
 *  
 * @ClassName SHospital
 * @Description TODO 系统中医院基本信息
 * @Author baiHoo.chen
 * @Date 2019-06-02 20:38:25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SHospital  implements Serializable {

	private Long id;	//0： 删除，1：启用
	private String hospitalName;
	private String hospitalCode;
	private String healthOrganName;
	private String healthOrganCode;
	private String healthOrganUrl;
	private Date createDate;
	private Date updateDate;
	private Integer isDelete;

}

