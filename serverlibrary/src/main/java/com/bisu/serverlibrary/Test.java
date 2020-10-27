package com.bisu.serverlibrary;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;

public class Test {

    static int[] array = new int[]{3,2,1,5,6,4};

    public static void main(String[] args) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });

//        quickSort(array,0,5);

//        reverseList();
//        ArrayList<ArrayList<Integer>> arrayList = new ArrayList<>();

//        permute(array);

       // 从上往下打印二叉树
//        TreeNode node = new TreeNode(0);
//        node.left = new TreeNode(1);
//        node.right = new TreeNode(2);
//        PrintFromTopToBottom(node);

        //解析elf
//        ReadElf.readelf();

        //线程池
//        ThreadPool.excuteCached();
//        ThreadPool.excuteFixed();
//        ThreadPool.excuteScheduled();
//        ThreadPool.excuteSingle();
        xor();
    }

    private static void xor() {
        int[] command3=new  int[]{0xA1,0xA2,0xA3,0xA4,/*STX 4byte*/
                0x04,0x00,/*size 2byte*/
                0x25,/*CMD 1byte*/
                0xBB,0xBB,0x38};
                int a  = 0xA1;
        for(int i = 1; i< command3.length-1; i++){
            a = a ^ command3[i];
        }
        System.out.println(a);
    }


    private static void quickSort(int[] arr, int low, int high) {

        if (low < high) {
            // 找寻基准数据的正确索引
            int index = getIndex(arr, low, high);

            // 进行迭代对index之前和之后的数组进行相同的操作使整个数组变成有序
            //quickSort(arr, 0, index - 1); 之前的版本，这种姿势有很大的性能问题，谢谢大家的建议
            quickSort(arr, low, index - 1);
            quickSort(arr, index + 1, high);
        }

    }

    private static int getIndex(int[] arr, int low, int high) {
        // 基准数据
        int tmp = arr[low];
        while (low < high) {
            // 当队尾的元素大于等于基准数据时,向前挪动high指针
            while (low < high && arr[high] >= tmp) {
                high--;
            }
            // 如果队尾元素小于tmp了,需要将其赋值给low
            arr[low] = arr[high];
            // 当队首元素小于等于tmp时,向前挪动low指针
            while (low < high && arr[low] <= tmp) {
                low++;
            }
            // 当队首元素大于tmp时,需要将其赋值给high
            arr[high] = arr[low];

        }
        // 跳出循环时low和high相等,此时的low或high就是tmp的正确索引位置
        // 由原理部分可以很清楚的知道low位置的值并不是tmp,所以需要将tmp赋值给arr[low]
        arr[low] = tmp;
        return low; // 返回tmp的正确位置
    }

    private static void reverseList() {
        System.out.println("单向链表反转 \n******************************");
        Node head = initList();
        System.out.println(head);

        Node p = head;
        Node q = head.next;
        Node temp = null;
        p.next = temp;
        while (q != null) {
            temp = q.next;
            q.next = p;
            p = q;
            q = temp;
        }
        System.out.println(p);

    }

    private static Node initList() {
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);
        Node n4 = new Node(4);
        Node n5 = new Node(5);
        Node n6 = new Node(6);
        Node n7 = new Node(7);

        n1.next = n2;
        n2.next = n3;
        n3.next = n4;
        n4.next = n5;
        n5.next = n6;
        n6.next = n7;

        return n1;
    }


    static class Node {

        Node next;
        int value;

        Node(int value) {
            this.value = value;
        }

        Node() {

        }

        @NonNull
        @Override
        public String toString() {
            return "value = " + value + "  " + (next == null ? "" : next.toString());
        }
    }


    /**
     * 快速排序 选择一个值分成左小右大，再分别左右两个数组
     * 关键点：1、选一个比较值，再从数组左右两端比较大小，左大右小就交换左右的位置数据
     * 2、while 循环内先判断是否可以交换，再进行下标修改，下标修改三个判断，先右--再左++，再交换
     *
     * @param arg
     * @param s
     * @param e
     */
    public static void speedSort(int[] arg, int s, int e) {
        System.out.println("index s =" + s + " e=" + e);
        System.out.println("");
        print(array);
        if (s >= e) {
            return;
        }
        int temp = arg[s];
        int start = s;
        int end = e;
        while (start < end) {


            if (arg[end] >= temp) {
                end--;
            }

            if (arg[start] < temp && start < end) {
                start++;
            }

            if (arg[start] >= temp && arg[end] < temp) {
                System.out.println(" \n\t  交换下标  start =" + start + " end=" + end);
                int t = arg[start];
                arg[start] = arg[end];
                arg[end] = t;
            }

            System.out.println("");
            print(array);

        }
        speedSort(arg, s, start);
        speedSort(arg, start + 1, e);
    }

    private static void print(int[] args) {
        for (int i : args) {
            System.out.print(i);
            System.out.print(",");
        }
    }


//    public static List<List<Integer>> permute(int[] nums) {
//
//        ArrayList<List<Integer>> arrayList = new ArrayList<>();
//        arrayList.remo
//        LinkedList<String> strings;
//        strings.addLast();
//        strings.removeFirst();
//        for (int i = 0; i < nums.length; i++) {
//            for (int j = 0; j < nums.length; j++) {
//
//            }
//        }
//        System.out.println(arrayList);
//
//        char[] chars = new char[2];
//        chars[0] = 12;
//        chars[1] = 12;
//        return arrayList;
//    }

    public static class TreeNode {
        int val = 0;
        TreeNode left = null;
        TreeNode right = null;

        public TreeNode(int val) {
            this.val = val;

        }

    }

    public static ArrayList<Integer> PrintFromTopToBottom(TreeNode root) {

        if(root == null){
            return null;
        }
        ArrayList<TreeNode> l1 = new ArrayList();
        ArrayList<TreeNode> l2 = new ArrayList();
        ArrayList<Integer> l3 = new ArrayList();
        l1.remove(0);
        l1.add(root);
        while(l1.size() != 0){
            TreeNode node = l1.remove(l1.size()-1);
            l3.add(node.val);
            l2.add(node.left);
            l2.add(node.right);
            if(l1.size() == 0){
                l1.addAll(l2);
            }
        }
        return l3;


    }
}
