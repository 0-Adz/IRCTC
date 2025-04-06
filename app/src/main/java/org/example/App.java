/*
 * This source file was generated by the Gradle 'init' task
 */
package org.example;

import org.example.entities.Train;
import org.example.entities.User;
import org.example.services.UserBookingService;
import org.example.util.UserServiceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class App {

    public static void main(String[] args) {
        System.out.println("Running Train Booking System");
        Scanner scanner = new Scanner(System.in);

        int option = 0;
        UserBookingService userBookingService;

        try {
            userBookingService = new UserBookingService();
        }catch(IOException ex){
            System.out.println("There is something wrong");
            return;
        }

        while(option != 7){
            System.out.println("Please choose one of the following options");
            System.out.println("1. Sign up");
            System.out.println("2. Login");
            System.out.println("3. Fetch Booking Details");
            System.out.println("4. Search Trains");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel Booking");
            System.out.println("7. Exit");
            option = scanner.nextInt();

            switch (option){
                case 1:
                    System.out.println("Enter Username");
                    String nameToSignup = scanner.next();
                    System.out.println("Enter Password");
                    String passwordToSignup = scanner.next();
                    User userToSignup = new User(nameToSignup, passwordToSignup, UserServiceUtil.hashPassword(passwordToSignup), new ArrayList<>(), UUID.randomUUID().toString());
                    userBookingService.signUp(userToSignup);
                    break;
                case 2:
                    System.out.println("Enter Username");
                    String nameToLogin = scanner.next();
                    System.out.println("Enter Password");
                    String passwordToLogin = scanner.next();
                    User userToLogin = new User(nameToLogin, passwordToLogin, UserServiceUtil.hashPassword(passwordToLogin), new ArrayList<>(), UUID.randomUUID().toString());
                    try {
                        userBookingService = new UserBookingService(userToLogin);
                    }catch(IOException ex){
                        return;
                    }
                    break;
                case 3:
                    System.out.println("Fetching Booking Details");
                    userBookingService.fetchBooking();
                    break;
                case 4:
                    System.out.println("Source station");
                    String sourceStation = new Scanner(System.in).next();
                    System.out.println("Destination station");
                    String destinationStation = new Scanner(System.in).next();
                    List<Train> trains = userBookingService.getTrains(sourceStation,destinationStation);

            }
        }
    }
}
