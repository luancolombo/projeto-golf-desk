import { useEffect, useState } from "react";
import { CashRegisterPage } from "./features/cash-register/components/CashRegisterPage";
import { AuthGate } from "./features/auth/AuthGate";
import { useAuth } from "./features/auth/AuthContext";
import { canCloseCashRegister } from "./features/auth/permissions";
import { AgendaPage } from "./pages/AgendaPage";
import { MaterialsPage } from "./pages/MaterialsPage";
import { PlayersPage } from "./pages/PlayersPage";

export type AppPage = "players" | "agenda" | "materials" | "cash-register";

function App() {
  const { role } = useAuth();
  const [activePage, setActivePage] = useState<AppPage>("players");
  const canAccessCashRegister = canCloseCashRegister(role);

  useEffect(() => {
    if (activePage === "cash-register" && !canAccessCashRegister) {
      setActivePage("players");
    }
  }, [activePage, canAccessCashRegister]);

  if (activePage === "materials") {
    return (
      <AuthGate>
        <MaterialsPage onNavigate={setActivePage} />
      </AuthGate>
    );
  }

  if (activePage === "agenda") {
    return (
      <AuthGate>
        <AgendaPage onNavigate={setActivePage} />
      </AuthGate>
    );
  }

  if (activePage === "cash-register" && canAccessCashRegister) {
    return (
      <AuthGate>
        <CashRegisterPage onNavigate={setActivePage} />
      </AuthGate>
    );
  }

  return (
    <AuthGate>
      <PlayersPage onNavigate={setActivePage} />
    </AuthGate>
  );
}

export default App;
