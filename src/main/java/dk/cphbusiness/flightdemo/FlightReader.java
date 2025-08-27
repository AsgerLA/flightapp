package dk.cphbusiness.flightdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.cphbusiness.flightdemo.dtos.FlightDTO;
import dk.cphbusiness.flightdemo.dtos.FlightInfoDTO;
import dk.cphbusiness.utils.Utils;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
public class FlightReader {

    private static long getTotalTime(List<FlightInfoDTO> infoList) {
        return infoList.stream()
                .filter(i -> i.getAirline() != null &&
                        i.getDestination() != null &&
                        i.getOrigin() != null &&
                        i.getAirline().equals("Lufthansa"))
                .mapToLong(i -> i.getDuration().toMinutes())
                .sum();
    }

    private static int getCountAirlineFlight(List<FlightInfoDTO> infoList) {
        return infoList.stream()
                .filter(i -> i.getAirline() != null &&
                        i.getDestination() != null &&
                        i.getOrigin() != null &&
                        i.getAirline().equals("Lufthansa"))
                .collect(Collectors.toList()).size();
    }

    private static List<FlightInfoDTO> getListOfFlightsBetweenTwoAirports(List<FlightInfoDTO> infoList, String origin,String destination){
       List<FlightInfoDTO> resultList = infoList.stream().filter(i-> i.getOrigin()!= null
               && i.getDestination()!=null
               && i.getOrigin().equals(origin)
               &&i.getDestination().equals(destination)).toList();
       return resultList;
    }

    public static void main(String[] args) {
        try {
            List<FlightDTO> flightList = getFlightsFromFile("flights.json");
            List<FlightInfoDTO> flightInfoDTOList = getFlightInfoDetails(flightList);
            //flightInfoDTOList.forEach(System.out::println);
            System.out.println("Time: "+getTotalTime(flightInfoDTOList));
            System.out.println("Count: "+getCountAirlineFlight(flightInfoDTOList));
            System.out.println(getListOfFlightsBetweenTwoAirports(flightInfoDTOList, "Fukuoka", "Haneda Airport"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<FlightDTO> getFlightsFromFile(String filename) throws IOException {

        ObjectMapper objectMapper = Utils.getObjectMapper();

        // Deserialize JSON from a file into FlightDTO[]
        FlightDTO[] flightsArray = objectMapper.readValue(Paths.get("flights.json").toFile(), FlightDTO[].class);

        // Convert to a list
        List<FlightDTO> flightsList = List.of(flightsArray);
        return flightsList;
    }

    public static List<FlightInfoDTO> getFlightInfoDetails(List<FlightDTO> flightList) {
        List<FlightInfoDTO> flightInfoList = flightList.stream()
           .map(flight -> {
                LocalDateTime departure = flight.getDeparture().getScheduled();
                LocalDateTime arrival = flight.getArrival().getScheduled();
                Duration duration = Duration.between(departure, arrival);
                FlightInfoDTO flightInfo =
                        FlightInfoDTO.builder()
                            .name(flight.getFlight().getNumber())
                            .iata(flight.getFlight().getIata())
                            .airline(flight.getAirline().getName())
                            .duration(duration)
                            .departure(departure)
                            .arrival(arrival)
                            .origin(flight.getDeparture().getAirport())
                            .destination(flight.getArrival().getAirport())
                            .build();

                return flightInfo;
            })
        .toList();
        return flightInfoList;
    }

}
