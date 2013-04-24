package com.zj.retrieval.master.dao;

import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.zj.retrieval.master.Configuration;
import com.zj.retrieval.master.Matrix;

public class MatrixDao {
	private SimpleJdbcTemplate template;
	
	public void insert(Matrix m) {
		String sql = "INSERT INTO T_MATRIX(`ID`, `MTX_HEADER_ID`) VALUES(:id, :headerId)";
		if (StringUtils.isBlank(m.getId())) 
			m.setId(UUID.randomUUID().toString());
		
		SqlParameterSource param = new BeanPropertySqlParameterSource(m);
		if (template.update(sql, param) != 1)
			throw new RuntimeException("The value of insert T_MATRIX is not 1");
		
		insertMatrixRow(m);
	}
	
	private void insertMatrixRow(Matrix m) {
		StringBuilder sql = new StringBuilder()
		.append("INSERT INTO T_MATRIX_ROW(`ID`, `MTXR_MATRIX_ID`, `MTXR_ROW_INDEX`, `MTXR_COL_INDEX`, `MTXR_VALUE`)")
		.append(" VALUES(:id, :matrixId, :row, :col, :value)");
		
		for (int row = 0; row < m.getRowSize(); row++) {
			for (int col = 0; col < m.getColSize(); col++) {
				SqlParameterSource param = new MapSqlParameterSource()
					.addValue("id", UUID.randomUUID().toString())
					.addValue("matrixId", m.getId())
					.addValue("row", row)
					.addValue("col", col)
					.addValue("value", m.getValue(row, col));
				if (template.update(sql.toString(), param) != 1) 
					throw new RuntimeException("The value of insert T_MATRIX_ROW is not 1");
			}
		}
	}
	
	public void setDataSource(DataSource dataSource) {
		this.template = new SimpleJdbcTemplate(dataSource);
	}
	
	public static MatrixDao getInstance() {
		return (MatrixDao) Configuration.getBean("matrixDao");
	}
}
