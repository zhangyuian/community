package com.zhangyu.community.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: zhang
 * @date: 2022/3/27
 * @description:
 */

@Component
public class SensitiveFilter {

    private TrieNode rootNode = new TrieNode();

    // 敏感词替换标志
    private String REPLACEMENT = "***";

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    @PostConstruct
    private void init() {
        try (
                InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
        ) {
            String keyword;
            while((keyword = reader.readLine()) != null){
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词失败：" + e.getMessage());
        }
    }

    private class TrieNode {

        private boolean isKeywordEnd = false;

        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        //添加子节点
        public void addSubNode(char ch, TrieNode node) {
            subNodes.put(ch, node);
        }

        //获取子节点
        public TrieNode getSubNode(char ch) {
            return subNodes.get(ch);
        }
    }

    public void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char ch = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(ch);
            if (subNode == null) {
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(ch, subNode);
            }
            // 指向子节点，进入下一轮循环
            tempNode = subNode;

            // 设置结束标志
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        TrieNode point1 = rootNode;
        int point2 = 0;
        int point3 = 0;
        // 为了防止fabcd和abc为敏感词的，检索fabc会漏判abc的情况，以point2为循环结束标志
        while (point2 < text.length()) {
            if (point3 < text.length()) {
                char ch = text.charAt(point3);

                //判断是否为特殊符号
                if (isSymbol(ch)) {
                    if (point1 == rootNode) {
                        point2++;
                        sb.append(ch);
                    }
                    point3++;
                    continue;
                }

                // 检查下级节点
                point1 = point1.getSubNode(ch);
                if (point1 == null) {
                    //以point2指向的字符不是敏感词
                    sb.append(text.charAt(point2));
                    point2++;
                    point3 = point2;
                    point1 = rootNode;
                } else if (point1.isKeywordEnd()) {
                    //发现敏感词
                    sb.append(REPLACEMENT);
                    point3++;
                    point2 = point3;
                } else {
                    point3++;
                }
            } else {
                sb.append(text, point2, point3);
                point3++;
                point1 = rootNode;
            }
        }
        return sb.toString();
    }

    private boolean isSymbol(char ch) {
        if (!CharUtils.isAsciiAlphanumeric(ch) && (ch < 0x2E80 || ch > 0x9FFF))
            return true;
        return false;
    }

    //测试用
//    public static void main(String[] args) {
//        SensitiveFilter sensitiveFilter = new SensitiveFilter();
//        sensitiveFilter.addKeyword("abc");
//        String s = "ab$%^&**#cde";
//        String filter = sensitiveFilter.filter(s);
//        System.out.println(filter);
//    }
}
