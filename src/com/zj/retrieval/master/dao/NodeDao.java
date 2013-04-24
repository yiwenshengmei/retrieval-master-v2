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
			// ԭ�ȵ�ndֻ��id��Ϣ�����ڸ���id�����ݿ���ȡ����node��������Ϣ
			node = queryById(node.getId());
			// �ҵ����ĸ��ڵ�
			Node parent = queryById(node.getParentId());
			
			// ��nd���丸�ڵ��е���Ϣɾ�������Ӹ��ڵ���ӽ���б���ɾ��nd
			RetrievalDataSource dataSource = parent.getRetrievalDataSource();
			int row = dataSource.getChildNodes().indexOf(node.getId());
			dataSource.getChildNodes().remove(node.getId());
			// ɾ�����ڵ�����е������Ϣ
			dataSource.getMatrix().removeRow(row);
			// ���¸��ڵ��owl
			parent.setOwl(BizNode.getOWL(parent));
			// �����ڵ�д�����ݿ�
			String sqlUpdateParentNode = "UPDATE `fish` SET `owl`=:owl WHERE `id`=:id";
			SqlParameterSource paramUpdateParentNode = new BeanPropertySqlParameterSource(parent);
			if (template.update(sqlUpdateParentNode, paramUpdateParentNode) != 1) {
				throw new Exception("���¸���ʱʧ��@NodeService.delNode()"); // Rollback
			}
			
			// ��ʼ�����ݿ���ɾ��nd
			String sql = "DELETE FROM `fish` where id=?";
			if (template.update(sql, node.getId()) != 1)
				throw new Exception(String.format("ɾ���ڵ�[id=%1$s]ʱʧ��", node.getId()));
			else
				return true;
		} catch (Exception ex) {
			logger.error("delNode()��������", ex);
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
				throw new Exception("���½ڵ㷵�ؽ����Ϊ1"); // Rollback
			}
			return true;
		} catch (Exception ex) {
			logger.error(String.format("��ѯ�ڵ�ʱ����[id=%1$s]", node.getId()), ex);
			throw new Exception("���½ڵ�ʱ����", ex);
		}
	}
	
	public boolean updateRootNode(Node root) throws Exception {
		try {
			root.setOwl(BizNode.getOWL(root));
			String sql = "update `fish` set `uri_name`=:uriName, `name`=:name, `name_en`=:englishName, `images`=:imagesStr, " +
					"`owl`=:owl, `uri`=:uri where `id`=:id";
			SqlParameterSource param = new BeanPropertySqlParameterSource(root);
			int result = template.update(sql, param);
			
			if (result != 1) throw new Exception("����rootNodeʱ���ؽ��������1.");
			return true;
		} catch (Exception ex) {
			logger.error(String.format("��ѯ�ڵ�ʱ����[id=%1$s]", root.getId()), ex);
			throw new Exception("���½ڵ�ʱ����", ex);
		}
		
	}
	
	public Node getNodeByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}



	public void addNode(Node newNode, Node parentNode, AttributeSelector as) {
		try {
			// ����parent��matrix����
			logger.info("���¸��ڵ����������");
			Matrix matrix = parentNode.getRetrievalDataSource().getMatrix();
			//   ���޸��У���newNode��parentNode���������ԵĽ���ƥ��
			//   ���matrixΪ��[����������������]���������������������˵������Ӷ�����
			//   ������û����ӣ���Ϊû����֪����������ƥ��
			int[] newRow = new int[matrix.getColSize()];
			for(int i = 0; i < newRow.length; i++)
				newRow[i] = as.getAttributeMapping().get(i) ? NodeFeature.YES : NodeFeature.NO;
			matrix.addRow(newRow, 0, newRow.length);

			//   ���޸��У���parentNode��Ӵ���newNodeʱһ����ӵ�������
			//   ����������Ե�ͬʱ�������Լ���parentNode��attribute�б�
			List<NodeFeature> parentAttributes = parentNode.getRetrievalDataSource().getAttributes();
			for(NodeFeature attr : as.getNewAttributeMapping().keySet()) {
				// matrix����Ϊ�գ���վ������1����Ҫ�ĳ���ʼ����1
				int[] newCol = matrix.getRowSize() == 0 ? new int[1] : new int[matrix.getRowSize()];
				for(int j = 0; j < newCol.length; j++) {
					newCol[j] = (j != newCol.length - 1 ? 0 : 
						(as.getNewAttributeMapping().get(attr) ? NodeFeature.YES : NodeFeature.NO));
				}
				matrix.addCol(newCol, 0, newCol.length);
				// ͬʱ����parentNode��attribte�б�
				parentAttributes.add(attr);
			}
			
			// ����������newNode��owl�ַ���
			logger.info("�����������½ڵ��owl�ַ���");
			newNode.setOwl(BizNode.getOWL(newNode));
			
			// �����ݿ��в���newNode���һ���Զ����ɵ�idֵ
			// ��idֵ���õ�newNode��
			logger.info("���½ڵ�д�����ݿ�");
			if (newNode.getId().isEmpty() || newNode.getId() == null) {
				newNode.setId(UUID.randomUUID().toString());
			}
			String sqlInsertNewNode = "insert into fish(`id`, `uri_name`, `name`, `images`, " +
					"`name_en`, `parent_id`, `owl`, `uri`) values(:id, :uriName, :name, :imagesStr, " +
					":englishName, :parentId, :owl, :uri)";
			SqlParameterSource paramInsertNewNode = new BeanPropertySqlParameterSource(newNode);
			if (template.update(sqlInsertNewNode, paramInsertNewNode) != 1) {
				throw new Exception("����ڵ�ʱʧ��@NodeService.addNode()"); // Rollback
			}
	
			// ��newNode��idֵ����parentNode��childNodes�б���
			logger.info("���¸��ڵ���ӽ���б�");
			parentNode.getRetrievalDataSource().getChildNodes().add(newNode.getId());
			// ����parentNode��owl�ַ���
			logger.info("���¸��ڵ��owl�ַ���");
			parentNode.setOwl(BizNode.getOWL(parentNode));
			
			// ��parentNode����д�����ݿ�
			// ����ֻ�޸���parentNode��owl��Ϣ����������ֻ����owl�ֶ�
			logger.info("�����ڵ�д�����ݿ�");
			String sqlUpdateParentNode = "update `fish` set `owl`=:owl where id=:id";
			SqlParameterSource paramUpdateParentNode = new BeanPropertySqlParameterSource(parentNode);
			if (template.update(sqlUpdateParentNode, paramUpdateParentNode) != 1) {
				throw new Exception("���¸���ʱʧ��@NodeService.addNode()"); // Rollback
			}
			
		} catch (Exception ex) {
			logger.error("NodeService.addNode()������������", ex);
			throw new RuntimeException("NodeService.addNode()������������", ex);
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
			logger.error("����VirtualNodeʧ�ܡ�");
			throw new Exception("����VirtualNodeʧ�ܡ�");
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
		if (result != 1) throw new RuntimeException("������ڵ�Ľ����Ϊ1");
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
		
			
//			// 2.����VirtualNode[id=Node.VIRTUAL_NODE_ID]��childNodes����
//			logger.debug("������ڵ�");
//			String vtrlNodeQuerySQL = "select `owl` from `fish` where `id`=?";
//			List<Node> vNodes = sess.query(vtrlNodeQuerySQL, new NodeRowMapper(), Node.VIRTUAL_NODE_ID);
//			
//			Node vNode = null;
//			if (vNodes == null) {
//				logger.error("��ѯVirtualNodeʱ����null��");
//				throw new Exception("��ѯVirtualNodeʱ����null��");
//			}
//			if (vNodes.size() == 0) {
//				logger.info("VirtualNode�����ڣ��ؽ�֮");
//				createVirtualNode(sess);
//			} else {
//				vNode = vNodes.get(0);
//			}
//			
//			vNode.getRetrievalDataSource().getChildNodes().add(rootNode.getId());
//	
//			// �ؽ�VirtualNode��owl�ֶ�
//			vNode.setOwl(Node.getOwlFromNode(vNode, sess));
//			logger.info("�����µ�owl�ַ�����" + vNode.getOwl());
//			
//			// ��VirtualNodeд�����ݿ�
//			logger.info("��ʼ��VirtualNodeд������...");
//			String update_vrtl_node_sql = "update `fish` set `owl`=:owl where `id`='virtual_node'";
//			SqlParameterSource vrtl_node_param = new BeanPropertySqlParameterSource(vNode);
//			sess.update(update_vrtl_node_sql, vrtl_node_param);
//			logger.info("VirtualNodeд�����ݿ�ɹ�");
	}

	// ����ֻ����������Ϣ�Ľڵ�
	public void addNodeBrief(Node newNode, Node parentNode, AttributeSelector as) {
//		try {
//			// ����parent��matrix����
//			logger.info("���¸��ڵ����������");
//			Matrix matrix = parentNode.getRetrievalDataSource().getMatrix();
//			//   ���޸��У���newNode��parentNode���������ԵĽ���ƥ��
//			//   ���matrixΪ��[����������������]���������������������˵������Ӷ�����
//			//   ������û����ӣ���Ϊû����֪����������ƥ��
//			int[] newRow = new int[matrix.getColSize()];
//			for(int i = 0; i < newRow.length; i++)
//				newRow[i] = as.getAttributeMapping().get(i) ? NodeAttribute.YES : NodeAttribute.NO;
//			matrix.addRow(newRow, 0, newRow.length);
//
//			//   ���޸��У���parentNode��Ӵ���newNodeʱһ����ӵ�������
//			//   ����������Ե�ͬʱ�������Լ���parentNode��attribute�б�
//			List<NodeAttribute> parentAttributes = parentNode.getRetrievalDataSource().getAttributes();
//			for(NodeAttribute attr : as.getNewAttributeMapping().keySet()) {
//				// matrix����Ϊ�գ���վ������1����Ҫ�ĳ���ʼ����1
//				int[] newCol = matrix.getRowSize() == 0 ? new int[1] : new int[matrix.getRowSize()];
//				for(int j = 0; j < newCol.length; j++) {
//					newCol[j] = (j != newCol.length - 1 ? 0 : 
//						(as.getNewAttributeMapping().get(attr) ? NodeAttribute.YES : NodeAttribute.NO));
//				}
//				matrix.addCol(newCol, 0, newCol.length);
//				// ͬʱ����parentNode��attribte�б�
//				parentAttributes.add(attr);
//			}
//			
//			// ����������newNode��owl�ַ���
////			log.info("�����������½ڵ��owl�ַ���");
////			newNode.setOwl(Node.getOwlFromNode(newNode, sqlclient));
//			
//			logger.info("���½ڵ�д�����ݿ�");
//			// ע�⣬brief�ڵ��IDֵ���ɿͻ����ṩ�ģ�����
//			// Ϊ�½ڵ�detailType����ֵ: brief
//			newNode.setDetailType(DetailType.BRIEF);
//			String sqlInsertNewNode = "insert into fish(`id`, `name`, `name_en`, `parent_id`, `contact`, `detail_type`) values(" +
//					                                    ":id, :name, :englishName, :parentId, :contact, :detailType)";
//			SqlParameterSource paramInsertNewNode = new BeanPropertySqlParameterSource(newNode) ;
//			if (template.update(sqlInsertNewNode, paramInsertNewNode) != 1) {
//				throw new Exception("����ڵ�ʱʧ��@NodeService.addNode()"); // Rollback
//			}
//	
//			// ��newNode��idֵ����parentNode��childNodes�б���
//			logger.info("���¸��ڵ���ӽ���б�");
//			parentNode.getRetrievalDataSource().getChildNodes().add(newNode.getId());
//			// ����parentNode��owl�ַ���
//			logger.info("���¸��ڵ��owl�ַ���");
//			parentNode.setOwl(BizNode.getOWL(parentNode));
//					
//			// ��parentNode����д�����ݿ�
//			// ����ֻ�޸���parentNode��owl��Ϣ����������ֻ����owl�ֶ�
//			logger.info("�����ڵ�д�����ݿ�");
//			String sqlUpdateParentNode = "update `fish` set `owl`=:owl where id=:id";
//			SqlParameterSource paramUpdateParentNode = new BeanPropertySqlParameterSource(parentNode);
//			if (template.update(sqlUpdateParentNode, paramUpdateParentNode) != 1) {
//				throw new Exception("���¸���ʱʧ��@NodeService.addNode()"); // Rollback
//			}
//			
//		} catch (Exception ex) {
//			logger.error("NodeService.addNode()������������", ex);
//			throw new RuntimeException("NodeService.addNode()������������", ex);
//		}
	}

	public static NodeDao getInstance() {
		return (NodeDao) Configuration.getBean("nodeDao");
	}
}
