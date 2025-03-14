package com.example.loop.mapper.reply;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.loop.domain.reply.ReplyDTO;

@Mapper
public interface ReplyMapper {
	// 계시글의 댓글들을 가져오는 메서드
	List<ReplyDTO> getReplyByBoardNum(int boardNum);
	
	// 계시글의 댓글들과 유저의 프로필 사진까지 가져오는 메서드
	List<Map<String, Object>> getReplyByBoardNumWithProfile(int boardNum, int offset, String userId, boolean isLiked);
	
	//계시글의 댓글 수를 가져오는 메서드
	int getReplyCountByBoardNum(int BoardNum);
	
	// 계시글의 댓글을 작성하는 코드
	int replyWrite(ReplyDTO replyDTO);
	
	// 계시글의 댓글을 지우는 코드
	int replyDelete(String userId, int replyNum);
	
	// 댓글 업데이트
	int replyUpdate(ReplyDTO replyDTO);

	List<Integer> checkByboardNum(int boardNum);

	void deleteByreplyNum(Integer reply);
	
	// 댓글 번호로 댓글 조회
	List<Map<String, Object>> getReplyByNum(int replyNum);

	List<Map<String, Object>> getReplyLikeByReplyNum(String userId, boolean isLiked, int replyNum, int offset);

	void addlikeNum(int replyNum, String userId);

	void deletelikeNum(int replyNum, String userId);

	void deleteByBlock(String userId, String blockId);
  
  
 	int getReplyByUserId(String userId);
}