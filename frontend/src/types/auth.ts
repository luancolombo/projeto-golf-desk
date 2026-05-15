export type UserRole = "MANAGER" | "RECEPTIONIST";

export type LoginRequest = {
  email: string;
  password: string;
};

export type LoginResponse = {
  accessToken: string;
  refreshToken: string;
  tokenType: "Bearer";
  expiresIn: number;
  userId: number;
  name: string;
  email: string;
  role: UserRole;
};

export type RefreshTokenRequest = {
  refreshToken: string;
};

export type RefreshTokenResponse = {
  accessToken: string;
  refreshToken: string;
  tokenType: "Bearer";
  expiresIn: number;
};

export type AuthenticatedUser = {
  id: number;
  name: string;
  email: string;
  role: UserRole;
};

export type AuthSessionStatus = "anonymous" | "authenticated";

export type AuthSession = {
  accessToken: string;
  refreshToken: string;
  tokenType: "Bearer";
  expiresIn: number;
  user: AuthenticatedUser;
};
