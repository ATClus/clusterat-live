package com.clusterat.live;

import com.clusterat.live.mcp.RentalPropertyMCPService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider RentalPropertiesTools(RentalPropertyMCPService rentalPropertyMCPService) {
        return MethodToolCallbackProvider.builder().toolObjects(rentalPropertyMCPService).build();
    }
}
