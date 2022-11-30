package com.example.debriserver.core.Post;

import com.example.debriserver.core.Post.model.GetScrapRes;
import com.example.debriserver.core.Post.model.PostPostsReq;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.core.Post.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PostDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int insertPosts(PostPostsReq postPostsReq){
        String insertPostQuery = "INSERT INTO Post(boardIdx, userIdx, postContent, postName) VALUES(?,?,?,?)";
        Object []insertPostParams = new Object[]{
                postPostsReq.getBoardIdx(),
                postPostsReq.getUserIdx(),
                postPostsReq.getPostContent(),
                postPostsReq.getPostName()
        };
        this.jdbcTemplate.update(insertPostQuery, insertPostParams);

        String lastInsertIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery, int.class);

    }

    /*public int insertPostsImgs(int postIdx, PostImgUrlReq postImgUrlReq){
        String insertPostImgsQuery = "INSERT INTO PostImage(postIdx, url) VALUES(?,?)";
        Object []insertPostImgsParams = new Object[]{postIdx, postImgUrlReq.getUrl()};
        this.jdbcTemplate.update(insertPostImgsQuery, insertPostImgsParams);

        String lastInsertIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery, int.class);

    }*/

    public int updatePost(int postIdx, String postContent){
        String updatePostQuery = "UPDATE Post SET postContent=? WHERE postIdx=?";
        Object []updatePostParams = new Object[]{postContent, postIdx};
        return this.jdbcTemplate.update(updatePostQuery, updatePostParams);
    }

    public int checkUserExist(int userIdx){
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ? and status = 'ACTIVE')";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);

    }

    public int checkPostExist(int postIdx){
        String checkPostExistQuery = "select exists(select postIdx from Post where postIdx = ? and status = 'ACTIVE')";
        int checkPostExistParams = postIdx;
        return this.jdbcTemplate.queryForObject(checkPostExistQuery,
                int.class,
                checkPostExistParams);

    }

    public int checkPostMarkedExist(int postIdx, int userIdx)
    {
        String checkPostMarkedExistQuery = "select exists(select postIdx from PostMarked where postIdx = ? and userIdx = ?)";
        Object[] checkPostMarkedExistParams = new Object[]{
                postIdx,
                userIdx
        };
        return this.jdbcTemplate.queryForObject(checkPostMarkedExistQuery,
                int.class,
                checkPostMarkedExistParams);
    }

    /**
     * 있으면 TRUE, 없으면 FALSE
     * */
    public boolean checkBoardExist(int boardIdx){
        String checkBoardQuery = "SELECT exists(SELECT boardIdx  FROM Board WHERE boardIdx = ?);";

        int result = this.jdbcTemplate.queryForObject(checkBoardQuery, int.class, boardIdx);

        return result != 0;
    }

    public int deletePost(int postIdx){
        String deletePostQuery = "UPDATE Post SET status='DELETE' WHERE postIdx=?";
        int deletePostParams = postIdx;
        return this.jdbcTemplate.update(deletePostQuery,
                deletePostParams);
    }

    /**
     * PostMarked 테이블에 스크랩 된 상태로 데이터 추가
     **/
    public int insertPostMarked(int postIdx, int userIdx)
    {
        String insertPostMarkedQuery = "INSERT INTO PostMarked(postIdx, userIdx) VALUES(?, ?)";
        Object[] insertPostMarkedParams = new Object[]{
                postIdx,
                userIdx
        };
        return this.jdbcTemplate.update(insertPostMarkedQuery,
                insertPostMarkedParams);
    }

    /**
     * 스크랩 설정
     **/
    public int scrapPost(int postIdx, int userIdx)
    {
        String scrapPostQuery = "UPDATE PostMarked SET status = 'ACTIVE' WHERE postIdx = ? and userIdx = ?";
        Object[] scrapPostParams = new Object[]{
                postIdx,
                userIdx
        };
        return this.jdbcTemplate.update(scrapPostQuery,
                scrapPostParams);
    }


    /**
     * 스크랩 취소
     **/
    public int unScrapPost(int postIdx, int userIdx)
    {
        String unScrapPostQuery = "UPDATE PostMarked SET status = 'INACTIVE' WHERE postIdx = ? and userIdx = ?";
        Object[] unScrapPostParams = new Object[]{
                postIdx,
                userIdx
        };
        return this.jdbcTemplate.update(unScrapPostQuery,
                unScrapPostParams);
    }

    /**
     * 유저가 스크랩한 글
     **/
    public List<GetScrapRes> getScrapPosts(int userIdx, int pageNum)
    {
        int idx = 12 * (pageNum - 1);

        String getScrapPostsQuery = "SELECT p.postIdx, p.boardIdx, u.nickname, p.postName, pl.likeStatus, pm.status as scrapStatus,\n" +
                "       (SELECT COUNT(postIdx) FROM PostLike WHERE postIdx = p.postIdx and likeStatus = 'LIKE') as cntPost,\n" +
                "       TIMESTAMPDIFF(minute, (SELECT createdAt FROM Post WHERE postIdx = p.postIdx), CURRENT_TIMESTAMP) as postCreatedAt,\n" +
                "       (SELECT COUNT(commentIdx) FROM Comment WHERE postIdx = p.postIdx and status = 'ACTIVE') as cntComment,\n" +
                "       (SELECT boardName FROM Board WHERE boardIdx = p.boardIdx) as boardName\n" +
                "FROM Post as p\n" +
                "    LEFT JOIN ReportedUser as ru ON ru.reportedUserIdx = p.userIdx AND ru.reportUserIdx = " + userIdx + " AND ru.status = 'BLOCK'\n" +
                "    LEFT JOIN PostLike as pl on p.postIdx = pl.postIdx AND pl.userIdx = " + userIdx + "\n" +
                "    LEFT JOIN PostMarked pm On p.postIdx = pm.postIdx AND pm.userIdx = " + userIdx + "\n" +
                "    LEFT JOIN User as u on p.userIdx = u.userIdx\n" +
                "WHERE p.status = 'ACTIVE' AND pm.status = 'ACTIVE' AND reportedUserIdx is null\n" +
                "order by p.postIdx LIMIT ?, 12";

        return this.jdbcTemplate.query(getScrapPostsQuery,
                (rs, rowNum) -> new GetScrapRes
                        (
                            rs.getInt("postIdx"),
                            rs.getInt("boardIdx"),
                            rs.getString("nickName"),
                            rs.getString("postName"),
                            rs.getString("likeStatus"),
                            rs.getString("scrapStatus"),
                            rs.getInt("cntPost"),
                            rs.getInt("postCreatedAt"),
                            rs.getInt("cntComment"),
                            rs.getString("boardName")
                        ),
                idx);
    }

    public int insertPostLike(PostPostLikeReq postPostLikeReq) {
        String insertPostLikeQuery = "INSERT INTO PostLike(postIdx, userIdx, likeStatus) VALUES(?,?,?)";
        String checkExistQuery = "SELECT EXISTS(SELECT postIdx FROM PostLike WHERE postIdx = ? and userIdx = ?);";
        String updatePostLikeQuery = "UPDATE PostLike SET likeStatus = 'LIKE' WHERE postIdx = ? and userIdx = ?;";

        Object[] insertPostLikeParams = new Object[]{
                postPostLikeReq.getPostIdx(),
                postPostLikeReq.getUserIdx(),
                postPostLikeReq.getLikeStatus()
        };

        Object[] checkExistParameters = new Object[]{
                postPostLikeReq.getPostIdx(),
                postPostLikeReq.getUserIdx()
        };

        int check = this.jdbcTemplate.queryForObject(checkExistQuery, int.class, checkExistParameters);

        if(check == 0) return this.jdbcTemplate.update(insertPostLikeQuery, insertPostLikeParams);
        else return this.jdbcTemplate.update(updatePostLikeQuery, checkExistParameters);
    }

    public int deletePostLike(int postIdx, int userIdx) {

        String insertPostLikeQuery = "UPDATE PostLike SET likeStatus = 'NULL' WHERE postIdx=? and userIdx = ?";

        Object[] insertPostLikeParams = new Object[]{
                postIdx,
                userIdx
        };

        return this.jdbcTemplate.update(insertPostLikeQuery, insertPostLikeParams);
    }
    
    public List<GetPostListRes> getPostList(int userIdx, int boardIdx, int pageNum){
        int idx = 12 * (pageNum - 1);

        String getListQuery = "SELECT distinct p.boardIdx, p.postIdx, p.userIdx, u.nickname, p.postName, pl.likeStatus,\n" +
                "                pm.status as scrapStatus, b.boardName,\n" +
                "                (SELECT COUNT(postIdx) FROM PostLike WHERE postIdx = p.postIdx and likeStatus = 'LIKE') as contPostLike,\n" +
                "                (SELECT TIMESTAMPDIFF(minute, (SELECT createdAt FROM Post WHERE postIdx = p.postIdx), CURRENT_TIMESTAMP)) as postCreatedAt,\n" +
                "                (SELECT COUNT(commentIdx) FROM Comment WHERE postIdx = p.postIdx and status = 'ACTIVE') as commentCont\n" +
                "FROM Post as p\n" +
                "         LEFT JOIN User as u ON p.userIdx = u.userIdx\n" +
                "         LEFT JOIN ReportedUser as ru ON ru.reportedUserIdx = p.userIdx AND ru.reportUserIdx = " + userIdx + " AND ru.status = 'BLOCK'\n" +
                "         LEFT JOIN PostLike as pl ON p.postIdx = pl.postIdx AND pl.userIdx = " + userIdx + "\n" +
                "         LEFT JOIN PostMarked pm On p.postIdx = pm.postIdx AND pm.userIdx = " + userIdx + "\n" +
                "         LEFT JOIN Board as b on p.boardIdx = b.boardIdx\n" +
                "WHERE p.status = 'ACTIVE' AND b.boardIdx = ? AND reportedUserIdx is null\n" +
                "order by p.postIdx LIMIT ?, 12";

        return this.jdbcTemplate.query(getListQuery,
                (rs, rowNum) -> new GetPostListRes
                        (
                                rs.getInt("boardIdx"),
                                rs.getInt("postIdx"),
                                rs.getString("nickName"),
                                rs.getString("postName"),
                                rs.getInt("contPostLike"),
                                rs.getString("likeStatus"),
                                rs.getString("scrapStatus"),
                                rs.getInt("postCreatedAt"),
                                rs.getInt("commentCont"),
                                rs.getString("boardName")
                        ),
                boardIdx, idx);
    }

    public List<GetPostSearchListRes> getPostSearchList(int userIdx, String keyword, int pageNum){
        int idx = 12 * (pageNum - 1);

        String getPostSearchListQuery = "SELECT distinct p.boardIdx, p.postIdx, p.userIdx, u.nickname, p.postName, pl.likeStatus,\n" +
                "                pm.status as scrapStatus, b.boardName,\n" +
                "                (SELECT COUNT(postIdx) FROM PostLike WHERE postIdx = p.postIdx and likeStatus = 'LIKE') as contPostLike,\n" +
                "                (SELECT TIMESTAMPDIFF(minute, (SELECT createdAt FROM Post WHERE postIdx = p.postIdx), CURRENT_TIMESTAMP)) as postCreatedAt,\n" +
                "                (SELECT COUNT(commentIdx) FROM Comment WHERE postIdx = p.postIdx and status = 'ACTIVE') as commentCont\n" +
                "FROM Post as p\n" +
                "    LEFT JOIN User as u ON p.userIdx = u.userIdx\n" +
                "    LEFT JOIN ReportedUser as ru ON ru.reportedUserIdx = p.userIdx AND ru.reportUserIdx = " + userIdx + " AND ru.status = 'BLOCK'\n" +
                "    LEFT JOIN PostLike as pl ON p.postIdx = pl.postIdx AND pl.userIdx = " + userIdx + "\n" +
                "    LEFT JOIN PostMarked pm On p.postIdx = pm.postIdx AND pm.userIdx = " + userIdx + "\n" +
                "    LEFT JOIN Board as b on p.boardIdx = b.boardIdx\n" +
                "WHERE p.status = 'ACTIVE' AND p.postName like '" + keyword + "%' AND reportedUserIdx is null\n" +
                "order by p.postIdx LIMIT ?, 12";

        return this.jdbcTemplate.query(getPostSearchListQuery,
                (rs, rowNum) -> new GetPostSearchListRes
                        (
                                rs.getInt("boardIdx"),
                                rs.getInt("postIdx"),
                                rs.getString("nickName"),
                                rs.getString("postName"),
                                rs.getInt("contPostLike"),
                                rs.getString("likeStatus"),
                                rs.getString("scrapStatus"),
                                rs.getInt("postCreatedAt"),
                                rs.getInt("commentCont"),
                                rs.getString("boardName")
                        )
                , idx);
    }

    public GetPostRes getPost(int postIdx, int userIdx){
        String getPostQuery =
                "SELECT Post.boardIdx, B.boardName, Post.postIdx, Post.postName, Post.postContent, Post.userIdx as authorIdx, U.nickname,\n" +
                        "(SELECT COUNT(postIdx) FROM PostLike WHERE postIdx = ? and likeStatus = 'LIKE') as likeNum,\n" +
                        "(SELECT COUNT(commentIdx) FROM Comment WHERE postIdx = ? and status = 'ACTIVE') as commentNum,\n" +
                        "(SELECT IFNULL(max(likeStatus), 'NULL') as likeStatus FROM PostLike WHERE postIdx = ? and userIdx = ?) as likeStatus,\n" +
                        "(SELECT IFNULL(max(status), 'INACTIVE') as scrapStatus FROM PostMarked WHERE postIdx = ? and userIdx = ?) as scrapStatus,\n" +
                        "TIMESTAMPDIFF(minute, Post.createdAt, CURRENT_TIMESTAMP) as createdDate\n" +
                        "FROM\n" +
                        "Post left join User U on Post.userIdx = U.userIdx\n" +
                        "left join Board B on Post.boardIdx = B.boardIdx\n" +
                        "WHERE Post.postIdx = ? and Post.status = 'ACTIVE';";

        Object[] getPostParameters = new Object[]{
                postIdx,
                postIdx,
                postIdx,
                userIdx,
                postIdx,
                userIdx,
                postIdx,
        };
        
        return this.jdbcTemplate.queryForObject(getPostQuery,
                (rs, rowNum) -> new GetPostRes
                        (
                                rs.getInt("boardIdx"),
                                rs.getInt("postIdx"),
                                rs.getInt("authorIdx"),
                                rs.getString("postName"),
                                rs.getString("nickname"),
                                rs.getString("postContent"),
                                rs.getInt("likeNum"),
                                rs.getInt("createdDate"),
                                rs.getInt("commentNum"),
                                rs.getString("scrapStatus").equalsIgnoreCase("ACTIVE"),
                                rs.getString("likeStatus").equalsIgnoreCase("LIKE"),
                                rs.getString("boardName")
                        ),
                getPostParameters);
    }

    public List<GetPostListRes> getBoardPostList(String key, int boardIdx, int userIdx, int pageNum) {
        int idx = 12 * (pageNum - 1);

        String getPostSearchListQuery = "SELECT distinct p.boardIdx, p.postIdx, p.userIdx, u.nickname, p.postName, pl.likeStatus,\n" +
                "                pm.status as scrapStatus, b.boardName,\n" +
                "                (SELECT COUNT(postIdx) FROM PostLike WHERE postIdx = p.postIdx and likeStatus = 'LIKE') as contPostLike,\n" +
                "                (SELECT TIMESTAMPDIFF(minute, (SELECT createdAt FROM Post WHERE postIdx = p.postIdx), CURRENT_TIMESTAMP)) as postCreatedAt,\n" +
                "                (SELECT COUNT(commentIdx) FROM Comment WHERE postIdx = p.postIdx and status = 'ACTIVE') as commentCont\n" +
                "FROM Post as p\n" +
                "    LEFT JOIN User as u ON p.userIdx = u.userIdx\n" +
                "    LEFT JOIN ReportedUser as ru ON ru.reportedUserIdx = p.userIdx AND ru.reportUserIdx = " + userIdx + " AND ru.status = 'BLOCK'\n" +
                "    LEFT JOIN PostLike as pl ON p.postIdx = pl.postIdx AND pl.userIdx = " + userIdx + "\n" +
                "    LEFT JOIN PostMarked pm On p.postIdx = pm.postIdx AND pm.userIdx = " + userIdx + "\n" +
                "    LEFT JOIN Board as b on p.boardIdx = b.boardIdx\n" +
                "WHERE p.status = 'ACTIVE' AND p.postName like '" + key + "%' AND b.boardIdx = ? AND reportedUserIdx is null\n" +
                "order by p.postIdx LIMIT ?, 12";

        return this.jdbcTemplate.query(getPostSearchListQuery,
                (rs, rowNum) -> new GetPostListRes
                        (
                                rs.getInt("boardIdx"),
                                rs.getInt("postIdx"),
                                rs.getString("nickName"),
                                rs.getString("postName"),
                                rs.getInt("contPostLike"),
                                rs.getString("likeStatus"),
                                rs.getString("scrapStatus"),
                                rs.getInt("postCreatedAt"),
                                rs.getInt("commentCont"),
                                rs.getString("boardName")
                        ), boardIdx, idx);
    }
}
