package com.example.debriserver.core.Jwt;


import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.core.Jwt.Model.PatchRefreshRes;
import com.example.debriserver.utility.Model.RefreshJwtRes;
import com.example.debriserver.utility.jwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class JwtDao {
    private JdbcTemplate jdbcTemplate;
    private final jwtUtility JwtUtility = new jwtUtility();

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public PatchRefreshRes getRefresh(RefreshJwtRes refreshJwtRes) throws BasicException {
        String refreshToken = refreshJwtRes.getRefreshToken();
        Object[] refreshParameters = new Object[]{
                refreshToken,
                refreshJwtRes.getUserIdx()
        };

        String updateRefreshTokenQuery = "UPDATE User SET jwtRefreshToken = ? WHERE userIdx = ?;";

        this.jdbcTemplate.update(updateRefreshTokenQuery, refreshParameters);
        PatchRefreshRes patchRefreshRes = new PatchRefreshRes();

        patchRefreshRes.setAccessToken(refreshJwtRes.getAccessToken());
        patchRefreshRes.setRefreshToken(refreshJwtRes.getRefreshToken());

        return patchRefreshRes;
    }
}
