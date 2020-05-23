package com.jt.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jt.mapper.ItemCatMapper;
import com.jt.mapper.ItemMapper;
import com.jt.pojo.ItemCat;
import com.jt.vo.EasyUITree;

@Service
public class ItemCatServiceImpl implements ItemCatService {
	
	@Autowired
	private ItemCatMapper itemCatMapper;

	@Override
	public ItemCat findItemCatNameById(Long itemCatId) {
		
		return itemCatMapper.selectById(itemCatId);
	}

	/**
	 * 数据转化:
	 * 	List<EasyUITree> VO对象 页面要求返回的数据结果
	 *  List<ItemCat>    数据库记录
	 *  ItemCat对象转化EasyUITree对象
	 */
	@Override
	public List<EasyUITree> findItemCatList(Long parentId) {
		//1.根据parentId查询数据库记录 
		List<ItemCat> catList = findItemCatListByParentId(parentId);
		List<EasyUITree> treeList = new ArrayList<EasyUITree>();
		//2.利用循环的方式实现数据的遍历
		for (ItemCat itemCat : catList) {
			//目的为了封装VO对象
			Long id = itemCat.getId();
			String text = itemCat.getName();	//获取节点名称
			//如果是父级则默认closed,否则open  可以被选中
			String state = itemCat.getIsParent() ? "closed" : "open";
			EasyUITree tree = new EasyUITree(id, text, state);
			//将tree对象封装到List集合中
			treeList.add(tree);
		}
		return treeList;
	}

	private List<ItemCat> findItemCatListByParentId(Long parentId) {
		QueryWrapper<ItemCat> queryWrapper = new QueryWrapper<ItemCat>();
		queryWrapper.eq("parent_id", parentId);
		return itemCatMapper.selectList(queryWrapper);
	}
	
}
