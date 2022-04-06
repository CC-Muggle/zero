package com.data4truth.dataStructure.index;


import java.util.Objects;

/**
 *
 * 红黑树实现方法
 *
 * 红黑树的每个节点上都有存储位表示节点的颜色，颜色是红(Red)或黑(Black)。
 * 红黑树的特性:
 * (1) 每个节点或者是黑色，或者是红色。
 * (2) 根节点是黑色。
 * (3) 每个叶子节点是黑色。 [注意：这里叶子节点，是指为空的叶子节点！]
 * (4) 如果一个节点是红色的，则它的子节点必须是黑色的。
 * (5) 从一个节点到该节点的子孙节点的所有路径上包含相同数目的黑节点。
 *
 * @author yangcj
 */
public class RedBlackTree<T extends Comparable<T>> {

    public static void main(String[] args) {

    }

    private volatile RedBlackTreeNode<T> root;

    public void insert(T value){
        RedBlackTreeNode<T> node = new RedBlackTreeNode<>(value);

        // 判断是否为根节点
        if(Objects.isNull(root)){
            synchronized (this){
                if(Objects.isNull(root)){
                    // 满足上述条件1，则附上处理
                    node.setBlack();
                    root = node;
                }
            }
        }else{
            
        }

    }

    public void delete(T value){

    }

    public RedBlackTreeNode<T> find(T value){
        return null;
    }

}

class RedBlackTreeNode<T extends Comparable<T>>{

    private T value;

    /**
     * 左侧元素，必定小于当前元素
     */
    private RedBlackTreeNode<T> leftNode;

    /**
     * 右侧元素必定大于当前元素
     */
    private RedBlackTreeNode<T> rightNode;

    /**
     * true为 红色 false为黑色
     */
    private boolean colorFlag;

    public RedBlackTreeNode(){

    }

    public RedBlackTreeNode(T value){
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public RedBlackTreeNode<T> getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(RedBlackTreeNode<T> leftNode) {
        this.leftNode = leftNode;
    }

    public RedBlackTreeNode<T> getRightNode() {
        return rightNode;
    }

    public void setRightNode(RedBlackTreeNode<T> rightNode) {
        this.rightNode = rightNode;
    }

    public boolean isColorFlag() {
        return colorFlag;
    }

    public void setRed() {
        this.colorFlag = true;
    }

    public void setBlack() {
        this.colorFlag = false;
    }
}
