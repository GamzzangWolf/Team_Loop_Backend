package com.example.loop.service.reply;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.loop.domain.reply.ReplyDTO;
import com.example.loop.mapper.reply.ReplyMapper;
import com.example.loop.mapper.user.NotificationMapper;

@Service
public class ReplyServiceImpl implements ReplyService{
	
	private final ReplyMapper replyMapper;
	private final NotificationMapper notifyMapper;
	
	public ReplyServiceImpl(ReplyMapper replyMapper,NotificationMapper notifyMapper) {
		this.replyMapper = replyMapper;
		this.notifyMapper = notifyMapper;
	}
	
	
	@Override
	public List<ReplyDTO> getReplyByBoardNum(int boardNum) {
		// TODO Auto-generated method stub
		// 보드 넘버로 댓글을 가져옴
		List<ReplyDTO> result = replyMapper.getReplyByBoardNum(boardNum);
		System.out.println("보드 아이디를 가지고 댓글 가져오기 " + result);
		
		return result;
	}
	

	@Override
	public List<Map<String, Object>> getReplyByBoardNumWithProfile(int boardNum, int page, String userId, boolean isLiked) {
		// TODO Auto-generated method stub
		int offset = (page-1)*10;
		return replyMapper.getReplyByBoardNumWithProfile(boardNum, offset, userId, isLiked);
	}

	@Override
	public List<Map<String, Object>> replyWrite(ReplyDTO replyDTO) {
		// TODO Auto-generated method stub
		// 댓글 등록
		int result = 0;
		if((replyDTO.getUserId() != null && !replyDTO.getUserId().isEmpty())
				&& (replyDTO.getReplyContents() != null && !replyDTO.getReplyContents().isEmpty())) {
			result = replyMapper.replyWrite(replyDTO);
			if(result > 0) {
				return replyMapper.getReplyByNum(replyDTO.getReplyNum());
			}
			System.out.println("댓글 정상 등록 : " + result);
		}else if(replyDTO.getUserId() == null || replyDTO.getUserId().isEmpty()){
			System.out.println("댓글 등록 실패 / 아이디 오류");
		}else if(replyDTO.getReplyContents() == null || replyDTO.getReplyContents().isEmpty()) {
			System.out.println("댓글 등록 실패 / 콘텐츠 내용 없음");
		}else {
			System.out.println("댓글 등록 실패 / UnExpected Error");
		}
		return null;
		
	}

	@Override
	public int replyDelete(String userId, int replyNum) {
		// TODO Auto-generated method stub
		// 댓글 지우기
		int result = 0;
		if(userId==null || userId.isEmpty()) {
			System.out.println("댓글 삭제 실패 / 유저 아이디 오류");
		}else {
			try {
				result = replyMapper.replyDelete(userId, replyNum);
				notifyMapper.deleteNotify(3, replyNum, userId);
				System.out.println("정상 삭제 완료 : " + result);
				
			}catch (Exception e) {
				// TODO: handle exception
				System.out.println("UnExpected Error");
				e.printStackTrace();
			}
		}
		return result;
	}


	// 댓글 업데이트
    @Override
    public List<Map<String, Object>> replyUpdate(ReplyDTO replyDTO) {
        // 유효성 검사
        if (replyDTO.getUserId() == null || replyDTO.getUserId().isEmpty()) {
            System.out.println("댓글 업데이트 / 유저 아이디 오류");
            return null; // 유저 아이디가 없는 경우 null 반환
        } else if (replyDTO.getReplyContents() == null || replyDTO.getReplyContents().isEmpty()) {
            System.out.println("댓글 업데이트 / 내용이 비어있음");
            return null; // 내용이 없는 경우 null 반환
        }

        // 업데이트 실행
        try {
            // 댓글을 업데이트하고 결과를 받음
            int result = replyMapper.replyUpdate(replyDTO);

            if (result > 0) {
                // 업데이트가 성공하면 수정된 댓글을 반환
                System.out.println("댓글 업데이트 성공");
                return replyMapper.getReplyByNum(replyDTO.getReplyNum()); // 수정된 ReplyDTO 객체 반환
            } else {
                System.out.println("댓글 업데이트 실패");
                return null; // 업데이트 실패 시 null 반환
            }
        } catch (Exception e) {
            // 예외 발생 시 로그 출력
            System.out.println("댓글 업데이트 중 예외 발생");
            e.printStackTrace();
            return null; // 예외 발생 시 null 반환
        }
    }


	@Override
	public List<Map<String, Object>> getReplyLikeByReplyNum(String userId, boolean isLiked, int replyNum, int page) {
		int offset = (page-1)*10;
		return replyMapper.getReplyLikeByReplyNum(userId, isLiked, replyNum, offset);
	}


	@Override
	public boolean toggleLikeState(int replyNum, String userId, boolean likeCheck) {
		if (likeCheck == false) {
			replyMapper.addlikeNum(replyNum, userId);
		} else if (likeCheck == true) {
			replyMapper.deletelikeNum(replyNum, userId);
		}
		return true;
	}





}