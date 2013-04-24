package com.zj.retrieval.master.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.zj.retrieval.master.entity.NodeImage;

public class NodeImageRowMapper implements ParameterizedRowMapper<NodeImage> {

	@Override
	public NodeImage mapRow(ResultSet rs, int arg1) throws SQLException {
		NodeImage image  = new NodeImage();
		image.setId(rs.getString("ID"));
		image.setHeaderId(rs.getString("HEADER_ID"));
		image.setPath(rs.getString("PATH"));
		return image;
	}

}
