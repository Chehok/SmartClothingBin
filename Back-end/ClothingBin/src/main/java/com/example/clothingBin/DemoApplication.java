package com.example.clothingBin;

import com.example.clothingBin.src.dao.user.UserRepository;
import com.example.clothingBin.src.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);

//        // 메모리 사용량 출력
//        long heapSize = Runtime.getRuntime().totalMemory();
//        System.out.println("HEAP Size(M) : "+ heapSize / (1024*1024) + " MB");

    }

}
