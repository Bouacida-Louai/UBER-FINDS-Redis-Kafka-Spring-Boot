package com.louaidev.locationservice.controller;

import com.louaidev.locationservice.dto.DriverLocationRequest;
import com.louaidev.locationservice.dto.NearByDriverResponse;
import com.louaidev.locationservice.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/locations")
@Slf4j
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    //driver Phone calls this every 3 seconds
    @PostMapping("/driver/update")
    public ResponseEntity<String> updateDriverLOcation(@RequestBody DriverLocationRequest driverLocationRequest){

        locationService.updateDriverLocation(driverLocationRequest);
        return ResponseEntity.ok("Location updated successfully");

    }

//matching service calls this when ride is requested
    @GetMapping("/drivers/nearby")
    public ResponseEntity<List<NearByDriverResponse>> getNearByDrivers(
            @RequestParam double latitud ,
            @RequestParam double longitude,
            @RequestParam (defaultValue = "5") double radius
    ){

        return ResponseEntity.ok(locationService.getNearByDrivers(latitud,longitude,radius));
    }

    //when driver goes offline

    @DeleteMapping("/drivers/{driverID}")
    public ResponseEntity<String> removeDriver(@PathVariable String driverID){

        locationService.removeDriver(driverID);
        return ResponseEntity.ok("Driver removed successfully");

    }



}
