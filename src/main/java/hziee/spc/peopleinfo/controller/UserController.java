package hziee.spc.peopleinfo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hziee.spc.peopleinfo.entitey.People;
import hziee.spc.peopleinfo.mapper.PeopleMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class UserController {


    private final PeopleMapper mapper;
    private final ObjectMapper objectMapper;

    public UserController(PeopleMapper mapper, @Qualifier("objectMapper") ObjectMapper objectMapper) {
        this.mapper = mapper;
        this.objectMapper = objectMapper;
    }

    //模糊查询，可选的参数有name，gender，idType，idNumber，birthday，phone，email，以及一个关键字value
    //分页查询，必须的参数有page，rows
    //return data like this:
    //{"total":"142","rows":[{"id":"1060","firstname":"\u5b9e\u6253\u5b9e\u7684","lastname":"\u963f\u8428213","phone":"\u5927\u8d5b","email":"11@123.com"},{"id":"1061","firstname":"nbnbnbnb","lastname":"nbnbnnbn","phone":"1234213","email":"21321@163.com"},{"id":"1062","firstname":"nbnbnbnb","
    @PostMapping(value = "/user/get"/*, produces = "text/html"*/)
    public ResponseEntity<Object> searchUser(@RequestParam(required = false, name = "name", defaultValue = "name") String name,
                                             @RequestParam(required = false, name = "value", defaultValue = "") String value,
                                                @RequestParam(required = false, name = "page", defaultValue = "0") int page,
                                                @RequestParam(required = false, name = "rows", defaultValue = "0") int rows) throws JsonProcessingException {


        Iterable<People> peopleIterable = mapper.selectByValue(name, value, (page-1)*rows, rows);
        Map<String,Object> map = new HashMap<>();


        map.put("total",mapper.count());
        map.put("rows",peopleIterable);
        //返回的请求体格式为text/html

        //将map转换为json格式

        String json = objectMapper.writeValueAsString(map);

        return ResponseEntity.ok(json);
    }


    @PostMapping(value = "user/post", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> saveUser(
            @RequestParam("name") String name,
            @RequestParam("namePinYin") String namePinYin,
            @RequestParam("gender") String gender,
            @RequestParam("idType") String idType,
            @RequestParam("idNumber") String idNumber,
            @RequestParam("birthday") LocalDate birthday,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email
    ) {
        People people = new People();

        people.setId(UUID.randomUUID().toString().replace("-", ""));
        System.out.println(people.getId());
        people.setName(name);
        people.setNamePinYin(namePinYin);
        people.setGender(gender);
        people.setIdType(idType);
        people.setIdNumber(idNumber);
        people.setBirthday(birthday);
        people.setPhone(phone);
        people.setEmail(email);
        people.setCreatetime(LocalDateTime.now());
        people.setUpdatetime(LocalDateTime.now());

        try {
            mapper.save(people);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("save failed");
        }
        return ResponseEntity.ok("saved success");
    }

    @PostMapping("/user/delete/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") String id) {
        try {
            mapper.delete(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("delete failed");
        }
        return ResponseEntity.status(HttpStatus.OK).body("delete success");
    }

    @PostMapping("/user/edit/{id}")
    public ResponseEntity<Object> editUser(@PathVariable("id") String id,
                                           @RequestParam("name") String name,
                                           @RequestParam("namePinYin") String namePinYin,
                                           @RequestParam("gender") String gender,
                                           @RequestParam("idType") String idType,
                                           @RequestParam("idNumber") String idNumber,
                                           @RequestParam("birthday") LocalDate birthday,
                                           @RequestParam("phone") String phone,
                                           @RequestParam("email") String email) {

        // 创建一个用户对象
        People people = new People();
        // 为用户对象设置相应的属性

        people.setName(name);
        people.setNamePinYin(namePinYin);
        people.setGender(gender);
        people.setIdType(idType);
        people.setIdNumber(idNumber);
        people.setBirthday(birthday);
        people.setPhone(phone);
        people.setEmail(email);
        people.setUpdatetime(LocalDateTime.now());
        people.setId(id);
        try {
            mapper.update(people);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("edit failed");
        }
        return ResponseEntity.status(HttpStatus.OK).body("edit success");
    }


}
