package com.zj.retrieval.master.dao;

import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.zj.retrieval.master.Node;
import com.zj.retrieval.master.entity.NodeImage;
import com.zj.retrieval.master.mapper.NodeImageRowMapper;

public class NodeImageDao {
	private SimpleJdbcTemplate template;
	private final Logger logger = LoggerFactory.getLogger(NodeImageDao.class);
	
	public void insert(NodeImage img) {
		StringBuilder sql = new StringBuilder()
		.append("INSERT INTO `T_IMAGE`(`ID`, `IMG_PATH`, `IMG_HEADER_ID`) VALUES(:id, :path, :headerId)");
		
		SqlParameterSource param = new BeanPropertySqlParameterSource(img);
		int result = template.update(sql.toString(), param);
		if (result != 1) throw new RuntimeException("插入image时返回结果不为1");
	}
	
	public List<NodeImage> queryByNodeId(String nodeId) throws Exception {
		String sql = "SELECT * FROM T_IMAGE IMG WHERE IMG.ID = ?";
		return template.query(sql, new NodeImageRowMapper(), new Object[] { nodeId });
	}
	
	public void setDataSource(DataSource dataSource) {
		this.template = new SimpleJdbcTemplate(dataSource);
	}
}
