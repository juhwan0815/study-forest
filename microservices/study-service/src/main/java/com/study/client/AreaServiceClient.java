package com.study.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "area-service")
public interface AreaServiceClient {

    @GetMapping("/areas/code")
    AreaResponse findByCode(@RequestParam("code") String code);

    @GetMapping("/areas/{areaId}")
    AreaResponse findById(@PathVariable Long areaId);

    @GetMapping("/areas/{areaId}/around")
    List<AreaResponse> findAroundById(@PathVariable Long areaId, @RequestParam Integer distance);
}
