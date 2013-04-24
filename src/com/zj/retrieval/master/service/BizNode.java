package com.zj.retrieval.master.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.ServletActionContext;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zj.retrieval.master.AttributeSelector;
import com.zj.retrieval.master.DALService;
import com.zj.retrieval.master.IDALAction;
import com.zj.retrieval.master.entity.FeatureImage;
import com.zj.retrieval.master.entity.MatrixItem;
import com.zj.retrieval.master.entity.MatrixRow;
import com.zj.retrieval.master.entity.NodeAttribute;
import com.zj.retrieval.master.entity.NodeFeature;
import com.zj.retrieval.master.entity.NodeImage;
import com.zj.retrieval.master.entity.RetrievalDataSource;

public class BizNode {
	
	private static final Logger logger = LoggerFactory.getLogger(BizNode.class);
	
	private static List<String> saveImageFiles(File[] files, String[] fileNames, File folder, String msg) throws IOException {
		List<String> paths = new ArrayList<String>();
		if (files == null) return paths;
		for (int i = 0; i < files.length; i++) {
			File destFile = new File(folder, UUID.randomUUID().toString() + ".jpg");
			FileUtils.copyFile(files[i], destFile);
			logger.debug(String.format("%1$s, %2$s -> %3$s", msg, fileNames[i], destFile));
			paths.add(destFile.getPath());
		}
		return paths;
	}
	
	/**
	 * ��Node�����е�ͼƬ�����浽�ļ�ϵͳ��δ��Ҫ�־û���Node�е�ͼƬ�ֶθ�ֵ
	 * @param node
	 * @param savePath
	 * @throws IOException
	 */
	public static void preProcessImages(Node node, String savePath) throws IOException {
		File folder = new File(savePath);
		if(!folder.exists()) {
			folder.mkdirs();
			logger.debug("���ڱ���ͼƬ���ļ��в����ڣ���ʼ����: " + folder.getPath());
		}
		
		List<String> paths = saveImageFiles(node.getImageFiles(), node.getImageFilesFileName(), folder, "Save a NodeImage");
		node.setImages(NodeImage.batchCreate(paths, node));
		
		if (node.getRetrievalDataSource() != null && node.getRetrievalDataSource().getFeatures() != null) {
			for (NodeFeature feature : node.getRetrievalDataSource().getFeatures()) {
				List<String> featureImagePaths = saveImageFiles(feature.getImageFiles(), 
						feature.getImageFilesFileName(), folder, "Save a NodeFeatureImage");
				feature.setImages(FeatureImage.batchCreate(featureImagePaths, feature));
			}
		}
	}
	
	public static void buildRelation(Node... nodes) {
		for (Node node : nodes) {
			RetrievalDataSource rds = node.getRetrievalDataSource();
			// ����RetrievalDataSource -> Node�Ĺ�ϵ
			rds.setNode(node);
			// ����Attribute -> Node�Ĺ�ϵ
			for (NodeAttribute attr : node.getAttributes())
				attr.setNode(node);
			// ����NodeImage -> Node�Ĺ�ϵ
			for (NodeImage img : node.getImages())
				img.setNode(node);
			// ����NodeFeature -> RetrievalDataSource�Ĺ�ϵ
			for (NodeFeature feature : rds.getFeatures()) { 
				feature.setRetrievalDataSource(rds);
				// ����FeatureImage -> NodeFeature�Ĺ�ϵ
				for (FeatureImage fimg : feature.getImages())
					fimg.setFeature(feature);
			}
			Matrix mtx = rds.getMatrix();
			// ����Matrix -> RetrievalDataSource�Ĺ�ϵ
			mtx.setRetrievalDataSource(rds);
			// ����MatrixRow -> Matrix�Ĺ�ϵ
			for (MatrixRow row : mtx.getRows()) {
				row.setMatrix(mtx);
				// ����MatrixItem -> MatrixRow�Ĺ�ϵ
				for (MatrixItem item : row.getItems()) 
					item.setRow(row);
			}
		}
	}
	
	public static AttributeSelector getAttributeSelector(Node nd) {
		List<Integer> resultData = new ArrayList<Integer>();
		List<NodeFeature> attrs = nd.getRetrievalDataSource().getFeatures();
		for (int i = 0; i < attrs.size(); i++) {
			resultData.add(i);
		}
		return new AttributeSelector(resultData);
	}
	
	/**
	 * ��child�ڵ���ӵ�parent�ڵ���
	 * ͬʱ����parent�ڵ���������󣬰�����
	 * 1. ����parent��childNodes�ֶ�
	 * 2. ����child.getFeaturesOfParentֵ��parent�ڵ�������������������
	 * 3. ����parent�ڵ��features�ֶΣ�����newFeatures
	 * 4. ����parent�ڵ���������󣬼���newFeatures��Ӧ����
	 * @param child
	 * @param parent
	 * @param newFeatures
	 */
	public static void addChildToParent(Node child, Node parent, List<NodeFeature> newFeatures) {
		// ���¸��ڵ���ӽڵ��б�
		parent.getChildNodes().add(child);
		// ���¸��ڵ�ľ���
		Matrix mtx = parent.getRetrievalDataSource().getMatrix();
		// �½�һ�У��䳤�Ƚ����ڸ��ڵ���������
		MatrixRow newRow = new MatrixRow(mtx);
		// ������������������У������Ǹ��ڵ㵱ǰ���������Feature������node.getFeaturesOfParent�У�����Yes��������дNo
		List<NodeFeature> parentFeatures = parent.getRetrievalDataSource().getFeatures();
		for(int i = 0; i < parentFeatures.size(); i++) {
			MatrixItem item = child.getFeaturesOfParent().contains(parentFeatures.get(i)) ? MatrixItem.Yes(newRow) : MatrixItem.No(newRow);
			newRow.addItem(item);
		}
		mtx.addRow(newRow);
		
		// ����parent�ڵ���������󣬼���newFeatures��Ӧ����
		addNewFeaturesToNode(parent, newFeatures);
	}
	
