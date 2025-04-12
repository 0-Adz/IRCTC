package org.example.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.Ticket;
import org.example.entities.Train;
import org.example.entities.User;
import org.example.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserBookingService {

    private User user;

    private List<User> userList;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String USERS_PATH = "app/src/main/java/org/example/localDb/users.json";

    public UserBookingService(User user1) throws IOException {
        this.user = user1;
        loadUsers();
    }
    public UserBookingService() throws IOException {
        loadUsers();
    }

    public void loadUsers() throws IOException{
        File users = new File(USERS_PATH);
        userList = objectMapper.readValue(users, new TypeReference<>() {
        });
    }

    public User loginUser(String name, String password){
        Optional<User> foundUser = userList.stream().filter(user1 -> user1.getName().equalsIgnoreCase(name) && UserServiceUtil.checkPassword(password, user1.getHashedPassword())).findFirst();
        return foundUser.orElse(null);
    }

    public void signUp(User user1){
        try{
            userList.add(user1);
            saveUserListToFile();
        }catch (IOException ex){
            System.out.println("Unable to SignUp");
        }
    }

    private  void saveUserListToFile() throws IOException {
        File usersFile = new File(USERS_PATH);
        objectMapper.writeValue(usersFile, userList);
    }

    public void fetchBooking(){
        try {
            if (userList == null || user == null) {
                System.out.println("Cannot fetch bookings. Missing user or user list.");
                return;
            }

            Optional<User> userFetched = userList.stream()
                    .filter(user1 ->
                            user1.getName() != null &&
                                    user.getName() != null &&
                                    user1.getHashedPassword() != null &&
                                    user.getPassword() != null &&
                                    user1.getName().equalsIgnoreCase(user.getName()) &&
                                    UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword())
                    )
                    .findFirst();

            userFetched.ifPresent(User::printTickets);
        } catch (Exception e) {
            System.out.println("An error occurred while fetching booking: " + e.getMessage());
            e.fillInStackTrace(); // for debugging
        }
    }

    public Boolean cancelBooking(String ticketId){

        return Boolean.FALSE;
    }

    public List<Train> getTrains(String sourceStation, String destinationStation){
        try {
            TrainService trainService = new TrainService();
            return trainService.searchTrains(sourceStation, destinationStation);
        }catch(IOException ex){
            return new ArrayList<>();
        }
    }

    public List<List<Integer>> fetchSeats(Train train){
        return train.getSeats();
    }
    public Boolean bookTrainSeat(Train trainSelectedForBooking, Integer row, Integer col, String source, String destination){
        try{
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = trainSelectedForBooking.getSeats();
            if(row >= 0 && row < seats.size() && col >= 0 && col < seats.get(row).size()){
                if(seats.get(row).get(col) == 0){
                    seats.get(row).set(col,1);
                    trainSelectedForBooking.setSeats(seats);
                    trainService.addTrain(trainSelectedForBooking);
                    String userId1 = user.getUserId();
                    String dateOfTravel = "2023-12-08T18:30:00Z";
                    ObjectMapper objectMapper = new ObjectMapper();

                    // Read users from file
                    List<User> users = objectMapper.readValue(new File(USERS_PATH), new TypeReference<List<User>>() {});

                    // Find and update the user's tickets
                    for (User user1 : users) {
                        if (user1.getUserId().equals(userId1)) {
                            Ticket ticket = new Ticket(UUID.randomUUID().toString(),user1.getUserId(),source,destination,dateOfTravel,trainSelectedForBooking);
                            List<Ticket> userTickets = user1.getTicketsBooked();
                            userTickets.add(ticket);
                            user1.setTicketsBooked(userTickets);
                            break;
                        }
                    }

                    // Write back to the file
                    objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(USERS_PATH), users);
                    return true;
                }
                else{
                    return false;
                }
            }else{
                return false;
            }
        }catch(IOException ex){
            return Boolean.FALSE;
        }
    }
}
