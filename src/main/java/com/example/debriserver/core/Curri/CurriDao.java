package com.example.debriserver.core.Curri;

import com.example.debriserver.core.Curri.Model.GetScrapListRes;
import com.example.debriserver.core.Curri.Model.PostCurriScrapRes;
import com.example.debriserver.core.Lecture.Model.GetLectureListRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

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

        // 변경

        if(checkUnScrapedCurriExist2(curriIdx,userIdx)==true){
            rescrap(curriIdx,userIdx);

        }else {
            String insertQuery = "INSERT INTO CurriScrap (curriIdx, scrapUserIdx, status) VALUES ((SELECT curriIdx FROM Curriculum WHERE Curriculum.curriIdx=?) , ? , 'ACTIVE')";
            int insertParams = curriIdx;
            int insertParams2 = userIdx;

            this.jdbcTemplate.update(insertQuery, insertParams, insertParams2);
        }
        String getScrapedCurriQuery = "SELECT distinctrow A.* FROM Curriculum as A INNER JOIN CurriScrap as B ON A.curriIdx = B.curriIdx WHERE B.curriIdx= ? and B.status = 'ACTIVE' and A.status = 'ACTIVE' and B.scrapUserIdx = ?";

        return this.jdbcTemplate.queryForObject(getScrapedCurriQuery,
                (rs, rowNum) -> new PostCurriScrapRes(
                        rs.getInt("curriIdx"),
                        rs.getString("curriName"),
                        rs.getString("curriAuthor"),
                        rs.getString("visibleStatus"),
                        rs.getString("langTag"),
                        rs.getFloat("progressRate"),
                        rs.getString("status"),
                        rs.getInt("ownerIdx")

                ),curriIdx ,userIdx);

    }

    /**
     * 스크랩취소
     * @param scrapIdx
     * @return
     */
    public int scrapCancel(int scrapIdx){
        String deleteUserQuery = "UPDATE CurriScrap SET status='INACTIVE' WHERE scrapIdx = ? and status = 'ACTIVE'";
        int deleteUserParams = scrapIdx;
        return this.jdbcTemplate.update(deleteUserQuery,
                deleteUserParams);
    }

    /**
     * 스크랩한 커리큘럼 존재 유무
     * @param userIdx
     * @return
     */
    public boolean checkScrapedCurriExist(int curriIdx, int userIdx) {
        String checkQuery = "SELECT COUNT(*) FROM CurriScrap WHERE curriIdx =? and scrapUserIdx =? and status ='ACTIVE'";

        int result = this.jdbcTemplate.queryForObject(checkQuery, int.class, curriIdx, userIdx);

        if(result == 0) return false;
        else return true;
    }

    /**
     * 스크랩취소된 커리큘럼 존재유무
     * @param scrapIdx
     * @return
     */
    public boolean checkUnScrapedCurriExist(int scrapIdx) {
        String checkQuery = "SELECT COUNT(*) FROM CurriScrap WHERE scrapIdx = ? and status = 'INACTIVE'";

        int result = this.jdbcTemplate.queryForObject(checkQuery, int.class, scrapIdx);

        if(result == 0) return false;
        else return true;
    }

    public boolean checkUnScrapedCurriExist2(int curriIdx, int userIdx) {
        String checkQuery = "SELECT COUNT(*) FROM CurriScrap WHERE curriIdx = ? and scrapUserIdx =? and status = 'INACTIVE'";

        int result = this.jdbcTemplate.queryForObject(checkQuery, int.class, curriIdx, userIdx);

        if(result == 0) return false;
        else return true;
    }

    /**
     * 취소한 스크랩 커리큘럼 되돌리기
     */
    public void rescrap(int curriIdx ,int userIdx) {
        String updateQuery = "UPDATE CurriScrap SET status = 'ACTIVE' WHERE curriIdx = ? and scrapUserIdx = ? and status = 'INACTIVE' ";

        this.jdbcTemplate.update(updateQuery, curriIdx, userIdx);



    }


    /**
     * 커리큘럼 스크랩 리스트 조회
     */
    public List<GetScrapListRes> getCurriScrapList(int userIdx) {
        String getQuery =
                "SELECT A.curriIdx, A.curriName, A.curriAuthor, A.langTag, A.progressRate \n" +
                        "FROM Curriculum as A JOIN CurriScrap as B ON A.curriIdx = B.curriIdx\n" +
                        "WHERE B.status = 'ACTIVE' and A.status = 'ACTIVE' and B.scrapUserIdx = ?;";

        return this.jdbcTemplate.query(getQuery,(rs, rowNum)
                -> new GetScrapListRes(
                rs.getInt("curriIdx"),
                rs.getString("curriName"),
                rs.getString("curriAuthor"),
                rs.getString("langTag"),
                rs.getFloat("progressRate")
        ), userIdx);
    }

    public boolean checkScrapExist(int userIdx) {
        String checkQuery = "SELECT COUNT(*) FROM CurriScrap WHERE scrapUserIdx = ? and status ='ACTIVE' ";

        int result = this.jdbcTemplate.queryForObject(checkQuery, int.class, userIdx);

        if(result !=0)return true;
        else return  false;
    }

}
