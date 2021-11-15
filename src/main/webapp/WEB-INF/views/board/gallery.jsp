<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html lang="ko">

<head>
<jsp:include page="/WEB-INF/views/include/head.jsp" />
<style>
table tbody tr {
	cursor: pointer;
}

table#board span.reply-level {
	display: inline-block;
}
</style>
</head>

<body>

	<!-- Navbar area -->
	<jsp:include page="/WEB-INF/views/include/top.jsp" />
	<!-- end of Navbar area -->


	<!-- Page Layout here -->
	<div class="row container">

		<!-- left menu area -->
		<jsp:include page="/WEB-INF/views/include/left.jsp" />
		<!-- end of left menu area -->


		<div class="col s12 m8 l9">
			<!-- page content  -->
			<div class="section">

				<div class="card-panel">
					<div class="row">
						<div class="col s12" style="padding: 0 2%;">

							<h5>갤러리 게시판 (글개수: ${ pageMaker.totalCount })</h5>
							<div class="divider" style="margin-bottom: 20px;"></div>

							<c:choose>
								<c:when test="${ pageMaker.totalCount gt 0 }">
									<div class="container" align="center">
										<div class="row">
											<c:forEach var="attach" items="${ attachList }">
												<c:set var="fileCallPath" value="${ attach.uploadpath }/s_${ attach.uuid }_${ attach.filename }" />
												<c:set var="originPath" value="${ attach.uploadpath }/${ attach.uuid }_${ attach.filename }" />
												<div class="col s3" style="margin: 5px 0;">
													<a href="/display?fileName=${ originPath }"> <img src="/display?fileName=${ fileCallPath }" class="z-depth-3"
														style="width: 150px; height: 150px;">
													</a>
												</div>
											</c:forEach>
										</div>
									</div>
								</c:when>
								<c:otherwise>
									<td class="center-align" colspan="5">갤러리 이미지가 없습니다.</td>
								</c:otherwise>
							</c:choose>

							<br>
							<ul class="pagination center">
								<%-- 이전 --%>
								<c:if test="${ pageMaker.prev eq true }">
									<li class="waves-effect"><a href="/board/gallery?pageNum=${ pageMaker.startPage - 1 }&type=${ pageMaker.cri.type }#board"><i
											class="material-icons">chevron_left</i></a></li>
								</c:if>

								<%-- 페이지블록 내 최대 5개 페이지씩 출력 --%>
								<c:forEach var="i" begin="${ pageMaker.startPage }" end="${ pageMaker.endPage }" step="1">
									<li class="waves-effect ${ (pageMaker.cri.pageNum eq i) ? 'active' : '' }"><a
										href="/board/gallery?pageNum=${ i }&type=${ pageMaker.cri.type }#board">${ i }</a></li>
								</c:forEach>

								<%-- 다음 --%>
								<c:if test="${ pageMaker.next eq true }">
									<li class="waves-effect"><a href="/board/gallery?pageNum=${ pageMaker.endPage + 1 }&type=${ pageMaker.cri.type }#board"><i
											class="material-icons">chevron_right</i></a></li>
								</c:if>
							</ul>

							<div class="divider" style="margin: 30px 0;"></div>



						</div>
					</div>
				</div>
				<!-- end of card-panel -->

			</div>
		</div>

	</div>

	<!-- footer area -->
	<jsp:include page="/WEB-INF/views/include/bottom.jsp" />
	<!-- end of footer area -->


	<!-- Scripts -->
	<jsp:include page="/WEB-INF/views/include/commonJs.jsp" />
</body>

</html>





