package com.cdf.texasholdem.service;

import com.cdf.texasholdem.mapper.PersonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    @Autowired
    PersonMapper personMapper;

    /**
     * 检查用户名是否可用
     * 检测数据库中是否已有相同数据
     * @return true 可用 false 不可用
     */
    public boolean checkUserid(String userid) {
        int i = personMapper.ifPerson(userid);
        return i == 0;
    }
}
