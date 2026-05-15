import type { ReactNode } from "react";
import { LoginPage } from "../../pages/LoginPage";
import { useAuth } from "./AuthContext";

type AuthGateProps = {
  children: ReactNode;
};

export function AuthGate({ children }: AuthGateProps) {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <LoginPage />;
  }

  return children;
}
