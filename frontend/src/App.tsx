import { useState } from "react";
import { MaterialsPage } from "./pages/MaterialsPage";
import { PlayersPage } from "./pages/PlayersPage";

export type AppPage = "players" | "materials";

function App() {
  const [activePage, setActivePage] = useState<AppPage>("players");

  if (activePage === "materials") {
    return <MaterialsPage onNavigate={setActivePage} />;
  }

  return <PlayersPage onNavigate={setActivePage} />;
}

export default App;
