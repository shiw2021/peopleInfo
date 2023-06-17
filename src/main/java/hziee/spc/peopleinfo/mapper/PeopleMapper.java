package hziee.spc.peopleinfo.mapper;

import hziee.spc.peopleinfo.entitey.People;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PeopleMapper {
    //计算数量
    @Select("select count(*) from table_1")
    public int count();
    @Select("select * from table_1 order by createtime desc")
    @Results(id = "people", value = {
            @Result(property = "id", column = "Column_1"),
            @Result(property = "namePinYin", column = "name_pin_yin"),
            @Result(property = "idType", column = "id_type"),
            @Result(property = "idNumber", column = "id_number"),

    })
    public List<People> selectAll();

    //search people by 任意的 keyword
//    @Select("SELECT * FROM table_1 WHERE ${name} LIKE CONCAT('%',#{value},'%') order by createtime desc")
    @Select("SELECT * FROM table_1 WHERE ${name} LIKE CONCAT('%',#{value},'%') order by createtime desc limit #{page},#{rows}")
    @ResultMap("people")
    List<People> selectByValue(@Param("name") String name, @Param("value") String value, @Param("page") int page, @Param("rows") int rows);
    //save people
    @Insert("INSERT INTO table_1 " +
            "(Column_1, name, name_pin_yin, gender, id_type, id_number, birthday, phone, email, createtime, updatetime) " +
            "VALUES " +
            "(#{id}, #{name}, #{namePinYin}, #{gender}, #{idType}, #{idNumber}, #{birthday}, #{phone}, #{email}, #{createtime}, #{updatetime})")
    void save(People people) throws Exception;

    //delete people
    @Delete("DELETE FROM table_1 WHERE Column_1 = #{id}")
    void delete(String id);

    //update people by id
    @Update("UPDATE table_1 " +
            "SET name = #{name}, name_pin_yin = #{namePinYin}, gender = #{gender}, id_type = #{idType}, " +
            "id_number = #{idNumber}, birthday = #{birthday}, phone = #{phone}, email = #{email}, updatetime = #{updatetime} " +
            "WHERE Column_1 = #{id}")
    void update(People people);


}
