package com.example.loop.service.reply;

import java.util.List;
import java.util.Map;

import com.example.loop.domain.reply.ReplyDTO;

public interface ReplyService {
	// 계시글의 댓글들을 가져오는 메서드
	List<ReplyDTO> getReplyByBoardNum(int boardNum);
	
	// 계시글의 댓글들과 유저의 프로필 사진까지 가져오는 메서드
	List<Map<String, Object>> getReplyByBoardNumWithProfile(int boardNum, int page, String userId, boolean isLiked);
	
	// 계시글의 댓글을 작성하는 코드
	List<Map<String, Object>> replyWrite(ReplyDTO replyDTO);
	
	// 계시글의 댓글을 지우는 코드
	int replyDelete(String userId, int replyNum);
	
	// 댓글 업데이트
	List<Map<String, Object>> replyUpdate(ReplyDTO replyDTO);

	List<Map<String, Object>> getReplyLikeByReplyNum(String userId, boolean isLiked, int replyNum, int page);

	boolean toggleLikeState(int replyNum, String userId, boolean likeCheck);
}