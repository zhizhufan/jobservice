<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<div class="qyd">
	<div class="qyd_line">
		<h2 class="qyd_t1">
			<a id="ctl00_user_left_HyperLink1" href="${contextPath }/user/goCenter">个人管理中心首页</a>
		</h2>
		<h3 id="ctl00_user_left_q5" class="qyd_t2">
			<span>简历管理</span>
		</h3>
		<div id="ctl00_user_left_q6" class="qyd_t3">
			<ul>
				<li><a href="${contextPath }/secure/resume/save">添加简历</a>
				</li>
				<li><a href="${contextPath }/secure/resume/getManage">我的简历</a>
				</li>
			</ul>
		</div>
		<h3 id="ctl00_user_left_q7" class="qyd_t2">
			<span>应聘管理</span>
		</h3>
		<div id="ctl00_user_left_q8" class="qyd_t3">
			<ul>
				<li><a href="${contextPath }/secure/resume/getSentJob/1">我的应聘记录</a>
				</li>
			<!-- 	<li><a href="#">收藏的职位</a>
				</li>
				<li><a href="#">面试邀请通知</a>
				</li> -->
			</ul>
		</div>
		<h3 id="ctl00_user_left_H1" class="qyd_t2">
			<span>控制面板</span>
		</h3>
		<div id="ctl00_user_left_H2" class="qyd_t3">
			<ul>
				<li><a href="${contextPath }/secure/user/passWordEdit">修改登录密码</a>
				</li>
			</ul>
		</div>
		<h3 class="qyd_t2">
			<span>客服中心</span>
		</h3>
		<div class="qyd_t3">
			<ul>
				<li>
					<p>招聘咨询电话：</p>
					<p class="red">
						<strong style="color:Red">0574-23670051</strong>
					</p></li>
			</ul>
		</div>
	</div>
</div>