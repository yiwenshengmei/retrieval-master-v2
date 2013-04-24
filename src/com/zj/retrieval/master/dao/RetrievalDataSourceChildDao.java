package com.zj.retrieval.master.dao;

import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.zj.retrieval.master.Configuration;

public class RetrievalDataSourceChildDao {
	private SimpleJdbcTemplate template;
	
	public static RetrievalDataSourceChildDao getInstance() {
		return (RetrievalDataSourceChildDao) Configuration.getBean("retrievalDataSourceChildDao");
	}
	
	public void insert(List<String> childs, String headerId) {
		StringBuilder sql = new StringBuilder()
		.append("INSERT INTO T_RETRIEVAL_DATA_SOURCE_CHILD(`ID`, `RDSC_HEADER_ID`, `RDSC_INDEX`, `RDSC_TO_NODE_ID`)")
		.append(" VALUES(:id, :headerId, :index, :toNodeId)");

		for (int index = 0; index < childs.size(); index++) {
			SqlParameterSource param = new MapSqlParameterSource()
				.addValue("id", UUID.randomUUID().toString())
				.addValue("headerId", headerId)
				.addValue("index", index)
				.addValue("toNodeId", childs.get(index));
			template.update(sql.toString(), param);
		}
	}
	
	public void setDataSource(DataSource dataSource) {
		this.template = new SimpleJdbcTemplate(dataSource);
	}
}
