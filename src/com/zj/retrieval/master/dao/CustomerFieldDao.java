package com.zj.retrieval.master.dao;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.zj.retrieval.master.AbstractAction;
import com.zj.retrieval.master.Configuration;
import com.zj.retrieval.master.entity.NodeAttribute;

public class CustomerFieldDao extends AbstractAction {
	public static CustomerFieldDao getInstance() {
		return (CustomerFieldDao) Configuration.getBean("customerFieldDao");
	}
	
	public void insert(NodeAttribute field) {
		StringBuilder sql = new StringBuilder()
		.append("INSERT INTO T_CUSTOMER_FIELD(`ID`, `FD_KEY`, `FD_VALUE`, `FD_HEADER_ID`)")
		.append(" VALUES(:id, :key, :value, :headerId)");
		
		SqlParameterSource param = new BeanPropertySqlParameterSource(field);
		template.update(sql.toString(), param);
	}
}
