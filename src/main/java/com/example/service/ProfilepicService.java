package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.domain.ProfilepicVO;
import com.example.mapper.ProfilepicMapper;

@Service
public class ProfilepicService {

	@Autowired
	private ProfilepicMapper profilepicMapper;

	public ProfilepicVO getProfilepic(String id) {
		return profilepicMapper.getProfilepic(id);
	}

	public void insertProfilepic(ProfilepicVO ProfilepicVO) {
		profilepicMapper.insertProfilepic(ProfilepicVO);
	}

	public void updateProfilepic(ProfilepicVO ProfilepicVO) {
		profilepicMapper.updateProfilepic(ProfilepicVO);
	}

	public void deleteProfilepic(String id) {
		profilepicMapper.deleteProfilepic(id);
	}

}
