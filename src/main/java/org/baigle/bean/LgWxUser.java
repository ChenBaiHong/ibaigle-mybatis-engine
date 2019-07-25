package org.baigle.bean;

import lombok.*;
import java.io.Serializable;
import java.util.Date;

/**
 *  
 * @ClassName LgWxUser
 * @Description TODO 柳工医院微信用户表
 * @Author baiHoo
 * @Date 2019-06-20 10:53:15
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LgWxUser  implements Serializable {

	private String id;	//医疗行业唯一识别主键ID
	private String wxName;	//微信用户名称
	private String wxGender;	//微信用户性别,
	private String wxGenderDict;	//微信用户性别字典文本
	private String wxAge;	//微信用户年龄
	private String wxIcon;	//微信用户头像
	private String openId;	//微信用户的OPENID
	private String qrCodeImg;	//二维码图片
	private Date lastVisitTime;	//最后登录时间
	private String sessionKey;	//会话标识键
	private String city;	//市
	private String province;	//省
	private String country;	//国家
	private String nickName;	//网名
	private String createBy;	//创建人
	private Date createTime;	//创建时间
	private String updateBy;	//更新人
	private Date updateTime;	//更新时间

}

