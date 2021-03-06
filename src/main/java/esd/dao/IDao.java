package esd.dao;

import java.util.List;
import java.util.Map;

/**
 * 公共Dao接口
 * 
 * @author Administrator
 * 
 */
public interface IDao<T> {

	// 保存一个对象
	public boolean save(T t);

	// 删除一个对象
	public boolean delete(Integer id);

	// 删除一个对象
	public boolean delete(String id);

	// 更新一个对象
	public boolean update(T t);

	// 按id查询一个对象
	public T getById(Integer id);

	// 按id查询一个对象
	public T getById(String id);
	
	/**
	 * 根据id, 得到该对象的最新updatecheck
	 * @param id
	 * @return
	 */
	public Integer getUpdateCheck(Integer id);
	
	/**
	 * 根据id, 得到该对象的最新updatecheck
	 * @param id
	 * @return
	 */
	public Integer getUpdateCheck(String  id);

	// 根据自身对象属性值查找是否存在对象
	public List<T> getByObj(T t);

	// 分页查询方法, @param map中为具体的参数:
	// 1-类对象, 名称为对应类的小写!!切记切记!! 字段的值即为查询条件; 2-start: 起始索引; 3-size: 返回条数
	public List<T> getByPage(Map<String, Object> map);

	// 获得数据总条数-与上面的分页查询方法配合使用
	public int getTotalCount(Map<String, Object> map);

}
