package com.github.ngodat0103.usersvc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Locale;
import lombok.*;

@Getter
@Builder
public class AccountDto {
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String accountId;

  private String nickName;

  @NotBlank
  @Email
  @Size(max = 255)
  private String email;

  @NotBlank
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Size(min = 8, max = 255)
  private String password;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String zoneInfo;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String pictureUrl;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Locale locale;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private boolean emailVerified;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private LocalDateTime lastUpdatedDate;
}
