package com.example.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.domain.MemberVO;
import com.example.domain.ProfilepicVO;
import com.example.service.MemberService;
import com.example.service.ProfilepicService;
import com.example.util.JScript;

import net.coobird.thumbnailator.Thumbnailator;

// @Component 계열 애노테이션이므로 스프링 빈이 됨
// 스프링의 프론트컨트롤러인 DispatcherServlet 객체가 사용할 컨트롤러가 됨

@Controller
@RequestMapping("/member/*")
public class MemberController {

	// @Autowired
	private MemberService memberService;
	@Autowired
	private ProfilepicService profilepicService;

	// 생성자로 의존객체를 받도록 선언하면 @Autowired 애노테이션 생략 가능
	public MemberController(MemberService memberService) {
		super();
		this.memberService = memberService;
	}

	@GetMapping("/join") // GET - "/member/join"
	public void joinForm() {
		System.out.println("join 호출됨...");

		// return "member/join";

		// 컨트롤러 메소드의 리턴타입이 void일 경우는
		// 요청 URL주소(여기서는 "/member/join")를
		// 실행할 JSP 경로명("member/join")으로 사용함.
		// URL 요청 경로명과 JSP 경로명이 같을경우 사용할 수 있음.
	}

	@PostMapping("/join") // POST - "/member/join"
	public ResponseEntity<String> join(MemberVO memberVO) {
		// 스프링의 프론트 컨트롤러(DispatcherServlet)는
		// 호출하는 컨트롤러의 매개변수 타입이 VO에 해당(getter/setter가 존재)하면
		// VO객체를 생성후 사용자 요청 파라미터값을 자동으로 채워서 넣어줌.

		// 회원가입날짜 설정
		memberVO.setRegDate(new Date());

		// 비밀번호를 jbcrypt 라이브러리 사용해서 암호화하여 저장하기
		String passwd = memberVO.getPasswd();
		String pwHash = BCrypt.hashpw(passwd, BCrypt.gensalt()); // 60글자로 암호화된 문자열 리턴함
		memberVO.setPasswd(pwHash); // 암호화된 비밀번호 문자열로 수정하기

		// 생년월일 문자열에서 하이픈(-) 기호 제거하기
		String birthday = memberVO.getBirthday();
		birthday = birthday.replace("-", ""); // 하이픈 문자열을 빈문자열로 대체
		memberVO.setBirthday(birthday);

		System.out.println(memberVO); // 서버 콘솔 출력

		// 회원가입 insert 처리하기
		memberService.register(memberVO);

		// 리다이렉트 방법 1.
		// return "redirect:/member/login"; // 리다이렉트 요청경로를 문자열로 리턴

		// 리다이렉트 방법 2.
		// 스프링에서는 너무 많은 권한을 가진 request, response 객체에 대해서 직접적인 접근을 최소화하길 권장함.
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "text/html; charset=UTF-8");

		String str = JScript.href("회원가입 성공!", "/member/login");

