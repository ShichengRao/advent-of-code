package aoc2021;

import java.io.*;
import java.util.*;

public class Day3{
	
	public String solve(boolean part1, Scanner in) throws FileNotFoundException {
		List<String> inps = new ArrayList<>();
		while(in.hasNext()) {
			inps.add(in.nextLine());
		}
		int[] differentials = new int[12];
		for(String inp: inps) {
			char[] chars = inp.toCharArray();
			for(int i = 0; i < differentials.length; i++) {
				differentials[i] += chars[i] == '1'?1:-1;
			}
		}
		if(!part1) {
			List<String> newNums = new ArrayList<>();
			for(String num: inps) {
				newNums.add(num);
			}
			return "" + Integer.parseInt(helper(inps, "",true),2) * Integer.parseInt(helper(newNums,"",false),2);
		}
		StringBuilder epsilon = new StringBuilder();
		StringBuilder gamma = new StringBuilder();
		for(int i = 0; i < differentials.length; i++) {
			gamma.append(differentials[i] > 0?"1":"0");
			epsilon.append(differentials[i] > 0?"0":"1");
		}
		return "" + Integer.parseInt(gamma.toString(),2) * Integer.parseInt(epsilon.toString(),2);
	}
	
	public static String helperGolfed(List<String> n, String p, boolean m) {
		final String f=p;
		n = n.stream().filter(v->v.startsWith(f)).toList();
		if(n.size()==1)
			return n.get(0);
		int b=n.stream().mapToInt(s -> s.charAt(f.length())=='1'?1:-1).sum();
		return helper(n,p+(b>=0^m?"0":"1"),m);
	}
	
	public static String helper(List<String> nums, String prefix, boolean most) {
		final String prefix_final = prefix;
		nums = nums.stream().filter(val -> val.startsWith(prefix_final)).toList();
		if(nums.size() == 1) {
			return nums.get(0);
		}
		boolean moreOnes = nums.stream().mapToInt(num -> num.charAt(prefix_final.length()) == '1'?1:-1).sum() >= 0;
		prefix += moreOnes ^ most?"0":"1";
		return helper(nums,prefix, most);
	}
}
