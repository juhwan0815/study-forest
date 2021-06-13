package com.study.studyservice.client;

import com.study.studyservice.model.location.response.LocationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "location-service")
public interface LocationServiceClient {

    @GetMapping("/locations/code")
    LocationResponse findLocationByCode(@RequestParam("code") String code);

    @GetMapping("/locations/{locationId}")
    LocationResponse findLocationById(@PathVariable Long locationId);
}
