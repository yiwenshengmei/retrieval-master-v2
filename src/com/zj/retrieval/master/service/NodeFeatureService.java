package com.zj.retrieval.master.service;

import com.zj.retrieval.master.entity.NodeFeature;

public class NodeFeatureService {
	public static String textValue(int value) {
		switch(value) {
			case NodeFeature.YES : return "Yes";
			case NodeFeature.NO : return "No";
			case NodeFeature.UNKNOW : return "Unknow";
			default : return "Unknow Feature Answer Value";
		}
	}
	
	public static String shotTextValue(int value) {
		switch(value) {
			case NodeFeature.YES : return "¡Ì";
			case NodeFeature.NO : return "¡Á";
			case NodeFeature.UNKNOW : return "-";
			default : return "Unknow Feature Answer Value";
		}
	}
}
