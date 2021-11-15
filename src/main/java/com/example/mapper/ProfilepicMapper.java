package com.example.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.domain.ProfilepicVO;

public interface ProfilepicMapper {

	@Select("SELECT * FROM profilepic WHERE MID = #{id}")
	ProfilepicVO getProfilepic(String id);

	@Insert("INSERT INTO profilepic (uuid, uploadpath, filename, mid) "
			+ "VALUES (#{uuid},#{uploadpath},#{filename},#{mid}) ")
	void insertProfilepic(ProfilepicVO ProfilepicVO);

	@Update("UPDATE profilepic SET uuid = #{uuid}, uploadpath = #{uploadpath}, filename = #{filename} "
			+ "WHERE mid = #{mid} ")
	void updateProfilepic(ProfilepicVO ProfilepicVO);

	@Delete("DELETE FROM profilepic WHERE mid = #{id} ")
	void deleteProfilepic(String id);

}
