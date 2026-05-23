import { useEffect, useState } from "react";
import { AppShell } from "./components/layout/AppShell";
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
  const [apiStatus, setApiStatus] = useState("Aguardando consulta");
  const canAccessCashRegister = canCloseCashRegister(role);

  useEffect(() => {
    if (activePage === "cash-register" && !canAccessCashRegister) {
      setActivePage("players");
    }
  }, [activePage, canAccessCashRegister]);

  function renderPage() {
    if (activePage === "materials") {
      return <MaterialsPage onApiStatusChange={setApiStatus} />;
    }

    if (activePage === "agenda") {
      return <AgendaPage onApiStatusChange={setApiStatus} />;
    }

    if (activePage === "cash-register" && canAccessCashRegister) {
      return <CashRegisterPage onApiStatusChange={setApiStatus} />;
    }

    return <PlayersPage onApiStatusChange={setApiStatus} />;
  }

  return (
    <AuthGate>
      <AppShell
        activePage={activePage}
        apiStatus={apiStatus}
        canAccessCashRegister={canAccessCashRegister}
        onNavigate={setActivePage}
      >
        {renderPage()}
      </AppShell>
    </AuthGate>
  );
}

export default App;
