package org.yyzh.hos.sys;

import lombok.*;
import java.io.Serializable;
import java.util.Date;

/**
 *  
 * @ClassName AppMenu
 * @Description TODO 应用菜单
 * @Author baiHoo.chen
 * @Date 2019-06-02 20:38:25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppMenu  implements Serializable {

	private Long id;	//菜单唯一标识
	private String menuName;	//菜单名称
	private String menuLabel;	//菜单标签
	private String menuCode;	//菜单编码
	private String isLeaf;	//是否为叶子节点
	private String parameter;	//菜单参数
	private String uiEntry;	//UI 实体
	private String menuLevel;	//菜单级别
	private Long rootId;	//根节点
	private String displayOrder;	//显示排序
	private String imagePath;	//图片路径
	private String expandPath;	//展开图标路径
	private String menuSeq;	//菜单层级序列码
	private String openMode;	//打开模式
	private Integer subCount;	//子节点个数
	private Long appId;	//应用ID
	private String funcCode;	//菜单对应的功能编码
	private Long parentSid;	//父节点ID
	private Date createDate;
	private Date updateDate;
	private Integer isDelete;	//0： 删除，1：启用

}

