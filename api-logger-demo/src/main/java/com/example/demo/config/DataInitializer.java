package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 * 
 * @author 示例开发者
 * @since 1.0.0
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // 初始化一些示例用户数据
        if (userRepository.count() == 0) {
            userRepository.save(new User("张三", "zhangsan@example.com", 25));
            userRepository.save(new User("李四", "lisi@example.com", 30));
            userRepository.save(new User("王五", "wangwu@example.com", 28));
            userRepository.save(new User("赵六", "zhaoliu@example.com", 32));
            userRepository.save(new User("孙七", "sunqi@example.com", 26));
        }
    }
} 