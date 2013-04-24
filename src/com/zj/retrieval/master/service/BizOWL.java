package com.zj.retrieval.master.service;

import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamesmurty.utils.XMLBuilder;
import com.zj.retrieval.master.Utils;
import com.zj.retrieval.master.entity.FeatureImage;
import com.zj.retrieval.master.entity.NodeAttribute;
import com.zj.retrieval.master.entity.NodeFeature;
import com.zj.retrieval.master.entity.NodeImage;

public class BizOWL {
	
	private final static Logger logger = LoggerFactory.getLogger(BizOWL.class);
	
	public static String createOwl(Node node) throws Exception {

		XMLBuilder xml = XMLBuilder.create("rdf:RDF");
		
		xml.a("xmlns:rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		xml.a("xmlns:owl", "http://www.w3.org/2002/07/owl#");
		xml.a("xmlns:rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		xml.a("xmlns:xsd", "http://www.w3.org/2001/XMLSchema#");
		
		createClassTypeXML(node, xml);

		String xmlStr = xml.asString();
		logger.debug(xmlStr);
		return xmlStr;
	}
	
	private static void createClassTypeXML(Node node, XMLBuilder xml) throws Exception {

		// Create <owl:Class>
		String parentURI = StringUtils.EMPTY;
		if (node.getParentNode() != null)
			parentURI = (node.getParentNode().getId() == Node.VIRTUAL_NODE_ID ? StringUtils.EMPTY :	Utils.null2Empty(node.getParentNode().getUri()));
		String rdfId = Utils.null2Empty(node.getUri()) + "#" + Utils.null2Empty(node.getEnglishName());
		XMLBuilder elemClass = xml.e("owl:Class").a("rdf:ID", rdfId);
		elemClass.e("rdfs:subClassOf").a("rdf:resource", Utils.null2Empty(parentURI));
		elemClass.e("rdfs:label").t(Utils.null2Empty(node.getLabel()));
		
		// Create <desc>
		elemClass.e("desc").t(Utils.null2Empty(node.getDesc()));
		
		// Create <images>
		XMLBuilder eImages = elemClass.e("images");
		for (NodeImage nodeImage : node.getImages()) {
			eImages.e("item").t(nodeImage.getPath() == null ? StringUtils.EMPTY : FilenameUtils.getName(nodeImage.getPath()));
		}
		
		// Create <attributes>
		XMLBuilder elemUserfields = elemClass.e("attributes");
		for (NodeAttribute attr : node.getAttributes()) {
			elemUserfields.e("attribute").a("key", Utils.null2Empty(attr.getKey())).t(Utils.null2Empty(attr.getValue()));
			
		}

		// Create <features>
		List<NodeFeature> features = node.getRetrievalDataSource().getFeatures();
		XMLBuilder elemAttributes = elemClass.e("features");
		for(int index = 0; index < features.size(); index ++) {
			
			XMLBuilder elemAttribute = elemAttributes.e("feature");
			elemAttribute.a("name", Utils.null2Empty(features.get(index).getName()))
				         .a("english_name", Utils.null2Empty(features.get(index).getEnglishName()))
				         .a("index", String.valueOf(index))
					     	.e("desc").t(Utils.null2Empty(features.get(index).getDesc())).up();
			
			XMLBuilder featureImages = elemAttribute.e("images");
			for (FeatureImage featureImage : features.get(index).getImages()) {
				featureImages.e("image").a("path", featureImage.getPath() == null ? StringUtils.EMPTY : FilenameUtils.getName(featureImage.getPath()));
			}
		}

		// Create <matrix>
		XMLBuilder elemMatrix = elemClass.e("matrix");
		Matrix matrix = node.getRetrievalDataSource().getMatrix();
		for(int rowindex = 0; rowindex < matrix.getRowSize(); rowindex++) {
			elemMatrix.e("row").a("index", String.valueOf(rowindex))
				.t(StringUtils.join(matrix.getRow(rowindex).getValueList(), " "));
		}
		
		// Create <childNodes>
		XMLBuilder elemChildNodes = elemClass.e("child_nodes");
		List<Node> childNodes = node.getChildNodes();
		for(int index = 0; index < childNodes.size(); index++) {
			elemChildNodes.e("node").a("index", String.valueOf(index)).t(Utils.null2Empty(childNodes.get(index).getName()));
		}
	
	}
	
	private static void createIndividualTypeXML(Node node, XMLBuilder xml) throws Exception {

		String parentEnName = StringUtils.EMPTY;
		if (node.getParentNode() != null)
			Utils.null2Empty(parentEnName = node.getParentNode().getEnglishName());
		XMLBuilder nodeXml = xml.e(parentEnName);
		nodeXml.a("rdf:ID", Utils.null2Empty(node.getUri()) + "#" + Utils.null2Empty(node.getEnglishName()))
			.e("label").t(Utils.null2Empty(node.getLabel())).up()
			.e("name").t(Utils.null2Empty(node.getName())).up()
			.e("desc").t(Utils.null2Empty(node.getDesc()));

		// Create <images>
		XMLBuilder eImages = nodeXml.e("images");
		for (NodeImage nodeImage : node.getImages()) {
			eImages.e("item").t(Utils.null2Empty(nodeImage.getPath()));
		}
		
		// Create <attributes>
		XMLBuilder elemUserfields = nodeXml.e("attributes");
		for (NodeAttribute attr : node.getAttributes()) {
			elemUserfields.e("attribute").a("key", Utils.null2Empty(attr.getKey())).t(Utils.null2Empty(attr.getValue()));
		}
	}
}
