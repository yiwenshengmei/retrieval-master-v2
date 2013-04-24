package com.zj.retrieval.master.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.zj.retrieval.master.Configuration;
import com.zj.retrieval.master.AttributeSelector;
import com.zj.retrieval.master.DetailType;
import com.zj.retrieval.master.Matrix;
import com.zj.retrieval.master.Node;
import com.zj.retrieval.master.entity.NodeAttribute;
import com.zj.retrieval.master.entity.NodeFeature;
import com.zj.retrieval.master.entity.NodeImage;
import com.zj.retrieval.master.entity.RetrievalDataSource;
import com.zj.retrieval.master.mapper.NodeRowMapper;
import com.zj.retrieval.master.service.BizNode;

public class NodeDao {
	
	private SimpleJdbcTemplate template;
	private TransactionTemplate tt;
	private static Logger logger = LoggerFactory.getLogger(NodeDao.class);
	
	public void setTxManager(PlatformTransactionManager txManager) {
		this.tt = new TransactionTemplate(txManager);
	}
	
	public List<Node> getAllNodeAsBrief() {
		String sql = "select `id`, `images` as imagesStr, `name`, `parent_id` as parentId, `detail_type` as detailType from `fish`";
		ParameterizedRowMapper<Node> rm = 
				ParameterizedBeanPropertyRowMapper.newInstance(Node.class);
		List<Node> queryResult = template.query(sql, rm);
		return queryResult;
	}
	
	public boolean delete(Node node) {
		try {
			// 原先的nd只有id信息，现在根据id从数据库中取出该node的完整信息
			node = queryById(node.getId());
			// 找到它的父节点
			Node parent = queryById(node.getParentId());
			
			// 将nd在其父节点中的信息删除，即从父节点的子结点列表中删除nd
			RetrievalDataSource dataSource = parent.getRetrievalDataSource();
			int row = dataSource.getChildNodes().indexOf(node.getId());
			dataSource.getChildNodes().remove(node.getId());
			// 删除父节点矩阵中的相关信息
			dataSource.getMatrix().removeRow(row);
			// 更新父节点的owl
			parent.setOwl(BizNode.getOWL(parent));
			// 将父节点写回数据库
			String sqlUpdateParentNode = "UPDATE `fish` SET `owl`=:owl WHERE `id`=:id";
			SqlParameterSource paramUpdateParentNode = new BeanPropertySqlParameterSource(parent);
			if (template.update(sqlUpdateParentNode, paramUpdateParentNode) != 1) {
				throw new Exception("更新父类时失败@NodeService.delNode()"); // Rollback
			}
			
			// 开始从数据库中删除nd
			String sql = "DELETE FROM `fish` where id=?";
			if (template.update(sql, node.getId()) != 1)
				throw new Exception(String.format("删除节点[id=%1$s]时失败", node.getId()));
			else
				return true;
		} catch (Exception ex) {
			logger.error("delNode()发生错误", ex);
			return false;
		}
	}

	public boolean update(Node node) throws Exception {
		try {
			node.setOwl(BizNode.getOWL(node));
			String sql = "update fish " +
					"set `uri_name` = :uriName, " +
					"`name` = :name, " +
					"`images` = :imagesStr, " +
					"`name_en` = :englishName, " +
					"`parent_id` = :parentId, " +
					"`owl` = :owl, " +
					"`uri` = :uri " +
					"where `id` = :id";
			SqlParameterSource param = new BeanPropertySqlParameterSource(node);
			if (template.update(sql, param) != 1) {
				throw new Exception("更新节点返回结果不为1"); // Rollback
			}
			return true;
		} catch (Exception ex) {
			logger.error(String.format("查询节点时出错[id=%1$s]", node.getId()), ex);
			throw new Exception("更新节点时出错", ex);
		}
	}
	
