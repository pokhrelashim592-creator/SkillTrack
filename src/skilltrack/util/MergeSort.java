package skilltrack.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MergeSort {

    public static <T> ArrayList<T> sort(List<T> input, Comparator<T> cmp) {
        ArrayList<T> arr = new ArrayList<>(input);
        if (arr.size() <= 1) return arr;
        return mergeSort(arr, cmp);
    }

    private static <T> ArrayList<T> mergeSort(ArrayList<T> arr, Comparator<T> cmp) {
        int n = arr.size();
        if (n <= 1) return arr;

        int mid = n / 2;
        ArrayList<T> left = new ArrayList<>(arr.subList(0, mid));
        ArrayList<T> right = new ArrayList<>(arr.subList(mid, n));

        left = mergeSort(left, cmp);
        right = mergeSort(right, cmp);

        return merge(left, right, cmp);
    }

    private static <T> ArrayList<T> merge(ArrayList<T> left, ArrayList<T> right, Comparator<T> cmp) {
        ArrayList<T> out = new ArrayList<>(left.size() + right.size());
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            if (cmp.compare(left.get(i), right.get(j)) <= 0) {
                out.add(left.get(i++));
            } else {
                out.add(right.get(j++));
            }
        }
        while (i < left.size()) out.add(left.get(i++));
        while (j < right.size()) out.add(right.get(j++));
        return out;
    }
}
