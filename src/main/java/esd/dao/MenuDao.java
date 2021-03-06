package esd.dao;

import java.util.List;

import esd.bean.Menu;

/**
 * menu Dao
 * 
 * @author Administrator
 * 
 */
public interface MenuDao extends IDao<Menu> {

	// 按id查询一个对象
	public Menu getById(String id);
	
	// 获得所有菜单状态
	public List<Menu> getMenuChecked();

}