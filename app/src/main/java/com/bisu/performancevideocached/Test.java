package com.bisu.performancevideocached;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

public class Test {

    static int[] array = new int[]{5, 4, 7, 9, 1, 3, 2, 6, 8, 0, 15, 19, 11, 10};

    public static void main(String[] args) {

//        quickSort();

//        reverseList();


    }

    private static void quickSort() {
        System.out.println("快速排序 \n******************************");
        print(array);
        speedSort(array, 0, array.length - 1);
        System.out.println("");
        print(array);
        System.out.println("\n******************************");

    }

    private static void reverseList() {
        System.out.println("单向链表反转 \n******************************");
        Node head = initList();
        System.out.println(head);

        Node p = head;
        Node q = head.next;
        Node temp = null;
        p.next = temp;
        while (q != null){
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
}
