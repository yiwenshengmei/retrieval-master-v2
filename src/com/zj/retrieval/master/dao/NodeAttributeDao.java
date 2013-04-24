package com.zj.retrieval.master.dao;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.zj.retrieval.master.Configuration;
import com.zj.retrieval.master.entity.NodeFeature;

public class NodeAttributeDao {
	private SimpleJdbcTemplate template;
	
	public void setDataSource(DataSource dataSource) {
		this.template = new SimpleJdbcTemplate(dataSource);
	}
	
	public void insert(NodeFeature attr) {
		StringBuilder sql = new StringBuilder()
		.append("INSERT INTO T_ATTRIBUTE(`ID`, `ATTR_NAME`, `ATTR_NAME_EN`, `ATTR_HEADER_ID`, `ATTR_DESC`, `ATTR_INDEX`) ")
		.append("VALUES(:id, :name, :englishName, :headerId, :desc, :index)");
		
		SqlParameterSource param = new BeanPropertySqlParameterSource(attr);
		int result = template.update(sql.toString(), param);
		if (result != 1) throw new RuntimeException("插入T_ATTRIBUTE时返回结果不为1");
	}
	
	public static NodeAttributeDao getInstance() {
		return (NodeAttributeDao) Configuration.getBean("nodeAttributeDao");
	}
}