		return new ResponseEntity<String>(str, headers, HttpStatus.OK);
	} // join

	@GetMapping("/joinIdDupChk") // GET - "/member/joinIdDupChk"
	public void joinIdDupChk(@ModelAttribute("id") String id, Model model) {
		// 아이디 중복여부 확인
		int count = memberService.getCountById(id);

		model.addAttribute("count", count);
		// model.addAttribute("id", id);

		// 스프링에서는 request에 직접 데이터를 쓰지않는다.
		// Model 타입 객체에 데이터를 쓰면 request 영역객체에 데이터를 자동으로 옮겨줌.
		// request.setAttribute("count", count);
		// request.setAttribute("id", id);

		// return "/member/joinIdDupChk";
	} // joinIdDupChk

	@GetMapping("/login") // GET - "/member/login"
	public void login() {
		System.out.println("login 호출됨...");
		// return "member/login";
	}

	@PostMapping("/login") // POST - "/member/login"
	public ResponseEntity<String> login(String id, String passwd,
			@RequestParam(required = false, defaultValue = "false") boolean rememberMe, HttpSession session,
			HttpServletResponse response) {
		// 콘트롤러 메소드의 매개변수 형식이 기본자료형(String형 포함)에 해당하면
		// 해당 매개변수 이름으로 사용자 입력 파라미터값을 자동으로 찾아서 넣어줌.

		MemberVO memberVO = memberService.getMemberById(id);

		boolean isPasswdSame = false;
		String message = "";

		if (memberVO != null) { // id 일치
			isPasswdSame = BCrypt.checkpw(passwd, memberVO.getPasswd());

			if (isPasswdSame == false) { // 비밀번호 일치하지 않음
				message = "비밀번호가 일치하지 않습니다.";
			}
		} else { // id 불일치
			message = "존재하지 않는 아이디 입니다.";
		}

		// 로그인 실패인 경우 (없는 아이디거나 비밀번호가 틀렸을때)
		if (memberVO == null || isPasswdSame == false) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "text/html; charset=UTF-8");

			String str = JScript.back(message);

			return new ResponseEntity<String>(str, headers, HttpStatus.OK);
		}

		// 로그인 성공인 경우
		// 로그인 인증 처리
		// 사용자 당 유지되는 세션객체에 기억할 데이터를 저장
		session.setAttribute("id", id);

		// 로그인 상태유지가 체크되었으면(사용자가 로그인 유지를 원하면)
		if (rememberMe == true) {
			// 쿠키 생성
			Cookie cookie = new Cookie("loginId", id);
			// 쿠키 유효시간(유통기한) 설정
			// cookie.setMaxAge(60 * 10); // 초단위로 설정. 10분 = 60초 * 10
			cookie.setMaxAge(60 * 60 * 24 * 7); // 1주일 설정.

			// 쿠키 경로설정
			cookie.setPath("/"); // 프로젝트 모든 경로에서 쿠키 받도록 설정

			// 클라이언트로 보낼 쿠키를 response 응답객체에 추가하기. -> 응답시 쿠키도 함께 보냄.
			response.addCookie(cookie);
		}

		// 리다이렉트 방식 1. - String
		// return "redirect:/";

		// 리다이렉트 방식 2. - ResponseEntity
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", "/"); // redirect 경로를 "/"로 지정
		// 리다이렉트일 경우는 응답코드로 HttpStatus.FOUND 를 지정해야함에 주의!
		return new ResponseEntity<String>(headers, HttpStatus.FOUND);
	} // login

	@GetMapping("/logout")
	public String logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		// 세션 비우기
		session.invalidate();

		// 로그인 상태유지용 쿠키가 있으면 삭제처리하기
		// 쿠키값 가져오기
		Cookie[] cookies = request.getCookies();

		// 특정 쿠키 삭제하기(브라우저가 삭제하도록 유효기간 0초로 설정해서 보내기)
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("loginId")) {
					cookie.setMaxAge(0); // 쿠키 유효기간 0초 설정(삭제 의도)
					cookie.setPath("/");
					response.addCookie(cookie); // 응답객체에 추가하기
				}
			}
		}

		// 로그인 화면으로 리다이렉트 이동
		return "redirect:/member/login";
	} // logout

	/*******************
	 ** 비밀번호 변경 **
	 *******************/

	@GetMapping("/passwd")
	public void passwd() {

		System.out.println("passwd 호출...");

	} // passwd

	@PostMapping("/passwd")
	public ResponseEntity<String> passwd(String oldPasswd, String newPasswd, HttpSession session,
			HttpServletResponse response) {

		String id = (String) session.getAttribute("id");
		MemberVO memberVO = memberService.getMemberById(id);

		boolean isPasswdSame = false;
		String message = "";

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "text/html; charset=UTF-8");
		String str = null;

		isPasswdSame = BCrypt.checkpw(oldPasswd, memberVO.getPasswd());

		// 비밀번호 일치하지 않을 때
		if (isPasswdSame == false) {
			message = "기존의 비밀번호와 일치하지 않음";

			str = JScript.back(message);
			return new ResponseEntity<String>(str, headers, HttpStatus.OK);
		}

		// 비밀번호를 jbcrypt 라이브러리 사용해서 암호화하여 저장하기
		String pwHash = BCrypt.hashpw(newPasswd, BCrypt.gensalt());
		memberVO.setPasswd(pwHash); // 60글자로 암호화된 문자열 리턴함
		memberService.updateOnlyPasswd(memberVO); // 암호화된 비밀번호 문자열로 수정하기

		str = JScript.href("비밀번호 수정성공", "/");

		return new ResponseEntity<String>(str, headers, HttpStatus.OK);
	} // passwd

	/*****************
	 ** 내정보 수정 **
	 *****************/

	@GetMapping("/modify") // GET - "/member/modify"
	public String modifyForm(HttpSession session, Model model) throws Exception {

		// 세션에서 로그인 아이디 가져오기
		String id = (String) session.getAttribute("id");

		// 아이디에 해당하는 자신의 정보를 DB에서 가져오기
		MemberVO memberVO = memberService.getMemberById(id);
		ProfilepicVO profilepicVO = profilepicService.getProfilepic(id);

		model.addAttribute("member", memberVO);
		model.addAttribute("profilepicVO", profilepicVO);

		return "/member/modifyMember";
	} // modifyForm

	@PostMapping("/modify")
	public ResponseEntity<String> modify(MemberVO memberVO, MultipartFile file, HttpSession session)
			throws IllegalStateException, IOException {

		ProfilepicVO profilepicVO = uploadProfileImage(file, memberVO.getId(), "profilepic");

		// 업로드 또는 변경할 이미지 파일이 있는경우
		if (profilepicVO != null) {
			// 현재 해당 id의 회원정보 프로필 사진 조회
			ProfilepicVO profileVO = profilepicService.getProfilepic(memberVO.getId());

			if (profileVO != null) { // 프로필 사진이 있을 경우

				deleteProfileImage(profileVO, "profilepic"); // 프로필 사진 삭제

				profilepicService.updateProfilepic(profilepicVO); // 새 프로필 사진으로 업데이트

			} else { // 프로필 사진이 없을 경우
				// 프로필 사진 추가
				profilepicService.insertProfilepic(profilepicVO);
			}

			session.setAttribute("profilepicVO", profilepicVO);
		}

		// 회원정보 수정날짜로 수정하기
		memberVO.setRegDate(new Date());

		// 생년월일 문자열에서 하이픈(-) 기호 제거하기
		String birthday = memberVO.getBirthday();
		birthday = birthday.replace("-", ""); // 하이픈 문자열을 빈문자열로 대체
		memberVO.setBirthday(birthday);

		// DB 테이블에서 id에 해당하는 데이터 행 가져오기
		MemberVO dbMemberVO = memberService.getMemberById(memberVO.getId());

		boolean isPasswdSame = BCrypt.checkpw(memberVO.getPasswd(), dbMemberVO.getPasswd());

		// 비밀번호 일치하지 않을 때
		if (isPasswdSame == false) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "text/html; charset=UTF-8");

			String str = JScript.back("비밀번호 틀림");

			return new ResponseEntity<String>(str, headers, HttpStatus.OK);
		}

		// 비밀번호 일치할 때
		memberService.updateById(memberVO); // 회원정보 수정하기

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "text/html; charset=UTF-8");

		String str = JScript.href("회원정보 수정성공!", "/");

		return new ResponseEntity<String>(str, headers, HttpStatus.OK);
	} // modify

	/*******************
	 ** 프로필 업로드 **
	 *******************/

	private ProfilepicVO uploadProfileImage(MultipartFile file, String id, String isProfilepic)
			throws IllegalStateException, IOException {

		ProfilepicVO profilepicVO = new ProfilepicVO();

		// 업로드 처리로 생성해야할 파일정보가 없으면 메소드 종료
		if (file == null) {
			return profilepicVO;
		}

		String uploadFolder = "C:/kjw/upload"; // 업로드 기준경로

		File uploadPath = new File(uploadFolder, getFolder());

		// 프로필 사진일 경우(경로 변경)
		if (isProfilepic != null) {
			uploadFolder = "C:/kjw/upload/profilepic/" + id;
			uploadPath = new File(uploadFolder);
		}

		if (!uploadPath.exists()) {
			uploadPath.mkdirs();
		}

		if (!file.isEmpty()) {
			String originalFilename = file.getOriginalFilename();
			UUID uuid = UUID.randomUUID();
			String uploadFilename = uuid.toString() + "_" + originalFilename;

			File proFile = new File(uploadPath, uploadFilename); // 생성할 파일이름 정보

			file.transferTo(proFile);

			// 현재 업로드한 파일이 이미지 파일이면 썸네일 이미지를 추가로 생성하기
			File outFile = new File(uploadPath, "s_" + uploadFilename);

			Thumbnailator.createThumbnail(proFile, outFile, 200, 200); // 썸네일 이미지 파일 생성하기

			profilepicVO.setUuid(uuid.toString());
			profilepicVO.setUploadpath((isProfilepic != null) ? "profilepic" : getFolder());
			profilepicVO.setFilename(originalFilename);
			profilepicVO.setMid(id);
		}

		return profilepicVO;
	} // uploadProfileImage

	/*****************
	 ** 프로필 삭제 **
	 *****************/

	private void deleteProfileImage(ProfilepicVO profilepicVO, String isProfilepic) {

		if (profilepicVO == null) {
			return;
		}

		String basePath = "C:/kjw/upload";

		String uploadpath = basePath + "/" + profilepicVO.getUploadpath();

		if (isProfilepic != null) {
			basePath = "C:/kjw/upload/profilepic/" + profilepicVO.getMid();
			uploadpath = basePath + "/";
		}

		String filename = profilepicVO.getUuid() + "_" + profilepicVO.getFilename();

		System.out.println("uploadpath" + uploadpath);

		File file = new File(uploadpath, filename);

		if (file.exists() == true) { // 해당 경로에 첨부파일이 존재하면
			file.delete(); // 첨부파일 삭제하기
		}

		File thumbnailFile = new File(uploadpath, "s_" + filename);
		System.out.println("thumbnailFile" + thumbnailFile.getPath());

		if (thumbnailFile.exists() == true) {
			thumbnailFile.delete();
		}

	} // deleteProfileImage

	// 년/월/일 형식의 폴더명 리턴하는 메소드
	private String getFolder() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String str = sdf.format(new Date());
		return str;
	}

	/**************
	 ** 회원탈퇴 **
	 **************/

	@GetMapping("/remove")
	public String removeForm() {

		System.out.println("removeForm() 호출됨...");

		return "member/removeMember";
	} // removeForm

	@PostMapping("/remove")
	public ResponseEntity<String> remove(String passwd, HttpSession session, HttpServletRequest request,
			HttpServletResponse response) {

		System.out.println("remove() 호출됨...");

		// 세션에서 로그인 아이디 가져오기
		String id = (String) session.getAttribute("id");

		// DB에서 아이디로 자신의 정보를 VO로 가져오기
		MemberVO memberVO = memberService.getMemberById(id);

		// 비밀번호 비교하기
		boolean isPasswdSame = BCrypt.checkpw(passwd, memberVO.getPasswd());

		// 비밀번호가 일치하지 않을때
		if (isPasswdSame == false) { // !isPasswdSame
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "text/html; charset=UTF-8");

			String str = JScript.back("비밀번호 틀림");

			return new ResponseEntity<String>(str, headers, HttpStatus.OK);
		}

		// 비밀번호가 일치할 때
		memberService.deleteById(id); // DB 레코드 삭제

		// 해당 회원 프로필 사진 조회
		ProfilepicVO memberProfileImage = profilepicService.getProfilepic(id);

		// 프로필 사진이 존재한다면 삭제
		if (memberProfileImage != null) {
			// 프로필사진 정보 삭제
			profilepicService.deleteProfilepic(id);
			// 프로필 사진 삭제
			deleteProfileImage(memberProfileImage, "profilepic");
		}

		session.invalidate(); // 세션 비우기

		// 쿠키값 가져오기
		Cookie[] cookies = request.getCookies();

		// 특정 쿠키 삭제하기
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("loginId")) {
					cookie.setMaxAge(0); // 쿠키 유효기간 0초 설정(삭제 의도)
					cookie.setPath("/");
					response.addCookie(cookie); // 응답객체에 추가하기
				}
			}
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "text/html; charset=UTF-8");

		String str = JScript.href("회원탈퇴 성공!", "/");

		return new ResponseEntity<String>(str, headers, HttpStatus.OK);
	} // remove

}