	public boolean updateRootNode(Node root) throws Exception {
		try {
			root.setOwl(BizNode.getOWL(root));
			String sql = "update `fish` set `uri_name`=:uriName, `name`=:name, `name_en`=:englishName, `images`=:imagesStr, " +
					"`owl`=:owl, `uri`=:uri where `id`=:id";
			SqlParameterSource param = new BeanPropertySqlParameterSource(root);
			int result = template.update(sql, param);
			
			if (result != 1) throw new Exception("插入rootNode时返回结果不等于1.");
			return true;
		} catch (Exception ex) {
			logger.error(String.format("查询节点时出错[id=%1$s]", root.getId()), ex);
			throw new Exception("更新节点时出错", ex);
		}
		
	}
	
	public Node getNodeByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}



	public void addNode(Node newNode, Node parentNode, AttributeSelector as) {
		try {
			// 更新parent的matrix属性
			logger.info("更新父节点的特征矩阵");
			Matrix matrix = parentNode.getRetrievalDataSource().getMatrix();
			//   先修改行：将newNode和parentNode中已有特性的进行匹配
			//   如果matrix为空[列数或行数等于零]，从特征矩阵的语义上来说无论添加多少行
			//   都等于没有添加，因为没有已知的特性与其匹配
			int[] newRow = new int[matrix.getColSize()];
			for(int i = 0; i < newRow.length; i++)
				newRow[i] = as.getAttributeMapping().get(i) ? NodeFeature.YES : NodeFeature.NO;
			matrix.addRow(newRow, 0, newRow.length);

			//   再修改列：向parentNode添加创建newNode时一起添加的新特性
			//   在添加新特性的同时将新特性加入parentNode的attribute列表
			List<NodeFeature> parentAttributes = parentNode.getRetrievalDataSource().getAttributes();
			for(NodeFeature attr : as.getNewAttributeMapping().keySet()) {
				// matrix可能为空，向空矩阵添加1列需要的长度始终是1
				int[] newCol = matrix.getRowSize() == 0 ? new int[1] : new int[matrix.getRowSize()];
				for(int j = 0; j < newCol.length; j++) {
					newCol[j] = (j != newCol.length - 1 ? 0 : 
						(as.getNewAttributeMapping().get(attr) ? NodeFeature.YES : NodeFeature.NO));
				}
				matrix.addCol(newCol, 0, newCol.length);
				// 同时更新parentNode的attribte列表
				parentAttributes.add(attr);
			}
			
			// 创建并设置newNode的owl字符串
			logger.info("创建并设置新节点的owl字符串");
			newNode.setOwl(BizNode.getOWL(newNode));
			
			// 向数据库中插入newNode并且获得自动生成的id值
			// 将id值设置到newNode中
			logger.info("将新节点写入数据库");
			if (newNode.getId().isEmpty() || newNode.getId() == null) {
				newNode.setId(UUID.randomUUID().toString());
			}
			String sqlInsertNewNode = "insert into fish(`id`, `uri_name`, `name`, `images`, " +
					"`name_en`, `parent_id`, `owl`, `uri`) values(:id, :uriName, :name, :imagesStr, " +
					":englishName, :parentId, :owl, :uri)";
			SqlParameterSource paramInsertNewNode = new BeanPropertySqlParameterSource(newNode);
			if (template.update(sqlInsertNewNode, paramInsertNewNode) != 1) {
				throw new Exception("插入节点时失败@NodeService.addNode()"); // Rollback
			}
	
			// 将newNode的id值加入parentNode的childNodes列表中
			logger.info("更新父节点的子结点列表");
			parentNode.getRetrievalDataSource().getChildNodes().add(newNode.getId());
			// 更新parentNode的owl字符串
			logger.info("更新父节点的owl字符串");
			parentNode.setOwl(BizNode.getOWL(parentNode));
			
			// 将parentNode重新写回数据库
			// 由于只修改了parentNode的owl信息，所以这里只更新owl字段
			logger.info("将父节点写回数据库");
			String sqlUpdateParentNode = "update `fish` set `owl`=:owl where id=:id";
			SqlParameterSource paramUpdateParentNode = new BeanPropertySqlParameterSource(parentNode);
			if (template.update(sqlUpdateParentNode, paramUpdateParentNode) != 1) {
				throw new Exception("更新父类时失败@NodeService.addNode()"); // Rollback
			}
			
		} catch (Exception ex) {
			logger.error("NodeService.addNode()方法发生错误", ex);
			throw new RuntimeException("NodeService.addNode()方法发生错误", ex);
		}
	}

	public void setDataSource(DataSource dataSource) {
		template = new SimpleJdbcTemplate(dataSource);
	}
	
	public static void createVirtualNode(SimpleJdbcTemplate sess) throws Exception {
		StringBuilder insertVNodeSQL = new StringBuilder()
		.append("INSERT INTO `fish`(")
		.append("`id`, `uri_name`, `name`, `name_en`, `parent_id`, `owl`, `uri`) ")
		.append("VALUES(?,'','','',?,'','');");
		int insert_vrtl_node_result = sess.getJdbcOperations().update(insertVNodeSQL.toString(), new Object[] {Node.VIRTUAL_NODE_ID, "-1"});
		if (insert_vrtl_node_result != 1) {
			logger.error("创建VirtualNode失败。");
			throw new Exception("创建VirtualNode失败。");
		}
	}
	
	private void doInsert(Node node) {
		if (StringUtils.isBlank(node.getId()))
			node.setId(UUID.randomUUID().toString());
		logger.debug("id=" + node.getId());
		
		StringBuilder sql = new StringBuilder()
		.append("INSERT INTO `T_NODE`(`ID`, `ND_URI_NAME`, `ND_NAME`, `ND_PARENT_ID`")
		.append(", `ND_URI`, `ND_DETAIL_TYPE_ID`, `ND_CONTACT`, `ND_NAME_EN`) ")
		.append("VALUES(:id, :uriName, :name, :parentId, :uri, :detailTypeId, :contact, :englishName)");
		SqlParameterSource param = new BeanPropertySqlParameterSource(node);
		
		int result = template.update(sql.toString(), param);
		if (result != 1) throw new RuntimeException("插入根节点的结果不为1");
	}
	
	public void insert(final Node node) throws Exception {
		tt.execute(new TransactionCallback() {
			@Override
			public Object doInTransaction(TransactionStatus arg0) {
				// Save to T_NODE.
				doInsert(node);
				
				// Save to T_IMAGE.
				NodeImageDao imgdao = Configuration.getNodeImageDao();
				for (NodeImage img : node.getImages()) {
					imgdao.insert(img);
				}
				
				// Save to T_CUSTOMER_FIELD.
				for (NodeAttribute fd : node.getCustomerFields()) {
					CustomerFieldDao.getInstance().insert(fd);
				}
				
				// Save to T_RETRIEVAL_DATA_SOURCE.
				RetrievalDataSourceDao.getInstance().insert(node.getRetrievalDataSource());
				
				return null;
			}
		});
		
			
//			// 2.更新VirtualNode[id=Node.VIRTUAL_NODE_ID]的childNodes属性
//			logger.debug("更新虚节点");
//			String vtrlNodeQuerySQL = "select `owl` from `fish` where `id`=?";
//			List<Node> vNodes = sess.query(vtrlNodeQuerySQL, new NodeRowMapper(), Node.VIRTUAL_NODE_ID);
//			
//			Node vNode = null;
//			if (vNodes == null) {
//				logger.error("查询VirtualNode时返回null。");
//				throw new Exception("查询VirtualNode时返回null。");
//			}
//			if (vNodes.size() == 0) {
//				logger.info("VirtualNode不存在，重建之");
//				createVirtualNode(sess);
//			} else {
//				vNode = vNodes.get(0);
//			}
//			
//			vNode.getRetrievalDataSource().getChildNodes().add(rootNode.getId());
//	
//			// 重建VirtualNode的owl字段
//			vNode.setOwl(Node.getOwlFromNode(vNode, sess));
//			logger.info("生成新的owl字符串：" + vNode.getOwl());
//			
//			// 将VirtualNode写回数据库
//			logger.info("开始将VirtualNode写回数据...");
//			String update_vrtl_node_sql = "update `fish` set `owl`=:owl where `id`='virtual_node'";
//			SqlParameterSource vrtl_node_param = new BeanPropertySqlParameterSource(vNode);
//			sess.update(update_vrtl_node_sql, vrtl_node_param);
//			logger.info("VirtualNode写回数据库成功");
	}

	// 增加只包含检索信息的节点
	public void addNodeBrief(Node newNode, Node parentNode, AttributeSelector as) {
//		try {
//			// 更新parent的matrix属性
//			logger.info("更新父节点的特征矩阵");
//			Matrix matrix = parentNode.getRetrievalDataSource().getMatrix();
//			//   先修改行：将newNode和parentNode中已有特性的进行匹配
//			//   如果matrix为空[列数或行数等于零]，从特征矩阵的语义上来说无论添加多少行
//			//   都等于没有添加，因为没有已知的特性与其匹配
//			int[] newRow = new int[matrix.getColSize()];
//			for(int i = 0; i < newRow.length; i++)
//				newRow[i] = as.getAttributeMapping().get(i) ? NodeAttribute.YES : NodeAttribute.NO;
//			matrix.addRow(newRow, 0, newRow.length);
//
//			//   再修改列：向parentNode添加创建newNode时一起添加的新特性
//			//   在添加新特性的同时将新特性加入parentNode的attribute列表
//			List<NodeAttribute> parentAttributes = parentNode.getRetrievalDataSource().getAttributes();
//			for(NodeAttribute attr : as.getNewAttributeMapping().keySet()) {
//				// matrix可能为空，向空矩阵添加1列需要的长度始终是1
//				int[] newCol = matrix.getRowSize() == 0 ? new int[1] : new int[matrix.getRowSize()];
//				for(int j = 0; j < newCol.length; j++) {
//					newCol[j] = (j != newCol.length - 1 ? 0 : 
//						(as.getNewAttributeMapping().get(attr) ? NodeAttribute.YES : NodeAttribute.NO));
//				}
//				matrix.addCol(newCol, 0, newCol.length);
//				// 同时更新parentNode的attribte列表
//				parentAttributes.add(attr);
//			}
//			
//			// 创建并设置newNode的owl字符串
////			log.info("创建并设置新节点的owl字符串");
////			newNode.setOwl(Node.getOwlFromNode(newNode, sqlclient));
//			
//			logger.info("将新节点写入数据库");
//			// 注意，brief节点的ID值是由客户端提供的！！！
//			// 为新节点detailType设置值: brief
//			newNode.setDetailType(DetailType.BRIEF);
//			String sqlInsertNewNode = "insert into fish(`id`, `name`, `name_en`, `parent_id`, `contact`, `detail_type`) values(" +
//					                                    ":id, :name, :englishName, :parentId, :contact, :detailType)";
//			SqlParameterSource paramInsertNewNode = new BeanPropertySqlParameterSource(newNode) ;
//			if (template.update(sqlInsertNewNode, paramInsertNewNode) != 1) {
//				throw new Exception("插入节点时失败@NodeService.addNode()"); // Rollback
//			}
//	
//			// 将newNode的id值加入parentNode的childNodes列表中
//			logger.info("更新父节点的子结点列表");
//			parentNode.getRetrievalDataSource().getChildNodes().add(newNode.getId());
//			// 更新parentNode的owl字符串
//			logger.info("更新父节点的owl字符串");
//			parentNode.setOwl(BizNode.getOWL(parentNode));
//					
//			// 将parentNode重新写回数据库
//			// 由于只修改了parentNode的owl信息，所以这里只更新owl字段
//			logger.info("将父节点写回数据库");
//			String sqlUpdateParentNode = "update `fish` set `owl`=:owl where id=:id";
//			SqlParameterSource paramUpdateParentNode = new BeanPropertySqlParameterSource(parentNode);
//			if (template.update(sqlUpdateParentNode, paramUpdateParentNode) != 1) {
//				throw new Exception("更新父类时失败@NodeService.addNode()"); // Rollback
//			}
//			
//		} catch (Exception ex) {
//			logger.error("NodeService.addNode()方法发生错误", ex);
//			throw new RuntimeException("NodeService.addNode()方法发生错误", ex);
//		}
	}

	public static NodeDao getInstance() {
		return (NodeDao) Configuration.getBean("nodeDao");
	}
}