	/**
	 * ���feature�Ƿ������features�У�ƥ��ԭ���ǶԱ�id
	 * @param features ������Ŀ�꼯��
	 * @param feature ������Ŀ��
	 * @return �����򷵻�true�����򷵻�false
	 */
	private static boolean containsFeature(List<NodeFeature> features, NodeFeature feature) {
		for (NodeFeature f : features) {
			if (f.getId() == null)
				throw new IllegalArgumentException("���ڱȽϵ�Feature��id����Ϊnull��");
			if (f.getId().equals(feature.getId()))
				return true;
		}
		return false;
	}
	
	private static void initialize(Node node) {
		Hibernate.initialize(node.getChildNodes());
		Hibernate.initialize(node.getImages());
		Hibernate.initialize(node.getParentNode());
		Hibernate.initialize(node.getAttributes());
		RetrievalDataSource rds = node.getRetrievalDataSource();
		Hibernate.initialize(rds.getMatrix());
		for (NodeFeature feature : rds.getFeatures()) {
			Hibernate.initialize(feature.getImages());
		}
		for (MatrixRow row : rds.getMatrix().getRows()) {
			Hibernate.initialize(row.getItems());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Map> getParentNodes() throws Exception {
		List<Map> nodes = (List<Map>) DALService.doAction(new IDALAction() {
			@Override
			public Object doAction(Session sess, Transaction tx) throws Exception {
				StringBuilder sql = new StringBuilder()
				.append("select nd.id, nd.name, nd.desc")
				.append(" from t_node nd");
				return sess.createSQLQuery(sql.toString())
						.addScalar("desc", StandardBasicTypes.STRING)
						.addScalar("name", StandardBasicTypes.STRING)
						.addScalar("id", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
			}
		});
		
		return nodes;
	}
	
	public static Node getNode(final String id) throws Exception {
		Node nd = (Node) DALService.doAction(new IDALAction() {
			@Override
			public Object doAction(Session sess, Transaction tx) throws Exception {
				Node nd = (Node) sess.get(Node.class, id);
				if (nd == null)
					throw new IllegalArgumentException("û���ҵ��ڵ�[id=" + id + "]");
				initialize(nd);
				nd.setOwl(BizOWL.createOwl(nd));
				return nd;
			}
		});
		return nd;
	}
	
	public static void changePath2Url(Node node) {
		String contextPath = ServletActionContext.getServletContext().getContextPath(); // ����"/retrieval-master"
		for (NodeImage img : node.getImages()) {
			img.setUrl(contextPath + "/images/" + FilenameUtils.getName(img.getPath()));
		}
		RetrievalDataSource rds = node.getRetrievalDataSource();
		for (NodeFeature feature : rds.getFeatures()) {
			for (FeatureImage img : feature.getImages())
				img.setUrl(contextPath + "/images/" + FilenameUtils.getName(img.getPath()));
		}
	}

	/**
	 * ��ָ���Ľڵ��������������ͬʱ���¸ýڵ��������������У�
	 * @param node
	 * @param newFeatures
	 */
	public static void addNewFeaturesToNode(Node node, List<NodeFeature> newFeatures) {
		if (newFeatures == null) return;
		RetrievalDataSource rds = node.getRetrievalDataSource();
		// 0. ����newFeatures������
		for (NodeFeature f : newFeatures) 
			f.setRetrievalDataSource(rds);
		// 1. copy��������node��
		node.getRetrievalDataSource().getFeatures().addAll(newFeatures);
		
		// 2. ����Matrix
		// ����ýڵ��������3�У�����Ҫ���4���������������4��[Unknow, Unknow, Unknow]����������ӵ����ڵ������
		Matrix mtx = node.getRetrievalDataSource().getMatrix();
		for (int i = 0; i < newFeatures.size(); i++) {
			int rowSize = mtx.getRowSize();
			if (rowSize == 0) break;
			List<MatrixItem> newCol = new ArrayList<MatrixItem>();
			for (int j = 0; j < rowSize; j++) {
				newCol.add( MatrixItem.Unknow(null) );
			}
			mtx.addCol(newCol);
		}
	}
	
	public static void deleteFeatureFromNode(Node node, List<NodeFeature> features) {
		if (!checkNodeContainsFeatures(node, features))
			throw new IllegalArgumentException("Ҫɾ�����������Ƕ��Ǹýڵ��е����������Բ���ɾ����[nodeid=" + node.getId() + "]");
		
		// 0. ɾ��node.childNodes�е�featuresOfParent
		for (Node child : node.getChildNodes()) {
			child.getFeaturesOfParent().removeAll(features);
		}
		
		// 1. ����Matrix����ɾ��feature��Ӧ����
		for (NodeFeature feature : features) {
			int col = node.getFeatures().indexOf(feature);
			List<MatrixItem> items = node.getMatrix().removeCol(col);
		}
		
		// 2. ��feature������ɾ��
		for (NodeFeature feature : features) {
			node.getFeatures().remove(feature);
		}
	}
	
	private static boolean checkNodeContainsFeatures(Node node, List<NodeFeature> features) {
		for (NodeFeature feature : features) {
			if (!containsFeature(node.getFeatures(), feature))
				return false;
		}
		return true;
	}
	
	public static Node getNodeByName(String name, Session sess) {
		return (Node) sess.createQuery("from Node nd where nd.name = :name").setString("name", name).uniqueResult();
	}
}
