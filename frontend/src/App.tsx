import { useState } from "react";
import { AgendaPage } from "./pages/AgendaPage";
import { MaterialsPage } from "./pages/MaterialsPage";
import { PlayersPage } from "./pages/PlayersPage";

export type AppPage = "players" | "agenda" | "materials";

function App() {
  const [activePage, setActivePage] = useState<AppPage>("players");

  if (activePage === "materials") {
    return <MaterialsPage onNavigate={setActivePage} />;
  }

  if (activePage === "agenda") {
    return <AgendaPage onNavigate={setActivePage} />;
  }

  return <PlayersPage onNavigate={setActivePage} />;
}

export default App;
