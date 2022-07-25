package com.example.debriserver.core.Board;

import com.example.debriserver.core.Board.model.GetScrapBoardCountRes;
import com.example.debriserver.core.Board.model.GetUnscrapBoardListRes;
import com.example.debriserver.core.Board.model.GetScrapBoardListRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class BoardDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int checkBoardExist(int boardIdx) {
        String checkBoardExistQuery = "SELECT EXISTS(SELECT boardIdx FROM Board WHERE boardIdx = ? AND status = 'ACTIVE')";
        int checkBoardExistParams = boardIdx;
        return this.jdbcTemplate.queryForObject(checkBoardExistQuery,
                int.class,
                checkBoardExistParams);
    }

    public int checkUserExist(int userIdx){
        String checkUserExistQuery = "SELECT EXISTS(SELECT userIdx FROM User WHERE userIdx = ? AND status = 'ACTIVE')";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);
    }


    public int checkBoardSubsExist(int boardIdx, int userIdx) {
        String checkBoardSubsExistQuery = "SELECT EXISTS(SELECT boardIdx FROM BoardSubscription WHERE boardIdx = ? AND userIdx = ?)";
        Object [] checkBoardSubsExistParams = new Object[] {boardIdx, userIdx};
        return this.jdbcTemplate.queryForObject(checkBoardSubsExistQuery,
                int.class,
                checkBoardSubsExistParams);
    }

    public int insertBoardSubs(int boardIdx, int userIdx) {
        String insertBoardSubsQuery = "INSERT INTO BoardSubscription(boardIdx, userIdx) VALUES (?,?)";
        Object [] insertBoardSubsParams = new Object[] {boardIdx, userIdx};
        return this.jdbcTemplate.update(insertBoardSubsQuery, insertBoardSubsParams);
    }

    public int scrapBoard(int boardIdx, int userIdx) {
        String scrapBoardQuery = "UPDATE BoardSubscription SET status = 'ACTIVE' WHERE boardIdx = ? and userIdx = ?";
        Object [] scrapBoardParams = new Object[] {boardIdx, userIdx};
        return this.jdbcTemplate.update(scrapBoardQuery, scrapBoardParams);
    }

    public int cancelScrapBoard(int boardIdx, int userIdx) {
        String scrapBoardQuery = "UPDATE BoardSubscription SET status = 'INACTIVE' WHERE boardIdx = ? and userIdx = ?";
        Object [] scrapBoardParams = new Object[] {boardIdx, userIdx};
        return this.jdbcTemplate.update(scrapBoardQuery, scrapBoardParams);
    }

    public List<GetScrapBoardListRes> getScrapBoardList(int userIdx) {
        String getBoardListQuery = "SELECT b.boardIdx, b.boardName, b.boardAdmin, b.createdAt, b.updatedAt, bs.status\n" +
                "FROM Board AS b\n" +
                "LEFT JOIN(SELECT boardIdx, userIdx, status FROM BoardSubscription) bs ON b.boardIdx = bs.boardIdx\n" +
                "WHERE bs.userIdx = ? and b.status = 'ACTIVE' and bs.status = 'ACTIVE'";
             
        int getBoardListParams = userIdx;

        return this.jdbcTemplate.query(getBoardListQuery,
                (rs, rowNum) -> new GetScrapBoardListRes(
                        rs.getInt("boardIdx"),
                        rs.getString("boardName"),
                        rs.getString("boardAdmin"),
                        rs.getString("createdAt"),
                        rs.getString("updatedAt"),
                        rs.getString("status")
                ), getBoardListParams);
    }

    public List<GetUnscrapBoardListRes> getList(int userIdx) {
        String getListQuery = "SELECT distinct b.boardIdx, b.boardName, b.boardAdmin, b.createdAt, b.updatedAt\n" +
                "FROM Board as b\n" +
                "WHERE NOT EXISTS(\n" +
                "    SELECT DISTINCT bs.boardIdx\n" +
                "    FROM BoardSubscription as bs\n" +
                "    JOIN User as u\n" +
                "    WHERE u.userIdx = ? and u.userIdx = bs.userIdx and bs.status = 'ACTIVE' and bs.boardIdx = b.boardIdx\n" +
                ");;";

        return this.jdbcTemplate.query(getListQuery,
                (rs, rowNum) -> new GetUnscrapBoardListRes(
                        rs.getInt("boardIdx"),
                        rs.getString("boardName"),
                        rs.getString("boardAdmin"),
                        rs.getString("createdAt"),
                        rs.getString("updatedAt")
                ), userIdx);
    }


    public int countScrapBoardList(int userIdx) {
        String countScrapQuery = "SELECT COUNT(boardIdx) FROM BoardSubscription WHERE userIdx = ? and status = 'ACTIVE';";

        return this.jdbcTemplate.queryForObject(countScrapQuery, int.class, userIdx);
  }
  
    /**
     * true면 없음 ->
     * false만 있음
     * */
    public boolean checkUnscrapExist(int boardIdx, int userIdx) {
        String checkQuery = "SELECT EXISTS(SELECT boardIdx FROM BoardSubscription WHERE boardIdx = ? AND userIdx = ? AND status = 'INACTIVE');";

        Object[] parameters = new Object[] {
                boardIdx,
                userIdx
        };

        int result = this.jdbcTemplate.queryForObject(checkQuery, int.class, parameters);

        return result == 0;

    }
}
