package com.example.debriserver.core.Curri;

import com.example.debriserver.core.Curri.Model.PostCurriCreateReq;
import com.example.debriserver.core.Curri.Model.PostCurriCreateRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class CurriDao {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public void getDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public PostCurriCreateRes createCurri(PostCurriCreateReq postCurriCreateReq){

        // Curri 테이블에 데이터 저장
        String insertQuery = "";
        Object[] insertCurriParameters = new Object[]{
                postCurriCreateReq.getCurriName(),
                postCurriCreateReq.getCurriAuthor(),
                postCurriCreateReq.getVisibleStatus(),
                postCurriCreateReq.getLangTag(),
                postCurriCreateReq.getOwnerIdx()
        };
        this.jdbcTemplate.update(insertQuery,insertCurriParameters);

        // 선택한 Lecture의 정보를 Material 테이블과 연결
        String getLetureQuery = "";

    }
}