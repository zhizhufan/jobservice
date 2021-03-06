package esd.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import esd.bean.Area;
import esd.bean.User;
import esd.controller.Constants;

/**
 * @Package: esd.service
 * @Title: Cookie.java Create on 2014-11-25 上午8:57:52
 * @Description:
 * 
 *               Cookie工具类,封装Cookie常用操作
 * 
 * @author yufu
 * @version v 0.1
 */
public class CookieHelper {

	/**
	 * 设置cookie有效期，根据需要自定义[本系统设置为30天]
	 */
	private final static int COOKIE_MAX_AGE = 1000 * 60 * 60 * 24 * 30;

	/**
	 * 
	 * @desc 删除指定Cookie
	 * @param response
	 * @param cookie
	 */
	public static void removeCookie(HttpServletResponse response, Cookie cookie) {
		if (cookie != null) {
			cookie.setPath("/");
			cookie.setValue("");
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
	}

	/**
	 * 
	 * @desc 删除指定Cookie
	 * @param response
	 * @param name
	 */
	public static void removeCookie(HttpServletRequest request,
			HttpServletResponse response, String name) {
		Cookie cookie = getCookie(request, name);
		if (cookie != null) {
			cookie.setPath("/");
			cookie.setValue("");
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
	}

	/**
	 * 
	 * @desc 删除指定Cookie
	 * @param response
	 * @param cookie
	 * @param domain
	 */
	public static void removeCookie(HttpServletResponse response,
			Cookie cookie, String domain) {
		if (cookie != null) {
			cookie.setPath("/");
			cookie.setValue("");
			cookie.setMaxAge(0);
			cookie.setDomain(domain);
			response.addCookie(cookie);
		}
	}

	/**
	 * 
	 * @desc 根据Cookie名称得到Cookie的值，没有返回Null
	 * @param request
	 * @param name
	 * @return
	 */
	public static String getCookieValue(HttpServletRequest request, String name) {
		Cookie cookie = getCookie(request, name);
		if (cookie != null) {
			return cookie.getValue();
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @desc 根据Cookie名称得到Cookie对象，不存在该对象则返回Null
	 * @param request
	 * @param name
	 */
	public static Cookie getCookie(HttpServletRequest request, String name) {
		Cookie cookies[] = request.getCookies();
		if (cookies == null || name == null || name.length() == 0)
			return null;
		Cookie cookie = null;
		for (int i = 0; i < cookies.length; i++) {
			if (!cookies[i].getName().equals(name))
				continue;
			cookie = cookies[i];
			if (request.getServerName().equals(cookie.getDomain()))
				break;
		}

		return cookie;
	}

	/**
	 * 
	 * @desc 添加一条新的Cookie信息，默认有效时间与session同步
	 * @param response
	 * @param name
	 * @param value
	 */
	public static void setCookie(HttpServletResponse response, String name,
			String value) {
		if (value == null)
			value = "";
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	/**
	 * 
	 * @desc 添加一条新的Cookie信息，可以设置其最长有效时间(单位：秒)
	 * @param response
	 * @param name
	 * @param value
	 * @param maxAge
	 */
	public static void setCookie(HttpServletResponse response, String name,
			String value, int maxAge) {
		if (value == null)
			value = "";
		Cookie cookie = new Cookie(name, value);
		if (maxAge != 0) {
			cookie.setMaxAge(maxAge);
		} else {
			cookie.setMaxAge(COOKIE_MAX_AGE);
		}
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	/**
	 * 
	 * @desc 添加新的Cookie信息
	 * @param response
	 * @param name
	 * @param value
	 * @param maxAge
	 */
	public static void setCookie(HttpServletResponse response,
			HttpServletRequest request, User user, Area area) {
		// user不为空, 则将user信息放入到cookie中
		if (user != null) {
			if (Constants.Authority.ADMIN.getValue() > user.getAuthority()) {
				// 普通用户存放cookie方式
				setCookie(response, Constants.USERID,
						String.valueOf(user.getId()));
				setCookie(response, Constants.USERNAME, user.getLoginName());
				setCookie(response, Constants.USERPASSWORD, user.getPassWord());
				setCookie(response, Constants.USERIDENTITY, user.getIdentity());
				setCookie(response, Constants.USERAUTHORITY,
						String.valueOf(user.getAuthority()));
				setCookie(response, Constants.USERREGISTERTIME,
						KitService.dateForShow(user.getCreateDate()));
			} else {
				// 管理员用户存放cookie方式
				setCookie(response, Constants.ADMINUSERID,
						String.valueOf(user.getId()));
				setCookie(response, Constants.ADMINUSERNAME, user.getLoginName());
				setCookie(response, Constants.ADMINUSERIDENTITY, user.getIdentity());
				setCookie(response, Constants.ADMINUSERAUTHORITY,
						String.valueOf(user.getAuthority()));
				try {
					String title = URLEncoder.encode(user.getTitle(), "UTF-8");
					setCookie(response, Constants.ADMINUSERTITLE, title);
					String nickName = URLEncoder.encode(user.getNickName(),
							"UTF-8");
					setCookie(response, Constants.ADMINUSERNICKNAME, nickName);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

		}
		// area不为空, 则将area信息放入到cookie中
		if (area != null) {
			setCookie(response, Constants.AREACODE, area.getCode(),
					Integer.MAX_VALUE);
			request.setAttribute(Constants.AREACODE, area.getCode());
			// 地区名称不为空时, 才将其encode后放入到cookie中
			if (area.getName() != null) {
				try {
					request.setAttribute(Constants.AREANAME, area.getName());
					setCookie(response, Constants.AREANAME,
							URLEncoder.encode(area.getName(), "utf-8"),
							Integer.MAX_VALUE);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 杀死所有普通用户的cookie信息, bl 为true 则包括地区code, false或者null不包括
	 * 
	 * @param response
	 * @param bl
	 */
	public static void killUserCookie(HttpServletResponse response, Boolean bl) {
		// 杀死所有cookie
		setCookie(response, Constants.USERID, null, 0);
		setCookie(response, Constants.USERNAME, null, 0);
		setCookie(response, Constants.USERPASSWORD, null, 0);
		setCookie(response, Constants.USERIDENTITY, null, 0);
		setCookie(response, Constants.USERAUTHORITY, null, 0);
		setCookie(response, Constants.USERREGISTERTIME, null, 0);
		setCookie(response, Constants.USERCOMPANYID, null, 0);
		if (bl) {
			setCookie(response, Constants.AREACODE, null, 0);
			setCookie(response, Constants.AREANAME, null, 0);
		}
	}

	/**
	 * 杀死所有管理员用户的cookie信息, bl 为true 则包括地区code, false或者null不包括
	 * 
	 * @param response
	 * @param bl
	 */
	public static void killAdminCookie(HttpServletResponse response, Boolean bl) {
		// 杀死所有cookie
		setCookie(response, Constants.ADMINUSERID, null, 0);
		setCookie(response, Constants.ADMINUSERNAME, null, 0);
		setCookie(response, Constants.ADMINUSERIDENTITY, null, 0);
		setCookie(response, Constants.ADMINUSERAUTHORITY, null, 0);
		setCookie(response, Constants.ADMINUSERTITLE, null, 0);
		setCookie(response, Constants.ADMINUSERNICKNAME, null, 0);
		if (bl) {
			setCookie(response, Constants.AREACODE, null, 0);
			setCookie(response, Constants.AREANAME, null, 0);
		}
	}

	/***
	 * 从cookie中获得当前的地区code, 如果没有, 则说明不是从网站群跳转过来的, 则设置为全国的地区code-10000000
	 * 
	 * @param request
	 * @return
	 */
	public static String getLocalArea(HttpServletRequest request) {
		String area = getCookieValue(request, Constants.AREACODE);
		if (area == null || "".equals(area)) {
			area = Constants.AREACOUNTRY;
		}
		return area;

	}
}