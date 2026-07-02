package com.louaidev.locationservice.service;

import com.louaidev.locationservice.dto.DriverLocationRequest;
import com.louaidev.locationservice.dto.NearByDriverResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {

    private static final String DRIVERS_GEO_KEY = "driver_location";

    private final RedisTemplate<String,String> redisTemplate;
    /**
     * update driver location every 3 second
     * called every 3 seconds by driver phone
     Maps to redis geoadd command
     */

    public void updateDriverLocation(DriverLocationRequest driverLocationRequest) {

        log.info("updation location for driver :{}",driverLocationRequest.getDriverId());


        Point driverPoint  = new Point(driverLocationRequest.getLongitude(), driverLocationRequest.getLatitude());
        redisTemplate.opsForGeo().add(DRIVERS_GEO_KEY,driverPoint,driverLocationRequest.getDriverId());

        log.info("location is updated for driver :{}",driverLocationRequest.getDriverId());

    }


    /**

     * get nearby drivers within radius
     * called by matching service
     * Maps to redis FEORADIUS command
     */
    public List<NearByDriverResponse> getNearByDrivers(double latitud, double longitude, double radiusInKm) {

        log.info("finding drivers near lat :{} lat :{} withing {} Km ",latitud,longitude,radiusInKm);

        Circle searchArea = new Circle(new Point(longitude, latitud),
                new Distance(radiusInKm, org.springframework.data.geo.Metrics.KILOMETERS));

        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo().radius(DRIVERS_GEO_KEY, searchArea, RedisGeoCommands.GeoRadiusCommandArgs
                        .newGeoRadiusArgs()
                        .includeCoordinates().includeDistance()
                        .sortAscending()
                        .limit(10)
                );
         List<NearByDriverResponse> nearByDrivers = new ArrayList<>();

        if (results != null){
            results.getContent().forEach(result->{
                RedisGeoCommands.GeoLocation<String> location = result.getContent();
                nearByDrivers.add(new NearByDriverResponse(location.getName(),
                location.getPoint().getX(),
                        location.getPoint().getY(),
                        result.getDistance().getValue()
                ));
            });
        }


        return nearByDrivers;
    }


    public void removeDriver(String driverID) {
    }
}
