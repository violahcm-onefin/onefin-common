//package com.onefin.common;
//
//import java.util.Date;
//import java.util.Enumeration;
//import java.util.UUID;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//
///**
// * 
// * @author khanhnguyen
// *
// */
//public class HTTPRequestUtil {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPRequestUtil.class.getName());
//	private static final String cName = HTTPRequestUtil.class.getSimpleName() + ":: ";
//	
//	/**
//	 * Get header parameter
//	 * 
//	 * @param request
//	 * @param key
//	 * @return
//	 */
//	public static String getHeaderParam(HttpServletRequest request, String key) {
//		Enumeration<String> headers = request.getHeaders(key);
//		if (headers != null && headers.hasMoreElements()) {
//			String value = headers.nextElement();
//			return value;
//		}
//		return "";
//	}
//	
//	/**
//	 * Decode and validate request from client
//	 * 
//	 * @param request			: request object
//	 * @param apiGatewayIssuer	: who send this request (name of partner)
//	 * @param acceptComponent	: name of component send request (from client side) 
//	 * @param secret			: Loaded from configuration
//	 * @return
//	 */
//	public static ResponseEntity<HttpStatus> validateRequest(HttpServletRequest request, String apiGatewayIssuer, String acceptComponent, String secret) {
//		String token = HTTPRequestUtil.getHeaderParam(request, OneFinConstants.AUTHORIZATION);
//		
//		boolean isValid = validateToken(token, apiGatewayIssuer, acceptComponent, secret);
//		if (isValid) {
//			return new ResponseEntity<HttpStatus>(HttpStatus.OK);
//		}
//		
//		LOGGER.error(cName + "Unauthorize!, You are not allow to access OneFin!");
//		return new ResponseEntity<HttpStatus>(HttpStatus.UNAUTHORIZED);
//	}
//	
//	/**
//	 * Generate Access Token from client request
//	 * 
//	 * @param issuer	: who send request, for example: "SoftSpace"
//	 * @param subject	: send from component name: for example: "eKYC"
//	 * @param secret	: Server will share this "secret" to client, normally it is configurable at the server side
//	 * @return
//	 */
//	public static String generateToken(String issuer, String subject, String secret) {
//		String jws = ""; 
//		try {
//			long timestamp = System.currentTimeMillis();
//			jws = JWT.create()
//				  .withIssuer(issuer)			
//				  .withSubject(subject)			
//				  .withExpiresAt(new Date(timestamp + 5*60*1000))		// expired in 5 minutes
//				  .sign(Algorithm.HMAC256(secret.getBytes(OneFinConstants.UTF8)));
//		} catch (Exception e) {
//			LOGGER.error(cName + "Cannot encode Authorization header!", e);
//		}
//		
//		return jws;
//	}
//	
//	/**
//	 * Validate Token
//	 * 
//	 * @param token
//	 * @param apiGatewayIssuer
//	 * @param acceptMap
//	 * @param secret
//	 * @return
//	 */
//	public static boolean validateToken(String token, String apiGatewayIssuer, String acceptComponent, String secret) {
//		try {
//			
//			Claims body = Jwts.parser()
//                    .setSigningKey(secret.getBytes(OneFinConstants.UTF8))
//                    .parseClaimsJws(token)
//                    .getBody();
//			
//			String issuer = body.getIssuer();
//			
//			if (issuer != null && apiGatewayIssuer.equals(issuer)) {
//				String sender = body.getSubject();
//				if (sender != null && sender.equals(acceptComponent)) {
//					Date expired = body.getExpiration();
//					if (expired != null) {
//						long curDateTime = System.currentTimeMillis();
//						if (curDateTime <= expired.getTime()) {
//							// Valid request
//							LOGGER.debug(cName + "Request valid!");
//							return true;
//						} else {
//							LOGGER.error(cName + "Request expired!");
//						}
//					}
//				} else {
//					LOGGER.debug(cName + "Invalid sender!");
//				}
//			} else {
//				LOGGER.debug(cName + "Invalid issuer!");
//			}
//		} catch (Exception e) {
//			LOGGER.error(cName + "Cannot decode token!", e);
//		}
//		
//		LOGGER.error(cName + "Unauthorize!, You are not allow to access OneFin!");
//		return false;
//	}
//
//	
//	/**
//	 * Test generate, validate token
//	 * 
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		String issuer = "SoftSpace";
//		String sender = "eKYC";
//		String secret = UUID.randomUUID().toString();
//		
//		String token = HTTPRequestUtil.generateToken(issuer, sender, secret);
//		LOGGER.debug("Token=" + token);
//		
//		
//		boolean isValid1 = HTTPRequestUtil.validateToken(token, issuer, sender, secret);
//		LOGGER.debug(" Is Token Valid1:" + isValid1);
//		
//		boolean isValid2 = HTTPRequestUtil.validateToken(token, "xxxx", sender, secret);
//		LOGGER.debug(" Is Token Valid2:" + isValid2);
//
//		boolean isValid3 = HTTPRequestUtil.validateToken(token, issuer, "xxxx", secret);
//		LOGGER.debug(" Is Token Valid3:" + isValid3);
//
//		boolean isValid4 = HTTPRequestUtil.validateToken(token, issuer, sender, "xxxx");
//		LOGGER.debug(" Is Token Valid4:" + isValid4);
//	
//		/*
//		 * Test request expired after 5 minutes
//		 * 
//		try {
//			Thread.sleep(5*60*1000 + 1);
//			boolean isValid5 = HTTPRequestUtil.validateToken(token, issuer, sender, "xxxx");
//			LOGGER.debug(" Is Token Valid5:" + isValid5);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		*/
//	}
//}
