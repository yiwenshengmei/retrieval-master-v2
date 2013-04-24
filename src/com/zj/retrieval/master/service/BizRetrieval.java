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
		// 初始化的时matchRows中所有的值就是matrix中所有的行号
		if (hasInited)
			return;
		if (mappingRows == null)
			mappingRows = new ArrayList<Integer>();
		// 因为NodeRetrieval是单例，所以每次都要先清空状态
		mappingRows.clear();
		for (int i = 0; i < node.getChildNodes().size(); i++) {
			mappingRows.add(i);
		}
		hasInited = true;
	}
	
	public BizRetrieval() {}
	
	public RetrievalResult retrieval(List<Integer> selectState) {
		// selectState的第 代表retrievalNode的id，从下标1开始才是answer
		for (int i = 1; i < selectState.size(); i++) {
			int perAnswer = selectState.get(i);
			NodeFeature perFeature = node.getRetrievalDataSource().getFeatures().get(i - 1);
			logger.debug(String.format("%1$s => %2$s", perFeature.getName(), NodeFeature.textValue(perAnswer)));
			// 如果回答unknown，轮空本次，这可能将导致答案出现多个
			if (perAnswer == NodeFeature.UNKNOW)
				continue;
			Matrix matrix = node.getRetrievalDataSource().getMatrix();
			Iterator<Integer> iter = mappingRows.iterator();
			// i对应于selectState中对第i个特征的回答，同时对应于matrix中第i-1
			List<Integer> compareFeaureCol = matrix.getCol(i - 1);
			while (iter.hasNext()) {
				if (compareFeaureCol.get(iter.next()) == perAnswer)
					continue;
				else
					iter.remove();
			}
		}
		// 这里判断该返回什么
		RetrievalResult result = new RetrievalResult();
		if ((selectState.size() - 1) >= node.getRetrievalDataSource().getFeatures().size()
				|| mappingRows.size() <= 1) {
			// 1. 自然结束的情况，即所有特征都已询问过
			// 2. 可能还没有询问过全部的特征，但是已经确定没有匹配的子结点
			// 3. 匹配的子结点列表中只剩下元素
			result.hasResult(true);
			result.setResult(getMatchedNode());
		} else {
			// 还没有完成，返回下一个询问的特征
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
