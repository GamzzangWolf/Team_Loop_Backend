package com.example.loop.mapper.board;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookmarkMapper {

	int checkByboardNum(int boardNum);

	void deleteFilesByBoardnum(int boardNum);

	boolean getStateByboardNum(String userId, int boardNum);

	void addbookmark(int boardNum, String userId);

	void deletebookmark(int boardNum, String userId);

	void deletebookmarks(String userId, String blockId);

}