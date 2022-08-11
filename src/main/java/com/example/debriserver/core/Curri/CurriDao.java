package com.example.debriserver.core.Curri;

import com.example.debriserver.core.Curri.model.PostCurriScrapRes;
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

    /**
     * 커리큘럼 스크랩
     * @param curriIdx
     * @param userIdx
     * @return
     */
    public PostCurriScrapRes scrapCurri(int curriIdx, int userIdx) {

        String insertQuery = "INSERT INTO Curriculum (curriName,curriAuthor,visibleStatus,langTag,progressRate,status,scraped,ownerIdx) " +
                "SELECT curriName,curriAuthor,visibleStatus,langTag,progressRate,status,scraped,ownerIdx FROM Curriculum where curriIdx = ?";
        int insertParams = curriIdx;

        this.jdbcTemplate.update(insertQuery,insertParams);

        String updateQuery = "UPDATE Curriculum SET progressRate = 0, scraped = 'TRUE', ownerIdx = ? WHERE curriIdx = (SELECT LAST_INSERT_ID()) ";
        int updateParams = userIdx;

        this.jdbcTemplate.update(updateQuery,updateParams);

        String getScrapedCurriQuery = "SELECT curriIdx,curriName,curriAuthor,visibleStatus,langTag,progressRate,status,scraped,ownerIdx " +
                "FROM Curriculum WHERE curriIdx = (SELECT LAST_INSERT_ID()) ";


        return this.jdbcTemplate.queryForObject(getScrapedCurriQuery,
                (rs, rowNum) -> new PostCurriScrapRes(
                        rs.getInt("curriIdx"),
                        rs.getString("curriName"),
                        rs.getString("curriAuthor"),
                        rs.getString("visibleStatus"),
                        rs.getString("langTag"),
                        rs.getFloat("progressRate"),
                        rs.getString("status"),
                        rs.getString("scraped"),
                        rs.getInt("ownerIdx")

                ));

    }
    public int scrapCancel(String userId){
        String deleteUserQuery = "UPDATE Curriculum SET status='DELETE' WHERE userId = ? and status = 'ACTIVE'";
        String deleteUserParams = userId;
        return this.jdbcTemplate.update(deleteUserQuery,
                deleteUserParams);
    }

    /**
     * 스크랩한 커리큘럼 존재 유무
     * @param userIdx
     * @return
     */
    public boolean checkScrapedCurriExist(int curriIdx, int userIdx) {
        String checkQuery = "SELECT COUNT(*) FROM Curriculum WHERE curriName = (SELECT curriName FROM Curriculum WHERE curriIdx =?) and curriAuthor = (SELECT curriAuthor FROM Curriculum WHERE curriIdx =?)and scraped = 'TRUE' and ownerIdx =? ";

        int result = this.jdbcTemplate.queryForObject(checkQuery, int.class, curriIdx,curriIdx,userIdx);

        if(result == 0) return false;
        else return true;
    }
}
