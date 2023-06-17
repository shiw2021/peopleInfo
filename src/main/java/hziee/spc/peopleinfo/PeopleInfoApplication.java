package hziee.spc.peopleinfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan("hziee.spc.peopleinfo.mapper")
//@ComponentScan("hziee.spc.peopleinfo.controller")
public class PeopleInfoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PeopleInfoApplication.class, args);
    }

}
