package org.example.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainService {

    private Train train;
    private List<Train> trainList;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TRAINS_PATH = "app/src/main/java/org/example/localDb/trains.json";

    public TrainService() throws IOException{
        loadTrains();
    }

    public TrainService(Train train1) throws IOException{
        this.train = train1;
        loadTrains();
    }

    public void loadTrains() throws IOException{
        File trains = new File(TRAINS_PATH);
        trainList = objectMapper.readValue(trains, new TypeReference<>() {
        });
    }

    public List<Train> searchTrains(String sourceStation, String destinationStation){
        return trainList.stream().filter(train -> validTrain(train,sourceStation,destinationStation)).collect(Collectors.toList());
    }
    public boolean validTrain(Train train, String sourceStation, String destinationStation){
        List<String> stationOrder = train.getStations();

        int sourceInd =  stationOrder.indexOf(sourceStation.toLowerCase());
        int destinationInd =  stationOrder.indexOf(destinationStation.toLowerCase());

        return sourceInd != -1 && destinationInd != -1 && sourceInd < destinationInd;
    }
    public void addTrain(Train newTrain){
        Optional<Train> existingTrain = trainList.stream()
                .filter(train -> train.getTrainId().equalsIgnoreCase(newTrain.getTrainId()))
                .findFirst();
        if(existingTrain.isPresent()){
            updateTrain(newTrain);
        }
        else{
            trainList.add(newTrain);
            saveTrainListToFile();
        }
    }
    public void updateTrain(Train train){
        OptionalInt index = IntStream.range(0, trainList.size())
                .filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(train.getTrainId()))
                .findFirst();
        if(index.isPresent()){
            trainList.set(index.getAsInt(), train);
            saveTrainListToFile();
        }
        else{
            addTrain(train);
        }
    }

    private void saveTrainListToFile(){
        try{
            objectMapper.writeValue(new File(TRAINS_PATH), trainList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
