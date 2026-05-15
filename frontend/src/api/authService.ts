import { apiClient } from "./apiClient";
import type { LoginRequest, LoginResponse, RefreshTokenRequest, RefreshTokenResponse } from "../types";

const BASE_PATH = "/auth";

export const authService = {
  login(credentials: LoginRequest) {
    return apiClient.post<LoginResponse, LoginRequest>(`${BASE_PATH}/login`, credentials);
  },
  refresh(refreshToken: string) {
    const payload: RefreshTokenRequest = { refreshToken };
    return apiClient.post<RefreshTokenResponse, RefreshTokenRequest>(`${BASE_PATH}/refresh`, payload);
  },
  logout(refreshToken: string) {
    const payload: RefreshTokenRequest = { refreshToken };
    return apiClient.post<void, RefreshTokenRequest>(`${BASE_PATH}/logout`, payload);
  }
};
