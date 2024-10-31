package io.wisoft.company_local;

import com.auth0.jwk.JwkException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.corbado.entities.SessionValidationResult;
import com.corbado.exceptions.CorbadoServerException;
import com.corbado.exceptions.StandardException;
import com.corbado.generated.model.Identifier;
import com.corbado.sdk.Config;
import com.corbado.sdk.CorbadoSdk;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Optional;

@Controller
public class AuthController {

  @Value("${projectID}")
  private String projectID;

  @Value("${apiSecret}")
  private String apiSecret;

  private final AuthService authService;
  private CorbadoSdk sdk;
  private Config config; // Config 객체 선언

  @Autowired
  public AuthController(AuthService authService) throws StandardException {
    this.authService = authService;
  }

  @PostConstruct
  public void init() throws StandardException {
    this.config = new Config(projectID, apiSecret);

    this.config.setBackendApi("https://backendapi.cloud.corbado.io");
    this.config.setShortSessionCookieName("cbo_short_session");
    this.config.setShortSessionLength(300);

    this.sdk = new CorbadoSdk(config);
  }

  @GetMapping("/company")
  public String profilePage(
      @RequestHeader("cookie") String cookies,
      Model model
  ) throws JwkException, CorbadoServerException, StandardException {
    this.sdk = new CorbadoSdk(config);
    String[] cookieArray = cookies.split("; ");
    String cboShortSession = null;

    for (String cookie : cookieArray) {
      if (cookie.startsWith("cbo_short_session=")) {
        cboShortSession = cookie.substring("cbo_short_session=".length());
        break;
      }
    }

    if (cboShortSession == null) {
      model.addAttribute("message", "Session is missing");
      System.out.println("Session is missing");
      return "error";  // 에러 페이지로 이동
    }

    try {
      final SessionValidationResult validationResp =
          sdk.getSessions().getAndValidateCurrentUser(cboShortSession);

      // JWT 검증
      List<Identifier> emails = sdk.getIdentifiers().listAllEmailsByUserId(validationResp.getUserID());
      String userEmail = emails.getFirst().getValue();
      System.out.println(cboShortSession);
      System.out.println("userEmail: " + userEmail);

      if (userEmail != null) {
        // 사용자 프로필 정보 반환
        Optional<User> user = authService.getUserByEmail(userEmail); // 이메일로 사용자 가져오기
        if (user.isPresent()) {
          model.addAttribute("USER_NAME", user.get().getName());
          model.addAttribute("USER_EMAIL", user.get().getEmail());
          return "company";  // 사용자 정보가 있는 경우 프로필 페이지 반환
        } else {
          model.addAttribute("message", "User not found");
          return "error";  // 사용자 정보가 없을 경우 에러 페이지로 이동
        }
      } else {
        model.addAttribute("message", "Unauthorized access");
        return "error";  // 사용자 이메일이 없는 경우 에러 페이지로 이동
      }

    } catch (JWTDecodeException e) {
      model.addAttribute("message", "잘못된 접근입니다.");
      System.out.println("Failed to decode JWT: " + e.getMessage());
      return "error";  // JWT 디코딩 실패 시 에러 페이지로 이동

    } catch (Exception e) {
      model.addAttribute("message", "An unexpected error occurred");
      System.out.println("An unexpected error occurred: " + e.getMessage());
      return "error";  // 다른 예외가 발생할 경우 에러 페이지로 이동
    }
  }

}
