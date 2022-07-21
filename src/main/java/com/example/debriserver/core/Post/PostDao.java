package com.example.debriserver.core.Post;

import com.example.debriserver.core.Post.model.GetScrapRes;
import com.example.debriserver.core.Post.model.PostPostsReq;
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


    public int deletePost(int postIdx){
        String deletePostQuery = "UPDATE Post SET status='DELETE' WHERE postIdx=?";
        Object [] deletePostParams = new Object[]{postIdx};
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
    public List<GetScrapRes> getScrapPosts(int userIdx)
    {
        String getScrapPostsQuery = "select PM.postIdx, P.boardIdx, PM.userIdx, P.postContent, P.postName, P.createdAt, P.updatedAt\n" +
                "FROM Post as P\n" +
                "left join(select postIdx, userIdx, status from PostMarked) PM on P.postIdx = PM.postIdx\n" +
                "where PM.userIdx = ? and P.status = 'ACTIVE'";
        int getScrapPostsParams = userIdx;
        return this.jdbcTemplate.query(getScrapPostsQuery,
                (rs, rowNum) -> new GetScrapRes(
                        rs.getInt("postIdx"),
                        rs.getInt("boardIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("postContent"),
                        rs.getString("postName"),
                        rs.getString("createdAt"),
                        rs.getString("updatedAt")
                ), getScrapPostsParams);
    }

}
