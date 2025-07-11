package com.ailab;

import com.ailab.common.properties.AesProperties;
import com.ailab.common.properties.JwtProperties;
import com.ailab.common.util.AesUtils;
import com.ailab.common.util.JwtUtils;
import com.ailab.mapper.UserMapper;
import com.ailab.pojo.dto.AuthLoginDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@SpringBootTest
class AilabServerApplicationTests {

	@Resource
	private UserMapper userMapper;
	@Resource
	private JwtProperties jwtProperties;
	@Resource
	private AesProperties aesProperties;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void testDatabaseConnection() {
		userMapper.selectList(null).forEach(user -> {
			System.out.println("User ID: " + user.getId());
			System.out.println("Account Name: " + user.getAccountName());
			System.out.println("Name: " + user.getName());
			System.out.println("Email: " + user.getEmail());
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

	@Test
	public void testPassword() {
		String password = "123456";
		String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		System.out.println("Hashed Password: " + hashedPassword);
	}

	@Test
	public void testAes() {
		String email = "123@example.com";
		String encrypt = AesUtils.encrypt(email, aesProperties.getKey());
		System.out.println("Encrypted Email: " + encrypt);
		String decrypt = AesUtils.decrypt(encrypt, aesProperties.getKey());
		System.out.println("Decrypted Email: " + decrypt);
	}

}
