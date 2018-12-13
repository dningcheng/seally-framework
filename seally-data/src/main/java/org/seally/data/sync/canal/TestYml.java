package org.seally.data.sync.canal;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

/**
*
*@author dnc
*@version 创建时间：2018年12月13日 下午4:41:45 
*
*
*/
public class TestYml {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 String yamlStr = "key:    hello yaml";
		    Yaml yaml = new Yaml();
		    Object ret = yaml.load(yamlStr);
		    
		    System.out.println(ret);
	}
	
}
