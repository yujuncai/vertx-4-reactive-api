package org.limadelrey.vertx4.reactive.rest.api.utils;

import org.limadelrey.vertx4.reactive.rest.api.message.KeyVo;

import java.util.*;


//堆排序和获取topK排名、获取leastK排名
public class HeapSortUtils {

    public static void heapSort(KeyVo[] arr) {
        int n = arr.length;

        // 构建最大堆
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        // 一个个提取元素
        for (int i = n - 1; i > 0; i--) {
            // 移动当前根到末尾
            KeyVo temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;

            // 重新堆化剩余堆
            heapify(arr, i, 0);
        }
    }

    private static void heapify(KeyVo[] arr, int n, int i) {
        int largest = i; // 初始化最大值为根
        int left = 2 * i + 1; // 左子节点位置
        int right = 2 * i + 2; // 右子节点位置

        // 如果左子节点大于根
        if (left < n && arr[left].compareTo(arr[largest]) > 0) {
            largest = left;
        }

        // 如果右子节点大于当前最大值
        if (right < n && arr[right].compareTo(arr[largest]) > 0) {
            largest = right;
        }

        // 如果最大值不是根
        if (largest != i) {
            KeyVo swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;

            // 递归地堆化受影响的子树
            heapify(arr, n, largest);
        }
    }

    public static void main(String[] args) {
        List<KeyVo> dataList = new ArrayList<>();
        dataList.add(new KeyVo("1",1,5));
        dataList.add(new KeyVo("2",2,5));
        dataList.add(new KeyVo("3",3,5));
        dataList.add(new KeyVo("4",4,5));
        dataList.add(new KeyVo("5",5,5));
        dataList.add(new KeyVo("6",6,5));
        dataList.add(new KeyVo("7",7,5));
        dataList.add(new KeyVo("8",8,5));
        dataList.add(new KeyVo("9",9,5));

        System.out.println("Original List:");
        dataList.forEach(System.out::println);

        KeyVo[] arr = dataList.toArray(new KeyVo[0]);
        heapSort(arr);
        KeyVo[] topN = new KeyVo[Math.min(10, arr.length)];
        System.arraycopy(arr, 0, topN, 0, Math.min(10, arr.length));
        ArrayList<KeyVo> keyVos = new ArrayList<>(List.of(topN));
        System.out.println("\nSorted List:");
        keyVos.forEach(s -> System.out.println(s.getKey()));
    }
}