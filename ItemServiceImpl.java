package com.jt.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jt.mapper.ItemDescMapper;
import com.jt.mapper.ItemMapper;
import com.jt.pojo.Item;
import com.jt.pojo.ItemDesc;
import com.jt.vo.EasyUITable;

@Service
public class ItemServiceImpl implements ItemService {
	
	@Autowired
	private ItemMapper itemMapper;
	@Autowired
	private ItemDescMapper itemDescMapper;
	
	/**
	 * 原理:指定Page对象之后根据条件查询.返回Page对象. 包含了分页相关的全部数据.
	 * 使用MP方式实现分页
	 * 1.current  查询页数
	 * 2.size     查询记录数
	 */
	@Override
	public EasyUITable findItemByPage(int page, int rows) {
		//传递Page对象 之后可以动态的获取所有的分页数据
		IPage<Item> iPage = new Page<>(page, rows);
		QueryWrapper<Item> queryWrapper = new QueryWrapper<Item>();
		//降序排列
		queryWrapper.orderByDesc("updated");
		iPage = itemMapper.selectPage(iPage, queryWrapper);
		//1.获取记录总数
		int total = (int) iPage.getTotal();
		List<Item> itemList = iPage.getRecords();
		return new EasyUITable(total, itemList);
	}
	
	/**
	 * 用户点击一次提交之后,实现2张表数据,同时入库
	 * tb_item,tb_itemDesc,id的值应该相同
	 */
	@Override
	@Transactional	//控制事务
	public void saveItem(Item item,ItemDesc itemDesc){
		//实现tb_item入库
		item.setStatus(1) 
		.setCreated(new Date())
		.setUpdated(item.getCreated());
		itemMapper.insert(item);	//主键自增,入库之后,将所有字段自动映射!!
		
		//实现tb_itemDesc入库操作
		itemDesc.setItemId(item.getId())
				.setCreated(item.getCreated())
				.setUpdated(item.getUpdated());
		itemDescMapper.insert(itemDesc);
	}

	/**
	 * 根据甲方需求 实现CRUD
	 */
	@Override
	public void updateItem(Item item) {
		System.out.println(item);
		//根据主键进行修改.
		item.setUpdated(new Date());
		//根据对象中不为null的属性充当set条件
		itemMapper.updateById(item);
	}

	@Override
	public void deleteItems(Long[] ids) {
		
		List<Long> idList = Arrays.asList(ids);
		//MP方式实现数据删除
		//itemMapper.deleteBatchIds(idList);
		//手动的实现数据删除操作
		itemMapper.deleteItems(ids);
	}

	@Override
	public void updateStatus(int status, Long[] ids) {
		
		//1.使用MP的方式实现数据更新  entity 修改修改的数据,updateWrapper
		/**
		Item item = new Item();
		item.setStatus(status)
			.setUpdated(new Date());
		UpdateWrapper<Item> updateWrapper = new UpdateWrapper<Item>();
		updateWrapper.in("id", Arrays.asList(ids));
		itemMapper.update(item, updateWrapper);
		**/
		//2.作业 使用sql的方式实现该业务.
		itemMapper.updateStatus(status,new Date(),ids);
		
	}
	
	
	
	
	
	
	
}	
	
	/**
	 * 分页sql语句       每页20条
	 * 	第一页:
	 * 	select * from tb_item limit 0,20;	21个数   取20个 [0,19]下标
	 * 	第二页:
	 * 	select * from tb_item limit 20,20;	21个数  取20个  [20,39]下标
	 * 	第三页:
	 * 	select * from tb_item limit 40,20;	21个数  取20个  [40,59]下标
	 * 	第N页:
	 * 	select * from tb_item limit (page-1)rows,rows;
	 */
	/*
	 * @Override public EasyUITable findItemByPage(int page, int rows) {
	 * 
	 * //1.total 记录总数 Integer total = itemMapper.selectCount(null); //2.list 分页之后的结果
	 * 手写分页 int start = (page-1)*rows; List<Item> itemList =
	 * itemMapper.findItemByPage(start,rows); return new EasyUITable(total,
	 * itemList); }
	 */

