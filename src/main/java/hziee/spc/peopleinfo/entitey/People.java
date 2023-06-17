package hziee.spc.peopleinfo.entitey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data

public class People {

    String id;
    String name;
    private String namePinYin;
    String gender;
    String idType;
    String IdNumber;
    LocalDate birthday;
    String phone;
    String email;
    LocalDateTime createtime;
    LocalDateTime updatetime;
}

