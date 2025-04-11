package org.example.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.Train;
import org.example.entities.User;
import org.example.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserBookingService {

    private User user;

    private List<User> userList;

    private ObjectMapper objectMapper = new ObjectMapper();

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
        userList = objectMapper.readValue(users, new TypeReference<List<User>>() {
        });
    }

    public Boolean loginUser(){
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        return foundUser.isPresent();
    }

    public Boolean signUp(User user1){
        try{
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }

    private  void saveUserListToFile() throws IOException {
        File usersFile = new File(USERS_PATH);
        objectMapper.writeValue(usersFile, userList);
    }

    public void fetchBooking(){
        Optional<User> userFetched = userList.stream()
                .filter(user1 -> {return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(),user1.getHashedPassword());})
                .findFirst();
        if(userFetched.isPresent()){
            userFetched.get().printTickets();
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
    public Boolean bookTrainSeat(Train trainSelectedForBooking, Integer row, Integer col){
        try{
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = trainSelectedForBooking.getSeats();
            if(row >= 0 && row < seats.size() && col >= 0 && col < seats.get(row).size()){
                if(seats.get(row).get(col) == 0){
                    seats.get(row).set(col,1);
                    trainSelectedForBooking.setSeats(seats);
                    trainService.addTrain(trainSelectedForBooking);
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
