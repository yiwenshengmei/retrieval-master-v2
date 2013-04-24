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
	 * 把Node对象中的图片流保存到文件系统并未需要持久化的Node中的图片字段赋值
	 * @param node
	 * @param savePath
	 * @throws IOException
	 */
	public static void preProcessImages(Node node, String savePath) throws IOException {
		File folder = new File(savePath);
		if(!folder.exists()) {
			folder.mkdirs();
			logger.debug("用于保存图片的文件夹不存在，开始创建: " + folder.getPath());
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
			// 建立RetrievalDataSource -> Node的关系
			rds.setNode(node);
			// 建立Attribute -> Node的关系
			for (NodeAttribute attr : node.getAttributes())
				attr.setNode(node);
			// 建立NodeImage -> Node的关系
			for (NodeImage img : node.getImages())
				img.setNode(node);
			// 建立NodeFeature -> RetrievalDataSource的关系
			for (NodeFeature feature : rds.getFeatures()) { 
				feature.setRetrievalDataSource(rds);
				// 建立FeatureImage -> NodeFeature的关系
				for (FeatureImage fimg : feature.getImages())
					fimg.setFeature(feature);
			}
			Matrix mtx = rds.getMatrix();
			// 建立Matrix -> RetrievalDataSource的关系
			mtx.setRetrievalDataSource(rds);
			// 建立MatrixRow -> Matrix的关系
			for (MatrixRow row : mtx.getRows()) {
				row.setMatrix(mtx);
				// 建立MatrixItem -> MatrixRow的关系
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
	 * 将child节点添加到parent节点中
	 * 同时更新parent节点的特征矩阵，包括：
	 * 1. 更新parent的childNodes字段
	 * 2. 根据child.getFeaturesOfParent值在parent节点的特征矩阵中添加新行
	 * 3. 更新parent节点的features字段，加入newFeatures
	 * 4. 更新parent节点的特征矩阵，加入newFeatures对应的列
	 * @param child
	 * @param parent
	 * @param newFeatures
	 */
	public static void addChildToParent(Node child, Node parent, List<NodeFeature> newFeatures) {
		// 更新父节点的子节点列表
		parent.getChildNodes().add(child);
		// 更新父节点的矩阵
		Matrix mtx = parent.getRetrievalDataSource().getMatrix();
		// 新建一行，其长度将等于父节点矩阵的列数
		MatrixRow newRow = new MatrixRow(mtx);
		// 从左至右依次填充新行，规则是父节点当前列所代表的Feature存在于node.getFeaturesOfParent中，则填Yes，否则填写No
		List<NodeFeature> parentFeatures = parent.getRetrievalDataSource().getFeatures();
		for(int i = 0; i < parentFeatures.size(); i++) {
			MatrixItem item = child.getFeaturesOfParent().contains(parentFeatures.get(i)) ? MatrixItem.Yes(newRow) : MatrixItem.No(newRow);
			newRow.addItem(item);
		}
		mtx.addRow(newRow);
		
		// 更新parent节点的特征矩阵，加入newFeatures对应的列
		addNewFeaturesToNode(parent, newFeatures);
	}
	
	/**
	 * 检测feature是否存在于features中，匹配原则是对比id
	 * @param features 被检测的目标集合
	 * @param feature 被检测的目标
	 * @return 存在则返回true，否则返回false
	 */
	private static boolean containsFeature(List<NodeFeature> features, NodeFeature feature) {
		for (NodeFeature f : features) {
			if (f.getId() == null)
				throw new IllegalArgumentException("用于比较的Feature的id不能为null！");
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
					throw new IllegalArgumentException("没有找到节点[id=" + id + "]");
				initialize(nd);
				nd.setOwl(BizOWL.createOwl(nd));
				return nd;
			}
		});
		return nd;
	}
	
	public static void changePath2Url(Node node) {
		String contextPath = ServletActionContext.getServletContext().getContextPath(); // 返回"/retrieval-master"
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
	 * 向指定的节点添加新特征，并同时更新该节点的特征矩阵（添加列）
	 * @param node
	 * @param newFeatures
	 */
	public static void addNewFeaturesToNode(Node node, List<NodeFeature> newFeatures) {
		if (newFeatures == null) return;
		RetrievalDataSource rds = node.getRetrievalDataSource();
		// 0. 设置newFeatures的所属
		for (NodeFeature f : newFeatures) 
			f.setRetrievalDataSource(rds);
		// 1. copy新特征到node中
		node.getRetrievalDataSource().getFeatures().addAll(newFeatures);
		
		// 2. 更新Matrix
		// 假设该节点矩阵中有3行，并需要添加4个新特征，则构造出4组[Unknow, Unknow, Unknow]这样的列添加到父节点矩阵中
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
			throw new IllegalArgumentException("要删除的特征并非都是该节点中的特征，所以不能删除。[nodeid=" + node.getId() + "]");
		
		// 0. 删除node.childNodes中的featuresOfParent
		for (Node child : node.getChildNodes()) {
			child.getFeaturesOfParent().removeAll(features);
		}
		
		// 1. 更新Matrix——删除feature对应的列
		for (NodeFeature feature : features) {
			int col = node.getFeatures().indexOf(feature);
			List<MatrixItem> items = node.getMatrix().removeCol(col);
		}
		
		// 2. 从feature集合中删除
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
