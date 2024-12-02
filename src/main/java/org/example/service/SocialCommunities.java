package org.example.service;

import org.example.domain.Friendship;
import org.example.domain.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SocialCommunities {

    SocialNetwork socialNetwork;
    HashMap<Integer, List<Integer>> adjList;

    List<Integer> max = new ArrayList<>();

    public SocialCommunities(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
    }

    void DFS(Integer v, HashMap<Integer, Boolean> visited, List<Integer> community) {
        visited.put(v, true);
        if(community != null){
            community.add(v);
        }
        System.out.println(v + " " + this.socialNetwork.findUser(v).getFirstName() + " " + this.socialNetwork.findUser(v).getLastName());
        if (adjList.containsKey(v)) {
            for (Integer x : adjList.get(v)) {
                if (!visited.containsKey(x))
                    DFS(x, visited, community);
            }
        }
    }

    public void createAdjList(){
        // creates an adjacency list of user and its friends
        adjList = new HashMap<Integer, List<Integer>>();
        socialNetwork.getUsers().forEach(user -> {
            List<Integer> friends = new ArrayList<>();
            for (Friendship friendship : socialNetwork.getFriendships()) {
                if (friendship.getIdUser1().equals(user.getId()))
                    friends.add(friendship.getIdUser2());
                if (friendship.getIdUser2().equals(user.getId()))
                    friends.add(friendship.getIdUser1());
            }
            if (!friends.isEmpty())
                this.adjList.put(user.getId(), friends);
        });
    }

    public int connectedCommunities() {
        // creates an adjacency list of user and its friends
        createAdjList();

        // list of ids of users
        List<Integer> ids = new ArrayList<>();
        for (User user : socialNetwork.getUsers())
            ids.add(user.getId());

        int nrOfCommunities = 0;
        HashMap<Integer, Boolean> visited = new HashMap<Integer, Boolean>();
        for (Integer v : ids) {
            if (!visited.containsKey(v)) {
                DFS(v, visited, null);
                nrOfCommunities++;
                System.out.println();
            }
        }
        return nrOfCommunities;
    }

    public List<Integer> mostSocialCommunity() {
        // creates an adjacency list of user and its friends
        createAdjList();

        // list of ids of users
        List<Integer> ids = new ArrayList<>();
        for (User user : socialNetwork.getUsers())
            ids.add(user.getId());


        HashMap<Integer, Boolean> visited = new HashMap<Integer, Boolean>();
        ids.forEach(v -> {
            if (!visited.containsKey(v)) {
                List<Integer> community = new ArrayList<>();
                DFS(v, visited, community);
                if(community.size() > max.size()){
                    max = community;
                }
                System.out.println();
            }
        });

        return max;
    }
}