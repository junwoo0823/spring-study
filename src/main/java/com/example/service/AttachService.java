package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.domain.AttachVO;
import com.example.domain.Criteria;
import com.example.mapper.AttachMapper;

@Service
public class AttachService {

	@Autowired
	private AttachMapper attachMapper;

	// 특정 게시글에 포함된 첨부파일 정보 가져오기
	public List<AttachVO> getAttachesByBno(int bno) {
		return attachMapper.getAttachesByBno(bno);
	}

	public List<AttachVO> getAttachesByUploadpath(String uploadpath) {
		return attachMapper.getAttachesByUploadpath(uploadpath);
	}

	public List<AttachVO> getAttachesByUuids(List<String> uuidList) {
		return attachMapper.getAttachesByUuids(uuidList);
	}

	public int deleteAttachesByUuids(List<String> uuidList) {
		return attachMapper.deleteAttachesByUuids(uuidList);
	}

	/* 페이징으로 글의 이미지 파일(type = 'I') 가져오기 */
	public List<AttachVO> getAttachImage(Criteria cri) {

		int startRow = (cri.getPageNum() - 1) * cri.getAmount();
		cri.setStartRow(startRow);

		return attachMapper.getAttachImage(cri);
	}

	// 페이징 적용하여 이미지 파일 전체 개수 가져오기
	public int getImageTotalCount() {
		int count = attachMapper.getImageTotalCount();
		return count;
	}

}
