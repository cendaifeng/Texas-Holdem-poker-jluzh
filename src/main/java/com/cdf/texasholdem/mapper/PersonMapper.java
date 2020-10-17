package com.cdf.texasholdem.mapper;

import com.cdf.texasholdem.bean.Person;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PersonMapper {

    @Select("select * from person where id=#{id}")
    public Person getPersonById(String id);

    @Delete("delete from person where id=#{id}")
    public int deletePersonById(String id);

//    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into person(id, password, name) values(#{id}, #{password}, #{name})")
    public int insertPerson(Person person);

    @Update("update person set UUID=#{UUID} where id=#{id}")
    public int updatePersonUUID(Person person);

    @Update("update person set bankroll=#{bankRoll} where id=#{id}")
    public int updateBankRoll(Person person);
}
