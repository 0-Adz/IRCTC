package org.example.services;

import org.example.entities.Train;

import java.util.List;
import java.util.stream.Collectors;

public class TrainService {

    public List<Train> searchTrains(String sourceStation, String destinationStation){
        return trainList.stream().filter(train -> validTrain(train,sourceStation,destinationStation)).collect(Collectors.toList());
    }
    public boolean validTrain(Train train, String sourceStation, String destinationStation){
        List<String> stationOrder = train.getStations();

        int sourceInd =  stationOrder.indexOf(sourceStation.toLowerCase());
        int destinationInd =  stationOrder.indexOf(destinationStation.toLowerCase());

        return sourceInd != -1 && destinationInd != -1 && sourceInd < destinationInd;
    }
}
