package com.study.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "study-service")
public interface StudyServiceClient {
}
