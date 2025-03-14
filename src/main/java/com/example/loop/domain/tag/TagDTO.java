package com.example.loop.domain.tag;

import lombok.Data;

@Data
public class TagDTO {
	private int tagId;
	private String tagName;
	private String normalizedTag;
	private double similarityId;
}