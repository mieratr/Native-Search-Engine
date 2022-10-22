package finalproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry; // You may need it to implement fastSort

public class Sorting {

	/*
	 * This method takes as input an HashMap with values that are Comparable. 
	 * It returns an ArrayList containing all the keys from the map, ordered 
	 * in descending order based on the values they mapped to. 
	 * 
	 * The time complexity for this method is O(n^2) as it uses bubble sort, where n is the number 
	 * of pairs in the map. 
	 */
    public static <K, V extends Comparable> ArrayList<K> slowSort (HashMap<K, V> results) {
        ArrayList<K> sortedUrls = new ArrayList<K>();
        sortedUrls.addAll(results.keySet());	//Start with unsorted list of urls

        int N = sortedUrls.size();
        for(int i=0; i<N-1; i++){
			for(int j=0; j<N-i-1; j++){
				if(results.get(sortedUrls.get(j)).compareTo(results.get(sortedUrls.get(j+1))) <0){
					K temp = sortedUrls.get(j);
					sortedUrls.set(j, sortedUrls.get(j+1));
					sortedUrls.set(j+1, temp);					
				}
			}
        }
        return sortedUrls;                    
    }
    
    
	/*
	 * This method takes as input an HashMap with values that are Comparable. 
	 * It returns an ArrayList containing all the keys from the map, ordered 
	 * in descending order based on the values they mapped to. 
	 * 
	 * The time complexity for this method is O(n*log(n)), where n is the number 
	 * of pairs in the map. 
	 */
    public static <K, V extends Comparable> ArrayList<K> fastSort(HashMap<K, V> results) {
		ArrayList<Entry<K, V>> sortedUrls = new ArrayList<Entry<K,V>>();
		sortedUrls.addAll(results.entrySet());	//Start with unsorted list of urls
		int N = sortedUrls.size();
		quickSort(sortedUrls, 0, N-1);
		ArrayList<K> answer = new ArrayList<K>();
		for (Entry entry : sortedUrls) {
			answer.add((K) entry.getKey());
		}
		return answer;
	}
	
	private static <K, V extends Comparable> void quickSort(ArrayList<Entry<K,V>> list, int leftIndex, int rightIndex) {
		while (leftIndex < rightIndex) {
			if (rightIndex - leftIndex < 50) {
				insertionSort(list, leftIndex, rightIndex);
				return;
			} else {
				int pivot = placeAndDivide(list, leftIndex, rightIndex);
				if (pivot - leftIndex < rightIndex - pivot) {
					quickSort(list, leftIndex, pivot-1);
					leftIndex = pivot +  1;
				} else {
					quickSort(list, pivot+1, rightIndex);
					rightIndex = pivot - 1;
				}
			}
		}
	}
	
	private static <K, V extends Comparable> int placeAndDivide(ArrayList<Entry<K,V>> list, int leftIndex, int rightIndex) {
		int midIndex = (leftIndex + rightIndex) / 2;
		swap(list, rightIndex, midIndex);
		V pivot = list.get(rightIndex).getValue(); 
		int wall = leftIndex - 1;
		for (int i = leftIndex; i < rightIndex; i++) {
			if (list.get(i).getValue().compareTo(pivot) > 0) {
				wall++;
				swap(list, i, wall);
			}
		}
		swap(list, rightIndex, wall + 1);
		return wall+1;
	}
	
	private static <K, V extends Comparable> void insertionSort(ArrayList<Entry<K,V>> list, int leftIndex, int rightIndex) {
		for (int i = leftIndex; i <= rightIndex; i++) {
			Entry<K,V> element = list.get(i);
			int k = i;
			while (k > 0 && (element.getValue().compareTo(list.get(k-1).getValue()) > 0)) {
				list.set(k, list.get(k-1));
				k--;
			}
			list.set(k, element);
		}
	}
	
	
	private static <K, V extends Comparable> void swap (ArrayList<Entry<K,V>> list, int left, int right) {
		Entry temp = list.get(right);
		list.set(right, list.get(left));
		list.set(left, temp);
	}
}