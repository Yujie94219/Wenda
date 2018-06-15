package com.wenda.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/*
 *@ClassName:SensitiveService
 *@Description:采用字典树（前缀树）
 */
@Service
public class SensitiveService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        rootNode = new TrieNode();

        try {
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                lineTxt = lineTxt.trim();
                addWord(lineTxt);
            }
            read.close();
        } catch (Exception e) {
            logger.error("读取敏感词文件失败" + e.getMessage());
        }
    }

    private class TrieNode {
        //是不是关键词的结尾
        private boolean end = false;
        //当前节点下所有的子节点
        private Map<Character, TrieNode> subNodes = new HashMap<Character, TrieNode>();

        public void addSubNode(Character key, TrieNode node) {
            subNodes.put(key, node);
        }

        //获取下一个节点
        TrieNode getSubNode(Character key) {
            return subNodes.get(key);
        }

        boolean isKeyWordEnd() {
            return end;
        }

        void setkeywordEnd(boolean end) {
            this.end = end;
        }

        public int getSubNodeCount() {
            return subNodes.size();
        }
    }

    //确定根节点
    private TrieNode rootNode = new TrieNode();

    private boolean isSymbol(char c) {
        int ic = (int) c;
        //东亚文字0x2E80-0x9FFF
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    //过滤
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }

        StringBuilder res = new StringBuilder();

        String replacement = "***";
        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;

        while (position < text.length()) {
            char c = text.charAt(position);

            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    res.append(c);
                    ++begin;
                }

                ++position;
                continue;
            }

            tempNode = tempNode.getSubNode(c);

            //没有当前位字符的敏感词，起始位指针与当前位指针均从下一位开始重新查找
            if (tempNode == null) {
                res.append(text.charAt(begin));
                position = begin + 1;
                begin = position;
                tempNode = rootNode;
            } else if (tempNode.isKeyWordEnd()) {
                //当前位属于敏感词中，且已找到末尾
                res.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            } else {
                //当前位属于敏感词中，且未到末尾
                ++position;
            }
        }
        res.append(text.substring(begin));
        return res.toString();
    }


    //增加关键词
    private void addWord(String lineTxt) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < lineTxt.length(); ++i) {
            Character c = lineTxt.charAt(i);
            if (isSymbol(c)) {
                continue;
            }

            TrieNode node = tempNode.getSubNode(c);

            if (node == null) {     //没初始化
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }

            tempNode = node;

            if (i == lineTxt.length() - 1) {
                //关键词结束，设置结束标志
                tempNode.setkeywordEnd(true);
            }
        }
    }
}
