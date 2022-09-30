package com.example.debriserver.core.Board;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.core.Board.model.GetBoardSearchListRes;
import com.example.debriserver.core.Board.model.GetScrapBoardCountRes;
import com.example.debriserver.core.Board.model.GetScrapBoardListRes;
import com.example.debriserver.core.Post.model.GetScrapRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.debriserver.basicModels.BasicServerStatus.*;

@Service
public class BoardService {

    @Autowired
    private final BoardProvider boardProvider;

    @Autowired
    private final BoardDao boardDao;

    public BoardService(BoardProvider boardProvider, BoardDao boardDao) {
        this.boardProvider = boardProvider;
        this.boardDao = boardDao;
    }

    public void scrapBoard(int boardIdx, int userIdx) throws BasicException {

        if (boardProvider.checkBoardExist(boardIdx) == 0) {
            throw new BasicException(BOARD_NOT_EXIST);
        }

        if (boardProvider.checkBoardSubsExist(boardIdx, userIdx) == 0) {

            int result = boardDao.insertBoardSubs(boardIdx, userIdx);
            if (result == 0) {
                throw new BasicException(BOARD_INSERT_FAIL);
            }
        }
        else if(!boardProvider.checkUnscrapExist(boardIdx, userIdx)){

            int result2 = boardDao.scrapBoard(boardIdx, userIdx);
            if (result2 == 0) {
                throw new BasicException(BOARD_SCRAP_FAIL);
            }
        }
        else {
            throw new BasicException(SCRAP_BOARD_EXIST);
        }

        try {

        } catch (Exception e){
            throw new BasicException(DB_ERROR);
        }
    }

    public void cancelScrapBoard(int boardIdx, int userIdx) throws BasicException {

        if (boardProvider.checkBoardExist(boardIdx) == 0) {
            throw new BasicException(BOARD_NOT_EXIST);
        }

        try {
            if (boardProvider.checkBoardSubsExist(boardIdx, userIdx) != 0) {

                int result = boardDao.cancelScrapBoard(boardIdx, userIdx);
                if (result == 0) {
                    throw new BasicException(BOARD_SCRAP_CANCEL_FAIL);
                }
            }
            else {
                throw new BasicException(SCRAP_BOARD_NOT_EXIST);
            }
        } catch (Exception e) {
            throw new BasicException(DB_ERROR);
        }
    }

    public List<GetScrapBoardListRes> getScrapBoardList(int userIdx) throws BasicException {

        try {
            if (boardDao.countScrapBoardList(userIdx) > 0) {
                List<GetScrapBoardListRes> getScrapBoardListRes = boardDao.getScrapBoardList(userIdx);
                return getScrapBoardListRes;
            } else {
                throw new BasicException(BOARD_GET_SCRAP_LIST_FAIL);
            }
        } catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }

    public List<GetScrapBoardListRes> getAllBoardList() throws BasicException{
        try{
            return boardDao.getAllBoardList();
        }catch (Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(DB_ERROR);
        }
    }

    public List<GetBoardSearchListRes> getBoardSearchList(String key, int userIdx) throws BasicException{
        try{
            return boardDao.getBoardSearchList(key, userIdx);
        }catch (Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(DB_ERROR);
        }
    }
}
