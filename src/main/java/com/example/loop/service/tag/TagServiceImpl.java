package com.example.loop.service.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.loop.domain.tag.TagDTO;
import com.example.loop.mapper.tag.TagMapper;

@Service
public class TagServiceImpl implements TagService{

	private final TagMapper tagMapper;
	
	public TagServiceImpl(TagMapper tagMapper) {
		this.tagMapper = tagMapper;
	}

	@Override
	public List<TagDTO> getTagIdsByBoardNum(int boardNum) {
		// TODO Auto-generated method stub
		// 테그를 보드 아이디를 기준으로 dto로 포장해 가져오는 serviceimpl
		List<TagDTO> result = tagMapper.getTagIdsByBoardNum(boardNum);
		return result;
	}
	
	@Override
	public List<String> getTagNamesByBoardNum(int boardNum) {
		// TODO Auto-generated method stub
		// 태그 이름들을 가져와서 처리하는 코드
		List<String> tagNames = tagMapper.getTagNamesByBoardNum(boardNum);
		
		return tagNames;
	}
	

	@Override
	public List<Map<String, Object>> searchTagByUserInput(String userInput) {
		// TODO Auto-generated method stub
		// 사용자 검색 태그 처리
		return tagMapper.searchTagByUserInput(userInput);
	}
	

	@Override
	public int tagInput(TagDTO tagDTO) {
		// TODO Auto-generated method stub
		// 테그 분류 코드 작성후 Import 후 작성
		return 0;
	}

	@Override
	public int tagRelByBoardNum(int tagId, int boardNum) {
		// TODO Auto-generated method stub
		// 테그 분류 코드 작성후 Import 후 작성
		return 0;
	}

	@Override
	public int tagFollow(String userId, List<Integer> tagIds) {
	    // 유저가 여러 태그를 팔로우할 수 있게 만드는 서비스 로직
	    int result = 0;
	    
	    // 유효성 검사: userId와 tagIds가 모두 있어야 팔로우 처리 가능
	    if (userId != null && !userId.isEmpty() && tagIds != null && !tagIds.isEmpty()) {
	        // 각 tagId에 대해 팔로우 처리
	        for (Integer tagId : tagIds) {
	            if (tagId != null) {
	                result += tagMapper.tagFollow(userId, tagId);  // DB에서 팔로우 처리
	            }
	        }
	        System.out.println("태그 팔로우 성공");
	    } else {
	        if (userId == null || userId.isEmpty()) {
	            System.out.println("유저 아이디 없음");
	        } else if (tagIds == null || tagIds.isEmpty()) {
	            System.out.println("태그 아이디 없음");
	        }
	    }

	    return result;
	}

	@Override
	public int tagUnFollow(String userId, List<Integer> tagIds) {
	    // 유저가 여러 태그를 언팔로우할 수 있게 만드는 서비스 로직
	    int result = 0;
	    
	    // 유효성 검사: userId와 tagIds가 모두 있어야 언팔로우 처리 가능
	    if (userId != null && !userId.isEmpty() && tagIds != null && !tagIds.isEmpty()) {
	        // 각 tagId에 대해 언팔로우 처리
	        for (Integer tagId : tagIds) {
	            if (tagId != null) {
	                result += tagMapper.tagUnFollow(userId, tagId);  // DB에서 언팔로우 처리
	            }
	        }
	        System.out.println("태그 언팔로우 성공");
	    } else {
	        if (userId == null || userId.isEmpty()) {
	            System.out.println("유저 아이디 없음");
	        } else if (tagIds == null || tagIds.isEmpty()) {
	            System.out.println("태그 아이디 없음");
	        }
	    }

	    return result;
	}

}