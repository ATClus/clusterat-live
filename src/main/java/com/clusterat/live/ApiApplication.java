package com.clusterat.live;

import com.clusterat.live.mcp.RentalPropertyMCPService;
import com.clusterat.live.mcp.SearXNGMCPService;
import com.clusterat.live.mcp.WorkoutMCPService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider RentalPropertiesTools(RentalPropertyMCPService rentalPropertyMCPService) {
        return MethodToolCallbackProvider.builder().toolObjects(rentalPropertyMCPService).build();
    }

    @Bean
    public ToolCallbackProvider SearXNGTools(SearXNGMCPService searXNGMCPService) {
        return MethodToolCallbackProvider.builder().toolObjects(searXNGMCPService).build();
    }

    @Bean
    public ToolCallbackProvider WorkoutTools(WorkoutMCPService workoutMCPService) {
        return MethodToolCallbackProvider.builder().toolObjects(workoutMCPService).build();
    }
}
