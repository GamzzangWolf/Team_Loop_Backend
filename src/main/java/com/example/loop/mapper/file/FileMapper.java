package com.example.loop.mapper.file;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.loop.domain.file.FileDTO;

@Mapper
public interface FileMapper {
	int insertFile(FileDTO file);
	
	FileDTO getFileBySystemname(String systemname);
	List<FileDTO> getFiles(int boardnum);
	
	int deleteFileBySystemname(String systemname);
	int deleteFilesByBoardnum(long boardnum);
	
	//2024 11 14
	List<String> getBoardFiles(int boardNum);

	int checkByboardNum(int boardNum);
}