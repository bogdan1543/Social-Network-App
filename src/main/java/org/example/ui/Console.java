package org.example.ui;

import org.example.domain.FriendRequest;
import org.example.domain.Friendship;
import org.example.domain.User;
import org.example.domain.validator.ValidationException;
import org.example.service.SocialCommunities;
import org.example.service.SocialNetwork;

import java.util.List;
import java.util.Scanner;

public class Console {

    private final SocialNetwork socialNetwork;
    private final SocialCommunities socialCommunities;

    public Console(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
        this.socialCommunities = new SocialCommunities(socialNetwork);
    }

    void printMenu() {
        System.out.println("\t\t\tMENU\t\t\t");
        System.out.println("1. Add user");
        System.out.println("2. Remove user");
        System.out.println("3. Add friendship");
        System.out.println("4. Remove friendship");
        System.out.println("5. Print users");
        System.out.println("6. Print friendships");
        System.out.println("7. Communities");
        System.out.println("8. Most social community");
        System.out.println("0. EXIT");
    }

    public void run() {
        Scanner scan = new Scanner(System.in);
        boolean ok = true;
        while (ok) {
            printMenu();
            String input = scan.nextLine();
            switch (input) {
                case "1":
                    addUser();
                    break;
                case "2":
                    removeUser();
                    break;
                case "3":
                    addFriendship();
                    break;
                case "4":
                    removeFriendship();
                    break;
                case "5":
                    printUsers();
                    break;
                case "6":
                    printFriendships();
                    break;
                case "7":
                    printConnectedCommunities();
                    break;
                case "8":
                    printMostSocialCommunity();
                    break;
                case "0":
                    System.out.println("exit");
                    ok = false;
                    break;
                default:
                    System.out.println("Invalid input!");
                    break;
            }
        }
    }


    /**
     * Prints the users from the social network
     */
    void printUsers() {
        System.out.println("\t\t\tUSERS\t\t\t");
        for (User u : socialNetwork.getUsers()) {
            System.out.println("ID: " + u.getId() + " " + u.getFirstName() + " " + u.getLastName());
        }
    }


    /**
     * Adds user to the social network
     */
    void addUser() {
        System.out.println("Add user");
        Scanner scan = new Scanner(System.in);
        System.out.println("First name: ");
        String firstName = scan.nextLine();
        System.out.println("Last name: ");
        String lastName = scan.nextLine();
        try {
            socialNetwork.addUser(new User(firstName, lastName));
        } catch (ValidationException e) {
            System.out.println("Invalid user!");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid argument");
        }

    }


    /**
     * Removes user from social network
     */
    void removeUser() {
        printUsers();
        System.out.println("Remove user");
        Scanner scan = new Scanner(System.in);
        System.out.println("Id: ");
        String var = scan.nextLine();
        try {
            Integer id = Integer.parseInt(var);
            User user = socialNetwork.removeUser(id);
        } catch (IllegalArgumentException e) {
            System.out.println("ID must be a number! It can't contain letters or symbols! ");
        }
    }


    /**
     * Prints friendships
     */
    void printFriendships() {
        for (User user : socialNetwork.getUsers()){
            for (User friend : socialNetwork.getUserFriends(user).keySet()){
                System.out.println(user.getFirstName() + "-" + friend.getFirstName());
            }
        }
        for (Friendship friendship : socialNetwork.getFriendships()){
            if (friendship.getFriendRequestStatus() != FriendRequest.ACCEPTED){
                socialNetwork.respondFriendRequest(friendship, FriendRequest.valueOf("ACCEPTED"));
            }

        }
        for (User user : socialNetwork.getUsers()){
            for (User friend : socialNetwork.getUserFriends(user).keySet()){
                System.out.println(user.getFirstName() + "-" + friend.getFirstName());
            }
        }

    }

    /**
     * Adds a new friendship between two users
     */
    void addFriendship() {
        Scanner scan = new Scanner(System.in);
        System.out.println("ID of the first user: ");
        String var1 = scan.nextLine();
        System.out.println("ID of the second user: ");
        String var2 = scan.nextLine();
        try {
            int id1 = 0, id2 = 0;
            try {
                id1 = Integer.parseInt(var1);
                id2 = Integer.parseInt(var2);
            } catch (IllegalArgumentException e) {
                System.out.println("ID must be a number! It can't contain letters or symbols! ");
            }
            socialNetwork.addFriendship(new Friendship(id1, id2));
        } catch (ValidationException e) {
            System.out.println("Friendship is invalid! ");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid arguments! ");
        }
    }


    /**
     * Removes a friendship between two friends
     */
    private void removeFriendship() {
        Scanner scan = new Scanner(System.in);
        System.out.println("ID of the first user: ");
        String var1 = scan.nextLine();
        System.out.println("ID of the second user: ");
        String var2 = scan.nextLine();
        try {
            int id1 = 0, id2 = 0;
            try {
                id1 = Integer.parseInt(var1);
                id2 = Integer.parseInt(var2);
            } catch (IllegalArgumentException e) {
                System.out.println("ID must be a number! It can't contain letters or symbols! ");
            }
            socialNetwork.removeFriendship(id1, id2);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid arguments! ");
        }
    }


    /**
     * Prints connected communities
     */
    private void printConnectedCommunities() {
        System.out.println("Social Communities\n");
        int nrOfCommunities = socialCommunities.connectedCommunities();
        System.out.println("Number of Social Communities: " + nrOfCommunities);
    }

    private void printMostSocialCommunity() {
        System.out.println("Most social community: ");
        List<Integer> mostSocialCommunity = socialCommunities.mostSocialCommunity();
        mostSocialCommunity.forEach(System.out::println);
    }

}
