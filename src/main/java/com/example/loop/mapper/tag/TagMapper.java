package com.example.loop.mapper.tag;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.loop.domain.board.TagRelationDTO;
import com.example.loop.domain.tag.TagDTO;

@Mapper
public interface TagMapper {
	
	//테그들을 계시글 아이디로 dto로 포장하여 가져옴
	List<TagDTO> getTagIdsByBoardNum(int boardNum);
	
	//계시글에 달린 태그들의 이름을 가져오기
	List<String> getTagNamesByBoardNum(int boardNum);
	
	//사용자 입력값으로 테그 맵 반환
	List<Map<String, Object>> searchTagByUserInput(String userInput);
	
	//테그 테이블에 데이터를 삽입
	int tagInput(TagDTO tagDTO);
	
	//테그 관계 테이블에 데이터 삽입
	int tagRelByBoardNum(int tagId, int boardNum);
	
	
	// 유저가 테그를 팔로우
	int tagFollow(String userId, Integer tagId);
	
	//유저가 테그를 언팔로우
	int tagUnFollow(String userId, Integer tagId);

	void tagRelByBoardNum(TagRelationDTO tagRelation);

	TagDTO getTagByName(String tagName);

	void insertTagRelation(TagRelationDTO tagRelation);

	int getLastTagId();

	int countBoardsByTagId(int tagId);

	void deleteTagFollowByTagId(int tagId);

	void deleteTagByTagId(int tagId);

	void deleteNullTagRelations();

}