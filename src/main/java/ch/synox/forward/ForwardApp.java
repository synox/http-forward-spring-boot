package ch.synox.forward;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

import javax.servlet.Servlet;

@SpringBootConfiguration
@EnableAutoConfiguration
public class ForwardApp {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ForwardApp.class, args);
    }

    @Bean
    public Servlet dispatcherServlet() {
        return new ForwardServlet("https://en.wikipedia.org");
    }

}
