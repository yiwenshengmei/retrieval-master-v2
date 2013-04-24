/*
 * (c) Copyright 2007 Bokesoft Co,Ltd. All Rights Reserved.
 * $Id: himalaya-codetemplates.xml 13967 2009-04-09 01:30:41Z xuel $
 */
package com.zj.retrieval.master.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.zj.retrieval.master.Configuration;
import com.zj.retrieval.master.Node;
import com.zj.retrieval.master.dao.NodeImageDao;
import com.zj.retrieval.master.entity.NodeImage;
import com.zj.retrieval.master.service.BizNode;

public class NodeRowMapper implements ParameterizedRowMapper<Node> {
	
	private final Logger logger = LoggerFactory.getLogger(NodeRowMapper.class);

	@Override
	public Node mapRow(ResultSet rs, int arg1) throws SQLException {
		/*
		 * 
		 * String sql = "select `id`, `uri_name` as uriName, `name`, `images` as imagesStr, " +
					"`name_en` as englishName, `parent_id` as parentId, " +
					"`owl`, `uri`, `detail_type` as detailType, `contact` from `fish` where `id`=?";
		 * 
		 */
		Node node = new Node();
		node.setId(rs.getString("id"));
		node.setUri(rs.getString("uri"));
		node.setName(rs.getString("name"));
		node.setEnglishName(rs.getString("name_en"));
		node.setDesc(rs.getString("desc"));
		node.setContact(rs.getString("contact"));
		
		try {
			List<NodeImage> imgs = Configuration.getNodeImageDao().queryByNodeId(node.getId());
		}
		catch (Exception e) {
			logger.error(String.format("在为node[id=%1$s]赋值images时发生错误", node.getId()), e);
		}
		
		try {
			node.setOwl(BizNode.getOWL(node));
		}
		catch (Exception e) {
			logger.error(String.format("在为node[id=%1$s]赋值owl时发生错误", node.getId()), e);
		}
		return node;
	}

}
