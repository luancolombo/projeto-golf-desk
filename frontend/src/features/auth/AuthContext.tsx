import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import {
  clearApiAuthTokens,
  setApiAuthTokens,
  setApiRefreshFailureHandler,
  setApiTokenRefreshHandler
} from "../../api";
import type { AuthSession, AuthSessionStatus, LoginResponse, RefreshTokenResponse } from "../../types";

const AUTH_SESSION_STORAGE_KEY = "golfOfficeAuthSession";
export const AUTH_SESSION_MESSAGE_STORAGE_KEY = "golfOfficeAuthSessionMessage";

type AuthContextValue = {
  session: AuthSession | null;
  user: AuthSession["user"] | null;
  accessToken: string | null;
  refreshToken: string | null;
  role: AuthSession["user"]["role"] | null;
  status: AuthSessionStatus;
  isAuthenticated: boolean;
  setSessionFromLogin: (response: LoginResponse) => void;
  updateTokens: (response: RefreshTokenResponse) => void;
  clearSession: () => void;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

type AuthProviderProps = {
  children: ReactNode;
};

function loadStoredSession() {
  const storedSession = sessionStorage.getItem(AUTH_SESSION_STORAGE_KEY);

  if (!storedSession) {
    return null;
  }

  try {
    const session = JSON.parse(storedSession) as AuthSession;
    setApiAuthTokens(session.accessToken, session.refreshToken);
    return session;
  } catch {
    sessionStorage.removeItem(AUTH_SESSION_STORAGE_KEY);
    clearApiAuthTokens();
    return null;
  }
}

function saveSession(session: AuthSession | null) {
  if (!session) {
    sessionStorage.removeItem(AUTH_SESSION_STORAGE_KEY);
    clearApiAuthTokens();
    return;
  }

  sessionStorage.setItem(AUTH_SESSION_STORAGE_KEY, JSON.stringify(session));
  setApiAuthTokens(session.accessToken, session.refreshToken);
}

function createSessionFromLogin(response: LoginResponse): AuthSession {
  return {
    accessToken: response.accessToken,
    refreshToken: response.refreshToken,
    tokenType: response.tokenType,
    expiresIn: response.expiresIn,
    user: {
      id: response.userId,
      name: response.name,
      email: response.email,
      role: response.role
    }
  };
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [session, setSession] = useState<AuthSession | null>(() => loadStoredSession());

  const setAndStoreSession = useCallback((nextSession: AuthSession | null) => {
    setSession(nextSession);
    saveSession(nextSession);
  }, []);

  const setSessionFromLogin = useCallback((response: LoginResponse) => {
    setAndStoreSession(createSessionFromLogin(response));
  }, [setAndStoreSession]);

  const updateTokens = useCallback((response: RefreshTokenResponse) => {
    setSession((currentSession) => {
      if (!currentSession) {
        return null;
      }

      const nextSession: AuthSession = {
        ...currentSession,
        accessToken: response.accessToken,
        refreshToken: response.refreshToken,
        tokenType: response.tokenType,
        expiresIn: response.expiresIn
      };

      saveSession(nextSession);
      return nextSession;
    });
  }, []);

  const clearSession = useCallback(() => {
    setAndStoreSession(null);
  }, [setAndStoreSession]);

  useEffect(() => {
    setApiTokenRefreshHandler((response) => {
      setSession((currentSession) => {
        if (!currentSession) {
          return null;
        }

        const nextSession: AuthSession = {
          ...currentSession,
          accessToken: response.accessToken,
          refreshToken: response.refreshToken,
          tokenType: response.tokenType,
          expiresIn: response.expiresIn
        };

        saveSession(nextSession);
        return nextSession;
      });
    });

    setApiRefreshFailureHandler(() => {
      sessionStorage.setItem(
        AUTH_SESSION_MESSAGE_STORAGE_KEY,
        "Sua sessao expirou. Faca login novamente para continuar."
      );
      setAndStoreSession(null);
    });

    return () => {
      setApiTokenRefreshHandler(null);
      setApiRefreshFailureHandler(null);
    };
  }, [setAndStoreSession]);

  const value = useMemo<AuthContextValue>(() => {
    const user = session?.user ?? null;
    const status: AuthSessionStatus = session ? "authenticated" : "anonymous";

    return {
      session,
      user,
      accessToken: session?.accessToken ?? null,
      refreshToken: session?.refreshToken ?? null,
      role: user?.role ?? null,
      status,
      isAuthenticated: status === "authenticated",
      setSessionFromLogin,
      updateTokens,
      clearSession
    };
  }, [clearSession, session, setSessionFromLogin, updateTokens]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }

  return context;
}
