package com.example.iosfileuploader.core.utils.grpc;

import com.example.iosfileuploader.core.utils.SystemParameterManager;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Configuration
public class ManagedChannelService {

    @Autowired
    SystemParameterManager systemParameterManager;
    private ManagedChannel channel;
    @Bean
    public ManagedChannel grpcChannel() {
        String grpcServerDomain = systemParameterManager.getParam("grpcServerDomain", String.class);
        Integer grpcServerPort = systemParameterManager.getParam("grpcServerPort", Integer.class);

        channel = ManagedChannelBuilder.forAddress(grpcServerDomain, grpcServerPort)
                .usePlaintext()
                .build();
        return channel;
    }

    @PreDestroy
    public void cleanup() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown(); // Initiate graceful shutdown
            try {
                if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                    channel.shutdownNow(); // Force shutdown if needed
                }
            } catch (InterruptedException e) {
                channel.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
