package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day23 extends DayTemplate {

    Set<String> computers = new HashSet<>();
    Map<String, Set<String>> connections = new HashMap<>();
    Set<Set<String>> maximalCliques = new HashSet<>();

    public String solve(boolean part1, Scanner in) {
        long answer = 0;
        List<String> lines = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            lines.add(line);
        }

        for(String line: lines){
            String first = line.split("-")[0];
            String second = line.split("-")[1];
            Set<String> f = connections.getOrDefault(first, new HashSet<>());
            Set<String> s = connections.getOrDefault(second, new HashSet<>());
            f.add(second);
            s.add(first);
            connections.put(first, f);
            connections.put(second, s);
            computers.add(first);
            computers.add(second);
        }

        List<String> forIndices = new ArrayList<>(computers);

        if(part1){
            for(String first: forIndices){
                for(String second: connections.get(first)){
                    for(String third: connections.get(second)){
                        if(connections.get(third).contains(first) && (first.startsWith("t") || second.startsWith("t") || third.startsWith("t"))){
                            answer++;
                        }
                    }
                }
            }
            answer/=6;
        }
        else{
            bronKerbosch(new HashSet<>(), computers, new HashSet<>());
            Set<String> biggest = new HashSet<>();
            for(Set<String> candidate: maximalCliques){
                if(candidate.size() > biggest.size()){
                    biggest = candidate;
                }
            }
            return listToString(new ArrayList<>(biggest));
        }
        return answer+"";
    }

    private void bronKerbosch(Set<String> clique, Set<String> candidates, Set<String> excluded){
        if(candidates.isEmpty() && excluded.isEmpty()){
            maximalCliques.add(clique);
        }
        else{
            Set<String> candidatesOrExcluded = new HashSet<>(candidates);
            candidatesOrExcluded.addAll(excluded);
            String pivot = candidatesOrExcluded.iterator().next();
            candidates.removeAll(connections.get(pivot));
            for(String v: List.copyOf(candidates)){
                Set<String> nextClique = new HashSet<>(clique);
                nextClique.add(v);
                Set<String> nextCandidates = new HashSet<>(connections.get(v));
                nextCandidates.retainAll(candidates);
                Set<String> nextExcluded = new HashSet<>(connections.get(v));
                nextExcluded.retainAll(excluded);

                bronKerbosch(nextClique, nextCandidates, nextExcluded);
                candidates.remove(v);
                excluded.add(v);
            }
            Set<String> nextClique = new HashSet<>(clique);
            nextClique.add(pivot);
            Set<String> nextCandidates = new HashSet<>(connections.get(pivot));
            nextCandidates.retainAll(candidates);
            Set<String> nextExcluded = new HashSet<>(connections.get(pivot));
            nextExcluded.retainAll(excluded);
            bronKerbosch(nextClique, nextCandidates, nextExcluded);
            candidates.remove(pivot);
            excluded.add(pivot);
        }
    }

    private String listToString(List<String> computers){
        Collections.sort(computers);
        String result = "";
        for(String computer: computers){
            result+=computer;
            result+=",";
        }
        return result.substring(0, result.length() - 1);
    }
}