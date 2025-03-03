package com.dsa.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LeetCodePracticeProblems {

	public static void main(String[] args) {

		/*
		 * Problem - 1: 3Sum
		 */

		/*
		// inputs
		int[] nums = { -1, 0, 1, 2, -1, -4 }; 	// expected: [[-1, -1, 2], [-1, 0, 1]]
//		int[] nums = { -2, 0, 1, 1, 2 };		// expected: [[-2, 0, 2], [-2, 1, 1]]

		// logic
		int size = nums.length;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size - 1; j++) {
				if (nums[j] > nums[j + 1]) {
					int temp = nums[j];
					nums[j] = nums[j + 1];
					nums[j + 1] = temp;
				}
			}
		}

		List<List<Integer>> result = new ArrayList<>();

		// Use three pointers on sorted array
		for (int i = 0; i < size; i++) {
			if (i > 0 && nums[i] == nums[i - 1]) {
				continue;
			}
			int l = i + 1;
			int r = size - 1;
			while (l < r) {
				int sum = nums[i] + nums[l] + nums[r];
				if (sum == 0) {
					result.add(Arrays.asList(nums[i], nums[l], nums[r]));

					// check for duplicates
					while (l < r && nums[l] == nums[l + 1]) {
						l++;
					}
					while (l < r && nums[r] == nums[r - 1]) {
						r--;
					}
				}
				if (sum < 0) {
					l++;
				} else {
					r--;
				}
			}
		}
		
		System.out.println(result);
		*/

		
		/*
		 * Problem - 2: Container With Most Water
		 */

		/*
		// inputs
		int[] height = { 1, 8, 6, 2, 5, 4, 8, 3, 7 }; // expected: 49

		// logic
        int maxArea = 0;
        int left = 0;
        int right = height.length - 1;
        while (left < right) {
            int currentMinHeight = 0;
            if (height[left] < height[right]) {
                currentMinHeight = height[left];
            } else {
                currentMinHeight = height[right];
            }

            int area = currentMinHeight * (right - left);
            if (maxArea < area) {
                maxArea = area;
            }

            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }

        System.out.println(maxArea);
        */
        
        
        /*
         * Problem - 3: Longest Substring Without Repeating Characters
         */
        
		/*
        // inputs
//        String s = "abcabcbb";  	// expected: 3
//        String s = "pwwkew"; 		// expected: 3
//        String s = "au";			// expected: 2
//        String s = "dvdf";		// expected: 3
        String s = "anviaj";		// expected: 4
         
        // logic
        List<List<Character>> parentList = new ArrayList<>();
        List<Character> childList = new ArrayList<>();
        int length = s.length();
        
        int maxLength = 0;
        if (length > 1) {
	        for (int i=0; i<length; i++) {
	        	if (childList.contains(s.charAt(i))) {
	        		parentList.add(childList);
	        		childList = new ArrayList<>();
	        		if (i!=0 && (s.charAt(i-1) != s.charAt(i))) {
	        			childList.add(s.charAt(i-1));
	        		}
	        		childList.add(s.charAt(i));
	        	} else {
	        		childList.add(s.charAt(i));
	        	}
	        }
	        if (!childList.isEmpty()) {
	        	parentList.add(childList);
	        }
	        
	        for (List<Character> child : parentList) {
				if (maxLength <  child.size()) {
					maxLength = child.size();
				}
			} 
        } else {
        	maxLength = length;
        }
        
        
        System.out.println(parentList);
        System.out.println(maxLength);
        */
		
		/*
		 * problem - 4: Minimum Cost For Tickets (Approach: Dynamic Programming)
		 */
	    
	    /*
	    // input
        int[] days = { 1, 4, 6, 7, 8, 20 }; // expected: 11
        int[] costs = { 2, 7, 15 };
	    
        int maxDay = days[days.length-1];
        boolean[] travelDays = new boolean[maxDay + 1];

        for (int day : days) {
            travelDays[day] = true;
        }

        int[] dp = new int[maxDay + 1];
        dp[0] = 0;
        for (int i=1; i<=maxDay; i++) {
            if (!travelDays[i]) {
                dp[i] = dp[i-1];
                continue;
            }

            dp[i] = costs[0] + dp[i-1];
            dp[i] = Math.min(dp[Math.max(0, i-7)] + costs[1], dp[i]);
            dp[i] = Math.min(dp[Math.max(0, i-30)] + costs[2], dp[i]);
        }
        
        for (int i : dp) {
            System.out.print(i + " ");
        }
        */
	    
	    /*
         * problem - 5: Valid Anagram (Arrays & Hashing)
         */
        
	    /*
	    // input
	    String s = "anagram"; // expected: true
	    String t = "nagaram";
	    
	    // logic
	    boolean isAnagram = false;
	    
	    Map<Character, Integer> inputMap = new HashMap<>();
	    Map<Character, Integer> targetMap = new HashMap<>();
	    
	    if (s.length() == t.length()) {
    	    
            int inputLength = s.length();
            for (int i=0; i<inputLength; i++) {
                inputMap.put(s.charAt(i) , Integer.valueOf((inputMap.getOrDefault(s.charAt(i) , 0) + 1)));
            }
            
            int targetLength = t.length();
            for (int i=0; i<targetLength; i++) {
                targetMap.put(t.charAt(i) , Integer.valueOf((targetMap.getOrDefault(t.charAt(i) , 0) + 1)));
            }
            
            System.out.println(inputMap);
            System.out.println(targetMap);
                        
            for (Entry<Character, Integer> entrySet : inputMap.entrySet()) {
                Character key = entrySet.getKey();
                int value = entrySet.getValue();
                if (targetMap.containsKey(key) && targetMap.get(key) == value) {
                    continue;
                } else {
                    System.out.println(false);                    
                }
            }
            System.out.println(true);
	    }
	    System.out.println(false);
	    */
	    

	    /*
         * problem - 6: Group Anagrams (Arrays & Hashing)
         */
	    
	    /*
	    // input
	    String[] strs = {"act","pots","tops","cat","stop","hat"}; // expected: [[[act, cat], [pots, tops, stop], [hat]]]
	    
	    // logic
	    Map<String, List<String>> result = new HashMap<>();
	    
	    for (String str : strs) {
            char[] charArray = str.toCharArray();
            Arrays.sort(charArray);
            String sortedString = new String(charArray);
            result.putIfAbsent(sortedString, new ArrayList<>());
            result.get(sortedString).add(str);
        }
	    
	    System.out.println(Arrays.asList(result.values()));
	    */
	    
	    
	    /*
         * problem - 6: Climbing Stairs (Dynamic Programming)
         */
	    
	    int n = 0;
	    
	    
	    
	}

}
