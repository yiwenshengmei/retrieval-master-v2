package com.zj.retrieval.master.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zj.retrieval.master.RetrievalResult;
import com.zj.retrieval.master.entity.NodeFeature;

public class BizRetrieval {
	private Node node;
	private List<Integer> mappingRows;
	private boolean hasInited = false;
	private final static Logger logger = LoggerFactory.getLogger(BizRetrieval.class);
	
	public BizRetrieval(Node retrievalNode) {
		this.node = retrievalNode;
		this.mappingRows = new ArrayList<Integer>();
		initMappingRows();
	}
	
	private void initMappingRows() {
		// ��ʼ����ʱmatchRows�����е�ֵ����matrix�����е��к�
		if (hasInited)
			return;
		if (mappingRows == null)
			mappingRows = new ArrayList<Integer>();
		// ��ΪNodeRetrieval�ǵ���������ÿ�ζ�Ҫ�����״̬
		mappingRows.clear();
		for (int i = 0; i < node.getChildNodes().size(); i++) {
			mappingRows.add(i);
		}
		hasInited = true;
	}
	
	public BizRetrieval() {}
	
	public RetrievalResult retrieval(List<Integer> selectState) {
		// selectState�ĵ� ����retrievalNode��id�����±�1��ʼ����answer
		for (int i = 1; i < selectState.size(); i++) {
			int perAnswer = selectState.get(i);
			NodeFeature perFeature = node.getRetrievalDataSource().getFeatures().get(i - 1);
			logger.debug(String.format("%1$s => %2$s", perFeature.getName(), NodeFeature.textValue(perAnswer)));
			// ����ش�unknown���ֿձ��Σ�����ܽ����´𰸳��ֶ��
			if (perAnswer == NodeFeature.UNKNOW)
				continue;
			Matrix matrix = node.getRetrievalDataSource().getMatrix();
			Iterator<Integer> iter = mappingRows.iterator();
			// i��Ӧ��selectState�жԵ�i�������Ļش�ͬʱ��Ӧ��matrix�е�i-1
			List<Integer> compareFeaureCol = matrix.getCol(i - 1);
			while (iter.hasNext()) {
				if (compareFeaureCol.get(iter.next()) == perAnswer)
					continue;
				else
					iter.remove();
			}
		}
		// �����жϸ÷���ʲô
		RetrievalResult result = new RetrievalResult();
		if ((selectState.size() - 1) >= node.getRetrievalDataSource().getFeatures().size()
				|| mappingRows.size() <= 1) {
			// 1. ��Ȼ�������������������������ѯ�ʹ�
			// 2. ���ܻ�û��ѯ�ʹ�ȫ���������������Ѿ�ȷ��û��ƥ����ӽ��
			// 3. ƥ����ӽ���б���ֻʣ��Ԫ��
			result.hasResult(true);
			result.setResult(getMatchedNode());
		} else {
			// ��û����ɣ�������һ��ѯ�ʵ�����
			result.hasResult(false);
			int nextAttributeId = selectState.size() - 1;
			result.setNextFeature(node.getRetrievalDataSource().getFeatures().get(nextAttributeId));
		}
		result.setLastState(selectState);
		return result;
	}
	
	public void setRetrievalNode(Node retrievalNode) {
		this.node = retrievalNode;
		initMappingRows();
	}

	private List<Node> getMatchedNode() {
		List<Node> mappedNodes = new ArrayList<Node>();
		for (int mappedRow : mappingRows) {
			mappedNodes.add(node.getChildNodes().get(mappedRow));
		}
		return mappedNodes;
	}
}
