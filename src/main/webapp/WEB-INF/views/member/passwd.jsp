<%@page import="com.example.domain.MemberVO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">

<head>
<jsp:include page="/WEB-INF/views/include/head.jsp" />
</head>

<body>

	<!-- Navbar area -->
	<jsp:include page="/WEB-INF/views/include/top.jsp" />
	<!-- end of Navbar area -->


	<!-- Page Layout -->
	<div class="row container">

		<!-- left menu area -->
		<jsp:include page="/WEB-INF/views/include/left.jsp" />
		<!-- end of left menu area -->

		<div class="col s12 m8 l9">
			<!-- page content  -->
			<div class="section">

				<!-- card panel -->
				<div class="card-panel">
					<div class="row">
						<div class="col s12" style="padding: 0 5%;">

							<h5>비밀번호 변경</h5>
							<div class="divider" style="margin: 30px 0;"></div>

							<form action="/member/passwd" method="post">

								<div class="row">
									<div class="input-field col s12">
										<i class="material-icons prefix">lock_clock</i> <input id="id" type="text" class="validate" name="id" value="${ id }" readonly> <label
											for="id">아이디</label>
									</div>
								</div>

								<div class="row">
									<div class="input-field col s12">
										<i class="material-icons prefix">lock_clock</i> <input id="oldPasswd" type="password" class="validate" name="oldPasswd" required autofocus>
										<label for="oldPasswd">기존 비밀번호</label>
									</div>
								</div>

								<div class="row">
									<div class="input-field col s12">
										<i class="material-icons prefix">lock_open</i> <input id="newPasswd" type="password" class="validate" name="newPasswd" required> <label
											for="newPasswd">새 비밀번호</label>
									</div>
								</div>

								<div class="row">
									<div class="input-field col s12">
										<i class="material-icons prefix">lock</i> <input id="newPasswd2" type="password" class="validate" name="newPasswd2"> <label
											for="newPasswd2">비밀번호 재확인</label>
									</div>
								</div>

								<br>
								<div class="row center">
									<button class="btn waves-effect waves-light" type="submit">
										수정하기 <i class="material-icons right">create</i>
									</button>
									&nbsp;&nbsp;
									<button class="btn waves-effect waves-light" type="reset">
										초기화 <i class="material-icons right">clear</i>
									</button>
								</div>
							</form>

						</div>
					</div>
				</div>
				<!-- end of card panel -->

			</div>
		</div>

	</div>
	<!-- end of Page Layout -->


	<!-- footer area -->
	<jsp:include page="/WEB-INF/views/include/bottom.jsp" />
	<!-- end of footer area -->


	<!-- Scripts -->
	<jsp:include page="/WEB-INF/views/include/commonJs.jsp" />

	<script>
		$('input#newPasswd2').on('focusout', function() {
			const newPasswd = $('input#newPasswd').val();
			const newPasswd2 = $(this).val();

			if (newPasswd != newPasswd2) {
				alert('비밀번호가 일치하지 않습니다.');
				$('input#newPasswd').focus();
			}
		});
	</script>

</body>

</html>




