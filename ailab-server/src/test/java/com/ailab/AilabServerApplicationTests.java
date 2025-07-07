package com.ailab;

import com.ailab.common.util.JwtUtils;
import com.ailab.mapper.UserMapper;
import com.ailab.pojo.dto.AuthLoginDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
class AilabServerApplicationTests {

	@Resource
	private UserMapper userMapper;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void testDatabaseConnection() {
		userMapper.selectList(null).forEach(user -> {
			System.out.println("User ID: " + user.getId());
			System.out.println("Account Name: " + user.getAccountName());
			System.out.println("Name: " + user.getName());
			System.out.println("Phone: " + user.getPhone());
			System.out.println("Role: " + user.getRole());
			System.out.println("SNO: " + user.getSno());
			System.out.println("Create Time: " + user.getCreateTime());
			System.out.println("Update Time: " + user.getUpdateTime());
			System.out.println("-----------------------------");
		});
	}

	@Test
	public void test1() {
		String password = "1234";
		String hashpw = BCrypt.hashpw(password, BCrypt.gensalt());
		System.out.println("Hashed Password: " + hashpw);
	}

	@Test
	public void testJwt() {
		String secretKey = "mySecretKey1234567890ahduweweyewifhuwh";
		Long expireTime = 10000L;
		AuthLoginDTO loginDTO = AuthLoginDTO.builder()
				.accountName("testUser")
				.password("testPassword")
				.build();
		Map<String, Object> data = Map.of(
				"userInfo", loginDTO
		);
		String token = JwtUtils.createToken(secretKey, expireTime, data);
		System.out.println("Generated Token: " + token);
		// 解析token
		Claims parsedData = JwtUtils.parseToken(secretKey, token);
		System.out.println("Parsed Data: " + parsedData);
		AuthLoginDTO authLoginDTO = JwtUtils.getObjectFromClaims(parsedData, "userInfo", AuthLoginDTO.class);
		System.out.println("userInfo: " + authLoginDTO);
	}


}
