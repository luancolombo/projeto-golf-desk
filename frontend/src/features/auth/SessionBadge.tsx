import { useState } from "react";
import { authService } from "../../api";
import { useAuth } from "./AuthContext";

type SessionBadgeProps = {
  apiStatus: string;
};

export function SessionBadge({ apiStatus }: SessionBadgeProps) {
  const { clearSession, refreshToken, user } = useAuth();
  const [isLoggingOut, setIsLoggingOut] = useState(false);

  async function handleLogout() {
    setIsLoggingOut(true);

    try {
      if (refreshToken) {
        await authService.logout(refreshToken);
      }
    } finally {
      clearSession();
      setIsLoggingOut(false);
    }
  }

  return (
    <div className="header-status-stack">
      <div className="session-status">
        <span>Logado como</span>
        <strong>{user?.name || "Usuario"}</strong>
        <small>{user?.role}</small>
        <button className="session-logout-button" disabled={isLoggingOut} type="button" onClick={handleLogout}>
          {isLoggingOut ? "Saindo..." : "Sair"}
        </button>
      </div>
      <div className="api-status">
        <span>API</span>
        <strong>{apiStatus}</strong>
      </div>
    </div>
  );
}
